/**
 * A simple interpreter to demonstrate scanning, parsing, symbol tables,
 * parse trees, and interpreted program execution.
 * 
 * (c) 2026 by Ronald Mak
 * Department of Computer Science
 * San Jose State University
 */
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import frontend.Listing;
import frontend.SyntaxErrorHandler;
import intermediate.antlr4.*;
import intermediate.semantics.Semantics;
import intermediate.symtab.Symtab;
import intermediate.util.CrossReferencer;
import backend.interpreter.*;

public class SimpleA3
{
    public static void main(String args[])
    {
        if (args.length != 1)
        {
            System.out.println("Usage: SimpleA3 sourceFileName");
            System.exit(-1);
        }
        
        String sourceFileName = args[0];  // source file name
        FileInputStream source = null;    // input stream
        CharStream chars = null;          // character stream
        
        try
        {
            // Generate a source file listing.
            Listing listing = new Listing(sourceFileName);
            listing.print();
            
            // Create the input stream.
            source = new FileInputStream(sourceFileName);
            
            // Create the character stream from the input stream.
            chars = CharStreams.fromStream(source);
        }
        catch (IOException ex)
        {
            System.out.println("Source file error: " + sourceFileName);
            System.exit(-1);
        }
        
        SyntaxErrorHandler syntaxErrorHandler = new SyntaxErrorHandler();

        System.out.printf("\nPASS 1 Syntax:\n");
        
        // Create a lexer which scans the character stream
        // to create a token stream.
        SimpleA3Lexer lexer = new SimpleA3Lexer(chars);
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorHandler);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Create a parser which parses the token stream
        // to create a parse tree.
        SimpleA3Parser parser = new SimpleA3Parser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorHandler);

        // Build the parse tree.
        ParseTree tree = parser.program();
        int errorCount = syntaxErrorHandler.getCount();
        if (errorCount > 0) 
        {
            System.out.printf("\nThere were %d syntax errors.\n", 
                              errorCount);
            return;
        }
        else
        {
            System.out.println("There were no syntax errors.");
        }
        
        System.out.printf("\nPASS 2 Semantics:\n");
        Semantics pass2 = new Semantics();
        pass2.visit(tree);
        
        errorCount = pass2.getErrorCount();
        if (errorCount > 0)
        {
            System.out.printf("\nThere were %d semantic errors.\n", 
                              errorCount);
        }
        else
        {
            System.out.println("There were no semantic errors.");
        }
        
        Symtab symtab = pass2.getSymtab();
        CrossReferencer xref = new CrossReferencer();
        xref.print(symtab);
        
        if (errorCount == 0)
        {
            System.out.printf("\nPass 3 Execution output:\n\n");
            
            Executor pass3 = new Executor();
            pass3.visit(tree);
        }
    }
}

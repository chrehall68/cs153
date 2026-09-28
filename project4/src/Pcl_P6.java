import java.io.InputStream;

import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import frontend.Listing;
import frontend.SyntaxErrorHandler;

import intermediate.antlr4.Pcl_P6Parser;
import intermediate.semantics.Semantics_P6;
import intermediate.antlr4.Pcl_P6Lexer;
import backend.converter.Converter_P6;

public class Pcl_P6 
{
    public static void main(String[] args) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("USAGE: Pc1_P4 sourceFileName");
            return;
        }

        String sourceFileName = args[0];
        
        try
        {
            Listing lstng = new Listing(sourceFileName);
            lstng.print();
        }
        catch(IOException ex)
        {
            System.out.printf("ERROR: %s\n", ex.getMessage());
            System.exit(-1);
        }        
        
        InputStream source = 
                new FileInputStream(sourceFileName);
        CharStream cs = CharStreams.fromStream(source);
        
        SyntaxErrorHandler syntaxErrorHandler = new SyntaxErrorHandler();

        System.out.printf("\nPASS 1 Syntax:\n");
        
        Pcl_P6Lexer lexer = new Pcl_P6Lexer(cs);
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorHandler);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Pcl_P6Parser parser = new Pcl_P6Parser(tokens);

        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorHandler);

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
        
        Semantics_P6 pass2 = new Semantics_P6();
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
        
        if (errorCount == 0)
        {
            System.out.printf("\nPASS 3 Conversion:\n\n");
            
            Converter_P6 pass3 = new Converter_P6();
            pass3.visit(tree);
            
            System.out.printf("Object file \"%s\" created.\n",
                          pass3.getObjectFileName());
        }
    }
}

import backend.converter.Converter_P2;

import frontend.Listing;
import frontend.SyntaxErrorHandler;

import intermediate.antlr4.Pcl_P2Lexer;
import intermediate.antlr4.Pcl_P2Parser;
import intermediate.semantics.Semantics_P2;
import intermediate.symtab.Symtab;
import intermediate.util.CrossReferencer;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Pcl_P2 {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("USAGE: Pc1_P2 sourceFileName");
            return;
        }

        String sourceFileName = args[0];

        try {
            Listing lstng = new Listing(sourceFileName);
            lstng.print();
        } catch (IOException ex) {
            System.out.printf("ERROR: %s\n", ex.getMessage());
            System.exit(-1);
        }

        InputStream source = new FileInputStream(sourceFileName);
        CharStream cs = CharStreams.fromStream(source);

        SyntaxErrorHandler syntaxErrorHandler = new SyntaxErrorHandler();

        System.out.printf("\nPASS 1 Syntax:\n");

        Pcl_P2Lexer lexer = new Pcl_P2Lexer(cs);
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorHandler);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Pcl_P2Parser parser = new Pcl_P2Parser(tokens);

        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorHandler);

        ParseTree tree = parser.program();

        int errorCount = syntaxErrorHandler.getCount();
        if (errorCount > 0) {
            System.out.printf("\nThere were %d syntax errors.\n", errorCount);
            return;
        } else {
            System.out.println("There were no syntax errors.");
        }

        System.out.printf("\nPASS 2 Semantics:\n");

        Semantics_P2 pass2 = new Semantics_P2();
        pass2.visit(tree);

        errorCount = pass2.getErrorCount();
        if (errorCount > 0) {
            System.out.printf("\nThere were %d semantic errors.\n", errorCount);
        } else {
            System.out.println("There were no semantic errors.");
        }

        Symtab symtab = pass2.getSymtab();
        CrossReferencer xref = new CrossReferencer();
        xref.print(symtab);

        if (errorCount == 0) {
            System.out.printf("\nPASS 3 Conversion:\n\n");

            Converter_P2 pass3 = new Converter_P2();
            pass3.visit(tree);

            System.out.printf("Object file \"%s\" created.\n", pass3.getObjectFileName());
        }
    }
}

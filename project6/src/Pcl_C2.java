import static backend.BackendMode.Mode.*;

import backend.BackendMode;
import backend.compiler.Compiler;
import backend.converter.Converter;
import backend.interpreter.Executor;

import frontend.Listing;
import frontend.SyntaxErrorHandler;

import intermediate.antlr4.Pcl_C2Lexer;
import intermediate.antlr4.Pcl_C2Parser;
import intermediate.semantics.Semantics;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Pcl_C2 {
    public static void main(String[] args) throws Exception {
        BackendMode.mode = EXECUTOR;
        String option = args[0];

        if (option.equals("-convert")) BackendMode.mode = CONVERTER;
        else if (option.equals("-execute")) BackendMode.mode = EXECUTOR;
        else if (option.equals("-debug")) BackendMode.mode = DEBUGGER;
        else if (option.equals("-compile")) BackendMode.mode = COMPILER;
        else {
            System.out.println("ERROR: Invalid option.");
            System.out.println("   option: -convert, -execute, -debug, or -compile");
        }

        String sourceFileName = args[1];

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

        Pcl_C2Lexer lexer = new Pcl_C2Lexer(cs);
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorHandler);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Pcl_C2Parser parser = new Pcl_C2Parser(tokens);

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

        Semantics pass2 = new Semantics();
        pass2.visit(tree);

        errorCount = pass2.getErrorCount();
        if (errorCount > 0) {
            System.out.printf("\nThere were %d semantic errors.\n", errorCount);
            System.out.println("Converted file not created or modified.");
            return;
        } else {
            System.out.println("There were no semantic errors.");
        }

        switch (BackendMode.mode) {
            case CONVERTER:
                {
                    // Convert from Pascal to Java.
                    System.out.printf("\nPASS 3 Convert to Java: ");
                    Converter pass3 = new Converter();
                    pass3.visit(tree);

                    System.out.printf(
                            "Converted file \"%s\" created.\n", pass3.getObjectFileName());
                    break;
                }

            case EXECUTOR:
            case DEBUGGER:
                {
                    // Execute the Pascal program.
                    System.out.printf("\nPASS 3 Execution:\n\n");
                    Executor pass3 = new Executor();
                    pass3.visit(tree);
                    break;
                }

            case COMPILER:
                {
                    // Compile the Pascal program.
                    System.out.printf("\nPASS 3 Compilation: ");
                    Compiler pass3 = new Compiler();
                    pass3.visit(tree);

                    System.out.printf("Object file \"%s\" created.\n", pass3.getObjectFileName());
                    break;
                }
        }
    }
}

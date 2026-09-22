package intermediate.semantics;

import org.antlr.v4.runtime.ParserRuleContext;

public class SemanticErrorHandler {
    public enum Code {
        UNDECLARED_IDENTIFIER("Undeclared identifier"),
        REDECLARED_IDENTIFIER("Redeclared identifier"),
        INVALID_CONSTANT("Invalid constant"),
        INVALID_TYPE("Invalid type"),
        INVALID_SIGN("Invalid sign"),
        INVALID_SUBRANGE("Invalid subrange"),
        INVALID_SUBRANGE_CONSTANT("Invalid subrange constant"),
        INVALID_INDEX_TYPE("Index type must be ordinal"),
        INVALID_PACKED_ARRAY("Can only pack arrays of char"),
        INVALID_HASHTABLE_KEY_TYPE("The hashtable key type must be ordinal"),
        ;

        private String message;

        Code(String message) {
            this.message = message;
        }
    }

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void flag(Code code, int lineNumber, String text) {
        if (count == 0) {
            System.out.println("\n===== SEMANTIC ERRORS =====\n");
            System.out.printf("%-4s %-40s %s\n", "Line", "Message", "Found near");
            System.out.printf("%-4s %-40s %s\n", "----", "-------", "----------");
        }

        count++;
        System.out.printf("%03d  %-40s \"%s\"\n", lineNumber, code.message, text);
    }

    public void flag(Code code, ParserRuleContext ctx) {
        flag(code, ctx.getStart().getLine(), ctx.getText());
    }
}

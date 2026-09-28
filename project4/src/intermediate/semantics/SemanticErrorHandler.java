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
        INVALID_VARIABLE("Invalid variable"),
        INVALID_CONTROL_VARIABLE("Invalid control variable datatype"),
        DUPLICATE_CASE_CONSTANT("Duplicate CASE constant"),
        TYPE_MISMATCH("Mismatched datatype"),
        TOO_MANY_SUBSCRIPTS("Too many subscripts"),
        TYPE_MUST_BE_INTEGER("Datatype must be integer"),
        TYPE_MUST_BE_NUMERIC("Datatype must be integer or real"),
        TYPE_MUST_BE_BOOLEAN("Datatype must be boolean"),
        INCOMPATIBLE_ASSIGNMENT("Incompatible assignment"),
        INCOMPATIBLE_COMPARISON("Incompatible comparison"),
        NAME_MUST_BE_PROCEDURE("Must be a procedure name"),
        NAME_MUST_BE_FUNCTION("Must be a function name"),
        ARGUMENT_COUNT_MISMATCH("Invalid number of arguments"),
        ARGUMENT_MUST_BE_VARIABLE("Argument must be a variable"),
        INVALID_REFERENCE_PARAMETER("Reference parameter cannot be scalar"),
        INVALID_RETURN_TYPE("Invalid function return type"),
        INVALID_FIELD("Invalid field"),
        INVALID_MODIFIER("Invalid field or subscript"),
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

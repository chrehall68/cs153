/**
 * <h1>RuntimeErrorHandler</h1>
 *
 * <p>Runtime error handler for the backend interpreter.</p>
 *
 * <p>Copyright (c) 2024 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
package backend.interpreter;

import org.antlr.v4.runtime.ParserRuleContext;

public class RuntimeErrorHandler
{
    public enum Code
    {
        UNINITIALIZED_VALUE("Uninitialized value"),
        VALUE_RANGE("Value out of range"),
        INVALID_CASE_EXPRESSION_VALUE("Invalid CASE expression value"),
        DIVISION_BY_ZERO("Division by zero"),
        INVALID_STANDARD_FUNCTION_ARGUMENT("Invalid standard function argument"),
        INVALID_INPUT("Invalid input"),
        STACK_OVERFLOW("Runtime stack overflow"),
        UNIMPLEMENTED_FEATURE("Unimplemented runtime feature");

        private String message;  // error message

        Code(String message) { this.message = message; }
    }
    
    private static final int MAX_ERRORS = 5;

    private int count = 0;    // count of runtime errors

    /**
     * Getter
     * @return the count of runtime errors.
     */
    public int getCount() { return count; }

    /**
     * Flag a runtime error.
     * @param node the root node of the offending statement or expression.
     * @param errorCode the runtime error code.
     * @param backend the backend processor.
     */
    public void flag(Code code, ParserRuleContext ctx)
    {
        System.out.printf("\n*** RUNTIME ERROR at line %03d: %s\n",
                          ctx.getStart().getLine(), code.message);

        if (++count > MAX_ERRORS) 
        {
            System.out.println("*** ABORTED AFTER TOO MANY RUNTIME ERRORS.");
            System.exit(-1);
        }
    }
}

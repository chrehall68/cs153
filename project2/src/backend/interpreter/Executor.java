package backend.interpreter;

import intermediate.antlr4.SimpleA3BaseVisitor;
import intermediate.antlr4.SimpleA3Parser.*;
import intermediate.symtab.*;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashSet;

public class Executor extends SimpleA3BaseVisitor<Object> {
    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) {
        VariableContext variableCtx = ctx.variable();
        ExpressionContext expressionCtx = ctx.expression();

        // Evaluate the right-hand-side expression;
        Double value = (Double) visit(expressionCtx);

        // Store the value into the variable's symbol table entry.
        SymtabEntry variableEntry = variableCtx.entry;
        variableEntry.setValue(value);

        return null;
    }

    @Override
    public Object visitRepeatStatement(RepeatStatementContext ctx) {
        StatementListContext listCtx = ctx.statementList();
        boolean value;

        do {
            visit(listCtx);
            value = (Boolean) visit(ctx.expression());
        } while (!value);

        return null;
    }

    @Override
    public Object visitWhileStatement(WhileStatementContext ctx) {
        while ((Boolean) visit(ctx.expression())) {
            visit(ctx.statement());
        }

        return null;
    }

    @Override
    public Object visitIfStatement(IfStatementContext ctx) {
        if ((Boolean) visit(ctx.expression())) {
            visit(ctx.statement(0));
        } else if (ctx.ELSE() != null) {
            visit(ctx.statement(1));
        }
        return null;
    }

    @Override
    public Object visitWritelnStatement(WritelnStatementContext ctx) {
        visitChildren(ctx);
        System.out.println();

        return null;
    }

    @Override
    public Object visitWriteArguments(WriteArgumentsContext ctx) {
        StringBuffer format = new StringBuffer();

        // Loop over the write arguments.
        for (WriteArgumentContext writeArgCtx : ctx.writeArgument()) {
            Object value = visit(writeArgCtx.expression());
            FormatContext formatCtx = writeArgCtx.format();
            boolean isStringLiteral = value instanceof String;
            boolean isCharacterLiteral = value instanceof Character;

            // Print a literal string or character without formatting.
            if ((isStringLiteral || isCharacterLiteral) && (formatCtx == null)) {
                System.out.print(value);
            }

            // For any other argument, append a field specifier.
            else {
                format.append("%");

                if (formatCtx != null) {
                    WidthContext widthCtx = formatCtx.width();
                    SignContext signCtx = widthCtx.sign();
                    String sign = ((signCtx != null) && (signCtx.getText().equals("-"))) ? "-" : "";
                    format.append(sign).append(widthCtx.integerConstant().getText());
                }

                if (isStringLiteral) {
                    format.append("s");
                    String str = (String) value;
                    System.out.printf(format.toString(), str);
                } else if (isCharacterLiteral) {
                    format.append("c");
                    char ch = (Character) value;
                    System.out.printf(format.toString(), ch);
                } else {
                    format.append(".");
                    PrecisionContext precisionCtx =
                            formatCtx == null ? null : formatCtx.precision();
                    String precisionText =
                            precisionCtx != null ? precisionCtx.integerConstant().getText() : "0";
                    format.append(precisionText);

                    format.append("f");
                    System.out.printf(format.toString(), (double) value);
                }
            }
        }

        return null;
    }

    @Override
    public Object visitExpression(ExpressionContext ctx) {
        SimpleExpressionContext simpleCtx1 = ctx.simpleExpression(0);
        RelOpContext relOpCtx = ctx.relOp();
        Object operand1 = visit(simpleCtx1);

        // Only one simple expression.
        if (relOpCtx == null) return operand1;

        // Two simple expressions.
        String op = relOpCtx.getText();
        SimpleExpressionContext simpleCtx2 = ctx.simpleExpression(1);

        double value1 = (Double) operand1;
        double value2 = (Double) visit(simpleCtx2);

        if (op.equals("=")) return value1 == value2;
        else if (op.equals("<>")) return value1 != value2;
        else if (op.equals("<")) return value1 < value2;
        else if (op.equals("<=")) return value1 <= value2;
        else if (op.equals(">")) return value1 > value2;
        else if (op.equals(">=")) return value1 >= value2;

        return null;
    }

    @Override
    public Object visitSimpleExpression(SimpleExpressionContext ctx) {
        int count = ctx.term().size();
        Boolean negate = (ctx.sign() != null) && ctx.sign().getText().equals("-");

        // First term.
        TermContext termCtx1 = ctx.term(0);
        Object operand1 = visit(termCtx1);
        if (negate) operand1 = -((Double) operand1);

        // Loop over the subsequent terms.
        for (int i = 1; i < count; i++) {
            String op = ctx.addOp(i - 1).getText().toLowerCase();
            TermContext termCtx2 = ctx.term(i);
            Object operand2 = visit(termCtx2);

            if (operand2 instanceof Double) {
                double value1 = (Double) operand1;
                double value2 = (Double) operand2;
                operand1 = (op.equals("+")) ? value1 + value2 : value1 - value2;
            } else if (operand2 instanceof Boolean) {
                operand1 = ((Boolean) operand1) || ((Boolean) operand2);
            } else // String
            {
                operand1 = ((String) operand1) + ((String) operand2);
            }
        }

        return operand1;
    }

    @Override
    public Object visitTerm(TermContext ctx) {
        int count = ctx.factor().size();

        // First factor.
        FactorContext factorCtx1 = ctx.factor(0);
        Object operand1 = visit(factorCtx1);

        // Loop over the subsequent factors.
        for (int i = 1; i < count; i++) {
            String op = ctx.mulOp(i - 1).getText().toLowerCase();
            FactorContext factorCtx2 = ctx.factor(i);
            Object operand2 = visit(factorCtx2);

            if (operand2 instanceof Double) {
                double value1 = (Double) operand1;
                double value2 = (Double) operand2;

                if (op.equals("*")) operand1 = value1 * value2;
                else if ((op.equals("/")) || (op.equals("div")) || (op.equals("mod"))) {
                    // Check for division by zero.
                    if (value2 == 0) {
                        runtimeError("Division by zero", factorCtx2);
                        operand1 = 0;
                    } else if (op.equals("/")) operand1 = value1 / value2;
                    else {
                        long long1 = (long) value1;
                        long long2 = (long) value2;
                        long result = (op.equals("div")) ? long1 / long2 : long1 % long2;
                        operand1 = (double) result;
                    }
                }
            } else // Boolean
            {
                operand1 = ((Boolean) operand1) && ((Boolean) operand2);
            }
        }

        return operand1;
    }

    @Override
    public Object visitFactorVariable(FactorVariableContext ctx) {
        return ctx.variable().entry.getValue();
    }

    @Override
    public Object visitUnsignedIntegerConstant(UnsignedIntegerConstantContext ctx) {
        // For now, force integer value to double.
        int intValue = (Integer) ctx.integerConstant().value;
        return (double) intValue;
    }

    @Override
    public Object visitUnsignedRealConstant(UnsignedRealConstantContext ctx) {
        return (Double) ctx.realConstant().value;
    }

    @Override
    public Object visitFactorCharacterConstant(FactorCharacterConstantContext ctx) {
        return (Character) ctx.characterConstant().value;
    }

    @Override
    public Object visitStringConstant(StringConstantContext ctx) {
        return (String) ctx.value;
    }

    @Override
    public Object visitFactorParenthesized(FactorParenthesizedContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitFactorNot(FactorNotContext ctx) {
        return !((Boolean) visit(ctx.factor()));
    }

    /**
     * Flag a runtime error.
     * @param node the root node of the offending statement or expression.
     * @param message the runtime error message.
     * @param ctx the context.
     */
    public void runtimeError(String message, ParserRuleContext ctx) {
        System.out.printf(
                "\n*** RUNTIME ERROR at line %03d: %s\n", ctx.getStart().getLine(), message);
    }

    @Override
    public Object visitForStatement(ForStatementContext ctx) {
        // end expression evaluation must come before the assignment visit to avoid
        // evaluating this expression with side effects of visiting the first assignment
        Double last = (Double) visit(ctx.expression());

        visit(ctx.assignmentStatement());

        AssignmentStatementContext assignStat = ctx.assignmentStatement();
        VariableContext iterator = assignStat.variable();
        SymtabEntry counter = iterator.entry;

        StatementContext body = ctx.statement();

        if (ctx.TO() != null) {
            while (counter.getValue() <= last) {
                visit(body);
                counter.setValue(counter.getValue() + 1);
            }
        }

        if (ctx.DOWNTO() != null) {
            while (counter.getValue() >= last) {
                visit(body);
                counter.setValue(counter.getValue() - 1);
            }
        }

        return null;
    }

    @Override
    public Object visitCaseStatement(CaseStatementContext ctx) {
        Object switchedValue = visitExpression(ctx.expression());
        for (var branch : ctx.selectBranch()) {
            var constants = branch.selectConstants();
            HashSet<Object> values = new HashSet<>();
            for (var selectConstant : constants.selectConstant()) {
                if (selectConstant.stringConstant() != null) {
                    values.add(visit(selectConstant.stringConstant()));
                } else {
                    // must be a numeric value
                    SignContext signContext = selectConstant.sign();
                    boolean positive = signContext == null || signContext.getText().equals("+");
                    Object value;
                    if (selectConstant.identifier() != null) {
                        value = selectConstant.identifier().entry.getValue();
                    } else {
                        value = visit(selectConstant.unsignedConstant());
                    }
                    if (!positive) {
                        value = -(double) value;
                    }
                    values.add(value);
                }
            }
            // now that we have the values, check membership
            if (values.contains(switchedValue)) {
                return visit(branch.statement());
            }
        }

        return null;
    }
}

package backend.interpreter;

import static backend.interpreter.RuntimeErrorHandler.Code.DIVISION_BY_ZERO;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;

import java.util.ArrayList;
import java.util.HashMap;

public class Expressions extends Executor {
    Object expression(ExpressionContext ctx) {
        SimpleExpressionContext simpleCtx1 = ctx.simpleExpression().get(0);
        RelOpContext relOpCtx = ctx.relOp();
        Object operand1 = visit(simpleCtx1);
        Typespec typespec1 = simpleCtx1.typespec;

        // More than one simple expression?
        if (relOpCtx != null) {
            String op = relOpCtx.getText();
            SimpleExpressionContext simpleCtx2 = ctx.simpleExpression().get(1);
            Object operand2 = visit(simpleCtx2);
            Typespec typespec2 = simpleCtx2.typespec;

            boolean integerMode = false;
            boolean realMode = false;
            boolean characterMode = false;

            if ((typespec1 == Predefined.integerType) && (typespec2 == Predefined.integerType)) {
                integerMode = true;
            } else if ((typespec1 == Predefined.realType) || (typespec2 == Predefined.realType)) {
                realMode = true;
            } else if ((typespec1 == Predefined.charType) && (typespec2 == Predefined.charType)) {
                characterMode = true;
            }

            if (integerMode || characterMode) {
                int value1 =
                        typespec1 == Predefined.integerType
                                ? (Integer) operand1
                                : (Character) operand1;
                int value2 =
                        typespec2 == Predefined.integerType
                                ? (Integer) operand2
                                : (Character) operand2;
                Boolean result = false;

                if (op.equals("=")) result = value1 == value2;
                else if (op.equals("<>")) result = value1 != value2;
                else if (op.equals("<")) result = value1 < value2;
                else if (op.equals("<=")) result = value1 <= value2;
                else if (op.equals(">")) result = value1 > value2;
                else if (op.equals(">=")) result = value1 >= value2;

                return result;
            } else if (realMode) {
                double value1 =
                        typespec1 == Predefined.integerType
                                ? (Integer) operand1
                                : (Double) operand1;
                double value2 =
                        typespec2 == Predefined.integerType
                                ? (Integer) operand2
                                : (Double) operand2;
                Boolean result = false;

                if (op.equals("=")) result = value1 == value2;
                else if (op.equals("<>")) result = value1 != value2;
                else if (op.equals("<")) result = value1 < value2;
                else if (op.equals("<=")) result = value1 <= value2;
                else if (op.equals(">")) result = value1 > value2;
                else if (op.equals(">=")) result = value1 >= value2;

                return result;
            } else // stringMode)
            {
                String value1 = (String) operand1;
                String value2 = (String) operand2;
                Boolean result = false;
                int comp = value1.compareTo(value2);

                if (op.equals("=")) result = comp == 0;
                else if (op.equals("<>")) result = comp != 0;
                else if (op.equals("<")) result = comp < 0;
                else if (op.equals("<=")) result = comp <= 0;
                else if (op.equals(">")) result = comp > 0;
                else if (op.equals(">=")) result = comp >= 0;

                return result;
            }
        }

        return operand1;
    }

    Object simpleExpression(SimpleExpressionContext ctx) {
        int count = ctx.term().size();
        Boolean negate = (ctx.sign() != null) && ctx.sign().getText().equals("-");

        // First term.
        TermContext termCtx1 = ctx.term().get(0);
        Object operand1 = visit(termCtx1);
        Typespec typespec1 = termCtx1.typespec;

        if (negate) {
            if (typespec1 == Predefined.integerType) {
                operand1 = -((Integer) operand1);
            } else if (typespec1 == Predefined.realType) {
                operand1 = -((Double) operand1);
            }
        }

        // Loop over the subsequent terms.
        for (int i = 1; i < count; i++) {
            String op = ctx.addOp().get(i - 1).getText().toLowerCase();
            TermContext termCtx2 = ctx.term().get(i);
            Object operand2 = visit(termCtx2);
            Typespec typespec2 = termCtx2.typespec;

            boolean integerMode = false;
            boolean realMode = false;
            boolean booleanMode = false;

            if ((typespec1 == Predefined.integerType) && (typespec2 == Predefined.integerType)) {
                integerMode = true;
            } else if ((typespec1 == Predefined.realType) || (typespec2 == Predefined.realType)) {
                realMode = true;
            } else if ((typespec1 == Predefined.booleanType)
                    && (typespec2 == Predefined.booleanType)) {
                booleanMode = true;
            }

            if (integerMode) {
                int value1 = (Integer) operand1;
                int value2 = (Integer) operand2;
                operand1 = (op.equals("+")) ? value1 + value2 : value1 - value2;
            } else if (realMode) {
                double value1 =
                        typespec1 == Predefined.integerType
                                ? (Integer) operand1
                                : (Double) operand1;
                double value2 =
                        typespec2 == Predefined.integerType
                                ? (Integer) operand2
                                : (Double) operand2;
                operand1 = (op.equals("+")) ? value1 + value2 : value1 - value2;
            } else if (booleanMode) {
                operand1 = ((Boolean) operand1) || ((Boolean) operand2);
            } else // stringMode
            {
                operand1 = ((String) operand1) + ((String) operand2);
            }
        }

        return operand1;
    }

    Object term(TermContext ctx) {
        int count = ctx.factor().size();

        // First factor.
        FactorContext factorCtx1 = ctx.factor().get(0);
        Object operand1 = visit(factorCtx1);
        Typespec typespec1 = factorCtx1.typespec.baseType();

        // Loop over the subsequent factors.
        for (int i = 1; i < count; i++) {
            String op = ctx.mulOp().get(i - 1).getText().toLowerCase();
            FactorContext factorCtx2 = ctx.factor().get(i);
            Object operand2 = visit(factorCtx2);
            Typespec typespec2 = factorCtx2.typespec.baseType();

            boolean integerMode = false;
            boolean realMode = false;

            if ((typespec1 == Predefined.integerType) && (typespec2 == Predefined.integerType)) {
                integerMode = true;
            } else if ((typespec1 == Predefined.realType) || (typespec2 == Predefined.realType)) {
                realMode = true;
            }

            if (integerMode) {
                int value1 = (Integer) operand1;
                int value2 = (Integer) operand2;

                if (op.equals("*")) operand1 = value1 * value2;
                else if (op.equals("div") || op.equals("/") || op.equals("mod")) {
                    // Check for division by zero.
                    if (value2 == 0) {
                        error.flag(DIVISION_BY_ZERO, factorCtx2);
                        operand1 = 0;
                    }

                    if (op.equals("div")) {
                        operand1 = value1 / value2;
                    } else if (op.equals("/")) {
                        double doubleValue = value1;
                        operand1 = doubleValue / value2;
                    } else // mod
                    {
                        operand1 = value1 % value2;
                    }
                }
            } else if (realMode) {
                double value1 =
                        typespec1 == Predefined.integerType
                                ? ((Integer) operand1).doubleValue()
                                : ((Double) operand1).doubleValue();
                double value2 =
                        typespec2 == Predefined.integerType
                                ? ((Integer) operand2).doubleValue()
                                : ((Double) operand2).doubleValue();

                if (op.equals("*")) operand1 = value1 * value2;
                else if (op.equals("/")) {
                    // Check for division by zero.
                    if (value2 == 0) {
                        error.flag(DIVISION_BY_ZERO, factorCtx2);
                        operand1 = 0;
                    } else operand1 = value1 / value2;
                }
            } else // booleanMode
            {
                operand1 = ((Boolean) operand1) && ((Boolean) operand2);
            }
        }

        return operand1;
    }

    Object factorVariable(FactorVariableContext ctx) {
        VariableContext variableCtx = ctx.variable();
        Kind kind = variableCtx.entry.getKind();

        // Obtain a constant's value from its symbol table entry.
        if ((kind == CONSTANT) || (kind == ENUMERATED_CONSTANT)) {
            Object value = variableCtx.entry.getValue();

            if (variableCtx.typespec == Predefined.booleanType) {
                value = (Integer) value != 0;
            }

            return value;
        } else if (kind == FUNCTION) {
            return visit(variableCtx);
        }

        // Obtain a variable's value from its memory cell.
        else {
            Cell variableCell = (Cell) visit(variableCtx);
            Object value = variableCell.getValue();

            if (debugging) {
                debugger.factorVariable(variableCtx, value, variableCtx.typespec);
            }

            return value;
        }
    }

    @SuppressWarnings("unchecked")
    Object variable(VariableContext ctx) {
        SymtabEntry variableEntry = ctx.entry;
        String variableName = variableEntry.getName();
        Typespec variableTypespec = variableEntry.getTypespec();
        int nestingLevel = variableEntry.getSymtab().getNestingLevel();

        // Is it a parameterless function call?
        if (variableEntry.getKind() == FUNCTION) {
            return executeFunctionCall(variableEntry, null);
        }

        // Get the variable reference from the appropriate activation record.
        StackFrame frame = runtimeStack.getTopmost(nestingLevel);
        Cell variableCell = frame.getCell(variableName);

        // Execute any array subscripts or record fields.
        for (ModifierContext modCtx : ctx.modifier()) {
            // Subscripts.
            if (modCtx.indexList() != null) {
                // Compute a new reference for each subscript.
                for (IndexContext indexCtx : modCtx.indexList().index()) {
                    Typespec indexTypespec = variableTypespec.getArrayIndexType();
                    int minIndex = 0;

                    if (indexTypespec.getForm() == SUBRANGE) {
                        minIndex = (Integer) indexTypespec.getSubrangeMinValue();
                    }

                    int value = (Integer) visit(indexCtx.expression());
                    int index = value - minIndex;

                    variableCell = ((Cell[]) variableCell.getValue())[index];
                    variableTypespec = variableTypespec.getArrayElementType();
                }
            }

            // Record field.
            else {
                SymtabEntry fieldEntry = modCtx.field().entry;
                String fieldName = fieldEntry.getName();

                // Compute a new reference for the field.
                HashMap<String, Cell> map = (HashMap<String, Cell>) variableCell.getValue();
                variableCell = map.get(fieldName);
                variableTypespec = fieldEntry.getTypespec();
            }
        }

        return variableCell;
    }

    Object integerConstant(IntegerConstantContext ctx) {
        return Integer.parseInt(ctx.getText());
    }

    Object realConstant(RealConstantContext ctx) {
        return Double.parseDouble(ctx.getText());
    }

    Object characterConstant(CharacterConstantContext ctx) {
        return ctx.getText().charAt(1);
    }

    Object stringConstant(StringConstantContext ctx) {
        String pascalString = ctx.getText();
        return convertString(pascalString);
    }

    Object booleanConstant(BooleanConstantContext ctx) {
        return ctx.getText().equalsIgnoreCase("true");
    }

    Object factorFunctionCall(FactorFunctionCallContext ctx) {
        FunctionCallContext callCtx = ctx.functionCall();
        SymtabEntry routineEntry = callCtx.functionName().entry;
        ArgumentListContext argListCtx = callCtx.argumentList();

        return executeFunctionCall(routineEntry, argListCtx);
    }

    private Object executeFunctionCall(SymtabEntry routineEntry, ArgumentListContext argListCtx) {
        StackFrame newFrame = new StackFrame(routineEntry);

        // Execute any call arguments and initialize
        // the parameters in the routine's new stack frame.
        if (argListCtx != null) {
            ArrayList<SymtabEntry> parms = routineEntry.getRoutineParameters();
            executeCallArguments(argListCtx, parms, newFrame);
        }

        // Push the routine's stack frame onto the runtime stack
        // and execute the procedure.
        runtimeStack.push(newFrame);

        // Execute the routine.
        CompoundStatementContext stmtCtx = (CompoundStatementContext) routineEntry.getExecutable();
        visit(stmtCtx);

        // Get the function value from its associated variable.
        String functionName = routineEntry.getName();
        Cell valueCell = newFrame.getCell(functionName);
        Object functionValue = valueCell.getValue();

        // Pop off the routine's stack frame.
        runtimeStack.pop();

        return functionValue;
    }

    Object factorNot(FactorNotContext ctx) {
        boolean value = (boolean) visit(ctx.factor());
        return !value;
    }

    Object factorParens(FactorParensContext ctx) {
        return visit(ctx.expression());
    }
}

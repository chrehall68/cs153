package backend.converter;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec_P6.Form.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec_P6;

public class Expressions_P6 extends Converter_P6 {
    String expression(ExpressionContext ctx) {
        SimpleExpressionContext simpleCtx1 = ctx.simpleExpression(0);
        RelOpContext relOpCtx = ctx.relOp();
        String simpleText1 = (String) visit(simpleCtx1);
        String text = simpleText1;

        // Second simple expression?
        if (relOpCtx != null) {
            String op = relOpCtx.getText();

            if (op.equals("=")) op = "==";
            else if (op.equals("<>")) op = "!=";

            SimpleExpressionContext simpleCtx2 = ctx.simpleExpression(1);
            String simpleText2 = (String) visit(simpleCtx2);

            // Java uses the compareTo method for strings.
            if (simpleCtx1.typespec == Predefined.stringType) {
                text = "(" + simpleText1 + ")." + "compareTo(" + simpleText2 + ") " + op + " 0";
            } else {
                text = simpleText1 + " " + op + " " + simpleText2;
            }
        }

        return text;
    }

    String simpleExpression(SimpleExpressionContext ctx) {
        int count = ctx.term().size();
        String text = "";

        if ((ctx.sign() != null) && (ctx.sign().getText().equals("-"))) {
            text += "-";
        }

        // Loop over the terms.
        for (int i = 0; i < count; i++) {
            TermContext termCtx = ctx.term(i);
            text += (String) visit(termCtx);

            if (i < count - 1) {
                String addOp = ctx.addOp(i).getText().toLowerCase();
                if (addOp.equals("or")) addOp = "||";

                text += " " + addOp + " ";
            }
        }

        return text;
    }

    String term(TermContext ctx) {
        int count = ctx.factor().size();
        String mulOp = null;
        String text = "";

        // Loop over the factors.
        for (int i = 0; i < count; i++) {
            FactorContext factorCtx = ctx.factor(i);
            boolean realDivision = false;
            String factorString = (String) visit(factorCtx);

            if (i < count - 1) {
                mulOp = ctx.mulOp(i).getText().toLowerCase();
                if (mulOp.equals("and")) mulOp = " && ";
                else if (mulOp.equals("div")) mulOp = "/";
                else if (mulOp.equals("mod")) mulOp = "%";
                else if (mulOp.equals("/")) realDivision = true;
            }

            if (realDivision && (factorCtx.typespec == Predefined.integerType)) {
                text += "((double) " + factorString + ")";
            } else {
                text += factorString;
            }

            if (i < count - 1) text += mulOp;
        }

        return text;
    }

    String variable(VariableContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        SymtabEntry variableEntry = idCtx.entry;
        String variableName = variableEntry.getName();
        Typespec_P6 typespec = idCtx.typespec;
        Kind kind = variableEntry.getKind();

        if ((typespec != Predefined.booleanType) && (kind == ENUMERATED_CONSTANT)) {
            String typeIdName = typespec.getIdentifier().getName();
            variableName = typeIdName + "." + variableName;
        }

        if (kind == FUNCTION) variableName += "()";

        // Loop over any subscript and field modifiers.
        for (ModifierContext modCtx : ctx.modifier()) {
            // Subscripts.
            if (modCtx.indexList() != null) {
                for (IndexContext indexCtx : modCtx.indexList().index()) {
                    Typespec_P6 indexType = typespec.getArrayIndexType();
                    int minIndex = 0;

                    if (indexType.getForm() == SUBRANGE) {
                        minIndex = (Integer) indexType.getSubrangeMinValue();
                    }

                    ExpressionContext exprCtx = indexCtx.expression();
                    String expr = (String) visit(exprCtx);
                    String subscript =
                            (minIndex == 0)
                                    ? expr
                                    : (minIndex < 0)
                                            ? "(" + expr + ")+" + (-minIndex)
                                            : "(" + expr + ")-" + minIndex;

                    variableName += "[" + subscript + "]";

                    typespec = typespec.getArrayElementType();
                }
            }

            // Record field.
            else {
                FieldContext fieldCtx = modCtx.field();
                String fieldName = fieldCtx.entry.getName();
                variableName += "." + fieldName;
                typespec = fieldCtx.typespec;
            }
        }

        return variableName;
    }

    String functionCall(FunctionCallContext ctx) {
        FunctionNameContext funcNameCtx = ctx.functionName();
        String functionName = funcNameCtx.identifier().entry.getName();

        String text = functionName + "(";

        if (ctx.argumentList() != null) {
            text += (String) visit(ctx.argumentList());
        }

        return text += ")";
    }

    String argumentList(ArgumentListContext ctx) {
        StringBuilder argList = new StringBuilder();
        String separator = "";

        for (ArgumentContext argCtx : ctx.argument()) {
            String argString = (String) visit(argCtx.expression());
            argList.append(separator).append(argString);
            separator = ", ";
        }

        return argList.toString();
    }

    String factorNot(FactorNotContext ctx) {
        return "!(" + (String) visit(ctx.factor()) + ")";
    }

    String factorParens(FactorParensContext ctx) {
        return "(" + (String) visit(ctx.expression()) + ")";
    }
}

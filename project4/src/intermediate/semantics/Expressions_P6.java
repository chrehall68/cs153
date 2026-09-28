package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.TypeChecker.*;
import static intermediate.type.Typespec_P6.Form.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec_P6;

import java.util.ArrayList;
import java.util.List;

public class Expressions_P6 extends Semantics_P6 {
    Typespec_P6 expression(ExpressionContext ctx) {
        SimpleExpressionContext simpleExprCtx1 = ctx.simpleExpression(0);

        // First simple expression.
        Typespec_P6 simpleTypespec1 = (Typespec_P6) visit(simpleExprCtx1);
        ctx.typespec = simpleTypespec1;

        RelOpContext relOpCtx = ctx.relOp();

        // Second simple expression?
        if (relOpCtx != null) {
            SimpleExpressionContext simpleExprCtx2 = ctx.simpleExpression(1);

            Typespec_P6 simpleTypespec2 = (Typespec_P6) visit(simpleExprCtx2);
            if (!comparisonCompatible(simpleTypespec1, simpleTypespec2)) {
                error.flag(INCOMPATIBLE_COMPARISON, ctx);
            }

            ctx.typespec = Predefined.booleanType;
        }

        return ctx.typespec;
    }

    Typespec_P6 simpleExpression(SimpleExpressionContext ctx) {
        int count = ctx.term().size();
        SignContext signCtx = ctx.sign();
        Boolean hasSign = signCtx != null;

        // First term.
        TermContext termCtx1 = ctx.term(0);
        Typespec_P6 termTypespec1 = (Typespec_P6) visit(termCtx1);
        ctx.typespec = termTypespec1;

        // Loop over any subsequent terms.
        for (int i = 1; i < count; i++) {
            String op = ctx.addOp(i - 1).getText().toLowerCase();
            TermContext termCtx2 = ctx.term(i);
            Typespec_P6 termTypespec2 = (Typespec_P6) visit(termCtx2);

            if (op.equals("or")) {
                if (!termTypespec1.isBoolean()) {
                    error.flag(TYPE_MUST_BE_BOOLEAN, termCtx1);
                }
                if (!termTypespec2.isBoolean()) {
                    error.flag(TYPE_MUST_BE_BOOLEAN, termCtx2);
                }
                if (hasSign) {
                    error.flag(INVALID_SIGN, signCtx);
                }

                ctx.typespec = Predefined.booleanType;
            } else if (areBothInteger(termTypespec1, termTypespec2)) {
                ctx.typespec = Predefined.integerType;
            } else if (isAtLeastOneReal(termTypespec1, termTypespec2)) {
                ctx.typespec = Predefined.realType;
            } else if (areBothString(termTypespec1, termTypespec2)) {
                if (!op.equals("+")) {
                    error.flag(TYPE_MUST_BE_NUMERIC, termCtx1);
                    ctx.typespec = Predefined.integerType;
                } else if (hasSign) {
                    error.flag(INVALID_SIGN, signCtx);
                    ctx.typespec = Predefined.stringType;
                }
            } else {
                if (!termTypespec1.isNumeric()) {
                    error.flag(TYPE_MUST_BE_NUMERIC, termCtx1);
                }
                if (!termTypespec2.isNumeric()) {
                    error.flag(TYPE_MUST_BE_NUMERIC, termCtx2);
                }
                ctx.typespec = Predefined.integerType;
            }

            termTypespec1 = termTypespec2;
        }

        return ctx.typespec;
    }

    Typespec_P6 term(TermContext ctx) {
        int count = ctx.factor().size();

        // First factor.
        FactorContext factorCtx1 = ctx.factor(0);
        Typespec_P6 factorTypespec1 = (Typespec_P6) visit(factorCtx1);
        ctx.typespec = factorTypespec1;

        // Loop over any subsequent factors.
        for (int i = 1; i < count; i++) {
            String op = ctx.mulOp(i - 1).getText().toLowerCase();
            FactorContext factorCtx2 = ctx.factor(i);
            Typespec_P6 factorTypespec2 = (Typespec_P6) visit(factorCtx2);

            if (op.equals("and")) {
                if (!factorTypespec1.isBoolean()) {
                    error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx1);
                }
                if (!factorTypespec2.isBoolean()) {
                    error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx2);
                }
                ctx.typespec = Predefined.booleanType;
            } else if (op.equals("*")) {
                if (areBothInteger(factorTypespec1, factorTypespec2)) {
                    ctx.typespec = Predefined.integerType;
                } else if (isAtLeastOneReal(factorTypespec1, factorTypespec2)) {
                    ctx.typespec = Predefined.realType;
                } else {
                    if (!factorTypespec1.isNumeric()) {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx1);
                    }
                    if (!factorTypespec2.isNumeric()) {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx2);
                    }
                    ctx.typespec = Predefined.integerType;
                }
            } else if (op.equals("/")) {
                if (areBothInteger(factorTypespec1, factorTypespec2)
                        || isAtLeastOneReal(factorTypespec1, factorTypespec2)) {
                    ctx.typespec = Predefined.realType;
                } else {
                    if (!factorTypespec1.isNumeric()) {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx1);
                    }
                    if (!factorTypespec2.isNumeric()) {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx2);
                    }
                    ctx.typespec = Predefined.realType;
                }
            } else if (op.equals("div") || op.equals("mod")) {
                if (!factorTypespec1.isInteger()) {
                    error.flag(TYPE_MUST_BE_INTEGER, factorCtx1);
                }
                if (!factorTypespec2.isInteger()) {
                    error.flag(TYPE_MUST_BE_INTEGER, factorCtx2);
                }
                ctx.typespec = Predefined.integerType;
            }

            factorTypespec1 = factorTypespec2;
        }

        return ctx.typespec;
    }

    Typespec_P6 factorVariable(FactorVariableContext ctx) {
        VariableContext variableCtx = ctx.variable();
        ctx.typespec = variableCtx.typespec = variable(variableCtx);

        return ctx.typespec;
    }

    Typespec_P6 factorIntegerConstant(FactorIntegerConstantContext ctx) {
        IntegerConstantContext integerConstCtx = ctx.integerConstant();
        ctx.typespec = integerConstCtx.typespec = (Typespec_P6) visit(integerConstCtx);

        return ctx.typespec;
    }

    Typespec_P6 factorRealConstant(FactorRealConstantContext ctx) {
        RealConstantContext realConstCtx = ctx.realConstant();
        ctx.typespec = realConstCtx.typespec = (Typespec_P6) visit(realConstCtx);

        return ctx.typespec;
    }

    Typespec_P6 factorCharacterConstant(FactorCharacterConstantContext ctx) {
        CharacterConstantContext characterConstCtx = ctx.characterConstant();
        ctx.typespec = characterConstCtx.typespec = (Typespec_P6) visit(characterConstCtx);

        return ctx.typespec;
    }

    Typespec_P6 factorStringConstant(FactorStringConstantContext ctx) {
        StringConstantContext stringConstCtx = ctx.stringConstant();
        ctx.typespec = stringConstCtx.typespec = (Typespec_P6) visit(stringConstCtx);

        return ctx.typespec;
    }

    Typespec_P6 factorBooleanConstant(FactorBooleanConstantContext ctx) {
        BooleanConstantContext booleanConstCtx = ctx.booleanConstant();
        ctx.typespec = booleanConstCtx.typespec = (Typespec_P6) visit(booleanConstCtx);

        return ctx.typespec;
    }

    Typespec_P6 factorFunctionCall(FactorFunctionCallContext ctx) {
        FunctionCallContext callCtx = ctx.functionCall();
        FunctionNameContext funcNameCtx = callCtx.functionName();
        IdentifierContext funcIdCtx = funcNameCtx.identifier();
        ArgumentListContext listCtx = callCtx.argumentList();
        String name = callCtx.functionName().getText().toLowerCase();
        SymtabEntry functionEntry = symtabStack.lookup(name);
        boolean badName = false;

        ctx.typespec = Predefined.integerType;

        if (functionEntry == null) {
            error.flag(UNDECLARED_IDENTIFIER, funcNameCtx);
            badName = true;
        } else if (functionEntry.getKind() != FUNCTION) {
            error.flag(NAME_MUST_BE_FUNCTION, funcNameCtx);
            badName = true;
        }

        // Bad function name. Do a simple arguments check and then leave.
        if (badName) {
            for (ArgumentContext exprCtx : listCtx.argument()) {
                visit(exprCtx);
            }
        }

        // Good function name.
        else {
            ArrayList<SymtabEntry> parameters = functionEntry.getRoutineParameters();
            checkCallArguments(listCtx, parameters);
            ctx.typespec = functionEntry.getTypespec();
        }

        funcNameCtx.entry = funcIdCtx.entry = functionEntry;
        funcNameCtx.typespec = funcIdCtx.typespec = ctx.typespec;

        return ctx.typespec;
    }

    Typespec_P6 factorNot(FactorNotContext ctx) {
        FactorContext factorCtx = ctx.factor();
        ctx.typespec = factorCtx.typespec = (Typespec_P6) visit(factorCtx);

        if (factorCtx.typespec != Predefined.booleanType) {
            error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx);
        }

        return ctx.typespec;
    }

    Typespec_P6 factorParens(FactorParensContext ctx) {
        ExpressionContext exprCtx = ctx.expression();
        ctx.typespec = (Typespec_P6) visit(exprCtx);

        return ctx.typespec;
    }

    Typespec_P6 variable(VariableContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        List<ModifierContext> modifierList = ctx.modifier();
        String variableName = idCtx.getText().toLowerCase();
        SymtabEntry variableEntry = symtabStack.lookup(variableName);

        if (variableEntry != null) {
            int lineNumber = ctx.getStart().getLine();
            variableEntry.appendLineNumber(lineNumber);

            Kind kind = variableEntry.getKind();
            switch (kind) {
                case TYPE:
                case PROGRAM:
                case PROGRAM_PARAMETER:
                case PROCEDURE:
                case UNDEFINED:
                    error.flag(INVALID_VARIABLE, ctx);
                    break;

                case CONSTANT:
                case ENUMERATED_CONSTANT:
                case FUNCTION:
                    if (modifierList.size() > 0) {
                        error.flag(INVALID_MODIFIER, ctx.modifier(0));
                    }

                default:
                    break;
            }

            ctx.entry = idCtx.entry = variableEntry;
            idCtx.typespec = variableEntry.getTypespec();

            ctx.typespec = modifiedVariableTypespec(ctx, idCtx.typespec);
        } else {
            error.flag(UNDECLARED_IDENTIFIER, ctx);
            ctx.typespec = Predefined.integerType;
        }

        return ctx.typespec;
    }

    private Typespec_P6 modifiedVariableTypespec(VariableContext varCtx, Typespec_P6 typespec) {
        // Loop over the modifiers.
        for (ModifierContext modCtx : varCtx.modifier()) {
            // Subscripts.
            if (modCtx.indexList() != null) {
                IndexListContext indexListCtx = modCtx.indexList();

                // Loop over the subscripts.
                for (IndexContext indexCtx : indexListCtx.index()) {
                    if (typespec.getForm() == ARRAY) {
                        Typespec_P6 indexTypespec = typespec.getArrayIndexType();
                        ExpressionContext exprCtx = indexCtx.expression();
                        Typespec_P6 exprTypespec = (Typespec_P6) visit(exprCtx);

                        if (indexTypespec.baseType() != exprTypespec.baseType()) {
                            error.flag(TYPE_MISMATCH, exprCtx);
                        }

                        // Datatype of the next dimension.
                        typespec = typespec.getArrayElementType();
                    } else {
                        error.flag(TOO_MANY_SUBSCRIPTS, indexCtx);
                    }
                }
            } else // Record field.
            {
                if (typespec.getForm() == RECORD) {
                    Symtab symtab = typespec.getRecordSymtab();
                    FieldContext fieldCtx = modCtx.field();
                    String fieldName = fieldCtx.identifier().getText().toLowerCase();
                    SymtabEntry fieldEntry = symtab.lookup(fieldName);

                    // Field of the record type?
                    if (fieldEntry != null) {
                        typespec = fieldEntry.getTypespec();
                        fieldCtx.entry = fieldEntry;
                        fieldCtx.typespec = typespec;
                        fieldEntry.appendLineNumber(modCtx.getStart().getLine());
                    } else {
                        error.flag(INVALID_FIELD, modCtx);
                    }
                }

                // Not a record variable.
                else {
                    error.flag(INVALID_FIELD, modCtx);
                }
            }
        }

        return typespec;
    }
}

package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_P6BaseVisitor;
import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabStack;
import intermediate.type.TypeChecker;
import intermediate.type.Typespec_P6;

import java.util.ArrayList;

public class Semantics_P6 extends Pcl_P6BaseVisitor<Object> {
    public static SymtabEntry programEntry;

    protected static SymtabStack symtabStack;
    protected static SemanticErrorHandler error;

    protected static Program_P6 programDeclarations;
    protected static ConstantDefinitions_P6 constantDefinitions;
    protected static TypeDefinitions_P6 typeDefinitions;
    protected static VariableDeclarations_P6 variableDeclarations;
    protected static Statements_P6 statements;
    protected static Expressions_P6 expressions;

    static {
        symtabStack = new SymtabStack();
        error = new SemanticErrorHandler();

        programDeclarations = new Program_P6();
        constantDefinitions = new ConstantDefinitions_P6();
        typeDefinitions = new TypeDefinitions_P6();
        variableDeclarations = new VariableDeclarations_P6();
        statements = new Statements_P6();
        expressions = new Expressions_P6();

        Predefined.initialize(symtabStack);
    }

    public int getErrorCount() {
        return error.getCount();
    }

    protected void checkCallArguments(
            ArgumentListContext listCtx, ArrayList<SymtabEntry> parameters) {
        int parmsCount = parameters.size();
        int argsCount = listCtx != null ? listCtx.argument().size() : 0;

        if (parmsCount != argsCount) {
            error.flag(ARGUMENT_COUNT_MISMATCH, listCtx);
            return;
        }

        // Check each argument against the corresponding parameter.
        for (int i = 0; i < parmsCount; i++) {
            ArgumentContext argCtx = listCtx.argument().get(i);
            ExpressionContext exprCtx = argCtx.expression();
            visit(exprCtx);

            SymtabEntry parmEntry = parameters.get(i);
            Typespec_P6 parmType = parmEntry.getTypespec();
            Typespec_P6 argType = exprCtx.typespec;

            // For a VAR parameter, the argument must be a variable
            // with the same datatype.
            if (parmEntry.getKind() == REFERENCE_PARAMETER) {
                if (expressionIsVariable(exprCtx)) {
                    if (parmType != argType) {
                        error.flag(TYPE_MISMATCH, exprCtx);
                    }
                } else {
                    error.flag(ARGUMENT_MUST_BE_VARIABLE, exprCtx);
                }
            }

            // For a value parameter, the argument type must be
            // assignment compatible with the parameter type.
            else if (!TypeChecker.assignmentCompatible(parmType, argType)) {
                error.flag(TYPE_MISMATCH, exprCtx);
            }
        }
    }

    /**
     * Determine whether or not an expression is a variable only.
     * @param exprCtx the ExpressionContext.
     * @return true if it's an expression only, else false.
     */
    private boolean expressionIsVariable(ExpressionContext exprCtx) {
        // Only a single simple expression?
        if (exprCtx.simpleExpression().size() == 1) {
            SimpleExpressionContext simpleCtx = exprCtx.simpleExpression().get(0);
            // Only a single term?
            if (simpleCtx.term().size() == 1) {
                TermContext termCtx = simpleCtx.term().get(0);

                // Only a single factor?
                if (termCtx.factor().size() == 1) {
                    return termCtx.factor().get(0) instanceof FactorVariableContext;
                }
            }
        }

        return false;
    }

    @Override
    public Object visitProgram(ProgramContext ctx) {
        return programDeclarations.program(ctx);
    }

    @Override
    public Object visitProgramHeader(ProgramHeaderContext ctx) {
        return programDeclarations.programHeader(ctx);
    }

    @Override
    public Object visitConstantDefinition(ConstantDefinitionContext ctx) {
        return constantDefinitions.constantDefinition(ctx);
    }

    @Override
    public Object visitConstantSignedIdentifier(ConstantSignedIdentifierContext ctx) {
        return constantDefinitions.constantSignedIdentifier(ctx);
    }

    @Override
    public Object visitConstantSignedInteger(ConstantSignedIntegerContext ctx) {
        return constantDefinitions.constantSignedInteger(ctx);
    }

    @Override
    public Object visitConstantSignedReal(ConstantSignedRealContext ctx) {
        return constantDefinitions.constantSignedReal(ctx);
    }

    @Override
    public Object visitConstantCharacter(ConstantCharacterContext ctx) {
        return constantDefinitions.constantCharacter(ctx);
    }

    @Override
    public Object visitConstantString(ConstantStringContext ctx) {
        return constantDefinitions.constantString(ctx);
    }

    @Override
    public Object visitConstantBoolean(ConstantBooleanContext ctx) {
        return constantDefinitions.constantBoolean(ctx);
    }

    @Override
    public Object visitConstantIdentifier(ConstantIdentifierContext ctx) {
        return constantDefinitions.constantIdentifier(ctx);
    }

    @Override
    public Object visitIntegerConstant(IntegerConstantContext ctx) {
        return constantDefinitions.integerConstant(ctx);
    }

    @Override
    public Object visitRealConstant(RealConstantContext ctx) {
        return constantDefinitions.realConstant(ctx);
    }

    @Override
    public Object visitCharacterConstant(CharacterConstantContext ctx) {
        return constantDefinitions.characterConstant(ctx);
    }

    @Override
    public Object visitStringConstant(StringConstantContext ctx) {
        return constantDefinitions.stringConstant(ctx);
    }

    @Override
    public Object visitBooleanConstant(BooleanConstantContext ctx) {
        return constantDefinitions.booleanConstant(ctx);
    }

    @Override
    public Object visitTypeDefinition(TypeDefinitionContext ctx) {
        return typeDefinitions.typeDefinition(ctx);
    }

    @Override
    public Object visitTypeSpecification(TypeSpecificationContext ctx) {
        return typeDefinitions.typeSpecification(ctx);
    }

    @Override
    public Object visitTypeIdentifier(TypeIdentifierContext ctx) {
        return typeDefinitions.typeIdentifier(ctx);
    }

    @Override
    public Object visitEnumeratedType(EnumeratedTypeContext ctx) {
        return typeDefinitions.enumeratedType(ctx);
    }

    @Override
    public Object visitSubrangeType(SubrangeTypeContext ctx) {
        return typeDefinitions.subrangeType(ctx);
    }

    @Override
    public Object visitArrayType(ArrayTypeContext ctx) {
        return typeDefinitions.arrayType(ctx);
    }

    @Override
    public Object visitRecordType(RecordTypeContext ctx) {
        return typeDefinitions.recordType(ctx);
    }

    @Override
    public Object visitVariableDeclarations(VariableDeclarationsContext ctx) {
        return variableDeclarations.variableDeclarations(ctx);
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) {
        return statements.assignmentStatement(ctx);
    }

    @Override
    public Object visitRepeatStatement(RepeatStatementContext ctx) {
        return statements.repeatStatement(ctx);
    }

    @Override
    public Object visitFactorFunctionCall(FactorFunctionCallContext ctx) {
        return expressions.factorFunctionCall(ctx);
    }

    @Override
    public Object visitExpression(ExpressionContext ctx) {
        return expressions.expression(ctx);
    }

    @Override
    public Object visitSimpleExpression(SimpleExpressionContext ctx) {
        return expressions.simpleExpression(ctx);
    }

    @Override
    public Object visitTerm(TermContext ctx) {
        return expressions.term(ctx);
    }

    @Override
    public Object visitVariable(VariableContext ctx) {
        return expressions.variable(ctx);
    }

    @Override
    public Object visitFactorVariable(FactorVariableContext ctx) {
        return expressions.factorVariable(ctx);
    }

    @Override
    public Object visitFactorRealConstant(FactorRealConstantContext ctx) {
        return expressions.factorRealConstant(ctx);
    }

    @Override
    public Object visitFactorCharacterConstant(FactorCharacterConstantContext ctx) {
        return expressions.factorCharacterConstant(ctx);
    }

    @Override
    public Object visitFactorStringConstant(FactorStringConstantContext ctx) {
        return expressions.factorStringConstant(ctx);
    }

    @Override
    public Object visitFactorBooleanConstant(FactorBooleanConstantContext ctx) {
        return expressions.factorBooleanConstant(ctx);
    }

    @Override
    public Object visitFactorIntegerConstant(FactorIntegerConstantContext ctx) {
        return expressions.factorIntegerConstant(ctx);
    }

    @Override
    public Object visitFactorNot(FactorNotContext ctx) {
        return expressions.factorNot(ctx);
    }

    @Override
    public Object visitFactorParens(FactorParensContext ctx) {
        return expressions.factorParens(ctx);
    }

    @Override
    public Object visitProcFuncDefinition(ProcFuncDefinitionContext ctx) {
        return programDeclarations.procFuncDefinition(ctx);
    }

    @Override
    public Object visitParameterDeclarationsList(ParameterDeclarationsListContext ctx) {
        return programDeclarations.parameterDeclarationsList(ctx);
    }

    @Override
    public Object visitParameterDeclarations(ParameterDeclarationsContext ctx) {
        return programDeclarations.parameterDeclarations(ctx);
    }
}

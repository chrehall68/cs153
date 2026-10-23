package intermediate.semantics;

import java.util.ArrayList;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.antlr4.Pcl_C2BaseVisitor;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabStack;
import intermediate.type.TypeChecker;
import intermediate.type.Typespec;
import backend.BackendMode.Mode;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.semantics.SemanticErrorHandler.Code.*;

public class Semantics extends Pcl_C2BaseVisitor<Object>
{
    public static SymtabEntry programEntry;
    public static Mode mode;

    protected static SymtabStack symtabStack;
    protected static SemanticErrorHandler error;
    
    protected static Program     programImpl;
    protected static Constants   constantsImpl;
    protected static Types       typesImpl;
    protected static Variables   variablesImpl;
    protected static ProcFuncs   procFuncsImpl;
    protected static Statements  statementsImpl;
    protected static Expressions expressionsImpl;
    
    static
    {
        symtabStack = new SymtabStack();
        error  = new SemanticErrorHandler();
        
        programImpl     = new Program();
        constantsImpl   = new Constants();
        typesImpl       = new Types();
        variablesImpl   = new Variables();
        procFuncsImpl   = new ProcFuncs();
        statementsImpl  = new Statements();
        expressionsImpl = new Expressions();
        
        Predefined.initialize(symtabStack);
    } 
    
    public int getErrorCount() { return error.getCount(); }
    
    protected void checkCallArguments(ArgumentListContext listCtx,
                                    ArrayList<SymtabEntry> parameters)
    {
        int parmsCount = parameters.size();
        int argsCount = listCtx != null ? listCtx.argument().size() : 0;
        
        if (parmsCount != argsCount)
        {
            error.flag(ARGUMENT_COUNT_MISMATCH, listCtx);
            return;
        }
        
        // Check each argument against the corresponding parameter.
        for (int i = 0; i < parmsCount; i++)
        {
            ArgumentContext argCtx = listCtx.argument().get(i);
            ExpressionContext exprCtx = argCtx.expression();
            visit(exprCtx);
            
            SymtabEntry parmEntry = parameters.get(i);
            Typespec parmType = parmEntry.getTypespec ();
            Typespec argType  = exprCtx.typespec;
            
            // For a VAR parameter, the argument must be a variable
            // with the same datatype.
            if (parmEntry.getKind() == REFERENCE_PARAMETER)
            {
                if (expressionIsVariable(exprCtx))
                {
                    if (parmType != argType)
                    {
                        error.flag(TYPE_MISMATCH, exprCtx);
                    }
                }
                else
                {
                    error.flag(ARGUMENT_MUST_BE_VARIABLE, exprCtx);
                }
            }
            
            // For a value parameter, the argument type must be
            // assignment compatible with the parameter type.
            else if (!TypeChecker.assignmentCompatible(parmType, argType))
            {
                error.flag(TYPE_MISMATCH, exprCtx);
            }
        }
    }
    
    /**
     * Determine whether or not an expression is a variable only.
     * @param exprCtx the ExpressionContext.
     * @return true if it's an expression only, else false.
     */
    private boolean expressionIsVariable(ExpressionContext exprCtx)
    {
        // Only a single simple expression?
        if (exprCtx.simpleExpression().size() == 1)
        {
            SimpleExpressionContext simpleCtx = 
                                              exprCtx.simpleExpression().get(0);
            // Only a single term?
            if (simpleCtx.term().size() == 1)
            {
                TermContext termCtx = simpleCtx.term().get(0);
                
                // Only a single factor?
                if (termCtx.factor().size() == 1)
                {
                    return termCtx.factor().get(0) instanceof 
                                            FactorVariableContext;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public Object visitProgram(ProgramContext ctx)
    {
        return programImpl.program(ctx);
    }
    
    @Override 
    public Object visitProgramHeader(ProgramHeaderContext ctx) 
    { 
        return programImpl.programHeader(ctx);
    }

    @Override 
    public Object visitConstantDefinition(ConstantDefinitionContext ctx) 
    { 
        return constantsImpl.constantDefinition(ctx);
    }
    
    @Override
    public Object visitConstantSignedIdentifier(ConstantSignedIdentifierContext ctx)
    {
        return constantsImpl.constantSignedIdentifier(ctx);
    }
    
    @Override
    public Object visitConstantSignedInteger(ConstantSignedIntegerContext ctx)
    {
        return constantsImpl.constantSignedInteger(ctx);
    }
    
    @Override
    public Object visitConstantSignedReal(ConstantSignedRealContext ctx)
    {
        return constantsImpl.constantSignedReal(ctx);
    }
    
    @Override
    public Object visitConstantCharacter(ConstantCharacterContext ctx)
    {
        return constantsImpl.constantCharacter(ctx);
    }
    
    @Override
    public Object visitConstantString(ConstantStringContext ctx)
    {
        return constantsImpl.constantString(ctx);
    }
    
    @Override
    public Object visitConstantBoolean(ConstantBooleanContext ctx)
    {
        return constantsImpl.constantBoolean(ctx);
    }
    
    @Override
    public Object visitConstantIdentifier(ConstantIdentifierContext ctx)
    {
        return constantsImpl.constantIdentifier(ctx);
    }
    
    @Override
    public Object visitIntegerConstant(IntegerConstantContext ctx)
    {
        return constantsImpl.integerConstant(ctx);
    }
    
    @Override
    public Object visitRealConstant(RealConstantContext ctx)
    {
        return constantsImpl.realConstant(ctx);
    }
    
    @Override
    public Object visitCharacterConstant(CharacterConstantContext ctx)
    {
        return constantsImpl.characterConstant(ctx);
    }
    
    @Override
    public Object visitStringConstant(StringConstantContext ctx)
    {
        return constantsImpl.stringConstant(ctx);
    }
    
    @Override
    public Object visitBooleanConstant(BooleanConstantContext ctx)
    {
        return constantsImpl.booleanConstant(ctx);
    }
    
    @Override 
    public Object visitTypeDefinition(TypeDefinitionContext ctx) 
    { 
        return typesImpl.typeDefinition(ctx);
    }
    
    @Override 
    public Object visitTypeSpecification(TypeSpecificationContext ctx) 
    { 
        return typesImpl.typeSpecification(ctx);
    }
    
    @Override 
    public Object visitTypeIdentifier(TypeIdentifierContext ctx)
    { 
        return typesImpl.typeIdentifier(ctx);
    }

    @Override 
    public Object visitEnumeratedType(EnumeratedTypeContext ctx) 
    { 
        return typesImpl.enumeratedType(ctx);
    }

    @Override 
    public Object visitSubrangeType(SubrangeTypeContext ctx) 
    { 
        return typesImpl.subrangeType(ctx);
    }

    @Override 
    public Object visitArrayType(ArrayTypeContext ctx) 
    { 
        return typesImpl.arrayType(ctx);
    }

    @Override 
    public Object visitRecordType(RecordTypeContext ctx) 
    {
        return typesImpl.recordType(ctx);
    }

    @Override 
    public Object visitVariableDeclarations(
                                VariableDeclarationsContext ctx) 
    { 
        return variablesImpl.variableDeclarations(ctx);
    }
    
    @Override
    public Object visitProcFuncDefinition(ProcFuncDefinitionContext ctx) 
    {
        return programImpl.procFuncDefinition(ctx);
    }

    @Override 
    public Object visitParameterDeclarationsList(
                                ParameterDeclarationsListContext ctx) 
    {
        return programImpl.parameterDeclarationsList(ctx);
    }
    
    @Override 
    public Object visitParameterDeclarations(
                                ParameterDeclarationsContext ctx) 
    {
        return programImpl.parameterDeclarations(ctx);
    }
    
    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) 
    { 
        return statementsImpl.assignmentStatement(ctx);
    }
    
    @Override 
    public Object visitIfStatement(IfStatementContext ctx)
    {
        return statementsImpl.ifStatement(ctx);
    }

    @Override 
    public Object visitRepeatStatement(RepeatStatementContext ctx) 
    {
        return statementsImpl.repeatStatement(ctx);
    }

    @Override 
    public Object visitWhileStatement(WhileStatementContext ctx) 
    { 
        return statementsImpl.whileStatement(ctx); 
    }

    @Override 
    public Object visitForStatement(ForStatementContext ctx) 
    {
        return statementsImpl.forStatement(ctx);
    }
    
    @Override
    public Object visitCaseStatement(CaseStatementContext ctx)
    {
        return statementsImpl.caseStatement(ctx);
    }

    @Override 
    public Object visitProcedureCallStatement(
                                ProcedureCallStatementContext ctx) 
    {
        return statementsImpl.procedureCallStatement(ctx);
    }
    
    @Override 
    public Object visitExpression(ExpressionContext ctx) 
    { 
        return expressionsImpl.expression(ctx); 
    }

    @Override 
    public Object visitSimpleExpression(SimpleExpressionContext ctx) 
    { 
        return expressionsImpl.simpleExpression(ctx); 
    }

    @Override 
    public Object visitTerm(TermContext ctx) 
    { 
        return expressionsImpl.term(ctx); 
    }
    
    @Override
    public Object visitVariable(VariableContext ctx)
    {
        return expressionsImpl.variable(ctx);
    }
    
    @Override
    public Object visitFactorVariable(FactorVariableContext ctx)
    {
        return expressionsImpl.factorVariable(ctx);
    }
    
    @Override
    public Object visitFactorRealConstant(FactorRealConstantContext ctx)
    {
        return expressionsImpl.factorRealConstant(ctx);
    }
    
    @Override
    public Object visitFactorCharacterConstant(FactorCharacterConstantContext ctx)
    {
        return expressionsImpl.factorCharacterConstant(ctx);
    }
    
    @Override
    public Object visitFactorStringConstant(FactorStringConstantContext ctx)
    {
        return expressionsImpl.factorStringConstant(ctx);
    }
    
    @Override
    public Object visitFactorBooleanConstant(FactorBooleanConstantContext ctx)
    {
        return expressionsImpl.factorBooleanConstant(ctx);
    }
    
    @Override
    public Object visitFactorIntegerConstant(FactorIntegerConstantContext ctx)
    {
        return expressionsImpl.factorIntegerConstant(ctx);
    }

    @Override 
    public Object visitFactorFunctionCall(FactorFunctionCallContext ctx) 
    {
        return expressionsImpl.factorFunctionCall(ctx);
    }
    
    @Override
    public Object visitFactorNot(FactorNotContext ctx)
    {
        return expressionsImpl.factorNot(ctx);
    }
    
    @Override
    public Object visitFactorParens(FactorParensContext ctx)
    {
        return expressionsImpl.factorParens(ctx);
    }
}

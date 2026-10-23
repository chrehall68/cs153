package intermediate.semantics;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.TypeChecker.*;
import static intermediate.type.Typespec.Form.*;

import java.util.ArrayList;
import java.util.HashSet;

public class Statements extends Semantics
{
    Object assignmentStatement(AssignmentStatementContext ctx)
    {
        VariableContext variableCtx = ctx.variable();
        ExpressionContext exprCtx   = ctx.expression();
        
        Typespec variableTypespec = (Typespec) visit(variableCtx);
        Typespec exprTypespec     = (Typespec) visit(exprCtx);
        
        if (!assignmentCompatible(variableTypespec, exprTypespec))
        {
            error.flag(INCOMPATIBLE_ASSIGNMENT, exprCtx);
        }
        
        return null;
    }
    
    Object ifStatement(IfStatementContext ctx)
    {
        ExpressionContext     exprCtx  = ctx.expression();
        TrueStatementContext  trueCtx  = ctx.trueStatement();
        FalseStatementContext falseCtx = ctx.falseStatement();
        
        visit(exprCtx);
        Typespec exprTypespec = exprCtx.typespec;
        
        if (!exprTypespec.isBoolean())
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
        
        visit(trueCtx);
        if (falseCtx != null) visit(falseCtx);
        
        return null;
    }
        
    Object repeatStatement(RepeatStatementContext ctx)
    {
        ExpressionContext exprCtx = ctx.expression();
        Typespec exprTypespec = (Typespec) visit(exprCtx);
        
        if (!exprTypespec.isBoolean())
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
        
        visit(ctx.statementList());
        return null;
    }
    
    Object whileStatement(WhileStatementContext ctx)
    {
        ExpressionContext exprCtx = ctx.expression();
        visit(exprCtx);
        Typespec exprTypespec = exprCtx.typespec;
        
        if (!exprTypespec.isBoolean())
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
        
        visit(ctx.statement());
        return null;
    }
    
    Object forStatement(ForStatementContext ctx)
    {
        VariableContext variableCtx = ctx.variable();
        visit(variableCtx);
        
        String controlName = 
                    variableCtx.identifier().getText().toLowerCase();
        Typespec controlTypespec = Predefined.integerType;
        
        if (variableCtx.entry != null)
        {
            controlTypespec = variableCtx.typespec.baseType();
            
            if (   (controlTypespec.getForm() != SCALAR)
                || (controlTypespec == Predefined.realType)
                || (controlTypespec == Predefined.stringType)
                || (variableCtx.modifier().size() != 0))
            {
                error.flag(INVALID_CONTROL_VARIABLE, variableCtx);
            }
        }
        else
        {
            error.flag(UNDECLARED_IDENTIFIER, ctx.getStart().getLine(), 
                       controlName);
        }
        
        ExpressionContext startCtx = ctx.expression().get(0);
        ExpressionContext endCtx   = ctx.expression().get(1);
        
        visit(startCtx);
        visit(endCtx);
        
        if (startCtx.typespec != controlTypespec)
        {
            error.flag(TYPE_MISMATCH, startCtx);
        }
        if (startCtx.typespec != endCtx.typespec) 
        {
            error.flag(TYPE_MISMATCH, endCtx);
        }
        
        visit(ctx.statement());
        return null;
    }
    
    Object caseStatement(CaseStatementContext ctx)
    {
        ExpressionContext exprCtx = ctx.expression();
        visit(exprCtx);
        Typespec exprTypespec = exprCtx.typespec;
        
        if (!exprTypespec.isOrdinal())
        {
            error.flag(TYPE_MISMATCH, exprCtx);
            exprTypespec = Predefined.integerType;
        }
        
        CaseBranchListContext branchListCtx = ctx.caseBranchList();
        
        // Loop over the CASE branches.
        for (CaseBranchContext branchCtx 
                        : branchListCtx.caseBranch())
        {
            caseBranch(branchCtx, exprTypespec);
        }
        
        return null;
    }
    
    private void caseBranch(CaseBranchContext branchCtx,
                            Typespec exprTypespec)
    {
        CaseConstantListContext constListCtx = 
                                    branchCtx.caseConstantList();
        StatementContext stmtCtx = branchCtx.statement();
        HashSet<Integer> constants = new HashSet<>();
        
        if (constListCtx != null)
        {
            // Loop over the CASE constants in each branch.
            for (CaseConstantContext caseConstCtx :
                                    constListCtx.caseConstant())
            {
                ConstantContext constCtx = caseConstCtx.constant();
                Typespec constTypespec = (Typespec) visit(constCtx);
                Object constValue = constCtx.value;
                
                if (constTypespec != exprTypespec)
                {
                    error.flag(TYPE_MISMATCH, constCtx);
                }
                else if (   (constTypespec == Predefined.integerType)
                         || (constTypespec.getForm() == ENUMERATED))
                {
                    caseConstCtx.value = (Integer) constValue;
                }
                else if (constCtx.typespec == Predefined.charType)
                {
                    caseConstCtx.value = (Character) constValue;
                }
                
                if (constants.contains(caseConstCtx.value))
                {
                    error.flag(DUPLICATE_CASE_CONSTANT, constCtx);
                }
                else
                {
                    constants.add(caseConstCtx.value);
                }
            }
        }
        
        if (stmtCtx != null) visit(stmtCtx);
    }
    
    Object procedureCallStatement(ProcedureCallStatementContext ctx)
    {
        ProcedureNameContext procNameCtx = ctx.procedureName();
        IdentifierContext procIdCtx = procNameCtx.identifier();
        ArgumentListContext listCtx = ctx.argumentList();
        String name = ctx.procedureName().getText().toLowerCase();
        SymtabEntry procedureEntry = symtabStack.lookup(name);
        boolean badName = false;

        if (procedureEntry == null)
        {
            error.flag(UNDECLARED_IDENTIFIER, procNameCtx);
            badName = true;
        } else if (procedureEntry.getKind() != PROCEDURE)
        {
            error.flag(NAME_MUST_BE_PROCEDURE, procNameCtx);
            badName = true;
        }

        // Bad procedure name. Do a simple arguments check and then leave.
        if (badName)
        {
            for (ArgumentContext exprCtx : listCtx.argument())
            {
                visit(exprCtx);
            }
        }

        // Good procedure name.
        else
        {
            ArrayList<SymtabEntry> parms = procedureEntry.getRoutineParameters();
            checkCallArguments(listCtx, parms);
        }

        procNameCtx.entry = procIdCtx.entry = procedureEntry;
        return null;
    }
}

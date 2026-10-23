package backend.interpreter;

import java.util.ArrayList;
import java.util.HashMap;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec;

public class Statements extends Executor
{
    Object statement(StatementContext ctx)
    {
        if (debugging) debugger.statement(ctx);
        
        executionCount++;
        visitChildren(ctx);
        
        return null;
    }
    
    Object statementList(StatementListContext ctx) 
    {
        for (StatementContext stmtCtx : ctx.statement())
        {
            if (stmtCtx.emptyStatement() == null)
            {
                visit(stmtCtx);
            }
        }
        
        return null;
    }
    
    Object assignmentStatement(AssignmentStatementContext ctx)
    {
        ExpressionContext exprCtx = ctx.expression();
        Object value = visit(exprCtx);
        assignValue(ctx.variable(), value, exprCtx.typespec);

        
        return null;
    }
    
    Object ifStatement(IfStatementContext ctx) 
    {
        TrueStatementContext  trueCtx  = ctx.trueStatement();
        FalseStatementContext falseCtx = ctx.falseStatement();
        boolean value = (Boolean) visit(ctx.expression());
        
        if      (value)            visit(trueCtx);
        else if (falseCtx != null) visit(falseCtx);
        
        return null;
    }    
    
    Object repeatStatement(RepeatStatementContext ctx) 
    {
        StatementListContext listCtx = ctx.statementList();
        boolean value;
        
        do
        {
            visit(listCtx);
            value = (Boolean) visit(ctx.expression());
        } while (!value);
            
        return null;
    }
    
    Object whileStatement(WhileStatementContext ctx) 
    {
        StatementContext stmtCtx = ctx.statement();
        boolean value = (Boolean) visit(ctx.expression());
        
        while (value)
        {
            visit(stmtCtx);
            value = (Boolean) visit(ctx.expression());
        }
        
        return null;
    }
    
    Object forStatement(ForStatementContext ctx) 
    {
        VariableContext   controlCtx   = ctx.variable();
        ExpressionContext startExprCtx = ctx.expression().get(0);
        ExpressionContext stopExprCtx  = ctx.expression().get(1);

        // Initial control value.
        Object startValue = visit(startExprCtx);
        assignValue(controlCtx, startValue, startExprCtx.typespec);
        
        // Terminal control value.
        boolean to = ctx.TO() != null;
        Object stopValue = visit(stopExprCtx);
        
        // Integer control values.
        if (controlCtx.typespec.baseType() == Predefined.integerType)
        {
            int control = (Integer) startValue;
            int stop    = (Integer) stopValue;
            
            if (to)
            {
                while (control <= stop)
                {
                    visit(ctx.statement());
                    Object nextValue = ++control;
                    assignValue(controlCtx, nextValue, Predefined.integerType);
                }
            }
            else  // downto
            {
                while (control >= stop)
                {
                    visit(ctx.statement());
                    Object nextValue = --control;
                    assignValue(controlCtx, nextValue, Predefined.integerType);
                }
            }
        }
        
        // Character control values.
        else
        {
            char control = (Character) startValue;
            char stop    = (Character) stopValue;
            
            if (to)
            {
                while (control <= stop)
                {
                    visit(ctx.statement());
                    Object nextValue = ++control;
                    assignValue(controlCtx, nextValue, Predefined.charType);
                }
            }
            else  // downto
            {
                while (control >= stop)
                {
                    visit(ctx.statement());
                    Object nextValue = --control;
                    assignValue(controlCtx, nextValue, Predefined.charType);
                }
            }
        }

        return null;
    }

    Object caseStatement(CaseStatementContext ctx) 
    {
        ExpressionContext exprCtx = ctx.expression();
        CaseBranchListContext branchListCtx = ctx.caseBranchList();
        
        // First time: Create the jump table.
        if (ctx.jumpTable == null) 
        {
            ctx.jumpTable = createJumpTable(branchListCtx);
        }
        
        Object value = visit(exprCtx);
        int intValue = 
            (exprCtx.typespec == Predefined.integerType) ? (Integer)   value
                                                         : (Character) value;
        
        // From the jump table obtain the statement corresponding to the value.
        StatementContext stmtCtx = ctx.jumpTable.get(intValue);
        if (stmtCtx != null) visit(stmtCtx);

        return null;
    }
    
    /**
     * Create the jump table for a CASE statement.
     * @param branchListCtx the CaseBranchListContext.
     * @return the jump table.
     */
    private HashMap<Integer, StatementContext> createJumpTable(
                            CaseBranchListContext branchListCtx)
    {
        HashMap<Integer, StatementContext> table = new HashMap<>();
        
        // Loop over the CASE branches.
        for (CaseBranchContext branchCtx : branchListCtx.caseBranch())
        {
            CaseConstantListContext constListCtx = 
                                                    branchCtx.caseConstantList();
            StatementContext stmtCtx = branchCtx.statement();
        
            if (constListCtx != null)
            {
                // Loop over the CASE constants of each CASE branch.
                for (CaseConstantContext caseConstCtx : 
                                                    constListCtx.caseConstant())
                {
                    table.put(caseConstCtx.value, stmtCtx);
                }
            }
        }
        
        return table;
    }

    Object procedureCallStatement(ProcedureCallStatementContext ctx) 
    {
        SymtabEntry routineEntry = ctx.procedureName().entry;
        ArgumentListContext argListCtx = ctx.argumentList();
        StackFrame newFrame = new StackFrame(routineEntry);
        
        // Execute any actual parameters and initialize
        // the formal parameters in the routine's new stack frame.
        if (argListCtx != null) 
        {
            ArrayList<SymtabEntry> parameters = 
                                            routineEntry.getRoutineParameters();
            executeCallArguments(argListCtx, parameters, newFrame);
        }

        // Push the routine's stack frame onto the runtime stack 
        // and execute the procedure.
        runtimeStack.push(newFrame);

        // Execute the routine.
        CompoundStatementContext stmtCtx = 
            (CompoundStatementContext) routineEntry.getExecutable();
        visit(stmtCtx);

        // Pop off the routine's stack frame.
        runtimeStack.pop();
        
        return null;
    }
    
    Object writelnStatement(WritelnStatementContext ctx)
    {
        visitChildren(ctx);
        System.out.println();  
        
        return null;
    }
    
    Object writeArguments(WriteArgumentsContext ctx)
    {
        // Loop over the write arguments.
        for (WriteArgumentContext writeArgCtx : ctx.writeArgument())
        {
            ExpressionContext exprCtx = writeArgCtx.expression();
            FormatContext formatCtx = writeArgCtx.format();  
            StringBuilder formatString = new StringBuilder();
            
            Typespec typespec = exprCtx.typespec;
            boolean isIntegerType = typespec == Predefined.integerType;
            boolean isRealType    = typespec == Predefined.realType;
            boolean isCharType    = typespec == Predefined.charType;
            boolean isBooleanType = typespec == Predefined.booleanType;
            
            Object arg = visit(exprCtx);
            String argText = exprCtx.getText();
            
            // A string or character constant without formatting.
            if (   (formatCtx == null)
                && isSingleton(exprCtx)
                && (   (argText.charAt(0) == '"') 
                    || (argText.charAt(0) == '\'')))
            {
                String str = argText.substring(1, argText.length()-1);
                System.out.printf(str);
            }
            
            // For any other argument, append a field specifier.
            else
            {
                formatString.append("%");
                
                if (formatCtx != null)
                {
                    WidthContext widthCtx = formatCtx.width();
                    SignContext signCtx = widthCtx.sign();
                    String sign = (   (signCtx != null) 
                                   && (signCtx.getText().equals("-"))) 
                                ? "-" : "";
                    formatString.append(sign)
                                .append(widthCtx.integerConstant()
                                .getText());
                    
                    if (isRealType)
                    {
                        formatString.append(".");
                        
                        PrecisionContext precisionCtx = formatCtx.precision();
                        String precisionText = precisionCtx != null
                                ? precisionCtx.integerConstant().getText()
                                : "0";
                        formatString.append(precisionText);
                    }
                }

                String typeFlag = isIntegerType ? "d"
                                : isRealType    ? "f"
                                : isBooleanType ? "b"
                                : isCharType    ? "c" 
                                :                 "s";
                formatString.append(typeFlag);

                System.out.printf(formatString.toString(), arg);
            }
        }
        
        return null;
    }
    
    private boolean isSingleton(ExpressionContext exprCtx)
    {
        if (exprCtx.simpleExpression().size() > 1) return false;
        
        SimpleExpressionContext simpExprCtx = exprCtx.simpleExpression(0);
        if (simpExprCtx.term().size() > 1) return false;
        
        return true;
    }
}
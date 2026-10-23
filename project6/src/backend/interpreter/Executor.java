package backend.interpreter;

import static backend.BackendMode.Mode.DEBUGGER;

import static intermediate.symtab.SymtabEntry.Kind.*;

import backend.BackendMode;

import intermediate.antlr4.Pcl_C2BaseVisitor;
import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.semantics.Semantics;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;

import java.util.ArrayList;
import java.util.Scanner;

public class Executor extends Pcl_C2BaseVisitor<Object> {
    protected static String currentSeparator = "";
    protected static String programName;

    protected static int executionCount = 0; // count of executed statements
    protected static RuntimeStack runtimeStack; // runtime stack
    protected static Scanner sysin; // runtime input
    protected static RuntimeErrorHandler error; // runtime error handler
    protected static Debugger debugger; // interactive debugger
    protected static BackendMode mode; // backend mode
    protected static SymtabEntry programEntry;
    protected static boolean debugging;

    protected static Program program;
    protected static Statements statements;
    protected static Expressions expressions;

    static {
        program = new Program();
        statements = new Statements();
        expressions = new Expressions();

        runtimeStack = new RuntimeStack();
        sysin = new Scanner(System.in);
        error = new RuntimeErrorHandler();
        debugger = new Debugger(runtimeStack);

        programEntry = Semantics.programEntry;
    }

    public Executor() {
        debugging = BackendMode.mode == DEBUGGER;
    }

    /**
     * Convert a Pascal string to a Java string.
     * @param pascalString the Pascal string.
     * @return the Java string.
     */
    protected String convertString(String pascalString) {
        String unquoted = pascalString.substring(1, pascalString.length() - 1);
        return unquoted.replace("''", "'");
    }

    /**
     * Assign a value to a target variable's memory cell.
     * @param variableCtx the VariableContext of the target.
     * @param value the value to assign.
     * @param valueType the datatype of the value.
     * @return the target variable's memory cell.
     */
    protected Cell assignValue(VariableContext variableCtx, Object value, Typespec valueType) {
        Typespec targetType = variableCtx.typespec;
        Cell targetCell = (Cell) visit(variableCtx);

        assignValue(targetCell, targetType, value, valueType);
        if (debugging) {
            debugger.assignment(variableCtx, value, valueType);
        }

        return targetCell;
    }

    /**
     * Assign a value to a target variable's memory cell.
     * @param targetCell the target variable's memory cell.
     * @param targetType the datatype of the target variable.
     * @param value the value to assign.
     * @param valueType the datatype of the value.
     */
    protected void assignValue(
            Cell targetCell, Typespec targetType, Object value, Typespec valueType) {
        // Assign with any necessary type conversions.
        if ((targetType == Predefined.integerType) && (valueType == Predefined.charType)) {
            int charValue = (Character) value;
            targetCell.setValue(charValue);
        } else if (targetType == Predefined.realType) {
            double doubleValue =
                    (valueType == Predefined.integerType)
                            ? (Integer) value
                            : (valueType == Predefined.charType)
                                    ? (Character) value
                                    : (Double) value;
            targetCell.setValue(doubleValue);
        } else {
            targetCell.setValue(value);
        }
    }

    /**
     * Execute procedure and function call arguments.
     * @param argListCtx the ArgumentListContext
     * @param parameters the routine's parameters.
     * @param frame the routine's stack frame.
     */
    protected void executeCallArguments(
            ArgumentListContext argListCtx, ArrayList<SymtabEntry> parameters, StackFrame frame) {
        // Loop over the parameters.
        for (int i = 0; i < parameters.size(); i++) {
            SymtabEntry parmEntry = parameters.get(i);
            String parmName = parmEntry.getName();
            Kind parmKind = parmEntry.getKind();
            Cell parmCell = frame.getCell(parmName);
            ArgumentContext argCtx = argListCtx.argument().get(i);
            Object value = visit(argCtx);

            // Value parameter: Copy the argument's value.
            if (parmKind == VALUE_PARAMETER) {
                assignValue(parmCell, parmEntry.getTypespec(), value, argCtx.expression().typespec);
            }

            // Reference parameter: Copy the argument's cell.
            else {
                FactorContext factorCtx =
                        argCtx.expression().simpleExpression().get(0).term().get(0).factor().get(0);
                VariableContext varCtx = ((FactorVariableContext) factorCtx).variable();

                Cell argCell = (Cell) visitVariable(varCtx);
                frame.replaceCell(parmName, argCell);
            }
        }
    }

    @Override
    public Object visitProgram(ProgramContext ctx) {
        programName = programEntry.getName();
        return program.program(ctx);
    }

    @Override
    public Object visitParameters(ParametersContext ctx) {
        currentSeparator = "";
        return visit(ctx.parameterDeclarationsList());
    }

    @Override
    public Object visitStatement(StatementContext ctx) {
        return statements.statement(ctx);
    }

    @Override
    public Object visitStatementList(StatementListContext ctx) {
        return statements.statementList(ctx);
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) {
        return statements.assignmentStatement(ctx);
    }

    @Override
    public Object visitIfStatement(IfStatementContext ctx) {
        return statements.ifStatement(ctx);
    }

    @Override
    public Object visitRepeatStatement(RepeatStatementContext ctx) {
        return statements.repeatStatement(ctx);
    }

    @Override
    public Object visitWhileStatement(WhileStatementContext ctx) {
        return statements.whileStatement(ctx);
    }

    @Override
    public Object visitForStatement(ForStatementContext ctx) {
        return statements.forStatement(ctx);
    }

    @Override
    public Object visitCaseStatement(CaseStatementContext ctx) {
        return statements.caseStatement(ctx);
    }

    @Override
    public Object visitProcedureCallStatement(ProcedureCallStatementContext ctx) {
        return statements.procedureCallStatement(ctx);
    }

    @Override
    public Object visitWritelnStatement(WritelnStatementContext ctx) {
        return statements.writelnStatement(ctx);
    }

    @Override
    public Object visitWriteArguments(WriteArgumentsContext ctx) {
        return statements.writeArguments(ctx);
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
    public Object visitFactorIntegerConstant(FactorIntegerConstantContext ctx) {
        return expressions.integerConstant(ctx.integerConstant());
    }

    @Override
    public Object visitFactorRealConstant(FactorRealConstantContext ctx) {
        return expressions.realConstant(ctx.realConstant());
    }

    @Override
    public Object visitFactorCharacterConstant(FactorCharacterConstantContext ctx) {
        return expressions.characterConstant(ctx.characterConstant());
    }

    @Override
    public Object visitFactorStringConstant(FactorStringConstantContext ctx) {
        return expressions.stringConstant(ctx.stringConstant());
    }

    @Override
    public Object visitFactorBooleanConstant(FactorBooleanConstantContext ctx) {
        return expressions.booleanConstant(ctx.booleanConstant());
    }

    @Override
    public Object visitFactorFunctionCall(FactorFunctionCallContext ctx) {
        return expressions.factorFunctionCall(ctx);
    }

    @Override
    public Object visitFactorNot(FactorNotContext ctx) {
        return expressions.factorNot(ctx);
    }

    @Override
    public Object visitFactorParens(FactorParensContext ctx) {
        return expressions.factorParens(ctx);
    }
}

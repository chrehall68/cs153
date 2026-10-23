package backend.compiler;

import static intermediate.semantics.Semantics.programEntry;

import intermediate.antlr4.Pcl_C2BaseVisitor;
import intermediate.antlr4.Pcl_C2Parser.*;

public class Compiler extends Pcl_C2BaseVisitor<Object> {
    protected static JasminEmitter code;
    protected static boolean programVariables = true;
    protected static String programName;

    protected static Program programImpl;
    protected static ProcFuncs procFuncsImpl;
    protected static Statements statementsImpl;
    protected static Expressions expressionsImpl;

    static {
        programImpl = new Program();
        procFuncsImpl = new ProcFuncs();
        statementsImpl = new Statements();
        expressionsImpl = new Expressions();
    }

    public String getObjectFileName() {
        return code.getObjectFileName();
    }

    @Override
    public Object visitProgram(ProgramContext ctx) {
        programName = programEntry.getName();
        code = new JasminEmitter(programName, "j");

        return programImpl.program(ctx);
    }

    @Override
    public Object visitProcedureAndFunctionsPart(ProcedureAndFunctionsPartContext ctx) {
        return procFuncsImpl.procedureAndFunctionsPart(ctx);
    }

    @Override
    public Object visitStatement(StatementContext ctx) {
        return statementsImpl.statement(ctx);
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) {
        return statementsImpl.assignmentStatement(ctx);
    }

    @Override
    public Object visitRepeatStatement(RepeatStatementContext ctx) {
        return statementsImpl.repeatStatement(ctx);
    }

    @Override
    public Object visitWhileStatement(WhileStatementContext ctx) {
        return statementsImpl.whileStatement(ctx);
    }

    @Override
    public Object visitForStatement(ForStatementContext ctx) {
        return statementsImpl.forStatement(ctx);
    }

    @Override
    public Object visitIfStatement(IfStatementContext ctx) {
        return statementsImpl.ifStatement(ctx);
    }

    @Override
    public Object visitCaseStatement(CaseStatementContext ctx) {
        return statementsImpl.caseStatement(ctx);
    }

    @Override
    public Object visitProcedureCallStatement(ProcedureCallStatementContext ctx) {
        return statementsImpl.procedureCallStatement(ctx);
    }

    @Override
    public Object visitWriteStatement(WriteStatementContext ctx) {
        return statementsImpl.writeStatement(ctx);
    }

    @Override
    public Object visitWritelnStatement(WritelnStatementContext ctx) {
        return statementsImpl.writelnStatement(ctx);
    }

    @Override
    public Object visitExpression(ExpressionContext ctx) {
        return expressionsImpl.expression(ctx);
    }

    @Override
    public Object visitSimpleExpression(SimpleExpressionContext ctx) {
        return expressionsImpl.simpleExpression(ctx);
    }

    @Override
    public Object visitTerm(TermContext ctx) {
        return expressionsImpl.term(ctx);
    }

    @Override
    public Object visitFunctionCall(FunctionCallContext ctx) {
        expressionsImpl.functionCall(ctx);
        return null;
    }

    @Override
    public Object visitFactorNot(FactorNotContext ctx) {
        return expressionsImpl.factorNot(ctx);
    }

    @Override
    public Object visitVariable(VariableContext ctx) {
        return expressionsImpl.variable(ctx);
    }

    @Override
    public Object visitIntegerConstant(IntegerConstantContext ctx) {
        return expressionsImpl.integerConstant(ctx);
    }

    @Override
    public Object visitRealConstant(RealConstantContext ctx) {
        return expressionsImpl.realConstant(ctx);
    }

    @Override
    public Object visitBooleanConstant(BooleanConstantContext ctx) {
        return expressionsImpl.booleanConstant(ctx);
    }

    @Override
    public Object visitCharacterConstant(CharacterConstantContext ctx) {
        return expressionsImpl.characterConstant(ctx);
    }

    @Override
    public Object visitStringConstant(StringConstantContext ctx) {
        return expressionsImpl.stringConstant(ctx);
    }
}

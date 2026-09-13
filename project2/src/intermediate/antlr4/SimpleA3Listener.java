// Generated from ./SimpleA3.g4 by ANTLR 4.13.2

package intermediate.antlr4;


import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SimpleA3Parser}.
 */
public interface SimpleA3Listener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#program}.
     * @param ctx the parse tree
     */
    void enterProgram(SimpleA3Parser.ProgramContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#program}.
     * @param ctx the parse tree
     */
    void exitProgram(SimpleA3Parser.ProgramContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#programHeader}.
     * @param ctx the parse tree
     */
    void enterProgramHeader(SimpleA3Parser.ProgramHeaderContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#programHeader}.
     * @param ctx the parse tree
     */
    void exitProgramHeader(SimpleA3Parser.ProgramHeaderContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#block}.
     * @param ctx the parse tree
     */
    void enterBlock(SimpleA3Parser.BlockContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#block}.
     * @param ctx the parse tree
     */
    void exitBlock(SimpleA3Parser.BlockContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#declarations}.
     * @param ctx the parse tree
     */
    void enterDeclarations(SimpleA3Parser.DeclarationsContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#declarations}.
     * @param ctx the parse tree
     */
    void exitDeclarations(SimpleA3Parser.DeclarationsContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#identifier}.
     * @param ctx the parse tree
     */
    void enterIdentifier(SimpleA3Parser.IdentifierContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#identifier}.
     * @param ctx the parse tree
     */
    void exitIdentifier(SimpleA3Parser.IdentifierContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#statement}.
     * @param ctx the parse tree
     */
    void enterStatement(SimpleA3Parser.StatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#statement}.
     * @param ctx the parse tree
     */
    void exitStatement(SimpleA3Parser.StatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#emptyStatement}.
     * @param ctx the parse tree
     */
    void enterEmptyStatement(SimpleA3Parser.EmptyStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#emptyStatement}.
     * @param ctx the parse tree
     */
    void exitEmptyStatement(SimpleA3Parser.EmptyStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#compoundStatement}.
     * @param ctx the parse tree
     */
    void enterCompoundStatement(SimpleA3Parser.CompoundStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#compoundStatement}.
     * @param ctx the parse tree
     */
    void exitCompoundStatement(SimpleA3Parser.CompoundStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#statementList}.
     * @param ctx the parse tree
     */
    void enterStatementList(SimpleA3Parser.StatementListContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#statementList}.
     * @param ctx the parse tree
     */
    void exitStatementList(SimpleA3Parser.StatementListContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#assignmentStatement}.
     * @param ctx the parse tree
     */
    void enterAssignmentStatement(SimpleA3Parser.AssignmentStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#assignmentStatement}.
     * @param ctx the parse tree
     */
    void exitAssignmentStatement(SimpleA3Parser.AssignmentStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#repeatStatement}.
     * @param ctx the parse tree
     */
    void enterRepeatStatement(SimpleA3Parser.RepeatStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#repeatStatement}.
     * @param ctx the parse tree
     */
    void exitRepeatStatement(SimpleA3Parser.RepeatStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#caseStatement}.
     * @param ctx the parse tree
     */
    void enterCaseStatement(SimpleA3Parser.CaseStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#caseStatement}.
     * @param ctx the parse tree
     */
    void exitCaseStatement(SimpleA3Parser.CaseStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#selectBranch}.
     * @param ctx the parse tree
     */
    void enterSelectBranch(SimpleA3Parser.SelectBranchContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#selectBranch}.
     * @param ctx the parse tree
     */
    void exitSelectBranch(SimpleA3Parser.SelectBranchContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#selectConstants}.
     * @param ctx the parse tree
     */
    void enterSelectConstants(SimpleA3Parser.SelectConstantsContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#selectConstants}.
     * @param ctx the parse tree
     */
    void exitSelectConstants(SimpleA3Parser.SelectConstantsContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#selectConstant}.
     * @param ctx the parse tree
     */
    void enterSelectConstant(SimpleA3Parser.SelectConstantContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#selectConstant}.
     * @param ctx the parse tree
     */
    void exitSelectConstant(SimpleA3Parser.SelectConstantContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#writeStatement}.
     * @param ctx the parse tree
     */
    void enterWriteStatement(SimpleA3Parser.WriteStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#writeStatement}.
     * @param ctx the parse tree
     */
    void exitWriteStatement(SimpleA3Parser.WriteStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#writelnStatement}.
     * @param ctx the parse tree
     */
    void enterWritelnStatement(SimpleA3Parser.WritelnStatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#writelnStatement}.
     * @param ctx the parse tree
     */
    void exitWritelnStatement(SimpleA3Parser.WritelnStatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#writeArguments}.
     * @param ctx the parse tree
     */
    void enterWriteArguments(SimpleA3Parser.WriteArgumentsContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#writeArguments}.
     * @param ctx the parse tree
     */
    void exitWriteArguments(SimpleA3Parser.WriteArgumentsContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#writeArgument}.
     * @param ctx the parse tree
     */
    void enterWriteArgument(SimpleA3Parser.WriteArgumentContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#writeArgument}.
     * @param ctx the parse tree
     */
    void exitWriteArgument(SimpleA3Parser.WriteArgumentContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#format}.
     * @param ctx the parse tree
     */
    void enterFormat(SimpleA3Parser.FormatContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#format}.
     * @param ctx the parse tree
     */
    void exitFormat(SimpleA3Parser.FormatContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#width}.
     * @param ctx the parse tree
     */
    void enterWidth(SimpleA3Parser.WidthContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#width}.
     * @param ctx the parse tree
     */
    void exitWidth(SimpleA3Parser.WidthContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#precision}.
     * @param ctx the parse tree
     */
    void enterPrecision(SimpleA3Parser.PrecisionContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#precision}.
     * @param ctx the parse tree
     */
    void exitPrecision(SimpleA3Parser.PrecisionContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#sign}.
     * @param ctx the parse tree
     */
    void enterSign(SimpleA3Parser.SignContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#sign}.
     * @param ctx the parse tree
     */
    void exitSign(SimpleA3Parser.SignContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#expression}.
     * @param ctx the parse tree
     */
    void enterExpression(SimpleA3Parser.ExpressionContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#expression}.
     * @param ctx the parse tree
     */
    void exitExpression(SimpleA3Parser.ExpressionContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#simpleExpression}.
     * @param ctx the parse tree
     */
    void enterSimpleExpression(SimpleA3Parser.SimpleExpressionContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#simpleExpression}.
     * @param ctx the parse tree
     */
    void exitSimpleExpression(SimpleA3Parser.SimpleExpressionContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#term}.
     * @param ctx the parse tree
     */
    void enterTerm(SimpleA3Parser.TermContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#term}.
     * @param ctx the parse tree
     */
    void exitTerm(SimpleA3Parser.TermContext ctx);

    /**
     * Enter a parse tree produced by the {@code factorVariable}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void enterFactorVariable(SimpleA3Parser.FactorVariableContext ctx);

    /**
     * Exit a parse tree produced by the {@code factorVariable}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void exitFactorVariable(SimpleA3Parser.FactorVariableContext ctx);

    /**
     * Enter a parse tree produced by the {@code factorUnsignedConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void enterFactorUnsignedConstant(SimpleA3Parser.FactorUnsignedConstantContext ctx);

    /**
     * Exit a parse tree produced by the {@code factorUnsignedConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void exitFactorUnsignedConstant(SimpleA3Parser.FactorUnsignedConstantContext ctx);

    /**
     * Enter a parse tree produced by the {@code factorCharacterConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void enterFactorCharacterConstant(SimpleA3Parser.FactorCharacterConstantContext ctx);

    /**
     * Exit a parse tree produced by the {@code factorCharacterConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void exitFactorCharacterConstant(SimpleA3Parser.FactorCharacterConstantContext ctx);

    /**
     * Enter a parse tree produced by the {@code factorStringConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void enterFactorStringConstant(SimpleA3Parser.FactorStringConstantContext ctx);

    /**
     * Exit a parse tree produced by the {@code factorStringConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void exitFactorStringConstant(SimpleA3Parser.FactorStringConstantContext ctx);

    /**
     * Enter a parse tree produced by the {@code factorNot}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void enterFactorNot(SimpleA3Parser.FactorNotContext ctx);

    /**
     * Exit a parse tree produced by the {@code factorNot}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void exitFactorNot(SimpleA3Parser.FactorNotContext ctx);

    /**
     * Enter a parse tree produced by the {@code factorParenthesized}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void enterFactorParenthesized(SimpleA3Parser.FactorParenthesizedContext ctx);

    /**
     * Exit a parse tree produced by the {@code factorParenthesized}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     */
    void exitFactorParenthesized(SimpleA3Parser.FactorParenthesizedContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#relOp}.
     * @param ctx the parse tree
     */
    void enterRelOp(SimpleA3Parser.RelOpContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#relOp}.
     * @param ctx the parse tree
     */
    void exitRelOp(SimpleA3Parser.RelOpContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#addOp}.
     * @param ctx the parse tree
     */
    void enterAddOp(SimpleA3Parser.AddOpContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#addOp}.
     * @param ctx the parse tree
     */
    void exitAddOp(SimpleA3Parser.AddOpContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#mulOp}.
     * @param ctx the parse tree
     */
    void enterMulOp(SimpleA3Parser.MulOpContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#mulOp}.
     * @param ctx the parse tree
     */
    void exitMulOp(SimpleA3Parser.MulOpContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#variable}.
     * @param ctx the parse tree
     */
    void enterVariable(SimpleA3Parser.VariableContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#variable}.
     * @param ctx the parse tree
     */
    void exitVariable(SimpleA3Parser.VariableContext ctx);

    /**
     * Enter a parse tree produced by the {@code unsignedIntegerConstant}
     * labeled alternative in {@link SimpleA3Parser#unsignedConstant}.
     * @param ctx the parse tree
     */
    void enterUnsignedIntegerConstant(SimpleA3Parser.UnsignedIntegerConstantContext ctx);

    /**
     * Exit a parse tree produced by the {@code unsignedIntegerConstant}
     * labeled alternative in {@link SimpleA3Parser#unsignedConstant}.
     * @param ctx the parse tree
     */
    void exitUnsignedIntegerConstant(SimpleA3Parser.UnsignedIntegerConstantContext ctx);

    /**
     * Enter a parse tree produced by the {@code unsignedRealConstant}
     * labeled alternative in {@link SimpleA3Parser#unsignedConstant}.
     * @param ctx the parse tree
     */
    void enterUnsignedRealConstant(SimpleA3Parser.UnsignedRealConstantContext ctx);

    /**
     * Exit a parse tree produced by the {@code unsignedRealConstant}
     * labeled alternative in {@link SimpleA3Parser#unsignedConstant}.
     * @param ctx the parse tree
     */
    void exitUnsignedRealConstant(SimpleA3Parser.UnsignedRealConstantContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#integerConstant}.
     * @param ctx the parse tree
     */
    void enterIntegerConstant(SimpleA3Parser.IntegerConstantContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#integerConstant}.
     * @param ctx the parse tree
     */
    void exitIntegerConstant(SimpleA3Parser.IntegerConstantContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#realConstant}.
     * @param ctx the parse tree
     */
    void enterRealConstant(SimpleA3Parser.RealConstantContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#realConstant}.
     * @param ctx the parse tree
     */
    void exitRealConstant(SimpleA3Parser.RealConstantContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#characterConstant}.
     * @param ctx the parse tree
     */
    void enterCharacterConstant(SimpleA3Parser.CharacterConstantContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#characterConstant}.
     * @param ctx the parse tree
     */
    void exitCharacterConstant(SimpleA3Parser.CharacterConstantContext ctx);

    /**
     * Enter a parse tree produced by {@link SimpleA3Parser#stringConstant}.
     * @param ctx the parse tree
     */
    void enterStringConstant(SimpleA3Parser.StringConstantContext ctx);

    /**
     * Exit a parse tree produced by {@link SimpleA3Parser#stringConstant}.
     * @param ctx the parse tree
     */
    void exitStringConstant(SimpleA3Parser.StringConstantContext ctx);
}

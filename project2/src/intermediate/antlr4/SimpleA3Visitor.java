// Generated from ./SimpleA3.g4 by ANTLR 4.13.2

package intermediate.antlr4;


import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SimpleA3Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SimpleA3Visitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#program}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProgram(SimpleA3Parser.ProgramContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#programHeader}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProgramHeader(SimpleA3Parser.ProgramHeaderContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#block}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlock(SimpleA3Parser.BlockContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#declarations}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDeclarations(SimpleA3Parser.DeclarationsContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#identifier}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdentifier(SimpleA3Parser.IdentifierContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#statement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatement(SimpleA3Parser.StatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#emptyStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEmptyStatement(SimpleA3Parser.EmptyStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#compoundStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCompoundStatement(SimpleA3Parser.CompoundStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#statementList}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatementList(SimpleA3Parser.StatementListContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#assignmentStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAssignmentStatement(SimpleA3Parser.AssignmentStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#repeatStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRepeatStatement(SimpleA3Parser.RepeatStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#caseStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCaseStatement(SimpleA3Parser.CaseStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#selectBranch}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSelectBranch(SimpleA3Parser.SelectBranchContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#selectConstants}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSelectConstants(SimpleA3Parser.SelectConstantsContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#selectConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSelectConstant(SimpleA3Parser.SelectConstantContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#writeStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWriteStatement(SimpleA3Parser.WriteStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#writelnStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWritelnStatement(SimpleA3Parser.WritelnStatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#writeArguments}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWriteArguments(SimpleA3Parser.WriteArgumentsContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#writeArgument}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWriteArgument(SimpleA3Parser.WriteArgumentContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#format}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFormat(SimpleA3Parser.FormatContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#width}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWidth(SimpleA3Parser.WidthContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#precision}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPrecision(SimpleA3Parser.PrecisionContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#sign}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSign(SimpleA3Parser.SignContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpression(SimpleA3Parser.ExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#simpleExpression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSimpleExpression(SimpleA3Parser.SimpleExpressionContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#term}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTerm(SimpleA3Parser.TermContext ctx);

    /**
     * Visit a parse tree produced by the {@code factorVariable}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactorVariable(SimpleA3Parser.FactorVariableContext ctx);

    /**
     * Visit a parse tree produced by the {@code factorUnsignedConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactorUnsignedConstant(SimpleA3Parser.FactorUnsignedConstantContext ctx);

    /**
     * Visit a parse tree produced by the {@code factorCharacterConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactorCharacterConstant(SimpleA3Parser.FactorCharacterConstantContext ctx);

    /**
     * Visit a parse tree produced by the {@code factorStringConstant}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactorStringConstant(SimpleA3Parser.FactorStringConstantContext ctx);

    /**
     * Visit a parse tree produced by the {@code factorNot}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactorNot(SimpleA3Parser.FactorNotContext ctx);

    /**
     * Visit a parse tree produced by the {@code factorParenthesized}
     * labeled alternative in {@link SimpleA3Parser#factor}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFactorParenthesized(SimpleA3Parser.FactorParenthesizedContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#relOp}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRelOp(SimpleA3Parser.RelOpContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#addOp}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAddOp(SimpleA3Parser.AddOpContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#mulOp}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMulOp(SimpleA3Parser.MulOpContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#variable}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(SimpleA3Parser.VariableContext ctx);

    /**
     * Visit a parse tree produced by the {@code unsignedIntegerConstant}
     * labeled alternative in {@link SimpleA3Parser#unsignedConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnsignedIntegerConstant(SimpleA3Parser.UnsignedIntegerConstantContext ctx);

    /**
     * Visit a parse tree produced by the {@code unsignedRealConstant}
     * labeled alternative in {@link SimpleA3Parser#unsignedConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnsignedRealConstant(SimpleA3Parser.UnsignedRealConstantContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#integerConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIntegerConstant(SimpleA3Parser.IntegerConstantContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#realConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRealConstant(SimpleA3Parser.RealConstantContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#characterConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCharacterConstant(SimpleA3Parser.CharacterConstantContext ctx);

    /**
     * Visit a parse tree produced by {@link SimpleA3Parser#stringConstant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStringConstant(SimpleA3Parser.StringConstantContext ctx);
}

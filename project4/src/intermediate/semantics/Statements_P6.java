package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.type.TypeChecker.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.type.Typespec_P6;

public class Statements_P6 extends Semantics_P6 {
    Object assignmentStatement(AssignmentStatementContext ctx) {
        VariableContext variableCtx = ctx.variable();
        ExpressionContext exprCtx = ctx.expression();

        Typespec_P6 variableTypespec = (Typespec_P6) visit(variableCtx);
        Typespec_P6 exprTypespec = (Typespec_P6) visit(exprCtx);

        if (!assignmentCompatible(variableTypespec, exprTypespec)) {
            error.flag(INCOMPATIBLE_ASSIGNMENT, exprCtx);
        }

        return null;
    }

    Object repeatStatement(RepeatStatementContext ctx) {
        ExpressionContext exprCtx = ctx.expression();
        Typespec_P6 exprTypespec = (Typespec_P6) visit(exprCtx);

        if (!exprTypespec.isBoolean()) {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }

        visit(ctx.statementList());
        return null;
    }
}

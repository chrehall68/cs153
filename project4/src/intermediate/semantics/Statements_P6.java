package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.TypeChecker.*;
import static intermediate.type.Typespec_P6.Form.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec_P6;

import java.util.ArrayList;

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

    Object procedureStatement(ProcedureStatementContext ctx) {
        ProcedureIdentifierContext procIdenCtx = ctx.procedureIdentifier();
        IdentifierContext procIdCtx = procIdenCtx.identifier();
        ArgumentListContext listCtx = ctx.argumentList();
        String name = procIdenCtx.getText().toLowerCase();
        SymtabEntry procEntry = symtabStack.lookup(name);
        boolean badName = false;

        if (procEntry == null) {
            error.flag(UNDECLARED_IDENTIFIER, procIdenCtx);
            badName = true;
        } else if (procEntry.getKind() != PROCEDURE) {
            error.flag(NAME_MUST_BE_PROCEDURE, procIdenCtx);
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
            ArrayList<SymtabEntry> parameters = procEntry.getRoutineParameters();
            checkCallArguments(listCtx, parameters);
        }

        procIdenCtx.entry = procIdCtx.entry = procEntry;

        return null;
    }
}

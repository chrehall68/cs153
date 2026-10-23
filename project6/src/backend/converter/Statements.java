package backend.converter;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.type.Typespec;

public class Statements extends Converter {
    Object compoundStatement(CompoundStatementContext ctx) {
        code.emitStart("{");
        code.indent();
        visit(ctx.statementList());
        code.dedent();
        code.emitLine("}");

        return null;
    }

    Object statementList(StatementListContext ctx) {
        for (StatementContext stmtCtx : ctx.statement()) {
            visit(stmtCtx);
        }

        return null;
    }

    Object assignmentStatement(AssignmentStatementContext ctx) {
        code.emitStart();
        visit(ctx.variable());
        code.emit(" = ");
        visit(ctx.expression());
        code.emitEnd(";");

        return null;
    }

    Object ifStatement(IfStatementContext ctx) {
        TrueStatementContext trueStmtCtx = ctx.trueStatement();
        FalseStatementContext falseStmtCtx = ctx.falseStatement();

        code.emitStart("if (");
        visit(ctx.expression());
        code.emitEnd(")");

        StatementContext stmtCtx = trueStmtCtx.statement();
        boolean isCompound = stmtCtx.compoundStatement() != null;

        if (!isCompound) code.indent();
        visit(trueStmtCtx);
        if (!isCompound) code.dedent();

        if (falseStmtCtx != null) {
            code.emitStart("else ");

            stmtCtx = falseStmtCtx.statement();
            isCompound = stmtCtx.compoundStatement() != null;

            if (!isCompound) code.indent();
            visit(falseStmtCtx);
            if (!isCompound) code.dedent();
        }

        return null;
    }

    Object repeatStatement(RepeatStatementContext ctx) {
        boolean needBraces = ctx.statementList().statement().size() > 1;

        code.emitStart("do");
        if (needBraces) code.emitLine("{");
        code.indent();

        visit(ctx.statementList());

        code.dedent();
        if (needBraces) code.emitLine("}");

        code.emitStart("while (!(");
        visit(ctx.expression());
        code.emitEnd("));");

        return null;
    }

    Object whileStatement(WhileStatementContext ctx) {
        boolean compound = ctx.statement().compoundStatement() != null;

        code.emitStart("while (");
        visit(ctx.expression());
        code.emit(") ");

        if (!compound) code.indent();
        visit(ctx.statement());
        if (!compound) code.dedent();

        return null;
    }

    Object caseStatement(CaseStatementContext ctx) {
        code.emitStart("switch (");
        visit(ctx.expression());
        code.emit(")");
        code.emitLine("{");

        if (ctx.caseBranchList() != null) {
            code.indent();
            visit(ctx.caseBranchList());
            code.dedent();
        }

        code.emitLine("}");
        return null;
    }

    Object caseBranch(CaseBranchContext ctx) {
        CaseConstantListContext listCtx = ctx.caseConstantList();

        if (listCtx != null) {
            // Loop over this branch's constants.
            int i = 0;
            code.emitStart();
            for (CaseConstantContext constCtx : listCtx.caseConstant()) {
                code.emit("case " + constCtx.getText());
                if (++i == 3) {
                    code.emitEnd(":");
                    code.emitStart();
                    i = 0;
                } else code.emit(": ");
            }

            code.indent();
            visit(ctx.statement());
            code.emitLine("break;");
            code.dedent();
        }

        return null;
    }

    Object forStatement(ForStatementContext ctx) {
        VariableContext controlCtx = ctx.variable();
        boolean isCompound = ctx.statement().compoundStatement() != null;
        boolean to = ctx.TO() != null;

        String controlName = controlCtx.entry.getName();

        // Initialize the control variable.
        code.emitStart("for (" + controlName + " = ");
        visit(ctx.expression().get(0));
        code.emit("; ");

        // Test the control variable.
        code.emit(controlName);
        code.emit(to ? " <= " : " >= ");
        visit(ctx.expression().get(1));
        code.emit("; ");

        // Increment or decrement the control variable.
        code.emit(to ? "++" : "--");
        code.emitEnd(controlName + ") ");

        // Statement.
        if (!isCompound) code.indent();
        visit(ctx.statement());
        if (!isCompound) code.dedent();

        return null;
    }

    Object procedureCallStatement(ProcedureCallStatementContext ctx) {
        ProcedureNameContext procNameCtx = ctx.procedureName();
        String procedureName = procNameCtx.getText().toLowerCase();

        code.emitStart(procedureName);
        code.emit("(");
        if (ctx.argumentList() != null) visit(ctx.argumentList());
        code.emitEnd(");");

        return null;
    }

    boolean isWriteln = false;

    Object writeStatement(WriteStatementContext ctx) {
        WriteArgumentsContext writeArgsCtx = ctx.writeArguments();

        code.emitStart("System.out.printf(");
        isWriteln = false;
        visit(writeArgsCtx);

        code.emitEnd(");");
        return null;
    }

    Object writelnStatement(WritelnStatementContext ctx) {
        WriteArgumentsContext writeArgsCtx = ctx.writeArguments();

        if (writeArgsCtx == null) {
            code.emitStart("System.out.println()");
            code.emitEnd(";");
        } else {
            code.emitStart("System.out.printf(");
            isWriteln = true;
            visit(writeArgsCtx);

            code.emitEnd(");");
        }

        return null;
    }

    Object writeArguments(WriteArgumentsContext ctx) {
        StringBuilder formatString = new StringBuilder();
        StringBuilder arguments = new StringBuilder();

        // Loop over the write arguments to build the format string.
        for (WriteArgumentContext writeArgCtx : ctx.writeArgument()) {
            ExpressionContext exprCtx = writeArgCtx.expression();
            FormatContext formatCtx = writeArgCtx.format();

            Typespec typespec = exprCtx.typespec;
            boolean isIntegerType = typespec == Predefined.integerType;
            boolean isRealType = typespec == Predefined.realType;
            boolean isCharType = typespec == Predefined.charType;
            boolean isBooleanType = typespec == Predefined.booleanType;

            String arg = exprCtx.getText();

            // A string or character constant without formatting.
            if ((formatCtx == null)
                    && isSingleton(exprCtx)
                    && ((arg.charAt(0) == '"') || (arg.charAt(0) == '\''))) {
                String format = arg.substring(1, arg.length() - 1);
                formatString.append(format);
            }

            // For any other argument, append a field specifier.
            else {
                formatString.append("%");

                if (formatCtx != null) {
                    WidthContext widthCtx = formatCtx.width();
                    SignContext signCtx = widthCtx.sign();
                    String sign = ((signCtx != null) && (signCtx.getText().equals("-"))) ? "-" : "";
                    formatString.append(sign).append(widthCtx.integerConstant().getText());

                    if (isRealType) {
                        formatString.append(".");

                        PrecisionContext precisionCtx = formatCtx.precision();
                        String precisionText =
                                precisionCtx != null
                                        ? precisionCtx.integerConstant().getText()
                                        : "0";
                        formatString.append(precisionText);
                    }
                }

                String typeFlag =
                        isIntegerType
                                ? "d"
                                : isRealType ? "f" : isBooleanType ? "b" : isCharType ? "c" : "s";
                formatString.append(typeFlag);

                arguments.append(", ").append(arg);
            }
        }

        if (isWriteln) formatString.append("\\n");
        code.emit("\"" + formatString.toString() + "\"");

        // Loop over the write arguments to emit the expressions.
        for (WriteArgumentContext writeArgCtx : ctx.writeArgument()) {
            ExpressionContext exprCtx = writeArgCtx.expression();
            FormatContext formatCtx = writeArgCtx.format();

            String arg = exprCtx.getText();

            // Not a string or character constant without formatting.
            if (!((formatCtx == null)
                    && isSingleton(exprCtx)
                    && ((arg.charAt(0) == '"') || (arg.charAt(0) == '\'')))) {
                code.emit(", ");
                visit(exprCtx);
            }
        }

        return null;
    }

    private boolean isSingleton(ExpressionContext exprCtx) {
        if (exprCtx.simpleExpression().size() > 1) return false;

        SimpleExpressionContext simpExprCtx = exprCtx.simpleExpression(0);
        if (simpExprCtx.term().size() > 1) return false;

        return true;
    }
}

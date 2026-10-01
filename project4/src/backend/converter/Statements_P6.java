package backend.converter;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.type.Typespec_P6;

public class Statements_P6 extends Converter_P6 {
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
            if (stmtCtx.emptyStatement() == null) {
                visit(stmtCtx);
            }
        }

        return null;
    }

    Object assignmentStatement(AssignmentStatementContext ctx) {
        String lhs = (String) visit(ctx.variable());
        String expr = (String) visit(ctx.expression());
        code.emitStart(lhs + " = " + expr);
        code.emitEnd(";");

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
        code.emit((String) visit(ctx.expression()));
        code.emitEnd("));");

        return null;
    }

    Object writeStatement(WriteStatementContext ctx) {
        WriteArgumentsContext writeArgsCtx = ctx.writeArguments();
        visit(writeArgsCtx);

        code.emitStart("System.out.printf(\"");
        code.emit(writeArgsCtx.formatString + "\"");

        if (writeArgsCtx.arguments.length() > 0) {
            code.emit(writeArgsCtx.arguments);
        }

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
            visit(writeArgsCtx);
            code.emit("\"" + writeArgsCtx.formatString + "\\n\"");

            if (writeArgsCtx.arguments.length() > 0) {
                code.emit(writeArgsCtx.arguments);
            }

            code.emitEnd(");");
        }

        return null;
    }

    Object writeArguments(WriteArgumentsContext ctx) {
        StringBuilder formatString = new StringBuilder();
        StringBuilder arguments = new StringBuilder();

        // Loop over the write arguments.
        for (WriteArgumentContext writeArgCtx : ctx.writeArgument()) {
            ExpressionContext exprCtx = writeArgCtx.expression();
            FormatContext formatCtx = writeArgCtx.format();

            Typespec_P6 typespec = exprCtx.typespec;
            boolean isIntegerType = typespec == Predefined.integerType;
            boolean isRealType = typespec == Predefined.realType;
            boolean isCharType = typespec == Predefined.charType;
            boolean isStringType = typespec == Predefined.stringType;
            boolean isBooleanType = typespec == Predefined.booleanType;

            String arg = (String) visit(exprCtx);

            // A string or character without formatting.
            if ((formatCtx == null)
                    && (isCharType || isStringType)
                    && isSingleton(exprCtx)
                    // if it's actually a string or character, it'll start with
                    // either " (string) or ' (character)
                    // otherwise, it's a variable with type string or character
                    // and for that we would need a format string
                    && !arg.isEmpty()
                    && (arg.charAt(0) == '\'' || arg.charAt(0) == '\"')) {
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

        ctx.formatString = formatString.toString();
        ctx.arguments = arguments.toString();

        return null;
    }

    Object procedureCall(ProcedureStatementContext ctx) {
        ProcedureIdentifierContext procNameCtx = ctx.procedureIdentifier();
        String procedureName = procNameCtx.identifier().entry.getName();

        String text = procedureName + "(";

        if (ctx.argumentList() != null) {
            text += (String) visit(ctx.argumentList());
        }

        code.emitLine(text += ");");
        return null;
    }

    private boolean isSingleton(ExpressionContext exprCtx) {
        if (exprCtx.simpleExpression().size() > 1) return false;

        SimpleExpressionContext simpExprCtx = exprCtx.simpleExpression(0);
        if (simpExprCtx.term().size() > 1) return false;

        return true;
    }
}

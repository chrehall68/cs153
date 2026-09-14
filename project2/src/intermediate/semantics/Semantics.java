package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.SimpleA3BaseVisitor;
import intermediate.antlr4.SimpleA3Parser.*;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;

public class Semantics extends SimpleA3BaseVisitor<Object> {
    private Symtab symtab = new Symtab();
    private SemanticErrorHandler error = new SemanticErrorHandler();
    private SymtabEntry programEntry;

    public Symtab getSymtab() {
        return symtab;
    }

    public int getErrorCount() {
        return error.getCount();
    }

    @Override
    public Object visitProgramHeader(ProgramHeaderContext ctx) {
        int lineNumber = ctx.start.getLine();
        IdentifierContext idCtx = ctx.identifier();
        String programName = idCtx.getText();

        programEntry = symtab.enter(programName, PROGRAM);
        programEntry.appendLineNumber(lineNumber);

        idCtx.entry = programEntry;
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) {
        int lineNumber = ctx.start.getLine();
        VariableContext variableCtx = ctx.variable();
        ExpressionContext expressionCtx = ctx.expression();

        // Enter the variable name into the symbol table
        // if it isn't already in there.
        String variableName = variableCtx.getText();
        SymtabEntry variableEntry = symtab.lookup(variableName.toLowerCase());

        if (variableEntry == null) {
            variableEntry = symtab.enter(variableName, VARIABLE);
        }

        variableEntry.appendLineNumber(lineNumber);
        variableCtx.entry = variableEntry;

        visit(expressionCtx);
        return null;
    }

    @Override
    public Object visitVariable(VariableContext ctx) {
        IdentifierContext idCtx = ctx.identifier();

        visit(idCtx);
        ctx.entry = idCtx.entry;

        return null;
    }

    @Override
    public Object visitIdentifier(IdentifierContext ctx) {
        int lineNumber = ctx.start.getLine();
        String idName = ctx.getText();
        SymtabEntry idEntry = symtab.lookup(idName);

        if (idEntry != null) {
            ctx.entry = idEntry;
            idEntry.appendLineNumber(lineNumber);
        } else {
            error.flag(UNDECLARED_IDENTIFIER, ctx);
        }

        return null;
    }

    @Override
    public Object visitIntegerConstant(IntegerConstantContext ctx) {
        ctx.value = Integer.parseInt(ctx.getText());
        return null;
    }

    @Override
    public Object visitRealConstant(RealConstantContext ctx) {
        ctx.value = Double.parseDouble(ctx.getText());
        return null;
    }

    @Override
    public Object visitCharacterConstant(CharacterConstantContext ctx) {
        String str = ctx.getText();

        // Remove single quotes
        // Replace '' with '
        // Replace " with \"
        str = str.substring(1, str.length() - 1).replace("''''", "'\''").replace("\"", "\\\"");

        ctx.value = str.charAt(0);
        return null;
    }

    @Override
    public Object visitStringConstant(StringConstantContext ctx) {
        String str = ctx.getText();

        // Remove single quotes
        // Replace '' with '
        // Replace " with \"
        ctx.value = str.substring(1, str.length() - 1).replace("''", "'").replace("\"", "\\\"");

        return null;
    }
}

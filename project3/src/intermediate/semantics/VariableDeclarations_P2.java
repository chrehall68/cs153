package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec_P2;

public class VariableDeclarations_P2 extends Semantics_P2 {
    Typespec_P2 variableDeclarations(VariableDeclarationsContext ctx) {
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec_P2 typespec = (Typespec_P2) visit(typespecCtx);
        VariableIdentifierListContext varListCtx = ctx.variableIdentifierList();

        typespecCtx.typespec = typespec;

        for (IdentifierContext idCtx : varListCtx.identifier()) {
            int lineNumber = idCtx.getStart().getLine();
            String variableName = idCtx.getText();
            SymtabEntry variableEntry = symtab.lookup(variableName);

            if (variableEntry == null) {
                variableEntry = symtab.enter(variableName, VARIABLE);
                variableEntry.setTypespec(typespec);
                idCtx.entry = variableEntry;
                idCtx.typespec = typespec;
            } else {
                error.flag(REDECLARED_IDENTIFIER, idCtx);
            }

            variableEntry.appendLineNumber(lineNumber);
        }

        return typespec;
    }
}

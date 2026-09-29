package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec_P6;

public class VariableDeclarations_P6 extends Semantics_P6 {
    Typespec_P6 variableDeclarations(VariableDeclarationsContext ctx) {
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec_P6 typespec = (Typespec_P6) visit(typespecCtx);
        VariableIdentifierListContext varListCtx = ctx.variableIdentifierList();

        typespecCtx.typespec = typespec;

        for (IdentifierContext idCtx : varListCtx.identifier()) {
            int lineNumber = idCtx.getStart().getLine();
            String variableName = idCtx.getText();  // canonical name for variables is as written
            SymtabEntry variableEntry = symtabStack.lookupLocal(variableName);

            if (variableEntry == null) {
                variableEntry = symtabStack.enterLocal(variableName, VARIABLE);
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

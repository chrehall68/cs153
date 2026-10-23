package backend.converter;

import static intermediate.type.Typespec.Form.*;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.type.Typespec;
import intermediate.type.Typespec.Form;

public class Variables extends Converter {
    Object variableDeclarations(VariableDeclarationsContext ctx) {
        VariableIdentifierListContext varListCtx = ctx.variableIdentifierList();
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec typespec = typespecCtx.typespec.baseType();
        Form typeForm = typespec.getForm();
        String typeName = javaTypeName(typespec);

        code.emitStart();
        if (programVariables /* && !recordFields */) {
            code.emit("private static ");
        }
        code.emit(typeName);

        String separator = " ";
        for (IdentifierContext idCtx : varListCtx.identifier()) {
            String variableName = idCtx.entry.getName().toLowerCase();
            code.emit(separator + variableName);

            if (typeForm == ARRAY) emitNewArray(typespec, true);
            else if (typeForm == RECORD) emitNewRecord(typespec);

            separator = ", ";
        }

        code.emitEnd(";");
        return null;
    }
}

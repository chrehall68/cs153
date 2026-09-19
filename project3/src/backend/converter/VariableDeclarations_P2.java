package backend.converter;

import static intermediate.type.Typespec_P2.Form.*;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec_P2;
import intermediate.type.Typespec_P2.Form;

public class VariableDeclarations_P2 extends Converter_P2 {
    boolean first = true;

    Object variableDeclarations(VariableDeclarationsContext ctx) {
        VariableIdentifierListContext varListCtx = ctx.variableIdentifierList();
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec_P2 typespec = typespecCtx.typespec;
        Form typeForm = typespec.getForm();
        String typeName = javaTypeName(typespec);

        if (first) {
            code.emitLine();
            first = false;
        }

        code.emitStart();
        code.emit("private static " + typeName);

        String separator = " ";
        for (IdentifierContext idCtx : varListCtx.identifier()) {
            String variableName = idCtx.entry.getName().toLowerCase();
            code.emit(separator + variableName);

            if (typeForm == ARRAY) array(typespecCtx);

            separator = ", ";
        }

        code.emitEnd(";");
        return null;
    }

    private void array(TypeSpecificationContext typespecCtx) {
        String brackets = "";
        Typespec_P2 typespec = typespecCtx.typespec;
        String typeName = javaTypeName(typespec);

        while (typespec.getForm() == ARRAY) {
            brackets += "[]";
            typespec = typespec.getArrayElementType();
        }

        code.emit(brackets);
        code.emit(" = new " + typeName);

        typespec = typespecCtx.typespec;

        while (typespec.getForm() == ARRAY) {
            int elmtCount = typespec.getArrayElementCount();
            code.emit("[" + elmtCount + "]");
            typespec = typespec.getArrayElementType();
        }
    }

    private String javaTypeName(Typespec_P2 pascalType) {
        Form form = pascalType.getForm();
        SymtabEntry typeEntry = pascalType.getIdentifier();
        String pascalTypeName = typeEntry != null ? typeEntry.getName() : null;
        String javaTypeName = null;

        switch (form) {
            case SCALAR:
                return typeNameTable.get(pascalTypeName);

            case ENUMERATED:
                // TODO - this seems wrong. This means that enumerated types that
                // are declared anonymously don't get emitted
                return pascalTypeName != null ? pascalTypeName : "int";

            case SUBRANGE:
                Typespec_P2 baseType = pascalType.baseType();
                pascalTypeName = baseType.getIdentifier().getName();
                javaTypeName = typeNameTable.get(pascalTypeName);
                return javaTypeName != null ? javaTypeName : pascalTypeName;

            case STRING:
                return "String";

            case ARRAY:
                Typespec_P2 elmtType = pascalType.getArrayBaseType();

                if (elmtType.getIdentifier() != null) {
                    if (elmtType.getForm() == STRING) {
                        return "String";
                    }

                    pascalTypeName = elmtType.getIdentifier().getName();
                    javaTypeName = typeNameTable.get(pascalTypeName);

                    return javaTypeName != null ? javaTypeName : pascalTypeName;
                } else {
                    return "int";
                }
            case SET:
                Typespec_P2 setElementType = pascalType.getSetElementType();
                System.out.println("for " + pascalType);
                System.out.println(setElementType);
                String innerTypeName = javaTypeName(setElementType);
                // if (setElementType.getIdentifier() != null){
                //     // named type
                //     // as long as it's not something that's predefined, then this type
                //     // will have already been emitted?
                //     System.out.println("Inner has an identifier" +
                // setElementType.getIdentifier());
                // } else {
                //     System.out.println("Inner HAS NO identifier" +
                // setElementType.getIdentifier());
                //     // the ordinal type was declared right there
                //     // in which case we need to be careful about what type it is
                // }
                return "HashSet<" + innerTypeName + ">";

            default:
                return "*unknown*";
        }
    }
}

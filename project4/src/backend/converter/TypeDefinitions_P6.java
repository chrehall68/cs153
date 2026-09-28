package backend.converter;

import static intermediate.type.Typespec_P6.Form.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.type.Typespec_P6;
import intermediate.type.Typespec_P6.Form;

public class TypeDefinitions_P6 extends Converter_P6 {
    boolean first = true;

    Object typeDefinition(TypeDefinitionContext ctx) {
        IdentifierContext typeIdCtx = ctx.identifier();
        Typespec_P6 typespec = typeIdCtx.typespec;
        Form form = typespec.getForm();

        if (form == ENUMERATED) {
            String typeName = typeIdCtx.entry.getName();
            TypeSpecificationContext typespecCtx = ctx.typeSpecification();

            if (first) {
                code.emitLine();
                first = false;
            }

            code.emitStart();
            code.emit("private static enum " + typeName);

            visit(typespecCtx);
        }
        if (form == RECORD) {
            String typeName = typeIdCtx.entry.getName();
            code.emitStart();
            code.emit("private static class " + typeName + "{");
            code.indent();
            recordFields = true;
            visit(ctx.typeSpecification());
            recordFields = false;
            code.dedent();
            code.emitStart();
            code.emit("}");
        }

        return null;
    }

    Object enumeratedType(EnumeratedTypeContext ctx) {
        String separator = " {";

        for (IdentifierContext constIdCtx : ctx.identifier()) {
            String name = constIdCtx.entry.getName();
            code.emit(separator + name);
            separator = ", ";
        }

        code.emitEnd("};");
        return null;
    }
}

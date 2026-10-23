package backend.converter;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.type.Typespec;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;

public class Types extends Converter
{
    Object typeDefinition(TypeDefinitionContext ctx)
    {
        IdentifierContext typeIdCtx = ctx.identifier();
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        String typeName = typeIdCtx.entry.getName();
        Typespec typespec = typespecCtx.typespec;
        Form form = typespec.getForm();
        
        if (form == ENUMERATED)
        {
            if (programVariables) code.emitStart("private static ");
            else                  code.emitStart();

            code.emit("enum " + typeName);
            visit(typespecCtx);
        }

        return null;
    }
    
    Object enumeratedType(EnumeratedTypeContext ctx)
    {
        String separator = " {";
        
        for (IdentifierContext constIdCtx : ctx.identifier())
        {
            String name = constIdCtx.entry.getName().toUpperCase();
            code.emit(separator + name);
            separator = ", ";
        }

        code.emitEnd("};");
        return null;
    }
    
    Object recordType(RecordTypeContext ctx)
    {
        emitRecordFields(ctx.typespec);        
        return null;
    }
}

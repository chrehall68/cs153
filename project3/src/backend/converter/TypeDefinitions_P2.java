package backend.converter;

import intermediate.antlr4.Pcl_P2Parser.*;

import intermediate.type.Typespec_P2;
import intermediate.type.Typespec_P2.Form;

import static intermediate.type.Typespec_P2.Form.*;

public class TypeDefinitions_P2 extends Converter_P2
{
    boolean first = true;
    
    Object typeDefinition(TypeDefinitionContext ctx) 
    { 
        IdentifierContext typeIdCtx = ctx.identifier();
        Typespec_P2 typespec = typeIdCtx.typespec;
        Form form = typespec.getForm();
        
        if (form == ENUMERATED)
        {
            String typeName = typeIdCtx.entry.getName();
            TypeSpecificationContext typespecCtx = ctx.typeSpecification();
            
            if (first)
            {
                code.emitLine();
                first = false;
            }

            code.emitStart();
            code.emit("private static enum " + typeName);
            
            visit(typespecCtx);
        }
        
        return null;
    }
    
    Object enumeratedType(EnumeratedTypeContext ctx)
    {
        String separator = " {";
        
        for (IdentifierContext constIdCtx : 
                                    ctx.identifier())
        {
            String name = constIdCtx.entry.getName();
            code.emit(separator + name);
            separator = ", ";
        }

        code.emitEnd("};");
        return null;
    }
}

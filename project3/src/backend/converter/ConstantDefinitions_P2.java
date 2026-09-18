package backend.converter;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.type.Typespec_P2;

public class ConstantDefinitions_P2 extends Converter_P2
{
    Object constantDefinition(ConstantDefinitionContext ctx) 
    {
        ConstantContext constantCtx = ctx.constant();
        IdentifierContext idCtx = ctx.identifier();
        String constantName = 
                        idCtx.entry.getName().toUpperCase();
        Typespec_P2 typespec = constantCtx.typespec;
        String pascalTypeName = typespec.getIdentifier().getName();
        String javaTypeName = typeNameTable.get(pascalTypeName);        
        String str = toJavaString(constantCtx.value, 
                                  constantCtx.typespec);
        
        code.emitStart();
        code.emit("private static final ");
        code.emitEnd(javaTypeName + " " + constantName 
                                  + " = " + str + ";");
        
        return null;
    }
    
    private String toJavaString(Object value, Typespec_P2 typespec)
    {
        if (typespec == Predefined.charType)
        {
            return "'" + (Character) value + "'";
        }
        else if (typespec == Predefined.stringType)
        {
            return "\"" + (String) value + "\"";
        }
        else
        {
            return value.toString();
        }
    }
}

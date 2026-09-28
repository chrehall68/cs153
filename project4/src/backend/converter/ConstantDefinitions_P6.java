package backend.converter;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.type.Typespec_P6;

public class ConstantDefinitions_P6 extends Converter_P6
{
    String constantDefinition(ConstantDefinitionContext ctx) 
    {
        ConstantContext constantCtx = ctx.constant();
        IdentifierContext idCtx = ctx.identifier();
        String constantName = 
                        idCtx.entry.getName().toUpperCase();
        Typespec_P6 typespec = constantCtx.typespec;
        String pascalTypeName = typespec.getIdentifier().getName();
        String javaTypeName = typeNameTable.get(pascalTypeName);        
        
        String str = (String) visit(constantCtx);
        
        code.emitStart();
        code.emit("private static final ");
        code.emitEnd(javaTypeName + " " + constantName 
                                  + " = " + str + ";");
        
        return null;
    }
    
    String constantSignedIdentifier(ConstantSignedIdentifierContext ctx)
    {
        String str = ctx.constantIdentifier().identifier().entry.getName();
        
        SignContext signCtx = ctx.sign();
        if ((signCtx != null) && (signCtx.getText().equals("-")))
        {
            str = "-" + str;
        }
        
        return str;
    }
    
    String constantIdentifier(ConstantIdentifierContext ctx)
    {
        IdentifierContext idCtx = ctx.identifier();
        
        if (idCtx.typespec == Predefined.booleanType)
        {
            return ((Integer) idCtx.value) == 1 ? "true" : "false";
        }
        else
        {
            return idCtx.entry.getName();
        }
    }

    String integerConstant(IntegerConstantContext ctx)
    {
        return ctx.value.toString();
    }

    String realConstant(RealConstantContext ctx)
    {
        return ctx.value.toString();
    }

    String characterConstant(CharacterConstantContext ctx) 
    {
        return "'" + ctx.value + "'";
    }

    String stringConstant(StringConstantContext ctx) 
    {
        return "\"" + ctx.value + "\"";
    }

    String booleanConstant(BooleanConstantContext ctx) 
    {
        return String.valueOf(ctx.TRUE() != null);
    }
}

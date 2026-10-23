package intermediate.semantics;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

public class Constants extends Semantics
{
    Typespec constantDefinition(ConstantDefinitionContext ctx) 
    { 
        IdentifierContext idCtx = ctx.identifier();
        String constantName = idCtx.getText();
        SymtabEntry constantEntry = symtabStack.lookupLocal(constantName);
        
        if (constantEntry == null)
        {
            ConstantContext constCtx = ctx.constant();
            Typespec typespec = (Typespec) visit(constCtx);
            Object value = constCtx.value;
            
            constantEntry = 
                    symtabStack.enterLocal(constantName, CONSTANT);
            constantEntry.setValue(value);
            constantEntry.setTypespec(typespec);
            
            idCtx.entry = constantEntry;
            idCtx.value = constCtx.value = value;
            idCtx.typespec = constCtx.typespec = typespec;
        }
        else
        {
            error.flag(REDECLARED_IDENTIFIER, idCtx);
        }

        constantEntry.appendLineNumber(ctx.getStart().getLine());        
        return null;
    }
    
    Typespec constantSignedIdentifier(ConstantSignedIdentifierContext ctx)
    {
        SignContext signCtx = ctx.sign();
        ConstantIdentifierContext ctxConstIdCtx = ctx.constantIdentifier();
        ctx.typespec = ctxConstIdCtx.typespec 
                     = constantIdentifier(ctxConstIdCtx);
        ctx.value = ctxConstIdCtx.value;
        
        if ((signCtx != null) && (signCtx.getText().equals("-")))
        {
            boolean isReal = ctx.typespec == Predefined.realType;
            ctx.value = ctxConstIdCtx.value = 
                    isReal ? -(double) ctx.value : -(int) ctx.value;
        }
        
        return ctx.typespec;
    }
    
    Typespec constantSignedInteger(ConstantSignedIntegerContext ctx)
    {
        SignContext signCtx = ctx.sign();
        IntegerConstantContext integerConstCtx = ctx.integerConstant();
        ctx.typespec = integerConstCtx.typespec 
                     = integerConstant(integerConstCtx);
        ctx.value = integerConstCtx.value;
        
        if ((signCtx != null) && (signCtx.getText().equals("-")))
        {
            ctx.value = integerConstCtx.value = -(int) ctx.value;
        }
        
        return ctx.typespec;
    }
    
    Typespec constantSignedReal(ConstantSignedRealContext ctx)
    {
        SignContext signCtx = ctx.sign();
        RealConstantContext realConstCtx = ctx.realConstant();
        ctx.typespec = realConstCtx.typespec 
                     = realConstant(realConstCtx);
        ctx.value = realConstCtx.value;
        
        if ((signCtx != null) && (signCtx.getText().equals("-")))
        {
            ctx.value = realConstCtx.value = -(double) ctx.value;
        }
        
        return ctx.typespec;
    }
    
    Typespec constantCharacter(ConstantCharacterContext ctx)
    {
        CharacterConstantContext characterConstCtx = ctx.characterConstant();
        ctx.typespec = characterConstCtx.typespec 
                     = characterConstant(characterConstCtx);
        ctx.value = characterConstCtx.value;
        
        return ctx.typespec;
    }
    
    Typespec constantString(ConstantStringContext ctx)
    {
        StringConstantContext stringConstCtx = ctx.stringConstant();
        ctx.typespec = stringConstCtx.typespec 
                     = stringConstant(stringConstCtx);
        ctx.value = stringConstCtx.value;
        
        return ctx.typespec;
    }
     
    Typespec constantBoolean(ConstantBooleanContext ctx)
    {
        BooleanConstantContext booleanConstCtx = ctx.booleanConstant();
        ctx.typespec = booleanConstCtx.typespec 
                     = booleanConstant(booleanConstCtx);
        ctx.value = booleanConstCtx.value;
        
        return ctx.typespec;
    }
    
    Typespec constantIdentifier(ConstantIdentifierContext ctx)
    {
        IdentifierContext idCtx = ctx.identifier();
        String idName = idCtx.getText();
        SymtabEntry idEntry = symtabStack.lookup(idName);
        int lineNumber = idCtx.start.getLine();
        
        if (idEntry != null)
        {
            Kind kind = idEntry.getKind();
            if (   (kind == CONSTANT) 
                || (kind == ENUMERATED_CONSTANT))
            {
                idCtx.entry = idEntry;
                ctx.typespec = idCtx.typespec = idEntry.getTypespec();
                ctx.value = idCtx.value = idEntry.getValue();                
            }
            else 
            {
                error.flag(INVALID_CONSTANT, idCtx);
                ctx.typespec = idCtx.typespec = Predefined.undefinedType;
                ctx.value = idCtx.value = 0;
            }
            
            idEntry.appendLineNumber(lineNumber);
        }
        else
        {
            error.flag(UNDECLARED_IDENTIFIER, idCtx);
            
            SymtabEntry unknownEntry = 
                    symtabStack.enterLocal(idName, CONSTANT);
            unknownEntry.setValue(0);
            unknownEntry.setTypespec(Predefined.undefinedType);
            unknownEntry.appendLineNumber(lineNumber);
            
            ctx.value = idCtx.typespec = Predefined.undefinedType;
            ctx.value = idCtx.value = 0;
        }
        
        return idCtx.typespec;
    }
    
    Typespec integerConstant(IntegerConstantContext ctx)
    {
        ctx.value = Integer.parseInt(ctx.getText());
        ctx.typespec = Predefined.integerType;
        
        return ctx.typespec;
    }
    
    
    Typespec realConstant(RealConstantContext ctx)
    {
        ctx.value = Double.parseDouble(ctx.getText());
        ctx.typespec = Predefined.realType;
        
        return ctx.typespec;
    }
    
    Typespec characterConstant(CharacterConstantContext ctx)
    {
        String str = ctx.getText();
        
        str = str.substring(1, str.length() - 1) 
                 .replace("''''", "'\''")
                 .replace("\"", "\\\"");
        
        ctx.value = str.charAt(0); 
        ctx.typespec = Predefined.charType;
        
        return ctx.typespec;
    }
    
    Typespec stringConstant(StringConstantContext ctx)
    {
        String str = ctx.getText();
        
        ctx.value = str.substring(1, str.length() - 1) 
                       .replace("''", "'")
                       .replace("\"", "\\\"");
        ctx.typespec = Predefined.stringType;
        
        return ctx.typespec;
    }
    
    Typespec booleanConstant(BooleanConstantContext ctx)
    {
        ctx.value = ctx.TRUE() != null ? 1 : 0;
        ctx.typespec = Predefined.booleanType;
        
        return ctx.typespec;
    }
}

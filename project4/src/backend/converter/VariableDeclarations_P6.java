package backend.converter;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.type.Typespec_P6;
import intermediate.type.Typespec_P6.Form;

import static intermediate.type.Typespec_P6.Form.*;

public class VariableDeclarations_P6 extends Converter_P6
{
    boolean first = true;
    
    Object variableDeclarations(VariableDeclarationsContext ctx)
    {
        VariableIdentifierListContext varListCtx = 
                                        ctx.variableIdentifierList();   
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec_P6 typespec = typespecCtx.typespec;
        Form typeForm = typespec.getForm();
        String typeName = javaTypeName(typespec);
                
        if (first)
        {
            code.emitLine();
            first = false;
        }
        
        code.emitStart();
        if (programVariables && !recordFields) 
        {
            code.emit("private static ");
        }
        code.emit(typeName);
        
        String separator = " ";
        for (IdentifierContext idCtx : 
                                varListCtx.identifier())
        {
            String variableName = 
                        idCtx.entry.getName().toLowerCase();
            code.emit(separator + variableName);
            
            if (typeForm == ARRAY) array(typespecCtx);

            separator = ", ";
        }

        code.emitEnd(";");
        return null;
    }
    
    private void array(TypeSpecificationContext typespecCtx)
    {
        Typespec_P6 typespec = typespecCtx.typespec;
        String typeName = javaTypeName(typespec);
        
        emitArraySpecifier(typespec);
        code.emit(" = new " + typeName);
        
        typespec = typespecCtx.typespec;
        
        while (typespec.getForm() == ARRAY)
        {
            int elmtCount = typespec.getArrayElementCount();
            code.emit("[" + elmtCount + "]");
            typespec = typespec.getArrayElementType();
        }
    }
}

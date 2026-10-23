package intermediate.semantics;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

public class Variables extends Semantics
{
    Typespec variableDeclarations(VariableDeclarationsContext ctx) 
    { 
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec typespec = (Typespec) visit(typespecCtx);      
        VariableIdentifierListContext varListCtx =
                                    ctx.variableIdentifierList();
        
        typespecCtx.typespec = typespec;
        
        for (IdentifierContext idCtx : 
                                varListCtx.identifier())
        {
            int lineNumber = idCtx.getStart().getLine();        
            String variableName = idCtx.getText();
            SymtabEntry variableEntry = symtabStack.lookupLocal(variableName);
            
            if (variableEntry == null)
            {
                variableEntry = symtabStack.enterLocal(variableName, VARIABLE);
                variableEntry.setTypespec(typespec);  
                
                // Assign a slot number to a local variable.
                Symtab symtab = variableEntry.getSymtab();
                if (symtab.getNestingLevel() > 1)
                {
                    variableEntry.setSlotNumber(symtab.getNextSlotNumber());
                    
                    if (variableEntry.getTypespec() == Predefined.realType)
                    {
                        symtab.getNextSlotNumber();
                    }
                }
                
                idCtx.entry = variableEntry;
                idCtx.typespec = typespec;
            }
            else
            {
                error.flag(REDECLARED_IDENTIFIER, idCtx);
            }
            
            variableEntry.appendLineNumber(lineNumber);        
        }
        
        return typespec;
    }
}

package intermediate.semantics;

import java.util.ArrayList;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;

import static backend.BackendMode.Mode.EXECUTOR;
import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.symtab.SymtabEntry.Routine.*;
import static intermediate.type.Typespec.Form.*;

public class ProcFuncs extends Semantics
{
    @SuppressWarnings("unchecked")
    Object procFuncDefinition(ProcFuncDefinitionContext ctx)
    {
        FunctionHeadContext funcCtx = ctx.functionHead();
        ProcedureHeadContext procCtx = ctx.procedureHead();
        IdentifierContext idCtx = null;
        ParametersContext parameters = null;
        boolean functionDefinition = funcCtx != null;
        Typespec returnTypespec = null;
        String routineName;

        if (functionDefinition)
        {
            idCtx = funcCtx.identifier();
            parameters = funcCtx.parameters();
        } 
        else
        {
            idCtx = procCtx.identifier();
            parameters = procCtx.parameters();
        }

        routineName = idCtx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry routineEntry = symtabStack.lookupLocal(routineName);

        if (routineEntry != null)
        {
            error.flag(REDECLARED_IDENTIFIER, 
                       ctx.getStart().getLine(), routineName);
            return null;
        }

        routineEntry = symtabStack.enterLocal(
                routineName, functionDefinition ? FUNCTION : PROCEDURE);
        routineEntry.setRoutineCode(DECLARED);
        idCtx.entry = routineEntry;

        // Append to the parent routine's list of subroutines.
        SymtabEntry parentEntry = symtabStack.getLocalSymtab().getOwner();
        parentEntry.appendSubroutine(routineEntry);

        routineEntry.setRoutineSymtab(symtabStack.push());
        idCtx.entry = routineEntry;

        Symtab symtab = symtabStack.getLocalSymtab();
        symtab.setOwner(routineEntry);

        if (parameters != null)
        {
            ArrayList<SymtabEntry> parameterEntries = 
                    (ArrayList<SymtabEntry>) visit(parameters.parameterDeclarationsList());
            routineEntry.setRoutineParameters(parameterEntries);

            for (SymtabEntry parmEntry : parameterEntries)
            {
                parmEntry.setSlotNumber(symtab.getNextSlotNumber());

                if (parmEntry.getTypespec() == Predefined.realType)
                {
                    symtab.getNextSlotNumber();
                }
            }
        }

        if (functionDefinition)
        {
            TypeIdentifierContext typeIdCtx = funcCtx.typeIdentifier();
            visit(typeIdCtx);
            returnTypespec = typeIdCtx.identifier().typespec;

            if (returnTypespec.getForm() != SCALAR)
            {
                error.flag(INVALID_RETURN_TYPE, typeIdCtx);
                returnTypespec = Predefined.integerType;
            }

            routineEntry.setTypespec(returnTypespec);
            idCtx.typespec = returnTypespec;
        } 
        else
        {
            idCtx.typespec = null;
        }

        visit(ctx.block().declarations());

        // Enter the function's associated variable into its symbol table.
        if (functionDefinition)
        {
            SymtabEntry assocVarEntry = symtabStack.enterLocal(routineName, VARIABLE);
            assocVarEntry.setSlotNumber(symtab.getNextSlotNumber());
            assocVarEntry.setTypespec(returnTypespec);
            
            if (returnTypespec == Predefined.realType)
            {
                symtab.getNextSlotNumber();
            }
        }

        visit(ctx.block().compoundStatement());
        routineEntry.setExecutable(ctx.block().compoundStatement());

        symtabStack.pop();
        return null;
    }

    @SuppressWarnings("unchecked")
    Object parameterDeclarationsList(ParameterDeclarationsListContext ctx)
    {
        ArrayList<SymtabEntry> parameterList = new ArrayList<>();

        // Loop over the parameter declarations.
        for (ParameterDeclarationsContext dclCtx : 
                                        ctx.parameterDeclarations())
        {
            ArrayList<SymtabEntry> parameterSublist = 
                            (ArrayList<SymtabEntry>) visit(dclCtx);
            parameterList.addAll(parameterSublist);
        }

        return parameterList;
    }

    Object parameterDeclarations(ParameterDeclarationsContext ctx)
    {
        Kind kind = ctx.VAR() != null ? REFERENCE_PARAMETER 
                                      : VALUE_PARAMETER;
        TypeIdentifierContext typeIdCtx = ctx.typeIdentifier();

        Typespec parmTypespec = (Typespec) visit(typeIdCtx);
//        Typespec parmTypespec = typeIdCtx.identifier().typespec;

        ArrayList<SymtabEntry> parameterSublist = new ArrayList<>();

        // Loop over the parameter identifiers.
        ParameterIdentifierListContext parmListCtx = ctx.parameterIdentifierList();
        for (IdentifierContext parmIdCtx : parmListCtx.identifier())
        {
            int lineNumber = parmIdCtx.getStart().getLine();
            String parmName = parmIdCtx.IDENTIFIER().getText().toLowerCase();
            SymtabEntry parmEntry = symtabStack.lookupLocal(parmName);

            if (parmEntry == null)
            {
                parmEntry = symtabStack.enterLocal(parmName, kind);
                parmEntry.setTypespec(parmTypespec);

                if (   (kind == REFERENCE_PARAMETER) 
                    && (mode != EXECUTOR) 
                    && (parmTypespec.getForm() == SCALAR))
                {
                    error.flag(INVALID_REFERENCE_PARAMETER, parmIdCtx);
                }
            } 
            else
            {
                error.flag(REDECLARED_IDENTIFIER, parmIdCtx);
            }

            parmIdCtx.entry = parmEntry;
            parmIdCtx.typespec = parmTypespec;

            parameterSublist.add(parmEntry);
            parmEntry.appendLineNumber(lineNumber);
        }

        return parameterSublist;
    }
}

package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.symtab.SymtabEntry.Routine.DECLARED;
import static intermediate.type.Typespec_P6.Form.SCALAR;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec_P6;
import intermediate.util.CrossReferencer;

import java.util.ArrayList;

public class Program_P6 extends Semantics_P6 {
    Object program(ProgramContext ctx) {
        visit(ctx.programHeader());
        visit(ctx.block().declarations());
        visit(ctx.block().compoundStatement());

        // Print the cross-reference table.
        CrossReferencer crossReferencer = new CrossReferencer();
        crossReferencer.print(symtabStack);

        return null;
    }

    Object programHeader(ProgramHeaderContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String programName = idCtx.getText();

        programEntry = symtabStack.enterLocal(programName, PROGRAM);
        programEntry.setRoutineSymtab(symtabStack.push());
        programEntry.appendLineNumber(ctx.getStart().getLine());

        symtabStack.setProgramEntry(programEntry);
        symtabStack.getLocalSymtab().setOwner(programEntry);

        idCtx.entry = programEntry;
        return null;
    }

    @SuppressWarnings("unchecked")
    Object procFuncDefinition(ProcFuncDefinitionContext ctx) {
        FunctionHeadContext funcCtx = ctx.functionHead();
        ProcedureHeadContext procCtx = ctx.procedureHead();
        IdentifierContext idCtx = null;
        ParametersContext parameters = null;
        boolean functionDefinition = funcCtx != null;
        Typespec_P6 returnType = null;
        String routineName;

        if (functionDefinition) {
            idCtx = funcCtx.identifier();
            parameters = funcCtx.parameters();
        } else {
            idCtx = procCtx.identifier();
            parameters = procCtx.parameters();
        }

        routineName = idCtx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry routineEntry = symtabStack.lookupLocal(routineName);

        if (routineEntry != null) {
            error.flag(REDECLARED_IDENTIFIER, ctx.getStart().getLine(), routineName);
            return null;
        }

        routineEntry =
                symtabStack.enterLocal(routineName, functionDefinition ? FUNCTION : PROCEDURE);
        routineEntry.setRoutineCode(DECLARED);
        idCtx.entry = routineEntry;

        // Append to the parent routine's list of subroutines.
        SymtabEntry parentEntry = symtabStack.getLocalSymtab().getOwner();
        parentEntry.appendSubroutine(routineEntry);

        routineEntry.setRoutineSymtab(symtabStack.push());
        idCtx.entry = routineEntry;

        Symtab symtab = symtabStack.getLocalSymtab();
        symtab.setOwner(routineEntry);

        if (parameters != null) {
            ArrayList<SymtabEntry> parameterEntries =
                    (ArrayList<SymtabEntry>) visit(parameters.parameterDeclarationsList());
            routineEntry.setRoutineParameters(parameterEntries);
        }

        if (functionDefinition) {
            TypeIdentifierContext typeIdCtx = funcCtx.typeIdentifier();
            returnType = (Typespec_P6) visit(typeIdCtx);

            if (returnType.getForm() != SCALAR) {
                error.flag(INVALID_RETURN_TYPE, typeIdCtx);
                returnType = Predefined.integerType;
            }

            routineEntry.setTypespec(returnType);
            idCtx.typespec = returnType;
        } else {
            idCtx.typespec = null;
        }

        visit(ctx.block().declarations());

        // Enter the function's associated variable into its symbol table.
        if (functionDefinition) {
            SymtabEntry assocVarEntry = symtabStack.enterLocal(routineName, VARIABLE);
            assocVarEntry.setTypespec(returnType);
        }

        visit(ctx.block().compoundStatement());
        routineEntry.setExecutable(ctx.block().compoundStatement());

        symtabStack.pop();
        return null;
    }

    Object parameterDeclarationsList(ParameterDeclarationsListContext ctx) {
        ArrayList<SymtabEntry> parmList = new ArrayList<>();

        // Loop over the parameter declarations.
        for (ParameterDeclarationsContext parmDclCtx : ctx.parameterDeclarations()) {
            ArrayList<SymtabEntry> parameterSublist = parameterDeclarations(parmDclCtx);
            parmList.addAll(parameterSublist);
        }

        return parmList;
    }

    ArrayList<SymtabEntry> parameterDeclarations(ParameterDeclarationsContext parmDclCtx) {
        Kind kind = parmDclCtx.VAR() != null ? REFERENCE_PARAMETER : VALUE_PARAMETER;

        ParameterIdentifierListContext parmIdListCtx = parmDclCtx.parameterIdentifierList();
        TypeIdentifierContext typeIdCtx = parmDclCtx.typeIdentifier();
        Typespec_P6 parmTypespec = (Typespec_P6) visit(typeIdCtx);

        ArrayList<SymtabEntry> parmSublist = new ArrayList<>();

        for (IdentifierContext parmIdCtx : parmIdListCtx.identifier()) {
            SymtabEntry parmEntry = parameterIdentifier(parmIdCtx, parmTypespec, kind);
            parmSublist.add(parmEntry);
        }

        //        IdentifierContext typeIdCtx = ctx.identifier();
        //
        //        visit(typeIdCtx);
        //        Typespec_P6 parmTypespec = typeIdCtx.typespec;
        //
        //        ArrayList<SymtabEntry> parameterSublist = new ArrayList<>();
        //
        //        // Loop over the parameter identifiers.
        //        ParameterIdentifierListContext parmListCtx =
        //                                                ctx.parameterIdentifierList();
        //        for (ParameterIdentifierContext parmIdCtx :
        //                                            parmListCtx.parameterIdentifier())
        //        {
        //            int lineNumber = parmIdCtx.getStart().getLine();
        //            String parmName = parmIdCtx.IDENTIFIER().getText().toLowerCase();
        //            SymtabEntry parmEntry = symtabStack.lookupLocal(parmName);
        //
        //            if (parmEntry == null)
        //            {
        //                parmEntry = symtabStack.enterLocal(parmName, kind);
        //                parmEntry.setTypespec(parmTypespec);
        //
        //                if (   (kind == REFERENCE_PARAMETER)
        //                    && (parmTypespec.getForm() == SCALAR))
        //                {
        //                    error.flag(INVALID_REFERENCE_PARAMETER, parmIdCtx);
        //                }
        //            }
        //            else
        //            {
        //                error.flag(REDECLARED_IDENTIFIER, parmIdCtx);
        //            }
        //
        //            parmIdCtx.entry = parmEntry;
        //            parmIdCtx.type  = parmTypespec;
        //
        //            parameterSublist.add(parmEntry);
        //            parmEntry.appendLineNumber(lineNumber);
        //        }

        return parmSublist;
    }

    private SymtabEntry parameterIdentifier(
            IdentifierContext parmIdCtx, Typespec_P6 parmTypespec, Kind kind) {
        int lineNumber = parmIdCtx.getStart().getLine();
        String parmName = parmIdCtx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry parmEntry = symtabStack.lookupLocal(parmName);

        if (parmEntry == null) {
            parmEntry = symtabStack.enterLocal(parmName, kind);
            parmEntry.setTypespec(parmTypespec);

            if ((kind == REFERENCE_PARAMETER) && (parmTypespec.getForm() == SCALAR)) {
                error.flag(INVALID_REFERENCE_PARAMETER, parmIdCtx);
            }
        } else {
            error.flag(REDECLARED_IDENTIFIER, parmIdCtx);
        }

        parmIdCtx.entry = parmEntry;
        parmIdCtx.typespec = parmTypespec;

        parmEntry.appendLineNumber(lineNumber);
        return parmEntry;
    }
}

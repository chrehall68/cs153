package backend.compiler;

import static backend.compiler.Directive.*;
import static backend.compiler.Instruction.*;

import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;

import java.util.ArrayList;
import java.util.Map;

public class ProcFuncs extends Compiler {
    /**
     * Emit code for declared procedures and functions.
     */
    Object procedureAndFunctionsPart(ProcedureAndFunctionsPartContext ctx) {
        for (ProcFuncDefinitionContext defnCtx : ctx.procFuncDefinition()) {
            SymtabEntry routineEntry =
                    defnCtx.procedureHead() != null
                            ? defnCtx.procedureHead().identifier().entry
                            : defnCtx.functionHead().identifier().entry;
            emitRoutine(routineEntry);
        }

        return null;
    }

    /**
     * Emit code for a declared procedure or function.
     * @param routineEntry the symbol table entry of the routine's name.
     */
    private void emitRoutine(SymtabEntry routineEntry) {
        LocalStack localStack = new LocalStack();
        code.setLocalStack(localStack);

        emitHeader(routineEntry);
        emitLocalVariables(routineEntry);

        // Emit code for the compound statement.
        CompoundStatementContext stmtCtx = (CompoundStatementContext) routineEntry.getExecutable();
        visit(stmtCtx);

        emitReturn(routineEntry);
        emitEpilogue();
    }

    /**
     * Emit the routine header.
     * @param routineEntry the symbol table entry of the routine's name.
     */
    private void emitHeader(SymtabEntry routineEntry) {
        String routineName = routineEntry.getName();
        ArrayList<SymtabEntry> parmEntries = routineEntry.getRoutineParameters();
        StringBuilder buffer = new StringBuilder();

        // Procedure or function name.
        buffer.append(routineName);
        buffer.append("(");

        // Parameter and return type descriptors.
        if (parmEntries != null) {
            for (SymtabEntry parmEntry : parmEntries) {
                buffer.append(code.typeDescriptor(parmEntry));
            }
        }
        buffer.append(")");
        buffer.append(code.typeDescriptor(routineEntry));

        code.emitLine();
        if (routineEntry.getKind() == PROCEDURE) {
            code.emitComment("PROCEDURE " + routineName);
        } else {
            code.emitComment("FUNCTION " + routineName);
        }

        code.emitDirective(METHOD_PRIVATE_STATIC, buffer.toString());
    }

    /**
     * Emit directives for the local variables.
     * @param routineEntry the symbol table entry of the routine's name.
     */
    private void emitLocalVariables(SymtabEntry routineEntry) {
        int size = 0;
        Symtab symtab = routineEntry.getRoutineSymtab();

        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet()) {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();

            size++;
            if (entry.getTypespec() == Predefined.realType) size++;
        }

        LocalVariables localVariables = new LocalVariables(size);
        code.setLocalVariables(localVariables);

        code.emitLine();

        // Loop over all the routine's identifiers and
        // emit a .var directive for each variable and formal parameter.
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet()) {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            Kind kind = entry.getKind();

            if ((kind == VARIABLE) || (kind == VALUE_PARAMETER) || (kind == REFERENCE_PARAMETER)) {
                int slot = entry.getSlotNumber();
                code.emitDirective(
                        VAR, slot + " is " + entry.getName(), code.typeDescriptor(entry));
            }
        }
    }

    /**
     * Emit the routine's return code.
     * @param routineEntry the symbol table entry of the routine's name.
     */
    private void emitReturn(SymtabEntry routineEntry) {
        code.emitLine();

        // Function: Return the value in the implied function variable.
        if (routineEntry.getKind() == FUNCTION) {
            Typespec type = routineEntry.getTypespec();

            // Get the slot number of the function variable.
            String varName = routineEntry.getName();
            SymtabEntry varEntry = routineEntry.getRoutineSymtab().lookup(varName);
            code.emitLoadLocal(type, varEntry.getSlotNumber());
            code.emitReturnValue(type);
        }

        // Procedure: Just return.
        else code.emit(RETURN);
    }

    /**
     * Emit the routine's epilogue.
     */
    private void emitEpilogue() {
        LocalVariables localVariables = code.getLocalVariables();
        LocalStack localStack = code.getLocalStack();

        code.emitLine();
        code.emitDirective(LIMIT_LOCALS, localVariables.count());
        code.emitDirective(LIMIT_STACK, localStack.capacity());
        code.emitDirective(END_METHOD);
    }
}

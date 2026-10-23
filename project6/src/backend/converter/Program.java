package backend.converter;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Symtab;
import intermediate.type.Typespec;

import static intermediate.type.Typespec.Form.*;

public class Program extends Converter
{
    Object program(ProgramContext ctx)
    {
        code.emitLine("public class " + programName);
        code.emitLine("{");
        code.indent();
        
        IdentifierContext programIdCtx = ctx.programHeader().identifier();
        Symtab programSymtab = programIdCtx.entry.getRoutineSymtab();
        
        emitRecordDefinitions(programSymtab);
        visit(ctx.block().declarations()); 
        
        // Main.
        code.emitLine();
        code.emitLine("public static void main(String[] args)");        
        code.emitLine("{");
        code.indent();
        
        // Execution timer.
        code.emitLine("java.time.Instant _start = java.time.Instant.now();");
        
        // Allocate structured data.
        emitAllocateArray(programSymtab);
        code.emitLine();
        
        // Main compound statement.
        visit(ctx.block().compoundStatement().statementList());
        
        // Print the execution time.
        code.emitLine();
        code.emitLine("java.time.Instant _end = java.time.Instant.now();");
        code.emitLine("long _elapsed = java.time.Duration." +
                      "between(_start, _end).toMillis();");
        code.emitLine("System.out.printf(\"\\n[%,d milliseconds execution time.]" +
                                         "\\n\", _elapsed);");
         
        code.dedent();
        code.emitLine("}");

        code.dedent();
        code.emitLine("}");

        code.close();
        return null;
    }
    
    Object procFuncDefinition(ProcFuncDefinitionContext ctx)
    {
        FunctionHeadContext funcHeadCtx = ctx.functionHead();
        ProcedureHeadContext procHeadCtx = ctx.procedureHead();
        IdentifierContext idCtx = null;
        ParametersContext parmsCtx = null;
        boolean functionDefinition = funcHeadCtx != null;
        String routineName;

        programVariables = false;
        code.emitLine();
        code.emitStart("static ");

        if (functionDefinition)
        {
            idCtx = funcHeadCtx.identifier();
            parmsCtx = funcHeadCtx.parameters();
            visit(funcHeadCtx.typeIdentifier());
        } 
        else
        {
            idCtx = procHeadCtx.identifier();
            parmsCtx = procHeadCtx.parameters();
            code.emit("void");
        }

        routineName = idCtx.entry.getName();
        code.emit(" " + routineName);

        code.emit("(");
        if (parmsCtx != null) visit(parmsCtx);
        code.emitEnd(")");
        code.emitLine("{");
        code.indent();

        if (functionDefinition)
        {
            // Function associated variable.
            code.emitStart();
            visit(funcHeadCtx.typeIdentifier());
            code.emitEnd(" " + routineName + ";");
        }

        Symtab routineSymtab = idCtx.entry.getRoutineSymtab();
        emitRecordDefinitions(routineSymtab);
        visit(ctx.block().declarations());

        // Allocate structured data.
        emitAllocateArray(routineSymtab);
        code.emitLine();

        visit(ctx.block().compoundStatement().statementList());

        if (functionDefinition)
        {
            // Return function value.
            code.emitLine();
            code.emitLine("return " + routineName + ";");
        }

        code.dedent();
        code.emitLine("}");

        return null;
    }
    
    Object parameterDeclarations(ParameterDeclarationsContext ctx) 
    {
        ParameterIdentifierListContext parmListCtx = 
                                                ctx.parameterIdentifierList();
        TypeIdentifierContext typeIdCtx = ctx.typeIdentifier();
        Typespec parmTypespec = typeIdCtx.identifier().typespec;
        
        // Loop over the parameters.
        for (IdentifierContext parmIdCtx : parmListCtx.identifier())
        {
            code.emit(currentSeparator);
            
            visit(typeIdCtx);
            code.emit(" " + parmIdCtx.entry.getName());
            
            if (parmTypespec.getForm() == ARRAY) 
            {
                emitArrayBrackets(parmTypespec);
            }
            currentSeparator = ", ";
        }
        
        return null;
    }
}

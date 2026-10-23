package backend.compiler;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;

import static backend.compiler.Directive.*;
import static backend.compiler.Instruction.*;
import static intermediate.semantics.Semantics.programEntry;
import static intermediate.symtab.SymtabEntry.Kind.*;

import java.util.Map;

public class Program extends Compiler
{
    Object program(ProgramContext ctx)
    {
        code.emitDirective(CLASS_PUBLIC, programEntry.getName());
        code.emitDirective(SUPER, "java/lang/Object");

        emitProgramVariables();
        emitInputScanner();
        emitConstructor();   
        
        ProcedureAndFunctionsPartContext procedureAndFunctionsPart =
                ctx.block().declarations().procedureAndFunctionsPart();
        if (procedureAndFunctionsPart != null)
        {
            visit(procedureAndFunctionsPart);
        }
        
        emitMainMethod(ctx);
        
        code.closeObjectFile();        
        return null;
    }
    
    /**
     * Emit field directives for the program variables.
     */
    private void emitProgramVariables()
    {
        code.emitLine();
        code.emitDirective(FIELD_PRIVATE_STATIC, 
                              "_sysin", "Ljava/util/Scanner;");

        // Loop over all the program's identifiers and
        // emit a .field directive for each variable.
        Symtab symtab = programEntry.getRoutineSymtab();
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet())
        {            
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            
            if (entry.getKind() == VARIABLE) 
            {
                code.emitDirective(FIELD_PRIVATE_STATIC, entry.getName(),
                                      code.typeDescriptor(entry));
            }
        }
    }
    
    /**
     * Emit code for the runtime input scanner.
     */
    private void emitInputScanner()
    {
        LocalStack localStack = new LocalStack();
        code.setLocalStack(localStack);
        
        code.emitLine();
        code.emitComment("Runtime input scanner");
        code.emitDirective(METHOD_STATIC, "<clinit>()V");
        code.emitLine();
        
        code.emit(NEW, "java/util/Scanner");
        code.emit(DUP);
        code.emit(GETSTATIC, 
                  "java/lang/System/in Ljava/io/InputStream;");
        code.emit(INVOKESPECIAL, 
                  "java/util/Scanner/<init>(Ljava/io/InputStream;)V");
        code. emit(PUTSTATIC, 
                   code.getProgramName() + "/_sysin Ljava/util/Scanner;");
        code.emit(RETURN);
        
        code.emitLine();
        code.emitDirective(LIMIT_LOCALS, 0);
        code.emitDirective(LIMIT_STACK,  3);
        code.emitDirective(END_METHOD);
    }

    /**
     * Emit code for the main program constructor.
     * @param emitter the code emitter to use
     */
    private void emitConstructor()
    {
        LocalStack localStack = new LocalStack();
        code.setLocalStack(localStack);
        
        code.emitLine();
        code.emitComment("Main class constructor");
        code.emitDirective(METHOD_PUBLIC, "<init>()V");        
        code.emitDirective(VAR, 
                "0 is this L" + code.getProgramName() + ";");
        code.emitLine();

        code.emit(ALOAD_0);
        code.emit(INVOKESPECIAL, "java/lang/Object/<init>()V");
        code.emit(RETURN);

        code.emitLine();
        code.emitDirective(LIMIT_LOCALS, 1);
        code.emitDirective(LIMIT_STACK,  1);
        code.emitDirective(END_METHOD);
    }

    /**
     * Emit code for the program body as the main method.
     * @param ctx the ProgramContext.
     */
    private void emitMainMethod(ProgramContext ctx)
    {
        LocalStack localStack = new LocalStack();
        LocalVariables localVariables = new LocalVariables(5);
        
        code.setLocalVariables(localVariables);
        code.setLocalStack(localStack);
        
        code.emitLine();
        code.emitComment("MAIN");
        code.emitDirective(METHOD_PUBLIC_STATIC, 
                              "main([Ljava/lang/String;)V");

        emitMainPrologue(programEntry);

        // Emit code for the compound statement.
        code.emitLine();
        visit(ctx.block().compoundStatement());
        
        emitMainEpilogue();
    }

    /**
     * Emit the main method prologue.
     * @parm programEntry the symbol table entry for the program name.
     */
    private void emitMainPrologue(SymtabEntry programEntry)
    {
        LocalStack localStack = code.getLocalStack();

        code.emitDirective(VAR, "0 is args [Ljava/lang/String;");
        code.emitDirective(VAR, "1 is _start Ljava/time/Instant;");
        code.emitDirective(VAR, "2 is _end Ljava/time/Instant;");
        code.emitDirective(VAR, "3 is _elapsed J");
        
        // Runtime timer.
        code.emitLine();
        code.emit(INVOKESTATIC, "java/time/Instant/now()Ljava/time/Instant;");
        localStack.increase(1);
        code.emit(ASTORE_1);
    }

    /**
     * Emit the main method epilogue.
     */
    private void emitMainEpilogue()
    {
        LocalVariables localVariables = code.getLocalVariables();
        LocalStack localStack = code.getLocalStack();
    
        // Print the execution time.
        code.emitLine();
        code.emit(INVOKESTATIC, "java/time/Instant/now()Ljava/time/Instant;");
        localStack.increase(1);
        code.emit(ASTORE_2);           
        code.emit(ALOAD_1);             
        code.emit(ALOAD_2);             
        code.emit(INVOKESTATIC, 
                    "java/time/Duration/between(Ljava/time/temporal/Temporal;" +
                    "Ljava/time/temporal/Temporal;)Ljava/time/Duration;");
        localStack.decrease(1);
        code.emit(INVOKEVIRTUAL, "java/time/Duration/toMillis()J");
        localStack.increase(1);
        code. emit(LSTORE_3);   
        
        code.emit(GETSTATIC, "java/lang/System/out Ljava/io/PrintStream;");
        code.emit(LDC, "\"\\n[%,d milliseconds execution time.]\\n\"");
        code.emit(ICONST_1);             
        code.emit(ANEWARRAY, "java/lang/Object");
        code.emit(DUP);                 
        code.emit(ICONST_0);         
        code.emit(LLOAD_3);             
        code.emit(INVOKESTATIC, "java/lang/Long/valueOf(J)Ljava/lang/Long;");
        code.emit(AASTORE);        
        code.emit(INVOKEVIRTUAL, "java/io/PrintStream/printf(Ljava/lang/String;" +
                            "[Ljava/lang/Object;)Ljava/io/PrintStream;");
        localStack.decrease(2);
        code.emit(POP);          
    
        code.emitLine();
        code.emit(RETURN);
        code.emitLine();
        
        code.emitDirective(LIMIT_LOCALS, localVariables.count());
        code.emitDirective(LIMIT_STACK,  localStack.capacity());
        code.emitDirective(END_METHOD);
        
        code.close();  // the object file
    }
}
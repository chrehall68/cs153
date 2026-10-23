package backend.interpreter;

import intermediate.antlr4.Pcl_C2Parser.*;

public class Program extends Executor
{
    Object program(ProgramContext ctx)
    {
        long startTime = System.currentTimeMillis();
        
        StackFrame programFrame = new StackFrame(programEntry);
        runtimeStack.push(programFrame);
        
        if (debugging) debugger.start(ctx);
        visit(ctx.block().compoundStatement());

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.printf("\n%,20d statements executed." +
                          "\n%,20d runtime errors." +
                          "\n%,20d milliseconds execution time.\n",
                          executionCount, error.getCount(), elapsedTime);

        return null;
    }
}

package backend.interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.antlr4.Pcl_C2Parser;
import intermediate.symtab.SymtabEntry.Kind;
import backend.interpreter.antlr4.CommanderLexer;
import backend.interpreter.antlr4.CommanderParser;
import backend.interpreter.antlr4.CommanderParser.*;
import backend.interpreter.antlr4.CommanderBaseListener;

import static intermediate.symtab.SymtabEntry.Kind.*; 
import static intermediate.type.Typespec.Form.*;

public class Debugger extends CommanderBaseListener
{
    private RuntimeStack runtimeStack;
    private HashSet<Integer>     breakpoints = new HashSet<>();
    private HashSet<SymtabEntry> watchpoints = new HashSet<>();
    private Scanner console = new Scanner(System.in);
    boolean singleStepping = false;
    boolean resumeProgramExecution = false;
    
    /**
     * Constructor
     * @param runtimeStack the runtime stack.
     */
    public Debugger(RuntimeStack runtimeStack)
    {
        this.runtimeStack = runtimeStack;
    }
    
    /**
     * Read a debugger commands from the console.
     * @param ctx the current context.
     */
    private void readEvalPrintLoop(ParserRuleContext ctx)
    {
        System.out.println();
        printLineNumber(ctx.getStart().getLine());
        System.out.println();
        
        resumeProgramExecution = false;
        
        do
        {
            try
            {
                System.out.print("Command? ");
                String command = console.nextLine() + '\n';
                
                // Create the character stream from the input string.
                CharStream cs = CharStreams.fromString(command);
                
                // Create a lexer which scans the character stream
                // to create a token stream.
                CommanderLexer lexer = new CommanderLexer(cs);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                
                // Create a parser which parses the token stream.
                CommanderParser parser = new CommanderParser(tokens);
                
                // Parse the command and walk the parse tree.
                ParseTree tree = parser.command();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(this, tree);
            }
            catch (Exception ex)
            {
                System.out.println("*** Debugger command error.");
                resumeProgramExecution = false;
            }
        } while (!resumeProgramExecution);
    }
    
    /**
     * Start reading commands.
     * @param ctx the current context.
     */
    public void start(ParserRuleContext ctx)
    {
        readEvalPrintLoop(ctx);
    }

    /**
     * Debug a statement if single stepping or the statement is a breakpoint.
     * @param ctx the statement context.
     */
    public void statement(Pcl_C2Parser.StatementContext ctx)
    {
        int lineNumber = ctx.getStart().getLine();
        
        if (singleStepping || breakpoints.contains(lineNumber))
        {
            readEvalPrintLoop(ctx);
        }
    }

    /**
     * Debug an assignment if the target variable is a watchpoint.
     * @param ctx the target variable context.
     * @param value the value to assign.
     */
    public void assignment(Pcl_C2Parser.VariableContext ctx, 
                           Object value, Typespec type)
    {
        if (watchpoints.contains(ctx.entry))
        {
            printLineNumber(ctx.getStart().getLine());
            System.out.println(": " + ctx.entry.getName() + " <= " + value);
        }
    }

    /**
     * Debug a variable factor if the variable is a watchpoint.
     * @param ctx the variable context.
     * @param value the variable's value.
     */
    public void factorVariable(Pcl_C2Parser.VariableContext ctx, 
                               Object value, Typespec typespec)
    {   
        if (watchpoints.contains(ctx.entry))
        {
            printLineNumber(ctx.getStart().getLine());
            System.out.println(": " + ctx.entry.getName() + " is " + value);
        }
    }
    
    /**
     * Print the current line number.
     * @param lineNumber the line number.
     */
    private void printLineNumber(int lineNumber)
    {
        System.out.printf("At line %03d", lineNumber);
    }
    
    @Override
    public void exitGoCommand(GoCommandContext ctx)
    {
        singleStepping = false; 
        resumeProgramExecution = true;
    }

    @Override
    public void exitStepCommand(StepCommandContext ctx)
    {
        singleStepping = true;        
        resumeProgramExecution = true;
    }

    @Override
    public void exitQuitCommand(QuitCommandContext ctx)
    {
        System.out.println("Program terminated.");
        System.exit(0);
        
        resumeProgramExecution = false;
    }
 
    @Override
    public void exitBreakCommand(BreakCommandContext ctx)
    {
        // Record the list of breakpoints.
        if (ctx.lineNumberList() != null)
        {
            for (LineNumberContext lineNumberCtx : 
                                            ctx.lineNumberList().lineNumber())
            {
                int lineNumber = 
                            Integer.parseInt(lineNumberCtx.INTEGER().getText());
                breakpoints.add(lineNumber);
            }
        }
        else
        {
            System.out.print("Breakpoints:");
            
            // Print the list of breakpoints.
            if (breakpoints.size() > 0)
            {
                for (int lineNumber : breakpoints) 
                {
                    System.out.printf(" %03d", lineNumber);
                }
                System.out.println();
            }
            else
            {
                System.out.println(" none");
            }
        }
        
        resumeProgramExecution = false;
    }

    @Override
    public void exitUnbreakCommand(UnbreakCommandContext ctx)
    {
        for (LineNumberContext lineNumberCtx : 
                                            ctx.lineNumberList().lineNumber())
        {
            int lineNumber = Integer.parseInt(lineNumberCtx.INTEGER().getText());
            breakpoints.remove(lineNumber);
        }
        
        resumeProgramExecution = false;
    }

    @Override
    public void exitShowCommand(ShowCommandContext ctx)
    {
        for (VariableContext variableCtx :
                                                ctx.variableList().variable())
        {
            String variableName = variableCtx.IDENTIFIER().getText();
            
            // Print the variable name and the value.
            Cell variableCell = getCell(variableCtx);
            if (variableCell != null)
            {
                Object value = variableCell.getValue();
                System.out.println(variableName + " is " + value);
            }
            else
            {
                System.out.println("*** Undeclared or invalid: " + variableName);
            }
        }
        
        resumeProgramExecution = false;
    }
    
    /**
     * Get a variable's cell from its stack frame.
     * @param variableCtx the variable context.
     * @return the cell.
     */
    @SuppressWarnings("unchecked")
    private Cell getCell(VariableContext variableCtx)
    {
        String variableName = variableCtx.IDENTIFIER().getText();
        int currentLevel = runtimeStack.currentNestingLevel();
        StackFrame frame = null;
        Cell cell = null;
        
        // Find the variable's cell in the runtime stack.
        for (int level = currentLevel; (cell == null) && (level > 0); level--)
        {
            frame = runtimeStack.getTopmost(level);
            cell = frame.getCell(variableName);
        }
        
        if (cell == null) return null;
        
        // Get the variable's datatype.
        SymtabEntry routineEntry = frame.getRoutineEntry();
        Symtab routineSymtab = routineEntry.getRoutineSymtab();
        SymtabEntry variableId = routineSymtab.lookup(variableName);
        Typespec variableType = variableId.getTypespec();

        // Execute any array subscripts or record fields.
        for (ModifierContext modCtx : variableCtx.modifier()) 
        {
            // Subscripts.
            if (modCtx.indexList() != null)
            {
                if (variableType.getForm() == ARRAY)
                {
                    // Compute a new reference for each subscript.
                    for (IndexContext indexCtx :
                                                    modCtx.indexList().index())
                    {
                        Typespec indexType = variableType.getArrayIndexType();
                        int minIndex = 0;
                        
                        if (indexType.getForm() == SUBRANGE)
                        {
                            minIndex = (Integer) indexType.getSubrangeMinValue();
                        }
                        
                        int value = 
                                Integer.parseInt(indexCtx.INTEGER().getText());
                        int index = value - minIndex;
                        
                        cell = ((Cell[]) cell.getValue())[index];
                        variableType = variableType.getArrayElementType();
                    }
                }
                else
                {
                    System.out.println("*** Not an array.");
                    return null;
                }
            }

            // Record field.
            else 
            {
                if (variableType.getForm() == RECORD)
                {
                    Symtab symtab = variableType.getRecordSymtab();
                    String fieldName = 
                                modCtx.field().IDENTIFIER().getText().toLowerCase();
                    SymtabEntry fieldId = symtab.lookup(fieldName);
                    
                    if (fieldId != null)
                    {
                        // Compute a new reference for the field.
                        HashMap<String, Cell> map =
                                        (HashMap<String, Cell>) cell.getValue();
                        cell = map.get(fieldName);
                        variableType = fieldId.getTypespec();
                    }
                    else
                    {
                        System.out.println("*** Invalid field: " + fieldName);
                        return null;
                    }
                }
                else
                {
                    System.out.println("*** Not a record.");
                    return null;
                }
            }
        }

        return cell;
    }

    @Override
    public void exitStackCommand(StackCommandContext ctx)
    {
        ArrayList<SymtabEntry> callStack = new ArrayList<>();

        // Loop over the stack frames on the runtime stack
        // starting at the top of stack.
        for (int i = runtimeStack.size() - 1; i >= 0; --i) 
        {
            StackFrame frame = runtimeStack.get(i);
            SymtabEntry routineEntry = frame.getRoutineEntry();
            Kind kind = routineEntry.getKind();
            System.out.print(  kind == PROGRAM  ? "PROGRAM "
                             : kind == FUNCTION ? "FUNCTION "
                             :                    "PROCEDURE ");
            System.out.println(routineEntry.getName() + ":");

            // Add the symbol table entry of the procedure or function.
            callStack.add(routineEntry);

            // Print the value of each local variable.
            for (String name : frame.getAllNames()) 
            {
                Object value = frame.getCell(name).getValue();
                System.out.printf("%10s is ", name);
                System.out.println(value);
                
            }
        }
        
        resumeProgramExecution = false;
    }

    @Override
    public void exitWatchCommand(WatchCommandContext ctx)
    {
        // Record the list of watchpoints.
        if (ctx.simpleVariableList() != null)
        {
            for (SimpleVariableContext variableCtx :
                                    ctx.simpleVariableList().simpleVariable())
            {
                String variableName = variableCtx.IDENTIFIER().getText();
                SymtabEntry id = getSymtabEntry(variableName);
                
                if (id != null) watchpoints.add(id);
                else System.out.println("*** Undeclared: " + variableName);
            }
        }
        else
        {
            System.out.print("Watchpoints:");
            
            // Print the list of watchpoints.
            if (watchpoints.size() > 0)
            {
                for (SymtabEntry id : watchpoints)
                {
                    System.out.printf(" %s", id.getName());
                }
                System.out.println();
            }
            else
            {
                System.out.println(" none");
            }
        }
        
        resumeProgramExecution = false;
    }

    @Override
    public void exitUnwatchCommand(UnwatchCommandContext ctx)
    {
        for (SimpleVariableContext variableCtx :
                                    ctx.simpleVariableList().simpleVariable())
        {
            String variableName = variableCtx.IDENTIFIER().getText();
            SymtabEntry id = getSymtabEntry(variableName);
            
            if (id != null) watchpoints.remove(id);
            else System.out.println("*** Undeclared: " + variableName);
        }
        
        resumeProgramExecution = false;
    }
    
    /**
     * Get the symbol table entry of a variable.
     * @param variableName the variable's name.
     * @return the symbol table entry.
     */
    private SymtabEntry getSymtabEntry(String variableName)
    {
        SymtabEntry variableEntry = null;
        int currentLevel = runtimeStack.currentNestingLevel();
        
        // Look for the stack frame.
        for (int level = currentLevel; 
             (variableEntry == null) && (level > 0); 
             level--)
        {
            // Get the symbol table of the stack frame's routine. 
            StackFrame frame = runtimeStack.getTopmost(level);
            SymtabEntry routineEntry = frame.getRoutineEntry
                    ();
            Symtab routineSymtab = routineEntry.getRoutineSymtab();
            
            // Look up the variable in the routine's symbol table.
            variableEntry = routineSymtab.lookup(variableName);
        }
        
        return variableEntry;
    }
}

package backend.compiler;

import static backend.compiler.Instruction.*;
import static intermediate.type.Typespec.Form.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec;
import intermediate.type.Typespec.Form;

public class Statements extends Compiler
{
    Object statement(StatementContext ctx) 
    {
        if (   (ctx.compoundStatement() == null) 
            && (ctx.emptyStatement() == null))
        {
            code.emitComment(ctx);
        }
        
        return visitChildren(ctx);
    }
    
    Object assignmentStatement(AssignmentStatementContext ctx)
    {
        VariableContext   variableCtx  = ctx.variable();
        ExpressionContext exprCtx = ctx.expression();
        SymtabEntry varEntry = variableCtx.entry;
        Typespec varTypespec = variableCtx.typespec;
        Typespec exprType = exprCtx.typespec;
        
        // Emit code to evaluate the expression.
        visit(exprCtx);
        
        // real variable := integer constant
        if (   (varTypespec == Predefined.realType)
            && (exprType.baseType() == Predefined.integerType)) 
        {
            code.emit(I2D);
        }
        
        // Emit code to store the expression value into the target variable.
        // The target variable has no subscripts or fields.
        code.emitStoreValue(varEntry, varEntry.getTypespec());

        return null;
    }

    Object ifStatement(IfStatementContext ctx)
    {
        // Complete this method.
        
        return null;
    }
    
    Object repeatStatement(RepeatStatementContext ctx) 
    {
        Label loopTopLabel  = new Label();
        Label loopExitLabel = new Label();

        code.emitLabel(loopTopLabel);
        
        visit(ctx.statementList());
        visit(ctx.expression());
        code.emit(IFNE, loopExitLabel);
        code.emit(GOTO, loopTopLabel);
        
        code.emitLabel(loopExitLabel);
        
        return null;
    }
    
    Object whileStatement(WhileStatementContext ctx) 
    {
        // Complete this method.
        
        return null;
    }
    
    Object caseStatement(CaseStatementContext ctx)
    {
        // Complete this method.
        
        return null;
    }
    
    
    Object forStatement(ForStatementContext ctx)
    {
        // Complete this method.

        return null;
    }
    
    Object procedureCallStatement(ProcedureCallStatementContext ctx)
    {
        SymtabEntry procedureEntry = ctx.procedureName().entry;
        emitCall(procedureEntry, ctx.argumentList());
        return null;
    }
    
    /**
     * Emit a call to a procedure or a function.
     * @param procFuncEntry the routine name's symbol table entry.
     * @param argListCtx the ArgumentListContext.
     */
    void emitCall(SymtabEntry procFuncEntry,
                          ArgumentListContext argListCtx)
    {
        String routineName = procFuncEntry.getName();
        ArrayList<SymtabEntry> parmEntries = procFuncEntry.getRoutineParameters();
        
        // Emit code to evaluate any arguments.
        int i = 0;
        if (argListCtx != null)
        {
            for (ArgumentContext argCtx : argListCtx.argument())
            {
                ExpressionContext exprCtx = argCtx.expression();
                visit(exprCtx);
                
                if (   (parmEntries.get(i++).getTypespec() == Predefined.realType)
                    && (exprCtx.typespec == Predefined.integerType))
                {
                    code.emit(I2D);
                }
            }
        }
        
        StringBuilder buffer = new StringBuilder();

        // Procedure or function name.
        buffer.append(programName);
        buffer.append("/");
        buffer.append(routineName);
        buffer.append("(");

        // Parameter and return type descriptors.
        if (argListCtx != null) 
        {
            for (SymtabEntry parmEntry : parmEntries) 
            {
                buffer.append(code.typeDescriptor(parmEntry));
            }
        }
        buffer.append(")");
        buffer.append(code.typeDescriptor(procFuncEntry));

        // Generate a call to the routine.
        code.emit(INVOKESTATIC, buffer.toString());
        if (argListCtx != null) 
        {
            LocalStack localStack = code.getLocalStack();
            localStack.decrease(parmEntries.size());
        }
    }
  
    Object writeStatement(WriteStatementContext ctx)
    {
        emitWrite(ctx.writeArguments(), false);
        return null;
    }
    
    Object writelnStatement(WritelnStatementContext ctx)
    {
        emitWrite(ctx.writeArguments(), true);
        return null;
    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     * @param needLF true if need a line feed.
     */
    private void emitWrite(WriteArgumentsContext argsCtx,
                           boolean needLF)
    {
        code.emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (argsCtx == null) 
        {
            code.emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");

            LocalStack localStack = code.getLocalStack();
            localStack.decrease(1);
        }
            
        // Generate code for the arguments.
        else
        {
            StringBuffer formatString = new StringBuffer();
            int exprCount = createWriteFormat(argsCtx, formatString, 
                                              needLF);
            LocalStack localStack = code.getLocalStack();
            
            // Load the format string.
            code.emitLoadConstant(formatString.toString());
            
            // Emit the arguments array.
            if (exprCount > 0)
            {
                emitArgumentsArray(argsCtx, exprCount);

                code.emit(INVOKEVIRTUAL,
                             "java/io/PrintStream/printf(Ljava/lang/String;" +
                                 "[Ljava/lang/Object;)" +
                             "Ljava/io/PrintStream;");
                localStack.decrease(2);
                code.emit(POP);
            }
            else
            {
                code.emit(INVOKEVIRTUAL,
                             "java/io/PrintStream/print(Ljava/lang/String;)V");
                localStack.decrease(2);
            }
        }
    }
    
    /**
     * Create the printf format string.
     * @param argsCtx the WriteArgumentsContext.
     * @param format the format string to create.
     * @return the count of expression arguments.
     */
    private int createWriteFormat(WriteArgumentsContext ctx,
                                  StringBuffer formatString, 
                                  boolean needLF)
    {
        int exprCount = 0;
        
        // Loop over the write arguments to build the format string.
        for (WriteArgumentContext writeArgCtx : 
                                    ctx.writeArgument())
        {
            ExpressionContext exprCtx = writeArgCtx.expression();
            FormatContext formatCtx = writeArgCtx.format();  
            
            Typespec typespec = exprCtx.typespec;
            boolean isIntegerType = typespec == Predefined.integerType;
            boolean isRealType    = typespec == Predefined.realType;
            boolean isCharType    = typespec == Predefined.charType;
            boolean isBooleanType = typespec == Predefined.booleanType;
            
            String arg = exprCtx.getText();
            
            // A string or character constant without formatting.
            if (   (formatCtx == null)
                && isSingleton(exprCtx)
                && (   (arg.charAt(0) == '"') 
                    || (arg.charAt(0) == '\'')))
            {
                String format = 
                        arg.substring(1, arg.length()-1);
                format = format.replace("''", "'")
                               .replace("\"", "\\\"");
                formatString.append(format);
            }
            
            // For any other argument, append a field specifier.
            else
            {
                ++exprCount;
                formatString.append("%");
                
                if (formatCtx != null)
                {
                    WidthContext widthCtx = formatCtx.width();
                    SignContext signCtx = widthCtx.sign();
                    String sign = (   (signCtx != null) 
                                   && (signCtx.getText().equals("-"))) 
                                ? "-" : "";
                    formatString.append(sign)
                                .append(widthCtx.integerConstant()
                                                .getText());
                    
                    if (isRealType)
                    {
                        formatString.append(".");
                        
                        PrecisionContext precisionCtx = 
                                                formatCtx.precision();
                        String precisionText = precisionCtx != null
                            ? precisionCtx.integerConstant().getText()
                            : "0";
                        formatString.append(precisionText);
                    }
                }

                String typeFlag = isIntegerType ? "d"
                                : isRealType    ? "f"
                                : isBooleanType ? "b"
                                : isCharType    ? "c" 
                                :                 "s";
                formatString.append(typeFlag);
            }
        }
        
        if (needLF) formatString.append("\\n"); 
        return exprCount;
    }
    
    /**
     * Emit the printf arguments array.
     * @param argsCtx
     * @param exprCount
     */
    private void emitArgumentsArray(WriteArgumentsContext argsCtx,
                                    int exprCount)
    {
        // Create the arguments array.
        code.emitLoadConstant(exprCount);
        code.emit(ANEWARRAY, "java/lang/Object");

        int index = 0;

        // Loop over the write arguments to fill the arguments array.
        for (WriteArgumentContext argCtx : argsCtx.writeArgument())
        {
            ExpressionContext exprCtx = argCtx.expression();
            FormatContext formatCtx = argCtx.format();  
            Typespec type = exprCtx.typespec.baseType();
            
            String arg = exprCtx.getText();
            
            // Skip unformatted string constants, 
            // which were made part of the format string.
            if (   (formatCtx != null)
                || (!isSingleton(exprCtx))
                || (   (arg.charAt(0) != '"') 
                    && (arg.charAt(0) != '\'')))
            {
                code.emit(DUP);
                code.emitLoadConstant(index++);

                visit(exprCtx);

                Form form = type.getForm();
                if (    ((form == SCALAR) || (form == ENUMERATED))
                     && (type != Predefined.stringType))
                {
                    code.emit(INVOKESTATIC, code.valueOfSignature(type));
                }

                // Store the value into the array.
                code.emit(AASTORE);
            }
        }
    }
    
    private boolean isSingleton(ExpressionContext exprCtx)
    {
        if (exprCtx.simpleExpression().size() > 1) return false;
        
        SimpleExpressionContext simpleExpressionCtx = exprCtx.simpleExpression(0);
        if (simpleExpressionCtx.term().size() > 1) return false;
        
        TermContext termCtx = simpleExpressionCtx.term(0);
        if (termCtx.factor().size() > 1) return false;
        
        return true;
    }
}
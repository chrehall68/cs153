package backend.compiler;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static backend.compiler.Instruction.*;

public class Expressions extends Compiler
{
    /**
     * Emit code for an expression.
     * @param ctx the ExpressionContext.
     */
    Object expression(ExpressionContext ctx)
    {
        SimpleExpressionContext simpleCtx1 = ctx.simpleExpression().get(0);
        RelOpContext relOpCtx = ctx.relOp();
        Typespec type1 = simpleCtx1.typespec;
        LocalStack localStack = code.getLocalStack();
        
        visit(simpleCtx1);
        
        // More than one simple expression?
        if (relOpCtx != null)
        {
            String op = relOpCtx.getText();
            SimpleExpressionContext simpleCtx2 = ctx.simpleExpression().get(1);
            Typespec type2 = simpleCtx2.typespec;

            boolean integerMode   = false;
            boolean realMode      = false;
            boolean characterMode = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
            else if (   (type1 == Predefined.charType) 
                     && (type2 == Predefined.charType))
            {
                characterMode = true;
            }

            Label trueLabel = new Label();
            Label exitLabel = new Label();

            if (integerMode || characterMode) 
            {
                visit(simpleCtx2);
                
                if      (op.equals("=" )) code.emit(IF_ICMPEQ, trueLabel);
                else if (op.equals("<>")) code.emit(IF_ICMPNE, trueLabel);
                else if (op.equals("<" )) code.emit(IF_ICMPLT, trueLabel);
                else if (op.equals("<=")) code.emit(IF_ICMPLE, trueLabel);
                else if (op.equals(">" )) code.emit(IF_ICMPGT, trueLabel);
                else if (op.equals(">=")) code.emit(IF_ICMPGE, trueLabel);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) code.emit(I2D);
                visit(simpleCtx2);
                if (type2 == Predefined.integerType) code.emit(I2D);
                
                code.emit(DCMPG);

                if      (op.equals("=" )) code.emit(IFEQ, trueLabel);
                else if (op.equals("<>")) code.emit(IFNE, trueLabel);
                else if (op.equals("<" )) code.emit(IFLT, trueLabel);
                else if (op.equals("<=")) code.emit(IFLE, trueLabel);
                else if (op.equals(">" )) code.emit(IFGT, trueLabel);
                else if (op.equals(">=")) code.emit(IFGE, trueLabel);
            }
            else  // stringMode
            {
                visit(simpleCtx2);
                code.emit(INVOKEVIRTUAL,
                             "java/lang/String.compareTo(Ljava/lang/String;)I");
                localStack.decrease(1);
                
                if      (op.equals("=" )) code.emit(IFEQ, trueLabel);
                else if (op.equals("<>")) code.emit(IFNE, trueLabel);
                else if (op.equals("<" )) code.emit(IFLT, trueLabel);
                else if (op.equals("<=")) code.emit(IFLE, trueLabel);
                else if (op.equals(">" )) code.emit(IFGT, trueLabel);
                else if (op.equals(">=")) code.emit(IFGE, trueLabel);
            }

            code.emit(ICONST_0); // false
            code.emit(GOTO, exitLabel);
            code.emitLabel(trueLabel);
            code.emit(ICONST_1); // true
            code.emitLabel(exitLabel);
            
            localStack.decrease(1);  // only one branch will be taken
        }
        
        return null;
    }
    
    /**
     * Emit code for a simple expression.
     * @param ctx the SimpleExpressionContext.
     */
    Object simpleExpression(SimpleExpressionContext ctx)
    {
        int count = ctx.term().size();
        Boolean negate =    (ctx.sign() != null) 
                         && ctx.sign().getText().equals("-");
        
        // First term.
        TermContext termCtx1 = ctx.term().get(0);
        Typespec type1 = termCtx1.typespec;
        visit(termCtx1);
        
        if (negate) code.emit(type1 == Predefined.integerType ? INEG : DNEG);
        
        // Loop over the subsequent terms.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.addOp().get(i-1).getText().toLowerCase();
            TermContext termCtx2 = ctx.term().get(i);
            Typespec type2 = termCtx2.typespec;
            LocalStack localStack = code.getLocalStack();

            boolean integerMode = false;
            boolean realMode    = false;
            boolean booleanMode = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
            else if (   (type1 == Predefined.booleanType) 
                     && (type2 == Predefined.booleanType))
            {
                booleanMode = true;
            }
                            
            if (integerMode)
            {
                visit(termCtx2);
                
                if (op.equals("+")) code.emit(IADD);
                else                code.emit(ISUB);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) code.emit(I2D);
                visit(termCtx2);
                if (type2 == Predefined.integerType) code.emit(I2D);
                
                if (op.equals("+")) code.emit(DADD);
                else                code.emit(DSUB);
            }
            else if (booleanMode)
            {
                visit(termCtx2);
                code.emit(IOR);
            }
            else  // stringMode
            {
                visit(termCtx2);
                code.emit(INVOKEVIRTUAL,
                          "java/lang/String/concat(Ljava/lang/String;)"
                        + "Ljava/lang/String;");
                localStack.decrease(1);
            }
        }
        
        return null;
    }
    
    /**
     * Emit code for a term.
     * @param ctx the TermContext.
     */
    Object term(TermContext ctx)
    {
        int count = ctx.factor().size();
        
        // First factor.
        FactorContext factorCtx1 = ctx.factor().get(0);
        Typespec type1 = factorCtx1.typespec.baseType();
        visit(factorCtx1);
        
        // Loop over the subsequent factors.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.mulOp().get(i-1).getText().toLowerCase();
            FactorContext factorCtx2 = ctx.factor().get(i);
            Typespec type2 = factorCtx2.typespec.baseType();

            boolean integerMode = false;
            boolean realMode    = false;

            if (op.equals("/")) realMode = true;
            else if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
                
            if (integerMode)
            {
                visit(factorCtx2);            

                if      (op.equals("*"))   code.emit(IMUL);
                else if (op.equals("/"))   code.emit(DDIV);
                else if (op.equals("div")) code.emit(IDIV);
                else if (op.equals("mod")) code.emit(IREM);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) code.emit(I2D);
                visit(factorCtx2); 
                if (type2 == Predefined.integerType) code.emit(I2D);
                
                if      (op.equals("*")) code.emit(DMUL);
                else if (op.equals("/")) code.emit(DDIV);
            }
            else  // booleanMode
            {
                visit(factorCtx2);                 
                code.emit(IAND);
            }
        }
        
        return null;
    }

    Object functionCall(FunctionCallContext ctx) 
    {
        SymtabEntry funtionEntry = ctx.functionName().entry;
        statementsImpl.emitCall(funtionEntry, ctx.argumentList());
        
        // A function call leaves a value on the operand stack.
        LocalStack localStack = code.getLocalStack();
        localStack.increase(1);
        
        return null;
    }
    
    /**
     * Emit code for NOT.
     * @param ctx the NotFactorContext.
     */
    Object factorNot(FactorNotContext ctx)
    {
        visit(ctx.factor());
        code.emit(ICONST_1);
        code.emit(IXOR);
        
        return null;
    }

    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param ctx the VariableContext.
     */
    Object variable(VariableContext ctx)
    {
        if (ctx.entry.getKind() == FUNCTION)
        {
            statementsImpl.emitCall(ctx.entry, null);
        }
        else 
        {
            code.emitLoadValue(ctx.entry);
        }
        
        return null;
    }
    
    /**
     * Emit code to load an integer constant.
     * @parm intCtx the IntegerConstantContext.
     */
    Object integerConstant(IntegerConstantContext ctx)
    {
        int value = (Integer) ctx.value;
        code.emitLoadConstant(value);
        
        return null;
    }
    
    /**
     * Emit code to load real constant.
     * @parm intCtx the IntegerConstantContext.
     */
    Object realConstant(RealConstantContext ctx)
    {
        double value = (Double) ctx.value;
        code.emitLoadConstant(value);
        
        return null;
    }
    
    Object booleanConstant(BooleanConstantContext ctx)
    {
        int value = (Integer) ctx.value;
        code.emitLoadConstant(value);
        
        return null;
    }
    
    Object characterConstant(CharacterConstantContext ctx)
    {
        char value = (Character) ctx.value;
        code.emitLoadConstant(String.valueOf(value));
        
        return null;
    }
    
    Object stringConstant(StringConstantContext ctx)
    {
        String value = (String) ctx.value;
        code.emitLoadConstant(value);
        
        return null;
    }
}

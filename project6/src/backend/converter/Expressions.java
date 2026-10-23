package backend.converter;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;

public class Expressions extends Converter
{
    Object expression(ExpressionContext ctx) 
    {
        SimpleExpressionContext simpleCtx1 = 
                            ctx.simpleExpression(0);
        RelOpContext relOpCtx = ctx.relOp();
        
        // Only one simple expression?
        if (relOpCtx == null) visit(simpleCtx1);
        
        // Two simple expressions.
        else
        {
            String op = relOpCtx.getText();
            
            if      (op.equals("="))  op = "==";
            else if (op.equals("<>")) op = "!=";
            
            SimpleExpressionContext simpleCtx2 =
                            ctx.simpleExpression(1);
            
            // Java uses the compareTo method for strings.
            if (simpleCtx1.typespec == 
                                Predefined.stringType)
            {
                code.emit("(");
                visit(simpleCtx1);
                code.emit(")." + "compareTo(");
                visit(simpleCtx2);
                code.emit(") " + op + " 0");
            }
            else
            {
                visit(simpleCtx1);
                code.emit(" " + op + " ");
                visit(simpleCtx2);
            }
        }
        
        return null;
    }

    Object simpleExpression(SimpleExpressionContext ctx) 
    {
        int count = ctx.term().size();
        
        if ((ctx.sign() != null) && (ctx.sign().getText().equals("-")))
        {
            code.emit("-");
        }
        
        // Loop over the terms.
        for (int i = 0; i < count; i++)
        {
            TermContext termCtx = ctx.term(i);
            visit(termCtx);
            
            if (i < count-1)
            {
                String addOp = ctx.addOp(i).getText().toLowerCase();
                if (addOp.equals("or")) addOp = "||";
                
                code.emit(" " + addOp + " ");
            }
        }
        
        return null;
    }

    Object term(TermContext ctx) 
    {
        int count = ctx.factor().size();
        String mulOp = null;
        
        // Loop over the factors.
        for (int i = 0; i < count; i++)
        {
            FactorContext factorCtx = ctx.factor(i);
            Typespec typespec1 = factorCtx.typespec.baseType();
            Typespec typespec2 = null;
            boolean realDivision = false;
            
            if (i < count-1)
            {
                mulOp = ctx.mulOp(i).getText().toLowerCase();
                if      (mulOp.equals("and")) mulOp = " && ";
                else if (mulOp.equals("div")) mulOp = "/";
                else if (mulOp.equals("mod")) mulOp = "%";
                
                else if (mulOp.equals("/"))
                {
                    realDivision = true;
                    typespec2 = ctx.factor(i+1).typespec;
                }
            }
            
            if (realDivision
                && (typespec1 == Predefined.integerType)
                && (typespec2 != Predefined.realType))
            {
                code.emit("((double) ");
                visit(factorCtx);
                code.emit(")");
            }
            else
            {
                visit(factorCtx);
            }
            
            if (i < count-1) code.emit(mulOp);
        }
        
        return null;
    }

    Object variable(VariableContext ctx) 
    {
        IdentifierContext idCtx = ctx.identifier();
        SymtabEntry variableEntry = idCtx.entry;
        String variableName = variableEntry.getName().toLowerCase();
        Typespec typespec = idCtx.typespec;
        Kind kind = variableEntry.getKind();
               
        if (    (typespec != Predefined.booleanType)
             && (kind == ENUMERATED_CONSTANT))
        {
            String typeIdName = typespec.getIdentifier().getName();
            code.emit(typeIdName + "." 
                      + variableName.toUpperCase());
        }
        else if (kind == CONSTANT)
        {
            code.emit(variableName.toUpperCase());
        }
        else if (kind == FUNCTION) 
        {
            code.emit(variableName + "()");
        }
        else
        {
            code.emit(variableName);
        }
        
        // Loop over any subscript and field modifiers.
        for (ModifierContext modCtx : ctx.modifier())
        {
            // Subscripts.  
            if (modCtx.indexList() != null)
            {
                for (IndexContext indexCtx : modCtx.indexList().index())
                {
                    Typespec indexType = typespec.getArrayIndexType();
                    int minIndex = 0;
                    
                    if (indexType.getForm() == SUBRANGE)
                    {
                        minIndex = 
                            (Integer) indexType.getSubrangeMinValue();
                    }
                    
                    ExpressionContext exprCtx = indexCtx.expression();
                    code.emit("[");
                    if (minIndex == 0)
                    {
                        visit(exprCtx);
                    }
                    else
                    {
                        code.emit("(");
                        visit(exprCtx);
                        code.emit(")");
                        
                        if (minIndex < 0) code.emit("+" + (-minIndex));
                        else              code.emit("-" + minIndex);
                    }
                    code.emit("]");
                    
                    typespec = typespec.getArrayElementType();
                }
            }
            
            // Record field.
            else  
            {
                FieldContext fieldCtx = modCtx.field();
                String fieldName = fieldCtx.entry.getName();
                code.emit("." + fieldName);
                typespec = fieldCtx.typespec;
            }
        }
        
        return null;
    }

    Object functionCall(FunctionCallContext ctx) 
    {
        FunctionNameContext funcNameCtx = ctx.functionName();
        String functionName = funcNameCtx.getText().toLowerCase();
        
        code.emit(functionName + "(");
        if (ctx.argumentList() != null) visit(ctx.argumentList());
        code.emit(")");

        return null;
    }
    
    Object argumentList(ArgumentListContext ctx)
    {
        String separator = "";
        
        for (ArgumentContext argCtx : ctx.argument())
        {
            if (separator.length() > 0) code.emit(separator);
            visit(argCtx.expression());
            separator = ", ";
        }
        
        return null;
    }
    
    Object factorNot(FactorNotContext ctx)
    {
        code.emit("!(");
        visit(ctx.factor());
        code.emit(")");
        
        return null;
    }
    
    Object factorParens(FactorParensContext ctx)
    {
        code.emit("(");
        visit(ctx.expression());
        code.emit(")");
        
        return null;
    }
}

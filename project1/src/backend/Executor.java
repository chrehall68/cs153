/**
 * Executor class for a simple interpreter.
 * 
 * (c) 2020 by Ronald Mak
 * Department of Computer Science
 * San Jose State University
 */
package backend;

import java.util.ArrayList;
import java.util.HashSet;

import intermediate.*;
import static intermediate.Node.NodeType.*;

public class Executor
{
    private int lineNumber;
    private Symtab symtab;
    
    private static HashSet<Node.NodeType> singletons;
    private static HashSet<Node.NodeType> relationals;
    
    static
    {
        singletons  = new HashSet<Node.NodeType>();
        relationals = new HashSet<Node.NodeType>();
        
        singletons.add(VARIABLE);
        singletons.add(INTEGER_CONSTANT);
        singletons.add(REAL_CONSTANT);
        singletons.add(STRING_CONSTANT);
        
        relationals.add(EQ);
        relationals.add(LT);
    }
    
    public Executor(Symtab symtab)
    {
        this.symtab = symtab;
    }
    
    public Object visit(Node node)
    {
        switch (node.type)
        {
            case PROGRAM :  return visitProgram(node);
            
            case COMPOUND : 
            case ASSIGN :   
            case LOOP : 
            case CASE :
            case WRITE :
            case WRITELN :  return visitStatement(node);
            
            case TEST:      return visitTest(node);
            
            default :       return visitExpression(node);
        }
    }
    
    private Object visitProgram(Node programNode)
    {
        Node compoundNode = programNode.children.get(0);
        return visit(compoundNode);
    }
    
    private Object visitStatement(Node statementNode)
    {
        lineNumber = statementNode.lineNumber;
        
        switch (statementNode.type)
        {
            case COMPOUND :  return visitCompound(statementNode);
            case ASSIGN :    return visitAssign(statementNode);
            case LOOP :      return visitLoop(statementNode);
            case CASE :      return visitCase(statementNode);
            case WRITE :     return visitWrite(statementNode);
            case WRITELN :   return visitWriteln(statementNode);
            
            default :        return null;
        }
    }
    
    private Object visitCompound(Node compoundNode)
    {
        for (Node statementNode : compoundNode.children) visit(statementNode);
        
        return null;
    }
    
    private Object visitAssign(Node assignNode)
    {
        Node lhs = assignNode.children.get(0);
        Node rhs = assignNode.children.get(1);
        
        // Evaluate the right-hand-side expression;
        Double value = (Double) visit(rhs);
        
        // Store the value into the variable's symbol table entry.
        String variableName = lhs.text;
        SymtabEntry variableEntry = symtab.lookup(variableName);
        variableEntry.setValue(value);
        
        return null;
    }
    
    private Object visitLoop(Node loopNode)
    {        
        boolean b = false;
        do
        {
            for (Node node : loopNode.children)
            {
                Object value = visit(node);  // statement or test
                
                // Evaluate the test condition. Stop looping if true.
                b = (node.type == TEST) && ((boolean) value);
                if (b) break;
            }
        } while (!b);
        
        return null;
    }
    
    private Object visitTest(Node testNode)
    {
        return (Boolean) visit(testNode.children.get(0));
    }
    
    private Object visitCase(Node caseNode)
    {
        Object selectorValue = visit(caseNode.children.get(0));
        
        for (int i = 1; i < caseNode.children.size(); i++)
        {
            Node branchNode = caseNode.children.get(i);
            if (branchNode.children.size() == 0) continue;
            
            int statementIndex = branchNode.children.size() - 1;
            Node statementNode = branchNode.children.get(statementIndex);
            
            if (matchesBranch(selectorValue, branchNode, statementIndex))
            {
                visit(statementNode);
                break;
            }
        }
        
        return null;
    }
    
    private boolean matchesBranch(Object selectorValue, Node branchNode, int statementIndex)
    {
        for (int i = 0; i < statementIndex; i++)
        {
            Node labelNode = branchNode.children.get(i);
            
            if (labelNode.type == RANGE)
            {
                if (matchesRange(selectorValue, labelNode)) return true;
            }
            else if (matchesValue(selectorValue, visit(labelNode))) return true;
        }
        
        return false;
    }
    
    private boolean matchesRange(Object selectorValue, Node rangeNode)
    {
        Object startValue = visit(rangeNode.children.get(0));
        Object endValue   = visit(rangeNode.children.get(1));
        boolean inclusiveUpper = (Boolean) rangeNode.value;
        
        if (   (selectorValue instanceof String)
            && (startValue instanceof String)
            && (endValue instanceof String))
        {
            String selector = (String) selectorValue;
            String start = (String) startValue;
            String end = (String) endValue;
            
            return (selector.compareTo(start) >= 0)
                   && (inclusiveUpper ? selector.compareTo(end) <= 0
                                      : selector.compareTo(end) < 0);
        }
        
        if (   (selectorValue instanceof Double)
            && (startValue instanceof Double)
            && (endValue instanceof Double))
        {
            double selector = (Double) selectorValue;
            double start = (Double) startValue;
            double end = (Double) endValue;
            
            return (selector >= start)
                   && (inclusiveUpper ? selector <= end : selector < end);
        }
        
        return false;
    }
    
    private boolean matchesValue(Object selectorValue, Object labelValue)
    {
        if ((selectorValue instanceof String) && (labelValue instanceof String))
        {
            return ((String) selectorValue).equals((String) labelValue);
        }
        
        if ((selectorValue instanceof Double) && (labelValue instanceof Double))
        {
            return ((Double) selectorValue).doubleValue()
                   == ((Double) labelValue).doubleValue();
        }
        
        return false;
    }
    
    private Object visitWrite(Node writeNode)
    {
        printValue(writeNode.children);
        return null;
    }
    
    private Object visitWriteln(Node writelnNode)
    {
        if (writelnNode.children.size() > 0) printValue(writelnNode.children);
        System.out.println();
        
        return null;
    }

    private void printValue(ArrayList<Node> children)
    {
        long fieldWidth    = -1;
        long decimalPlaces = 0;
        
        // Use any specified field width and count of decimal places.
        if (children.size() > 1)
        {
            double fw = (Double) visit(children.get(1));
            fieldWidth = (long) fw;
            
            if (children.size() > 2) 
            {
                double dp = (Double) visit(children.get(2));
                decimalPlaces = (long) dp;
            }
        }
        
        // Print the value with a format.
        Node valueNode = children.get(0);
        if (valueNode.type == VARIABLE)
        {
            String format = "%";
            if (fieldWidth >= 0)    format += fieldWidth;
            if (decimalPlaces >= 0) format += "." + decimalPlaces;
            format += "f";
            
            Double value = (Double) visit(valueNode);
            System.out.printf(format, value);
        }
        else  // node type STRING_CONSTANT
        {
            String format = "%";
            if (fieldWidth > 0) format += fieldWidth;
            format += "s";
            
            String value = (String) visit(valueNode);
            System.out.printf(format, value);
        }
    }

    private Object visitExpression(Node expressionNode)
    {
        // Single-operand expressions.
        if (singletons.contains(expressionNode.type))
        {
            switch (expressionNode.type)
            {
                case VARIABLE         : return visitVariable(expressionNode);
                case INTEGER_CONSTANT : return visitIntegerConstant(expressionNode);
                case REAL_CONSTANT    : return visitRealConstant(expressionNode);
                case STRING_CONSTANT  : return visitStringConstant(expressionNode);
                
                default: return null;
            }
        }
        
        // Binary expressions.
        double value1 = (Double) visit(expressionNode.children.get(0));
        double value2 = (Double) visit(expressionNode.children.get(1));
        
        // Relational expressions.
        if (relationals.contains(expressionNode.type))
        {
            boolean value = false;
            
            switch (expressionNode.type)
            {
                case EQ : value = value1 == value2; break;
                case LT : value = value1 <  value2; break;
                
                default : break;
            }
            
            return value;
        }
           
        double value = 0.0;
        
        // Arithmetic expressions.
        switch (expressionNode.type)
        {
            case ADD :      value = value1 + value2; break;
            case SUBTRACT : value = value1 - value2; break;
            case MULTIPLY : value = value1 * value2; break;
                
            case DIVIDE :
            {
                if (value2 != 0.0) value = value1/value2;
                else
                {
                    runtimeError(expressionNode, "Division by zero");
                    return 0.0;
                }
                
                break;
            }
            
            default : break;
        }
        
        return Double.valueOf(value);
    }
    
    private Object visitVariable(Node variableNode)
    {
        // Obtain the variable's value from its symbol table entry.
        String variableName = variableNode.text;
        SymtabEntry variableEntry = symtab.lookup(variableName);
        Double value = variableEntry.getValue();
        
        return value;
    }
    
    private Object visitIntegerConstant(Node integerConstantNode)
    {
        long value = (Long) integerConstantNode.value;
        return (double) value;
    }
    
    private Object visitRealConstant(Node realConstantNode)
    {
        return (Double) realConstantNode.value;
    }
    
    private Object visitStringConstant(Node stringConstantNode)
    {
        return (String) stringConstantNode.value;
    }

    private void runtimeError(Node node, String message)
    {
        System.out.printf("RUNTIME ERROR at line %d: %s: %s\n", 
                          lineNumber, message, node.text);
        System.exit(-2);
    }
}

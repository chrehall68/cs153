package backend.converter;

import java.util.Hashtable;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.SymtabEntry;
import intermediate.antlr4.Pcl_P6BaseVisitor;
import intermediate.type.Typespec_P6;
import intermediate.type.Typespec_P6.Form;

import static intermediate.semantics.Semantics_P6.programEntry;
import static intermediate.type.Typespec_P6.Form.*;

public class Converter_P6 extends Pcl_P6BaseVisitor<Object>
{
    protected static JavaEmitter code;
    protected static boolean programVariables = true;
    protected static boolean recordFields = false;
    protected static String currentSeparator = "";
    protected static String programName;
    protected static Hashtable<String, String> typeNameTable;

    protected static Program_P6              program;  
    protected static ConstantDefinitions_P6  constantDefinitions;
    protected static TypeDefinitions_P6      typeDefinitions;
    protected static VariableDeclarations_P6 variableDeclarations;
    protected static Statements_P6           statements;
    protected static Expressions_P6          expressions;

    static
    {
        program              = new Program_P6();
        constantDefinitions  = new ConstantDefinitions_P6();
        typeDefinitions      = new TypeDefinitions_P6();
        variableDeclarations = new VariableDeclarations_P6();
        statements           = new Statements_P6();
        expressions          = new Expressions_P6();
        
        typeNameTable = new Hashtable<>();
        typeNameTable.put("integer", "int");
        typeNameTable.put("real",    "double");
        typeNameTable.put("boolean", "boolean");
        typeNameTable.put("char",    "char");
        typeNameTable.put("string",  "String");
    }
    
    public String getObjectFileName() { return code.getObjectFileName(); }

    protected String javaTypeName(Typespec_P6 pascalTypespec)
    {
        Form form = pascalTypespec.getForm();
        SymtabEntry typeEntry = pascalTypespec.getIdentifier();
        String pascalTypeName = 
            typeEntry != null ? typeEntry.getName() 
                              : null;
        String javaTypeName = null;
        
        switch (form)
        {
            case SCALAR:
                return typeNameTable.get(pascalTypeName);
                
            case ENUMERATED:
                return pascalTypeName != null ? pascalTypeName 
                                              : "int";
                
            case SUBRANGE:
                Typespec_P6 baseType = pascalTypespec.baseType();
                
                if (baseType.getIdentifier() != null)
                {
                    pascalTypeName = baseType.getIdentifier().getName();
                    javaTypeName = typeNameTable.get(pascalTypeName);
                    return javaTypeName != null ? javaTypeName 
                                        : pascalTypeName;
                }
                else return "int";
                
            case STRING:
                return "String";
                
            case ARRAY:
                Typespec_P6 elmtType = pascalTypespec.getArrayBaseType();
                
                if (elmtType.getIdentifier() != null)
                {
                    if (elmtType.getForm() == STRING)
                    {
                        return "String";
                    }
                    
                    pascalTypeName = elmtType.getIdentifier().getName();
                    javaTypeName = typeNameTable.get(pascalTypeName);
                    
                    return javaTypeName != null ? javaTypeName 
                                                : pascalTypeName;
                }
                else
                {
                    return "int";
                }
                
            case RECORD:
                return pascalTypeName;
                
            default: return "*unknown*";
        }
    }
    
    /**
     * Emit a pair of empty brackets for each dimension.
     * @param type the array datatype.
     */
    protected void emitArraySpecifier(Typespec_P6 typespec)
    {
        String brackets = "";
        
        while (typespec.getForm() == ARRAY)
        {
            brackets += "[]";
            typespec = typespec.getArrayElementType();
        }
        
        code.emit(brackets);
    }

    @Override
    public Object visitProgram(ProgramContext ctx)
    {
        programName = programEntry.getName();
        code = new JavaEmitter(programName, "java");

        return program.program(ctx);
    }

    @Override 
    public Object visitConstantDefinition(ConstantDefinitionContext ctx) 
    {
        return constantDefinitions.constantDefinition(ctx);
    }
    
    @Override
    public Object visitConstantSignedIdentifier(ConstantSignedIdentifierContext ctx)
    {
        return constantDefinitions.constantSignedIdentifier(ctx);
    }
    
    @Override 
    public Object visitConstantIdentifier(ConstantIdentifierContext ctx) 
    { 
        return constantDefinitions.constantIdentifier(ctx); 
    }

    @Override 
    public Object visitIntegerConstant(IntegerConstantContext ctx)
    { 
        return constantDefinitions.integerConstant(ctx); 
    }

    @Override 
    public Object visitRealConstant(RealConstantContext ctx)
    { 
        return constantDefinitions.realConstant(ctx); 
    }

    @Override 
    public Object visitCharacterConstant(CharacterConstantContext ctx) 
    { 
        return constantDefinitions.characterConstant(ctx); 
    }

    @Override 
    public Object visitStringConstant(StringConstantContext ctx) 
    { 
        return constantDefinitions.stringConstant(ctx); 
    }

    @Override 
    public Object visitBooleanConstant(BooleanConstantContext ctx) 
    { 
        return constantDefinitions.booleanConstant(ctx); 
    }

    @Override 
    public Object visitTypeDefinition(TypeDefinitionContext ctx) 
    {
        return typeDefinitions.typeDefinition(ctx);
    }

    @Override 
    public Object visitTypeIdentifier(TypeIdentifierContext ctx) 
    {
        Typespec_P6 pascalTypespec = ctx.identifier().typespec;
        String javaTypeName = javaTypeName(pascalTypespec);        
        code.emit(javaTypeName);
        
        return null;
    }
    
    @Override
    public Object visitEnumeratedType(EnumeratedTypeContext ctx)
    {
        return typeDefinitions.enumeratedType(ctx);
    }

    @Override 
    public Object visitVariableDeclarations(
                                VariableDeclarationsContext ctx) 
    { 
        return variableDeclarations.variableDeclarations(ctx);
    }

    @Override 
    public Object visitProcFuncDefinition(
                            ProcFuncDefinitionContext ctx) 
    {
        return program.procFuncDefinition(ctx);
    }
    
    @Override 
    public Object visitParameters(ParametersContext ctx) 
    {
        currentSeparator = "";
        return visit(ctx.parameterDeclarationsList());
    }

    @Override 
    public Object visitParameterDeclarations(
                            ParameterDeclarationsContext ctx) 
    {
        return program.parameterDeclarations(ctx);
    }
    
    @Override
    public Object visitCompoundStatement(CompoundStatementContext ctx) 
    {
        return statements.compoundStatement(ctx); 
    }
    
    @Override
    public Object visitStatementList(StatementListContext ctx) 
    {
        return statements.statementList(ctx);
    }
    
    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) 
    { 
        return statements.assignmentStatement(ctx); 
    }
    
    @Override
    public Object visitRepeatStatement(RepeatStatementContext ctx)
    {
        return statements.repeatStatement(ctx);
    }
    
    @Override
    public Object visitWriteStatement(WriteStatementContext ctx)
    {
        return statements.writeStatement(ctx);
    }
    
    @Override
    public Object visitWritelnStatement(WritelnStatementContext ctx)
    {
        return statements.writelnStatement(ctx);
    }
    
    @Override
    public Object visitWriteArguments(WriteArgumentsContext ctx)
    {
        return statements.writeArguments(ctx);
    }
    
    @Override 
    public Object visitExpression(ExpressionContext ctx) 
    { 
        return expressions.expression(ctx); 
    }

    @Override 
    public Object visitSimpleExpression(SimpleExpressionContext ctx) 
    { 
        return expressions.simpleExpression(ctx); 
    }

    @Override 
    public Object visitTerm(TermContext ctx) 
    { 
        return expressions.term(ctx); 
    }

    @Override 
    public Object visitVariable(VariableContext ctx) 
    { 
        return expressions.variable(ctx); 
    }

    @Override 
    public Object visitFunctionCall(FunctionCallContext ctx) 
    {
        return expressions.functionCall(ctx);
    }
    
    @Override
    public Object visitArgumentList(ArgumentListContext ctx)
    {
        return expressions.argumentList(ctx);
    }
    
    @Override
    public Object visitFactorNot(FactorNotContext ctx)
    {
        return expressions.factorNot(ctx);
    }
    
    @Override
    public Object visitFactorParens(FactorParensContext ctx)
    {
        return expressions.factorParens(ctx);
    }
}

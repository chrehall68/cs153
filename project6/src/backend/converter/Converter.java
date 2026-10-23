package backend.converter;

import java.util.Hashtable;
import java.util.Map;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.antlr4.Pcl_C2BaseVisitor;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec;
import intermediate.type.Typespec.Form;

import static intermediate.semantics.Semantics.programEntry;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;

public class Converter extends Pcl_C2BaseVisitor<Object>
{
    protected static JavaEmitter code;
    protected static boolean programVariables = true;
    protected static boolean recordFields = false;
    protected static String currentSeparator = "";
    protected static String programName;
    protected static Hashtable<String, String> typeNameTable;

    protected static Program     programImpl;  
    protected static Constants   constantsImpl;
    protected static Types       typesImpl;
    protected static Variables   variablesImpl;
    protected static Statements  statementsImpl;
    protected static Expressions expressionsImpl;

    static
    {
        programImpl     = new Program();
        constantsImpl   = new Constants();
        typesImpl       = new Types();
        variablesImpl   = new Variables();
        statementsImpl  = new Statements();
        expressionsImpl = new Expressions();
        
        typeNameTable = new Hashtable<>();
        typeNameTable.put("integer", "int");
        typeNameTable.put("real",    "double");
        typeNameTable.put("boolean", "boolean");
        typeNameTable.put("char",    "char");
        typeNameTable.put("string",  "String");
    }
    
    public String getObjectFileName() { return code.getObjectFileName(); }

    protected String javaTypeName(Typespec pascalTypespec)
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
                Typespec baseType = pascalTypespec.baseType();
                
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
                Typespec elmtType = pascalTypespec.getArrayBaseType();
                
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
     * Emit a record type definition for an unnamed record.
     * @param symtab the symbol table that can contain unnamed records.
     */
    protected void emitRecordDefinitions(Symtab symtab)
    {
        // Loop to look for names of unnamed record types.
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet())
        {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            Typespec typespec = entry.getTypespec();
            
            if (   (entry.getKind() == TYPE) 
                && (typespec.getForm() == RECORD))
            {
                if (programVariables) code.emitStart("private static ");
                else                  code.emitStart();
                
                code.emit("class " + entry.getName());
                emitRecordFields(typespec);
                code.emitLine();
            }
        }
    }
    
    protected void emitNewRecord(Typespec typespec)
    {
        String typeName = javaTypeName(typespec);
        code.emit(" = new " + typeName + "()");
    }
    
    protected void emitRecordFields(Typespec recordTypespec)
    {
        Symtab symtab = recordTypespec.getRecordSymtab();
        
        code.emitStart("{");
        code.indent();

        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet())
        {            
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            Typespec typespec = entry.getTypespec();
            String typeName = javaTypeName(typespec);
            Form typeForm = typespec.getForm();
            
            code.emitStart(typeName + " ");
            code.emit(entry.getName());
            
            if      (typeForm == ARRAY)  emitNewArray(typespec, true);
            else if (typeForm == RECORD) emitNewRecord(typespec);

            code.emitEnd(";");
        }
        
        code.dedent();
        code.emitLine("}");
    }
    
    protected Typespec emitNewArray(Typespec typespec,
                                       boolean bracketFlag)
    {
        String javaTypeName = javaTypeName(typespec);

        if (bracketFlag) emitArrayBrackets(typespec);
        code.emit(" = new " + javaTypeName);
        
        while (typespec.getForm() == ARRAY)
        {
            int elmtCount = typespec.getArrayElementCount();
            code.emit("[" + elmtCount + "]");
            typespec = typespec.getArrayElementType();
        }
        
        return typespec;
    }
    
    /**
     * Emit a pair of empty brackets for each dimension.
     * @param type the array data type.
     */
    protected void emitArrayBrackets(Typespec typespec)
    {
        String brackets = "";
        
        while (typespec.getForm() == ARRAY)
        {
            brackets += "[]";
            typespec = typespec.getArrayElementType();
        }
        
        code.emit(brackets);
    }
    
    protected void emitAllocateArray(Symtab symtab)
    {
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet())
        {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            Typespec variableTypespec = entry.getTypespec();
            Kind kind = entry.getKind();
            
            if (   (kind == VARIABLE) 
                && (variableTypespec.getForm() == ARRAY)
                && (arrayForm(variableTypespec) == RECORD))
                
            {
                String variableName = entry.getName();
                Typespec elmtTypespec = 
                        variableTypespec.getArrayElementType();
                
                if (elmtTypespec.isStructured())
                {
                    emitNewArrayElement(variableName, 
                                        variableTypespec);
                }
            }
        }
     }
    
    private Form arrayForm(Typespec typespec)
    {
        while (typespec.getForm() == ARRAY)
        {
            typespec = typespec.getArrayElementType();
        }
        
        return typespec.getForm();
    }
    
    /**
     * Emit code to allocate an array element.
     * @param lhsPrefix the prefix for the target variable name.
     * @param variableName the name of the target variable.
     * @param elmtType the element's datatype.
     */
    protected void emitNewArrayElement(String variableName, 
                                       Typespec elmtType)
    {        
        int dimensionCount = 0;

        do
        {
            int elmtCount = elmtType.getArrayElementCount();
            ++dimensionCount;
            String subscript = "_i" + dimensionCount;
            variableName += "[" + subscript + "]";
            
            code.emitLine("for (int " + subscript + " = 0; " +
                          subscript + " < " + elmtCount +
                          "; " + subscript + "++)");
            code.emitStart("{");
            code.indent();
            
            elmtType = elmtType.getArrayElementType();
        } while (elmtType.getForm() == ARRAY);
        
        String typeName = elmtType.getIdentifier().getName();
        code.emitStart(variableName + " = new " + typeName + "()");
        code.emitEnd(";");
        
        //emitNewRecordFields(lhsPrefix + variableName + ".", elmtType);
                    
        while (--dimensionCount >= 0) 
        {
            code.dedent();
            code.emitLine("}");
        }
    }

    @Override
    public Object visitProgram(ProgramContext ctx)
    {
        programName = programEntry.getName();
        code = new JavaEmitter(programName, "java");

        return programImpl.program(ctx);
    }

    @Override 
    public Object visitConstantDefinition(ConstantDefinitionContext ctx) 
    {
        return constantsImpl.constantDefinition(ctx);
    }
    
    @Override
    public Object visitConstantSignedIdentifier(ConstantSignedIdentifierContext ctx)
    {
        return constantsImpl.constantSignedIdentifier(ctx);
    }
    
    @Override 
    public Object visitConstantIdentifier(ConstantIdentifierContext ctx) 
    { 
        return constantsImpl.constantIdentifier(ctx); 
    }

    @Override 
    public Object visitIntegerConstant(IntegerConstantContext ctx)
    { 
        return constantsImpl.integerConstant(ctx); 
    }

    @Override 
    public Object visitRealConstant(RealConstantContext ctx)
    { 
        return constantsImpl.realConstant(ctx); 
    }

    @Override 
    public Object visitCharacterConstant(CharacterConstantContext ctx) 
    { 
        return constantsImpl.characterConstant(ctx); 
    }

    @Override 
    public Object visitStringConstant(StringConstantContext ctx) 
    { 
        return constantsImpl.stringConstant(ctx); 
    }

    @Override 
    public Object visitBooleanConstant(BooleanConstantContext ctx) 
    { 
        return constantsImpl.booleanConstant(ctx); 
    }

    @Override 
    public Object visitTypeDefinition(TypeDefinitionContext ctx) 
    {
        return typesImpl.typeDefinition(ctx);
    }

    @Override 
    public Object visitTypeIdentifier(TypeIdentifierContext ctx) 
    {
        Typespec pascalTypespec = ctx.identifier().typespec;
        String javaTypeName = javaTypeName(pascalTypespec);        
        code.emit(javaTypeName);
        
        return null;
    }
    
    @Override
    public Object visitEnumeratedType(EnumeratedTypeContext ctx)
    {
        return typesImpl.enumeratedType(ctx);
    }
    
    @Override
    public Object visitRecordType(RecordTypeContext ctx)
    {
        return typesImpl.recordType(ctx);
    }

    @Override 
    public Object visitVariableDeclarations(
                                VariableDeclarationsContext ctx) 
    { 
        return variablesImpl.variableDeclarations(ctx);
    }

    @Override 
    public Object visitProcFuncDefinition(
                            ProcFuncDefinitionContext ctx) 
    {
        return programImpl.procFuncDefinition(ctx);
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
        return programImpl.parameterDeclarations(ctx);
    }
    
    @Override
    public Object visitCompoundStatement(CompoundStatementContext ctx) 
    {
        return statementsImpl.compoundStatement(ctx); 
    }
    
    @Override
    public Object visitStatementList(StatementListContext ctx) 
    {
        return statementsImpl.statementList(ctx);
    }
    
    @Override
    public Object visitAssignmentStatement(AssignmentStatementContext ctx) 
    { 
        return statementsImpl.assignmentStatement(ctx); 
    }
    
    @Override
    public Object visitRepeatStatement(RepeatStatementContext ctx)
    {
        return statementsImpl.repeatStatement(ctx);
    }
    
    @Override
    public Object visitWhileStatement(WhileStatementContext ctx)
    {
        return statementsImpl.whileStatement(ctx);
    }
    
    @Override
    public Object visitForStatement(ForStatementContext ctx)
    {
        return statementsImpl.forStatement(ctx);
    }
    
    @Override
    public Object visitIfStatement(IfStatementContext ctx)
    {
        return statementsImpl.ifStatement(ctx);
    }
    
    @Override
    public Object visitCaseStatement(CaseStatementContext ctx)
    {
        return statementsImpl.caseStatement(ctx);
    }
    
    @Override
    public Object visitCaseBranch(CaseBranchContext ctx)
    {
        return statementsImpl.caseBranch(ctx);
    }

    @Override 
    public Object visitProcedureCallStatement(ProcedureCallStatementContext ctx) 
    {
        return statementsImpl.procedureCallStatement(ctx);
    }
    
    @Override
    public Object visitWriteStatement(WriteStatementContext ctx)
    {
        return statementsImpl.writeStatement(ctx);
    }
    
    @Override
    public Object visitWritelnStatement(WritelnStatementContext ctx)
    {
        return statementsImpl.writelnStatement(ctx);
    }
    
    @Override
    public Object visitWriteArguments(WriteArgumentsContext ctx)
    {
        return statementsImpl.writeArguments(ctx);
    }
    
    @Override 
    public Object visitExpression(ExpressionContext ctx) 
    { 
        return expressionsImpl.expression(ctx); 
    }

    @Override 
    public Object visitSimpleExpression(SimpleExpressionContext ctx) 
    { 
        return expressionsImpl.simpleExpression(ctx); 
    }

    @Override 
    public Object visitTerm(TermContext ctx) 
    { 
        return expressionsImpl.term(ctx); 
    }

    @Override 
    public Object visitVariable(VariableContext ctx) 
    { 
        return expressionsImpl.variable(ctx); 
    }

    @Override 
    public Object visitFunctionCall(FunctionCallContext ctx) 
    {
        return expressionsImpl.functionCall(ctx);
    }
    
    @Override
    public Object visitArgumentList(ArgumentListContext ctx)
    {
        return expressionsImpl.argumentList(ctx);
    }
    
    @Override
    public Object visitFactorNot(FactorNotContext ctx)
    {
        return expressionsImpl.factorNot(ctx);
    }
    
    @Override
    public Object visitFactorParens(FactorParensContext ctx)
    {
        return expressionsImpl.factorParens(ctx);
    }
}

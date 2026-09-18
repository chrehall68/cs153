package backend.converter;

import java.util.Hashtable;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.semantics.Semantics_P2;
import intermediate.antlr4.Pcl_P2BaseVisitor;

public class Converter_P2 extends Pcl_P2BaseVisitor<Object>
{
    protected static JavaEmitter code;
    protected static String programName;
    protected static Hashtable<String, String> typeNameTable;

    protected static ProgramDeclarations_P2  programDeclarations;  
    protected static ConstantDefinitions_P2  constantDefinitions;
    protected static TypeDefinitions_P2      typeDefinitions;
    protected static VariableDeclarations_P2 variableDeclarations;

    static
    {
        programDeclarations  = new ProgramDeclarations_P2();
        constantDefinitions  = new ConstantDefinitions_P2();
        typeDefinitions      = new TypeDefinitions_P2();
        variableDeclarations = new VariableDeclarations_P2();
        
        typeNameTable = new Hashtable<>();
        typeNameTable.put("integer", "int");
        typeNameTable.put("real",    "double");
        typeNameTable.put("boolean", "boolean");
        typeNameTable.put("char",    "char");
        typeNameTable.put("string",  "String");
    }
    
    public String getObjectFileName() { return code.getObjectFileName(); }

    @Override
    public Object visitProgram(ProgramContext ctx)
    {
        programName = Semantics_P2.programEntry.getName();
        code = new JavaEmitter(programName, "java");

        return programDeclarations.program(ctx);
    }

    @Override 
    public Object visitConstantDefinition(ConstantDefinitionContext ctx) 
    {
        return constantDefinitions.constantDefinition(ctx);
    }

    @Override 
    public Object visitTypeDefinition(TypeDefinitionContext ctx) 
    {
        return typeDefinitions.typeDefinition(ctx);
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
}

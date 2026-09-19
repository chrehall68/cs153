package intermediate.semantics;

import intermediate.antlr4.Pcl_P2BaseVisitor;
import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;

public class Semantics_P2 extends Pcl_P2BaseVisitor<Object> {
    public static SymtabEntry programEntry;

    protected static Symtab symtab;
    protected static SemanticErrorHandler error;

    protected static ProgramDeclarations_P2 programDeclarations;
    protected static ConstantDefinitions_P2 constantDefinitions;
    protected static TypeDefinitions_P2 typeDefinitions;
    protected static VariableDeclarations_P2 variableDeclarations;

    static {
        symtab = new Symtab();
        error = new SemanticErrorHandler();

        programDeclarations = new ProgramDeclarations_P2();
        constantDefinitions = new ConstantDefinitions_P2();
        typeDefinitions = new TypeDefinitions_P2();
        variableDeclarations = new VariableDeclarations_P2();

        Predefined.initialize(symtab);
    }

    public Symtab getSymtab() {
        return symtab;
    }

    public int getErrorCount() {
        return error.getCount();
    }

    @Override
    public Object visitProgram(ProgramContext ctx) {
        return programDeclarations.program(ctx);
    }

    @Override
    public Object visitProgramHeader(ProgramHeaderContext ctx) {
        programEntry = (SymtabEntry) programDeclarations.programHeader(ctx);
        return null;
    }

    @Override
    public Object visitConstantDefinition(ConstantDefinitionContext ctx) {
        return constantDefinitions.constantDefinition(ctx);
    }

    @Override
    public Object visitConstant(ConstantContext ctx) {
        return constantDefinitions.constant(ctx);
    }

    @Override
    public Object visitConstantIdentifier(ConstantIdentifierContext ctx) {
        return constantDefinitions.constantIdentifier(ctx);
    }

    @Override
    public Object visitIntegerConstant(IntegerConstantContext ctx) {
        return constantDefinitions.integerConstant(ctx);
    }

    @Override
    public Object visitRealConstant(RealConstantContext ctx) {
        return constantDefinitions.realConstant(ctx);
    }

    @Override
    public Object visitCharacterConstant(CharacterConstantContext ctx) {
        return constantDefinitions.characterConstant(ctx);
    }

    @Override
    public Object visitStringConstant(StringConstantContext ctx) {
        return constantDefinitions.stringConstant(ctx);
    }

    @Override
    public Object visitBooleanConstant(BooleanConstantContext ctx) {
        return constantDefinitions.booleanConstant(ctx);
    }

    @Override
    public Object visitTypeDefinition(TypeDefinitionContext ctx) {
        return typeDefinitions.typeDefinition(ctx);
    }

    @Override
    public Object visitTypeSpecification(TypeSpecificationContext ctx) {
        return typeDefinitions.typeSpecification(ctx);
    }

    @Override
    public Object visitTypeIdentifier(TypeIdentifierContext ctx) {
        return typeDefinitions.typeIdentifier(ctx);
    }

    @Override
    public Object visitEnumeratedType(EnumeratedTypeContext ctx) {
        return typeDefinitions.enumeratedType(ctx);
    }

    @Override
    public Object visitSubrangeType(SubrangeTypeContext ctx) {
        return typeDefinitions.subrangeType(ctx);
    }

    @Override
    public Object visitArrayType(ArrayTypeContext ctx) {
        return typeDefinitions.arrayType(ctx);
    }

    @Override
    public Object visitSetType(SetTypeContext ctx) {return typeDefinitions.setType(ctx);}

    @Override
    public Object visitVariableDeclarations(VariableDeclarationsContext ctx) {
        return variableDeclarations.variableDeclarations(ctx);
    }
}

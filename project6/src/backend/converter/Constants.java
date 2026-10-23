package backend.converter;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.type.Typespec;

public class Constants extends Converter {
    Object constantDefinition(ConstantDefinitionContext ctx) {
        ConstantContext constantCtx = ctx.constant();
        IdentifierContext idCtx = ctx.identifier();
        String constantName = idCtx.entry.getName().toUpperCase();
        Typespec typespec = constantCtx.typespec;
        String pascalTypeName = typespec.getIdentifier().getName();
        String javaTypeName = typeNameTable.get(pascalTypeName);

        code.emitStart();
        code.emit("private static final ");
        code.emit(javaTypeName + " " + constantName + " = ");
        visit(constantCtx);
        code.emitEnd(";");

        return null;
    }

    Object constantSignedIdentifier(ConstantSignedIdentifierContext ctx) {
        SignContext signCtx = ctx.sign();
        if ((signCtx != null) && (signCtx.getText().equals("-"))) {
            code.emit("-");
        }

        code.emit(ctx.constantIdentifier().identifier().entry.getName());
        return null;
    }

    Object constantIdentifier(ConstantIdentifierContext ctx) {
        IdentifierContext idCtx = ctx.identifier();

        if (idCtx.typespec == Predefined.booleanType) {
            code.emit(((Integer) idCtx.value) == 1 ? "true" : "false");
        } else {
            code.emit(idCtx.entry.getName());
        }

        return null;
    }

    Object integerConstant(IntegerConstantContext ctx) {
        code.emit(ctx.value.toString());
        return null;
    }

    Object realConstant(RealConstantContext ctx) {
        code.emit(ctx.value.toString());
        return null;
    }

    Object characterConstant(CharacterConstantContext ctx) {
        code.emit("'" + ctx.value + "'");
        return null;
    }

    Object stringConstant(StringConstantContext ctx) {
        code.emit("\"" + ctx.value + "\"");
        return null;
    }

    Object booleanConstant(BooleanConstantContext ctx) {
        code.emit(String.valueOf(ctx.TRUE() != null));
        return null;
    }
}

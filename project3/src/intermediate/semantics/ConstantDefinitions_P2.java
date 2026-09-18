package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec_P2;

public class ConstantDefinitions_P2 extends Semantics_P2 {
    Object constantDefinition(ConstantDefinitionContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String constantName = idCtx.getText();
        SymtabEntry constantEntry = symtab.lookup(constantName);

        if (constantEntry == null) {
            ConstantContext constCtx = ctx.constant();
            Typespec_P2 typespec = (Typespec_P2) visit(constCtx);
            Object value = constCtx.value;

            constantEntry = symtab.enter(constantName, CONSTANT);
            constantEntry.setValue(value);
            constantEntry.setTypespec(typespec);

            idCtx.entry = constantEntry;
            idCtx.value = value;
            idCtx.typespec = constCtx.typespec = typespec;
        } else {
            error.flag(REDECLARED_IDENTIFIER, idCtx);
        }

        constantEntry.appendLineNumber(ctx.getStart().getLine());
        return null;
    }

    Object constant(ConstantContext ctx) {
        SignContext signCtx = ctx.sign();
        ConstantIdentifierContext constIdCtx = ctx.constantIdentifier();
        UnsignedConstantContext unsignedConstCtx = ctx.unsignedConstant();
        CharacterConstantContext charConstCtx = ctx.characterConstant();
        StringConstantContext stringConstCtx = ctx.stringConstant();
        BooleanConstantContext booleanConstCtx = ctx.booleanConstant();

        Typespec_P2 typespec;

        if (constIdCtx != null) {
            typespec = (Typespec_P2) visit(constIdCtx);
            ctx.value = constIdCtx.value;
        } else if (unsignedConstCtx != null) {
            IntegerConstantContext integerConstCtx = unsignedConstCtx.integerConstant();
            RealConstantContext realConstCtx = unsignedConstCtx.realConstant();

            if (integerConstCtx != null) {
                typespec = (Typespec_P2) visit(integerConstCtx);
                ctx.value = integerConstCtx.value;
            } else {
                typespec = (Typespec_P2) visit(realConstCtx);
                ctx.value = realConstCtx.value;
            }
        } else if (charConstCtx != null) {
            typespec = (Typespec_P2) visit(charConstCtx);
            ctx.value = charConstCtx.value;
        } else if (stringConstCtx != null) {
            typespec = (Typespec_P2) visit(stringConstCtx);
            ctx.value = stringConstCtx.value;
        } else {
            typespec = (Typespec_P2) visit(booleanConstCtx);
            ctx.value = booleanConstCtx.value;
        }

        if (signCtx != null) {
            if (typespec == Predefined.integerType) {
                ctx.value = -(Integer) ctx.value;
            } else if (typespec == Predefined.realType) {
                ctx.value = -(Double) ctx.value;
            } else {
                error.flag(INVALID_SIGN, signCtx);
            }
        }

        ctx.typespec = typespec;
        return ctx.typespec;
    }

    Object constantIdentifier(ConstantIdentifierContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String idName = idCtx.getText();
        SymtabEntry idEntry = symtab.lookup(idName);
        int lineNumber = idCtx.start.getLine();

        if (idEntry != null) {
            Kind kind = idEntry.getKind();
            if ((kind == CONSTANT) || (kind == ENUMERATED_CONSTANT)) {
                ctx.typespec = idCtx.typespec = idEntry.getTypespec();
                ctx.value = idCtx.value = idEntry.getValue();
            } else {
                error.flag(INVALID_CONSTANT, idCtx);
                ctx.typespec = idCtx.typespec = Predefined.undefinedType;
                ctx.value = idCtx.value = 0;
            }

            idEntry.appendLineNumber(lineNumber);
        } else {
            error.flag(UNDECLARED_IDENTIFIER, idCtx);

            SymtabEntry unknownEntry = symtab.enter(idName, CONSTANT);
            unknownEntry.setValue(0);
            unknownEntry.setTypespec(Predefined.undefinedType);
            unknownEntry.appendLineNumber(lineNumber);

            ctx.value = idCtx.typespec = Predefined.undefinedType;
            ctx.value = idCtx.value = 0;
        }

        return idCtx.typespec;
    }

    Object integerConstant(IntegerConstantContext ctx) {
        ctx.value = Integer.parseInt(ctx.getText());
        ctx.typespec = Predefined.integerType;

        return ctx.typespec;
    }

    Object realConstant(RealConstantContext ctx) {
        ctx.value = Double.parseDouble(ctx.getText());
        ctx.typespec = Predefined.realType;

        return ctx.typespec;
    }

    Object characterConstant(CharacterConstantContext ctx) {
        String str = ctx.getText();

        str = str.substring(1, str.length() - 1).replace("''''", "'\''").replace("\"", "\\\"");

        ctx.value = str.charAt(0);
        ctx.typespec = Predefined.charType;

        return ctx.typespec;
    }

    Object stringConstant(StringConstantContext ctx) {
        String str = ctx.getText();

        ctx.value = str.substring(1, str.length() - 1).replace("''", "'").replace("\"", "\\\"");
        ctx.typespec = Predefined.stringType;

        return ctx.typespec;
    }

    Object booleanConstant(BooleanConstantContext ctx) {
        ctx.value = ctx.TRUE() != null ? 1 : 0;
        ctx.typespec = Predefined.booleanType;

        return ctx.typespec;
    }
}

package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec_P6;

public class ConstantDefinitions_P6 extends Semantics_P6 {
    Typespec_P6 constantDefinition(ConstantDefinitionContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String constantName = idCtx.getText();
        SymtabEntry constantEntry = symtabStack.lookupLocal(constantName);

        if (constantEntry == null) {
            ConstantContext constCtx = ctx.constant();
            Typespec_P6 typespec = (Typespec_P6) visit(constCtx);
            Object value = constCtx.value;

            constantEntry = symtabStack.enterLocal(constantName, CONSTANT);
            constantEntry.setValue(value);
            constantEntry.setTypespec(typespec);

            idCtx.entry = constantEntry;
            idCtx.value = constCtx.value = value;
            idCtx.typespec = constCtx.typespec = typespec;
        } else {
            error.flag(REDECLARED_IDENTIFIER, idCtx);
        }

        constantEntry.appendLineNumber(ctx.getStart().getLine());
        return null;
    }

    Typespec_P6 constantSignedIdentifier(ConstantSignedIdentifierContext ctx) {
        SignContext signCtx = ctx.sign();
        ConstantIdentifierContext ctxConstIdCtx = ctx.constantIdentifier();
        ctx.typespec = ctxConstIdCtx.typespec = constantIdentifier(ctxConstIdCtx);
        ctx.value = ctxConstIdCtx.value;

        if ((signCtx != null) && (signCtx.getText().equals("-"))) {
            ctx.value = ctxConstIdCtx.value = -(int) ctx.value;
        }

        return ctx.typespec;
    }

    Typespec_P6 constantSignedInteger(ConstantSignedIntegerContext ctx) {
        SignContext signCtx = ctx.sign();
        IntegerConstantContext integerConstCtx = ctx.integerConstant();
        ctx.typespec = integerConstCtx.typespec = integerConstant(integerConstCtx);
        ctx.value = integerConstCtx.value;

        if ((signCtx != null) && (signCtx.getText().equals("-"))) {
            ctx.value = integerConstCtx.value = -(int) ctx.value;
        }

        return ctx.typespec;
    }

    Typespec_P6 constantSignedReal(ConstantSignedRealContext ctx) {
        SignContext signCtx = ctx.sign();
        RealConstantContext realConstCtx = ctx.realConstant();
        ctx.typespec = realConstCtx.typespec = realConstant(realConstCtx);
        ctx.value = realConstCtx.value;

        if ((signCtx != null) && (signCtx.getText().equals("-"))) {
            ctx.value = realConstCtx.value = -(double) ctx.value;
        }

        return ctx.typespec;
    }

    Typespec_P6 constantCharacter(ConstantCharacterContext ctx) {
        CharacterConstantContext characterConstCtx = ctx.characterConstant();
        ctx.typespec = characterConstCtx.typespec = characterConstant(characterConstCtx);
        ctx.value = characterConstCtx.value;

        return ctx.typespec;
    }

    Typespec_P6 constantString(ConstantStringContext ctx) {
        StringConstantContext stringConstCtx = ctx.stringConstant();
        ctx.typespec = stringConstCtx.typespec = stringConstant(stringConstCtx);
        ctx.value = stringConstCtx.value;

        return ctx.typespec;
    }

    Typespec_P6 constantBoolean(ConstantBooleanContext ctx) {
        BooleanConstantContext booleanConstCtx = ctx.booleanConstant();
        ctx.typespec = booleanConstCtx.typespec = booleanConstant(booleanConstCtx);
        ctx.value = booleanConstCtx.value;

        return ctx.typespec;
    }

    Typespec_P6 constantIdentifier(ConstantIdentifierContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String idName = idCtx.getText();
        SymtabEntry idEntry = symtabStack.lookup(idName);
        int lineNumber = idCtx.start.getLine();

        if (idEntry != null) {
            Kind kind = idEntry.getKind();
            if ((kind == CONSTANT) || (kind == ENUMERATED_CONSTANT)) {
                idCtx.entry = idEntry;
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

            SymtabEntry unknownEntry = symtabStack.enterLocal(idName, CONSTANT);
            unknownEntry.setValue(0);
            unknownEntry.setTypespec(Predefined.undefinedType);
            unknownEntry.appendLineNumber(lineNumber);

            ctx.value = idCtx.typespec = Predefined.undefinedType;
            ctx.value = idCtx.value = 0;
        }

        return idCtx.typespec;
    }

    Typespec_P6 integerConstant(IntegerConstantContext ctx) {
        ctx.value = Integer.parseInt(ctx.getText());
        ctx.typespec = Predefined.integerType;

        return ctx.typespec;
    }

    Typespec_P6 realConstant(RealConstantContext ctx) {
        ctx.value = Double.parseDouble(ctx.getText());
        ctx.typespec = Predefined.realType;

        return ctx.typespec;
    }

    Typespec_P6 characterConstant(CharacterConstantContext ctx) {
        String str = ctx.getText();

        str = str.substring(1, str.length() - 1).replace("''''", "'\''").replace("\"", "\\\"");

        ctx.value = str.charAt(0);
        ctx.typespec = Predefined.charType;

        return ctx.typespec;
    }

    Typespec_P6 stringConstant(StringConstantContext ctx) {
        String str = ctx.getText();

        ctx.value = str.substring(1, str.length() - 1).replace("''", "'").replace("\"", "\\\"");
        ctx.typespec = Predefined.stringType;

        return ctx.typespec;
    }

    Typespec_P6 booleanConstant(BooleanConstantContext ctx) {
        ctx.value = ctx.TRUE() != null ? 1 : 0;
        ctx.typespec = Predefined.booleanType;

        return ctx.typespec;
    }
}

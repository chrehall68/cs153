package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec_P2.Form.*;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec_P2;

import java.util.ArrayList;

public class TypeDefinitions_P2 extends Semantics_P2 {
    Typespec_P2 typeDefinition(TypeDefinitionContext ctx) {
        IdentifierContext typeIdCtx = ctx.identifier();
        String typeName = typeIdCtx.getText();
        SymtabEntry typeEntry = symtab.lookup(typeName);

        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec_P2 typespec = null;

        if (typeEntry == null) {
            typespec = (Typespec_P2) visit(typespecCtx);

            typeEntry = symtab.enter(typeName, TYPE);
            typeEntry.setTypespec(typespec);

            if (typespec.getIdentifier() == null) {
                typespec.setIdentifier(typeEntry);
            }
        } else {
            error.flag(REDECLARED_IDENTIFIER, typeIdCtx);
            typespec = Predefined.undefinedType;
        }

        typeIdCtx.entry = typeEntry;
        typeIdCtx.typespec = typespecCtx.typespec = typespec;

        typeEntry.appendLineNumber(ctx.getStart().getLine());
        return typespec;
    }

    Typespec_P2 typeSpecification(TypeSpecificationContext ctx) {
        ctx.typespec = (Typespec_P2) visitChildren(ctx);
        return ctx.typespec;
    }

    Typespec_P2 typeIdentifier(TypeIdentifierContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String typeName = idCtx.getText();
        SymtabEntry typeEntry = symtab.lookup(typeName);

        if (typeEntry != null) {
            if (typeEntry.getKind() == TYPE) {
                idCtx.typespec = typeEntry.getTypespec();
            } else {
                error.flag(INVALID_TYPE, ctx);
                idCtx.typespec = Predefined.undefinedType;
            }

            typeEntry.appendLineNumber(ctx.start.getLine());
            idCtx.entry = typeEntry;
        } else {
            error.flag(UNDECLARED_IDENTIFIER, ctx);

            SymtabEntry unknownEntry = symtab.enter(typeName, TYPE);
            unknownEntry.setValue(0);
            unknownEntry.setTypespec(Predefined.undefinedType);
            unknownEntry.appendLineNumber(ctx.start.getLine());

            idCtx.entry = unknownEntry;
            idCtx.typespec = Predefined.undefinedType;
        }

        return idCtx.typespec;
    }

    Typespec_P2 enumeratedType(EnumeratedTypeContext ctx) {
        Typespec_P2 enumTypespec = new Typespec_P2(ENUMERATED);
        ArrayList<SymtabEntry> constants = new ArrayList<>();
        int value = -1;

        for (IdentifierContext constIdCtx : ctx.identifier()) {
            String constantName = constIdCtx.getText();
            SymtabEntry constantEntry = symtab.lookup(constantName);

            if (constantEntry == null) {
                constantEntry = symtab.enter(constantName, ENUMERATED_CONSTANT);
                constantEntry.setTypespec(enumTypespec);
                constantEntry.setValue(++value);

                constants.add(constantEntry);
            } else {
                error.flag(REDECLARED_IDENTIFIER, constIdCtx);
            }

            constIdCtx.entry = constantEntry;
            constIdCtx.typespec = enumTypespec;

            constantEntry.appendLineNumber(ctx.getStart().getLine());
        }

        enumTypespec.setEnumeratedConstants(constants);
        ctx.typespec = enumTypespec;

        return enumTypespec;
    }

    Typespec_P2 subrangeType(SubrangeTypeContext ctx) {
        Typespec_P2 subrangeTypespec = new Typespec_P2(SUBRANGE);

        ConstantContext minCtx = ctx.constant().get(0);
        ConstantContext maxCtx = ctx.constant().get(1);

        Typespec_P2 minTypespec = (Typespec_P2) visit(minCtx);
        Typespec_P2 maxTypespec = (Typespec_P2) visit(maxCtx);

        int minValue = 0;
        int maxValue = 0;
        boolean badMin = false;
        boolean badMax = false;

        if (!minTypespec.isOrdinal()) badMin = true;
        if (!maxTypespec.isOrdinal()) badMax = true;
        if (minTypespec != maxTypespec) badMax = true;

        if (!(badMin || badMax)) {
            if ((minTypespec == Predefined.integerType) || (minTypespec.getForm() == ENUMERATED)) {
                minValue = (Integer) minCtx.value;
                maxValue = (Integer) maxCtx.value;
            } else {
                minValue = (Character) minCtx.value;
                maxValue = (Character) maxCtx.value;
            }
        }

        if (minValue > maxValue) {
            error.flag(INVALID_SUBRANGE, ctx);
            badMin = badMax = true;
        } else if (badMin || badMax) {
            error.flag(INVALID_SUBRANGE_CONSTANT, badMin ? minCtx : maxCtx);
        }

        if (badMin || badMax) {
            maxTypespec = minTypespec = Predefined.integerType;
        }

        subrangeTypespec.setSubrangeBaseType(minTypespec);
        subrangeTypespec.setSubrangeMinValue(minCtx.value);
        subrangeTypespec.setSubrangeMaxValue(maxCtx.value);

        ctx.typespec = subrangeTypespec;
        return subrangeTypespec;
    }

    Typespec_P2 arrayType(ArrayTypeContext ctx) {
        DimensionListContext dimListCtx = ctx.dimensionList();
        int dimensionsCount = dimListCtx.indexType().size();

        if (ctx.PACKED() != null) {
            return packedArray(ctx, dimensionsCount);
        }

        Typespec_P2 arrayTypespec = new Typespec_P2(ARRAY);
        ctx.typespec = arrayTypespec;

        for (int i = 0; i < dimensionsCount; i++) {
            IndexTypeContext indexCtx = dimListCtx.indexType().get(i);
            Typespec_P2 indexTypespec = (Typespec_P2) visit(indexCtx);

            if (!indexTypespec.isOrdinal()) {
                error.flag(INVALID_INDEX_TYPE, indexCtx);
                indexTypespec = Predefined.booleanType;
            }

            arrayTypespec.setArrayIndexType(indexTypespec);
            arrayTypespec.setArrayElementCount(elementCount(indexTypespec));

            if (i < dimensionsCount - 1) {
                Typespec_P2 elmtTypespec = new Typespec_P2(ARRAY);
                arrayTypespec.setArrayElementType(elmtTypespec);
                arrayTypespec = elmtTypespec;
            }
        }

        Typespec_P2 elmtTypespec = (Typespec_P2) visit(ctx.elmtType());
        arrayTypespec.setArrayElementType(elmtTypespec);

        return ctx.typespec;
    }

    Typespec_P2 hashtableType(HashtableTypeContext ctx) {
        Typespec_P2 hashtableTypespec = new Typespec_P2(HASHTABLE);
        ctx.typespec = hashtableTypespec;

        // Defensive: malformed hashtables (missing key/value after syntax
        // recovery) may have fewer than two typeSpecification children,
        // and visit() may return null. Fall back to undefinedType so that
        // later passes never see null.
        int n = ctx.typeSpecification().size();
        Typespec_P2 keyType = (n > 0) ? (Typespec_P2) visit(ctx.typeSpecification(0)) : null;
        Typespec_P2 elementType = (n > 1) ? (Typespec_P2) visit(ctx.typeSpecification(1)) : null;

        if (keyType == null) {
            keyType = Predefined.undefinedType;
        }
        else if (!keyType.isOrdinal()) {
            error.flag(INVALID_HASHTABLE_KEY_TYPE, ctx.typeSpecification(0));
            keyType = Predefined.undefinedType;
        }

        if (elementType == null) {
            elementType = Predefined.undefinedType;
        }

        hashtableTypespec.setHashtableKeyType(keyType);
        hashtableTypespec.setHashtableElementType(elementType);
        return hashtableTypespec;
    }

    private int elementCount(Typespec_P2 typespec) {
        int count = 0;

        if (typespec.getForm() == ENUMERATED) {
            ArrayList<SymtabEntry> constants = typespec.getEnumeratedConstants();
            count = constants.size();
        } else if (typespec.baseType() == Predefined.charType) {
            int minValue = ((Character) typespec.getSubrangeMinValue()).charValue();
            int maxValue = ((Character) typespec.getSubrangeMaxValue()).charValue();
            count = maxValue - minValue + 1;
        } else {
            int minValue = (Integer) typespec.getSubrangeMinValue();
            int maxValue = (Integer) typespec.getSubrangeMaxValue();
            count = maxValue - minValue + 1;
        }

        return count;
    }

    private Typespec_P2 packedArray(ArrayTypeContext ctx, int dimensionsCount) {
        ElmtTypeContext elmtTypeCtx = ctx.elmtType();
        Typespec_P2 elmtTypespec = (Typespec_P2) visit(elmtTypeCtx);

        if ((elmtTypespec == Predefined.charType) && (dimensionsCount == 1)) {
            return Predefined.stringType;
        } else {
            error.flag(INVALID_PACKED_ARRAY, elmtTypeCtx);
            ctx.typespec = Predefined.undefinedType;

            return Predefined.undefinedType;
        }
    }
}

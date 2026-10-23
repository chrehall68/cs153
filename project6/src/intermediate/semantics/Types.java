package intermediate.semantics;

import static intermediate.semantics.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;

import intermediate.antlr4.Pcl_C2Parser.*;
import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;
import intermediate.type.Typespec;

import java.util.ArrayList;

public class Types extends Semantics {
    Typespec typeDefinition(TypeDefinitionContext ctx) {
        IdentifierContext typeIdCtx = ctx.identifier();
        String typeName = typeIdCtx.getText();
        SymtabEntry typeEntry = symtabStack.lookupLocal(typeName);

        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec typespec = null;

        if (typeEntry == null) {
            typespec = (Typespec) visit(typespecCtx);

            // Removed the unnamed record type.
            if (typespec.getForm() == RECORD) {
                String unname = typespec.getIdentifier().getName();
                symtabStack.removeLocal(unname);
                typespec.setIdentifier(null);
            }

            typeEntry = symtabStack.enterLocal(typeName, TYPE);
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

    Typespec typeSpecification(TypeSpecificationContext ctx) {
        ctx.typespec = (Typespec) visitChildren(ctx);
        return ctx.typespec;
    }

    Typespec typeIdentifier(TypeIdentifierContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String typeName = idCtx.getText();
        SymtabEntry typeEntry = symtabStack.lookup(typeName);

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

            SymtabEntry unknownEntry = symtabStack.enterLocal(typeName, TYPE);
            unknownEntry.setValue(0);
            unknownEntry.setTypespec(Predefined.undefinedType);
            unknownEntry.appendLineNumber(ctx.start.getLine());

            idCtx.entry = unknownEntry;
            idCtx.typespec = Predefined.undefinedType;
        }

        return idCtx.typespec;
    }

    Typespec enumeratedType(EnumeratedTypeContext ctx) {
        Typespec enumTypespec = new Typespec(ENUMERATED);
        ArrayList<SymtabEntry> constants = new ArrayList<>();
        int value = -1;

        for (IdentifierContext constIdCtx : ctx.identifier()) {
            String constantName = constIdCtx.getText();
            SymtabEntry constantEntry = symtabStack.lookupLocal(constantName);

            if (constantEntry == null) {
                constantEntry = symtabStack.enterLocal(constantName, ENUMERATED_CONSTANT);
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

    Typespec subrangeType(SubrangeTypeContext ctx) {
        Typespec subrangeTypespec = new Typespec(SUBRANGE);

        ConstantContext minCtx = ctx.constant().get(0);
        ConstantContext maxCtx = ctx.constant().get(1);

        Typespec minTypespec = (Typespec) visit(minCtx);
        Typespec maxTypespec = (Typespec) visit(maxCtx);

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

    Typespec arrayType(ArrayTypeContext ctx) {
        DimensionListContext dimListCtx = ctx.dimensionList();
        int dimensionsCount = dimListCtx.indexType().size();

        if (ctx.PACKED() != null) {
            return packedArray(ctx, dimensionsCount);
        }

        Typespec arrayTypespec = new Typespec(ARRAY);
        ctx.typespec = arrayTypespec;

        for (int i = 0; i < dimensionsCount; i++) {
            IndexTypeContext indexCtx = dimListCtx.indexType().get(i);
            Typespec indexTypespec = (Typespec) visit(indexCtx);

            if (!indexTypespec.isOrdinal()) {
                error.flag(INVALID_INDEX_TYPE, indexCtx);
                indexTypespec = Predefined.booleanType;
            }

            arrayTypespec.setArrayIndexType(indexTypespec);
            arrayTypespec.setArrayElementCount(elementCount(indexTypespec));

            if (i < dimensionsCount - 1) {
                Typespec elmtTypespec = new Typespec(ARRAY);
                arrayTypespec.setArrayElementType(elmtTypespec);
                arrayTypespec = elmtTypespec;
            }
        }

        Typespec elmtTypespec = (Typespec) visit(ctx.elmtType());
        arrayTypespec.setArrayElementType(elmtTypespec);

        return ctx.typespec;
    }

    private int elementCount(Typespec typespec) {
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

    private Typespec packedArray(ArrayTypeContext ctx, int dimensionsCount) {
        ElmtTypeContext elmtTypeCtx = ctx.elmtType();
        Typespec elmtTypespec = (Typespec) visit(elmtTypeCtx);

        if ((elmtTypespec == Predefined.charType) && (dimensionsCount == 1)) {
            return Predefined.stringType;
        } else {
            error.flag(INVALID_PACKED_ARRAY, elmtTypeCtx);
            ctx.typespec = Predefined.undefinedType;

            return Predefined.undefinedType;
        }
    }

    Typespec recordType(RecordTypeContext ctx) {
        RecordTypeContext recordTypeCtx = ctx;
        Typespec recordTypespec = new Typespec(RECORD);

        Symtab recordSymtab = createRecordSymtab(recordTypeCtx.recordFields());
        recordTypespec.setRecordSymtab(recordSymtab);

        // Assume it's unnamed and generate a name for it.
        String recordTypeName = Symtab.generateUnnamedName();
        SymtabEntry recordEntry = symtabStack.enterLocal(recordTypeName, TYPE);
        recordTypespec.setIdentifier(recordEntry);
        recordEntry.setTypespec(recordTypespec);

        recordTypeCtx.typespec = recordTypespec;
        return recordTypespec;
    }

    /**
     * Create the symbol table for a record type.
     *
     * @param ctx        the RecordFieldsContext,
     * @return the symbol table.
     */
    private Symtab createRecordSymtab(RecordFieldsContext ctx) {
        Symtab recordSymtab = symtabStack.push();

        visit(ctx.variableDeclarationsList());
        recordSymtab.resetVariables(RECORD_FIELD);
        symtabStack.pop();

        return recordSymtab;
    }
}

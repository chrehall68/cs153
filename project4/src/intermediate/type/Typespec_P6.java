package intermediate.type;

import intermediate.symtab.Predefined;
import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;

import java.util.ArrayList;

public class Typespec_P6 {
    public enum Form {
        SCALAR,
        ENUMERATED,
        SUBRANGE,
        STRING,
        ARRAY,
        RECORD,
        UNKNOWN;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private interface TypeInfo {}

    private class EnumeratedInfo implements TypeInfo {
        private ArrayList<SymtabEntry> constants;
    }

    private class SubrangeInfo implements TypeInfo {
        private Typespec_P6 baseType;
        private Object minValue;
        private Object maxValue;
    }

    private class ArrayInfo implements TypeInfo {
        private Typespec_P6 indexType;
        private Typespec_P6 elementType;
        private int elementCount;
    }

    private class RecordInfo implements TypeInfo {
        String typePath;
        private Symtab symtab;
    }

    private Form form;
    private SymtabEntry identifier;
    private TypeInfo info;

    public Typespec_P6(Form form) {
        this.form = form;
        this.identifier = null;

        switch (form) {
            case ENUMERATED:
                info = new EnumeratedInfo();
                ((EnumeratedInfo) info).constants = new ArrayList<SymtabEntry>();
                break;

            case SUBRANGE:
                info = new SubrangeInfo();
                ((SubrangeInfo) info).minValue = 0;
                ((SubrangeInfo) info).maxValue = 0;
                ((SubrangeInfo) info).baseType = null;
                break;

            case ARRAY:
                info = new ArrayInfo();
                ((ArrayInfo) info).indexType = null;
                ((ArrayInfo) info).elementType = null;
                ((ArrayInfo) info).elementCount = 0;
                break;

            case RECORD:
                info = new RecordInfo();
                ((RecordInfo) info).typePath = null;
                ((RecordInfo) info).symtab = null;
                break;

            default:
                break;
        }
    }

    public Form getForm() {
        return form;
    }

    public SymtabEntry getIdentifier() {
        return identifier;
    }

    public void setIdentifier(SymtabEntry identifier) {
        this.identifier = identifier;
    }

    public boolean isInteger() {
        return this == Predefined.integerType;
    }

    public boolean isReal() {
        return this == Predefined.realType;
    }

    public boolean isChar() {
        return this == Predefined.charType;
    }

    public boolean isBoolean() {
        return this == Predefined.booleanType;
    }

    public boolean isString() {
        return this == Predefined.stringType;
    }

    public boolean isNumeric() {
        return isInteger() || isReal();
    }

    public boolean isOrdinal() {
        return isInteger()
                || isChar()
                || isBoolean()
                || (form == Form.ENUMERATED)
                || (form == Form.SUBRANGE);
    }

    public ArrayList<SymtabEntry> getEnumeratedConstants() {
        return ((EnumeratedInfo) info).constants;
    }

    public void setEnumeratedConstants(ArrayList<SymtabEntry> constants) {
        ((EnumeratedInfo) info).constants = constants;
    }

    public Typespec_P6 baseType() {
        return form == Form.SUBRANGE ? ((SubrangeInfo) info).baseType : this;
    }

    public void setSubrangeBaseType(Typespec_P6 baseType) {
        ((SubrangeInfo) info).baseType = baseType;
    }

    public Object getSubrangeMinValue() {
        return info != null ? ((SubrangeInfo) info).minValue : 0;
    }

    public void setSubrangeMinValue(Object minValue) {
        ((SubrangeInfo) info).minValue = minValue;
    }

    public Object getSubrangeMaxValue() {
        return info != null ? ((SubrangeInfo) info).maxValue : 0;
    }

    public void setSubrangeMaxValue(Object maxValue) {
        ((SubrangeInfo) info).maxValue = maxValue;
    }

    public Typespec_P6 getArrayBaseType() {
        Typespec_P6 elmtType = this;

        while (elmtType.form == Form.ARRAY) {
            elmtType = elmtType.getArrayElementType();
        }

        return elmtType.baseType();
    }

    public Typespec_P6 getArrayIndexType() {
        return ((ArrayInfo) info).indexType;
    }

    public void setArrayIndexType(Typespec_P6 indexType) {
        ((ArrayInfo) info).indexType = indexType;
    }

    public Typespec_P6 getArrayElementType() {
        return info != null ? ((ArrayInfo) info).elementType : Predefined.undefinedType;
    }

    public void setArrayElementType(Typespec_P6 elementType) {
        ((ArrayInfo) info).elementType = elementType;
    }

    public int getArrayElementCount() {
        return info != null ? ((ArrayInfo) info).elementCount : 0;
    }

    public void setArrayElementCount(int elementCount) {
        ((ArrayInfo) info).elementCount = elementCount;
    }

    public Symtab getRecordSymtab() {
        return ((RecordInfo) info).symtab;
    }

    public void setRecordSymtab(Symtab symtab) {
        ((RecordInfo) info).symtab = symtab;
    }

    public String getRecordTypePath() {
        return ((RecordInfo) info).typePath;
    }

    public void setRecordTypePath(String typePath) {
        ((RecordInfo) info).typePath = typePath;
    }
}

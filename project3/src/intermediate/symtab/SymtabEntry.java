package intermediate.symtab;

import intermediate.type.Typespec_P2;

import java.util.ArrayList;

public class SymtabEntry {
    public enum Kind {
        CONSTANT,
        ENUMERATED_CONSTANT,
        TYPE,
        VARIABLE,
        RECORD_FIELD,
        VALUE_PARAMETER,
        REFERENCE_PARAMETER,
        PROGRAM_PARAMETER,
        PROGRAM,
        PROCEDURE,
        FUNCTION,
        UNDEFINED;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private String name;
    private Kind kind;
    private Typespec_P2 typespec;
    private Symtab symtab;
    private Object value;
    private ArrayList<Integer> lineNumbers;

    public SymtabEntry(String name, Kind kind, Symtab symtab) {
        this.name = name;
        this.kind = kind;
        this.symtab = symtab;
        this.value = 0.0;
        lineNumbers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public Typespec_P2 getTypespec() {
        return typespec;
    }

    public Object getValue() {
        return value;
    }

    public ArrayList<Integer> getLineNumbers() {
        return lineNumbers;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setTypespec(Typespec_P2 typespec) {
        this.typespec = typespec;
    }

    public void appendLineNumber(int lineNumber) {
        lineNumbers.add(lineNumber);
    }
}

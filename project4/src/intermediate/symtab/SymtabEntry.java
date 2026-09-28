package intermediate.symtab;

import intermediate.type.Typespec_P6;

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

    public enum Routine {
        DECLARED,
        FORWARD,
        READ,
        READLN,
        WRITE,
        WRITELN,
        ABS,
        ARCTAN,
        CHR,
        COS,
        EOF,
        EOLN,
        EXP,
        LN,
        ODD,
        ORD,
        PRED,
        ROUND,
        SIN,
        SQR,
        SQRT,
        SUCC,
        TRUNC,
    }

    private interface EntryInfo {}

    private class ValueInfo implements EntryInfo {
        private Object value;
    }

    private class RoutineInfo implements EntryInfo {
        private Routine code; // routine code
        private Symtab symtab; // routine's symbol table
        private ArrayList<SymtabEntry> parameters; // routine's formal parameters
        private ArrayList<SymtabEntry> subroutines; // symtab entries of subroutines
        private Object executable; // routine's executable code
    }

    private String name;
    private Kind kind;
    private Typespec_P6 typespec;
    private Symtab symtab;
    private ArrayList<Integer> lineNumbers;
    private EntryInfo info;

    public SymtabEntry(String name, Kind kind, Symtab symtab) {
        this.name = name;
        this.kind = kind;
        this.symtab = symtab;
        lineNumbers = new ArrayList<>();

        // Initialize the appropriate entry information.
        switch (kind) {
            case CONSTANT:
            case ENUMERATED_CONSTANT:
            case VARIABLE:
            case RECORD_FIELD:
            case VALUE_PARAMETER:
                info = new ValueInfo();
                break;

            case PROGRAM:
            case PROCEDURE:
            case FUNCTION:
                info = new RoutineInfo();
                ((RoutineInfo) info).parameters = new ArrayList<SymtabEntry>();
                ((RoutineInfo) info).subroutines = new ArrayList<SymtabEntry>();
                break;

            default:
                break;
        }
    }

    public String getName() {
        return name;
    }

    public Typespec_P6 getTypespec() {
        return typespec;
    }

    public void setTypespec(Typespec_P6 typespec) {
        this.typespec = typespec;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Symtab getSymtab() {
        return symtab;
    }

    public ArrayList<Integer> getLineNumbers() {
        return lineNumbers;
    }

    public void appendLineNumber(int lineNumber) {
        lineNumbers.add(lineNumber);
    }

    public Object getValue() {
        return ((ValueInfo) info).value;
    }

    public void setValue(Object value) {
        ((ValueInfo) info).value = value;
    }

    public Routine getRoutineCode() {
        return ((RoutineInfo) info).code;
    }

    public void setRoutineCode(Routine code) {
        ((RoutineInfo) info).code = code;
    }

    public Symtab getRoutineSymtab() {
        return ((RoutineInfo) info).symtab;
    }

    public void setRoutineSymtab(Symtab symtab) {
        ((RoutineInfo) info).symtab = symtab;
    }

    public ArrayList<SymtabEntry> getRoutineParameters() {
        return ((RoutineInfo) info).parameters;
    }

    public void setRoutineParameters(ArrayList<SymtabEntry> parameters) {
        ((RoutineInfo) info).parameters = parameters;
    }

    public ArrayList<SymtabEntry> getSubroutines() {
        return ((RoutineInfo) info).subroutines;
    }

    public void appendSubroutine(SymtabEntry subroutineEntry) {
        ((RoutineInfo) info).subroutines.add(subroutineEntry);
    }

    public Object getExecutable() {
        return ((RoutineInfo) info).executable;
    }

    public void setExecutable(Object executable) {
        ((RoutineInfo) info).executable = executable;
    }
}

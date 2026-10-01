package intermediate.util;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec_P6.Form.*;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.*;
import intermediate.type.Typespec_P6.Form;

import java.util.ArrayList;
import java.util.Map;

public class CrossReferencer {
    private static final int NAME_WIDTH = 16;

    private static final String NAME_FORMAT = "%-" + NAME_WIDTH + "s";
    private static final String NUMBERS_LABEL = " Line numbers    ";
    private static final String NUMBERS_UNDERLINE = " ------------    ";
    private static final String NUMBER_FORMAT = " %03d";

    private static final int LABEL_WIDTH = NUMBERS_LABEL.length();
    private static final int INDENT_WIDTH = NAME_WIDTH + LABEL_WIDTH;

    private static final StringBuilder INDENT = new StringBuilder(INDENT_WIDTH);

    static {
        for (int i = 0; i < INDENT_WIDTH; ++i) INDENT.append(" ");
    }

    public void print(SymtabStack symtabStack) {
        System.out.println("\n===== CROSS-REFERENCE TABLE =====");

        SymtabEntry programEntry = symtabStack.getProgramEntry();
        printRoutine(programEntry);
    }

    /**
     * Print a cross-reference table for a routine.
     * @param routineEntry the routine identifier's symbol table entry.
     */
    private void printRoutine(SymtabEntry routineEntry) {
        Kind kind = routineEntry.getKind();
        System.out.println(
                "\n*** " + kind.toString().toUpperCase() + " " + routineEntry.getName() + " ***");
        printColumnHeadings();

        // Print the entries in the routine's symbol table.
        Symtab symtab = routineEntry.getRoutineSymtab();
        printSymtab(symtab);

        // Print any procedures and functions defined in the routine.
        ArrayList<SymtabEntry> subroutineEntries = routineEntry.getSubroutines();
        if (subroutineEntries != null) {
            for (SymtabEntry rtnEntry : subroutineEntries) printRoutine(rtnEntry);
        }
    }

    private void printColumnHeadings() {
        System.out.println();
        System.out.println(
                String.format(NAME_FORMAT, "Identifier") + NUMBERS_LABEL + "Type specification");
        System.out.println(
                String.format(NAME_FORMAT, "----------")
                        + NUMBERS_UNDERLINE
                        + "------------------");
    }

    /**
     * Print the entries in a symbol table.
     * @param symtab the symbol table.
     * @param recordTypes the list to fill with RECORD type specifications.
     */
    private void printSymtab(Symtab symtab) {
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet()) {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            System.out.printf(NAME_FORMAT, entry.getName());

            for (int number : entry.getLineNumbers()) {
                System.out.print(String.format(NUMBER_FORMAT, number));
            }
            System.out.println();

            printEntry(entry);
        }

        // Loop over the sorted list of entries again
        // to print each nested record's symbol table.
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet()) {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            if (entry.getKind() == TYPE) {
                Typespec_P6 type = entry.getTypespec();
                if (type.getForm() == RECORD) printRecord(type);
            }
        }
    }

    private void printEntry(SymtabEntry entry) {
        Kind kind = entry.getKind();
        int nestingLevel = entry.getSymtab().getNestingLevel();
        System.out.println(INDENT + "Identifier kind: " + kind.toString().replace("_", " "));
        System.out.println(INDENT + "Scope nesting level: " + nestingLevel);

        // Print the type specification.
        Typespec_P6 typespec = entry.getTypespec();
        printTypespec(typespec);

        switch (kind) {
            case CONSTANT:
                {
                    Object value = entry.getValue();
                    System.out.println(INDENT + "Constant value: " + toString(value, typespec));

                    // Print the type details only if the type is unnamed.
                    if (typespec.getIdentifier() == null) {
                        printTypespecDetail(typespec);
                    }

                    break;
                }

            case ENUMERATED_CONSTANT:
                {
                    Object value = entry.getValue();
                    System.out.println(INDENT + "Enumerated value: " + toString(value, typespec));

                    break;
                }

            case TYPE:
                {
                    // Print the type details only when the type is first defined.
                    if (entry == typespec.getIdentifier()) {
                        printTypespecDetail(typespec);
                    }

                    break;
                }

            case VARIABLE:
                {
                    // Print the type details only if the type is unnamed.
                    if (typespec.getIdentifier() == null) {
                        printTypespecDetail(typespec);
                    }

                    break;
                }

            case RECORD_FIELD:
                {
                    printTypespecDetail(typespec);
                    break;
                }

            default:
                break;
        }
    }

    /**
     * Print a type specification.
     * @param typespec the type specification.
     */
    private void printTypespec(Typespec_P6 typespec) {
        if (typespec != null) {
            Form form = typespec.getForm();
            SymtabEntry typeEntry = typespec.getIdentifier();
            String typeName = typeEntry != null ? typeEntry.getName() : "<unnamed>";

            System.out.println(INDENT + "Type form: " + form + ", Type id: " + typeName);
        }
    }

    private static final String ENUM_CONST_FORMAT = "%" + NAME_WIDTH + "s = %s";

    /**
     * Print the details of a type specification.
     * @param typespec the type specification.
     */
    private void printTypespecDetail(Typespec_P6 typespec) {
        Form form = typespec.getForm();

        switch (form) {
            case ENUMERATED:
                {
                    ArrayList<SymtabEntry> constantEntries = typespec.getEnumeratedConstants();

                    System.out.println(INDENT + "--- Enumerated constants ---");

                    // Print each enumeration constant and its value.
                    for (SymtabEntry constantEntry : constantEntries) {
                        String name = constantEntry.getName();
                        Object value = constantEntry.getValue();

                        System.out.println(INDENT + String.format(ENUM_CONST_FORMAT, name, value));
                    }

                    break;
                }

            case SUBRANGE:
                {
                    Object minValue = typespec.getSubrangeMinValue();
                    Object maxValue = typespec.getSubrangeMaxValue();
                    Typespec_P6 baseType = typespec.baseType();

                    System.out.println(INDENT + "--- Base type ---");
                    printTypespec(baseType);

                    // Print the base type details only if the type is unnamed.
                    if (baseType.getIdentifier() == null) {
                        printTypespecDetail(baseType);
                    }

                    System.out.print(INDENT + "Range: ");
                    System.out.println(
                            toString(minValue, baseType) + ".." + toString(maxValue, baseType));

                    break;
                }

            case ARRAY:
                {
                    Typespec_P6 indexType = typespec.getArrayIndexType();
                    Typespec_P6 elementType = typespec.getArrayElementType();
                    int count = typespec.getArrayElementCount();

                    System.out.println(INDENT + "--- INDEX TYPE ---");
                    printTypespec(indexType);

                    // Print the index type details only if the type is unnamed.
                    if (indexType.getIdentifier() == null) {
                        printTypespecDetail(indexType);
                    }

                    System.out.println(INDENT + "--- ELEMENT TYPE ---");
                    printTypespec(elementType);
                    System.out.println(INDENT.toString() + count + " elements");

                    // Print the element type details only if the type is unnamed.
                    if (elementType.getIdentifier() == null) {
                        printTypespecDetail(elementType);
                    }

                    break;
                }

            case RECORD:
                {
                    // Named records are printed separately by printSymtab().
                    if (typespec.getIdentifier() == null) {
                        // this way, the unnamed record definition
                        // will always directly follow the field that it belongs to
                        // that way there's no confusion as to which unnamed record
                        // a field belongs to
                        printRecord(typespec);
                        // need this here because, unlike with named records,
                        // these unnamed records might be followed by more identifiers
                        // and if we don't put this here, it'll look as if those other identifiers
                        // are "part of this record" (even when they're not)
                        System.out.println("--- END RECORD <unnamed> ---");
                    }

                    break;
                }

            default:
                break;
        }
    }

    private void printRecord(Typespec_P6 recordType) {
        SymtabEntry recordEntry = recordType.getIdentifier();
        String name = recordEntry != null ? recordEntry.getName() : "<unnamed>";

        System.out.println("\n--- RECORD " + name + " ---");
        printColumnHeadings();

        // Print the entries in the record's symbol table.
        Symtab symtab = recordType.getRecordSymtab();
        printSymtab(symtab);
    }

    /**
     * Convert a value to a string.
     * @param value the value.
     * @param type the value's datatype.
     * @return the string.
     */
    private String toString(Object value, Typespec_P6 typespec) {
        if (typespec == Predefined.charType) {
            return "'" + (Character) value + "'";
        } else if (typespec == Predefined.stringType) {
            return "\"" + (String) value + "\"";
        } else {
            return value.toString();
        }
    }
}

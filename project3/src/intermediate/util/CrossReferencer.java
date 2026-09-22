package intermediate.util;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.Typespec_P2;
import intermediate.type.Typespec_P2.Form;

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

    public void print(Symtab symtab) {
        System.out.println("\n===== CROSS-REFERENCE TABLE =====");
        printColumnHeadings();

        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet()) {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            System.out.printf(NAME_FORMAT, entry.getName());

            for (int number : entry.getLineNumbers()) {
                System.out.print(String.format(NUMBER_FORMAT, number));
            }
            System.out.println();

            printEntry(entry);
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

    private void printEntry(SymtabEntry entry) {
        Kind kind = entry.getKind();
        System.out.println(INDENT + "Identifier kind: " + kind.toString().replace("_", " "));

        // Print the type specification.
        Typespec_P2 type = entry.getTypespec();
        printTypespec(type);

        switch (kind) {
            case CONSTANT:
                {
                    Object value = entry.getValue();
                    System.out.println(INDENT + "Constant value: " + toString(value, type));

                    // Print the type details only if the type is unnamed.
                    if (type.getIdentifier() == null) {
                        printTypespecDetail(type);
                    }

                    break;
                }

            case ENUMERATED_CONSTANT:
                {
                    Object value = entry.getValue();
                    System.out.println(INDENT + "Enumerated value: " + toString(value, type));

                    break;
                }

            case TYPE:
                {
                    // Print the type details only when the type is first defined.
                    if (entry == type.getIdentifier()) {
                        printTypespecDetail(type);
                    }

                    break;
                }

            case VARIABLE:
                {
                    // Print the type details only if the type is unnamed.
                    if (type.getIdentifier() == null) {
                        printTypespecDetail(type);
                    }

                    break;
                }

            case RECORD_FIELD:
                {
                    printTypespecDetail(type);
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
    private void printTypespec(Typespec_P2 typespec) {
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
    private void printTypespecDetail(Typespec_P2 typespec) {
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
                    Typespec_P2 baseType = typespec.baseType();

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
                    Typespec_P2 indexType = typespec.getArrayIndexType();
                    Typespec_P2 elementType = typespec.getArrayElementType();
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

            case HASHTABLE:
                {
                    Typespec_P2 keyType = typespec.getHashtableKeyType();
                    Typespec_P2 elementType = typespec.getHashtableElementType();

                    System.out.println(INDENT + "--- KEY TYPE ---");
                    printTypespec(keyType);
                    System.out.println(INDENT + "--- ELEMENT TYPE ---");
                    printTypespec(elementType);
                    break;
                }

            default:
                break;
        }
    }

    /**
     * Convert a value to a string.
     * @param value the value.
     * @param type the value's datatype.
     * @return the string.
     */
    private String toString(Object value, Typespec_P2 typespec) {
        if (typespec == Predefined.charType) {
            return "'" + (Character) value + "'";
        } else if (typespec == Predefined.stringType) {
            return "\"" + (String) value + "\"";
        } else {
            return value.toString();
        }
    }
}

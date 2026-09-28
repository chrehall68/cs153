package intermediate.symtab;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec_P6.Form.*;

import intermediate.type.*;

import java.util.ArrayList;

public class Predefined {
    private static SymtabStack symtabStack;

    public static Typespec_P6 integerType;
    public static Typespec_P6 realType;
    public static Typespec_P6 booleanType;
    public static Typespec_P6 charType;
    public static Typespec_P6 stringType;
    public static Typespec_P6 undefinedType;

    public static void initialize(SymtabStack symtabStack) {
        Predefined.symtabStack = symtabStack;

        initializeTypes();
        initializeConstants();
    }

    private static void initializeTypes() {
        SymtabEntry integerEntry = symtabStack.enterLocal("integer", TYPE);
        integerType = new Typespec_P6(SCALAR);
        integerType.setIdentifier(integerEntry);
        integerEntry.setTypespec(integerType);

        SymtabEntry realEntry = symtabStack.enterLocal("real", TYPE);
        realType = new Typespec_P6(SCALAR);
        realType.setIdentifier(realEntry);
        realEntry.setTypespec(realType);

        SymtabEntry charEntry = symtabStack.enterLocal("char", TYPE);
        charType = new Typespec_P6(SCALAR);
        charType.setIdentifier(charEntry);
        charEntry.setTypespec(charType);

        SymtabEntry booleanEntry = symtabStack.enterLocal("boolean", TYPE);
        booleanType = new Typespec_P6(ENUMERATED);
        booleanType.setIdentifier(booleanEntry);
        booleanEntry.setTypespec(booleanType);

        SymtabEntry stringEntry = symtabStack.enterLocal("string", TYPE);
        stringType = new Typespec_P6(STRING);
        stringType.setIdentifier(stringEntry);
        stringEntry.setTypespec(stringType);

        undefinedType = new Typespec_P6(UNKNOWN);
    }

    private static void initializeConstants() {
        SymtabEntry falseEntry = symtabStack.enterLocal("false", ENUMERATED_CONSTANT);
        falseEntry.setTypespec(booleanType);
        falseEntry.setValue(0);

        SymtabEntry trueEntry = symtabStack.enterLocal("true", ENUMERATED_CONSTANT);
        trueEntry.setTypespec(booleanType);
        trueEntry.setValue(1);

        ArrayList<SymtabEntry> constants = booleanType.getEnumeratedConstants();
        constants.add(falseEntry);
        constants.add(trueEntry);
    }
}

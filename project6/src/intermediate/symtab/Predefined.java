package intermediate.symtab;

import java.util.ArrayList;

import intermediate.type.*;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;

public class Predefined
{
    private static SymtabStack symtabStack;
    
    public static Typespec integerType;
    public static Typespec realType;
    public static Typespec booleanType;
    public static Typespec charType;
    public static Typespec stringType;
    public static Typespec undefinedType;

    public static void initialize(SymtabStack symtabStack)
    {
        Predefined.symtabStack = symtabStack;
        
        initializeTypes();
        initializeConstants();
    }

    private static void initializeTypes()
    {
        SymtabEntry integerEntry = symtabStack.enterLocal("integer", TYPE);
        integerType = new Typespec(SCALAR);
        integerType.setIdentifier(integerEntry);
        integerEntry.setTypespec(integerType);

        SymtabEntry realEntry = symtabStack.enterLocal("real", TYPE);
        realType = new Typespec(SCALAR);
        realType.setIdentifier(realEntry);
        realEntry.setTypespec(realType);

        SymtabEntry charEntry = symtabStack.enterLocal("char", TYPE);
        charType = new Typespec(SCALAR);
        charType.setIdentifier(charEntry);
        charEntry.setTypespec(charType);

        SymtabEntry booleanEntry = symtabStack.enterLocal("boolean", TYPE);
        booleanType = new Typespec(ENUMERATED);
        booleanType.setIdentifier(booleanEntry);
        booleanEntry.setTypespec(booleanType);

        SymtabEntry stringEntry = symtabStack.enterLocal("string", TYPE);
        stringType = new Typespec(STRING);
        stringType.setIdentifier(stringEntry);
        stringEntry.setTypespec(stringType);

        undefinedType = new Typespec(UNKNOWN);
    }

    private static void initializeConstants()
    {
        SymtabEntry falseEntry = 
                    symtabStack.enterLocal("false", ENUMERATED_CONSTANT);
        falseEntry.setTypespec(booleanType);
        falseEntry.setValue(0);

        SymtabEntry trueEntry = 
                    symtabStack.enterLocal("true", ENUMERATED_CONSTANT);
        trueEntry.setTypespec(booleanType);
        trueEntry.setValue(1);

        ArrayList<SymtabEntry> constants = 
                            booleanType.getEnumeratedConstants();
        constants.add(falseEntry);
        constants.add(trueEntry);
    }
}

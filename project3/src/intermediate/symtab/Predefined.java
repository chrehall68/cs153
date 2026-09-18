package intermediate.symtab;

import java.util.ArrayList;

import intermediate.type.*;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec_P2.Form.*;

public class Predefined
{
    private static Symtab symtab;
    
    public static Typespec_P2 integerType;
    public static Typespec_P2 realType;
    public static Typespec_P2 booleanType;
    public static Typespec_P2 charType;
    public static Typespec_P2 stringType;
    public static Typespec_P2 undefinedType;

    public static void initialize(Symtab symtab)
    {
        Predefined.symtab = symtab;
        
        initializeTypes();
        initializeConstants();
    }

    private static void initializeTypes()
    {
        SymtabEntry integerEntry = symtab.enter("integer", TYPE);
        integerType = new Typespec_P2(SCALAR);
        integerType.setIdentifier(integerEntry);
        integerEntry.setTypespec(integerType);

        SymtabEntry realEntry = symtab.enter("real", TYPE);
        realType = new Typespec_P2(SCALAR);
        realType.setIdentifier(realEntry);
        realEntry.setTypespec(realType);

        SymtabEntry charEntry = symtab.enter("char", TYPE);
        charType = new Typespec_P2(SCALAR);
        charType.setIdentifier(charEntry);
        charEntry.setTypespec(charType);

        SymtabEntry booleanEntry = symtab.enter("boolean", TYPE);
        booleanType = new Typespec_P2(ENUMERATED);
        booleanType.setIdentifier(booleanEntry);
        booleanEntry.setTypespec(booleanType);

        SymtabEntry stringEntry = symtab.enter("string", TYPE);
        stringType = new Typespec_P2(STRING);
        stringType.setIdentifier(stringEntry);
        stringEntry.setTypespec(stringType);

        undefinedType = new Typespec_P2(UNKNOWN);
    }

    private static void initializeConstants()
    {
        SymtabEntry falseEntry = 
                    symtab.enter("false", ENUMERATED_CONSTANT);
        falseEntry.setTypespec(booleanType);
        falseEntry.setValue(0);

        SymtabEntry trueEntry = 
                    symtab.enter("true", ENUMERATED_CONSTANT);
        trueEntry.setTypespec(booleanType);
        trueEntry.setValue(1);

        ArrayList<SymtabEntry> constants = 
                            booleanType.getEnumeratedConstants();
        constants.add(falseEntry);
        constants.add(trueEntry);
    }
}

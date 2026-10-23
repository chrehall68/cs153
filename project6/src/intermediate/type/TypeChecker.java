package intermediate.type;

import intermediate.type.Typespec.Form;
import static intermediate.type.Typespec.Form.*;

public class TypeChecker
{
    public static boolean areBothInteger(Typespec typespec1, 
                                         Typespec typespec2)
    {
        return typespec1.isInteger() && typespec2.isInteger();
    }

    public static boolean isAtLeastOneReal(Typespec typespec1,
                                           Typespec typespec2)
    {
        return    (typespec1.isReal()    && typespec2.isReal())
               || (typespec1.isReal()    && typespec2.isInteger())
               || (typespec1.isInteger() && typespec2.isReal());
    }
    
    public static boolean areBothChar(Typespec typespec1, 
                                      Typespec typespec2)
    {
        return typespec1.isChar() && typespec2.isChar();
    }

    public static boolean areBothBoolean(Typespec typespec1, 
                                         Typespec typespec2)
    {
        return typespec1.isBoolean() && typespec2.isBoolean();
    }

    public static boolean areBothString(Typespec typespec1, 
                                        Typespec typespec2)
    {
        return typespec1.isString() && typespec2.isString();
    }
    
    private static boolean sameScalarOrEnumerated(Typespec typespec1,
                                                  Typespec typespec2)
    {
        Form form = typespec1.getForm();
        
        return    (typespec1 == typespec2)
               && ((form == SCALAR) || (form == ENUMERATED));
    }

    public static boolean assignmentCompatible(Typespec targetTypespec, 
                                               Typespec valueTypespec)
    {
        if (   (targetTypespec == null) 
            || (valueTypespec == null)) return false;

        targetTypespec = targetTypespec.baseType();
        valueTypespec  = valueTypespec.baseType();

        if (targetTypespec == valueTypespec) return true;

        else if (   targetTypespec.isReal() 
                 && valueTypespec.isInteger()) return true;

        else return false;
    }

    public static boolean comparisonCompatible(Typespec typespec1,
                                               Typespec typespec2)
    {
        if ((typespec1 == null) || (typespec2 == null)) return false;

        typespec1 = typespec1.baseType();
        typespec2 = typespec2.baseType();
        
        if (   isAtLeastOneReal(typespec1, typespec2)
            || sameScalarOrEnumerated(typespec1, typespec2)) return true;    
        else return false;
    }
}

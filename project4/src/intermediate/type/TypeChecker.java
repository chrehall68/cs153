package intermediate.type;

import intermediate.type.Typespec_P6.Form;
import static intermediate.type.Typespec_P6.Form.*;

public class TypeChecker
{
    public static boolean areBothInteger(Typespec_P6 typespec1, 
                                         Typespec_P6 typespec2)
    {
        return typespec1.isInteger() && typespec2.isInteger();
    }

    public static boolean isAtLeastOneReal(Typespec_P6 typespec1,
                                           Typespec_P6 typespec2)
    {
        return    (typespec1.isReal()    && typespec2.isReal())
               || (typespec1.isReal()    && typespec2.isInteger())
               || (typespec1.isInteger() && typespec2.isReal());
    }
    
    public static boolean areBothChar(Typespec_P6 typespec1, 
                                      Typespec_P6 typespec2)
    {
        return typespec1.isChar() && typespec2.isChar();
    }

    public static boolean areBothBoolean(Typespec_P6 typespec1, 
                                         Typespec_P6 typespec2)
    {
        return typespec1.isBoolean() && typespec2.isBoolean();
    }

    public static boolean areBothString(Typespec_P6 typespec1, 
                                        Typespec_P6 typespec2)
    {
        return typespec1.isString() && typespec2.isString();
    }

    public static boolean assignmentCompatible(Typespec_P6 targetTypespec, 
                                               Typespec_P6 valueTypespec)
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

    public static boolean comparisonCompatible(Typespec_P6 typespec1,
                                               Typespec_P6 typespec2)
    {
        if ((typespec1 == null) || (typespec2 == null)) return false;

        typespec1 = typespec1.baseType();
        typespec2 = typespec2.baseType();
        
        if (   isAtLeastOneReal(typespec1, typespec2)
            || sameScalarOrEnumerated(typespec1, typespec2)) return true;    
        else return false;
    }
    
    private static boolean sameScalarOrEnumerated(Typespec_P6 typespec1,
                                                  Typespec_P6 typespec2)
    {
        Form form = typespec1.getForm();
        
        return    (typespec1 == typespec2)
               && ((form == SCALAR) || (form == ENUMERATED));
    }
}

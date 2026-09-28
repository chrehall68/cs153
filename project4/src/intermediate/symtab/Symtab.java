package intermediate.symtab;

import static intermediate.symtab.SymtabEntry.Kind.VARIABLE;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import intermediate.symtab.SymtabEntry.Kind;

public class Symtab extends TreeMap<String, SymtabEntry>
{
    private static final long serialVersionUID = 0L;
    
    private int nestingLevel;
    private SymtabEntry ownerEntry; // symbol table entry of this symtab's owner

//    public static final String UNNAMED_PREFIX = "_unnamed_";
//    private static int unnamedIndex = 0;

    /**
     * Generate a name for an unnamed type.
     * @return the name;
     */
//    public static String generateUnnamedName()
//    {
//        unnamedIndex++;
//        return UNNAMED_PREFIX + unnamedIndex;
//    }
    
    public Symtab(int nestingLevel)
    {
        this.nestingLevel = nestingLevel;
    }
    
    public int getNestingLevel()  { return nestingLevel; }
    public SymtabEntry getOwner() { return ownerEntry; }
    
    public void setOwner(SymtabEntry ownerEntry) 
    { 
        this.ownerEntry = ownerEntry;
    }

    public SymtabEntry enter(String name, Kind kind) 
    { 
        SymtabEntry entry = new SymtabEntry(name, kind, this);
        put(name.toLowerCase(), entry);
        
        return entry;
    }
    
    public SymtabEntry lookup(String name) 
    { 
        return get(name.toLowerCase()); 
    }
    
    public void resetVariables(Kind kind)
    {
        Collection<SymtabEntry> entries = values();
        Iterator<SymtabEntry> it = entries.iterator();

        // Iterate over the entries and reset their kind.
        while (it.hasNext()) 
        {
            SymtabEntry entry = it.next();
            if (entry.getKind() == VARIABLE) entry.setKind(kind);
        }
    }
}

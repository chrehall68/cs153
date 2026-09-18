package intermediate.symtab;

import java.util.TreeMap;

import intermediate.symtab.SymtabEntry.Kind;

public class Symtab extends TreeMap<String, SymtabEntry>
{
    private static final long serialVersionUID = 0L; 

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
}

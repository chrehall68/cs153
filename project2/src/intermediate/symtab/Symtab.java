/**
 * Symbol table class for a simple interpreter.
 *
 * (c) 2026 by Ronald Mak
 * Department of Computer Science
 * San Jose State University
 */
package intermediate.symtab;

import intermediate.symtab.SymtabEntry.Kind;

import java.util.TreeMap;

public class Symtab extends TreeMap<String, SymtabEntry> {
    private static final long serialVersionUID = 0L;

    /**
     * Make an entry.
     * @param name the entry's name.
     * @param kind the kind of entry.
     */
    public SymtabEntry enter(String name, Kind kind) {
        SymtabEntry entry = new SymtabEntry(name, kind, this);
        put(name.toLowerCase(), entry);

        return entry;
    }

    /**
     * Look up an entry.
     * @param name the entry's name.
     * @return the entry or null if it's not in the symbol table.
     */
    public SymtabEntry lookup(String name) {
        return get(name.toLowerCase());
    }
}

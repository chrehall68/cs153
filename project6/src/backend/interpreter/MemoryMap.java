/**
 * <h1>MemoryMap</h1>
 *
 * <p>The interpreter's runtime memory map.</p>
 *
 * <p>Copyright (c) 2024 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
package backend.interpreter;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MemoryMap extends HashMap<String, Cell> {
    private static final long serialVersionUID = 0L;

    /**
     * Constructor.
     * Create a memory map and allocate its memory cells
     * based on the entries in a symbol table.
     * @param symtab the symbol table.
     */
    public MemoryMap(Symtab symtab) {
        for (Map.Entry<String, SymtabEntry> symtabEntry : symtab.entrySet()) {
            SymtabEntry entry = (SymtabEntry) symtabEntry.getValue();
            Kind kind = entry.getKind();

            switch (kind) {
                case VARIABLE:
                case FUNCTION:
                case VALUE_PARAMETER:
                case RECORD_FIELD:
                    {
                        // Not a reference parameter: Allocate cells for the
                        //                            value in the hashmap.
                        String name = entry.getName();
                        Typespec typespec = entry.getTypespec();
                        put(name, new Cell(allocateCellValue(typespec)));
                        break;
                    }

                case REFERENCE_PARAMETER:
                    {
                        // Reference parameter: Allocate a single cell to hold a
                        //                      reference in the hashmap.
                        String name = entry.getName();
                        put(name, new Cell(null));
                    }

                default:
                    break;
            }
        }
    }

    /**
     * Return the memory cell with the given name.
     * @param name the name.
     * @return the cell.
     */
    public Cell getCell(String name) {
        return get(name);
    }

    /**
     * Replace the memory cell with the given name.
     * @param name the name.
     * @param cell the replacement cell.
     */
    public void replaceCell(String name, Cell cell) {
        put(name, cell);
    }

    /**
     * Get an arraylist of all the names in the memory map.
     * @return the arraylist.
     */
    public ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList<String>();

        Set<String> names = keySet();
        Iterator<String> it = names.iterator();
        while (it.hasNext()) list.add(it.next());

        return list;
    }

    /**
     * Make an allocation for a value of a given data type for a memory cell.
     * @param typespec the data type.
     * @return the allocation.
     */
    private Object allocateCellValue(Typespec typespec) {
        switch (typespec.getForm()) {
            case ARRAY:
                return allocateArrayCells(typespec);
            case RECORD:
                return allocateRecordMap(typespec);
            default:
                return null; // uninitialized scalar value
        }
    }

    /**
     * Allocate the memory cells of an array.
     * @param typespec the array type.
     * @return the allocation.
     */
    private Object[] allocateArrayCells(Typespec typespec) {
        int elmtCount = typespec.getArrayElementCount();
        Typespec elmtType = typespec.getArrayElementType();
        Cell allocation[] = new Cell[elmtCount];

        for (int i = 0; i < elmtCount; ++i) {
            allocation[i] = new Cell(allocateCellValue(elmtType));
        }

        return allocation;
    }

    /**
     * Allocate the memory map for a record.
     * @param typespec the record type.
     * @return the allocation.
     */
    private MemoryMap allocateRecordMap(Typespec typespec) {
        Symtab symtab = (Symtab) typespec.getRecordSymtab();
        MemoryMap memoryMap = new MemoryMap(symtab);

        return memoryMap;
    }
}

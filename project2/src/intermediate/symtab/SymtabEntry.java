/**
 * Symbol table entry class for a simple interpreter.
 * 
 * (c) 2020 by Ronald Mak
 * Department of Computer Science
 * San Jose State University
 */
package intermediate.symtab;

import java.util.ArrayList;

public class SymtabEntry
{
    public enum Kind
    {
        CONSTANT, ENUMERATED_CONSTANT, TYPE, VARIABLE, RECORD_FIELD,
        VALUE_PARAMETER, REFERENCE_PARAMETER, PROGRAM_PARAMETER,
        PROGRAM, PROCEDURE, FUNCTION, UNDEFINED;
        
        public String toString() 
        { 
            return super.toString().toLowerCase(); 
        }
    }

    private String name;
    private Kind kind;
    private Symtab symtab;
    private Double value;
    private ArrayList<Integer> lineNumbers;
    
    /**
     * Constructor.
     * @param name the entry's name.
     * @param kind the kind of entry.
     * @param symtab the symbol table that contains this entry.
     */
    public SymtabEntry(String name, Kind kind, Symtab symtab)
    {
        this.name = name;
        this.kind = kind;
        this.symtab = symtab;
        this.value = 0.0;
        lineNumbers = new ArrayList<>();
    }
    
    /**
     * Getter.
     * @return the entry's name.
     */
    public String getName()  { return name;  }
    
    /**
     * Get the kind of entry.
     * @return the kind.
     */
    public Kind getKind() { return kind; }

    /**
     * Get the symbol table that contains this entry.
     * @return the symbol table.
     */
    public Symtab getSymtab() { return symtab; }

    /**
     * Getter.
     * @return the entry's value.
     */
    public Double getValue() { return value; }
    
    /**
     * Set the entry's value.
     * @param value the value to set.
     */
    public void setValue(Double value) { this.value = value; }
    
    /**
     * Getter.
     * @return the entry's line numbers.
     */
    public ArrayList<Integer> getLineNumbers() 
    { 
        return lineNumbers; 
    }

    /**
     * Append a line number to the entry's line numbers.
     * @param lineNumber the number to append.
     */
    public void appendLineNumber(int lineNumber)
    {
        lineNumbers.add(lineNumber);
    }
}

/**
 * Symbol table entry class for a simple interpreter.
 *
 * (c) 2020 by Ronald Mak
 * Department of Computer Science
 * San Jose State University
 */
package intermediate;

import java.util.ArrayList;

public class SymtabEntry {
    private String name;
    private Double value;
    private ArrayList<Integer> lineNumbers;

    /**
     * Constructor.
     * @param name the entry's name.
     */
    public SymtabEntry(String name) {
        this.name = name;
        this.value = 0.0;
        lineNumbers = new ArrayList<>();
    }

    /**
     * Getter.
     * @return the entry's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter.
     * @return the entry's value.
     */
    public Double getValue() {
        return value;
    }

    /**
     * Set the entry's value.
     * @param value the value to set.
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Getter.
     * @return the entry's line numbers.
     */
    public ArrayList<Integer> getLineNumbers() {
        return lineNumbers;
    }

    /**
     * Append a line number to the entry's line numbers.
     * @param lineNumber the number to append.
     */
    public void appendLineNumber(int lineNumber) {
        lineNumbers.add(lineNumber);
    }
}

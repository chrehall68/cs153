/**
 * <h1>Cell</h1>
 *
 * <p>The interpreter's runtime memory cell.</p>
 *
 * <p>Copyright (c) 2024 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
package backend.interpreter;

public class Cell
{
    private Object value = null;  // value contained in the memory cell

    /**
     * Constructor.
     * @param value the value for the cell.
     */
    public Cell(Object value)
    {
        this.value = value;
    }

    /**
     * Set a new value into the cell.
     * @param newValue the new value.
     */
    public void setValue(Object newValue) { value = newValue; }

    /**
     * Get the value in the cell.
     * @return the value.
     */
    public Object getValue() { return value; }
}

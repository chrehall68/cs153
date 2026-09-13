package intermediate.util;

import java.util.Map;

import intermediate.symtab.Symtab;
import intermediate.symtab.SymtabEntry;

public class CrossReferencer 
{
    public void print(Symtab symtab)
    {
        System.out.println(
                "\n===== CROSS-REFERENCE TABLE =====\n");
        System.out.println("Identifier           Line numbers");
        System.out.println("----------           ------------");

        for (Map.Entry<String, SymtabEntry> 
                            entry : symtab.entrySet())
        {
            System.out.printf("%-20s ", entry.getKey());
            
            SymtabEntry symtabEntry = 
                        (SymtabEntry) entry.getValue();
            for (int number : symtabEntry.getLineNumbers())
            {
                System.out.printf("%03d ", number);
            }
            System.out.println();
        }
    }
}

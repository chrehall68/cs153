import java.util.HashMap;

public class TestHashTables {

    private static enum color {
        red,
        green,
        blue
    };

    private static HashMap<Integer, Double> namedscores;
    private static HashMap<Character, String> namednames;
    private static HashMap<Boolean, HashMap<Integer, Double>> namednested;
    private static HashMap<Integer, Double> anonymousscores;
    private static HashMap<Character, String> anonymousnames;
    private static HashMap<Boolean, HashMap<Integer, String>> anonymousnested;
    private static HashMap<color, String> enumtable;
    private static HashMap<Integer, Double> rangetable;
    private static HashMap<Integer, Character> rangeliteraltable;
    private static HashMap<Integer, char[]> arraytable;
    private static HashMap<Integer, double[][]> matrixtable;

    public static void main(String[] args) {
        System.out.println("Done!");
    }
}

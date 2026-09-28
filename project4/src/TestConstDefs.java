public class TestConstDefs {
    private static final int ONE = 1;
    private static final int MINUSONE = -ONE;
    private static final int MINUSTWO = -2;
    private static final double THOUSAND = 1000.0;
    private static final double PI = 3.14159265;
    private static final double AVOGADRO = 6.0221408E23;
    private static final double HATOMSIZE = 1.05835442134E-10;
    private static final boolean TRUTH = true;
    private static final boolean FAKE = false;
    private static final char LETTERX = 'x';
    private static final String GREETING = "Hello, world";
    private static final String FRIDAY = "It's Friday!";
    private static final String QUOTED = "A \"quoted\" word";

    public static void main(String[] args) {
        java.time.Instant _start = java.time.Instant.now();

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}

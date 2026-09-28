public class FunctionTest {
    private static final double PI = 3.1415926;

    static int addem(int p1, int p2, int p3) {
        int addem;

        addem = p1 + p2 + p3;

        return addem;
    }

    static double twopi() {
        double twopi;

        twopi = 2 * PI;

        return twopi;
    }

    public static void main(String[] args) {
        java.time.Instant _start = java.time.Instant.now();

        System.out.printf("addem returned %d\n", addem(1, 2, 3));
        System.out.printf("twopi returned %f\n", twopi());

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}

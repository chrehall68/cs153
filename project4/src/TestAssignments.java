public class TestAssignments {

    private static int i, j;
    private static double x, y;
    private static boolean b;
    private static boolean a2[][] = new boolean[4][6];
    private static boolean a3[][][] = new boolean[3][4][4];

    public static void main(String[] args) {
        java.time.Instant _start = java.time.Instant.now();

        i = 3;
        j = i;
        System.out.printf("j = %5d\n", j);
        x = 3.14;
        y = x;
        System.out.printf("y = %5.2f\n", y);
        a2[2][4] = true;
        b = a2[2][4];
        a3[(12) - 10][(22) - 20][(38) - 37] = !(a2[2][4]);
        System.out.println();
        System.out.printf(
                "b = %b and a3[12][22, 38] = %b\n", b, a3[(12) - 10][(22) - 20][(38) - 37]);

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}

public class SquareRootProcedure {

    private static int i, row;
    private static double number;

    static double sqroot(double x) {
        double sqroot;

        double root, diff, prev;
        root = x;
        prev = root;
        do {
            root = (x / root + root) / 2;
            diff = prev - root;
            prev = root;
        } while (!(diff < 1.0E-6));
        sqroot = root;

        return sqroot;
    }

    static void headers() {
        int i;
        System.out.printf("%60s\n", "Square Root Table");
        System.out.println();
        System.out.printf("     ");
        i = 0;
        do {
            System.out.printf("        .%1d", i);
            i = i + 1;
        } while (!(i > 9));
        System.out.println();
    }

    public static void main(String[] args) {
        java.time.Instant _start = java.time.Instant.now();

        headers();
        row = 1;
        do {
            System.out.printf("%5d", row);
            i = 0;
            do {
                number = row + ((double) i) / 10;
                System.out.printf("%10.6f", sqroot(number));
                i = i + 1;
            } while (!(i > 9));
            System.out.println();
            row = row + 1;
        } while (!(row > 25));

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}

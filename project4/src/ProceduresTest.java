public class ProceduresTest {

    static void hello() {
        System.out.printf("Hello\n");
        System.out.printf("This is the no input pascal program\n");
    }

    static void hello1(double x) {
        System.out.printf("%10.0f\n", x);
        hello();
    }

    static void hello2(double x, double y) {

        double i;
        i = x * y;
        System.out.printf("%10.0f\n", i);
        hello();
        hello1(0.2);
    }

    static void hello3(double x, String y, boolean z) {
        System.out.printf("%100.0f%s\n", x, y);
        System.out.printf("%b\n", z);
        hello();
        hello1(100);
        hello2(0.3, 1.12);
    }

    public static void main(String[] args) {
        java.time.Instant _start = java.time.Instant.now();

        hello();
        hello1(2.0 + 4 - 23 + 21);
        hello2(2.0, 4.0);
        hello3(2.0, " why must it be this way", false);

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}

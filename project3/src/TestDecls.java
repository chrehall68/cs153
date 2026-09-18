public class TestDecls {
    private static final int MINUSFIVE = -5;
    private static final int TEN = 10;

    private static enum greek {
        ALPHA,
        BETA,
        GAMMA
    };

    private static enum days {
        MON,
        TUE,
        WED,
        THU,
        FRI,
        SAT,
        SUN
    };

    private static greek g;
    private static int m;
    private static days w;
    private static int n;
    private static char grade;
    private static int r;
    private static int a1a[] = new int[10], a1b[] = new int[10];
    private static double a2[] = new double[21];
    private static boolean a3[] = new boolean[4];
    private static greek a4[] = new greek[3];
    private static days a5[][] = new days[21][16];
    private static days a6[][][] = new days[21][16][8];
    private static char chs[] = new char[32];
    private static String str;
    private static String msg[] = new String[21];
    private static int ua1[] = new int[21];
    private static int ua2[][] = new int[21][5];
    private static greek ua3[][][] = new greek[2][21][3];
    private static String us4;
    private static String us5[][] = new String[21][5];

    public static void main(String[] args) {
        System.out.println("Done!");
    }
}

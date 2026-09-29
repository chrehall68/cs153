public class UnnamedRecordTest
{

    
    private static class $0$complex1Class{
        int re, im;
    }

    private static $0$complex1Class complex1 = new $0$complex1Class();
    
    private static class $1$complex2Class{
        int re, im;
    }

    private static $1$complex2Class complex2 = new $1$complex2Class();
    
    private static class $2$productClass{
        int re, im;
    }

    private static $2$productClass product = new $2$productClass();
    
    private static class $3$mULtIPLEClass{
        int a, b, c;
    }

    private static $3$mULtIPLEClass mULtIPLE[][] = {{new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}, {new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}, {new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}, {new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}, {new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}, {new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}, {new $3$mULtIPLEClass(), new $3$mULtIPLEClass(), new $3$mULtIPLEClass()}};
    
    private static class $4$anonymousInsideClass{
        int re, im;
        
        private static class $5$otherClass{
            int a, b;
            
            private static class $6$other2Class{
                int c, d;
                
                private static class $7$other3Class{
                    int e, f;
                }

                private static $7$other3Class other3 = new $7$other3Class();
            }

            private static $6$other2Class other2 = new $6$other2Class();
        }

        private static $5$otherClass other = new $5$otherClass();
    }

    private static $4$anonymousInsideClass anonymousInside = new $4$anonymousInsideClass();

    public static void main(String[] args)
    {
        java.time.Instant _start = java.time.Instant.now();

        complex1.re = 3;
        complex1.im = 2;
        complex2.re = 1;
        complex2.im = 4;
        System.out.printf("complex1 = %d + %di\n", complex1.re, complex1.im);
        System.out.printf("complex2 = %d + %di\n", complex2.re, complex2.im);
        product.re = complex1.re*complex2.re - complex1.im*complex2.im;
        product.im = complex1.re*complex2.im + complex1.im*complex2.re;
        System.out.printf("product  = %d + %di\n", product.re, product.im);
        anonymousInside.other.other2.other3.f = 4;
        System.out.printf("%d\n", anonymousInside.other.other2.other3.f);

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}

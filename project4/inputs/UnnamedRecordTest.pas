PROGRAM UnnamedRecordTest;

VAR
    complex1 : RECORD re, im : integer END;
    complex2 : RECORD re, im : integer END;
    product  : RECORD re, im : integer END;
    mULtIPLE : array [2..8] OF ARRAY [3..5] OF RECORD a,b,c : integer END;
    anonymousInside : RECORD
        re, im : integer;
        other : RECORD
            a, b : integer;
            other2 : RECORD
                c, d : integer;
                other3 : RECORD e, f : integer END
            END
        END
    END;

BEGIN
    complex1.re := 3;
    complex1.im := 2;
    complex2.re := 1;
    complex2.im := 4;
    
    writeln('complex1 = ', complex1.re, ' + ', complex1.im, 'i');
    writeln('complex2 = ', complex2.re, ' + ', complex2.im, 'i');
    
    product.re :=   complex1.re*complex2.re
                  - complex1.im*complex2.im;
    product.im :=   complex1.re*complex2.im
                  + complex1.im*complex2.re;
                  
    writeln('product  = ', product.re, ' + ', product.im, 'i');

    aNONYMOUsInsIDE.oTHER.OtHER2.othER3.F := 4;
    writeln(anonymousinside.other.other2.other3.f);
END.

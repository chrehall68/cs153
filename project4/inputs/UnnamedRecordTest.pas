PROGRAM UnnamedRecordTest;

VAR
    complex1 : RECORD re, im : integer END;
    complex2 : RECORD re, im : integer END;
    product  : RECORD re, im : integer END;

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
END.

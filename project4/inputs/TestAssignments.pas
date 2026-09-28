PROGRAM TestAssignments;

VAR
    i, j : integer;
    x, y : real;
    b : boolean;
    a2 : ARRAY [0..3, 0..5] OF boolean;
    a3 : ARRAY [10..12, 20..23] OF ARRAY [37..40] OF boolean;
    
BEGIN
    i := 3;
    j := i;   
    writeln('j = ', j:5);

    x := 3.14;
    y := x;
    writeln('y = ', y:5:2);
    
    a2[2][4] := true;
    b := a2[2][4];    
    a3[12][22, 38] := NOT a2[2, 4];
    writeln;   
    writeln('b = ', b, ' and a3[12][22, 38] = ', a3[12][22, 38]);
    
END.

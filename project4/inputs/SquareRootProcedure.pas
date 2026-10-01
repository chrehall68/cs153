PROGRAM SquareRootProcedure;

VAR
    i, row : integer;
    number : real;
    
FUNCTION sqroot(x : real) : real;

    VAR root, diff, prev : real;
    
    BEGIN
        root := x;
        prev := root;
        
        REPEAT
            root := (x/root + root)/2;
            diff := prev - root;
            prev := root;
        UNTIL diff < 1e-6;
        
        sqroot := root;
    END;
    
PROCEDURE headers;

    VAR i : integer;
    
    BEGIN
        writeln('Square Root Table':60);
        writeln;
        write('     ');
    
        i := 0;
        REPEAT
            write('        .', i:1);
            i := i + 1
        UNTIL i > 9;
        writeln;
    END;

BEGIN
    headers;

    row := 1;
    REPEAT
        write(row:5);
        
        i := 0;
        REPEAT
            number := row + i/10;
            write(sqroot(number):10:6);
            i := i + 1;
        UNTIL i > 9;
        
        writeln;
        row := row + 1
    UNTIL row > 25;
END.
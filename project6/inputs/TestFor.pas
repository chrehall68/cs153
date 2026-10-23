PROGRAM TestFor;

VAR
    i, j, k : integer;
    
BEGIN
    write('i = ');
    FOR i := 1 TO 5 DO write(i:2);
    writeln;
    writeln;
    
    FOR i := 1 TO 3 DO BEGIN
        FOR j := 3 DOWNTO 1 DO BEGIN
            FOR k := 1 TO 2 DO
                writeln('i = ', i, ' j = ', j, ' k = ', k);
        END;
    END;
    
    writeln;
    writeln('Done!')
END.

PROGRAM TestSimpleFor;

VAR i, j, k : integer;

BEGIN
    FOR i:= 0 TO 3 DO BEGIN 
        writeln(i);
    END;
    FOR j:= 3 DOWNTO 0 DO BEGIN 
        writeln(j);
    END;
    
END.
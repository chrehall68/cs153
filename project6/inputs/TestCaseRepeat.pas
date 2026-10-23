PROGRAM TestCaseRepeat;

VAR
    i, even, odd, prime : integer;
    
BEGIN
    i := 0; even := -999; odd := -999; prime := -999;

    REPEAT BEGIN
        CASE i OF
            2: BEGIN even := i; prime := i END;
            0, 4, 6, 8, 10, 12, 14: even := i;
            1, 3, 5, 7, 
            9, 11, 13, 15:  BEGIN
                                odd := i;
                                CASE i OF
                                    2, 3, 5, 
                                    7, 11, 13: prime := i
                                END
                            END
        END;

        write('i ='); write(i:3); 
        write(', even = ');  IF even  <> -999 THEN write(even:3)
                                              ELSE write('...'); 
        write(', odd = ');   IF odd   <> -999 THEN write(odd:3)   
                                              ELSE write('...');
        write(', prime = '); IF prime <> -999 THEN write(prime:3) 
                                              ELSE write('...');
        writeln;
        
        even := -999; odd := -999; prime := -999;
        i := i + 1;
    END UNTIL i = 15;
END.

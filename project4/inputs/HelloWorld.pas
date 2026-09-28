PROGRAM HelloWorld;

VAR
    i : integer;

BEGIN
    i := 0;
    
    RePeAt
        i := i + 1;
        writeln('#', i, ': Hello,' + ' world!');
    until i = 5;
END.
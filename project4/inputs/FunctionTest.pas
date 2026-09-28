PROGRAM FunctionTest;

CONST
    PI = 3.1415926;

FUNCTION addem(p1, p2, p3 : integer) : integer;
    BEGIN
        addem := p1 + p2 + p3;
    END;
    
FUNCTION twopi : real;
    BEGIN
        twopi := 2*PI;
    END;
    
BEGIN
    writeln('addem returned ', addem(1, 2, 3));
    writeln('twopi returned ', twopi);
END.

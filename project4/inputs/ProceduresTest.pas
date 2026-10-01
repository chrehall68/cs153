PROGRAM ProceduresTest;

PROCEDURE hello;
    BEGIN
        writeln('Hello');
        writeln('This is the no input pascal program');
    END;

PROCEDURE hello1(x:real);
    BEGIN
        writeln(x:10);
        hello;
    END;

PROCEDURE hello2(x: real; y: real);
    VAR i : REAL;
    BEGIN
        i := x*y;
        writeln(i:10);
        hello;
        hello1(0.2);
    END;

PROCEDURE hello3(x: real; y: string; z: boolean);
    BEGIN
        writeln(x:100, y);
        writeln(z);
        hello;
        hello1(100);
        hello2(0.3, 1.12);
    END;
BEGIN
    hello;
    hello1(2.0+4-23+21);
    hello2(2.0, 4.0);
    hello3(2.0, ' why must it be this way', false);
END.
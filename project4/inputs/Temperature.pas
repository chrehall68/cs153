PROGRAM Temperature;

VAR
    fahrenheit : integer;
    celsius : real;

BEGIN
    writeln('Fahrenheit   Celsius');
    writeln;
    
    REPEAT  
        celsius := (fahrenheit - 32)/1.8;
        write(fahrenheit:6);
        writeln(celsius:13:2);
        
        fahrenheit := fahrenheit + 1
    UNTIL fahrenheit > 100; 
END.
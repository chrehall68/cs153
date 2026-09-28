PROGRAM TestStrings;

TYPE
    days = (MON, TUE, WED, THU, FRI, SAT, SUN);
    weekdays = MON..FRI;
    sr1 = 5..25;    
    string32 = PACKED ARRAY [1..32] OF char;
    char32 = ARRAY [1..32] OF char;

VAR
    str : string32;
    chars : char32;
    
    msg : ARRAY [sr1] OF string32;
    ustr : ARRAY [sr1, weekdays] 
                OF PACKED ARRAY [THU..SUN] OF char;
    carr : ARRAY [sr1, weekdays] 
                OF ARRAY [THU..SUN] OF char;
                
BEGIN
END.
    
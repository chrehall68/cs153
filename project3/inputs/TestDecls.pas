PROGRAM TestDecls;

CONST
    MINUSFIVE = -5;
    TEN = 10;

TYPE
    greek = (ALPHA, BETA, GAMMA);
    days = (MON, TUE, WED, THU, FRI, SAT, SUN);
    weekdays = MON..FRI;
    sr1 = 5..25;    
    sr2 = MINUSFIVE..TEN;
    sr3 = -TEN..-3;
    
    ar1 = ARRAY [1..TEN] OF integer;
    ar2 = ARRAY [sr1] OF real;
    ar3 = ARRAY [WED..SAT] of boolean;
    ar4 = ARRAY [(SMALL, MEDIUM, LARGE)] OF greek;
    char32 = ARRAY [1..32] OF char;                  
    
    ar5 = ARRAY [sr1] OF ARRAY [sr2] OF days;
    ar6 = ARRAY [sr1, sr2, sr3] OF weekdays;
                        
    string32 = PACKED ARRAY [1..32] OF char;
   
VAR
    g : greek;
    m : sr1;
    w : weekdays;
    
    n : 45..50;
    grade : 'A'..'F';
    r : (IV, V, VI);
    
    a1a, a1b : ar1;
    a2 : ar2;
    a3 : ar3;
    a4 : ar4;
    a5 : ar5;
    a6 : ar6;
    chs : char32;
    str : string32;
    msg : ARRAY [sr1] OF string32;

    ua1 : ARRAY [sr1] OF (JAN, FEB, MAR, APR, MAY);
    ua2 : ARRAY [sr1] OF ARRAY [8..12] OF JAN..APR;
    ua3 : ARRAY [boolean, sr1, (RED, WHITE, BLUE)] 
                                         OF greek;
    us4 : PACKED ARRAY [sr1] OF char;
    us5 : ARRAY [sr1, weekdays] 
                OF PACKED ARRAY [THU..SUN] OF char;
                
BEGIN
END.

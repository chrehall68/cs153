PROGRAM TestArrays;

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
    
    ar5 = ARRAY [sr1] OF ARRAY [sr2] OF weekdays;
    ar6 = ARRAY [sr1, sr2, sr3] OF weekdays;

VAR
    a1a, a1b : ar1;
    a2 : ar2;
    a3 : ar3;
    a4 : ar4;
    a5 : ar5;
    a6 : ar6;
    chs : char32;

    ua1 : ARRAY ['a'..'z'] OF (JAN, FEB, MAR, APR);
    ua2 : ARRAY [sr1] OF ARRAY [8..12] OF JAN..APR;
    ua3 : ARRAY [boolean, sr1, (RED, WHITE, BLUE)] 
                                         OF greek;
                
BEGIN
END.

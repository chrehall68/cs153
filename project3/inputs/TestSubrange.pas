PROGRAM TestSubrange;

CONST
    MINUSFIVE = -5;
    TEN = 10;

TYPE
    days = (SUN, MON, TUE, WED, THU, FRI, SAT);
    weekdays = MON..FRI;
    sr1 = 5..25;    
    sr2 = MINUSFIVE..TEN;
    sr3 = -TEN..-3;

VAR
    k : sr1;
    w : weekdays;
    
    n : 45..50;
    grade : 'A'..'F';
    midweek : TUE..THU;

BEGIN
END.

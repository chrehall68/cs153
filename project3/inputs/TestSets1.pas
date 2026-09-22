PROGRAM TestSets1;

TYPE
    sr1 = 5..25;
    inner2 = (A,B,C);
    set1 = PaCKed sET Of sr1;
    set2 = set oF boolean;
    set3 = SET OF integer;
    set4 = SET OF inner2;
    set5 = SET OF (D,E,F);

    days = (MON, TUE, WED, THU, FRI, SAT, SUN);
    weekdays = MON..FRI;

VAR
    unnamed1 : pAcKed SEt OF sr1;
    unnamed2 : set OF weekdays;
    unnamed3 : SET OF days;
    unnamed4 : SET OF (THING1, THING2, THING3);
    unnamed5 : SET OF 1..20;
    unnamed6 : SET OF MON..THU;
    unnamed7 : SET OF char;

    named1 : set1;
    named2 : set2;
    named3 : set3;
    named4 : set4;
    named5 : set5;
BEGIN
END.


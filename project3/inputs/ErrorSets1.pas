PROGRAM ErrorSets1;

TYPE
    sr1 = 5..25;
    set1 = PaCKed sET Of sr1;
    notOrdinal1 = PaCKed sET Of set1;
    notOrdinal2 = SET OF string;
    arrType = ARRAY [1..32] OF char;
    notOrdinal3 = SET OF arrType;
    arrType2 = ARRAY [1..64] OF set1;
    notOrdinal4 = packed set of arrType2;

BEGIN
END.


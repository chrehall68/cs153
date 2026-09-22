PROGRAM ErrorHashTables1;

TYPE
    badRealKey = HASHTABLE OF real TO integer;
    goodAfterBadKey = HASHTABLE OF integer TO string;
    badArrayKeyType = HASHTABLE OF ARRAY [1..4] OF char TO integer;
    goodAfterBadArrayKey = HASHTABLE OF char TO real;
    badElement = HASHTABLE OF integer TO missingType;
    goodAfterBadElement = HASHTABLE OF boolean TO integer;

VAR
    goodVar : goodAfterBadKey;
BEGIN
END.

PROGRAM ErrorHashTables1;

TYPE
    badRealKey = HASHTABLE OF real TO integer;
    badElement = HASHTABLE OF integer TO missingType;
    valueCanBeAnything = HASHTABLE OF integer TO ARRAY [1..4] OF char;
    nestedValue = HASHTABLE OF char TO HASHTABLE OF boolean TO string;

VAR
    badArrayKey : HASHTABLE OF ARRAY [1..4] OF char TO integer;
BEGIN
END.

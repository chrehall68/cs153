PROGRAM TestHashTables;

TYPE
    color = (red, green, blue);
    smallInt = 1..10;
    scores = HASHTABLE OF integer TO real;
    names = HASHTABLE OF char TO string;
    nested = HASHTABLE OF boolean TO scores;
    enumKeys = HASHTABLE OF color TO string;
    rangeKeys = HASHTABLE OF smallInt TO real;
    arrayValue = HASHTABLE OF integer TO ARRAY [1..4] OF char;
    matrixValue = HASHTABLE OF integer TO ARRAY [1..4, 1..5] OF real;

VAR
    namedScores : scores;
    namedNames : names;
    namedNested : nested;
    anonymousScores : HASHTABLE OF integer TO real;
    anonymousNames : HASHTABLE OF char TO string;
    anonymousNested : HASHTABLE OF boolean TO HASHTABLE OF integer TO string;
    enumTable : enumKeys;
    rangeTable : rangeKeys;
    rangeLiteralTable : HASHTABLE OF 1..20 TO char;
    arrayTable : arrayValue;
    matrixTable : matrixValue;
BEGIN
END.

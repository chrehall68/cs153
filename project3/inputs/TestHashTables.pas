PROGRAM TestHashTables;

TYPE
    scores = HASHTABLE OF integer TO real;
    names = HASH TABLE OF char TO string;
    nested = HASHTABLE OF boolean TO scores;

VAR
    namedScores : scores;
    namedNames : names;
    namedNested : nested;
    anonymousScores : HASHTABLE OF integer TO real;
    anonymousNames : HASH TABLE OF char TO string;
    anonymousNested : HASHTABLE OF boolean TO HASHTABLE OF integer TO string;
BEGIN
END.

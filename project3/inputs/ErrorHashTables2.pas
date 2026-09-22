PROGRAM ErrorHashTables2;

TYPE
    missingTo = HASHTABLE OF integer string;

    goodAfterFirstError = HASHTABLE OF integer TO string;

    wrongKeyword = HASHTABLE OF integer OF string;

    goodAfterSecondError = HASHTABLE OF char TO real;

    missingKey = HASHTABLE TO string;

    goodAfterThirdError = HASHTABLE OF boolean TO string;

VAR
    x : goodAfterThirdError;
BEGIN
END.

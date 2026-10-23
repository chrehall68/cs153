grammar Commander;

@header 
{
    package backend.interpreter.antlr4;
}

command : goCommand
        | stepCommand
        | quitCommand
        | breakCommand
        | unbreakCommand
        | showCommand
        | stackCommand
        | watchCommand
        | unwatchCommand
        ;
        
goCommand      : GO NEWLINE ;
stepCommand    : STEP NEWLINE | NEWLINE ;
quitCommand    : QUIT NEWLINE ;
breakCommand   : BREAK lineNumberList? NEWLINE ;
unbreakCommand : UNBREAK lineNumberList NEWLINE ;
showCommand    : SHOW variableList NEWLINE ;
stackCommand   : STACK NEWLINE ;
watchCommand   : WATCH simpleVariableList? NEWLINE ;
unwatchCommand : UNWATCH simpleVariableList NEWLINE ;

lineNumberList     : lineNumber ( ','? lineNumber )* ;
variableList       : variable ( ','? variable )* ;
simpleVariableList : simpleVariable ( ','? simpleVariable )* ;

lineNumber     : INTEGER ;
simpleVariable : IDENTIFIER;

variable  : IDENTIFIER modifier* ;
modifier  : '[' indexList ']' | '.' field ;
indexList : index ( ',' index )* ;
index     : INTEGER ; 
field     : IDENTIFIER ;

BREAK   : B R E A K ;
UNBREAK : U N B R E A K ;
WATCH   : W A T C H ;
UNWATCH : U N W A T C H ;
SHOW    : S H O W ;
STACK   : S T A C K ;
STEP    : S T E P ;
GO      : G O ;
QUIT    : Q U I T ;

IDENTIFIER : [a-zA-Z][a-zA-Z0-9]* ;
INTEGER    : [0-9]+ ;
NEWLINE    : '\r'? '\n' ;

WS : [ \t]+ -> skip ; 

fragment A : ('a' | 'A') ;
fragment B : ('b' | 'B') ;
fragment C : ('c' | 'C') ;
fragment D : ('d' | 'D') ;
fragment E : ('e' | 'E') ;
fragment F : ('f' | 'F') ;
fragment G : ('g' | 'G') ;
fragment H : ('h' | 'H') ;
fragment I : ('i' | 'I') ;
fragment J : ('j' | 'J') ;
fragment K : ('k' | 'K') ;
fragment L : ('l' | 'L') ;
fragment M : ('m' | 'M') ;
fragment N : ('n' | 'N') ;
fragment O : ('o' | 'O') ;
fragment P : ('p' | 'P') ;
fragment Q : ('q' | 'Q') ;
fragment R : ('r' | 'R') ;
fragment S : ('s' | 'S') ;
fragment T : ('t' | 'T') ;
fragment U : ('u' | 'U') ;
fragment V : ('v' | 'V') ;
fragment W : ('w' | 'W') ;
fragment X : ('x' | 'X') ;
fragment Y : ('y' | 'Y') ;
fragment Z : ('z' | 'Z') ;

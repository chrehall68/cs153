grammar SimpleA3;

@header 
{
    package intermediate.antlr4;
    import intermediate.symtab.SymtabEntry;
}

/*----------------- */
/* Production rules */
/*----------------- */

program       : programHeader block '.' ;
programHeader : PROGRAM identifier ';' ; 

block        : declarations compoundStatement ;
declarations : ;

identifier      locals [ SymtabEntry entry = null ] 
    : IDENTIFIER ;

statement : compoundStatement
          | assignmentStatement
          | repeatStatement
          | writeStatement
          | writelnStatement
          | caseStatement
          | emptyStatement
          ;

emptyStatement : /* empty */ ;

compoundStatement : BEGIN statementList END ;     
statementList     : statement ( ';' statement )* ;

assignmentStatement : variable ':=' expression ;
repeatStatement     : REPEAT statementList UNTIL expression ;

caseStatement   : CASE expression OF selectBranch ( ';' selectBranch)* (';')? END ;
selectBranch    : selectConstants ':' statement ;
selectConstants : selectConstant ( ',' selectConstant )* ;
selectConstant  : stringConstant | ( sign? ( unsignedConstant | identifier ) ) ;

writeStatement   : WRITE writeArguments ;
writelnStatement : WRITELN writeArguments? ;
writeArguments   : '(' writeArgument (',' writeArgument)* ')' ;
writeArgument    : expression (':' format)? ;
format           : width (':' precision)? ;
width            : sign? integerConstant ;
precision        : integerConstant ;

sign : '-' | '+' ;

expression       : simpleExpression (relOp simpleExpression)? ;
simpleExpression : sign? term (addOp term)* ;
term             : factor (mulOp factor)* ;

factor : variable           # factorVariable
       | unsignedConstant   # factorUnsignedConstant   
       | characterConstant  # factorCharacterConstant
       | stringConstant     # factorStringConstant
       | NOT factor         # factorNot
       | '(' expression ')' # factorParenthesized
       ;
       
relOp : '=' | '<>' | '<' | '<=' | '>' | '>=' ;
addOp : '+' | '-' | OR ;
mulOp : '*' | '/' | DIV | MOD | AND ;

variable            locals [ SymtabEntry entry = null ] 
    : identifier ;
     
unsignedConstant    locals [ Object value = null ]
    : integerConstant   # unsignedIntegerConstant 
    | realConstant      # unsignedRealConstant
    ;

integerConstant     locals [ Object value = null ] 
    : INTEGER ;
    
realConstant        locals [ Object value = null ]    
    : REAL;

characterConstant   locals [ Object value = null ] 
    : CHARACTER ;
    
stringConstant      locals [ Object value = null ]    
    : STRING ;
    
/*------------------ */
/* Token definitions */
/*------------------ */

PROGRAM   : P R O G R A M ;
BEGIN     : B E G I N ;
END       : E N D ;
DIV       : D I V ;
MOD       : M O D ;
AND       : A N D ;
OR        : O R ;
NOT       : N O T ;
REPEAT    : R E P E A T ;
UNTIL     : U N T I L ;
WRITE     : W R I T E ;
WRITELN   : W R I T E L N ;
CASE      : C A S E ;
OF        : O F ;

IDENTIFIER : LETTER ( LETTER | DIGIT )*;
INTEGER    : DIGITS ;

REAL : DIGITS '.' DIGITS
     | DIGITS ('e' | 'E') ('+' | '-')? DIGITS
     | DIGITS '.' DIGITS ('e' | 'E') ('+' | '-')? DIGITS
     ;

LETTER : [a-zA-Z] ;
DIGIT  : [0-9];
DIGITS : DIGIT+ ;

WHITESPACE : [ \n\r\t]+ -> skip ; 
COMMENT    : '{' COMMENT_CHAR* '}' -> skip ;

QUOTE     : '\'' ;
CHARACTER : QUOTE CHARACTER_CHAR QUOTE ;
STRING    : QUOTE STRING_CHAR* QUOTE ;

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

fragment CHARACTER_CHAR : ~('\'')   // any non-quote character
                        ;
fragment STRING_CHAR : QUOTE QUOTE  // two consecutive quotes
                     | CHARACTER_CHAR 
                     ;
fragment COMMENT_CHAR : ~'}' ;      // any character except }
                     

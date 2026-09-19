grammar Pcl_P2 ;

@header {
    package intermediate.antlr4;
    
    import intermediate.symtab.SymtabEntry;
    import intermediate.type.Typespec_P2;
}

program           : programHeader block '.' ;
programHeader     : PROGRAM identifier programParameters? ';' ; 
programParameters : '(' identifier ( ',' identifier )* ')' ;

identifier      locals [ SymtabEntry entry = null,
                         Typespec_P2 typespec = null,
                         Object value = null
                       ] 
    : IDENTIFIER ;

block             : declarations compoundStatement ;
compoundStatement : BEGIN END ;

declarations : ( constantsPart ';' )? ( typesPart ';' )? 
               ( variablesPart ';' )? ( procedureAndFunctionsPart ';')? ;
                
constantsPart           : CONST constantDefinitionsList ;
constantDefinitionsList : constantDefinition ( ';' constantDefinition )* ;

constantDefinition : identifier '=' constant ;

constant        locals [ Typespec_P2 typespec = null, 
                         Object value = null
                       ]  
    : sign? ( constantIdentifier | unsignedConstant )
    | characterConstant
    | stringConstant
    | booleanConstant
    ;

sign : '-' | '+' ;

constantIdentifier  locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ]
    : identifier ;
     
unsignedConstant    locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ]
    : integerConstant 
    | realConstant
    ;

integerConstant     locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ] 
    : INTEGER ;
    
realConstant        locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ]    
    : REAL;

characterConstant   locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ] 
    : CHARACTER ;
    
stringConstant      locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ]   
    : STRING ;
    
booleanConstant     locals [ Typespec_P2 typespec = null,
                             Object value = null
                           ]
    : TRUE | FALSE ;

typesPart : TYPE typeDefinitionsList ;
typeDefinitionsList : typeDefinition ( ';' typeDefinition )* ;

typeDefinition : identifier '=' typeSpecification ;

typeSpecification     locals [ Typespec_P2 typespec = null ]
    : typeIdentifier
    | ordinalType
    | structuredType 
    ;

typeIdentifier : identifier ;

ordinalType           locals [ Typespec_P2 typespec = null ] 
    : enumeratedType
    | subrangeType
    ;
    
structuredType        locals [ Typespec_P2 typespec = null ]
    : arrayType 
    | recordType
    | setType
    ;

enumeratedType        locals [ Typespec_P2 typespec = null ] 
    : '(' identifier ( ',' identifier )* ')' ;

subrangeType          locals [ Typespec_P2 typespec = null ] 
    : constant '..' constant ;

arrayType             locals [ Typespec_P2 typespec = null ] 
    : PACKED? ARRAY '[' dimensionList ']' OF elmtType ;

// indexType since ordinalType could include typeIdentifier
// see page 16 in the manual
setType               locals [ Typespec_P2 typespec = null ]
    : PACKED? SET OF indexType;

dimensionList : indexType ( ',' indexType )* ;
indexType     : typeIdentifier | ordinalType ;
elmtType      : typeSpecification;

recordType : /* empty */ ;

variablesPart            : VAR variableDeclarationsList ;
variableDeclarationsList : variableDeclarations 
                                        ( ';' variableDeclarations )* ;
           
variableDeclarations   : variableIdentifierList ':' typeSpecification ;
variableIdentifierList : identifier ( ',' identifier )* ;
    
procedureAndFunctionsPart : /* empty */ ;

PROGRAM   : P R O G R A M ;
CONST     : C O N S T ;
TYPE      : T Y P E ;
PACKED    : P A C K E D ;
ARRAY     : A R R A Y ;
SET       : S E T ;
OF        : O F ;
RECORD    : R E C O R D ;
VAR       : V A R ;
BEGIN     : B E G I N ;
END       : E N D ;
TRUE      : T R U E ;
FALSE     : F A L S E ;

IDENTIFIER : LETTER ( LETTER | DIGIT )*;
INTEGER    : DIGITS ;

REAL : DIGITS '.' DIGITS
     | DIGITS ('e' | 'E') ('+' | '-')? DIGITS
     | DIGITS '.' DIGITS ('e' | 'E') ('+' | '-')? DIGITS
     ;

LETTER : [a-zA-Z] ;
DIGIT  : [0-9];
DIGITS : DIGIT+ ;

CHARACTER : QUOTE CHARACTER_CHAR QUOTE ;
STRING    : QUOTE STRING_CHAR* QUOTE ;

WHITESPACE : [ \n\r\t]+ -> skip ; 
COMMENT    : '{' COMMENT_CHAR* '}' -> skip ;

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

fragment QUOTE          : '\'' ;
fragment CHARACTER_CHAR : ~'\'' ;
fragment STRING_CHAR    : QUOTE QUOTE | CHARACTER_CHAR ;
fragment COMMENT_CHAR   : ~'}' ;

grammar Pcl_P6 ;

@header 
{
    package intermediate.antlr4;
    
    import intermediate.symtab.SymtabEntry;
    import intermediate.type.Typespec_P6;
}

program           : programHeader block '.' ;
programHeader     : PROGRAM identifier programParameters? ';' ; 
programParameters : '(' identifier ( ',' identifier )* ')' ;

identifier      locals [ SymtabEntry entry = null,
                         Typespec_P6 typespec = null,
                         Object value = null
                       ] 
    : IDENTIFIER ;

block : declarations compoundStatement ;

// ============
// Declarations
// ============

declarations : ( constantsPart ';' )? ( typesPart ';' )? 
               ( variablesPart ';' )? ( procedureAndFunctionsPart ';')? ;
                
constantsPart           : CONST constantDefinitionsList ;
constantDefinitionsList : constantDefinition ( ';' constantDefinition )* ;

constantDefinition : identifier '=' constant ;

constant        locals [ Typespec_P6 typespec = null, 
                         Object value = null
                       ]  
    : sign? constantIdentifier  # constantSignedIdentifier
    | sign? integerConstant     # constantSignedInteger
    | sign? realConstant        # constantSignedReal
    | characterConstant         # constantCharacter
    | stringConstant            # constantString
    | booleanConstant           # constantBoolean
    ;

sign : '-' | '+' ;

constantIdentifier  locals [ Typespec_P6 typespec = null,
                             Object value = null
                           ]
    : identifier ;

integerConstant     locals [ Typespec_P6 typespec = null,
                             Object value = null
                           ] 
    : INTEGER ;
    
realConstant        locals [ Typespec_P6 typespec = null,
                             Object value = null
                           ]    
    : REAL;

characterConstant   locals [ Typespec_P6 typespec = null,
                             Object value = null
                           ] 
    : CHARACTER ;
    
stringConstant      locals [ Typespec_P6 typespec = null,
                             Object value = null
                           ]   
    : STRING ;
    
booleanConstant     locals [ Typespec_P6 typespec = null,
                             Object value = null
                           ]
    : TRUE | FALSE ;

typesPart           : TYPE typeDefinitionsList ;
typeDefinitionsList : typeDefinition ( ';' typeDefinition )* ;

typeDefinition : identifier '=' typeSpecification ;

typeSpecification   locals [ Typespec_P6 typespec = null ]
    : typeIdentifier
    | ordinalType
    | structuredType 
    ;

typeIdentifier : identifier ;

ordinalType         locals [ Typespec_P6 typespec = null ] 
    : enumeratedType
    | subrangeType
    ;
    
structuredType      locals [ Typespec_P6 typespec = null ]
    : arrayType 
    | recordType
    ;

enumeratedType      locals [ Typespec_P6 typespec = null ] 
    : '(' identifier ( ',' identifier )* ')' ;

subrangeType        locals [ Typespec_P6 typespec = null ] 
    : constant '..' constant ;

arrayType           locals [ Typespec_P6 typespec = null ] 
    : PACKED? ARRAY '[' dimensionList ']' OF elmtType ;
    
dimensionList : indexType ( ',' indexType )* ;
indexType     : typeIdentifier | ordinalType ;
elmtType      : typeSpecification;

recordType          locals [ Typespec_P6 typespec = null ]   
    : RECORD recordFields ';'? END ;
recordFields : variableDeclarationsList ;

variablesPart            : VAR variableDeclarationsList ;
variableDeclarationsList : variableDeclarations 
                                        ( ';' variableDeclarations )* ;
           
variableDeclarations   : variableIdentifierList ':' typeSpecification ;
variableIdentifierList : identifier ( ',' identifier )* ;
    
procedureAndFunctionsPart : procFuncDefinition ( ';' procFuncDefinition)* ;
procFuncDefinition        : ( procedureHead | functionHead ) ';' block ;
procedureHead             : PROCEDURE identifier parameters? ;
functionHead              : FUNCTION  identifier parameters? 
                                            ':' typeIdentifier ;

parameters                : '(' parameterDeclarationsList ')' ;
parameterDeclarationsList : parameterDeclarations 
                                        ( ';' parameterDeclarations )* ;
parameterDeclarations     : VAR? parameterIdentifierList ':' typeIdentifier ;
parameterIdentifierList   : identifier ( ',' identifier )* ;

// ==========
// Statements
// ==========

compoundStatement : BEGIN statementList END ;
statementList     : statement ( ';' statement )* ;

statement : compoundStatement
          | assignmentStatement
          | repeatStatement
          | writeStatement
          | writelnStatement
          | readStatement
          | readlnStatement
          | emptyStatement
          ;

assignmentStatement : variable ':=' expression ;

repeatStatement : REPEAT statementList UNTIL expression ;

writeStatement   : WRITE writeArguments ;
writelnStatement : WRITELN writeArguments? ;

writeArguments  locals [ String formatString, String arguments ]
    : '(' writeArgument (',' writeArgument)* ')' ;
    
writeArgument    : expression (':' format)? ;
format           : width (':' precision)? ;
width            : sign? integerConstant ;
precision        : integerConstant ;

readStatement   : READ readArguments ;
readlnStatement : READLN readArguments ;
readArguments   : '(' variable ( ',' variable )* ')' ;

emptyStatement    : /* empty */ ;

// ===========
// Expressions
// ===========

expression          locals [ Typespec_P6 typespec = null ] 
    : simpleExpression (relOp simpleExpression)? ;
    
simpleExpression    locals [ Typespec_P6 typespec = null ] 
    : sign? term (addOp term)* ;
    
term                locals [ Typespec_P6 typespec = null ]
    : factor (mulOp factor)* ;
       
relOp : '=' | '<>' | '<' | '<=' | '>' | '>=' ;
addOp : '+' | '-' | OR ;
mulOp : '*' | '/' | DIV | MOD | AND ;

factor              locals [ Typespec_P6 typespec = null ] 
    : variable              # factorVariable
    | integerConstant       # factorIntegerConstant   
    | realConstant          # factorRealConstant   
    | characterConstant     # factorCharacterConstant
    | stringConstant        # factorStringConstant
    | booleanConstant       # factorBooleanConstant   
    | functionCall          # factorFunctionCall
    | NOT factor            # factorNot
    | '(' expression ')'    # factorParens
    ;

variable            locals [ Typespec_P6 typespec = null, 
                             SymtabEntry entry = null
                           ] 
    : identifier modifier* ;

modifier  : '[' indexList ']' 
          | '.' field
          ;
          
indexList : index ( ',' index )* ;
index     : expression ; 

field               locals [ Typespec_P6 typespec = null, 
                             SymtabEntry entry = null
                           ]     
    : identifier ;

functionCall : functionName '(' argumentList ')' ;

argumentList : argument ( ',' argument )* ;
argument     : expression ;

functionName        locals [ Typespec_P6 typespec = null, 
                             SymtabEntry entry = null
                           ] 
    : identifier ;

// ======
// Tokens
// ======

AND       : A N D ;
ARRAY     : A R R A Y ;
BEGIN     : B E G I N ;
CASE      : C A S E ;
CONST     : C O N S T ;
DIV       : D I V ;
DO        : D O ;
DOWNTO    : D O W N T O ;
ELSE      : E L S E ;
END       : E N D ;
FALSE     : F A L S E ;
FOR       : F O R ;
FUNCTION  : F U N C T I O N ;
IF        : I F ;
MOD       : M O D ;
NOT       : N O T ;
OF        : O F ;
OR        : O R ;
PACKED    : P A C K E D ;
PROCEDURE : P R O C E D U R E ;
PROGRAM   : P R O G R A M ;
READ      : R E A D ;
READLN    : R E A D L N ;
RECORD    : R E C O R D ;
REPEAT    : R E P E A T ;
THEN      : T H E N ;
TO        : T O ;
TRUE      : T R U E ;
TYPE      : T Y P E ;
UNTIL     : U N T I L ;
VAR       : V A R ;
WHILE     : W H I L E ;
WITH      : W I T H ;
WRITE     : W R I T E ;
WRITELN   : W R I T E L N ;

IDENTIFIER : LETTER ( LETTER | DIGIT )*;
INTEGER    : DIGITS ;

REAL : DIGITS '.' DIGITS
     | DIGITS ('e' | 'E') ('+' | '-')? DIGITS
     | DIGITS '.' DIGITS ('e' | 'E') ('+' | '-')? DIGITS
     ;

LETTER : [a-zA-Z] ;
DIGIT  : [0-9];
DIGITS : DIGIT+ ;

CHARACTER : QUOTE STRING_CHAR QUOTE ;
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

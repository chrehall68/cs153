.class public TestWhileEdge
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static n I
.field private static visits I

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	TestWhileEdge/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this LTestWhileEdge;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; MAIN
;
.method public static main([Ljava/lang/String;)V
.var 0 is args [Ljava/lang/String;
.var 1 is _start Ljava/time/Instant;
.var 2 is _end Ljava/time/Instant;
.var 3 is _elapsed J

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_1

;
; 007 n:=5
;
	iconst_5
	putstatic	TestWhileEdge/n I
;
; 008 visits:=0
;
	iconst_0
	putstatic	TestWhileEdge/visits I
;
; 011 whilen<5dovisits:=visits+100
;
L001:
	getstatic	TestWhileEdge/n I
	iconst_5
	if_icmplt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
;
; 011 visits:=visits+100
;
	getstatic	TestWhileEdge/visits I
	bipush	100
	iadd
	putstatic	TestWhileEdge/visits I
	goto	L001
L002:
;
; 012 writeln('skipped = ',visits)
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"skipped = %d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	TestWhileEdge/visits I
	invokestatic	java/lang/Integer/valueOf(I)Ljava/lang/Integer;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
;
; 015 whilen>0dobeginn:=n-1;visits:=visits+1end
;
L005:
	getstatic	TestWhileEdge/n I
	iconst_0
	if_icmpgt	L007
	iconst_0
	goto	L008
L007:
	iconst_1
L008:
	ifeq	L006
;
; 016 n:=n-1
;
	getstatic	TestWhileEdge/n I
	iconst_1
	isub
	putstatic	TestWhileEdge/n I
;
; 017 visits:=visits+1
;
	getstatic	TestWhileEdge/visits I
	iconst_1
	iadd
	putstatic	TestWhileEdge/visits I
	goto	L005
L006:
;
; 019 writeln('ran = ',visits)
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"ran = %d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	TestWhileEdge/visits I
	invokestatic	java/lang/Integer/valueOf(I)Ljava/lang/Integer;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
;
; 022 whilefalsedovisits:=visits+100
;
L009:
	iconst_0
	ifeq	L010
;
; 022 visits:=visits+100
;
	getstatic	TestWhileEdge/visits I
	bipush	100
	iadd
	putstatic	TestWhileEdge/visits I
	goto	L009
L010:
;
; 023 writeln('final = ',visits)
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"final = %d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	TestWhileEdge/visits I
	invokestatic	java/lang/Integer/valueOf(I)Ljava/lang/Integer;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_2
	aload_1
	aload_2
	invokestatic	java/time/Duration/between(Ljava/time/temporal/Temporal;Ljava/time/temporal/Temporal;)Ljava/time/Duration;
	invokevirtual	java/time/Duration/toMillis()J
	lstore_3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n[%,d milliseconds execution time.]\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	lload_3
	invokestatic	java/lang/Long/valueOf(J)Ljava/lang/Long;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

	return

.limit locals 5
.limit stack 7
.end method

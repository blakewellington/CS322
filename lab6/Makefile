# Makefile for CS322 Lab6. (Jingke Li)
#
JFLAGS = -g
JC = javac
JCC = javacc

.SUFFIXES: .jj .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

ast0:	ast0/Ast0.class

ast0psr: ast0/ast0Parser.class

ir0:	ir0/IR0.class

ir0gen:	ast0psr ir0 IR0Gen.class

ir0gen2: ast0psr ir0 IR0Gen2.class

ir0gen3: ast0psr ir0 IR0Gen3.class

clean:
	'rm' ast0/*.class ir0/*.class *.class



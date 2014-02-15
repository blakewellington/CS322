# Makefile for CS322 Hw3. (Jingke Li)
#
JFLAGS = -g
JC = javac
JCC = javacc

.SUFFIXES: .jj .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

ir1gen:	ast1psr ir1 IR1Gen.class

ast1:	ast1/Ast1.class

ast1psr: ast1/ast1Parser.class

ir1:	ir1/IR1.class

clean: 	
	rm ast1/*.class ir1/*.class *.class

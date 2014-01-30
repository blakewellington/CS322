# Makefile for CS322 Lab4. (Jingke Li)
#
JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

all:	ir0gen

ast0psr: ast0/ast0Parser.class

ir0:	ir0/IR0.class

ir0gen:	ast0psr ir0 IR0Gen.class

clean:	
	rm ast0/*.class ir0/*.class *.class


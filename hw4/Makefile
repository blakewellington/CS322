# Makefile for CS322 Lab5. (Jingke Li)
#
JFLAGS = -g

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

all:	sc0gen

ast0psr: ast0/ast0Parser.class

sc0gen:	ast0psr SC0Gen.class

clean:	
	rm ast0/*.class *.class


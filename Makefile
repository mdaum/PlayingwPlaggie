# Makefile for the plagiarism detection parser. Build everything with "make all".
# This is currently just a quick hack, cleanup is needed.
#
# $Id: Makefile,v 1.11 2002/10/21 08:22:33 aleksi Exp $
# Author: Aleksi Ahtiainen <aleksi@cs.hut.fi>

JAVA=java
JAVAC=javac
CUP_PATH=/m/nfs1.niksula/home0/u0/aleksi/public_html/plaggie/test/plaggie
PLAGGIE_PATH=/m/nfs1.niksula/home0/u0/aleksi/public_html/plaggie/test/plaggie/cup
JFLAGS= -classpath .:$(PLAGGIE_PATH):$(CUP_PATH)

lexer: plag/parser/java/lex/*.java
	${JAVAC} ${JFLAGS} plag/parser/java/lex/*.java

cup: lexer plag/parser/java/java15.cup
	cd plag/parser/java && ${JAVA} ${JFLAGS} java_cup.Main -parser Grm15 -symbols Sym15 < java15.cup 2> Grm15.err && tail Grm15.err && cd ../..

parser: cup plag/parser/java/Grm15.java plag/parser/java/Sym15.java
	${JAVAC} ${JFLAGS} plag/parser/java/Grm15.java plag/parser/java/Sym15.java

java: plag/parser/java/*.java
	${JAVAC} ${JFLAGS} plag/parser/java/*.java

main: parser java plag/parser/*.java plag/parser/report/*.java plag/parser/plaggie/*.java
	${JAVAC} ${JFLAGS} plag/parser/*.java plag/parser/report/*.java plag/parser/plaggie/*.java

all: main

distro: force_update
	rm -rf plaggie
	mkdir plaggie
	mkdir plaggie/plag
	mkdir plaggie/plag/parser
	mkdir plaggie/plag/parser/java
	mkdir plaggie/plag/parser/java/lex
	mkdir plaggie/plag/parser/report	
	mkdir plaggie/plag/parser/plaggie
	cp -R plag/parser/*.java plaggie/plag/parser
	cp -R plag/parser/java/*.java plaggie/plag/parser/java
	cp -R plag/parser/java/*.cup plaggie/plag/parser/java
	cp -R plag/parser/java/lex/*.java plaggie/plag/parser/java/lex
	cp -R plag/parser/report/*.java plaggie/plag/parser/report
	cp -R plag/parser/plaggie/*.java plaggie/plag/parser/plaggie
	cp java_cup_v10j.tar.gz plaggie
	cp Makefile plaggie
	cp README_PLAGGIE plaggie
	cp COPYING.PLAGGIE plaggie
	cp COPYING.JAVA15GRAMMAR plaggie
	cp COPYING.CUP plaggie
	cp CHANGES.PLAGGIE plaggie
	cp plaggie.properties plaggie
	gtar cvzf plaggie.tar.gz plaggie

force_update:

package plag.parser.java;

/* Lexer.java.  Copyright (C) 1998 C. Scott Ananian.
 * This program is free software; see the file COPYING for more details.
 */

public interface Lexer {
    public java_cup.runtime.Symbol nextToken() throws java.io.IOException, plag.parser.java.lex.LexerException;
    public void errorMsg(String msg, java_cup.runtime.Symbol info);
}

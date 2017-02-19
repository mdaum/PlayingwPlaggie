/* 
 *  Copyright (C) 2006 Aleksi Ahtiainen, Mikko Rahikainen.
 * 
 *  This file is part of Plaggie.
 *
 *  Plaggie is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.
 *
 *  Plaggie is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaggie; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301  USA
 */
package plag.parser.java;

import plag.parser.TokenList;
import plag.parser.Token;
import java.io.*;
import java.util.*;

/**
 * Implements the plagiarisim detection support to Java parsing.
 *
 */
public class PlagSym {

    /** The String to prepend to each line of multiline code, if it 
     * is printed */
    private static final String PRE_MULTILINE = "";
    
    /** How many code lines maximum are printed. Defaults to 1, if set
     * to 0, no code lines printed */
    private static int multiLineThreshold = 1;

    /** Should the line numbers be printed */
    private static boolean printLineNumbers = true;

    /** Should the value strings be one character long */
    private static boolean useOneCharacterValueStrings = false;

    /** String to use as separator between tokens */
    private static String separator = "\n";

    /** The beginning character number of each line */
    private static int[] lineStarts = null;
    
    /** All the characters in the checked file */
    private static StringBuffer fileContents = null;

    /** All the tokens as Token objects */
    private static TokenList tokens = null;

    /** Column length to use for printing the code on separate column */
    private static final int COL_LENGTH = 40;

    /**
     * Returns the line number of the character at charNumber.
     */
    private static int getLineNumber(int charNumber) {
	for (int i = 1; i < lineStarts.length; i++) {
	    if ((charNumber >= lineStarts[i-1]) && (charNumber < lineStarts[i])) {
		return i;
	    }
	}
	if (charNumber == lineStarts[lineStarts.length-1]) 
	    return lineStarts.length;
	return -1;
    }

    /**
     * Returns the characters indicated by the given indices, which
     * are also included in the returned string.
     */
    private static String getChars(int leftChar, int rightChar) {
	return fileContents.substring(leftChar, rightChar+1);
    }

    private static String createEmptyString(int l) {
	String s = "";
	for (int i = 0; i < l; i++) {
	    s+=" ";
	}
	return s;
    }

    /**
     * Adds a token.
     */
    public static void addToken(int leftChar, int rightChar, int value) {
	int lineLeft = getLineNumber(leftChar);
	int lineRight = getLineNumber(rightChar);

	Token t = new Token(lineLeft, lineRight, leftChar, rightChar, value);

	tokens.addToken(t);
	
    }

    public static void init(String filename, int multilineThreshold,
			    boolean printLineNumbers) {
	PlagSym.printLineNumbers = printLineNumbers;
	init(filename, multilineThreshold);
    }

    public static void init(String filename, int multilineThreshold) {
	PlagSym.multiLineThreshold = multilineThreshold;
	PlagSym.init(filename);
    }

    /**
     * Initializes the token printing for the given file.
     */
    public static void init(String filename) {
	initValueStrings();
	fileContents = new StringBuffer(10000);
	tokens = new TokenList(filename);
	try {
	    File f = new File(filename);
	    int length = (int)f.length();
	    
	    FileReader fis = new FileReader(f);
	    LineNumberReader b = new LineNumberReader(fis);

	    Vector vLineStarts = new Vector();
	    int curLine = -1;

	    /* Create the chars array and collect the line starts to a
	     * vector */
	    for (int i=0; i < length; i++) {
		if (b.getLineNumber() > curLine) {
		    curLine = b.getLineNumber();
		    vLineStarts.add(new Integer(i));
		}
		fileContents.append((char)b.read());
	    }

	    b.close();
	    fis.close();

	    /* Create the array of line start character numbers */
	    lineStarts = new int[vLineStarts.size()];
	    for (int i = 0; i < vLineStarts.size(); i++) {
		Integer j = (Integer)vLineStarts.get(i);
		lineStarts[i] = j.intValue();
	    }

	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    /**
     *  Only initializes the value strings. Has to be done, if used with results read from a file.
     */
    public static void init() {
	initValueStrings();
    }

    public final static String getValueString(int value) {
	if (!useOneCharacterValueStrings) {
	    return valueStrings[value];
	}
	else {
	    return ""+((char)(((int)'A')+value));
	}

		
    }

    public static TokenList getTokenList() {
	return tokens;
    }

    public final static int IMPORT_DECLARATION = 1;
    public final static int CLASS_DECLARATION = 2;
    public final static int INTERFACE_DECLARATION = 3;
    public final static int VARIABLE_DECLARATION = 4;
    public final static int METHOD_DECLARATION = 5;
    public final static int STATIC_INITIALIZATION = 6;
    public final static int CONSTRUCTOR_DECLARATION = 7;
    public final static int CONSTRUCTOR_INVOCATION_THIS = 8;
    public final static int CONSTRUCTOR_INVOCATION_SUPER = 9;
    public final static int CONSTANT_DECLARATION = 10;
    public final static int ABSTRACT_METHOD_DECLARATION = 11;
    public final static int SWITCH = 12;
    public final static int BREAK = 13;
    public final static int CONTINUE = 14;
    public final static int RETURN = 15;
    public final static int SYNCHRONIZED = 16;
    public final static int THROW = 17;
    public final static int TRY = 18;
    public final static int ASSERT = 19;
    public final static int ASSIGNMENT = 20;
    public final static int METHOD_INVOCATION = 21;
    public final static int NEW = 22;
    public final static int BLOCK = 23;
    public final static int CATCH = 24;
    public final static int FINALLY = 25;
    public final static int CASE = 26;
    public final static int ELSE = 27;
    public final static int IF = 28;
    public final static int DO = 29;
    public final static int INNER_CLASS_DECLARATION = 30;
    public final static int INNER_INTERFACE_DECLARATION = 31;
    public final static int INNER_CLASS_DECLARATION_END = 32;
    public final static int INNER_INTERFACE_DECLARATION_END = 33;
    public final static int CLASS_DECLARATION_END = 34;
    public final static int INTERFACE_DECLARATION_END = 35;
    public final static int METHOD_DECLARATION_END = 36;
    public final static int CONSTRUCTOR_DECLARATION_END = 37;
    public final static int SYNCHRONIZED_END = 38;
    public final static int DO_END = 39;
    public final static int WHILE_END = 40;
    public final static int SWITCH_END = 41;
    public final static int WHILE = 42;
    public final static int BLOCK_END = 43;
    public final static int FOR = 44;
    public final static int FOR_END = 45;
    
    public final static int CATCH_END = 46;
    public final static int FINALLY_END = 47;
    public final static int TRY_END = 48;
    
    public final static int PACKAGE_DECLARATION = 49;

    public final static int ANONYMOUS_INNER_CLASS = 50;
    public final static int ANONYMOUS_INNER_CLASS_END = 51;

    public final static int IF_END = 52;
    public final static int ELSE_END = 53;
    public final static int ENUM_DECLARATION = 54;
    public final static int ENUM_DECLARATION_END = 55;
    public final static int INNER_ENUM_DECLARATION = 56;
    public final static int INNER_ENUM_DECLARATION_END = 57;

    private static String[] valueStrings = 
	new String[INNER_ENUM_DECLARATION_END+1];
    
    private static void initValueStrings() {
	valueStrings[IMPORT_DECLARATION] = "IMPORT_DECLARATION";
	valueStrings[CLASS_DECLARATION] = "CLASS_DECLARATION";
	valueStrings[INTERFACE_DECLARATION] = "INTERFACE_DECLARATION";
	valueStrings[VARIABLE_DECLARATION] = "VARIABLE_DECLARATION";
	valueStrings[METHOD_DECLARATION] = "METHOD_DECLARATION";
	valueStrings[STATIC_INITIALIZATION] = "STATIC_INIT";
	valueStrings[CONSTRUCTOR_DECLARATION] = "CONSTRUCTOR_DECLARATION";
	valueStrings[CONSTRUCTOR_INVOCATION_THIS] = "CONSTRUCTOR_INVOCATION_THIS";
	valueStrings[CONSTRUCTOR_INVOCATION_SUPER] = "CONSTRUCTOR_INVOCATION_SUPER";
	valueStrings[CONSTANT_DECLARATION] = "CONSTANT_DECLARATION";
	valueStrings[ABSTRACT_METHOD_DECLARATION] = "ABSTRACT_METHOD_DECLARATION";
	valueStrings[SWITCH] = "SWITCH";
	valueStrings[BREAK] = "BREAK";
	valueStrings[CONTINUE] = "CONTINUE";
	valueStrings[RETURN] = "RETURN";
	valueStrings[SYNCHRONIZED] = "SYNCHRONIZED";
	valueStrings[THROW] = "THROW";
	valueStrings[TRY] = "TRY";
	valueStrings[ASSERT] = "ASSERT";
	valueStrings[ASSIGNMENT] = "ASSIGNMENT";
	valueStrings[METHOD_INVOCATION] = "METHOD_INVOCATION";
	valueStrings[NEW] = "NEW";
	valueStrings[BLOCK] = "BLOCK";
	valueStrings[CATCH] = "CATCH";
	valueStrings[FINALLY] = "FINALLY";
	valueStrings[CASE] = "CASE";
	valueStrings[ELSE] = "ELSE";
	valueStrings[IF] = "IF";
	valueStrings[DO] = "DO";
	valueStrings[INNER_CLASS_DECLARATION] = "INNER_CLASS_DECLARATION";
	valueStrings[INNER_INTERFACE_DECLARATION] = "INNER_INTERFACE_DECLARATION";

	valueStrings[INNER_CLASS_DECLARATION_END] = "INNER_CLASS_DECLARATION_END";
	valueStrings[INNER_INTERFACE_DECLARATION_END] = "INNER_INTERFACE_DECLARATION_END";
	valueStrings[CLASS_DECLARATION_END] = "CLASS_DECLARATION_END";
	valueStrings[INTERFACE_DECLARATION_END] = "INTERFACE_DECLARATION_END";
	valueStrings[METHOD_DECLARATION_END]  = "METHOD_DECLARATION_END";
	valueStrings[CONSTRUCTOR_DECLARATION_END] = "CONSTRUCTOR_DECLARATION_END";

	valueStrings[SYNCHRONIZED_END] = "SYNCHRONIZED_END";
	valueStrings[DO_END] = "DO_END";
	valueStrings[WHILE_END] = "WHILE_END";
	valueStrings[SWITCH_END] = "SWITCH_END";
		   
	valueStrings[WHILE] = "WHILE";
	valueStrings[BLOCK_END] = "BLOCK_END";

	valueStrings[FOR] = "FOR";
	valueStrings[FOR_END] = "FOR_END";

	valueStrings[CATCH_END] = "CATCH_END";
	valueStrings[FINALLY_END] = "FINALLY_END";
	valueStrings[TRY_END] = "TRY_END";

	valueStrings[PACKAGE_DECLARATION] = "PACKAGE_DECLARATION";

	valueStrings[ANONYMOUS_INNER_CLASS] = "ANONYMOUS_INNER_CLASS";
	valueStrings[ANONYMOUS_INNER_CLASS_END] = "ANONYMOUS_INNER_CLASS_END";

	valueStrings[IF_END] = "IF_END";
	valueStrings[ELSE_END] = "ELSE_END";
	valueStrings[ENUM_DECLARATION]="ENUM_DECLARATION";
	valueStrings[ENUM_DECLARATION_END]="ENUM_DECLARATION_END";
	valueStrings[INNER_ENUM_DECLARATION]="INNER_ENUM_DECLARATION";
	valueStrings[INNER_ENUM_DECLARATION_END]="INNER_ENUM_DECLARATION_END";
    }
}

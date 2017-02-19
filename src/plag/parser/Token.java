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
package plag.parser;

import java.util.Comparator;
import java.io.Serializable;

/**
 * Represents a token within source code. A token can be for example
 * the start of a declaration, a return call, start of a for loop
 * etc.
 * <p>
 * Tokens are comparable according to their value.
 */
public class Token 
    implements Comparable, Serializable {

    /** The line from which this token starts in the source code */ 
    private int startLine;
    /** The line at which this token ends in the source code */
    private int endLine;
    /** The character from which this token starts in the source code */
    private int startChar;
    /** The character at which this token ends in the source code */
    private int endChar;
    
    /** The value associated to this token */
    private int value;

    /**
     * Creates a new token
     *
     * @param startLine The line from which this token starts in the
     * source cod
     * @param endLine The line at which this token ends in the source
     * code
     * @param startChar The character from which this token starts in
     * the source code
     * @param endChar The character at which this token ends in the
     * source code
     * @param value The value associated to this token
     */
    public Token(int startLine, int endLine, int startChar, int endChar,
		 int value) {
	this.startLine = startLine;
	this.endLine = endLine;
	this.startChar = startChar;
	this.endChar = endChar;
	this.value = value;
    }
    
    /**
     * Returns the start line of this token.
     */
    public int getStartLine() {
	return this.startLine;
    }

    /**
     * Returns the end line of this token.
     */
    public int getEndLine() {
	return this.endLine;
    }
    
    /**
     * Returns the start character of this token.
     */
    public int getStartChar() {
	return this.startChar;
    }
    
    /**
     * Returns the end character of this token.
     */
    public int getEndChar() {
	return this.endChar;
    }

    /**
     * Returns the value associated to this token.
     */
    public int getValue() {
	return this.value;
    }

    /**
     * Returns the number of lines spanned by this token.
     */
    public int getLineCount() {
	return endLine - startLine + 1;
    }
    
    /** 
     * Returns the number of characters spanned by this token.
     */
    public int getCharCount() {
	return endChar - startChar + 1;
    }

    /**
     * A comparator, which orders tokens according to the      
     * characters at which they occur.
     */
    public static class OrderComparator 
	implements Comparator, Serializable {
	
	public int compare(Object o1, Object o2) {
	    Token t1 = (Token)o1;
	    Token t2 = (Token)o2;
	    
	    if (t1.getStartChar() < t2.getStartChar()) {
		return -1;
	    }
	    else if (t1.getStartChar() > t2.getStartChar()) {
		return 1;
	    }
	    else {
		// equal startChars
		if (t1.getEndChar() > t2.getEndChar()) {
		    // Ends later, so comes later in order
		    return 1;
		}
		else if (t1.getEndChar() < t2.getEndChar()) {
		    return -1;
		}
		else {
		    // equal start and end chars
		    if (t1.getValue() < t2.getValue()) {
			return -1;
		    }
		    else if (t1.getValue() > t2.getValue()) {
			return 1;
		    }
		    else {
			// equal start and end chars and values
			return 0;
		    }
		    
		}
	    }
	}

    }
	
    /**
     * Compares this token's value to another one's value.
     */
    public int compareTo(Object o2) {
	Token t2 = (Token)o2;
	if ((this.value - t2.getValue()) != 0) 
	    return (this.value - t2.getValue());
	else 
	    return -1;
	
    }
}

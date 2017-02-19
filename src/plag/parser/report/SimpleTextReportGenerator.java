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
package plag.parser.report;

import plag.parser.*;

import java.io.PrintStream;
import java.io.BufferedReader;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;

/**
 * A report generator, which generates a simple textual report of
 * detection results.
 *
 */
public class SimpleTextReportGenerator
    implements ReportGenerator
{


    /** The print stream at which the report is generated. */
    private PrintStream out;

    /** Whether to print generated token lists in the report */
    private boolean printTokenLists;

    // These below are all related to printing token lists:
    /** Whether to print line numbers with token lists */
    private boolean printLineNumbers = true;
    /** Multiline threshold used when printing token lists */
    private int multiLineThreshold = 1;
    /** The String to prepend to each line of multiline code, if it 
     * is printed */
    private static final String PRE_MULTILINE = "";
    /** Column length to use for printing the code on separate column */
    private static final int COL_LENGTH = 40;
    /** String to use as separator between tokens */
    private static String separator = "\n";

    private CodeTokenizer codeTokenizer = null;

    /**
     * Generates a report generator, which prints on the standard
     * output and doesn't print the token lists in the report.
     */
    public SimpleTextReportGenerator() {
	this(System.out, false, null);
    }

    /**
     * Generates a report generator, which prints on the given output
     * stream and doesn't print the token lists.
     */
    public SimpleTextReportGenerator(PrintStream out) {
	this(out, false, null);
    }

    /**
     * Generates a report generator, which prints on the given output
     * stream, prints token lists, if the given parameter is true by
     * using the given codeTokenizer.
     */
    public SimpleTextReportGenerator(PrintStream out, boolean printTokenLists,
				     CodeTokenizer codeTokenizer)
    {
	if (printTokenLists && codeTokenizer==null) {
	    throw new IllegalArgumentException("If token lists are printed, code tokenizer must be given.");
	}
	this.printTokenLists = printTokenLists;
	this.out = out;
	this.codeTokenizer = codeTokenizer;
    }
    
    /**
     * Generates a StringBuffer of the lines read from the given
     * buffered reader.
     */
    private StringBuffer getChars(BufferedReader br) 
	throws IOException 
    {

	StringBuffer fileContents = new StringBuffer(10000);
	
	String line;
	while ( (line = br.readLine()) != null) {
	    fileContents.append(line+"\n");
	}
	br.close();
	return fileContents;
    }
    
    /**
     * Returns the substring in chars starting from leftChar and
     * ending at rightChar
     */
    private String getChars(int leftChar, int rightChar, StringBuffer chars)
    {
	return chars.substring(leftChar, rightChar+1);
    }

    /**
     * Returns a string of space-characters.
     */
    private String createEmptyString(int l) {
	String s = "";
	for (int i = 0; i < l; i++) {
	    s+=" ";
	}
	return s;
    }
    
    /**
     * Prints the tokens and the associated code specified in the
     * given token list using the file read from the given reader.
     */
    private void printTokens(TokenList tokens,
			     BufferedReader br) 
	throws IOException 
    {

	StringBuffer fileContents = getChars(br);
	Iterator iter = tokens.iterator();
	
	int count = 0;

	while (iter.hasNext()) {
	    Token t = (Token)iter.next();
	    String ps = count+":";

	    int lineLeft = t.getStartLine();
	    int lineRight = t.getEndLine();
	    int leftChar = t.getStartChar();
	    int rightChar = t.getEndChar();
	    int value = t.getValue();

	    if (printLineNumbers) {
		ps += lineLeft+"("+leftChar+")-"+
 		    lineRight+"("+rightChar+"):";
	    }
	    ps += codeTokenizer.getValueString(value);
	    if (printLineNumbers) {
		ps += ":";
	    }
	    if (multiLineThreshold > 0) {
		/* Print multiLineThreshold number of lines */
		if (lineLeft == lineRight) {
		    ps += createEmptyString(COL_LENGTH - ps.length());
		    ps += getChars(leftChar, rightChar, fileContents)+separator;
		    out.print(ps);
		}
		else {
		    String s = getChars(leftChar, rightChar, fileContents);
		    int curCount = 0;
		    int start = 0;
		    int end = s.indexOf("\n");
		    // First line:
		    ps += createEmptyString(COL_LENGTH - ps.length());
		    ps += s.substring(start, end)+separator;
		    out.print(ps);
		    
		    start = end+1;
		    curCount++;
		    while ((curCount < multiLineThreshold) && 
			   ( (end = s.indexOf("\n",start)) != -1)) {
			out.print(createEmptyString(COL_LENGTH)+PRE_MULTILINE+s.substring(start, end)+separator);
			start = end+1;
			curCount++;
		    }
		    // Print the remaining characters
		    if (curCount < multiLineThreshold) {
			out.print(createEmptyString(COL_LENGTH)+PRE_MULTILINE+s.substring(start)+separator);
		    }
		}
	    }
	    else {
		out.print(separator);
	    }
	    count++;
	}
    }

    /**
     * Generates a report of the given detection result.
     */
    public void generateReport(DetectionResult dR) 
	throws Exception 
    {
	out.println("========================================================================");

	out.println("File A:"+dR.getFileA().getPath());
	out.println("File B:"+dR.getFileB().getPath());
	out.println("File A similarity:"+dR.getSimilarityA());
	out.println("File B similarity:"+dR.getSimilarityB());
	out.println("------------------------------------------------------------------------");
	if (printTokenLists) {
	    out.println("Tokens A:");
	    this.printTokens(dR.getTokensA(), dR.getFileReaderA());
	    out.println("------------------------------------------------------------------------");
	    out.println("Tokens B:");
	    this.printTokens(dR.getTokensB(), dR.getFileReaderB());
	    out.println("------------------------------------------------------------------------");
	}
	out.println("All matched tiles:");
	out.println(dR.getMatches());
	out.println("------------------------------------------------------------------------");
	out.println("File A with matched lines:");

	// NOW, BEWARE, THIS GETS REALLY UGLY!!!
	MatchedTileSet mtSet = dR.getMatches();
	mtSet.setOrdering(MatchedTileSet.ORDER_BY_A);
	BufferedReader br = dR.getFileReaderA();
	
	String line;
	boolean withinMatch = false;
	Iterator it = mtSet.iterator();
	MatchedTile mt = null;
	if (it.hasNext()) {
	    mt = (MatchedTile)it.next();
	}
	int lineCount = 0;
	int matchCount = 0;
	while ( (line = br.readLine()) != null) {
	    lineCount++;
	    if ((!withinMatch) && (mt != null) && 
		(mt.getTileA().getStartToken().getStartLine() == lineCount)) {
		// We moved into new matched tile
		withinMatch = true;
		matchCount++;
		out.print(mt.getId());
		// Scan through all the matches ending on this same line
		while ( (mt != null) && (mt.getTileA().getEndToken().getEndLine() == lineCount)) {
		    withinMatch = false;
		    if (it.hasNext()) {
			mt = (MatchedTile)it.next();
			if (mt.getTileA().getStartToken().getStartLine() == lineCount) {
				// And the next match starts on this same line, too...
			    withinMatch = true;
			    matchCount++;
			    out.print(","+mt.getId());
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		out.println(":"+line);
	    }
	    else if ((withinMatch) && 
		     (mt.getTileA().getEndToken().getEndLine() == lineCount)) {
		// We are on the last line of a match
		withinMatch = false;
		out.print(mt.getId());
		if (it.hasNext()) {
		    mt = (MatchedTile)it.next();
		    if (mt.getTileA().getStartToken().getStartLine() == lineCount) {
			withinMatch = true;
			matchCount++;
			out.print(","+mt.getId());
		    }
		}
		else {
		    mt = null;
		}
		// Scan through all the remaining matches, that happen to end on this same line
		while ( (mt != null) &&
			(mt.getTileA().getEndToken().getEndLine() == lineCount)) {
		    withinMatch = false;
		    if (it.hasNext()) {
			mt = (MatchedTile)it.next();
			if (mt.getTileA().getStartToken().getStartLine() == lineCount) {
				// And the next one starts here, too
			    withinMatch = true;
			    matchCount++;
			    out.print(","+mt.getId());
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		out.println(":"+line);
	    }
	    else if (withinMatch) {
		// We are in the middle of a match
		out.println(mt.getId()+":"+line);
	    }
	    else {
		// We are outside a match
		out.println(line);
	    }
	}
	
	br.close();

	out.println("------------------------------------------------------------------------");
	out.println("File B with matched lines:");

	// NOW, BE VERY SCARED, THIS GETS EVEN UGLIER -- COPY PASTING THE PREVIOUS CODE WITH SOME
	// METHOD NAME CHANGES ONLY...

	mtSet.setOrdering(MatchedTileSet.ORDER_BY_B);
	
	Debug.print(mtSet.toString());
	
	br = dR.getFileReaderB();
	    
	withinMatch = false;
	it = mtSet.iterator();
	mt = null;
	if (it.hasNext()) {
	    mt = (MatchedTile)it.next();
	}
	lineCount = 0;
	matchCount = 0;
	while ( (line = br.readLine()) != null) {
	    lineCount++;
	    if ((!withinMatch) && (mt != null) && 
		(mt.getTileB().getStartToken().getStartLine() == lineCount)) {
		// We moved into new matched tile
		withinMatch = true;
		matchCount++;
		out.print(mt.getId());
		// Scan through all the matches ending on this same line
		while ( (mt != null) && (mt.getTileB().getEndToken().getEndLine() == lineCount)) {
		    withinMatch = false;
		    if (it.hasNext()) {
			mt = (MatchedTile)it.next();
			if (mt.getTileB().getStartToken().getStartLine() == lineCount) {
				// And the next match starts on this same line, too...
			    withinMatch = true;
			    matchCount++;
			    out.print(","+mt.getId());
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		out.println(":"+line);
	    }
	    else if ((withinMatch) && 
		     (mt.getTileB().getEndToken().getEndLine() == lineCount)) {
		// We are on the last line of a match
		withinMatch = false;
		out.print(mt.getId());
		if (it.hasNext()) {
		    mt = (MatchedTile)it.next();
		    if (mt.getTileB().getStartToken().getStartLine() == lineCount) {
			withinMatch = true;
			matchCount++;
			out.print(","+mt.getId());
		    }
		}
		else {
		    mt = null;
		}
		// Scan through all the remaining matches, that happen to end on this same line
		while ( (mt != null) &&
			(mt.getTileB().getEndToken().getEndLine() == lineCount)) {
		    withinMatch = false;
		    if (it.hasNext()) {
			mt = (MatchedTile)it.next();
			if (mt.getTileB().getStartToken().getStartLine() == lineCount) {
				// And the next one starts here, too
			    withinMatch = true;
			    matchCount++;
			    out.print(","+mt.getId());
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		out.println(":"+line);
	    }
	    else if (withinMatch) {
		// We are in the middle of a match
		out.println(mt.getId()+":"+line);
	    }
	    else {
		// We are outside a match
		out.println(line);
	    }
	}
	
	br.close();
    }

    /**
     * Generates a report of each given detection result.
     */
    public void generateReport(List detectionResults) 
	throws Exception
    {
	Iterator iter = detectionResults.iterator();

	while (iter.hasNext()) {
	    this.generateReport((DetectionResult)iter.next());
	}
    }

}

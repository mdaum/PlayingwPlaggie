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

import java.io.*;
import java.util.*;
import plag.parser.*;
import java.text.NumberFormat;


/**
 * A report generator, that creates an HTML report of detection results.
 *
 */
public class SimpleHtmlSubmissionReportGenerator
    implements SubmissionReportGenerator
{
	/** Used for color coding results */
	private HashMap<Integer, String> colorMap;
	
    /** Used in naming the sub directories when generating report of several submissions */
    private int subNumber=1;

    /** Used to store the number of each submission */
    private HashMap subNumbers;

    /** The base directory to use */
    private File baseDir;

    /** The code tokenizer to use for printing the token values */
    private CodeTokenizer codeTokenizer;

    /** The String to prepend to each line of multiline code, if it 
     * is printed */
    private static final String PRE_MULTILINE = "";
    /** Column length to use for printing the code on separate column */
    private static final int COL_LENGTH = 40;
    /** String to use as separator between tokens */
    private static String separator = "\n";

    private int maxDetResultsToReport = 0;

    public SimpleHtmlSubmissionReportGenerator(File baseDir,
					       CodeTokenizer codeTokenizer,
					       int maxDetResultsToReport) {
	this.baseDir = baseDir;
	this.codeTokenizer = codeTokenizer;
	this.maxDetResultsToReport = maxDetResultsToReport;
	//Create palette of colors
	HexColors colors = new HexColors();
	this.colorMap = colors.GetRandomHexColor(50);
    }

    /**
     * Changes the ampersand, less than and greater than characters to
     * their html form in the givern string.
     */
    private String txt2html(String t) {
	StringBuffer hb = new StringBuffer();
	for (int i=0; i < t.length(); i++) {
	    char c = t.charAt(i);
	    switch (c) {
	    case '<':
		hb.append("&lt;");
		break;
	    case '>':
		hb.append("&gt;");
		break;
	    case '&':
		hb.append("&amp;");
		break;
	    default:
		hb.append(c);
	    }
	}
	return hb.toString();
    }

    /**
     * Generates the index.html file for the single submission detection
     * report page.
     *
     * @param file is the file to be generated
     * @param topFile is the HTML file appearing in the top fram
     * @param leftFile is the HTML file appearing in the left frame
     * @param rightFile is the HTML file appearing in the right frame
     * @param detResult is the detection result appearing on this page
     */
    private void generateFramesetIndex(File file, 
				       String topFile,
				       String leftFile,
				       String rightFile,
				       SubmissionDetectionResult detResult) 
	throws Exception
    {
	PrintStream f = new PrintStream(new FileOutputStream(file));
	
	String subAName = detResult.getSubmissionA().getName();
	String subBName = detResult.getSubmissionB().getName();

	f.println("<html>");
	f.println("<head>");
	f.println("<title>");
	f.println("Detection results for "+subAName+" and "+subBName);
	f.println("</title>");
	f.println("</head>");

	if (detResult.getFileDetectionResults().size() > 0) {
	    f.println("<frameset ROWS=\"230,100,*\">");
	    f.println("  <frame SRC=\""+topFile+"\" name=\"top\">");
	    f.println("  <frame SRC=\"matchedtiles1.html\" name=\"top2\">");
	    f.println("  <frameset COLS=\"50%,50%\">");
	    f.println("     <frame SRC=\""+leftFile+"\" name=\"left\">");
	    f.println("     <frame SRC=\""+rightFile+"\" name=\"right\">");
	    f.println("  </frameset>");
	    f.println("</frameset>");
	}
	else {
	    // If there are no file results, the frameset only contains the top file
	    f.println("<frameset>");
	    f.println("  <frame SRC=\""+topFile+"\" name=\"top\">");
	    f.println("</frameset>");
	}

	f.println("</html>");

	f.close();
    }

    /**
     * Generates the file appearing in the top frame in the single
     * submission detection result report.
     */    
    private void generateTopFile(File file,
				 SubmissionDetectionResult detResult) 
	throws Exception 
    {

	String subAName = detResult.getSubmissionA().getName();
	String subBName = detResult.getSubmissionB().getName();

	PrintStream f = new PrintStream(new FileOutputStream(file));
	
	// Generate information about the similarity values

	f.println("<html>");
	f.println("<head>");
	f.println("<title>");
	f.println("Detection results for "+subAName+" and "+subBName);
	f.println("</title>");
	f.println("</head>");
	f.println("<body>");
	f.println("<a href=\"../index.html\" target=\"_top\">Index</a>");
	f.println("<h1>Detection results for "+subAName+" and "+subBName+"</h1>");
	f.println("<br>Similarity:<b>"+Stats.getPercentage(detResult.getSimilarityA()*detResult.getSimilarityB())+"</b>");
	f.println("<br>Similarity A:<b>"+Stats.getPercentage(detResult.getSimilarityA())+"</b>");
	f.println("<br>Similarity B:<b>"+Stats.getPercentage(detResult.getSimilarityB())+"</b>");
	f.println("<p>");
	f.println("<A TARGET=\"left\" HREF=\""+
		  "../sub"+(Integer)subNumbers.get(detResult.getSubmissionA())+
		  "/simdistr.html"+
		  "\">Similarity distribution of "+subAName+"</A><BR>");
	f.println("<A TARGET=\"right\" HREF=\""+
		  "../sub"+(Integer)subNumbers.get(detResult.getSubmissionB())+
		  "/simdistr.html"+
		  "\">Similarity distribution of "+subBName+"</A><BR>");


	// Go through the file detectino results and generate a table
	// of them
	Iterator iter = detResult.getFileDetectionResults().iterator();
	
	f.println("<TABLE COLS=\"7\" BORDER=\"1\">");
	
	int fileCounter = 1;

	while (iter.hasNext()) {
	    DetectionResult fileDetRes = (DetectionResult)iter.next();
	    f.println("<TR><TD>");
	    f.println("<A HREF=\"filea"+fileCounter+".html\" TARGET=\"left\">");
	    f.println(fileDetRes.getFileA().getPath());
	    f.println("</A></TD><TD>");
	    f.println(Stats.getPercentage(fileDetRes.getSimilarityA()));
	    f.println("</TD><TD>");
	    f.println("<A HREF=\"tokensa"+fileCounter+".html\" TARGET=\"left\">");
	    f.println("Tokens");
	    f.println("</A>");
	    f.println("</TD><TD>");
	    f.println("<A HREF=\"fileb"+fileCounter+".html\" TARGET=\"right\">");
	    f.println(fileDetRes.getFileB().getPath());
	    f.println("</A></TD><TD>");
	    f.println(Stats.getPercentage(fileDetRes.getSimilarityB()));
	    f.println("</TD><TD>");
	    f.println("<A HREF=\"tokensb"+fileCounter+".html\" TARGET=\"right\">");
	    f.println("Tokens");
	    f.println("</A>");
	    f.println("</TD><TD>");
	    f.println("<A HREF=\"matchedtiles"+fileCounter+".html\" TARGET=\"top2\">");
	    f.println("Matched tiles");
	    f.println("</A>");
	    f.println("</TD></TR>");
	    fileCounter++;
	}
	f.println("</TABLE>");
	f.println("</BODY>");
	f.println("</HTML>");

	f.close();
    }


    /**
     * Creates the pages containing the program code of the files with
     * tags in order to easily locate the similar sections. Creates
     * two files named filea[counter].html and fileb[counter].html in
     * the given directory.
     */
    public void generateFiles(File dir, DetectionResult dR, int counter)
	throws Exception
    {
	PrintStream fa = new PrintStream(new FileOutputStream(dir.getPath()+File.separator+"filea"+counter+".html"));
	PrintStream fb = new PrintStream(new FileOutputStream(dir.getPath()+File.separator+"fileb"+counter+".html"));

	fa.println("<HTML>");
  	fa.println("<HEAD>");
  	fa.println("<TITLE>"+dR.getFileA().getPath()+"</TITLE>");
  	fa.println("</HEAD>");
  	fa.println("<BODY>");
  	fa.println("<H1>"+dR.getFileA().getPath()+"</H1>");
  	fa.println("<CODE><PRE>");

  	fb.println("<HTML>");
  	fb.println("<HEAD>");
  	fb.println("<TITLE>"+dR.getFileB().getPath()+"</TITLE>");
  	fb.println("</HEAD>");
  	fb.println("<BODY>");
  	fb.println("<H1>"+dR.getFileB().getPath()+"</H1>");
  	fb.println("<CODE><PRE>");

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
		fa.print("<A NAME=\"match"+mt.getId()+"\"></A>");
		fa.print("<A HREF=\"fileb"+counter+".html#match"+mt.getId()+"\" TARGET=\"right\">");
		fa.print(mt.getId());
		fa.print("</A>");
		// Scan through all the matches ending on this same line
		while ( (mt != null) && (mt.getTileA().getEndToken().getEndLine() == lineCount)) {
		    withinMatch = false;
		    if (it.hasNext()) {
			mt = (MatchedTile)it.next();
			if (mt.getTileA().getStartToken().getStartLine() == lineCount) {
				// And the next match starts on this same line, too...
			    withinMatch = true;
			    matchCount++;
			    fa.print("<A NAME=\"match"+mt.getId()+"\"></A>");
			    fa.print(",");
			    fa.print("<A HREF=\"fileb"+counter+".html#match"+mt.getId()+"\" TARGET=\"right\">");
			    fa.print(mt.getId());
			    fa.print("</A>");
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
			if(mt != null)
			{
				fa.println("<b><font color='" + colorMap.get(mt.getId()) + "'>" + ":" + txt2html(line) + "</font></b>");
			}
			else
			{
				fa.println(":" + txt2html(line));
			}
	    }
	    else if ((withinMatch) && 
		     (mt.getTileA().getEndToken().getEndLine() == lineCount)) {
		// We are on the last line of a match
		withinMatch = false;
		//fa.print(mt.getId());
		int tmpId = mt.getId();
		if (it.hasNext()) {
		    mt = (MatchedTile)it.next();
		    if (mt.getTileA().getStartToken().getStartLine() == lineCount) {
			withinMatch = true;
			matchCount++;
			fa.print("<A NAME=\"match"+mt.getId()+"\"></A>");
			fa.print(",");
			fa.print("<A HREF=\"fileb"+counter+".html#match"+mt.getId()+"\" TARGET=\"right\">");
			fa.print(mt.getId());
			fa.print("</A>");
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
			    fa.print("<A NAME=\"match"+mt.getId()+"\"></A>");
			    fa.print(",");
			    fa.print("<A HREF=\"fileb"+counter+".html#match"+mt.getId()+"\" TARGET=\"right\">");
			    fa.print(mt.getId());
			    fa.print("</A>");
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		//fa.println(":" + txt2html(line));
		if(mt!= null)
		{
			fa.println("<b><font color='" + colorMap.get(tmpId) + "'>" + tmpId + ":" + txt2html(line) + "</font></b>");
		}
		else
		{
			fa.println("<b><font color='" + colorMap.get(tmpId) + "'>"  + tmpId + ":" + txt2html(line) + "</font></b>");
		}
	    }
	    else if (withinMatch) {
	    	// We are in the middle of a match
			//fa.println( mt.getId() + ":" + txt2html(line));
			fa.println("<b><font color='" + colorMap.get(mt.getId()) + "'>" + mt.getId() + ":" + txt2html(line) + "</font></b>");
	    }
	    else {
		// We are outside a match
		fa.println(txt2html(line));
	    }
	}
	
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
		fb.print("<A NAME=\"match"+mt.getId()+"\"></A>");
		fb.print("<A HREF=\"filea"+counter+".html#match"+mt.getId()+"\" TARGET=\"left\">");
		fb.print(mt.getId());
		fb.print("</A>");
		// Scan through all the matches ending on this same line
		while ( (mt != null) && (mt.getTileB().getEndToken().getEndLine() == lineCount)) {
		    withinMatch = false;
		    if (it.hasNext()) {
			mt = (MatchedTile)it.next();
			if (mt.getTileB().getStartToken().getStartLine() == lineCount) {
				// And the next match starts on this same line, too...
			    withinMatch = true;
			    matchCount++;
 			    fb.print("<A NAME=\"match"+mt.getId()+"\"></A>");
			    fb.print(",");
			    fb.print("<A HREF=\"filea"+counter+".html#match"+mt.getId()+"\" TARGET=\"left\">");
			    fb.print(mt.getId());
			    fb.print("</A>");
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		if(mt!= null)
		{
			fb.println("<b><font color='" + colorMap.get(mt.getId()) + "'>" + ":" + txt2html(line) + "</font></b>");
		}
		else
		{
			fb.println(":" + txt2html(line));
		}
	    }
	    else if ((withinMatch) && 
		     (mt.getTileB().getEndToken().getEndLine() == lineCount)) {
		// We are on the last line of a match
		withinMatch = false;
		//fb.print(mt.getId());
		int tmpId = mt.getId();
		if (it.hasNext()) {
		    mt = (MatchedTile)it.next();
		    if (mt.getTileB().getStartToken().getStartLine() == lineCount) {
			withinMatch = true;
			matchCount++;
			fb.print("<A NAME=\"match"+mt.getId()+"\"></A>");
			fb.print(",");
			fb.print("<A HREF=\"filea"+counter+".html#match"+mt.getId()+"\" TARGET=\"left\">");
			fb.print(mt.getId());
			fb.print("</A>");
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
			    fb.print("<A NAME=\"match"+mt.getId()+"\"></A>");
			    fb.print(",");
			    fb.print("<A HREF=\"filea"+counter+".html#match"+mt.getId()+"\" TARGET=\"left\">");
			    fb.print(mt.getId());
			    fb.print("</A>");
			}
		    }
		    else {
			withinMatch = false;
			mt = null;
		    }
		}
		//fb.println(":" + txt2html(line));
		if(mt!= null)
		{
			fb.println("<b><font color='" + colorMap.get(tmpId) + "'>" + tmpId + ":" + txt2html(line) + "</font></b>");
		}
		else
		{
			fb.println("<b><font color='" + colorMap.get(tmpId) + "'>" + tmpId + ":" + txt2html(line) + "</font></b>");
		}
	    }
	    else if (withinMatch) {
	    	// We are in the middle of a match
			String hex = colorMap.get(mt.getId());
			//fb.println(mt.getId() + ":" + txt2html(line));
			fb.println("<b><font color='" + hex + "'>" + mt.getId() + ":" + txt2html(line) + "</font><b>");
	    }
	    else {
		// We are outside a match
		fb.println(txt2html(line));
	    }
	}

  	fa.println("</PRE></CODE>");
  	fa.println("</BODY>");
  	fa.println("</HTML>");
  	fa.close();

  	fb.println("</PRE></CODE>");
  	fb.println("</BODY>");
  	fb.println("</HTML>");
	fb.close();
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
     * Prints the tokens and the associated code specified in the
     * given token list using the file read from the given reader.
     */
    private void printTokens(TokenList tokens,
			     BufferedReader br, 
			     PrintStream out,
			     boolean printLineNumbers,
			     int multiLineThreshold) 
	throws IOException 
    {

	out.println("<HTML>");
	out.println("<HEAD></HEAD>");
	out.println("<BODY>");

	out.println("<CODE><PRE>");

	StringBuffer fileContents = getChars(br);
	Iterator iter = tokens.iterator();
	
	int count = 0;

	while (iter.hasNext()) {
	    Token t = (Token)iter.next();
	    
	    String ps = "<A NAME=\"token"+count+"\">";
	    ps += count+":";
	    ps += "</A>";

	    int lineLeft = t.getStartLine();
	    int lineRight = t.getEndLine();
	    int leftChar = t.getStartChar();
	    int rightChar = t.getEndChar();
	    int value = t.getValue();

	    //	    if (printLineNumbers) {
	    ps += lineLeft+"("+leftChar+")-"+
		lineRight+"("+rightChar+"):";
	    //	    }
	    ps += codeTokenizer.getValueString(value);
	    //	    if (printLineNumbers) {
	    ps += ":";
	    //	    }
	    if (multiLineThreshold > 0) {
		/* Print multiLineThreshold number of lines */
		if (lineLeft == lineRight) {
		    ps += createEmptyString(COL_LENGTH - ps.length());
		    ps += txt2html(getChars(leftChar, rightChar, fileContents))+separator;
		    out.print(ps);
		}
		else {
		    String s = getChars(leftChar, rightChar, fileContents);
		    int curCount = 0;
		    int start = 0;
		    int end = s.indexOf("\n");
		    // First line:
		    ps += createEmptyString(COL_LENGTH - ps.length());
		    ps += txt2html(s.substring(start, end))+separator;
		    out.print(ps);
		    
		    start = end+1;
		    curCount++;
		    while ((curCount < multiLineThreshold) && 
			   ( (end = s.indexOf("\n",start)) != -1)) {
			out.print(createEmptyString(COL_LENGTH)+PRE_MULTILINE+txt2html(s.substring(start, end))+separator);
			start = end+1;
			curCount++;
		    }
		    // Print the remaining characters
		    if (curCount < multiLineThreshold) {
			out.print(createEmptyString(COL_LENGTH)+PRE_MULTILINE+txt2html(s.substring(start))+separator);
		    }
		}
	    }
	    else {
		out.print(separator);
	    }
	    count++;
	}
	
	out.println("</PRE></CODE>");
	out.println("</BODY>");
	out.println("</HTML>");
	

    }

    /**
     * Generates the files containing a list of tokens, that have
     * appeared in the files stored in the given detection result. The
     * generated files are called tokensa[conter].html and
     * tokensb[counter].html.
     */
    private void generateTokenLists(File dir, DetectionResult dR, int counter) 
	throws Exception
    {
	BufferedReader brA = dR.getFileReaderA();
	BufferedReader brB = dR.getFileReaderB();
	TokenList tokensA = dR.getTokensA();
	TokenList tokensB = dR.getTokensB();

	PrintStream prA = new PrintStream(new FileOutputStream(dir.getPath()+File.separator+"tokensa"+counter+".html"));
	PrintStream prB = new PrintStream(new FileOutputStream(dir.getPath()+File.separator+"tokensb"+counter+".html"));

	this.printTokens(tokensA,
			 brA,
			 prA,
			 true,
			 1);

	this.printTokens(tokensB,
			 brB,
			 prB,
			 true,
			 1);

	brA.close();
	brB.close();
	prA.close();
	prB.close();
    }

    /**
     * Generates a list of matched tiles, that have appeared in the
     * two files. The generated file is matchedtiles[counter].html.
     */
    private void generateMatchedTiles(File dir, DetectionResult dR, int counter) 
	throws Exception 
    {
	PrintStream pr = new PrintStream(new FileOutputStream(dir.getPath()+File.separator+"matchedtiles"+counter+".html"));
	pr.println("<HTML>");
	pr.println("<HEAD>");
	pr.println("</HEAD>");
	pr.println("<BODY>");
	pr.println("<CODE><PRE>");

	MatchedTileSet matches = dR.getMatches();

	Iterator i = matches.getMatchedTiles();

	int c = 0;

	while (i.hasNext()) {
	    MatchedTile mt = (MatchedTile)i.next();

	    pr.print(mt.getId()+":"+mt.toString()+":");
	    pr.print("<A HREF=\"filea"+counter+".html#match"+mt.getId()+"\" TARGET=\"left\">");
	    pr.print("left");
	    pr.print("</A>");
	    
	    pr.print(":");

	    pr.print("<A HREF=\"fileb"+counter+".html#match"+mt.getId()+"\" TARGET=\"right\">");
	    pr.print("right");
	    pr.print("</A>");

	    pr.print(":");

	    pr.print("<A HREF=\"tokensa"+counter+".html#token"+mt.getTileA().getStartTokenIndex()+"\" TARGET=\"left\">");
	    pr.print("token left");
	    pr.print("</A>");

	    pr.print(":");

	    pr.print("<A HREF=\"tokensb"+counter+".html#token"+mt.getTileB().getStartTokenIndex()+"\" TARGET=\"right\">");
	    pr.print("token right");
	    pr.print("</A>");

	    pr.println();

	    c++;
	}

	pr.println("</PRE></CODE>");
	pr.println("</BODY>");
	pr.println("</HTML>");


	pr.close();
    }

    /**
     * Generates a report of the given single submission detection
     * result.
     */
    public void generateReport(SubmissionDetectionResult detResult)
	throws Exception
    {
	File dir = new File(baseDir.getPath()+File.separator+"detection"+subNumber);
	
	if (!dir.mkdir()) {
	    throw new IOException("Unable to create directory: "+dir.getPath());
	}

	// Generate dir/index.html with frameset info
	this.generateFramesetIndex(new File(dir.getPath()+File.separator+"index.html"),
				   "top.html",
				   "filea1.html",
				   "fileb1.html",
				   detResult);

	// Generate the index frame with list of all matched tiles in all the file detection results
	this.generateTopFile(new File(dir.getPath()+File.separator+"top.html"),
			     detResult);


	
	// Generate the contents of file frames
	Iterator iter = detResult.getFileDetectionResults().iterator();

	int fileCounter = 1;
	while (iter.hasNext()) {
	    DetectionResult fileDetRes = (DetectionResult)iter.next();
	    this.generateFiles(dir, fileDetRes, fileCounter);
	    this.generateTokenLists(dir, fileDetRes, fileCounter);
	    this.generateMatchedTiles(dir, fileDetRes, fileCounter);
	    fileCounter++;
	}

    }

    /**
     * Generates the index page of the report, when there are several
     * detection results to report. This is used to create different
     * index pages for different sort orders (sort order of detResults
     * changes, as doues the filename).
     */
    public void generateIndexPage(SortedSet detResults, HtmlPrintable configuration,
				  HtmlPrintable statistics, String filename, HashMap detNumbers) 
	throws Exception
    {
	PrintStream f = new PrintStream(new FileOutputStream(new File(this.baseDir+File.separator+filename)));
	
	f.println("<HTML>");
	f.println("<HEAD>");
	f.println("<TITLE>Plaggie - Plagiarism Detection Report</TITLE>");
	f.println("</HEAD>");
	f.println("<BODY>");
	f.println("<H1>Plaggie - Plagiarism Detection Report</H1>");
	
	f.println(""+
		  "[<A HREF=\"#configuration\">Configuration</A>] "+
		  "[<A HREF=\"#statistics\">Statistics</A>] "+
		  "[<A HREF=\"#results\">Results</A>]");

	f.println("<A NAME=\"configuration\"><H2>Configuration</H2></A>");
	configuration.printHtmlReport(f);

		f.println("<A NAME=\"statistics\"><H2>Statistics</H2></A>");
		statistics.printHtmlReport(f);


		f.println("<H2>Sorted Results</H2>");
		f.println("The results are sorted such that it shows all students copying from a single student");
		f.println("<TABLE BORDER=\"1\">");	
		UnionFind uf = new UnionFind();	
		// -- Go through the detection results and print a line about
		// them in the table
		Iterator iter = detResults.iterator();

		// Here we loop through all the detection results and create a hashmap of similar submissions
		HashMap<String, ArrayList<SortedResults>> map = new HashMap<String, ArrayList<SortedResults>>();
		while(iter.hasNext())
		{
			SubmissionDetectionResult detResult = (SubmissionDetectionResult) iter.next();
			
			String Student1 = detResult.getSubmissionA().getName();
			String Student2 = detResult.getSubmissionB().getName();
			uf.union(Student1, Student2);
			int number = ((Integer) detNumbers.get(detResult)).intValue();
			
			if(map.containsKey(Student1))
			{
				ArrayList<SortedResults> ar = map.get(Student1);
				Submission sb = detResult.getSubmissionB();
				SortedResults te = new SortedResults(sb, 
						Stats.getPercentage(detResult.getSimilarityA()), 
						Stats.getPercentage(detResult.getSimilarityB()), 
						Stats.getPercentage(detResult.getSimilarityA() * detResult.getSimilarityB()), number);
				ar.add(te);
			}
			else
			{
				Submission sb = detResult.getSubmissionB();
				SortedResults te = new SortedResults(sb, 
						Stats.getPercentage(detResult.getSimilarityA()), 
						Stats.getPercentage(detResult.getSimilarityB()), 
						Stats.getPercentage(detResult.getSimilarityA() * detResult.getSimilarityB()), number);
				ArrayList<SortedResults> ar = new ArrayList<SortedResults>();
				ar.add(te);
				map.put(Student1, ar);
			}
			
			if(map.containsKey(Student2))
			{
				ArrayList<SortedResults> ar = map.get(Student2);
				Submission sb = detResult.getSubmissionA();
				SortedResults te = new SortedResults(sb, 
						Stats.getPercentage(detResult.getSimilarityA()), 
						Stats.getPercentage(detResult.getSimilarityB()), 
						Stats.getPercentage(detResult.getSimilarityA() * detResult.getSimilarityB()), number);
				ar.add(te);
			}
			else
			{
				Submission sb = detResult.getSubmissionA();
				SortedResults te = new SortedResults(sb, 
						Stats.getPercentage(detResult.getSimilarityA()), 
						Stats.getPercentage(detResult.getSimilarityB()), 
						Stats.getPercentage(detResult.getSimilarityA() * detResult.getSimilarityB()), number);
				ArrayList<SortedResults> ar = new ArrayList<SortedResults>();
				ar.add(te);
				map.put(Student2, ar);
			}
		}
		
		if(map.size() > 0) {f.println("<TABLE BORDER=\"1\"> <tbody align='center'>");}	
		Set<String> key = map.keySet();
		for(String s : key)
		{
			f.println("<TR>");
			f.println("<TD>");
			f.println(s);
			//System.out.println();
			f.println("</TD>");
			ArrayList<SortedResults> ar = map.get(s);
			for(SortedResults t : ar)
			{
				
				//int number = ((Integer) detNumbers.get(t.detNumber)).intValue();
				
				//System.out.println(t);
				f.println("<TD bgcolor='#B3CAFE'>");
				f.println("<A HREF=\"detection" + t.detNumber + "/index.html\">");
				f.println(t);
				f.println("<BR/> <font color='#be0000'>(" + t.similarity + ")</font>");
				f.println("</A>");
				f.println("</TD>");
				
			}
			
			//System.out.println("\n");
			f.println("</TR>");
		}
		f.println("</tbody></TABLE><BR/><BR/>");
		
		f.println("<H2>Groups</H2>");
		f.println("The results shows groups of students who have been indentified as copying together.");
		
		// Return all the groups that share the similar submissions
		TreeMap<String, ArrayList<String>> groups = uf.GetGroups();
		Iterator<String> it = groups.keySet().iterator();
		if(groups.size() > 0)
		{
		
			f.println("<TABLE BORDER=\"1\"> <tbody align='center'>");
		}
		
		int i = 1;
		while (it.hasNext()) 
		{
			f.println("<TR>");
			f.println("<TD>");
			//System.out.println("Group: " + (i++));
			f.println("Group: " + (i++));
			f.println("</TD>");
			for (String x : groups.get(it.next())) 
			{
				f.println("<TD bgcolor='#B3CAFE'>");
				f.println(x);
				f.println("</TD>");
			}
			//System.out.println("\n");
			f.println("<TR>");
		}
		f.println("</tbody></TABLE><BR/><BR/>");
		
		f.println("<A NAME=\"results\"><H2>Results</H2></A>");

	f.println("<P>Change sorting by clicking the links on the header row.<BR>");
	f.println("Blacklisted student id's are highlighted.");

	f.println("<TABLE BORDER=\"1\">");

	f.println("<TR>"+
		  "<TD>Student A</TD><TD>Student B</TD>"+
		  "<TD><A HREF=\"index.html#results\">Similarity</A></TD>"+
		  "<TD><A HREF=\"index_sima.html#results\">Similarity A</A></TD>"+
		  "<TD><A HREF=\"index_simb.html#results\">Similarity B</A></TD>"+
		  "<TD><A HREF=\"index_maxfile.html#results\">Maximum file similarity</A></TD>"+
		  "<TD>Student A similarity distribution average</TD>"+
		  "<TD>Student B similarity distribution average</TD>"+
		  "</TR>");

	// -- Go through the detection results and print a line about
	// them in the table
	iter = detResults.iterator();

	while (iter.hasNext()) {
	    SubmissionDetectionResult detResult = (SubmissionDetectionResult)iter.next();

	    int number = ((Integer)detNumbers.get(detResult)).intValue();

	    f.println("<TR>");

	    f.println("<TD>");
	    f.println("<A HREF=\"detection"+number+"/index.html\">");
	    if (detResult.isBlacklistedA()) {
		f.print("<B>");
	    }
	    f.println(detResult.getSubmissionA().getName());
	    if (detResult.isBlacklistedA()) {
		f.print("</B>");
	    }
	    f.println("</A>");

	    f.println("</TD><TD>");

	    f.println("<A HREF=\"detection"+number+"/index.html\">");
	    if (detResult.isBlacklistedB()) {
		f.print("<B>");
	    }
	    f.println(detResult.getSubmissionB().getName());
	    if (detResult.isBlacklistedB()) {
		f.print("</B>");
	    }
	    f.println("</A>");

	    f.println("</TD><TD>");

	    f.print(Stats.getPercentage(detResult.getSimilarityA()*detResult.getSimilarityB()));

	    f.println("</TD><TD>");

	    f.print(Stats.getPercentage(detResult.getSimilarityA()));


	    f.println("</TD><TD>");

	    f.print(Stats.getPercentage(detResult.getSimilarityB()));

	    f.println("</TD><TD>");

	    f.print(Stats.getPercentage(detResult.getMaxFileSimilarityProduct()));

	    f.println("</TD><TD>");

	    f.print("<A HREF=\"sub"+subNumbers.get(detResult.getSubmissionA())+"/simdistr.html\">"+
		    Stats.getPercentage(Stats.getDistributionAverage(detResult.getSubmissionA().getName()))+
		    "</A>");

	    f.println("</TD><TD>");
	    
	    f.print("<A HREF=\"sub"+subNumbers.get(detResult.getSubmissionB())+"/simdistr.html\">"+
		    Stats.getPercentage(Stats.getDistributionAverage(detResult.getSubmissionB().getName()))+
		    "</A>");

	    f.println("</TD>");
	    

	    f.println("</TR>");
	}
	f.println("</TABLE>");

	f.println("</BODY>");
	f.println("</HTML>");
	f.close();
    }

    /**
     * Orders submission detection results according to their
     * similarity value A.
     */
    private static class SimilarityAComparator
	implements Comparator, Serializable
    {
	public int compare(Object o1, Object o2) {
	    SubmissionDetectionResult r1 = (SubmissionDetectionResult)o1;
	    SubmissionDetectionResult r2 = (SubmissionDetectionResult)o2;

	    try {
		if (r1.getSimilarityA() > r2.getSimilarityA()) {
		    return -1;
		}
		else {
		    return 1;
		}
	    }
	    catch (Exception e) {
		Debug.println(this, "Exception during compare: "+e.getMessage());
		e.printStackTrace();
		return -1;
	    }
	}
    }

    /**
     * Orders submission detection results according to their
     * similarity value B.
     */
    private static class SimilarityBComparator
	implements Comparator, Serializable
    {
	public int compare(Object o1, Object o2) {
	    SubmissionDetectionResult r1 = (SubmissionDetectionResult)o1;
	    SubmissionDetectionResult r2 = (SubmissionDetectionResult)o2;

	    try {
		if (r1.getSimilarityB() > r2.getSimilarityB()) {
		    return -1;
		}
		else {
		    return 1;
		}
	    }
	    catch (Exception e) {
		Debug.println(this, "Exception during compare: "+e.getMessage());
		e.printStackTrace();
		return -1;
	    }
	}
    }

    /**
     * Compares submission detection results using product of the two similarity
     * values.
     */
    private static class SimilarityProductComparator
	implements Comparator, Serializable
    {
	public int compare(Object o1, Object o2) {
	    SubmissionDetectionResult r1 = (SubmissionDetectionResult)o1;
	    SubmissionDetectionResult r2 = (SubmissionDetectionResult)o2;

	    try {
		if ((r1.getSimilarityA()*r1.getSimilarityB()) > (r2.getSimilarityA()*r2.getSimilarityB())) {
		    return -1;
		}
		else {
		    return 1;
		}
	    }
	    catch (Exception e) {
		Debug.println(this, "Exception during compare: "+e.getMessage());
		e.printStackTrace();
		return -1;
	    }
	}
    }


    /**
     * Compares submisison detection results using the maximum file comparison result
     * within them.
     */
    private static class MaximumFileSimilarityComparator
	implements Comparator, Serializable
    {
	public int compare(Object o1, Object o2) {
	    SubmissionDetectionResult r1 = (SubmissionDetectionResult)o1;
	    SubmissionDetectionResult r2 = (SubmissionDetectionResult)o2;

	    try {
		if (r1.getMaxFileSimilarityProduct() > r2.getMaxFileSimilarityProduct()) {
		    return -1;
		}
		else {
		    return 1;
		}
	    }
	    catch (Exception e) {
		Debug.println(this, "Exception during compare: "+e.getMessage());
		e.printStackTrace();
		return -1;
	    }
	}
    }


    /**
     * Generates a report of the given submission in sub directory
     * sub[n]. Currently the report only contains the similarity
     * distribution of the given submission against others.
     */
    private void generateSubmissionReport(Submission sub, int n) 
	throws Exception
    {
	// Create directory sub<n>
	File dir = new File(baseDir.getPath()+File.separator+"sub"+n);

	if (!dir.mkdir()) {
	    throw new IOException("Unable to create directory: "+dir.getPath());
	}

	// Create token lists of each file in submission
	// TODO

	// Create similarity distribution of submission
	
	File file = new File(dir.getPath()+File.separator+"simdistr.html");

	PrintStream f = new PrintStream(new FileOutputStream(file));

	f.println("<HTML>");
	f.println("<HEAD></HEAD>");
	f.println("<BODY>");
	f.println("<H3>Similarity distribution of submission "+sub.getName()+"</H3>");
	Stats.printHtmlDistribution(f, sub.getName(), 0.0, 1.0, 0.1, true, '#', 50);
	f.println("</BODY>");
	f.println("</HTML>");
	f.close();

    }

    /**
     * Generates a report of submission similarity detection. The
     * configuration and statistics are added to the report by using
     * the corresponding HtmlPrintable parameters.
     */
    public void generateReport(List detResults, List submissions, HtmlPrintable configuration,
			       HtmlPrintable statistics)
	throws Exception
    {
	// Limit the number of reported detection results, if they exceed the maximum

	if (detResults.size() > this.maxDetResultsToReport) {
	    TreeSet detSet = new TreeSet(new SimilarityProductComparator());
	    detSet.addAll(detResults);
	    detResults = new ArrayList();
	    Iterator iter = detSet.iterator();
	    int c = 0;
	    while ((iter.hasNext()) && (c < this.maxDetResultsToReport)) {
		SubmissionDetectionResult subDet =
		    (SubmissionDetectionResult)iter.next();
		detResults.add(subDet);
		c++;
	    }
	}


	// Generate the submission part of the report
	Iterator iter = submissions.iterator();

	int c = 1;

	this.subNumbers = new HashMap();

	System.out.print("Generating report of "+submissions.size()+" submissions:");

	while (iter.hasNext()) {
	    Submission sub = (Submission)iter.next();
	    this.generateSubmissionReport(sub, c);
	    subNumbers.put(sub, new Integer(c));
	    if ((c % 10) == 0) {
		System.out.print("."+c);
	    }
	    c++;
	}

	System.out.println("...DONE");

	// Generate the detection result part of the report
	iter = detResults.iterator();

	System.out.print("Generating report of "+detResults.size()+" detection results:");

	c = 1;

	HashMap detNumbers = new HashMap();

	while (iter.hasNext()) {
	    SubmissionDetectionResult detResult = (SubmissionDetectionResult)iter.next();
	    this.generateReport(detResult);
	    detNumbers.put(detResult, new Integer(subNumber));
	    
	    if ((c % 10) == 0) {
		System.out.print("."+c);
 	    }
	    c++;

	    subNumber++;

	}
	System.out.println("...DONE");

	System.out.print("Generating index pages...");

	TreeSet detSet = new TreeSet(new SimilarityProductComparator());
	detSet.addAll(detResults);
	this.generateIndexPage(detSet, configuration, statistics, "index.html", detNumbers);

	detSet = new TreeSet(new SimilarityAComparator());
	detSet.addAll(detResults);
	this.generateIndexPage(detSet, configuration, statistics, "index_sima.html", detNumbers);

	detSet = new TreeSet(new SimilarityBComparator());
	detSet.addAll(detResults);
	this.generateIndexPage(detSet, configuration, statistics, "index_simb.html", detNumbers);

	detSet = new TreeSet(new MaximumFileSimilarityComparator());
	detSet.addAll(detResults);
	this.generateIndexPage(detSet, configuration, statistics, "index_maxfile.html", detNumbers);
	

	System.out.println("DONE");

    }
}

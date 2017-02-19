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
package plag.parser.plaggie;

import java.io.*;
import java.util.*;
import plag.parser.report.HtmlPrintable;

/**
 * Configuration class for the plagiarism comparison tool.
 *
 */
public class Configuration
    implements HtmlPrintable
{
    
    public boolean printTokenLists;

    public int minimumMatchLength;
    
    public boolean useRecursive;
    
    public boolean fullFileDetectionReports;
    
    public boolean statFileDetectionReports;

    public boolean debugMessages;

    public boolean htmlReport;

    public String htmlDir;

    public double minimumSubmissionSimilarityValue;

    public boolean cacheTokenLists;
    
    public boolean createResultFile;

    public boolean readResultsFromFile;

    public String resultFile;

    public boolean severalSubmissionDirectories;

    public String submissionDirectory;

    public String excludeFiles;

    public double minimumFileSimilarityValueToReport;

    public String excludeSubdirectories;

    public String templates;

    public boolean excludeInterfaces;

    public String blacklistFile;

    public boolean showAllBlacklistedResults;

    public String codeTokenizer;

    public String filenameFilter;

    public int maximumDetectionResultsToReport;

    private String getProperty(Properties prop, String propName) 
	throws ConfigurationException 
    {
	String ret = null;
	ret = prop.getProperty(propName);
	if (ret == null) {
	    throw new ConfigurationException("Property "+propName+" not found in configuration file");
	}
	return ret;
    }

    /**
     * Reads the configuration from file specified by the parameter.
     */
    public Configuration(File file) 
	throws FileNotFoundException, IOException, ConfigurationException
    {
	Properties prop = new Properties();
	prop.load(new FileInputStream(file));
	
	minimumMatchLength = Integer.parseInt(this.getProperty(prop, "plag.parser.plaggie.minimumMatchLength"));
	
	maximumDetectionResultsToReport = Integer.parseInt(this.getProperty(prop, "plag.parser.plaggie.maximumDetectionResultsToReport"));

	debugMessages = (this.getProperty(prop, "plag.parser.plaggie.debugMessages").equals("true"));
	printTokenLists = (this.getProperty(prop, "plag.parser.plaggie.printTokenLists").equals("true"));
	
	useRecursive = (this.getProperty(prop, "plag.parser.plaggie.useRecursive").equals("true"));
	
	// Whether to print full tion reports between files.
	fullFileDetectionReports =
	    (this.getProperty(prop, "plag.parser.plaggie.fileDetectionReports").equals("full"));
	statFileDetectionReports = 
	    (this.getProperty(prop, "plag.parser.plaggie.fileDetectionReports").equals("stats"));

	htmlReport = 
	    (this.getProperty(prop, "plag.parser.plaggie.htmlReport").equals("true"));

	htmlDir = this.getProperty(prop, "plag.parser.plaggie.htmlDir");

	minimumSubmissionSimilarityValue = Double.parseDouble(this.getProperty(prop, "plag.parser.plaggie.minimumSubmissionSimilarityValue"));

	cacheTokenLists = 
	    (this.getProperty(prop, "plag.parser.plaggie.cacheTokenLists").equals("true"));

	createResultFile = 
	    (this.getProperty(prop, "plag.parser.plaggie.createResultFile").equals("true"));

	readResultsFromFile = 
	    (this.getProperty(prop, "plag.parser.plaggie.readResultsFromFile").equals("true"));

	resultFile = 
	    this.getProperty(prop, "plag.parser.plaggie.resultFile");

	severalSubmissionDirectories = 
	    (this.getProperty(prop, "plag.parser.plaggie.severalSubmissionDirectories").equals("true"));
	
	submissionDirectory = 
	    this.getProperty(prop, "plag.parser.plaggie.submissionDirectory");

	excludeFiles = 
	    this.getProperty(prop, "plag.parser.plaggie.excludeFiles");

	minimumFileSimilarityValueToReport = 
	    Double.parseDouble(this.getProperty(prop, "plag.parser.plaggie.minimumFileSimilarityValueToReport"));

	excludeSubdirectories = 
	    this.getProperty(prop, "plag.parser.plaggie.excludeSubdirectories");
	
	templates =
	    this.getProperty(prop, "plag.parser.plaggie.templates");
	
	excludeInterfaces = 
	    (this.getProperty(prop, "plag.parser.plaggie.excludeInterfaces").equals("true"));

	showAllBlacklistedResults = 
	    (this.getProperty(prop, "plag.parser.plaggie.showAllBlacklistedResults").equals("true"));
	

	blacklistFile = 
	    this.getProperty(prop, "plag.parser.plaggie.blacklistFile");

	codeTokenizer = 
	    this.getProperty(prop, "plag.parser.plaggie.codeTokenizer");

	filenameFilter = 
	    this.getProperty(prop, "plag.parser.plaggie.filenameFilter");
	
    }
    
    public Configuration() throws FileNotFoundException, IOException, ConfigurationException
    {
    		
    		minimumMatchLength = 5;
    		maximumDetectionResultsToReport = 100;
    		minimumSubmissionSimilarityValue = 0.5;
    		minimumFileSimilarityValueToReport = 0.5;
    		codeTokenizer = "plag.parser.java.JavaTokenizer";
    		
    		debugMessages = false;
    		printTokenLists = false;
    		useRecursive = true;
    		
    		// Whether to print full tion reports between files.
    		fullFileDetectionReports = false;
    		statFileDetectionReports = false;
    		htmlReport = true;

    		htmlDir = "html";
    		cacheTokenLists = true;
    		createResultFile = false;
    		readResultsFromFile = false;
    		resultFile = "results.data";
    		severalSubmissionDirectories = false;
    		submissionDirectory = "";
    		excludeFiles = "ExistingCode1.java,ExistingCode2.java";
    		excludeSubdirectories = "webstore,webstore/server,webstore/remote,webstore/servlets";
    		templates = "";
    		excludeInterfaces = true;
    		showAllBlacklistedResults = false;
    		blacklistFile = "blacklist.text";
    		filenameFilter = "plag.parser.java.JavaFilenameFilter";
    	    }
    
    private void printHtmlBooleanValue(PrintStream out, String title, boolean value)
    {
	out.println("<TR><TD>"+title+"</TD><TD>"+value+"</TD></TR>");
    }

    private void printHtmlStringValue(PrintStream out, String title, String value)
    {
	out.println("<TR><TD>"+title+"</TD><TD>"+value+"</TD></TR>");
    }

    private void printHtmlDoubleValue(PrintStream out, String title, double value)
    {
	out.println("<TR><TD>"+title+"</TD><TD>"+value+"</TD></TR>");
    }

    private void printHtmlIntegerValue(PrintStream out, String title, int value)
    {
	out.println("<TR><TD>"+title+"</TD><TD>"+value+"</TD></TR>");
    }


    /**
     * Writes the configuration data as an HTML table in the given print stream.
     */
    public void printHtmlReport(PrintStream out) {
	out.println("<TABLE BORDER=\"1\">");
	printHtmlIntegerValue(out,"Minimum match length used",minimumMatchLength);
	printHtmlBooleanValue(out,"Recurse subdirectories", useRecursive);
	printHtmlStringValue(out,"Html report directory", htmlDir);
	printHtmlDoubleValue(out,"Minimum submission similarity to report", minimumSubmissionSimilarityValue);
	printHtmlIntegerValue(out,"Maximum number of detection results to report", maximumDetectionResultsToReport);
	printHtmlDoubleValue(out,"Minimum file similarity to report",minimumFileSimilarityValueToReport);
	printHtmlStringValue(out,"Code tokenizer used",codeTokenizer);
	printHtmlStringValue(out,"Filename filter used", filenameFilter);
	if (createResultFile) {
	    printHtmlStringValue(out,"Detection results stored in file", resultFile);
	}
	if (readResultsFromFile) {
	    printHtmlStringValue(out,"Detection results read from file", resultFile);
	}
	if (severalSubmissionDirectories) {
	    printHtmlStringValue(out,"Checked submissions in subdirectory",submissionDirectory);
	}
	printHtmlStringValue(out, "Excluded files",excludeFiles);
	printHtmlStringValue(out, "Excluded subdirectories", excludeSubdirectories);
	printHtmlStringValue(out, "Excluded code in template files", templates);
	printHtmlBooleanValue(out, "Interface code excluded", excludeInterfaces);
	out.println("</TABLE>");
    }
}

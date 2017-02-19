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

import plag.parser.*;
import plag.parser.java.*;
import plag.parser.report.*;

import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.*;

/**
 * A compare tool for comparing two assignment submssions and
 * generating a report of them. Use plaggie.properties file to set
 * some propertieYs of the tool. Prints (and prompts) information about
 * the status to the standard output. For command line usage, see
 * README_PLAGGIE file. For configuration, see the configuration file
 * (plaggie.properties).
 *
 */
public class Plaggie
{

    private static final String CONF_FILE = "plaggie.properties";

    private static Configuration config = null;

    private static Runtime runtime;

    private static CodeTokenizer codeTokenizer = null;

    /**
     * Reads the configuration file and initializes the config object.
     */
    private static void configure(String confFile) 
	throws Exception 
    {
    	config = new Configuration(new File(confFile));
    }
    
    private static void configure() 
	throws Exception 
    {
    	config = new Configuration();
    }

    /**
     * Generates a FilenameFilter according to the configuration.
     */
    private static FilenameFilter generateFilenameFilter() 
	throws Exception
    {
	
	// Generate the filename filter
	ArrayList filters = new ArrayList();

	filters.add((FilenameFilter)Class.forName(config.filenameFilter).newInstance());

	filters.add(new ExcludeFilenameFilter(config.excludeFiles));
	filters.add(new SubdirectoryFilter(config.excludeSubdirectories));
	
	FilenameFilter filter = new MultipleFilenameFilter(filters);
	return filter;
    }

    /**
     * Returns a list of submissions, that can be found in the directory
     * given as a parameter. The exact format of the directory
     * hierarchy depends on the configuration, especially parameter
     * severalSubmissionDirectories.
     */
    private static ArrayList getDirectorySubmissions(File directory) 
	throws Exception 
    {
	File[] files = directory.listFiles();
	ArrayList submissions = new ArrayList();

	
	FilenameFilter filter = generateFilenameFilter();
	
	int c = 1;

	System.out.print("Generating submissions:");

	for (int i=0; i < files.length; i++) {
	    if ((c % 10) == 0) {
		System.out.print("."+c);
	    }
	    c++;
	    if (files[i].isDirectory()) {
		if (!config.severalSubmissionDirectories) {
		    Debug.println("Adding directory submission: "+files[i].getPath());
		    DirectorySubmission dirS = new DirectorySubmission(files[i],
								       filter,
								       config.useRecursive);
		    submissions.add(dirS);
		    Stats.incCounter("submissions");
		}
		else {
		    File subDir = new File(files[i].getPath()+File.separator+
					   config.submissionDirectory);
		    if (subDir.isDirectory()) {
			Debug.println("Adding directory submission: "+subDir.getPath());
			DirectorySubmission dirS = new DirectorySubmission(subDir,
									   filter,
									   config.useRecursive);
			submissions.add(dirS);
			Stats.incCounter("submissions");
		    }
		}
	    }
	    else {
		Debug.println("Adding single file submission: "+files[i].getPath());
		SingleFileSubmission sub = new SingleFileSubmission(files[i]);
		submissions.add(sub);
		Stats.addToDistribution("files_in_submission",1.0);
		Stats.incCounter("submissions");
		
	    }
	}
	System.out.println("...DONE");

	return submissions;

    }

    /**
     * Generates a list of detection results using the given list of
     * submissions. Not all detectoin results are stored in the
     * returned list, they are filtered out according to some
     * configuration parameters.
     */
    private static ArrayList generateDetectionResults(ArrayList submissions) 
	throws Exception 
    {

	ArrayList detResults = null;

	// Generate the black list
	HashMap blacklist = new HashMap();
	if (!(config.blacklistFile == null || config.blacklistFile.equals(""))) {
	    try {
		BufferedReader bin = new BufferedReader(new FileReader(config.blacklistFile));
		String s;
		Integer dummy = new Integer(0);
		while ( (s = bin.readLine()) != null) {
		    blacklist.put(s.toUpperCase(),dummy);
		}
	    }
	    catch (FileNotFoundException e) {
		System.out.println("Blacklist file "+config.blacklistFile+" not found, no blacklist used.");
	    }

	}
	
	// Create the code excluder
	ArrayList codeExcluders = new ArrayList();
	
	if (config.excludeInterfaces) {
	    codeExcluders.add(new InterfaceCodeExcluder());
	}
	
	StringTokenizer tokenizer = new StringTokenizer(config.templates, ",");
	
	while (tokenizer.hasMoreTokens()) 
	{
	    String token = tokenizer.nextToken().trim();
	    CodeExcluder cE = 
		new ExistingCodeExcluder(createTokenList(token, codeTokenizer),
					 config.minimumMatchLength);
	    codeExcluders.add(cE);
	}
	
	CodeExcluder codeExcluder = new MultipleCodeExcluder(codeExcluders);
	
	TokenSimilarityChecker tokenChecker = 
	    new SimpleTokenSimilarityChecker(config.minimumMatchLength,
					     codeExcluder);

	
	// No file excluders currently used, therefore null's
	SubmissionSimilarityChecker checker;
	if (config.cacheTokenLists) {
	    checker = 
		new CachingSimpleSubmissionSimilarityChecker(tokenChecker,
							     codeTokenizer,
							     new HashMap());
	}
	else {
	    checker =
		new SimpleSubmissionSimilarityChecker(tokenChecker,
						      codeTokenizer);
	}
	
	
	// Generate all the submission detection results
	
	detResults = new ArrayList();
	
	int totalCalculations = (submissions.size()*(submissions.size()-1))/2;
	Stats.setCounterLimit("similarity_comparisons", totalCalculations);
	Stats.setCounterLimit("submissions", Stats.getCounter("submissions"));
	Stats.setCounterLimit("parsed_files", Stats.getCounter("files_to_parse"));
	
	final Date startTime = new Date();

	System.out.println("Starting time: "+startTime);
	System.out.println("Similarity value report threshold: "+config.minimumSubmissionSimilarityValue);
	System.out.println("Memory used/free: "+runtime.totalMemory()+" / "+runtime.freeMemory());
	System.out.print("Running "+totalCalculations+" submission comparisons: ");
	
	/*	
	TimerTask printStatsTask = 
	    new TimerTask() {
		    public void run() {
			System.out.println();
			Stats.print(System.out);
			System.out.println("Memory used/free: "+runtime.totalMemory()+" / "+runtime.freeMemory());
			Date curTime = new Date();
			long timeElapsed = curTime.getTime() - startTime.getTime();

			int cDone, cTotal;

			try {
			    cDone = Stats.getCounter("similarity_comparisons");
			    if (cDone == 0) {
				cDone = 1;
			    }
			    cTotal = Stats.getCounterLimit("similarity_comparisons");
			}
			catch (StatsException e) {
			    System.out.println("Estimated end time: Not available");
			    return;
			}
			
			long timePerComparison = timeElapsed / cDone;
			
			long timeLeft = (cTotal-cDone) * timePerComparison;
			
			long endTime = curTime.getTime() + timeLeft;
			
			Date endDate = new Date(endTime);
			System.out.println("Estimated end time: "+endDate);
		    }
		};
	
	Timer timer = new Timer();
	timer.schedule(printStatsTask, 0, 5000);
	*/
	for (int i=0; i < submissions.size(); i++) {
	    for (int j=0; j < i; j++) {
		Submission subA = (Submission)submissions.get(i);
		Submission subB = (Submission)submissions.get(j);
		SubmissionDetectionResult detResult = 
		    new SubmissionDetectionResult(subA,
						  subB,
						  checker,
						  config.minimumFileSimilarityValueToReport);
	      
		Stats.addToDistribution("submission_similarities_a",detResult.getSimilarityA());
		Stats.addToDistribution("submission_similarities_b",detResult.getSimilarityB());
		Stats.addToDistribution("submission_similarities",detResult.getSimilarityA()*detResult.getSimilarityB());
		Stats.addToDistribution("maximum_file_similarities",detResult.getMaxFileSimilarityProduct());
		Stats.addToDistribution(subA.getName(), detResult.getSimilarityA());
		Stats.addToDistribution(subB.getName(), detResult.getSimilarityB());
		boolean onBlacklist = false;
		if (blacklist.get(detResult.getSubmissionA().getName().toUpperCase()) != null) {
		    detResult.setBlacklistedA(true);
		    onBlacklist = true;
		}
		if (blacklist.get(detResult.getSubmissionB().getName().toUpperCase()) != null) {
		    detResult.setBlacklistedB(true);
		    onBlacklist = true;
		}

		
		boolean alreadyAdded = false;
		if (onBlacklist) {
		    Stats.incCounter("blacklisted_detection_results");
		    if (config.showAllBlacklistedResults) {
			detResults.add(detResult);
			alreadyAdded = true;
		    }
		}

		if ((detResult.getSimilarityA() >= config.minimumSubmissionSimilarityValue) ||
		    (detResult.getSimilarityB() >= config.minimumSubmissionSimilarityValue)) {
		    if (!alreadyAdded) {
			detResults.add(detResult);
		    }
		    Stats.incCounter("similarity_over_threshold");
		}
		Stats.incCounter("similarity_comparisons");
		
	    }
	}
	//timer.cancel();
	System.out.println();
	System.out.println("Ending time: "+(new Date()));
	System.out.println();
	Stats.print(System.out);

	return detResults;

    }

    /**
     * Reads the detection results from a file (name read from
     * configuration file), if they have been stored in it earlier.
     */
    private static ArrayList readDetectionResultsFromFile() 
	throws Exception 
    {
	ArrayList detResults = null;
	// Read the submission detection result set from the resultFile
	System.out.println("Reading results from file "+config.resultFile);
	
	PlagSym.init();
	
	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(config.resultFile)));
	
	detResults = (ArrayList)ois.readObject();
	
	Stats.setInstance((Stats)ois.readObject());
	
	ois.close();	
	System.out.println("Done.");

	return detResults;
    }
    
    /**
     * Stores the given detection results in a file.
     */
    private static void storeDetectionResultsInFile(ArrayList detResults) 
	throws Exception
    {
	System.out.println("Generating result file "+config.resultFile);
	ObjectOutputStream oos = 
	    new ObjectOutputStream(new FileOutputStream(new File(config.resultFile)));
	oos.writeObject(detResults);
	oos.writeObject(Stats.getInstance());
	oos.close();
	System.out.println("Done.");
    }

    /**
     * Generates a report of the given detection results and
     * submissions. Exact format of the report depends on the
     * configuration parameters.
     */
    private static void generateReport(ArrayList detResults, ArrayList submissions) 
	throws Exception 
    {
		ReportGenerator fileRepGen;
		
		if (config.fullFileDetectionReports) {
		    fileRepGen =  
			new SimpleTextReportGenerator(System.out,
						      config.printTokenLists,
						      codeTokenizer);
		}
		else if (config.statFileDetectionReports) {
		    fileRepGen = 
			new SimpleTextSubmissionReportGenerator
			    .OnlyStatsReportGenerator(System.out);
		}
		else {
		    fileRepGen = null;
		}
		
		boolean generateFileDetectionReports = true;
		if (fileRepGen == null) {
		    generateFileDetectionReports = false;
		}
		
		
		SubmissionReportGenerator repGen = null;
		if (config.htmlReport) {
		    repGen = 
			new SimpleHtmlSubmissionReportGenerator(new File(config.htmlDir),
								codeTokenizer,
								config.maximumDetectionResultsToReport);
		}
		else {
		    repGen = 
			new SimpleTextSubmissionReportGenerator(System.out,
								generateFileDetectionReports,
								fileRepGen);
	
		}
		    
		System.out.println("************************************************************************");
		System.out.println("   Plaggie report");
		System.out.println("************************************************************************");
		
		HtmlPrintable statsPrinter = new HtmlPrintable() {
			public void printHtmlReport(PrintStream out) {
			    try {
	
				out.println("<H3>Distribution of similarities, Average: "+
					    Stats.getPercentage(Stats.getDistributionAverage("submission_similarities"))+
					    "</H3>");
				Stats.printHtmlDistribution(out, "submission_similarities", 0.0, 1.0, 0.1, true, '#', 80);
				out.println("<H3>Distribution of similarities A, Average: "+
					    Stats.getPercentage(Stats.getDistributionAverage("submission_similarities_a"))+
					    "</H3>");
				Stats.printHtmlDistribution(out, "submission_similarities_a", 0.0, 1.0, 0.1, true, '#',80);
				out.println("<H3>Distribution of similarities B, Average: "+
					    Stats.getPercentage(Stats.getDistributionAverage("submission_similarities_b"))+
					    "</H3>");
				Stats.printHtmlDistribution(out, "submission_similarities_b", 0.0, 1.0, 0.1, true, '#',80);
				out.println("<H3>Distribution of maximum file similarities, Average: "+
					    Stats.getPercentage(Stats.getDistributionAverage("maximum_file_similarities"))+
					    "</H3>");
				out.println("<P>If there are a lot of high similarity values (i.e. > 20%) in this distribution, "+
					    "there is a reason to suspect, that some code provided to the students "+
					    "with the exercise definition or somewhere else is not excluded from the "+
					    "detection. You can use the code exclusion parameters in the configuration "+
					    "file to exclude code from the detection algorithm.");
				Stats.printHtmlDistribution(out, "maximum_file_similarities", 0.0, 1.0, 0.1, true, '#', 80);
				
				out.println("<H3>Files per submission</H3>");
				Stats.printHtmlDistribution(out, "files_in_submission", 
							    Stats.getDistributionMin("files_in_submission"),
							    Stats.getDistributionMax("files_in_submission"),
							    1.0, false,
							    '#',80);
				out.println("<H3>Counters</H3>");
				Stats.printHtml(out);
			    }
			    catch (Exception e) {
				out.println("Exception while printing statistics:");
			        e.printStackTrace(out);
			    }
			}
			
		    };
		
		repGen.generateReport(detResults, submissions, config, statsPrinter);
		
		System.out.println("************************************************************************");
		System.out.println("   End of Plaggie report");
		System.out.println("************************************************************************");
	
    }

    /**
     * Returns the token list of the given file.
     */    
    private static TokenList createTokenList(String filename,
					     CodeTokenizer tokenizer) 
	throws Exception {

	TokenList tokens = tokenizer.tokenize(new File(filename));
	
	return tokens;
    }

    /**
     * Creates the directory for generating an HTML report. If
     * directory exists and is not empty, prompts user for its
     * removing. If it doesn't exist at all, prompts user for its
     * creation.
     */
    private static boolean createHtmlDirectory() 
	throws Exception 
    {

	String sDir = config.htmlDir;

	File dir = new File(sDir);
	
	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
	
	// Check the existence of HTML directory
	if (!dir.exists()) {
	    System.out.print("HTML report directory "+sDir+" does not exist. Do you want to create it ? [Y]/N :");
	    String input = b.readLine();
	    if ((input.length() == 0) || (input.toUpperCase().charAt(0) == 'Y')) {
		dir.mkdir();
		return true;
	    }
	    else {
		return false;
	    }
	}

	File[] files = dir.listFiles();
	
	if (files.length == 0) {
	    return true;
	}
	else {
	    // The HTML directory is not empty
	    System.out.print("HTML report directory "+sDir+" is not empty. Do you want to delete the directory contents and generate a new report? [Y]/n :");
	    String input = b.readLine();
	    if ((input.length() == 0) || (input.toUpperCase().charAt(0) == 'Y')) {
		removeDirectory(dir, false);
		return true;
	    }
	    else {
		return false;
	    }
	}
    }
    
    /**
     * Removes (recursively) the given directory. If removeBaseDir is
     * true, also the given directory is removed.
     */
    private static void removeDirectory(File dir, boolean removeBaseDir) 
	throws Exception
    {
	File[] iter = dir.listFiles();
	for (int i=0; i < iter.length; i++) {
	    File f = iter[i];
	    if (f.isDirectory()) {
		removeDirectory(f, true);
	    }
	    else {
		if (!f.delete()) {
		    throw new IOException("Unable to delete file "+f.getPath());
		}
	    }
	}
	if (removeBaseDir) {
	    if (!dir.delete()) {
		throw new IOException("Unable to delete directory "+dir.getPath());
	    }
	}
    }

    /**
     * The main program. Parses the command line parameters and runs
     * the detection algorithm and generates the report.
     */
    public static void main(String[] args) 
    {
    	// --> Set the path of the assignment folder in the 3rd slot in the array
    	args = new String[] { "-none", "-none",	"C:/Users/LawanSubba/Desktop/Test" };
    	
	try 
	{
	    runtime = Runtime.getRuntime();

	    // -- Print program info
	    System.out.println("Plaggie - Plagiarism Detection tool");

	    // -- Read the configuration file

	    String confFile = CONF_FILE;
	    File fConfFile = new File(confFile);
	    if (!fConfFile.exists()) {
		System.out.println("Configuration file "+confFile+" not found!");
		System.exit(1);
	    }
	    try {
		configure(confFile);
	    }
	    catch (ConfigurationException ce) {
		System.out.println(ce.getMessage());
		System.exit(1);
	    }

	    // -- Read the command line configuration

	    int argc = 0;
	    while ((args.length > argc) && (args[argc].startsWith("-"))) {
		if (args[argc].charAt(1) == 's') {
		    // minimumSubmissionSimilarityCount
		    double minSubSim = Double.parseDouble(args[argc].substring(2));
		    System.out.println("Command line config: Setting minimum submission similarity value to "+minSubSim);
		    config.minimumSubmissionSimilarityValue = minSubSim;
		}
		if (args[argc].substring(1).equals("nohtml")) {
		    // No HTML report
		    System.out.println("Command line config: No html report generated");
		    config.htmlReport = false;
		}
		argc++;
	    }

	    Debug.setEnabled(config.debugMessages);

	    //-- Check the remaining command line parameters
	    if (args.length <= argc) {
		throw new IllegalArgumentException("No directories and/or files specified on command line");
	    }

	    //-- Create or erase the html directory if necessary
	    if (config.htmlReport) {
		if (!createHtmlDirectory()) {
		    System.exit(0);
		}
	    }

	    // -- Get the directory(or file) names given as command
	    // -- line parameters
	    File file1 = new File(args[argc]);
	    argc++;
	    File file2 = null;
	    if (args.length > argc) {
		file2 = new File(args[argc]);
	    }
	    
	    // -- Create the code tokenizer object for parsing the source code files
	    codeTokenizer = (CodeTokenizer)Class.forName(config.codeTokenizer).newInstance();


	    // -- Read and create the submissions, if the results are not
	    // -- read from a file
	    ArrayList detResults = null;
	    ArrayList submissions = null;

	    if ((!config.readResultsFromFile) ||
		(config.readResultsFromFile && config.createResultFile)) {
		
		Stats.newCounter("submissions");
		Stats.newCounter("parsed_files");
		Stats.newCounter("files_to_parse");
		Stats.newCounter("parse_failures");
		Stats.newCounter("failed_file_comparisons");
		Stats.newCounter("file_comparisons");
		Stats.newCounter("similarity_over_threshold");
		Stats.newCounter("similarity_comparisons");
		Stats.newCounter("blacklisted_detection_results");
		
		Stats.newDistribution("files_in_submission");
		Stats.newDistribution("submission_similarities");
		Stats.newDistribution("submission_similarities_a");
		Stats.newDistribution("submission_similarities_b");
		Stats.newDistribution("maximum_file_similarities");



		// Create the submissions

		if (file2 == null) {
		    // file1 should now be a directory, which contains subdirectories, which each contain
		    // submission of one student
		    if (!file1.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: "+file1.getPath());
		    }
		    submissions = getDirectorySubmissions(file1);
		}
		else {
		    // two parameters, each is a directory or a file name. Create the submissions and add
		    // them to the list.

		    submissions = new ArrayList();

		    FilenameFilter filter = generateFilenameFilter();

		    if (file1.isDirectory()) {
			DirectorySubmission sub = new DirectorySubmission(file1,
									  filter,
									  config.useRecursive);
			submissions.add(sub);
			Stats.incCounter("submissions");
		    }
		    else { 
			submissions.add(new SingleFileSubmission(file1));
			Stats.incCounter("submissions");
		    }
		    
		    if (file2.isDirectory()) {
			submissions.add(new DirectorySubmission(file2,
								filter,
								config.useRecursive));
			Stats.incCounter("submissions");
		    }
		    else {
			submissions.add(new SingleFileSubmission(file2));
			Stats.incCounter("submissions");
		    }
		    
		}
		
		// -- Create distributions for all the submissions' similarity values
		Iterator iter = submissions.iterator();
		while (iter.hasNext()) {
		    Submission sub = (Submission)iter.next();
		    Stats.newDistribution(sub.getName());
		}

		// -- Create the detection results
		detResults = generateDetectionResults(submissions);
		
		if (config.createResultFile) {
		    storeDetectionResultsInFile(detResults);
		}
	    }

	    if (config.readResultsFromFile) {
		// -- Read the results from a file
		detResults = readDetectionResultsFromFile();
	    }

	    // -- Report the results
	    generateReport(detResults, submissions);
	    
	}
	
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
    
	/**
	 * The main program. Parses the command line parameters and runs the
	 * detection algorithm and generates the report.
	 */
	public static void Run(String[] args, HashMap<String, String> settings) 
	{
		try
		{
			//Configure all the settings
			configure();

			if(!settings.isEmpty())
			{
				if(settings.containsKey("minimumMatchLength"))
				{
					config.minimumMatchLength = Integer.parseInt(settings.get("minimumMatchLength"));
				}
				
				if(settings.containsKey("minimumSubmissionSimilarityValue"))
				{
					double tmp = Double.parseDouble(settings.get("minimumSubmissionSimilarityValue"));
					tmp = tmp/100;
					config.minimumSubmissionSimilarityValue = tmp;
				}
				
				if(settings.containsKey("maximumDetectionResultsToReport"))
				{
					config.maximumDetectionResultsToReport = Integer.parseInt(settings.get("maximumDetectionResultsToReport"));
				}
				
				if(settings.containsKey("excludeFiles"))
				{
					config.excludeFiles = settings.get("excludeFiles");
				}
				
			}
			
			// -- Read the command line configuration
			int argc = 0;
			
			// -- Get the directory(or file) names given as command
			// -- line parameters
			File file1 = new File(args[argc]);
			argc++;

			File file2 = null;
			if (args.length > argc && args[argc] != null && args[argc] != "") 
			{
				file2 = new File(args[argc]);
			}
			
			argc++;
			if (args.length > argc) 
			{
				config.htmlDir = args[argc];
			}
								
			// -- Create or erase the html directory if necessary
			if (config.htmlReport ) 
			{
				if(args.length < 3)
				{
					if (!createHtmlDirectory()) 
					{
						System.exit(0);
					}
				}
				else
				{
					createHtmlDirectoryForced();
				}
			}
	
			// -- Create the code tokenizer object for parsing the source code
			// files
			codeTokenizer = (CodeTokenizer) Class.forName(config.codeTokenizer).newInstance();
			
			// -- Instruct the code tokenizer to read and accept the Plaggie
			// configuration settings
			//codeTokenizer.acceptSystemConfiguration(config);

			// -- Read and create the submissions, if the results are not
			// -- read from a file
			ArrayList detResults = null;
			ArrayList submissions = null;

			if ((!config.readResultsFromFile) || (config.readResultsFromFile && config.createResultFile)) 
			{
				Stats.newCounter("submissions");
				Stats.newCounter("parsed_files");
				Stats.newCounter("files_to_parse");
				Stats.newCounter("parse_failures");
				Stats.newCounter("failed_file_comparisons");
				Stats.newCounter("file_comparisons");
				Stats.newCounter("similarity_over_threshold");
				Stats.newCounter("similarity_comparisons");
				Stats.newCounter("blacklisted_detection_results");

				Stats.newDistribution("files_in_submission");
				Stats.newDistribution("submission_similarities");
				Stats.newDistribution("submission_similarities_a");
				Stats.newDistribution("submission_similarities_b");
				Stats.newDistribution("maximum_file_similarities");

				// Create the submissions


				// file1 should now be a directory, which contains
				// subdirectories, which each contain
				// submission of one student
				if (!file1.isDirectory()) 
				{
					throw new IllegalArgumentException("Not a directory: "
							+ file1.getPath());
				}
				submissions = getDirectorySubmissions(file1);

				// -- Create distributions for all the submissions' similarity
				// values
				Iterator iter = submissions.iterator();
				while (iter.hasNext()) 
				{
					Submission sub = (Submission) iter.next();
					Stats.newDistribution(sub.getName());
				}

				// -- Create the detection results
				detResults = generateDetectionResults(submissions);

				if (config.createResultFile) 
				{
					storeDetectionResultsInFile(detResults);
				}
			}

			if (config.readResultsFromFile) 
			{
				// -- Read the results from a file
				detResults = readDetectionResultsFromFile();
			}

			// -- Report the results
			generateReport(detResults, submissions);
			Stats.clearCounters();
		}
		catch(Exception ex)
		{
			String errorLog = config.htmlDir + "\\error.log";
			
			boolean append = true;
		    FileHandler handler = null;
			try {
				handler = new FileHandler(errorLog, append);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    handler.setFormatter(new SimpleFormatter());
		    Logger logger = Logger.getLogger("Log");
		    
		    logger.addHandler(handler);
		    logger.setLevel(Level.CONFIG);
		    
		    logger.log(Level.INFO, ex.getMessage());
		    handler.close();
		}
	}
	
	private static void createHtmlDirectoryForced() throws Exception {

		String sDir = config.htmlDir;

		File dir = new File(sDir);

		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

		// Check the existence of HTML directory
		if (!dir.exists()) 
		{
			dir.mkdirs();
		}

		File[] files = dir.listFiles();

		if (files != null && files.length != 0) 
		{
			removeDirectory(dir, false);
		} 
	}
}


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

/**
 * A report generator, which generates a report of a set or a single Submission
 * detection result.
 *
 */
public class SimpleTextSubmissionReportGenerator
    implements SubmissionReportGenerator
{

    /** The print stream at which the report is generated. */
    private PrintStream out;

    /** Whether to print reports between all the files. */
    private boolean generateFileDetectionReports;
    /** The report generator to use for printing file detection reports. */
    private ReportGenerator fileDetectionReportGenerator = null;

    public SimpleTextSubmissionReportGenerator(PrintStream out,
					       boolean generateFileDetectionReports,
					       ReportGenerator fileDetectionReportGenerator) {
	this.out = out;
	this.generateFileDetectionReports = generateFileDetectionReports;
	this.fileDetectionReportGenerator = fileDetectionReportGenerator;
    }

    public void generateReport(SubmissionDetectionResult detResult)
	throws Exception
    {
	double similarityA = detResult.getSimilarityA();
	double similarityB = detResult.getSimilarityB();

	out.println("========================================================================");
	out.println("Similarity A:"+similarityA);
	out.println("Similarity B:"+similarityB);
	out.println("------------------------------------------------------------------------");
	out.println("Files in submission A:");
	Iterator files = detResult.getSubmissionA().getFiles();
	while (files.hasNext()) {
	    File f = (File)files.next();
	    out.println(f.getPath());
	}
	out.println("------------------------------------------------------------------------");
	out.println("Files in submission B:");
	files = detResult.getSubmissionB().getFiles();
	while (files.hasNext()) {
	    File f = (File)files.next();
	    out.println(f.getPath());
	}

	// Generate reports of all the file detection reports
	if (this.generateFileDetectionReports) {
	    out.println("------------------------------------------------------------------------");
	    out.println("File detection results");
	    Iterator fileDetResults = detResult.getFileDetectionResults().iterator();
	    ArrayList aList = new ArrayList();
	    while (fileDetResults.hasNext()) {
		aList.add(fileDetResults.next());
	    }
	    this.fileDetectionReportGenerator.generateReport(aList);
	}
    }

    public void generateReport(List detResults, List submissions, HtmlPrintable configuration,
			       HtmlPrintable statistics)
	throws Exception
    {
	Iterator iter = detResults.iterator();

	while (iter.hasNext()) {
	    this.generateReport((SubmissionDetectionResult)iter.next());
	}
    }

    public static class OnlyStatsReportGenerator
	implements ReportGenerator
    {
	private PrintStream out;

	public OnlyStatsReportGenerator(PrintStream out) {
	    this.out = out;
	}

	public void generateReport(DetectionResult detectionResult)
	    throws Exception 
	{
	    String fileA = detectionResult.getFileA().getPath();
	    String fileB = detectionResult.getFileB().getPath();

	    int matchCount = detectionResult.getMatches().size();
	    int tokensACount = detectionResult.getTokensA().size();
	    int tokensBCount = detectionResult.getTokensB().size();

	    double similarityA = detectionResult.getSimilarityA();
	    double similarityB = detectionResult.getSimilarityB();

	    out.println("------------------------------------------------------------------------");
	    out.println(fileA+": tokens:"+tokensACount+", similarity:"+similarityA);
	    out.println(fileB+": tokens:"+tokensBCount+", similarity:"+similarityB);
	    out.println("matches:"+matchCount);
	}

	public void generateReport(List detectionResults)
	    throws Exception
	{
	    Iterator iter = detectionResults.iterator();

	    while (iter.hasNext()) {
		this.generateReport((DetectionResult)iter.next());
	    }
	}
	


    }
}

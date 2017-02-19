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

import java.util.*;
import java.io.*;

public class SimpleSubmissionSimilarityChecker
    implements SubmissionSimilarityChecker
{
    protected TokenSimilarityChecker checker;

    protected CodeTokenizer tokenizer;

    protected static class DetectionResultComparator
	implements Comparator, Serializable
    {
	
	public int compare(Object o1, Object o2) 
	{
	    DetectionResult dr1 = (DetectionResult)o1;
	    DetectionResult dr2 = (DetectionResult)o2;
	    try {
		double sum1 =
		    dr1.getSimilarityA()+dr1.getSimilarityB();
		double sum2 = 
		    dr2.getSimilarityA()+dr2.getSimilarityB();
	    
		if (sum1 > sum2)
		    return -1;
		else
		    return 1;
	    }
	    catch (Exception e) {
		Debug.println(this, "Exception during compare: "+e.getMessage());
		e.printStackTrace();		
		return -1;
	    }
	}
    }

    /**
     * Contains an entry for each checked file in submissionA. The
     * data part contains a SortedSet (according to the order
     * specified in
     * SimpleSubmissionSimilarityChecker.DetectionResultComparator) of
     * all the DetectionResults against all the files in submissionB.
     */
    protected HashMap fileDetectionResults = null;

    public SimpleSubmissionSimilarityChecker(TokenSimilarityChecker checker,
					     CodeTokenizer tokenizer)
    {
	this.checker = checker;
	this.tokenizer = tokenizer;
    }

    /**
     * Counts the similarity between 2 submissions.
     */
    public void countSimilarities(Submission submissionA,
				  Submission submissionB)
	throws Exception
    {
	Iterator filesA = submissionA.getFiles();

	Iterator filesB;
	
	this.fileDetectionResults = new HashMap();

	Comparator detectionResultComp = new
	    SimpleSubmissionSimilarityChecker.
	    DetectionResultComparator();

	while (filesA.hasNext()) {
	    File fileA = (File)filesA.next();
	    SortedSet ss = new TreeSet(detectionResultComp);
	    fileDetectionResults.put(fileA, ss);
	    filesB = submissionB.getFiles();
	    while (filesB.hasNext()) {
		File fileB = (File)filesB.next();
		Debug.println(this,"Adding detection result between "+fileA.getPath()+" and "+fileB.getPath());
		
		try {
		    DetectionResult detResult = new DetectionResult(fileA,
								    fileB,
								    this.checker,
								    this.tokenizer);
		    ss.add(detResult);
		}
		catch (Exception e) {
		    Stats.incCounter("failed_file_comparisons");
		    //		    System.err.println("Error generating detection result between "+fileA.getPath()+" and "+fileB.getPath()+":"+e.getMessage()+". Result ignored.");
		    //e.printStackTrace();
		}
		Stats.incCounter("file_comparisons");
	    }
	}
    }

    /**
     * Return the resulting DetectionResults between the files of each
     * submisison.
     */
    public Collection getFileDetectionResults()
    {
	Iterator sets = fileDetectionResults.values().iterator();
	SortedSet retSet = new TreeSet(new SimpleSubmissionSimilarityChecker.DetectionResultComparator());

	while (sets.hasNext()) {
	    SortedSet ss = (SortedSet)sets.next();
	    Debug.println(this, "Adding "+ss.size()+" detection results to return set.");
	    retSet.addAll(ss);
	}
	Debug.println(this,retSet.size()+" detection results in the return set.");

	return retSet;
    }
    
    /**
     * Returns the similarity value of how much the first item is
     * similar to the second item.
     */
    public double getSimilarityValueA()
	throws Exception
    {
	// Get the best match of each file and calculate the average

	int counter = 0;
	double val = 0.0;

	Iterator sets = fileDetectionResults.values().iterator();
	
	while (sets.hasNext()) {
	    SortedSet ss = (SortedSet)sets.next();
	    if (ss.size() > 0) {
		DetectionResult dt = (DetectionResult)ss.first();
		val += dt.getSimilarityA();
		Debug.println(this, "Getting file similarity value A:"+dt.getSimilarityA());
		counter++;
	    }
	}
	if (counter > 0) 
	    return val/counter;
	else
	    return 0;
    }


    /**
     * Returns the similarity value of how much the second item is
     * similar to the first item. Use getSimilarityValueA to get a
     * better value!
     */
    public double getSimilarityValueB()
	throws Exception
    {
	// This could be done correctly by generating another
	// SimpleSubmissionSimilarityChecker and checking submissions
	// in another order. For now this method is enough, although
	// not even all the B files are taken into account (since the
	// hashmap is created using A files as keys)

	// Get the best match of each file and calculate the average

	int counter = 0;
	double val = 0.0;

	Iterator sets = fileDetectionResults.values().iterator();
	
	while (sets.hasNext()) {
	    SortedSet ss = (SortedSet)sets.next();
	    if (ss.size() > 0) {
		DetectionResult dt = (DetectionResult)ss.first();
		val += dt.getSimilarityB();
		counter++;
	    }
	}
	if (counter > 0) 
	    return val/counter;
	else
	    return 0;

    }

    
}


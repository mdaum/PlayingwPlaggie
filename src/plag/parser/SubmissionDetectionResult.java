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

import java.util.Collection;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;


public class SubmissionDetectionResult
    implements java.io.Serializable
{

    private Submission submissionA;
    private Submission submissionB;

    private double similarityA = -1.0;
    private double similarityB = -1.0;

    private boolean blacklistedA = false;
    private boolean blacklistedB = false;

    //    private SubmissionSimilarityChecker checker = null;

    private Collection fileDetectionResults = null;

    private void createData(SubmissionSimilarityChecker checker) 
	throws Exception 
    {
	checker.countSimilarities(submissionA,
				       submissionB);
	this.similarityA = checker.getSimilarityValueA();
	this.similarityB = checker.getSimilarityValueB();
	this.fileDetectionResults = checker.getFileDetectionResults();
    }

    private void dropFileDetectionResults(double minimumFileSimilarityValue) 
	throws Exception 
    {
	ArrayList storedResults = new ArrayList();
	Iterator iter = fileDetectionResults.iterator();
	while (iter.hasNext()) {
	    DetectionResult dR = (DetectionResult)iter.next();
	    if ((dR.getSimilarityA() >= minimumFileSimilarityValue) ||
		(dR.getSimilarityB() >= minimumFileSimilarityValue)) {
		storedResults.add(dR);
	    }
	}
	this.fileDetectionResults = storedResults;
    }

    public SubmissionDetectionResult(Submission submissionA,
				     Submission submissionB,
				     SubmissionSimilarityChecker checker,
				     double minimumFileSimilarityValue) 
	throws Exception 
    {
	this.submissionA = submissionA;
	this.submissionB = submissionB;
	this.createData(checker);
	this.dropFileDetectionResults(minimumFileSimilarityValue);
    }

    /**
     * returns the maximum file similarity value (product of file similarities
     * a and b).
     */
    public double getMaxFileSimilarityProduct() 
	throws Exception 
    {
	Collection fileDetResults = getFileDetectionResults();

	Iterator iter = fileDetResults.iterator();
	double maxVal = 0.0;

	while (iter.hasNext()) {
	    DetectionResult dR = (DetectionResult)iter.next();
	    double product = dR.getSimilarityA()*dR.getSimilarityB();
	    if (product > maxVal) {
		maxVal = product;
	    }
	}
	return maxVal;
    }

    public Submission getSubmissionA() 
    {
	return this.submissionA;
    }

    public Submission getSubmissionB() 
    {
	return this.submissionB;
    }

    public Collection getFileDetectionResults() 
    {
	return this.fileDetectionResults;
    }

    public double getSimilarityA() 
    {
	return this.similarityA;
    }
    
    public double getSimilarityB() 
    {
	return this.similarityB;
    }
   
    public void setBlacklistedA(boolean value) {
	this.blacklistedA = value;
    }

    public boolean isBlacklistedA() {
	return this.blacklistedA;
    }

    public void setBlacklistedB(boolean value) {
	this.blacklistedB = value;
    }

    public boolean isBlacklistedB() {
	return this.blacklistedB;
    }

}

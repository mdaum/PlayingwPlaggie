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

public class CachingSimpleSubmissionSimilarityChecker
    extends SimpleSubmissionSimilarityChecker
{
    
    protected Map tokenListCache;

    public CachingSimpleSubmissionSimilarityChecker(TokenSimilarityChecker checker,
						    CodeTokenizer tokenizer,
						    Map tokenListCache)
    {
	super(checker,tokenizer);
	this.tokenListCache = tokenListCache;
    }

    /**
     * Counts the similarity between 2 submissions. Caches the token lists generated from files.
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
		    DetectionResult detResult = new CachingDetectionResult(fileA,
									   fileB,
									   this.checker,
									   this.tokenizer,
									   this.tokenListCache);
		    ss.add(detResult);
		}
		catch (Exception e) {
		    Stats.incCounter("failed_file_comparisons");
		    //		    System.err.println("Error generating detection result between "+fileA.getPath()+" and "+fileB.getPath()+". Result ignored.");
		    //		    e.printStackTrace();
		}
		Stats.incCounter("file_comparisons");

	    }
	}
    }


}


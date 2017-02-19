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

import java.util.Iterator;
import java.util.Collection;

/**
 * Represents a similarity checker of two submissions.
 */
public interface SubmissionSimilarityChecker
    extends SimilarityChecker
{
    /**
     * Counts the similarity between 2 submissions.
     */
    public void countSimilarities(Submission submissionA,
				  Submission submissionB)
	throws Exception;

    /**
     * Return the resulting DetectionResults between the files of each
     * submisison.
     */
    public Collection getFileDetectionResults();
    
    /**
     * Returns the similarity value of how much the first item is
     * similar to the second item.
     */
    public double getSimilarityValueA()
	throws Exception;

    /**
     * Returns the similarity value of how much the second item is
     * similar to the first item.
     */
    public double getSimilarityValueB()
	throws Exception;
    
}

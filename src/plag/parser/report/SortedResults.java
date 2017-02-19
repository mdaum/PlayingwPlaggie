package plag.parser.report;

import plag.parser.Submission;

/**
 * This class provides the wrapper class to store similar Submissions together for faster access
 * @author LawanSubba
 *
 */
public class SortedResults {
	Submission s;
	String similarityA;
	String similarityB;
	String similarity;
	public int detNumber;
	
	/**
	 * @param s	represents the submission of interest
	 * @param similarityA represents the similarity A
	 * @param similarityB represents the similarity B
	 * @param similarity represents the similarity
	 * @param detNumber	the detection number
	 */
	public SortedResults(Submission s, String similarityA, String similarityB, String similarity, int detNumber) 
	{
		super();
		this.s = s;
		this.similarityA = similarityA;
		this.similarityB = similarityB;
		this.similarity = similarity;
		this.detNumber = detNumber;
	}

	@Override
	public String toString() 
	{
		return "" + s ;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + detNumber;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		result = prime * result
				+ ((similarity == null) ? 0 : similarity.hashCode());
		result = prime * result
				+ ((similarityA == null) ? 0 : similarityA.hashCode());
		result = prime * result
				+ ((similarityB == null) ? 0 : similarityB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortedResults other = (SortedResults) obj;
		if (detNumber != other.detNumber)
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		if (similarity == null) {
			if (other.similarity != null)
				return false;
		} else if (!similarity.equals(other.similarity))
			return false;
		if (similarityA == null) {
			if (other.similarityA != null)
				return false;
		} else if (!similarityA.equals(other.similarityA))
			return false;
		if (similarityB == null) {
			if (other.similarityB != null)
				return false;
		} else if (!similarityB.equals(other.similarityB))
			return false;
		return true;
	}
	
}

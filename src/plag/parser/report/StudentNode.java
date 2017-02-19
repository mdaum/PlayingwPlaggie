package plag.parser.report;

/* This class represents the data element which are used in the union find
* @author LawanSubba
*
*/
class StudentNode 
{
	public String parent;
	public int rank;

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentNode other = (StudentNode) obj;
		if (parent != other.parent)
			return false;
		return true;
	}

}
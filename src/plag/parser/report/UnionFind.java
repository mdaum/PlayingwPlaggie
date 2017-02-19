package plag.parser.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class holds the operations to perform the union find.
 * @author LawanSubba
 *
 */
class UnionFind 
{
	TreeMap<String, StudentNode> map = new TreeMap<String, StudentNode>();

	/**
	 * Function to find the string in the map
	 * @param i represents the string to find in the map
	 * @return returns the data of the map
	 */
	public StudentNode find(String Student) 
	{
		if (Student != map.get(Student).parent) 
		{
			map.get(Student).parent = find(map.get(Student).parent).parent;			
		}
		return map.get(Student);
	}

	/** Function to perform the union operation on the map
	 * @param d1 represents the first parameter
	 * @param d2 represents the second parameter
	 */
	public void union(String name1, String name2) 
	{
		StudentNode student1 = new StudentNode();
		student1.parent = name1;
		student1.rank = 0;
		
		StudentNode student2 = new StudentNode();
		student2.parent = name2;
		student2.rank = 0;
		
		if (!map.containsKey(name1))
			map.put(name1, student1);
		if (!map.containsKey(name2))
			map.put(name2, student2);

		StudentNode parent1 = find(name1);
		StudentNode parent2 = find(name2);

		if (parent1 != parent2) 
		{			
			if (parent1.rank < parent2.rank) 
			{
				find(parent1.parent).parent = parent2.parent;
				if (parent1.rank == parent2.rank)
				{
					parent2.rank++;
				}
				 
			} 
			else 
			{
				find(parent2.parent).parent = parent1.parent;
			}
		}
	}

	/**
	 * @return function returns the treemap of all groups of studnets that share similarirty
	 */
	public TreeMap<String, ArrayList<String>> GetGroups() 
	{
		TreeMap<String, ArrayList<String>> groups = new TreeMap<String, ArrayList<String>>();

		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) 
		{
			String student = keys.next();
			find(student);
			String parent = map.get(student).parent;
			ArrayList<String> alistStudents = new ArrayList<String>();
			
			if (!groups.containsKey(parent)) 
			{
				alistStudents.clear();
				alistStudents.add(student);
				groups.put(parent, alistStudents);
			}
			else
			{
				alistStudents = groups.get(parent);
				alistStudents.add(student);
			}
		}
		return groups;
	}
}
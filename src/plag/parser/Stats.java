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

import java.io.*;
import java.util.*;
import plag.parser.report.HtmlPrintable;
import java.text.NumberFormat;

public class Stats
    implements Serializable
{

    private static Stats instance = new Stats();

    private HashMap counters = new HashMap();

    private HashMap counterLimits = new HashMap();

    private HashMap distributions = new HashMap();

    private int longestCounterName = 0;

    /**
     * Returns the given value as a percentage.
     */
    public static String getPercentage(double val) {

	NumberFormat nf = NumberFormat.getPercentInstance();

	nf.setMaximumFractionDigits(1);
	
	return nf.format(val);

    }


    public static Stats getInstance() {
	return instance;
    }

    public static void setInstance(Stats newInstance) {
	instance = newInstance;
    }

    public static void newCounter(String name) 
	throws StatsException 
    {
	if (instance.counters.get(name) != null) {
	    throw new StatsException("Stat "+name+" already defined.");
	}
	instance.counters.put(name, new Integer(0));
	if (name.length() > instance.longestCounterName)
	    instance.longestCounterName = name.length();
    }

    public static void setCounterLimit(String name, int limit)
	throws StatsException
    {
	if (instance.counters.get(name) == null) {
	    throw new StatsException("Stat "+name+" does not exist.");
	}
	else {
	    instance.counterLimits.put(name, new Integer(limit));
	}
    }

    public static int getCounterLimit(String name) 
	throws StatsException 
    {
	if (instance.counterLimits.get(name) == null) {
	    throw new StatsException("Counter limit of counter "+name+" does not exist.");
	}
	else {
	    return ((Integer)instance.counterLimits.get(name)).intValue();
	}
    }

    public static void incCounter(String name) 
	throws StatsException
    {
	if (instance.counters.get(name) == null) {
	    throw new StatsException("Stat "+name+" does not exist.");
	}
	else
	    instance.counters.put(name, new Integer(((Integer)instance.counters.get(name)).intValue()+1));
    }

    public static int getCounter(String name)
	throws StatsException
    {
	if (instance.counters.get(name) == null) {
	    throw new StatsException("Stat "+name+" does not exist.");
	}
	else
	    return ((Integer)instance.counters.get(name)).intValue();
    }

    public static void newDistribution(String name)
	throws StatsException
    {
	if (instance.distributions.get(name) != null) {
	    throw new StatsException("Distribution "+name+" already defined.");
	}
	instance.distributions.put(name, new ArrayList());
    }

    public static List getDistribution(String name)
	throws StatsException
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else 
	    return ((List)instance.distributions.get(name));
    }

    public static double getDistributionMax(String name)
	throws StatsException 
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else {
	    List dist = (ArrayList)instance.distributions.get(name);
	    double max = Double.MIN_VALUE;
	    Iterator i = dist.iterator();
	    while (i.hasNext()) {
		double val = ((Double)i.next()).doubleValue();
		if (val > max) {
		    max = val;
		}
	    }
	    return max;
	}
    }
	    
    public static double getDistributionMin(String name)
	throws StatsException 
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else {
	    List dist = (ArrayList)instance.distributions.get(name);
	    double min = Double.MAX_VALUE;
	    Iterator i = dist.iterator();
	    while (i.hasNext()) {
		double val = ((Double)i.next()).doubleValue();
		if (val < min) {
		    min = val;
		}
	    }
	    return min;
	}
    }

    public static void addToDistribution(String name, double value)
	throws StatsException
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else {
	    ArrayList list = (ArrayList)instance.distributions.get(name);
	    list.add(new Double(value));
	}
    }

    public static double getDistributionAverage(String name)
	throws StatsException
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else {
	    ArrayList list = (ArrayList)instance.distributions.get(name);
	    if (list.size() == 0) {
		return 0.0;
	    }

	    Iterator iter = list.iterator();
	    double sum = 0.0;
	    while (iter.hasNext()) {
		sum += ((Double)iter.next()).doubleValue();
	    }
	    return sum / list.size();
	}
    }

    public static void printHtmlDistribution(PrintStream out, String name, double lowVal, double highVal, double step, boolean usePercentage,
					 char displayChar, int maxChars)
	throws StatsException 
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else {
	    List dist = (ArrayList)instance.distributions.get(name);
	    int counts[] = new int[(int)((highVal-lowVal)/step)+1];
	    Iterator iter = dist.iterator();
	    int maxCount = 0;
	    
	    while (iter.hasNext()) {
		double val = ((Double)iter.next()).doubleValue();
		if ((val >= lowVal) && (val <= highVal)) {
		    int p = (int)((val-lowVal)/step);
		    counts[p]++;
		    if (counts[p] > maxCount) {
			maxCount = counts[p];
		    }
		}
	    }

	    double oneStar = maxChars/(double)maxCount;

	    out.println("<TABLE BORDER=\"1\">");
	    for (int i = (counts.length - 1); i >= 0; i--) {
		out.print("<TR><TD>");
		if (usePercentage) {
		    out.print(" "+getPercentage(lowVal+i*step)+"</TD><TD>"+counts[i]);
		}
		else {
		    out.print(" "+(lowVal+i*step)+"</TD><TD>"+counts[i]);
		}
		out.print("</TD><TD>");
		int starCount = (int)(oneStar*counts[i]);
		for (int j=0; j < starCount; j++) {
		    out.print(displayChar);
		}
		out.print(createEmptyString(maxChars-starCount));
		out.println("</TD></TR>");
	    }
	    out.println("</TABLE>");
	}
    }

    public static void printDistribution(PrintStream out, String name, double lowVal, double highVal, double step,
					 char displayChar, int maxChars)
	throws StatsException
    {
	if (instance.distributions.get(name) == null) {
	    throw new StatsException("Distribution "+name+" does not exist.");
	}
	else {
	    List dist = (ArrayList)instance.distributions.get(name);
	    int counts[] = new int[(int)((highVal-lowVal)/step)+1];
	    Iterator iter = dist.iterator();
	    int maxCount = 0;
	    
	    while (iter.hasNext()) {
		double val = ((Double)iter.next()).doubleValue();
		if ((val >= lowVal) && (val <= highVal)) {
		    int p = (int)((val-lowVal)/step);
		    counts[p]++;
		    if (counts[p] > maxCount) {
			maxCount = counts[p];
		    }
		}
	    }

	    double oneStar = maxChars/(double)maxCount;

	    for (int i = (counts.length - 1); i >= 0; i--) {
		int starCount = (int)(oneStar*counts[i]);
		for (int j=0; j < starCount; j++) {
		    out.print(displayChar);
		}
		out.print(createEmptyString(maxChars-starCount));
		out.println(" "+(lowVal+i*step)+":"+counts[i]);
	    }
	}
    }

    public static void printHtml(PrintStream out) {
	Iterator statKeys = instance.counters.keySet().iterator();

	out.println("<TABLE BORDER=\"1\">");
  	while (statKeys.hasNext()) {
	    String statName = (String)statKeys.next();
	    int value = ((Integer)instance.counters.get(statName)).intValue();
	    out.print("<TR><TD>"+statName+"</TD><TD>"+value);
	    Integer limit;
	    if ( (limit = (Integer)instance.counterLimits.get(statName)) != null) {
		out.print("/"+limit.intValue());
	    }
	    out.println("</TD></TR>");
	}
	out.println("</TABLE>");
    }

    public static void print(PrintStream out) {
	Iterator statKeys = instance.counters.keySet().iterator();

  	while (statKeys.hasNext()) {
	    String statName = (String)statKeys.next();
	    int value = ((Integer)instance.counters.get(statName)).intValue();
	    out.print(statName+": "+createEmptyString(instance.longestCounterName-statName.length())+value);
	    Integer limit;
	    if ( (limit = (Integer)instance.counterLimits.get(statName)) != null) {
		out.print("/"+limit.intValue());
	    }
	    out.println();

	}

    }

    private static String createEmptyString(int l) {
	StringBuffer b = new StringBuffer();
	while (l > 0) {
	    b.append(" ");
	    l--;
	}
	return b.toString();
    }
    
	public static void clearCounters()
	{
		instance.counters.clear();
		instance.distributions.clear();
		instance.counterLimits.clear();
	}
}

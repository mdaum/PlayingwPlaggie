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

import java.io.FilenameFilter;
import java.io.File;

import java.util.*;
import plag.parser.Stats;

/**
 * Filename filter, which accepts all files not in a list.
 */
public class ExcludeFilenameFilter
    implements FilenameFilter
{
    
    private ArrayList excludeFiles = new ArrayList();
    

    {
	try {
	    Stats.newCounter("files_excluded_by_filename_filter");
	}
	catch (Exception e) {
	}
    }

    public ExcludeFilenameFilter() {
    }
    
    /**
     * Takes a comma separeted list of file names to be excluded by this filter.
     */
    public ExcludeFilenameFilter(String excludeFiles) {
	StringTokenizer tokens = new StringTokenizer(excludeFiles, ",");
	
	while (tokens.hasMoreTokens()) {
	    String token = tokens.nextToken().trim();
	    this.excludeFiles.add(token);
	}
    }

    public boolean accept(File dir,
			  String name)
    {
	Iterator i = excludeFiles.iterator();
	while (i.hasNext()) {
	    String ef = (String)i.next();
	    if (name.equals(ef)) {
		try {
		    Stats.incCounter("files_excluded_by_filename_filter");
		}
		catch (Exception e) {
		}
		return false;
	    }
	}
	return true;
    }
}

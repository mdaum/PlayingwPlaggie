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

/**
 * Filename filter, which accepts all files that are not in certain subdirectories.
 */
public class SubdirectoryFilter
    implements FilenameFilter
{
    private ArrayList excludeSubdirectories = new ArrayList();

    {
	try {
	    Stats.newCounter("files_excluded_by_subdirectory_filter");
	}
	catch (Exception e) {
	}
    }

    public SubdirectoryFilter()
    {
    }

    public SubdirectoryFilter(String excludeSubdirectories) {
	StringTokenizer tokens = new StringTokenizer(excludeSubdirectories, ",");
	
	while (tokens.hasMoreTokens()) {
	    String token = tokens.nextToken().trim();
	    this.excludeSubdirectories.add(token);
	}
    }
       

    public boolean accept(File dir,
			  String name)
    {
	Iterator i = excludeSubdirectories.iterator();

	while (i.hasNext()) {
	    String sub = (String)i.next();
	    String path = dir.getPath();
	    if (path.endsWith(File.separator+sub) ||
		path.equals(sub)) {
		try {
		    Stats.incCounter("files_excluded_by_subdirectory_filter");
		}
		catch (Exception e) {
		}
		
		return false;
	    }
	}
	return true;
    }

}

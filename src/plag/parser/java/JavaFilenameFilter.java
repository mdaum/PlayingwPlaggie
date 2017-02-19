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
package plag.parser.java;

import java.io.FilenameFilter;
import java.io.File;

import java.util.*;
import plag.parser.Stats;

/**
 * Filename filter, which accepts all files ending with ".java".
 *
 */
public class JavaFilenameFilter
    implements FilenameFilter
{

    /**
     * Initializes the statistics collection.
     */    
    {
	try {
	    Stats.newCounter("files_excluded_as_non_java_files");
	}
	catch (Exception e) {
	}
    }

    public JavaFilenameFilter() {
    }
    
    /**
     * Returns true only if the given file's name ends with .java (not case-sensitive).
     */
    public boolean accept(File dir,
			  String name)
    {
	if ((name.length() > 5) && (name.substring(name.length()-5).toLowerCase().equals(".java"))) {
	    return true;
	}
	else {
	    try {
		Stats.incCounter("files_excluded_as_non_java_files");
	    }
	    catch (Exception e) {
	    }
	    return false;
	}
    }
}

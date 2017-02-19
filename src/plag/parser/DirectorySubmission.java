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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A submission, which contains a complete file system directory.
 */
public class DirectorySubmission
    implements Submission
{

    private ArrayList files = new ArrayList();

    private String name;

    private void addDirectory(File dir, FilenameFilter filter, boolean recursive)
    {
	// get the files and add them to the array list
	File[] realFiles = dir.listFiles(filter);
	
	for (int i = 0; i < realFiles.length; i++) {
	    // Skip the directories now
	    if (!realFiles[i].isDirectory()) {
		files.add(realFiles[i]);
		try {
		    Stats.incCounter("files_to_parse");
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
  
	    }
	}
	
	// go through the sub directories if recursive
	if (recursive) {
	    File[] dirFiles = dir.listFiles();
	    for (int i = 0; i < dirFiles.length; i++) {
		if (dirFiles[i].isDirectory()) {
		    this.addDirectory(dirFiles[i], filter, recursive);
		}
	    }
	}
    }

    /**
     * @param directory the base directory
     * @param filter the file name filter.
     * @param recursive whether to recursively check all the sub directories.
     */
    public DirectorySubmission(File directory,
			       FilenameFilter filter,
			       boolean recursive)
	throws Exception
    {
	if (!directory.isDirectory()) {
	    throw new IllegalArgumentException("Not a directory: "+directory.getPath());
	}
	this.name = directory.getName();
	this.addDirectory(directory, filter, recursive);
	Stats.addToDistribution("files_in_submission",(double)files.size());

    }

    public Iterator getFiles() {
	return files.iterator();
    }
    
    public String toString() {
	return this.name;
    }

    public String getName() {
	return this.name;
    }

}

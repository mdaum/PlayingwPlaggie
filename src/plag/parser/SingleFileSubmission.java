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

import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

/**
 * A Submission which only contains one file.
 */
public class SingleFileSubmission
    implements Submission 
{

    private ArrayList files = new ArrayList();

    private String name;

    public SingleFileSubmission(File file) {
	if (file != null) {
	    this.files.add(file);
	    this.name = file.getPath();
	    try {
		Stats.incCounter("files_to_parse");
	    }
	    catch (Exception e) {
		e.printStackTrace();
	    }
	}
	else {
	    throw new IllegalArgumentException("Invalid file: null");
	}
    }

    public Iterator getFiles() {
	return files.iterator();
    }
    
    public String getName() {
	return this.name;
    }

}


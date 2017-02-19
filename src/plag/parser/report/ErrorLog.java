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
package plag.parser.report;

import java.io.PrintStream;

import java.util.Date;

/**
 * A class for logging the error messages generated during the
 * plagiarism detection process.
 */
public class ErrorLog
{

    private static ErrorLog errorLogger;

    public static void configure(ErrorLog errorLogger) {
	ErrorLog.errorLogger = errorLogger;
    }

    public static ErrorLog getInstance() throws Exception {
	if (errorLogger == null) {
	    throw new Exception("Error logging not configured");
	}
	else {
	    return errorLogger;
	}
    }
	
    private PrintStream out;

    public ErrorLog(PrintStream out) {
	this.out = out;
    }
    
    public void log(String msg) 
    {
	out.println(getTimeStamp()+":"+msg);
    }

    public void log(String msg, Throwable e)
    {
	out.println(getTimeStamp()+":"+msg);
	e.printStackTrace(out);
    }

    public String getTimeStamp() {
	return (new Date()).toString();
    }
}

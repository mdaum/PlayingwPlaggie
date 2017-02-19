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

public class Debug
{
    private static boolean enabled = true;


    public static void setEnabled(boolean newValue) {
	enabled = newValue;
    }

    public static void print(String s) {
	if (enabled) {
	    System.out.print("DEBUG:"+s);
	}
    }

    public static void print(Object o, String s) {
	if (enabled) {
	    System.out.print("DEBUG:"+o.getClass().getName()+":"+s);
	}
    }

    public static void println(String s) {
	if (enabled) {
	    print(s);
	    System.out.print("\n");
	}
    }

    public static void println(Object o, String s) {
	if (enabled) {
	    print(o, s);
	    System.out.print("\n");
	}
    }
}
	

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

import plag.parser.CodeExcluder;
import plag.parser.TokenList;
import plag.parser.Stats;

import java.util.Iterator;

/**
 * A code excluder, which excludes all the interface definitions from the code.
 *
 */
public class InterfaceCodeExcluder
    implements CodeExcluder
{
    /**
     * Initializes the statistics collection.
     */
    {
	try {
	    Stats.newCounter("tokens_excluded_as_interface_code");
	}
	catch (Exception e) {
	}
    }

    /**
     * Marks the interface code in both token lists in the given arrays as true.
     */
    public void mark(TokenList tokensA,
		     TokenList tokensB,
		     boolean[] marksA,
		     boolean[] marksB)
    {
	boolean curMark = false;
	for (int i = 0; i < tokensA.size(); i++) {
	    int value = tokensA.getToken(i).getValue();
	    if (curMark)
		marksA[i] = true;
	    if (value == PlagSym.INTERFACE_DECLARATION) {
		marksA[i] = true;
		try {
		    Stats.incCounter("interfaces_excluded");
		}
		catch (Exception e) {
		}
		curMark = true;
	    }
	    else if (value == PlagSym.INTERFACE_DECLARATION_END) {
		curMark = false;
	    }
	}

	curMark = false;
	for (int i = 0; i < tokensB.size(); i++) {
	    int value = tokensB.getToken(i).getValue();
	    if (curMark) {
		marksB[i] = true;
		try {
		    Stats.incCounter("tokens_excluded_as_interface_code");
		}
		catch (Exception e) {
		}
	    }
	    if (value == PlagSym.INTERFACE_DECLARATION) {
		marksB[i] = true;
		try {
		    Stats.incCounter("tokens_excluded_as_interface_code");
		}
		catch (Exception e) {
		}
		curMark = true;
	    }
	    else if (value == PlagSym.INTERFACE_DECLARATION_END) {
		curMark = false;
	    }
	}
    }
}

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

import java.util.Iterator;

public class ExistingCodeExcluder
    implements CodeExcluder
{
    private TokenList existingTokens = null;
    private int minimumMatchLength;

    {
	try {
	    Stats.newCounter("tokens_excluded_by_existing_code_filter");
	}
	catch (Exception e) {
	}
    }

    public ExistingCodeExcluder(TokenList existingTokens, int minimumMatchLength) {
	this.existingTokens = existingTokens;
	this.minimumMatchLength = minimumMatchLength;
    }
    
    /**
     * Alters marksA and marksB in order not to exclude some tokens in
     * the code from the check.
     *
     * @param tokensA The tokens in source code A
     * @param tokensB The tokens in source code B
     * @param marksA The marks of A.
     * @param marksB The marks of B.
     */
    public void mark(TokenList tokensA,
		     TokenList tokensB,
		     boolean[] marksA,
		     boolean[] marksB) 
    {
	TokenSimilarityChecker checker = 
	    new SimpleTokenSimilarityChecker(this.minimumMatchLength);

	this.exclude(tokensA, checker,
		     marksA);
	
	this.exclude(tokensB, checker,
		     marksB);
    }

    private void exclude(TokenList tokens, TokenSimilarityChecker checker,
			 boolean[] marks) {
	checker.countSimilarities(tokens, this.existingTokens);
	
	MatchedTileSet mtSet = checker.getSimilarityTiles();

	mtSet.setOrdering(MatchedTileSet.ORDER_BY_A);

	Debug.println(""+mtSet);
	
	Iterator iter = mtSet.iterator();

	while (iter.hasNext()) {
	    MatchedTile mt = (MatchedTile)iter.next();
	    
	    int start = mt.getTileA().getStartTokenIndex();
	    int end = mt.getTileA().getEndTokenIndex();

	    for (int i = start; i <= end; i++) {
		Debug.println(this, "Setting mark at "+i);
		try {
		    Stats.incCounter("tokens_excluded_by_existing_code_filter");
		}
		catch (Exception e) {
		}
		marks[i] = true;
	    }
	}
    }


}



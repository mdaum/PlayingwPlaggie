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

import java.io.*;


/**
 * Greedy string tiling algorithm from [5]. Very inefficient implementation.
 */
public class SimpleTokenSimilarityChecker
    implements TokenSimilarityChecker
{
    
    private TokenList tokensA;
    private TokenList tokensB;
    private int minimumMatchLength;
    
    private MatchedTileSet tiles = null;

    private CodeExcluder codeExcluder = new CodeExcluder() {
	    public void mark(TokenList tokensA,
			     TokenList tokensB,
			     boolean[] marksA,
			     boolean[] marksB)
	    {}
	};
	    

    public SimpleTokenSimilarityChecker(int minimumMatchLength) {
	this(minimumMatchLength,
	     new CodeExcluder() {
		     public void mark(TokenList tokensA,
				      TokenList tokensB,
				      boolean[] marksA,
				      boolean[] marksB)
		     {}
		 }
	     );
    }

    public SimpleTokenSimilarityChecker(int minimumMatchLength,
				  CodeExcluder codeExcluder) {
	if (codeExcluder == null) {
	    throw new IllegalArgumentException("Illegal code excluder: "+codeExcluder);
	}
	this.minimumMatchLength = minimumMatchLength;
	this.codeExcluder = codeExcluder;
    }


    /**
     * Algorithm from [5], simple, straigthforward and unoptimized implementation
     */
    public void countSimilarities(TokenList tokensA, 
				  TokenList tokensB) {
	this.tokensA = tokensA;
	this.tokensB = tokensB;
	tiles = new MatchedTileSet();
	MatchedTileSet matches = null;
	int maxMatch;

	int[] A = tokensA.getValueArray();
	int[] B = tokensB.getValueArray();

	boolean[] marksA = new boolean[A.length];
	boolean[] marksB = new boolean[B.length];

	for (int i=0; i < marksA.length; i++) {
	    marksA[i] = false;
	}
	
	for (int i=0; i < marksB.length; i++) {
	    marksB[i] = false;
	}

	// Run the code excluder
	this.codeExcluder.mark(tokensA, tokensB,
			       marksA, marksB);

	// The current tile id, will be increased when tiles are added to the final set
	int curId = 0;
	do {
	    maxMatch = minimumMatchLength;
	    matches = new MatchedTileSet();
	
	    for (int a = 0; a < A.length; a++) {
		if (!marksA[a]) {
		    for (int b = 0; b < B.length; b++) {
			if (!marksB[b]) {
			    int j = 0;
			    while (((a+j) < A.length) && 
				   ((b+j) < B.length) &&
				   (A[a+j] == B[b+j]) &&
				   (!marksA[a+j]) &&
				   (!marksB[b+j])) {
				j++;
			    }
			    if (j == maxMatch) {
				Debug.println(this,"Found maxLength tile of length "+j+" at A:"+a+" B:"+b);
				Tile tileA = new Tile(tokensA, a, a+j-1);
				Tile tileB = new Tile(tokensB, b, b+j-1);
				MatchedTile mt = 
				    new MatchedTile(tileA, tileB);
				boolean check = matches.addMatchedTileNoOverlap(mt);
				Debug.println(this,"Was tile added to matches: "+check);
			    }
			    else if (j > maxMatch) {
				Debug.println(this, "Found tile of length "+j+" at A:"+a+" B:"+b);
				matches = new MatchedTileSet();
				Tile tileA = new Tile(tokensA, a, a+j-1);
				Tile tileB = new Tile(tokensB, b, b+j-1);
				MatchedTile mt = 
				    new MatchedTile(tileA, tileB);
				boolean check = matches.addMatchedTileNoOverlap(mt);
			        Debug.println(this, "Was tile added to matches: "+check);
				maxMatch = j;
				Debug.println(this, "Setting maxMatch to "+j);
			    }
			}
		    }
		}
	    }
	    Iterator matchedTileIter = matches.getMatchedTiles();
	    while (matchedTileIter.hasNext()) {
		MatchedTile mt = (MatchedTile)matchedTileIter.next();
		Debug.println(this, "Marking tiles of length "+maxMatch+" A:"+mt.getTileA().getStartTokenIndex()+", B:"+mt.getTileB().getStartTokenIndex()+".");
		for (int j = 0; j < maxMatch; j++) {
		    marksA[mt.getTileA().getStartTokenIndex() + j] = true;
		    marksB[mt.getTileB().getStartTokenIndex() + j] = true;
		}
		mt.setId(curId++);
		tiles.addMatchedTile(mt);
		Debug.println(this, "Adding tile "+mt);
	    }
	    
	} while (maxMatch > minimumMatchLength);
    }

    public double getSimilarityValueA() 
	throws IllegalStateException 
    {
	if (tokensA == null) {
	    throw new IllegalStateException("Similarity count not yet run.");
	}
	int cAllTokens = tokensA.size();
	if (cAllTokens == 0) {
	    // Empty file, avoid division by zero!
	    return 0.0;
	}
	int cMatchedTokens = 0;
	Iterator i = tiles.iterator();
	while (i.hasNext()) {
	    MatchedTile mt = (MatchedTile)i.next();
	    cMatchedTokens += mt.size();
	}
	
	return (double)cMatchedTokens / (double)cAllTokens;
    }
    
    public double getSimilarityValueB() 
	throws IllegalStateException 
    {
	if (tokensA == null) {
	    throw new IllegalStateException("Similarity count not yet run.");
	}
	int cAllTokens = tokensB.size();
	if (cAllTokens == 0) {
	    // Empty file, avoid division by zero!
	    return 0.0;
	}
	int cMatchedTokens = 0;
	Iterator i = tiles.iterator();
	while (i.hasNext()) {
	    MatchedTile mt = (MatchedTile)i.next();
	    cMatchedTokens += mt.size();
	}
	
	return (double)cMatchedTokens / (double)cAllTokens;
    }

    public MatchedTileSet getSimilarityTiles() 
	throws IllegalStateException 
    {
	if (tokensA == null) {
	    throw new IllegalStateException("Similarity count not yet run.");
	}
	return tiles;
    }

}

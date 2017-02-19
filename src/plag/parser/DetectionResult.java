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

import java.io.Serializable;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileNotFoundException;

import java.util.Iterator;
import java.util.ArrayList;

/**
 * A class for storing the results of similarity detection between two
 * source files.
 */
public class DetectionResult
    implements Serializable
{
    
    private TokenList tokensA = null;
    private TokenList tokensB = null;

    private double similarityA = -1.0;
    private double similarityB = -1.0;

    private MatchedTileSet matches = null;

    protected File fileA = null;
    protected File fileB = null;

    //    protected CodeTokenizer tokenizer = null;

    //    protected TokenSimilarityChecker checker = null;

    /**
     * Creates the token list of the given file.
     */
    protected TokenList createTokenList(File file,
					CodeTokenizer tokenizer)
	throws Exception 
    {
	Debug.println(this, "Creating token list of file: "+file.getPath());

	TokenList tokens = tokenizer.tokenize(file);

	tokens.finalize();
	return tokens;
    }

    /**
     * Creates the token lists etc. using the given checker.
     */
    protected void createData(TokenSimilarityChecker checker,
			      CodeTokenizer tokenizer) 
	throws Exception 
    {
	this.tokensA = createTokenList(fileA, tokenizer);
	this.tokensB = createTokenList(fileB, tokenizer);
	
	checker.countSimilarities(tokensA,
				  tokensB);

	this.similarityA = checker.getSimilarityValueA();
	Debug.println(this, "Getting file similarity value A:"+this.similarityA);
	this.similarityB = checker.getSimilarityValueB();
	this.matches = checker.getSimilarityTiles();
    }

    protected DetectionResult() {
    }

    /**
     * Creates a new detection result with a similarity checker, 
     * which is used to create all the remaining data.
     */
    public DetectionResult(File fileA,
			   File fileB,
			   TokenSimilarityChecker checker,
			   CodeTokenizer tokenizer)
	throws Exception
    {
	this.fileA = fileA;
	this.fileB = fileB;
	this.createData(checker, tokenizer);
    }

    /**
     * Creates a new detection result with precalculated information.
     */
    public DetectionResult(TokenList tokensA,
			   TokenList tokensB,
			   double similarityA,
			   double similarityB,
			   MatchedTileSet matches,
			   File fileA,
			   File fileB) {
	this.tokensA = tokensA;
	this.tokensB = tokensB;
	this.similarityA = similarityA;
	this.similarityB = similarityB;
	this.matches = matches;
	this.fileA = fileA;
	this.fileB = fileB;
    }

    
    public TokenList getTokensA() 
    {
	return this.tokensA;
    }

    public TokenList getTokensB() 
    {
	return this.tokensB;
    }

    public double getSimilarityA() 
	throws Exception 

    {
	return this.similarityA;
    }

    public double getSimilarityB() 
	throws Exception 

    {
	return this.similarityB;
    }

    public MatchedTileSet getMatches() 
	throws Exception 
    {
	return this.matches;
    }

    public File getFileA() {
	return this.fileA;
    }
    
    public File getFileB() {
	return this.fileB;
    }

    public BufferedReader getFileReaderA() 
	throws FileNotFoundException 
    {
	return new BufferedReader(new FileReader(fileA));
    }
    
    public BufferedReader getFileReaderB() 
	throws FileNotFoundException 
    {
	return new BufferedReader(new FileReader(fileB));
    }

    public ArrayList getTilesA() {
	this.matches.setOrdering(MatchedTileSet.ORDER_BY_A);
	ArrayList aTiles = new ArrayList();
	Iterator i = matches.iterator();
	
	while (i.hasNext()) {
	    MatchedTile mt = (MatchedTile)i.next();
	    aTiles.add(mt.getTileA());
	}
	return aTiles;
    }
	
    public ArrayList getTilesB() {
	this.matches.setOrdering(MatchedTileSet.ORDER_BY_B);
	ArrayList bTiles = new ArrayList();
	Iterator i = matches.iterator();
	
	while (i.hasNext()) {
	    MatchedTile mt = (MatchedTile)i.next();
	    bTiles.add(mt.getTileB());
	}
	return bTiles;
    }


}






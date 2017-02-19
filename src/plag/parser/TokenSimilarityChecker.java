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

/**
 * Represents a similarity checker of two source code files. The idea
 * is, that similarities first need to be calculated using
 * countSimilarityValues, then they can be displayed using the other
 * methods.
 */
public interface TokenSimilarityChecker
    extends SimilarityChecker
{
    /**
     * Counts the similarity of the given tokens.
     */
    public void countSimilarities(TokenList tokensA,
				  TokenList tokensB);
    
    /**
     * Returns the similarity value of how much of the source code
     * file A is contained within the source code file B.
     */
    public double getSimilarityValueA();

    /**
     * Returns the similarity value of how much of the source code
     * file B is contained within the source code file A.
     */
    public double getSimilarityValueB();

    /**
     * Returns a set of all the matched tiles in the given two source
     * code files.
     */
    public MatchedTileSet getSimilarityTiles();

}

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


/**
 * Represents a tile in a token list. A tile defines a consecutive
 * list of tokens within a token list.
 */
public class Tile 
    implements Serializable
{

    /** The token, from which this tile starts */
    private int startTokenIndex;

    /** The token, at which this tile ends */
    private int endTokenIndex;

    /** The token list associated to this tile */
    private TokenList tokenList;

    /**
     * Creates a new tile.
     *
     * @param tokenList The token list associated to this tile
     * @param startTokenIndex The token index, from which this tile starts
     * @param endTokenIndex The token index, at which this tile ends
     */
    public Tile(TokenList tokenList, int startTokenIndex, int endTokenIndex) {
	this.tokenList = tokenList;
	this.startTokenIndex = startTokenIndex;
	this.endTokenIndex = endTokenIndex;
    }

    /**
     * Returns the length of this tile in tokens.
     */
    public int getLength() {
	return endTokenIndex - startTokenIndex + 1; 
    }

    /**
     * Returns the index of the start token.
     */
    public int getStartTokenIndex() {
	return startTokenIndex;
    }
    
    /**
     * Returns the index of the last token.
     */
    public int getEndTokenIndex() {
	return endTokenIndex;
    }
    

    public TokenList getTokenList() {
	return this.tokenList;
    }

    /** 
     * Returns the start token.
     */
    public Token getStartToken() {
	return tokenList.getToken(startTokenIndex);
    }

    /**
     * Returns the end token.
     */
    public Token getEndToken() {
	return tokenList.getToken(endTokenIndex);
    }

    /**
     * Checks, whether this tile overlaps with another one. Tiles
     * overlap, if their token indices overlap even with one
     * token. Does not check, whether the compared tiles are from the
     * same token list.
     *
     * @return true, if the tiles overlap; false otherwise
     */
    boolean overlaps(Tile t2) {

	return !((this.startTokenIndex > t2.getEndTokenIndex()) || (this.endTokenIndex < t2.getStartTokenIndex()));
    }
}

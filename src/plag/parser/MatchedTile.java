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
 * Represents a pair of matching tiles. The matching tiles are
 * comparable according to their length.
 *
 * @see plag.parser.Tile
 */
public class MatchedTile
    implements Serializable, Comparable
{

    /** The tile from token list A. */
    private Tile tileA;
    /** The tile from token list B. */
    private Tile tileB;

    /** The default tile id */
    private static final int DEFAULT_ID = -1;

    /** The id number of this tile */
    private int id;

    /**
     * Generates a new match between the given tiles.
     *
     * @param tileA The tile from token list A
     * @param tileB The tile from token list B
     * @param id The id of this tile
     */
    public MatchedTile(Tile tileA, Tile tileB, int id) {
	if (tileA.getLength() != tileB.getLength()) {
	    throw new IllegalArgumentException("Tile lengths do not match.");
	}
	this.tileA = tileA;
	this.tileB = tileB;
	this.id = id;
    }

    /** 
     * Generates a new match between the given tiles with default id.
     *
     * @param tileA The tile from token list A
     * @param tileB The tile from token list B
     */
    public MatchedTile(Tile tileA, Tile tileB) {
	this(tileA, tileB, DEFAULT_ID);
    }

    /**
     * Returns the id of this tile.
     */
    public int getId() {
	return this.id;
    }

    /**
     * Sets the id of this tile.
     */
    public void setId(int id) {
	this.id = id;
    }
    
    /**
     * Checks, whether this matched tile overlaps with another
     * one. Matched tiles overlap, if the sections they represent
     * overlap in either tile list A or tile list B.
     * 
     * @return true, if the matche tiles overlap; false otherwise
     */
    public boolean overlaps(MatchedTile mt2) {
	return ((this.getTileA().overlaps(mt2.getTileA())) ||
		(this.getTileB().overlaps(mt2.getTileB())));
    }

    /**
     * Returns the length of this matched tile in number of tokens.
     */
    public int getLength() {
	return this.size();
    }

    /**
     * Returns the length of this matched tile in number of tokens.
     */
    public int size() {
	return this.tileA.getLength();
    }

    /**
     * Compares this tile to another one according to their lengths.
     */
    public int compareTo(Object o2) {
	MatchedTile mt2 = (MatchedTile)o2;
	
	if ((this.getLength() - mt2.getLength()) != 0)
	    return (this.getLength() - mt2.getLength());
	else 
	    return -1;
    }

    /**
     * Returns the tile A.
     */
    public Tile getTileA() {
	return this.tileA;
    }

    /**
     * Returns the tile B.
     */
    public Tile getTileB() {
	return this.tileB;
    }
    
    /**
     * Generates a string representation of this tile.
     */
    public String toString() {
	StringBuffer b = new StringBuffer();
	
	int startAIndex = tileA.getStartTokenIndex();
	int startBIndex = tileB.getStartTokenIndex();
	int endAIndex = tileA.getEndTokenIndex();
	int endBIndex = tileB.getEndTokenIndex();

	Token startA = tileA.getStartToken();
	Token startB = tileB.getStartToken();
	Token endA = tileA.getEndToken();
	Token endB = tileB.getEndToken();

	b.append(startAIndex+"["+
		 startA.getStartLine()+"("+
		 startA.getStartChar()+")]-"+
		 endAIndex+"["+
		 endA.getEndLine()+"("+
		 endA.getEndChar()+")]..."+
		 startBIndex+"["+
		 startB.getStartLine()+"("+
		 startB.getStartChar()+")]-"+
		 endBIndex+"["+
		 endB.getEndLine()+"("+
		 endB.getEndChar()+")]");
	return b.toString();
    }

}
    

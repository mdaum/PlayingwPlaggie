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
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Comparator;

/**
 * Represents a set of matched tiles. The matched tiles can be added
 * to the set with separate operation (@see #addMatchedTileNoOverlap),
 * if non-overlapping list of matched tiles is wanted. The tiles are
 * stored in order according to their length by default, but the order
 * can be change by using the provided method.
 * <p>
 * This is not a thread safe class!
 *
 */
public class MatchedTileSet
    implements Serializable
{

    /** The matched tiles */
    private TreeSet matchedTiles = new TreeSet(new OrderByLength());

    public static final int ORDER_BY_LENGTH = 0;
    public static final int ORDER_BY_A = 1;
    public static final int ORDER_BY_B = 2;

    /** The current order of tokens */
    private int currentOrder = ORDER_BY_LENGTH;

    /**
     * Orders the matches in the set according to the order of their
     * occurence in token list B.
     */
    private static class OrderByB 
	implements Comparator, Serializable 
    {
	public int compare(Object o1, Object o2) {
	    MatchedTile mt1 = (MatchedTile)o1;
	    MatchedTile mt2 = (MatchedTile)o2;
	    
	    Tile t1 = mt1.getTileB();
	    Tile t2 = mt2.getTileB();

	    int startTokenIndex1 = t1.getStartTokenIndex();
	    int startTokenIndex2 =
		t2.getStartTokenIndex();
	    int endTokenIndex1 = t1.getEndTokenIndex();
	    int endTokenIndex2 = t2.getEndTokenIndex();
	    
	    if (startTokenIndex1 < startTokenIndex2) {
		return -1;
	    }
	    else if (startTokenIndex1 > startTokenIndex2) {
		return 1;
	    }
	    else {
		// Same start tokens
		if (endTokenIndex1 < endTokenIndex2) {
		    return -1;
		}
		else if (endTokenIndex1 > endTokenIndex2) {
		    return 1;
		}
		else {
		    // Same start and end tokens
		    return -1;
		}
	    }
	}
	    
    }

    /**
     * Orders the matches in the set according to their length
     */
    private static class OrderByLength
	implements Comparator, Serializable
    {
	public int compare(Object o1, Object o2) {
	    MatchedTile mt1 = (MatchedTile)o1;
	    MatchedTile mt2 = (MatchedTile)o2;
	    
	    if (mt1.size() > mt2.size()) {
		return 1;
	    }
	    else {
		return -1;
	    }
	}
    }

    /**
     * Orders the matches in the set according to the order of their
     * occurence in token list A.
     */
    private static class OrderByA
	implements Comparator, Serializable 
    {
	public int compare(Object o1, Object o2) {
	    MatchedTile mt1 = (MatchedTile)o1;
	    MatchedTile mt2 = (MatchedTile)o2;
	    
	    Tile t1 = mt1.getTileA();
	    Tile t2 = mt2.getTileA();

	    int startTokenIndex1 = t1.getStartTokenIndex();
	    int startTokenIndex2 =
		t2.getStartTokenIndex();
	    int endTokenIndex1 = t1.getEndTokenIndex();
	    int endTokenIndex2 = t2.getEndTokenIndex();
	    
	    if (startTokenIndex1 < startTokenIndex2) {
		return -1;
	    }
	    else if (startTokenIndex1 > startTokenIndex2) {
		return 1;
	    }
	    else {
		// Same start tokens
		if (endTokenIndex1 < endTokenIndex2) {
		    return -1;
		}
		else if (endTokenIndex1 > endTokenIndex2) {
		    return 1;
		}
		else {
		    // Same start and end tokens
		    return -1;
		}
	    }
	}
    }


    public void setOrdering(int newOrder) {
	if (newOrder != this.currentOrder) {
	    Comparator comp = null;

	    switch (newOrder) {
	    case ORDER_BY_LENGTH:
		comp = new OrderByLength();
		break;
	    case ORDER_BY_A:
		comp = new OrderByA();
		break;
	    case ORDER_BY_B:
		comp = new OrderByB();
		break;
	    default:
		throw new IllegalArgumentException("Unknown ordering "+newOrder);
	    }

	    TreeSet newMatchedTiles = new TreeSet(comp);
	    Iterator i = this.matchedTiles.iterator();
	    
	    while (i.hasNext()) {
		newMatchedTiles.add(i.next());
	    }

	    this.matchedTiles = newMatchedTiles;
	    this.currentOrder = newOrder;
	}
    }

    /**
     * Adds a new matched tile to this set.
     */
    public void addMatchedTile(MatchedTile mt) {
	matchedTiles.add(mt);
    }

    /**
     * Adds a new matched tile into this set. Only adds mt, if it does
     * not overlap with any existing matched tiles.
     * 
     * @return true, if tile was added; false otherwise
     */
    public boolean addMatchedTileNoOverlap(MatchedTile newMt) {
	Iterator it = matchedTiles.iterator();

	while (it.hasNext()) {
	    MatchedTile mt = (MatchedTile)it.next();
	    if (newMt.overlaps(mt)) {
		Debug.println("Checking whether tiles ["+newMt+"] and ["+mt+"] overlap: true");
		return false;
	    }
	    Debug.println("Checking whether tiles ["+newMt+"] and ["+mt+"] overlap: false");
	}
	this.addMatchedTile(newMt);
	return true;
    }

    /**
     * Returns all the matched tiles, whose length is greater than or
     * equal to minLength 
     */
    public Iterator getMatchedTiles(int minLength) {
	Tile dummy = new Tile(null, 0, minLength-1);
	return matchedTiles.tailSet(new MatchedTile(dummy, dummy)).iterator();
    }

    /**
     * Returns all the matched tiles in this set.
     */
    public Iterator getMatchedTiles() {
	return this.iterator();
    }

    /**
     * Returns all the matched tiles in this set.
     */
    public Iterator iterator() {
	return matchedTiles.iterator();
    }

    /**
     * Returns the number of tiles in this set.
     */
    public int size() {
	return matchedTiles.size();
    }

    /**
     * Returns the total number of matched tokens in this set.
     */
    public int getTokenCount() {
	int c = 0;
	Iterator i = getMatchedTiles();

	while (i.hasNext()) {
	    c += ((MatchedTile)i.next()).size();
	}
	return c;
    }

    /**
     * Returns a string representation of this set.
     */
    public String toString() {
	StringBuffer b = new StringBuffer();

	Iterator i = getMatchedTiles();

	while (i.hasNext()) {
	    MatchedTile mt = (MatchedTile)i.next();
	    
	    b.append(mt.getId()+":"+mt.toString()+"\n");
	}
	
	return b.toString();
    }

}

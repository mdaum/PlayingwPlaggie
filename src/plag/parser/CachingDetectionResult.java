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
import java.util.HashMap;
import java.util.Map;

/**
 * A class for storing the results of similarity detection between two
 * source files. Caches the token lists of the files in a static hash table.
 */
public class CachingDetectionResult
    extends DetectionResult
{

    protected Map tokenListCache;

    /**
     * Creates the token list of the given file, uses the cache, if possible.
     */
    protected TokenList createTokenList(File file,
					CodeTokenizer tokenizer) 
	throws Exception 
    
    {
	Object retList;
	if ( (retList = tokenListCache.get(file)) != null) {
	    if (retList instanceof Exception) {
		// Get the stored exception and rethrow it.
		throw (Exception)retList;
	    }
	    //	    System.out.println("Getting token list from cache");
	    return (TokenList)retList;
	}
	TokenList tokens = null;
	try {
	    Debug.println(this, "Creating token list of file: "+file.getPath());
	    
//  	    PlagSym.init(file.getPath());
	    
//  	    Reader fr = new BufferedReader(new FileReader(file));
//  	    Lexer l = new Lexer(fr, 4);
//  	    java_cup.runtime.lr_parser g = new Grm14(l);
//  	    g.parse();
	    
//  	    tokens = PlagSym.getTokenList();
	    
	    tokens = tokenizer.tokenize(file);

	    //	    fr.close();
	}
	catch (Exception e) {
	    // Store the exception and rethrow it
	    tokenListCache.put(file, e);
	    Stats.incCounter("parse_failures");
	    throw e;
	}
//  	catch (Error e2) {
//  	    //	    System.out.println("Error reading file:"+ file.getPath());
//  	    Exception e = new Exception("Error reading file:"+ file.getPath());
//  	    tokenListCache.put(file, e);
//  	    Stats.incCounter("parse_failures");
//  	    throw e;
//  	}
	tokenListCache.put(file, tokens);
	Stats.incCounter("parsed_files");
	return tokens;
    }

    public CachingDetectionResult(File fileA,
				  File fileB,
				  TokenSimilarityChecker checker,
				  CodeTokenizer tokenizer,
				  Map tokenListCache) 
	throws Exception
    {
	this.fileA = fileA;
	this.fileB = fileB;
	this.tokenListCache = tokenListCache;

	this.createData(checker, tokenizer);
    }

}




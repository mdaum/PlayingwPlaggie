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

import plag.parser.CodeTokenizer;
import plag.parser.TokenList;

import plag.parser.java.lex.Lexer;

import java.io.*;

/**
 * A tokenizer for Java program code files.
 *
 */
public class JavaTokenizer
    implements CodeTokenizer
{

    public JavaTokenizer()
    {
    }

    /**
     * Returns a token list of the given Java source code file.
     */
    public TokenList tokenize(File file)
	throws Exception
    {
		PlagSym.init(file.getPath());
		
		Reader fr = new BufferedReader(new FileReader(file));
		Lexer l = new Lexer(fr, 5);
		java_cup.runtime.lr_parser g = new Grm15(l);
		g.parse();
	
		TokenList tokens = PlagSym.getTokenList();
	
		fr.close();
	
		return tokens;
    }

    /**
     * Returns the descriptive name of a token with the given value.
     */
    public String getValueString(int value)
    {
	return PlagSym.getValueString(value);
    }


}


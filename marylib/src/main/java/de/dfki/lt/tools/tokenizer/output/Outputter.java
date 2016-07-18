/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2014  DFKI Language Technology Lab http://www.dfki.de/lt
 *   Author: Joerg Steffen, steffen@dfki.de
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.dfki.lt.tools.tokenizer.output;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;

import de.dfki.lt.tools.tokenizer.JTok;
import de.dfki.lt.tools.tokenizer.annotate.AnnotatedString;

/**
 * {@link Outputter} provides static methods that convert an {@link AnnotatedString} into a list of
 * nested representation of {@link Paragraph}s with {@link TextUnit}s and {@link Token}s.
 *
 * @author Joerg Steffen, DFKI
 */
public final class Outputter {

    // would create a new instance of {@link Outputter}; not to be used
    private Outputter() {

        // private constructor to enforce noninstantiability
    }


    /**
     * Creates a list of {@link Paragraph}s with {@link TextUnit}s and {@link Token}s from the given
     * annotated string.
     *
     * @param input the annotated string
     * @return a list of paragraphs
     */
    public static List<Paragraph> createParagraphs(AnnotatedString input) {

        // init lists for paragraphs, text units and tokens
        List<Paragraph> paraList = new ArrayList<Paragraph>();
        List<TextUnit> tuList = new ArrayList<TextUnit>();
        List<Token> tokenList = new ArrayList<Token>();

        // iterate over tokens and create token instances
        char c = input.setIndex(0);
        while (c != CharacterIterator.DONE) {

            int tokenStart = input.getRunStart(JTok.CLASS_ANNO);
            int tokenEnd = input.getRunLimit(JTok.CLASS_ANNO);
            // check if c belongs to a token
            String type = (String) input.getAnnotation(JTok.CLASS_ANNO);
            if (null != type) {
                // create new token instance
                Token tok =
                        new Token(tokenStart,
                                tokenEnd,
                                type,
                                input.substring(tokenStart, tokenEnd));

                // check if token is first token of a paragraph or text unit
                if (null != input.getAnnotation(JTok.BORDER_ANNO)) {
                    // add current text unit to paragraph and create new one
                    tuList.add(new TextUnit(tokenList));
                    tokenList = new ArrayList<Token>();
                }

                // check if token is first token of a paragraph
                if (input.getAnnotation(JTok.BORDER_ANNO) == JTok.P_BORDER) {
                    // add current paragraph to result list and create new one
                    paraList.add(new Paragraph(tuList));
                    tuList = new ArrayList<TextUnit>();
                }

                // add token to token list
                tokenList.add(tok);
            }
            // set iterator to next token
            c = input.setIndex(tokenEnd);
        }
        // add last text unit
        tuList.add(new TextUnit(tokenList));

        // add last paragraph
        paraList.add(new Paragraph(tuList));

        // return paragraph list
        return paraList;
    }


    /**
     * Creates a list of {@link Token}s from the given annotated string. Text units and paragraphs are
     * ignored.
     *
     * @param input the annotated string
     * @return a list of tokens
     */
    public static List<Token> createTokens(AnnotatedString input) {

        // init list for tokens
        List<Token> tokenList = new ArrayList<Token>();

        // iterate over tokens and create token instances
        char c = input.setIndex(0);
        while (c != CharacterIterator.DONE) {

            int tokenStart = input.getRunStart(JTok.CLASS_ANNO);
            int tokenEnd = input.getRunLimit(JTok.CLASS_ANNO);
            // check if c belongs to a token
            String type = (String) input.getAnnotation(JTok.CLASS_ANNO);
            if (null != type) {
                // create new token instance
                Token tok =
                        new Token(tokenStart,
                                tokenEnd,
                                type,
                                input.substring(tokenStart, tokenEnd));

                // add token to token list
                tokenList.add(tok);
            }
            // set iterator to next token
            c = input.setIndex(tokenEnd);
        }

        // return paragraph list
        return tokenList;
    }
}

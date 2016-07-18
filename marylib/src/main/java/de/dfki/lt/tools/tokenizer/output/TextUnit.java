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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a text unit with its tokens.
 *
 * @author Joerg Steffen, DFKI
 */
public class TextUnit {

    // start index of the text unit
    private int startIndex;

    // end index of the text unit
    private int endIndex;

    // list with the tokens of the text unit
    private List<Token> tokens;


    /**
     * Creates a new instance of {@link TextUnit}.
     */
    public TextUnit() {

        this.setStartIndex(0);
        this.setEndIndex(0);
        this.setTokens(new ArrayList<Token>());
    }


    /**
     * Creates a new instance of {@link TextUnit} containing the given tokens.
     *
     * @param tokens a list of tokens
     */
    public TextUnit(List<Token> tokens) {

        this.setTokens(tokens);
    }


    /**
     * @return the start index
     */
    public int getStartIndex() {

        return this.startIndex;
    }


    /**
     * @param startIndex the start index to set
     */
    public void setStartIndex(int startIndex) {

        this.startIndex = startIndex;
    }


    /**
     * @return the end index
     */
    public int getEndIndex() {

        return this.endIndex;
    }


    /**
     * @param endIndex the end index to set
     */
    public void setEndIndex(int endIndex) {

        this.endIndex = endIndex;
    }


    /**
     * @return the token list
     */
    public List<Token> getTokens() {

        return this.tokens;
    }


    /**
     * Sets the tokens of the text unit to the given parameter. As a side effect, it adjusts the start
     * index and end index of the text unit to the start index of the first token and the end index of
     * the last token.
     *
     * @param tokens a list of tokens
     */
    public void setTokens(List<Token> tokens) {

        this.tokens = tokens;
        if (tokens.size() > 0) {
            this.setStartIndex(tokens.get(0).getStartIndex());
            this.setEndIndex(tokens.get(tokens.size() - 1).getEndIndex());
        } else {
            this.setStartIndex(0);
            this.setEndIndex(0);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder(
                String.format("  Text Unit Start: %d%n  Text Unit End: %d%n",
                        this.getStartIndex(),
                        this.getEndIndex()));

        // add tokens
        for (Token oneToken : this.getTokens()) {
            result.append(oneToken.toString());
        }

        return result.toString();
    }
}

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

package de.dfki.lt.tools.tokenizer.regexp;

/**
 * Holds the result of matching an input string with a regular expression.
 *
 * @author Joerg Steffen, DFKI
 */
public class Match {

    // index within the input text where the match in its entirety began
    private int startIndex;

    // index within the input string where the match in its entirety ends;
    // the return value is the next position after the end of the string
    private int endIndex;

    // string matching the regular expression pattern
    private String image;


    /**
     * Creates a new instance of {@link Match} using the given parameters.
     *
     * @param startIndex the start index
     * @param endIndex   the end index
     * @param image      the match
     */
    public Match(int startIndex, int endIndex, String image) {

        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.image = image;
    }


    /**
     * @return the index within the input text where the match in its entirety began
     */
    public int getStartIndex() {

        return this.startIndex;
    }


    /**
     * @return the index within the input string where the match in its entirety ends; the return
     * value is the next position after the end of the string
     */
    public int getEndIndex() {

        return this.endIndex;
    }


    /**
     * @return the string matching the regular expression pattern
     */
    public String getImage() {

        return this.image;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format(
                "%d - %d: %s", this.startIndex, this.endIndex, this.image);
    }
}

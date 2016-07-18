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

import de.dfki.lt.tools.tokenizer.PunctDescription;

/**
 * Represents a token with its type and surface image.
 *
 * @author Joerg Steffen, DFKI
 */
public class Token {

    // the Penn Treebank replacements for brackets:
    private static final String LRB = "-LRB-";
    private static final String RRB = "-RRB-";
    private static final String LSB = "-LSB-";
    private static final String RSB = "-RSB-";
    private static final String LCB = "-LCB-";
    private static final String RCB = "-RCB-";

    // start index of the token
    private int startIndex;

    // end index of the token
    private int endIndex;

    // type of the token
    private String type;

    // surface image of the token
    private String image;


    /**
     * Creates a new instance of {@link Token}.
     */
    public Token() {

        this.setStartIndex(0);
        this.setEndIndex(0);
        this.setType(new String());
        this.setImage(new String());
    }


    /**
     * Creates a new instance of {@link Token} for the given start index, end index, type and surface
     * image.
     *
     * @param startIndex the start index
     * @param endIndex   the end index
     * @param type       the type
     * @param image      the surface image
     */
    public Token(int startIndex, int endIndex, String type, String image) {

        this.setStartIndex(startIndex);
        this.setEndIndex(endIndex);
        this.setType(type);
        this.setImage(image);
    }

    /**
     * This applies some replacements used in the Penn Treebank format to the given token image of the
     * given type.
     *
     * @param image the token image
     * @param type  the type
     * @return a modified string or {@code null} if no replacement took place
     */
    public static String applyPtbFormat(String image, String type) {

        String result = null;

        if (type.equals(PunctDescription.OPEN_BRACKET)) {

            if (image.equals("(")) {
                result = LRB;
            } else if (image.equals("[")) {
                result = LSB;
            } else if (image.equals("{")) {
                result = LCB;
            }
        } else if (type.equals(PunctDescription.CLOSE_BRACKET)) {

            if (image.equals(")")) {
                result = RRB;
            } else if (image.equals("]")) {
                result = RSB;
            } else if (image.equals("}")) {
                result = RCB;
            }
        } else if (type.equals(PunctDescription.OPEN_PUNCT)) {
            result = "``";
        } else if (type.equals(PunctDescription.CLOSE_PUNCT)) {
            result = "''";
        } else if (image.contains("/")) {
            result = image.replace("/", "\\/");
        } else if (image.contains("*")) {
            result = image.replace("*", "\\*");
        }

        return result;
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
     * @return the token type
     */
    public String getType() {

        return this.type;
    }

    /**
     * @param type the token type to set
     */
    public void setType(String type) {

        this.type = type;
    }

    /**
     * @return the surface image
     */
    public String getImage() {

        return this.image;
    }

    /**
     * @param image the surface image to set
     */
    public void setImage(String image) {

        this.image = image;
    }

    /**
     * Returns the Penn Treebank surface image of the token if a Penn Treebank replacement took place,
     * {@code null} otherwise.
     *
     * @return the surface image as the result of the Penn Treebank token replacement or {@code null}
     */
    public String getPtbImage() {

        return applyPtbFormat(this.image, this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder(
                String.format("    Token: %-15s\tType: %s\tStart: %s\tEnd: %s",
                        String.format("\"%s\"", this.getImage()),
                        this.getType(),
                        this.getStartIndex(),
                        this.getEndIndex()));

        String ptbImage = applyPtbFormat(this.image, this.type);
        if (null != ptbImage) {
            result.append(String.format("\tPTB: \"%s\"", ptbImage));
        }
        result.append(String.format("%n"));

        return result.toString();
    }
}

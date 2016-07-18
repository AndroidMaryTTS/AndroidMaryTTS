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
 * Represents a paragraph with its text units.
 *
 * @author Joerg Steffen, DFKI
 */
public class Paragraph {

    // start index of the paragraph
    private int startIndex;

    // the end index of the paragraph
    private int endIndex;

    // list with the text units of the paragraph
    private List<TextUnit> textUnits;


    /**
     * Creates a new instance of {@link Paragraph}.
     */
    public Paragraph() {

        this.setStartIndex(0);
        this.setEndIndex(0);
        this.setTextUnits(new ArrayList<TextUnit>());
    }


    /**
     * Creates a new instance of {@link Paragraph} that contains the given text units.
     *
     * @param textUnits a list of text units
     */
    public Paragraph(List<TextUnit> textUnits) {

        this.setTextUnits(textUnits);
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
     * @return the list with the text units
     */
    public List<TextUnit> getTextUnits() {

        return this.textUnits;
    }


    /**
     * Sets the text units of the paragraph to the given parameter. As a side effect, it adjusts the
     * start index and end index of the paragraph to the start index of the first text unit and the
     * end index of the last text unit.
     *
     * @param textUnits a list of text units
     */
    public void setTextUnits(List<TextUnit> textUnits) {

        this.textUnits = textUnits;
        if (textUnits.size() > 0) {
            this.setStartIndex(textUnits.get(0).getStartIndex());
            this.setEndIndex(textUnits.get(textUnits.size() - 1).getEndIndex());
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
                String.format("Paragraph Start: %d%nParagraph End: %d%n",
                        this.getStartIndex(),
                        this.getEndIndex()));

        // add text units
        for (TextUnit oneTu : this.getTextUnits()) {
            result.append(oneTu.toString());
        }

        return result.toString();
    }
}

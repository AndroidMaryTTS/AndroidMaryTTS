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

package de.dfki.lt.tools.tokenizer.annotate;

import java.util.HashMap;
import java.util.Map;

import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;

/**
 * {@link FastAnnotatedString} is a fast implementation of the {@link AnnotatedString} interface. It
 * reserves an array of objects and an array of booleans for each newly introduced annotation key.
 * This provides fast access at the cost of memory. So only introduce new annotation keys if
 * necessary.
 *
 * @author Joerg Steffen, DFKI
 */
public class FastAnnotatedString implements AnnotatedString {

    // current index within the string
    private int index;

    // index position at the end of the string
    private int endIndex;

    // content of the string as a character array
    private char[] content;

    // map of annotation keys to arrays of objects holding the annotation values;
    // the object at a certain index in the array is the annotation value of the corresponding
    // character in the annotated string
    private Map<String, Object> annotations;

    // map of annotation keys to arrays of booleans holding annotation borders
    private Map<String, boolean[]> borders;

    // last annotation key used
    private String currentKey;

    // last value array used
    private Object[] currentValues;

    // last border array used
    private boolean[] currentBorders;


    /**
     * Creates a new instance of {@link FastAnnotatedString} for the given input text.
     *
     * @param inputText the text to annotate
     */
    public FastAnnotatedString(String inputText) {

        // check if there is a string
        if (inputText == null) {
            throw new NullPointerException("null as input string is not allowed");
        }
        // initialization
        this.endIndex = inputText.length();
        this.content = inputText.toCharArray();
        this.annotations = new HashMap<>(5);
        this.borders = new HashMap<>(5);
        this.currentKey = null;
        this.currentBorders = null;
        this.currentValues = null;
        this.index = 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char first() {

        this.index = 0;
        return current();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char last() {

        if (0 != this.endIndex) {
            this.index = this.endIndex - 1;
        } else {
            this.index = this.endIndex;
        }
        return current();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char current() {

        if ((this.index >= 0) && (this.index < this.endIndex)) {
            return this.content[this.index];
        }
        return DONE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char next() {

        if (this.index < (this.endIndex - 1)) {
            this.index++;
            return this.content[this.index];
        }
        this.index = this.endIndex;
        return DONE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char previous() {

        if (this.index > 0) {
            this.index--;
            return this.content[this.index];
        }
        return DONE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getBeginIndex() {

        return 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getEndIndex() {

        return this.endIndex;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {

        return this.index;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char setIndex(int position) {

        if ((position < 0) || (position > this.endIndex)) {
            throw new IllegalArgumentException(String.format("Invalid index %d", position));
        }
        this.index = position;
        return current();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {

        try {
            FastAnnotatedString other = (FastAnnotatedString) super.clone();
            return other;
        } catch (CloneNotSupportedException cnse) {
            throw new ProcessingException(cnse.getLocalizedMessage(), cnse);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public char charAt(int charIndex) {

        if ((charIndex < 0) || (charIndex > this.endIndex)) {
            throw new IllegalArgumentException(String.format("Invalid index %d", charIndex));
        }
        if ((charIndex >= 0) && (charIndex < this.endIndex)) {
            return this.content[charIndex];
        }
        return DONE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String substring(int start, int end) {

        if ((start < 0)
                || (end > this.endIndex)
                || (start > end)) {
            throw new IllegalArgumentException(
                    String.format("Invalid substring range %d - %d", start, end));
        }
        return new String(this.content, start, end - start);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void annotate(String key, Object value, int start, int end) {

        // check if range is legal
        if ((start < 0)
                || (end > this.endIndex)
                || (start >= end)) {
            throw new IllegalArgumentException(
                    String.format("Invalid substring range %d - %d", start, end));
        }

        if (!key.equals(this.currentKey)) {
            // update currents
            Object probe = this.annotations.get(key);
            if (null == probe) {
                // create new arrays for this key
                this.currentValues = new Object[this.endIndex];
                this.currentBorders = new boolean[this.endIndex];
                this.currentKey = key;
                // if string is not empty, the first character is already a border
                if (this.endIndex > 0) {
                    this.currentBorders[0] = true;
                }
                // store arrays
                this.annotations.put(key, this.currentValues);
                this.borders.put(key, this.currentBorders);
            } else {
                this.currentValues = (Object[]) probe;
                this.currentBorders = this.borders.get(key);
                this.currentKey = key;
            }
        }

        // annotate
        for (int i = start; i < end; i++) {
            this.currentValues[i] = value;
            this.currentBorders[i] = false;
        }
        // set border for current annotation and the implicit next annotation (if there is one)
        this.currentBorders[start] = true;
        if (end < this.endIndex) {
            this.currentBorders[end] = true;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAnnotation(String key) {

        if ((this.index >= 0) && (this.index < this.endIndex)) {
            if (!key.equals(this.currentKey)) {
                // update currents
                Object probe = this.annotations.get(key);
                if (null != probe) {
                    this.currentKey = key;
                    this.currentValues = (Object[]) probe;
                    this.currentBorders = this.borders.get(key);
                } else {
                    return null;
                }
            }

            // get annotation value
            return this.currentValues[this.index];
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getRunStart(String key) {

        if (!key.equals(this.currentKey)) {
            // update currents
            Object probe = this.borders.get(key);
            if (null != probe) {
                this.currentKey = key;
                this.currentValues = (Object[]) this.annotations.get(key);
                this.currentBorders = (boolean[]) probe;
            } else {
                return 0;
            }
        }
        // search border
        for (int i = this.index; i >= 0; i--) {
            if (this.currentBorders[i]) {
                return i;
            }
        }
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getRunLimit(String key) {

        if (!key.equals(this.currentKey)) {
            // update currents
            Object probe = this.borders.get(key);
            if (null != probe) {
                this.currentKey = key;
                this.currentValues = (Object[]) this.annotations.get(key);
                this.currentBorders = (boolean[]) probe;
            } else {
                return this.endIndex;
            }
        }
        // search border
        for (int i = this.index + 1; i < this.endIndex; i++) {
            if (this.currentBorders[i]) {
                return i;
            }
        }
        return this.endIndex;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int findNextAnnotation(String key) {

        if (!key.equals(this.currentKey)) {
            // update currents
            Object probe = this.annotations.get(key);
            if (null != probe) {
                this.currentKey = key;
                this.currentValues = (Object[]) probe;
                this.currentBorders = this.borders.get(key);
            } else {
                return this.endIndex;
            }
        }

        // search next annotation
        int i;
        for (i = this.index + 1; i < this.endIndex; i++) {
            if (this.currentBorders[i]) {
                for (int j = i; j < this.endIndex; j++) {
                    if (null != this.currentValues[j]) {
                        return j;
                    }
                }
                return this.endIndex;
            }
        }
        return this.endIndex;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(String key) {

        // init result
        StringBuffer result = new StringBuffer();
        // make a backup of the current index
        int bakupIndex = this.index;
        // iterate over string
        this.index = 0;
        while (this.index < this.endIndex) {
            int endAnno = this.getRunLimit(key);
            if (null != getAnnotation(key)) {
                result.append(substring(this.index, endAnno) + "\t"
                        + this.index + "-" + endAnno + "\t"
                        + getAnnotation(key)
                        + System.getProperty("line.separator"));
            }
            this.index = endAnno;
        }
        // restore index
        this.index = bakupIndex;
        // return result
        return result.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return new String(this.content);
    }
}

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

import java.text.CharacterIterator;

/**
 * {@link AnnotatedString} is an interface for annotating strings and working on them. It merges the
 * functionality of {@link java.text.AttributedCharacterIterator} and
 * {@link java.text.AttributedString}.
 * <p/>
 * An annotated string allows iteration through both text and related annotation information. An
 * annotation is a key/value pair, identified by the key. No two annotations on a given character
 * can have the same key.
 * <p/>
 * A run with respect to an annotation is a maximum text range for which:
 * <ul>
 * <li>the annotation is undefined or null for the entire range, or
 * <li>the annotation value is defined and has the same non-null value for the entire range
 * </ul>
 *
 * @author Joerg Steffen, DFKI
 */
public interface AnnotatedString extends CharacterIterator {

    /**
     * Returns the index of the first character of the run with respect to the given annotation key
     * containing the current character.
     *
     * @param key the annotation key
     * @return the index
     */
    int getRunStart(String key);


    /**
     * Returns the index of the first character following the run with respect to the given annotation
     * key containing the current character.
     *
     * @param key the annotation key
     * @return the index
     */
    int getRunLimit(String key);


    /**
     * Adds an annotation to a subrange of the string.
     *
     * @param key        the annotation key
     * @param value      the annotation value
     * @param beginIndex the index of the first character of the range
     * @param endIndex   the index of the character following the last character of the range
     * @throws IllegalArgumentException if beginIndex is less then 0, endIndex is greater than the length of the string,
     *                                  or beginIndex and endIndex together don't define a non-empty subrange of the
     *                                  string
     */
    void annotate(String key, Object value, int beginIndex, int endIndex);


    /**
     * Returns the annotation value of the string at the current index for the given annotation key.
     *
     * @param key the annotation key
     * @return the annotation value or {@code null} if there is no annotation with the given key at
     * that position
     */
    Object getAnnotation(String key);


    /**
     * Returns the index of the first character annotated with the given annotation key following the
     * run containing the current character with respect to the given annotation key.
     *
     * @param key the annotation key
     * @return the index
     */
    int findNextAnnotation(String key);


    /**
     * Returns the substring between the given indices.
     *
     * @param startIndex the index of the first character of the range
     * @param endIndex   the index of the character following the last character of the range
     * @return the substring
     * @throws IllegalArgumentException if beginIndex is less then 0, endIndex is greater than the length of the string,
     *                                  or beginIndex and endIndex together don't define a non-empty subrange of the
     *                                  string
     */
    String substring(int startIndex, int endIndex);


    /**
     * Returns the character from the given position without changing the index.
     *
     * @param charIndex the index within the text; valid values range from {@link #getBeginIndex()} to
     *                  {@link #getEndIndex()}; an IllegalArgumentException is thrown if an invalid value is
     *                  supplied
     * @return the character at the specified position or {@link #DONE} if the specified position is
     * equal to {@link #getEndIndex()}
     */
    char charAt(int charIndex);


    /**
     * Returns a string representation of the annotated string with the annotation for the given
     * annotation key.
     *
     * @param key the annotation key
     * @return the string representation
     */
    String toString(String key);


    /**
     * Returns the surface string of the annotated string.
     *
     * @return the surface string
     */
    @Override
    String toString();
}

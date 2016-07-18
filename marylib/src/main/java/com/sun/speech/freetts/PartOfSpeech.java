/**
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;

/**
 * Determines the part of speech of a word.
 */
public interface PartOfSpeech {
    /**
     * Returns a description of the part of speech given a word.
     * The string is implementation dependent.
     *
     * @param word the word to classify
     * @return an implementation dependent part of speech for the word
     */
    String getPartOfSpeech(String word);
}


  

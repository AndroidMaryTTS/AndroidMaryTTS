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
package com.sun.speech.freetts.lexicon;

/**
 * Provides the phone list for words using letter-to-sound rules.  The
 * phone list is implementation dependent.
 */
public interface LetterToSound {
    /**
     * Calculate the phone list for a given word.  If a phone list
     * cannot be determined, <code>null</code> is returned.  The phone
     * list is implementation dependent.  The format of the
     * <code>partOfSpeech</code> is also implementation.  If the
     * <code>partOfSpeech</code> does not matter, pass in <code>null</code>.
     *
     * @param word         the word to get the phone list for
     * @param partOfSpeech the part of speech or <code>null</code>
     * @return the list of phones for word or <code>null</code>
     */
    String[] getPhones(String word, String partOfSpeech);
}


  

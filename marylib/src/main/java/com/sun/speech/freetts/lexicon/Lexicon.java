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

import java.io.IOException;
import java.util.List;

/**
 * Provides the phone list for words.  A Lexicon is composed of three
 * pieces:  an addenda, the compiled form, and the letter to sound
 * rules.
 * <ul>
 * <li>The addenda either contains Word instances that are not in
 * the compiled form, or it contains Word instances that replace
 * definitions in the compiled form.  The addenda is meant to be
 * relatively small (e.g., 10's of words).
 * <li>The compiled form is meant to hold a large number of words
 * (e.g., 10's of thousands of words) and provide a very efficient
 * means for finding those words.
 * <li>The letter to sound rules will attempt to find a definition for
 * a word not found in either the addenda or compiled form.
 * </ul>
 */
public interface Lexicon {
    /**
     * Gets the phone list for a given word.  If a phone list cannot
     * be found, <code>null</code> is returned.  The
     * <code>partOfSpeech</code> is implementation dependent, but
     * <code>null</code> always matches.
     *
     * @param word         the word to find
     * @param partOfSpeech the part of speech or <code>null</code>
     * @return the list of phones for word or null
     */
    String[] getPhones(String word, String partOfSpeech);

    /**
     * Gets the phone list for a given word.  If a phone list cannot
     * be found, <code>null</code> is returned.  The
     * <code>partOfSpeech</code> is implementation dependent, but
     * <code>null</code> always matches.
     *
     * @param word         the word to find
     * @param partOfSpeech the part of speech or <code>null</code>
     * @param useLTS       whether to use the letter-to-sound rules when
     *                     the word is not in the lexicon.
     * @return the list of phones for word or null
     */
    String[] getPhones(String word, String partOfSpeech, boolean useLTS);

    /**
     * Adds a word to the addenda.  The
     * part of speech is implementation dependent.
     *
     * @param word         the word to add
     * @param partOfSpeech the part of speech or <code>null</code>
     */
    void addAddendum(String word, String partOfSpeech, String[] phones);

    /**
     * Removes a word from the addenda.  Both the part of speech and
     * word must be an exact match.
     *
     * @param word         the word to add
     * @param partOfSpeech the part of speech
     */
    void removeAddendum(String word, String partOfSpeech);

    /**
     * Determines if the <code>currentWordPhone</code> represents a
     * new syllable boundary.
     *
     * @param syllablePhones   the phones in the current syllable so far
     * @param wordPhones       the phones for the whole word
     * @param currentWordPhone the word phone in question
     * @return <code>true</code> if the phone is a new boundary
     */
    boolean isSyllableBoundary(List syllablePhones,
                               String[] wordPhones,
                               int currentWordPhone);

    /**
     * Loads this lexicon.  The loading of a lexicon need not be done
     * in the constructor.
     *
     * @throws IOException if an error occurs while loading
     */
    void load() throws IOException;


    /**
     * Determines if this lexicon is loaded.
     *
     * @return <code>true</code> if the lexicon is loaded
     */
    boolean isLoaded();
}

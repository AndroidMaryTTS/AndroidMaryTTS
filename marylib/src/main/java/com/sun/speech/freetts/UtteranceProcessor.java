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
 * Performs an operation on an Utterance.
 * Examples of classes that might implement
 * this interface include a Tokenizer, Normalizer, PartOfSpeechTagger,
 * etc.
 */
public interface UtteranceProcessor {

    /**
     * Performs an operation on the given Utterance.
     *
     * @param u the utterance on which to perform operations
     * @throws ProcessException if an exception occurred during the operation
     */
    void processUtterance(Utterance u) throws ProcessException;
}


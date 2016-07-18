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
package com.sun.speech.freetts.en.us;

/**
 * Provides a CMU time lexicon-specific implementation of a Lexicon.
 * stored in a text file.
 */
public class CMUTimeLexicon extends CMULexicon {

    /**
     * Creates a default CMUTimeLexicon which is a binary lexicon
     */
    public CMUTimeLexicon() {
        super("cmutimelex");
    }
}

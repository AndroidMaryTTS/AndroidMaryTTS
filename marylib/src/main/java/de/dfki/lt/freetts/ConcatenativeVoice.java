/*
 * Portions Copyright 2004 DFKI GmbH.
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute, 
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
package de.dfki.lt.freetts;

import com.sun.speech.freetts.UtteranceProcessor;

import java.io.IOException;
import java.net.URL;

/**
 * A generic interface implementing what is common to all
 * concatentive voices (e.g., diphone, cluster unit and arctic voices).
 */
public interface ConcatenativeVoice {
    /**
     * Gets the url to the database that defines the unit data for this
     * voice.
     *
     * @return a url to the database
     */
    URL getDatabase();

    /**
     * Returns the pitch mark generator to be used by this voice.
     *
     * @return the pitchmark processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    UtteranceProcessor getPitchmarkGenerator() throws IOException;

    /**
     * Returns the unit concatenator to be used by this voice.
     *
     * @return the unit concatenator processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    UtteranceProcessor getUnitConcatenator() throws IOException;

    /**
     * Returns the unit selector to be used by this voice.
     *
     * @return the unit selector processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    UtteranceProcessor getUnitSelector() throws IOException;
}

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

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * Defines limited domain synthesis voice that specializes
 * in telling the time.  This is based on data created by
 * the example time domain voice in FestVox.
 */
public class CMUTimeVoice extends CMUClusterUnitVoice {

    /**
     * Creates a simple cluster unit voice
     *
     * @param name         the name of the voice
     * @param gender       the gender of the voice
     * @param age          the age of the voice
     * @param description  a human-readable string providing a
     *                     description that can be displayed for the users.
     * @param locale       the locale of the voice
     * @param domain       the domain of this voice.  For example,
     * @param organization the organization which created the voice
     *                     &quot;general&quot;, &quot;time&quot;, or
     *                     &quot;weather&quot;.
     * @param lexicon      the lexicon to load
     * @param database     the url to the database containing unit data
     *                     for this voice.
     */
    public CMUTimeVoice(String name, Gender gender, Age age,
                        String description, Locale locale, String domain,
                        String organization, CMULexicon lexicon, URL database) {
        super(name, gender, age, description, locale,
                domain, organization, lexicon, database);
    }

    /**
     * The FestVox voice does not take advantage of any post lexical
     * processing.  As a result, it doesn't end up getting certain
     * units that are expected by the typical post lexical processing.
     * For example, if "the" is followed by a word that begins with
     * a vowel, the typical post lexical processing will change its
     * pronunciation from "dh ax" to "dh iy".  We don't want this
     * in this voice.
     *
     * @return the post lexical analyzer in use by this voice
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getPostLexicalAnalyzer() throws IOException {
        /* Do nothing
         */
        return new UtteranceProcessor() {
            public void processUtterance(Utterance utterance)
                    throws ProcessException {
            }
        };
    }

    /**
     * Converts this object to a string
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "CMUTimeVoice";
    }
}

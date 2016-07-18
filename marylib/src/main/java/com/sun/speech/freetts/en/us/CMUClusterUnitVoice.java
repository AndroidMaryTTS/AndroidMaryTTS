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
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.clunits.ClusterUnitPitchmarkGenerator;
import com.sun.speech.freetts.clunits.ClusterUnitSelector;
import com.sun.speech.freetts.relp.UnitConcatenator;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import de.dfki.lt.freetts.ConcatenativeVoice;

/**
 * Defines voice that does cluster unit selection.
 */
public class CMUClusterUnitVoice extends CMUVoice implements ConcatenativeVoice {

    protected URL database;

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
    public CMUClusterUnitVoice(String name, Gender gender, Age age,
                               String description, Locale locale, String domain,
                               String organization, CMULexicon lexicon, URL database) {
        super(name, gender, age, description, locale,
                domain, organization, lexicon);
        setRate(150f);
        setPitch(100F);
        setPitchRange(12F);
        this.database = database;
    }

    /**
     * Gets the url to the database that defines the unit data for this
     * voice.
     *
     * @return a url to the database
     */
    public URL getDatabase() {
        return database;
    }

    /**
     * Sets up the FeatureSet for this Voice.
     *
     * @throws IOException if an I/O error occurs
     */
    protected void setupFeatureSet() throws IOException {
        super.setupFeatureSet();
        getFeatures().setString(FEATURE_JOIN_TYPE, "simple_join");
    }

    /**
     * Returns the unit selector to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * This voice uses  a cluster unit selector as the unit selector.
     *
     * @return the post lexical processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getUnitSelector() throws IOException {
        return new ClusterUnitSelector(getDatabase());
    }

    /**
     * Returns the pitch mark generator to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * There is no default unit selector
     *
     * @return the post lexical processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getPitchmarkGenerator() throws IOException {
        return new ClusterUnitPitchmarkGenerator();
    }

    /**
     * Returns the unit concatenator to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * There is no default unit selector
     *
     * @return the post lexical processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getUnitConcatenator() throws IOException {
        return new UnitConcatenator();
    }


    /**
     * Converts this object to a string
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "CMUClusterUnitVoice";
    }
}

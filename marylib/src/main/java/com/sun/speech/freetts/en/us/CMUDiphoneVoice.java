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
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.diphone.DiphonePitchmarkGenerator;
import com.sun.speech.freetts.diphone.DiphoneUnitSelector;
import com.sun.speech.freetts.relp.UnitConcatenator;
import com.sun.speech.freetts.util.Utilities;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import de.dfki.lt.freetts.ConcatenativeVoice;


/**
 * Defines an unlimited-domain diphone synthesis based voice
 */
public class CMUDiphoneVoice extends CMUVoice implements ConcatenativeVoice {

    protected URL database;

    /**
     * Creates a simple voice.  This is merely for backwards
     * compatibility with versions of FreeTTS earlier than v1.2
     * (i.e., before the voice manager was introduced).
     */
    public CMUDiphoneVoice() {
        this(null, null, null, null, null, null, null, null, null);
    }

    /**
     * Creates a simple voice
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
     * @param database     a url to the unit database file for this voice
     */
    public CMUDiphoneVoice(String name, Gender gender,
                           Age age, String description, Locale locale, String domain,
                           String organization, CMULexicon lexicon, URL database) {
        super(name, gender, age, description, locale,
                domain, organization, lexicon);
        setRate(150f);
        setPitch(100F);
        setPitchRange(11F);
        this.database = database;
    }

    /**
     * Gets the url to the database that defines the unit data for this
     * voice.
     *
     * @return a url to the database
     */
    public URL getDatabase() {
        if (database == null) {
            /* This is merely for backwards compatibility with
             * versions of FreeTTS earlier than v1.2 (i.e.,
             * before the voice manager was introduced).
             */
            String name = getFeatures().getString(Voice.DATABASE_NAME);
            database = Utilities.getResourceURL(name);
        }
        return database;
    }

    /**
     * Sets the FeatureSet for this Voice.
     *
     * @throws IOException if an I/O error occurs
     */
    protected void setupFeatureSet() throws IOException {
        super.setupFeatureSet();
    }

    /**
     * Returns the post lexical processor to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     *
     * @return the Unit selector
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getPostLexicalAnalyzer() throws IOException {
        return new CMUDiphoneVoicePostLexicalAnalyzer();
    }

    /**
     * Returns the pitch mark generator to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * This voice uses a DiphonePitchMark generator to generate
     * pitchmarks.
     *
     * @return the pitchmark processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getPitchmarkGenerator() throws IOException {
        return new DiphonePitchmarkGenerator();
    }

    /**
     * Returns the unit concatenator to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * This voice uses a relp.UnitConcatenator to concatenate units.
     *
     * @return the unit concatenator processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getUnitConcatenator() throws IOException {
        return new UnitConcatenator();
    }


    /**
     * Returns the unit selector to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * This voice uses the DiphoneUnitSelector to select units. The
     * unit selector requires the name of a diphone database. If no
     * diphone database has been specified then an Error is thrown.
     *
     * @return the unit selector processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getUnitSelector() throws IOException {
        return new DiphoneUnitSelector(getDatabase());
    }


    /**
     * Converts this object to a string
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "CMUDiphoneVoice";
    }
}


/**
 * Annotates the utterance with post lexical information. Converts AH
 * phonemes to AA phoneme in addition to the standard english postlex
 * processing.
 */
class CMUDiphoneVoicePostLexicalAnalyzer implements UtteranceProcessor {
    UtteranceProcessor englishPostLex =
            new com.sun.speech.freetts.en.PostLexicalAnalyzer();

    /**
     * performs the processing
     *
     * @param utterance the utterance to process/tokenize
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        fixPhoneme_AH(utterance);
        englishPostLex.processUtterance(utterance);
    }


    /**
     * Turns all AH phonemes into AA phonemes.
     * This should really be done in the index itself
     *
     * @param utterance the utterance to fix
     */
    private void fixPhoneme_AH(Utterance utterance) {
        for (Item item = utterance.getRelation(Relation.SEGMENT).getHead();
             item != null;
             item = item.getNext()) {
            if (item.getFeatures().getString("name").equals("ah")) {
                item.getFeatures().setString("name", "aa");
            }
        }
    }

    // inherited from Object
    public String toString() {
        return "PostLexicalAnalyzer";
    }
}

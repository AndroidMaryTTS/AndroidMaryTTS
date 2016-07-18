/**
 * Portions Copyright 2004 DFKI GmbH.
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package de.dfki.lt.freetts;

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.PartOfSpeech;
import com.sun.speech.freetts.PartOfSpeechImpl;
import com.sun.speech.freetts.PhoneSet;
import com.sun.speech.freetts.PhoneSetImpl;
import com.sun.speech.freetts.Tokenizer;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.clunits.ClusterUnitPitchmarkGenerator;
import com.sun.speech.freetts.clunits.ClusterUnitSelector;
import com.sun.speech.freetts.en.us.CMULexicon;
import com.sun.speech.freetts.en.us.FeatureProcessors;
import com.sun.speech.freetts.lexicon.Lexicon;
import com.sun.speech.freetts.relp.AudioOutput;
import com.sun.speech.freetts.relp.UnitConcatenator;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * A simple dummy voice as a starting point for non-US-English
 * cluster unit voices. All NLP stuff would need to be implemented
 * in order for this to become a full TTS voice.
 */
public class ClusterUnitVoice extends Voice implements ConcatenativeVoice {
    protected URL database;
    protected URL phonesetURL;
    protected URL partOfSpeechURL;
    private PhoneSet phoneSet;
    private ClusterUnitNamer unitNamer;

    public ClusterUnitVoice(String name, Gender gender, Age age,
                            String description, Locale locale, String domain,
                            String organization, Lexicon lexicon, URL database) {
        this(name, gender, age, description, locale, domain,
                organization, lexicon, database, null, null, null);
    }

    /**
     * Creates a ClusterUnitVoice
     *
     * @param database        the database of the voice
     * @param unitNamer       specifies the name of the Units (if null, an
     *                        ldom naming scheme will be used: 'ae_afternoon')
     * @param phonesetURL     leads to the phoneset, which will be used
     *                        for the FeatureProcessors (can be null)
     * @param partOfSpeechURL leads to the pos-textfile which will be used
     *                        for the FeatureProcessors (can be null)
     */
    public ClusterUnitVoice(String name, Gender gender, Age age,
                            String description, Locale locale, String domain,
                            String organization, Lexicon lexicon, URL database,
                            ClusterUnitNamer unitNamer, URL phonesetURL, URL partOfSpeechURL) {

        //TODO: do something useful with the lexicon
        super(name, gender, age, description, locale,
                domain, organization);
        setRate(150f);
        setPitch(100F);
        setPitchRange(12F);
        if (lexicon != null) {
            setLexicon(lexicon);
        } else {
            // Use a small dummy lexicon
            setLexicon(new CMULexicon("cmutimelex"));
        }
        this.database = database;
        this.unitNamer = unitNamer;
        this.phonesetURL = phonesetURL;
        this.partOfSpeechURL = partOfSpeechURL;
    }

    public Tokenizer getTokenizer() {
        return null;
    }


    protected void loader() throws IOException {
        setupFeatureProcessors();
    }


    protected UtteranceProcessor getAudioOutput() throws IOException {
        return new AudioOutput();
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
     * Returns the unit selector to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * This voice uses  a cluster unit selector as the unit selector.
     *
     * @return the post lexical processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getUnitSelector() throws IOException {
        return new ClusterUnitSelector(getDatabase(), unitNamer);
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

    protected void setupFeatureProcessors() throws IOException {
        if (phonesetURL != null) {
            phoneSet = new PhoneSetImpl(phonesetURL);
        }
        if (partOfSpeechURL != null) {
            PartOfSpeech pos = new PartOfSpeechImpl(partOfSpeechURL,
                    "content");
            addFeatureProcessor("gpos", new FeatureProcessors.Gpos(pos));
        }


        addFeatureProcessor("word_break", new FeatureProcessors.WordBreak());
        addFeatureProcessor("word_punc", new FeatureProcessors.WordPunc());
        addFeatureProcessor("word_numsyls", new FeatureProcessors.WordNumSyls());
        addFeatureProcessor("ssyl_in", new FeatureProcessors.StressedSylIn());
        addFeatureProcessor("syl_in", new FeatureProcessors.SylIn());
        addFeatureProcessor("syl_out", new FeatureProcessors.SylOut());
        addFeatureProcessor("ssyl_out", new
                FeatureProcessors.StressedSylOut());
        addFeatureProcessor("syl_break", new FeatureProcessors.SylBreak());
        addFeatureProcessor("old_syl_break", new FeatureProcessors.SylBreak());
        addFeatureProcessor("num_digits", new FeatureProcessors.NumDigits());
        addFeatureProcessor("month_range", new FeatureProcessors.MonthRange());
        addFeatureProcessor("token_pos_guess",
                new FeatureProcessors.TokenPosGuess());
        addFeatureProcessor("segment_duration",
                new FeatureProcessors.SegmentDuration());
        addFeatureProcessor("sub_phrases", new FeatureProcessors.SubPhrases());
        addFeatureProcessor("asyl_in", new FeatureProcessors.AccentedSylIn());
        addFeatureProcessor("last_accent", new FeatureProcessors.LastAccent());
        addFeatureProcessor("pos_in_syl", new FeatureProcessors.PosInSyl());
        addFeatureProcessor("position_type", new
                FeatureProcessors.PositionType());

        addFeatureProcessor("ph_cplace", new FeatureProcessors.PH_CPlace());
        addFeatureProcessor("ph_ctype", new FeatureProcessors.PH_CType());
        addFeatureProcessor("ph_cvox", new FeatureProcessors.PH_CVox());
        addFeatureProcessor("ph_vc", new FeatureProcessors.PH_VC());
        addFeatureProcessor("ph_vfront", new FeatureProcessors.PH_VFront());
        addFeatureProcessor("ph_vheight", new FeatureProcessors.PH_VHeight());
        addFeatureProcessor("ph_vlng", new FeatureProcessors.PH_VLength());
        addFeatureProcessor("ph_vrnd", new FeatureProcessors.PH_VRnd());

        addFeatureProcessor("seg_coda_fric", new
                FeatureProcessors.SegCodaFric());
        addFeatureProcessor("seg_onset_fric", new
                FeatureProcessors.SegOnsetFric());

        addFeatureProcessor("seg_coda_stop", new
                FeatureProcessors.SegCodaStop());
        addFeatureProcessor("seg_onset_stop", new
                FeatureProcessors.SegOnsetStop());

        addFeatureProcessor("seg_coda_nasal", new
                FeatureProcessors.SegCodaNasal());
        addFeatureProcessor("seg_onset_nasal", new
                FeatureProcessors.SegOnsetNasal());

        addFeatureProcessor("seg_coda_glide", new
                FeatureProcessors.SegCodaGlide());
        addFeatureProcessor("seg_onset_glide", new
                FeatureProcessors.SegOnsetGlide());

        addFeatureProcessor("seg_onsetcoda", new
                FeatureProcessors.SegOnsetCoda());
        addFeatureProcessor("syl_codasize", new
                FeatureProcessors.SylCodaSize());
        addFeatureProcessor("syl_onsetsize", new
                FeatureProcessors.SylOnsetSize());
        addFeatureProcessor("accented", new FeatureProcessors.Accented());
    }

    /**
     * Given a phoneme and a feature name, return the feature
     *
     * @param phone       the phoneme of interest
     * @param featureName the name of the feature of interest
     * @return the feature with the given name
     */
    public String getPhoneFeature(String phone, String featureName) {
        if (phoneSet != null)
            return phoneSet.getPhoneFeature(phone, featureName);
        else
            return null;
    }

}

/**
 * Portions Copyright 2003 Sun Microsystems, Inc.
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
import com.sun.speech.freetts.FeatureSet;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.PartOfSpeech;
import com.sun.speech.freetts.PartOfSpeechImpl;
import com.sun.speech.freetts.PhoneDurations;
import com.sun.speech.freetts.PhoneDurationsImpl;
import com.sun.speech.freetts.PhoneSet;
import com.sun.speech.freetts.PhoneSetImpl;
import com.sun.speech.freetts.Segmenter;
import com.sun.speech.freetts.Tokenizer;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.cart.CARTImpl;
import com.sun.speech.freetts.cart.Durator;
import com.sun.speech.freetts.cart.Intonator;
import com.sun.speech.freetts.cart.Phraser;
import com.sun.speech.freetts.en.ContourGenerator;
import com.sun.speech.freetts.en.PartOfSpeechTagger;
import com.sun.speech.freetts.en.PauseGenerator;
import com.sun.speech.freetts.relp.AudioOutput;
import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Provides generic support for a CMU Voice
 */
public abstract class CMUVoice extends Voice {
    private PhoneSet phoneSet;
    private boolean useBinaryIO =
            Utilities.getProperty("com.sun.speech.freetts.useBinaryIO",
                    "true").equals("true");

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
     */
    public CMUVoice(String name, Gender gender,
                    Age age, String description, Locale locale, String domain,
                    String organization, CMULexicon lexicon) {
        super(name, gender, age, description, locale, domain,
                organization);
        setLexicon(lexicon);
    }

    // overrides Voice.loader

    /**
     * Called by <code> Voice </code>  during loading, derived voices
     * should override this to provide customized loading.
     */
    protected void loader() throws IOException {
        setupFeatureSet();
        setupUtteranceProcessors();
        setupFeatureProcessors();
    }

    /**
     * Sets up the FeatureSet for this Voice.
     *
     * @throws IOException if an I/O error occurs
     */
    protected void setupFeatureSet() throws IOException {
        BulkTimer.LOAD.start("FeatureSet");
        FeatureSet features = getFeatures();
        features.setString(FEATURE_SILENCE, "pau");
        features.setString("join_type", "simple_join");
        BulkTimer.LOAD.stop("FeatureSet");
    }


    /**
     * Sets up the utterance processors for this voice. Subclasses
     * should define this method to setup the utterance processors for
     * the voice.
     *
     * @throws IOException throws an IOException if an error occurs
     */
    protected void setupUtteranceProcessors() throws IOException {
        List processors = getUtteranceProcessors();

        BulkTimer.LOAD.start("CartLoading");
        CARTImpl numbersCart = new CARTImpl(getResource("nums_cart.txt"));
        CARTImpl phrasingCart = new CARTImpl(getResource("phrasing_cart.txt"));
        CARTImpl accentCart = new CARTImpl(getResource("int_accent_cart.txt"));
        CARTImpl toneCart = new CARTImpl(getResource("int_tone_cart.txt"));
        CARTImpl durzCart = new CARTImpl(getResource("durz_cart.txt"));
        BulkTimer.LOAD.stop("CartLoading");

        BulkTimer.LOAD.start("UtteranceProcessors");
        PhoneDurations phoneDurations = new PhoneDurationsImpl(
                getResource("dur_stat.txt"));
        PronounceableFSM prefixFSM = new PrefixFSM
                (getResource("prefix_fsm.txt"));
        PronounceableFSM suffixFSM = new SuffixFSM
                (getResource("suffix_fsm.txt"));

        processors.add(new TokenToWords(numbersCart, prefixFSM, suffixFSM));
        processors.add(new PartOfSpeechTagger());
        processors.add(new Phraser(phrasingCart));
        processors.add(new Segmenter());
        processors.add(new PauseGenerator());
        processors.add(new Intonator(accentCart, toneCart));
        processors.add(getPostLexicalAnalyzer());
        processors.add(new Durator(durzCart, 150.0f, phoneDurations));
        processors.add(new ContourGenerator
                (getResource("f0_lr_terms.txt"), 170.0f, 34.0f));


        processors.add(getUnitSelector());
        processors.add(getPitchmarkGenerator());
        processors.add(getUnitConcatenator());
        BulkTimer.LOAD.stop("UtteranceProcessors");
    }

    // [[[TODO: currently a CMUVoice only allows customization of
    // the postlex, unit selector and wave synthesizer. This may 
    // grow as time goes on ]]]

    /**
     * Returns the post lexical processor to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     *
     * @return the post lexical analyzer in use by this voice
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getPostLexicalAnalyzer() throws IOException {
        return new com.sun.speech.freetts.en.PostLexicalAnalyzer();
    }

    /**
     * Returns the unit selector to be used by this voice
     * Derived voices typically override this to customize behaviors.
     *
     * @return the unit selector in use by this voice
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getUnitSelector() throws IOException {
        return null;
    }

    /**
     * Returns the pitch mark generator to be used by this voice
     * Derived voices typically override this to customize behaviors.
     *
     * @return the pitch mark generator to be used by this voice
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getPitchmarkGenerator() throws IOException {
        return null;
    }

    /**
     * Returns the unit concatenator to be used by this voice
     * Derived voices typically override this to customize behaviors.
     *
     * @return the Unit concatenator
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getUnitConcatenator() throws IOException {
        return null;
    }


    /**
     * Sets up the FeatureProcessors for this Voice.
     *
     * @throws IOException if an I/O error occurs
     */
    protected void setupFeatureProcessors() throws IOException {
        BulkTimer.LOAD.start("FeatureProcessing");
        PartOfSpeech pos = new PartOfSpeechImpl(
                getResource("part_of_speech.txt"),
                "content");

        phoneSet = new PhoneSetImpl(getResource("phoneset.txt"));

        addFeatureProcessor("word_break", new FeatureProcessors.WordBreak());
        addFeatureProcessor("word_punc", new FeatureProcessors.WordPunc());
        addFeatureProcessor("gpos", new FeatureProcessors.Gpos(pos));
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
        BulkTimer.LOAD.stop("FeatureProcessing");
    }

    /**
     * Given a phoneme and a feature name, return the feature
     *
     * @param phone       the phoneme of interest
     * @param featureName the name of the feature of interest
     * @return the feature with the given name
     */
    public String getPhoneFeature(String phone, String featureName) {
        return phoneSet.getPhoneFeature(phone, featureName);
    }

    /**
     * Returns the AudioOutput processor to be used by this voice
     * Derived voices typically override this to customize behaviors.
     *
     * @return the audio output processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getAudioOutput() throws IOException {
        return new AudioOutput();
    }

    /**
     * Gets a tokenizer for this voice
     *
     * @return the tokenizer
     */
    public Tokenizer getTokenizer() {
        Tokenizer tokenizer = new com.sun.speech.freetts.en.TokenizerImpl();
        tokenizer.setWhitespaceSymbols(USEnglish.WHITESPACE_SYMBOLS);
        tokenizer.setSingleCharSymbols(USEnglish.SINGLE_CHAR_SYMBOLS);
        tokenizer.setPrepunctuationSymbols(USEnglish.PREPUNCTUATION_SYMBOLS);
        tokenizer.setPostpunctuationSymbols(USEnglish.PUNCTUATION_SYMBOLS);
        return tokenizer;
    }

    /**
     * Converts this object to its String representation
     *
     * @return the string representation of this object
     */
    public String toString() {
        return "CMUVoice";
    }
}


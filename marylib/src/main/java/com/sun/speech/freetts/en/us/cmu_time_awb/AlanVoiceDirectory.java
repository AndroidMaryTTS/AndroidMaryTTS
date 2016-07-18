package com.sun.speech.freetts.en.us.cmu_time_awb;

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceDirectory;
import com.sun.speech.freetts.en.us.CMUClusterUnitVoice;
import com.sun.speech.freetts.en.us.CMULexicon;
import com.sun.speech.freetts.util.Utilities;

import java.util.Locale;


/**
 * This voice directory provides a default US/English Cluster Unit
 * voice imported from CMU Flite.
 */
public class AlanVoiceDirectory extends VoiceDirectory {
    /**
     * Print out information about this voice jarfile.
     */
    public static void main(String[] args) {
        System.out.println((new AlanVoiceDirectory()).toString());
    }

    /**
     * Gets the voices provided by this voice.
     *
     * @return an array of new Voice instances
     */
    public Voice[] getVoices() {
        CMULexicon lexicon = new CMULexicon("cmutimelex");
        Voice alan = new CMUClusterUnitVoice("alan", Gender.MALE,
                Age.YOUNGER_ADULT, "default time-domain cluster unit voice",
                Locale.US, "time", "cmu", lexicon,
                Utilities.getResourceURL("cmu_time_awb.bin"));
        Voice[] voices = {alan};
        return voices;
    }
}

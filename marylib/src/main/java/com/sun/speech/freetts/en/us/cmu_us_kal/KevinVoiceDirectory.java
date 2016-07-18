package com.sun.speech.freetts.en.us.cmu_us_kal;

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceDirectory;
import com.sun.speech.freetts.en.us.CMUDiphoneVoice;
import com.sun.speech.freetts.en.us.CMULexicon;
import com.sun.speech.freetts.util.Utilities;

import java.util.Locale;


/**
 * This voice directory provides default US/English Diphone voices
 * imported from CMU Flite
 */
public class KevinVoiceDirectory extends VoiceDirectory {
    /**
     * Print out information about this voice jarfile.
     */
    public static void main(String[] args) {
        System.out.println((new KevinVoiceDirectory()).toString());
    }

    /**
     * Gets the voices provided by this voice.
     *
     * @return an array of new Voice instances
     */
    public Voice[] getVoices() {
        CMULexicon lexicon = new CMULexicon("cmulex");
        Voice kevin = new CMUDiphoneVoice("kevin", Gender.MALE,
                Age.YOUNGER_ADULT, "default 8-bit diphone voice",
                Locale.US, "general", "cmu", lexicon,
//                this.getClass().getResource("cmu_us_kal.bin"));
                Utilities.getResourceURL("cmu_us_kal"));
        Voice kevin16 = new CMUDiphoneVoice("kevin16", Gender.MALE,
                Age.YOUNGER_ADULT, "default 16-bit diphone voice",
                Locale.US, "general", "cmu", lexicon,
//                this.getClass().getResource("cmu_us_kal16.bin"));
                Utilities.getResourceURL("cmu_us_kal16"));
        Voice[] voices = {kevin, kevin16};
        return voices;
    }
}

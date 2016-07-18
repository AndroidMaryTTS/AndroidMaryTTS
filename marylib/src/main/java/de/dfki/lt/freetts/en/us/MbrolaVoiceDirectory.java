package de.dfki.lt.freetts.en.us;

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.ValidationException;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceDirectory;
import com.sun.speech.freetts.en.us.CMULexicon;
import com.sun.speech.freetts.util.Utilities;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Provides access to MBROLA voices.
 */
public class MbrolaVoiceDirectory extends VoiceDirectory {

    /**
     * Prints out the MBROLA voices.
     */
    public static void main(String[] args) {
        System.out.println((new MbrolaVoiceDirectory()).toString());
    }

    public Voice[] getVoices() {

        String base = Utilities.getProperty("mbrola.base", null);

        if (base == null || base.trim().length() == 0) {
            System.out.println(
                    "System property \"mbrola.base\" is undefined.  "
                            + "Will not use MBROLA voices.");
            return new Voice[0];
        } else {

            CMULexicon lexicon = new CMULexicon("cmulex");

            Voice mbrola1 = new MbrolaVoice
                    ("us1", "us1", 150f, 180F, 22F,
                            "mbrola_us1", Gender.FEMALE, Age.YOUNGER_ADULT,
                            "MBROLA Voice us1",
                            Locale.US, "general", "mbrola", lexicon);

            Voice mbrola2 = new MbrolaVoice
                    ("us2", "us2", 150f, 115F, 12F,
                            "mbrola_us2", Gender.MALE, Age.YOUNGER_ADULT,
                            "MBROLA Voice us2",
                            Locale.US, "general", "mbrola", lexicon);

            Voice mbrola3 = new MbrolaVoice
                    ("us3", "us3", 150f, 125F, 12F,
                            "mbrola_us3", Gender.MALE, Age.YOUNGER_ADULT,
                            "MBROLA Voice us3",
                            Locale.US, "general", "mbrola", lexicon);

            Voice[] voices = {mbrola1, mbrola2, mbrola3};

            ArrayList validVoices = new ArrayList();
            int count = 0;

            for (int i = 0; i < voices.length; i++) {
                MbrolaVoiceValidator validator =
                        new MbrolaVoiceValidator((MbrolaVoice) voices[i]);
                try {
                    validator.validate();
                    validVoices.add(voices[i]);
                    count++;
                } catch (ValidationException ve) {
                    // does nothing if the voice is not found
                }
            }
            if (count == 0) {
                System.err.println(
                        "\n"
                                + "Could not validate any MBROLA voices at\n\n"
                                + "  " + base + "\n");
                if (base.indexOf('~') != -1) {
                    System.err.println(
                            "DO NOT USE ~ as part of the path name\n"
                                    + "to specify the mbrola.base property.");
                }
                System.err.println(
                        "Make sure you FULLY specify the path to\n"
                                + "the MBROLA directory using the mbrola.base\n"
                                + "system property.\n");
                return new Voice[0];
            } else {
                return ((Voice[]) validVoices.toArray(new Voice[count]));
            }
        }
    }
}



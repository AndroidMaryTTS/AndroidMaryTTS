/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;

/**
 * Provides a means to access the voices that are stored in a jar
 * file.  Every jar file that provides a voice must contain a subclass
 * of VoiceDirectory.  The class must provide a main() function that
 * calls dumpVoices() or performs an equivalent operation.  All
 * subclasses of VoiceDirectory can be assumed to always be created by
 * the default constructor (no arguments).
 * <p/>
 * Any jar file that has a subclass of VoiceDirectory must define
 * certain attributes in its Manifest.  "Main-class:" must refer to
 * the subclass of VoiceDirectory. "Class-Path:" lists the other jar
 * files upon which this is dependant.  For example,
 * "cmu_us_kal.jar" may be dependant on "en_us.jar" for its lexicon.
 * The Manifest must also have a "FreeTTSVoiceDefinition: true" entry.
 *
 * @see Voice
 * @see VoiceManager
 */
public abstract class VoiceDirectory {
    /**
     * Default constructor does nothing.  This may be overridden by
     * subclasses, but it is not recommended.  This is the only
     * constructor that will be called.
     */
    public VoiceDirectory() {
        // default constructor does nothing
    }

    /**
     * The main function must be implemented by subclasses to print
     * out information about provided voices.  For example, they may
     * just call dumpVoices()
     *
     * @see #toString()
     */
    public static void main(String[] args) {
        // subclasses must call dumpVoices()
    }

    /**
     * Provide a means to access the voices in a voice jar file.  The
     * implementation of this function is up to the subclasses.
     *
     * @return an array of Voice instances provided in the jar file
     */
    public abstract Voice[] getVoices();

    /**
     * Print the information about voices contained in this voice
     * directory to a String.
     *
     * @return a String containing the information
     * @see #main(String[] args)
     */
    public String toString() {
        String newline = System.getProperty("line.separator");
        Voice[] voices = getVoices();
        String s = "VoiceDirectory '" + this.getClass().getName() + "'"
                + newline;

        for (int i = 0; i < voices.length; i++) {
            s += newline + "Name: " + voices[i].getName() + newline
                    + "\tDescription: " + voices[i].getDescription() + newline
                    + "\tOrganization: " + voices[i].getOrganization() + newline
                    + "\tDomain: " + voices[i].getDomain() + newline
                    + "\tLocale: " + voices[i].getLocale().toString() + newline
                    + "\tStyle: " + voices[i].getStyle() + newline
                    + "\tGender: " + voices[i].getGender().toString() + newline
                    + "\tAge: " + voices[i].getAge().toString() + newline
                    + "\tPitch: " + voices[i].getPitch() + newline
                    + "\tPitch Range: " + voices[i].getPitchRange() + newline
                    + "\tPitch Shift: " + voices[i].getPitchShift() + newline
                    + "\tRate: " + voices[i].getRate() + newline
                    + "\tVolume: " + voices[i].getVolume() + newline
                    + newline;
        }
        return s;
    }
}

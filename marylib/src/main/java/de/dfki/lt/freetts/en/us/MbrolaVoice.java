/**
 * Copyright 2002 DFKI GmbH.
 * Portions Copyright 2002 Sun Microsystems, Inc.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package de.dfki.lt.freetts.en.us;

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.en.us.CMULexicon;
import com.sun.speech.freetts.en.us.CMUVoice;
import com.sun.speech.freetts.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import de.dfki.lt.freetts.mbrola.MbrolaAudioOutput;
import de.dfki.lt.freetts.mbrola.MbrolaCaller;
import de.dfki.lt.freetts.mbrola.ParametersToMbrolaConverter;

/**
 * Defines an unlimited-domain diphone synthesis based voice using
 * the MBROLA synthesis.
 */
public class MbrolaVoice extends CMUVoice {

    private static final String MRPA_TO_SAMPA_RENAME_LIST =
            "V ah i iy I ih U uh { ae @ ax r= er A aa O ao u uw E eh EI ey AI ay OI oy aU aw @U ow j y h hh N ng S sh T th Z zh D dh tS ch dZ jh _ pau";
    private String databaseDirectory; // where the voice database is
    private String database;          // name of the voice database

    /**
     * Creates an MbrolaVoice.
     *
     * @param databaseDirectory the directory within the MBROLA directory
     *                          where the voice database of this voice is located
     * @param database          the name of the voice database of this voice
     * @param rate              the rate of the voice
     * @param pitch             the pitch of the voice
     * @param range             the range of the voice
     * @param name              the name of the voice
     * @param gender            the gender of the voice
     * @param age               the age of the voice
     * @param description       a human-readable string providing a
     *                          description that can be displayed for the users.
     * @param locale            the locale of the voice
     * @param domain            the domain of this voice.  For example,
     * @param organization      the organization which created the voice
     * @param lexicon           the lexicon to use
     */
    public MbrolaVoice(String databaseDirectory,
                       String database, float rate, float pitch, float range,
                       String name, Gender gender, Age age,
                       String description, Locale locale, String domain,
                       String organization, CMULexicon lexicon) {
        super(name, gender, age, description, locale,
                domain, organization, lexicon);
        setRate(rate);
        setPitch(pitch);
        setPitchRange(range);
        this.databaseDirectory = databaseDirectory;
        this.database = database;
    }

    //[[Providing the Mbrola classes via getUnitSelector() and
    // getUnitConcatenator() is just a hack allowing us to use
    // the current CMUVoice.java framework. It only means that
    // after the Durator and the ContourGenerator, the classes
    // process the utterance (Selector before Concatenator).]]

    /**
     * Returns the unit selector to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     *
     * @return the unit selector
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getUnitSelector() throws IOException {
        return new ParametersToMbrolaConverter();
    }

    /**
     * Returns the command line that invokes the MBROLA executable.
     * The command will be in the form of:
     * <p/>
     * <pre> {mbrolaExecutable} -e -R {mbrolaRenameList} {mbrolaVoiceDB}
     * - -.raw </pre>
     */
    protected String[] getMbrolaCommand() {

        // Construct the mbrola command in such a way that
        // mbrola reads from stdin and writes raw, headerless audio data
        // to stdout; translates CMU us radio to sampa phonetic symbols;
        // and only complains, but does not abort, when encountering an
        // unknown diphone:
        String[] cmd =
                {getMbrolaBinary(), "-e", "-R", getRenameList(),
                        getDatabase(), "-", "-.raw"};

        if (false) {
            for (int i = 0; i < cmd.length; i++) {
                System.out.println(cmd[i]);
            }
        }

        return cmd;
    }

    /**
     * Returns the absolute name of the MBROLA directory.
     *
     * @return the absolute name of the MBROLA directory
     */
    public String getMbrolaBase() {
        return Utilities.getProperty("mbrola.base", ".");
    }

    /**
     * Returns the absolute file name of the MBROLA binary.
     *
     * @return the absolute file name of the MBROLA binary
     */
    public String getMbrolaBinary() {
        return getMbrolaBase() + File.separator + "mbrola";
    }

    /**
     * Returns the absolute file name of the MBROLA phonetic symbols
     * rename table.
     *
     * @return the absolute file name of the rename table
     */
    public String getRenameList() {
        return MRPA_TO_SAMPA_RENAME_LIST;
    }

    /**
     * Returns the absolute file name of the Voice database
     * this MbrolaVoice uses.
     *
     * @return the absolute file name of the Voice database
     */
    public String getDatabase() {
        return getMbrolaBase() + File.separator +
                databaseDirectory + File.separator + database;
    }

//    /**
//     * Returns the unit concatenator to be used by this voice.
//     * Derived voices typically override this to customize behaviors.
//     * 
//     * @return the unit conatenator
//     * 
//     * @throws IOException if an IO error occurs while getting
//     *     processor
//     */
//    protected UtteranceProcessor getUnitConcatenator() throws IOException {
//        return null;
//    }

    //[[Providing the Mbrola classes via getUnitSelector() and
    // getUnitConcatenator() is just a hack allowing us to use
    // the current CMUVoice.java framework. It only means that
    // after the Durator and the ContourGenerator, the classes
    // process the utterance (Selector before Concatenator).]]

    /**
     * Returns the unit concatenator to be used by this voice.
     * This method constructs the command line with which the
     * MBROLA binary will be called, and initialises the
     * MbrolaCaller accordingly.
     *
     * @return the unit conatenator
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected UtteranceProcessor getUnitConcatenator() throws IOException {
        return new MbrolaCaller(getMbrolaCommand());
    }

    /**
     * Returns the audio output used by this voice.
     *
     * @return the audio output used by this voice
     * @throws IOException if an I/O error occurs
     */
    protected UtteranceProcessor getAudioOutput() throws IOException {
        return new MbrolaAudioOutput();
    }

    /**
     * Get a resource for this voice.  Resources for this voice are located in
     * the package <code>com.sun.speech.freetts.en.us</code>.
     */
    protected URL getResource(String resource) {
        return com.sun.speech.freetts.en.us.CMUVoice.class.
                getResource(resource);
    }

    /**
     * Converts this object to a string
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "MbrolaVoice";
    }
}


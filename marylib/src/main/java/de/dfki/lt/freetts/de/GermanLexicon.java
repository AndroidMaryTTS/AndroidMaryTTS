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
package de.dfki.lt.freetts.de;

import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.lexicon.LexiconImpl;
import com.sun.speech.freetts.util.BulkTimer;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Provides a CMU lexicon-specific implementation of a Lexicon that is
 * stored in a text file.
 */
public class GermanLexicon extends LexiconImpl {


    /**
     * Creates a GermanLexicon based upon the given compiled and addenda
     * DBs and the given letter to sound rules
     *
     * @param compiledURL      the compiled database is loaded from here
     * @param addendaURL       the database addenda is loaded from here
     * @param letterToSoundURL the letter to sound rules are loaded
     *                         from here
     * @param binary           if <code>true</code> the input data are loaded as
     *                         binary ; otherwise if <code>false</code> the input
     *                         data are loaded as text.
     */
    public GermanLexicon(URL compiledURL,
                         URL addendaURL,
                         URL letterToSoundURL,
                         boolean binary) {
        setLexiconParameters(compiledURL, addendaURL, letterToSoundURL, binary);
    }

    /**
     * Creates the default CMU Lexicon which is a binary lexicon
     */
    public GermanLexicon() {
        this("germanlex");
    }

    /**
     * Creates the CMU Lexicon which is a binary lexicon
     *
     * @param basename the basename for the lexicon.
     */
    public GermanLexicon(String basename) {
        this(basename, true);
    }

    public GermanLexicon(String basename, boolean useBinaryIO) {
        java.net.URLClassLoader classLoader =
                VoiceManager.getVoiceClassLoader();
        String type = (useBinaryIO ? "bin" : "txt");

        URL letterToSoundURL = classLoader.getResource(
                "de/dfki/lt/freetts/de/" + basename + "_lts." + type);
        URL compiledURL = classLoader.getResource(
                "de/dfki/lt/freetts/de/" + basename
                        + "_compiled." + type);
        URL addendaURL = classLoader.getResource(
                "de/dfki/lt/freetts/de/" + basename
                        + "_addenda." + type);

        /* Just another try with possibly a different class loader
         * if the above didn't work.
         */
        if (letterToSoundURL == null) {
            Class cls = GermanLexicon.class;
            letterToSoundURL = cls.getResource(basename + "_lts." + type);
            compiledURL = cls.getResource(basename + "_compiled." + type);
            addendaURL = cls.getResource(basename + "_addenda." + type);
            if (letterToSoundURL == null) {
                System.err.println(
                        "GermanLexicon: Oh no!  Couldn't find lexicon data!");
            }
        }

        setLexiconParameters(compiledURL, addendaURL,
                letterToSoundURL, useBinaryIO);
    }

    /**
     * Get the GermanLexicon.
     *
     * @param useBinaryIO if true use binary IO to load DB
     * @throws IOException if problems occurred while reading the data
     */
    static public GermanLexicon getInstance(boolean useBinaryIO)
            throws IOException {
        return getInstance("dummylex", useBinaryIO);
    }

    /**
     * Get the GermanLexicon.
     *
     * @param useBinaryIO if true use binary IO to load DB
     * @throws IOException if problems occurred while reading the data
     */
    static public GermanLexicon getInstance(String basename, boolean useBinaryIO)
            throws IOException {
        GermanLexicon lexicon = new GermanLexicon(basename, useBinaryIO);
        lexicon.load();
        return lexicon;
    }

    /**
     * Provides test code for the GermanLexicon.
     * <br><b>Usage:</b><br>
     * <pre>
     *  de.dfki.lt.freetts.de.GermanLexicon [options]
     *
     * Where options is any combination of:
     *
     * -src path
     * -dest path
     * -generate_binary [base_name]
     * -compare
     * -showtimes
     *
     * </pre>
     */
    public static void main(String[] args) {
        LexiconImpl lex, lex2;
        boolean showTimes = false;
        String srcPath = ".";
        String destPath = ".";
        String baseName = "germanlex";

        try {
            if (args.length > 0) {
                BulkTimer.LOAD.start();
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-src")) {
                        srcPath = args[++i];
                    } else if (args[i].equals("-dest")) {
                        destPath = args[++i];
                    } else if (args[i].equals("-name")
                            && i < args.length - 1) {
                        baseName = args[++i];
                    } else if (args[i].equals("-generate_binary")) {

                        System.out.println("Loading " + baseName);
                        String path = "file:" + srcPath + "/" + baseName;
                        lex = new GermanLexicon(
                                new URL(path + "_compiled.txt"),
                                new URL(path + "_addenda.txt"),
                                new URL(path + "_lts.txt"),
                                false);
                        BulkTimer.LOAD.start("load_text");
                        lex.load();
                        BulkTimer.LOAD.stop("load_text");

                        System.out.println("Dumping " + baseName);
                        BulkTimer.LOAD.start("dump_text");
                        lex.dumpBinary(destPath + "/" + baseName);
                        BulkTimer.LOAD.stop("dump_text");

                    } else if (args[i].equals("-compare")) {

                        BulkTimer.LOAD.start("load_text");
                        lex = GermanLexicon.getInstance(baseName, false);
                        BulkTimer.LOAD.stop("load_text");

                        BulkTimer.LOAD.start("load_binary");
                        lex2 = GermanLexicon.getInstance(baseName, true);
                        BulkTimer.LOAD.stop("load_binary");

                        BulkTimer.LOAD.start("compare");
                        lex.compare(lex2);
                        BulkTimer.LOAD.stop("compare");
                    } else if (args[i].equals("-showtimes")) {
                        showTimes = true;
                    } else {
                        System.out.println("Unknown option " + args[i]);
                    }
                }
                BulkTimer.LOAD.stop();
                if (showTimes) {
                    BulkTimer.LOAD.show("GermanLexicon loading and dumping");
                }
            } else {
                System.out.println("Options: ");
                System.out.println("    -src path");
                System.out.println("    -dest path");
                System.out.println("    -compare");
                System.out.println("    -generate_binary");
                System.out.println("    -showtimes");
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    /**
     * Determines if the currentPhone represents a new syllable
     * boundary.
     *
     * @param syllablePhones   the phones in the current syllable so far
     * @param wordPhones       the phones for the whole word
     * @param currentWordPhone the word phone in question
     * @return <code>true</code> if the word phone in question is on a
     * syllable boundary; otherwise <code>false</code>.
     */
    public boolean isSyllableBoundary(List syllablePhones,
                                      String[] wordPhones,
                                      int currentWordPhone) {
        return false;
    }
}

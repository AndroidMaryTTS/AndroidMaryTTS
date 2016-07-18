/**
 * Portions Copyright 2001-2005 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;

import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.JavaClipAudioPlayer;
import com.sun.speech.freetts.audio.MultiFileAudioPlayer;
import com.sun.speech.freetts.audio.NullAudioPlayer;
import com.sun.speech.freetts.audio.RawFileAudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioSystem;

/**
 * Standalone utility that directly interacts with a CMUDiphoneVoice.
 */
public class FreeTTS {

    private final static String VERSION = "FreeTTS 1.2.1 March 10, 2005";
    private static AudioPlayer audioPlayer = null;
    private Voice voice;
    private boolean silent = false;
    private String audioFile = null;
    private boolean multiAudio = false;
    private boolean streamingAudio = false;
    private InputMode inputMode = InputMode.INTERACTIVE;

    /**
     * Constructs a default FreeTTS with the kevin16 voice.
     */
    public FreeTTS() {
        VoiceManager voiceManager = VoiceManager.getInstance();
        voiceManager.getVoice("kevin16");
    }

    /**
     * Creates a FreeTTS object with the given Voice.
     *
     * @param voice the voice to use
     */
    public FreeTTS(Voice voice) {
        this.voice = voice;
    }

    /**
     * Given a filename returns the extension for the file
     *
     * @param path the path to extract the extension from
     * @return the extension or <code>null</code> if none
     */
    private static String getExtension(String path) {
        int index = path.lastIndexOf(".");
        if (index == -1) {
            return null;
        } else {
            return path.substring(index + 1);
        }
    }

    /**
     * Given a filename returns the basename for the file
     *
     * @param path the path to extract the basename from
     * @return the basename of the file
     */
    private static String getBasename(String path) {
        int index = path.lastIndexOf(".");
        if (index == -1) {
            return path;
        } else {
            return path.substring(0, index);
        }
    }

    /**
     * Prints the usage message for FreeTTS.
     */
    static void usage(String voices) {
        System.out.println(VERSION);
        System.out.println("Usage:");
        System.out.println("    -detailedMetrics: turn on detailed metrics");
        System.out.println("    -dumpAudio file : dump audio to file ");
        System.out.println("    -dumpAudioTypes : dump the possible" +
                " output types");
        System.out.println("    -dumpMultiAudio file : dump audio to file ");
        System.out.println("    -dumpRelations  : dump the relations ");
        System.out.println("    -dumpUtterance  : dump the final utterance");
        System.out.println("    -dumpASCII file : dump the final wave to file as ASCII");
        System.out.println("    -file file      : speak text from given file");
        System.out.println("    -lines file     : render lines from a file");
        System.out.println("    -help           : shows usage information");
        System.out.println("    -voiceInfo      : print detailed voice info");
        System.out.println("    -metrics        : turn on metrics");
        System.out.println("    -run  name      : sets the name of the run");
        System.out.println("    -silent         : don't say anything");
        System.out.println("    -streaming      : use streaming audio player");
        System.out.println("    -text say me    : speak given text");
        System.out.println("    -url path       : speak text from given URL");
        System.out.println("    -verbose        : verbose output");
        System.out.println("    -version        : shows version number");
        System.out.println("    -voice VOICE    : " + voices);
    }

    /**
     * Starts interactive mode on the given FreeTTS. Reads text
     * from the console and gives it to FreeTTS to speak.
     * terminates on end of file.
     *
     * @param freetts the engine
     */
    private static void interactiveMode(FreeTTS freetts) {
        try {
            while (true) {
                String text;
                BufferedReader reader;
                reader = new BufferedReader(
                        new InputStreamReader(System.in));
                System.out.print("Enter text: ");
                System.out.flush();
                text = reader.readLine();
                if ((text == null) || (text.length() == 0)) {
                    freetts.shutdown();
                    System.exit(0);
                } else {
                    freetts.batchTextToSpeech(text);
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * Dumps the possible audio output file types
     */
    private static void dumpAudioTypes() {
        AudioFileFormat.Type[] types =
                AudioSystem.getAudioFileTypes();

        for (int i = 0; i < types.length; i++) {
            System.out.println(types[i].getExtension());
        }
    }

    /**
     * The main entry point for FreeTTS.
     */
    public static void main(String[] args) {

        String text = null;
        String inFile = null;
        String protocol = null;
        boolean dumpAudioTypes = false;
        Voice voice = null;

        VoiceManager voiceManager = VoiceManager.getInstance();
        String voices = voiceManager.toString();

        // find out what Voice to use first
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-voice")) {
                if (++i < args.length) {
                    String voiceName = args[i];
                    if (voiceManager.contains(voiceName)) {
                        voice = voiceManager.getVoice(voiceName);
                    } else {
                        System.out.println("Invalid voice: " + voiceName);
                        System.out.println
                                ("  Valid voices are " + voices);
                        System.exit(1);
                    }
                } else {
                    usage(voices);
                    System.exit(1);
                }
                break;
            }
        }

        if (voice == null) { // default Voice is kevin16
            voice = voiceManager.getVoice("kevin16");
        }

        if (voice == null) {
            throw new Error("The specified voice is not defined");
        }
        FreeTTS freetts = new FreeTTS(voice);

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-metrics")) {
                voice.setMetrics(true);
            } else if (args[i].equals("-detailedMetrics")) {
                voice.setDetailedMetrics(true);
            } else if (args[i].equals("-silent")) {
                freetts.setSilentMode(true);
            } else if (args[i].equals("-streaming")) {
                freetts.setStreamingAudio(true);
            } else if (args[i].equals("-verbose")) {
                voice.setVerbose(true);
            } else if (args[i].equals("-dumpUtterance")) {
                voice.setDumpUtterance(true);
            } else if (args[i].equals("-dumpAudioTypes")) {
                dumpAudioTypes = true;
            } else if (args[i].equals("-dumpRelations")) {
                voice.setDumpRelations(true);
            } else if (args[i].equals("-dumpASCII")) {
                if (++i < args.length) {
                    voice.setWaveDumpFile(args[i]);
                } else {
                    usage(voices);
                }
            } else if (args[i].equals("-dumpAudio")) {
                if (++i < args.length) {
                    freetts.setAudioFile(args[i]);
                } else {
                    usage(voices);
                }
            } else if (args[i].equals("-dumpMultiAudio")) {
                if (++i < args.length) {
                    freetts.setAudioFile(args[i]);
                    freetts.setMultiAudio(true);
                } else {
                    usage(voices);
                }
            } else if (args[i].equals("-version")) {
                System.out.println(VERSION);
            } else if (args[i].equals("-voice")) {
                // do nothing here, just skip the voice name
                i++;
            } else if (args[i].equals("-help")) {
                usage(voices);
                System.exit(0);
            } else if (args[i].equals("-voiceInfo")) {
                System.out.println(VoiceManager.getInstance().getVoiceInfo());
                System.exit(0);
            } else if (args[i].equals("-text")) {
                freetts.setInputMode(InputMode.TEXT);
                // add the rest of the args as text
                StringBuffer sb = new StringBuffer();
                for (int j = i + 1; j < args.length; j++) {
                    sb.append(args[j]);
                    sb.append(" ");
                }
                text = sb.toString();
                break;
            } else if (args[i].equals("-file")) {
                if (++i < args.length) {
                    inFile = args[i];
                    freetts.setInputMode(InputMode.FILE);
                } else {
                    usage(voices);
                }
            } else if (args[i].equals("-lines")) {
                if (++i < args.length) {
                    inFile = args[i];
                    freetts.setInputMode(InputMode.LINES);
                } else {
                    usage(voices);
                }
            } else if (args[i].equals("-url")) {
                if (++i < args.length) {
                    inFile = args[i];
                    freetts.setInputMode(InputMode.URL);
                } else {
                    usage(voices);
                }
            } else if (args[i].equals("-run")) {
                if (++i < args.length) {
                    voice.setRunTitle(args[i]);
                } else {
                    usage(voices);
                }
            } else {
                System.out.println("Unknown option:" + args[i]);
            }
        }

        if (dumpAudioTypes) {
            dumpAudioTypes();
        }

        freetts.startup();

        if (freetts.getInputMode() == InputMode.TEXT) {
            freetts.batchTextToSpeech(text);
        } else if (freetts.getInputMode() == InputMode.FILE) {
            freetts.fileToSpeech(inFile);
        } else if (freetts.getInputMode() == InputMode.URL) {
            freetts.urlToSpeech(inFile);
        } else if (freetts.getInputMode() == InputMode.LINES) {
            freetts.lineToSpeech(inFile);
        } else {
            interactiveMode(freetts);
        }

        if (freetts.getVoice().isMetrics() &&
                !freetts.getSilentMode()) {
            // [[[ TODO: get first byte timer times back in ]]]
            // freetts.getFirstByteTimer().showTimes();
            // freetts.getFirstSoundTimer().showTimes();
        }

        freetts.shutdown();
        System.exit(0);
    }

    /**
     * Starts this FreeTTS Synthesizer by loading the void and creating
     * a new AudioPlayer.
     */
    public void startup() {
        voice.allocate();
        if (!getSilentMode()) {
            if (audioFile != null) {
                AudioFileFormat.Type type = getAudioType(audioFile);
                if (type != null) {
                    if (multiAudio) {
                        audioPlayer = new
                                MultiFileAudioPlayer(getBasename(audioFile), type);
                    } else
                        audioPlayer = new
                                SingleFileAudioPlayer(getBasename(audioFile), type);
                } else {
                    try {
                        audioPlayer = new RawFileAudioPlayer(audioFile);
                    } catch (IOException ioe) {
                        System.out.println("Can't open " + audioFile +
                                " " + ioe);
                    }
                }
            } else if (!streamingAudio) {
                audioPlayer = new JavaClipAudioPlayer();
            } else {
                try {
                    audioPlayer = voice.getDefaultAudioPlayer();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        if (audioPlayer == null) {
            audioPlayer = new NullAudioPlayer();
        }

        if (false) {
            System.out.println("Using " + audioPlayer);
        }

        voice.setAudioPlayer(audioPlayer);
    }

    /**
     * Returns the audio type based upon the extension of the given
     * file
     *
     * @param file the file of interest
     * @return the audio type of the file or null if it is a
     * non-supported type
     */
    private AudioFileFormat.Type getAudioType(String file) {
        AudioFileFormat.Type[] types =
                AudioSystem.getAudioFileTypes();
        String extension = getExtension(file);

        for (int i = 0; i < types.length; i++) {
            if (types[i].getExtension().equals(extension)) {
                return types[i];
            }
        }
        return null;
    }

    /**
     * Shuts down this FreeTTS synthesizer by closing the AudioPlayer
     * and voice.
     */
    public void shutdown() {
        audioPlayer.close();
        voice.deallocate();
    }

    /**
     * Converts the given text to speech based using processing
     * options currently set in FreeTTS.
     *
     * @param text the text to speak
     * @return true if the utterance was played properly
     */
    public boolean textToSpeech(String text) {
        return voice.speak(text);
    }

    /**
     * Converts the given text to speech based using processing
     * options currently set in FreeTTS.
     *
     * @param text the text to speak
     * @return true if the utterance was played properly
     */
    private boolean batchTextToSpeech(String text) {
        boolean ok;
        voice.startBatch();
        ok = textToSpeech(text);
        voice.endBatch();
        return ok;
    }

    /**
     * Reads the file pointed to by the given path and
     * renders each line as speech individually.
     */
    private boolean lineToSpeech(String path) {
        boolean ok = true;
        voice.startBatch();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;

            while ((line = reader.readLine()) != null && ok) {
                ok = textToSpeech(line);
            }
            reader.close();
        } catch (IOException ioe) {
            voice.error("can't read " + path);
        }
        voice.endBatch();

        return ok;

    }

    /**
     * Returns the voice used by FreeTTS.
     *
     * @return the voice used by freetts
     */
    protected Voice getVoice() {
        return voice;
    }

    /**
     * Converts the text contained in the given stream to speech.
     *
     * @param is the stream containing the text to speak
     */
    public boolean streamToSpeech(InputStream is) {
        boolean ok;
        voice.startBatch();
        ok = voice.speak(is);
        voice.endBatch();
        return ok;
    }

    /**
     * Converts the text contained in the given path to speech.
     *
     * @param urlPath the file containing the text to speak
     * @return true if the utterance was played properly
     */
    public boolean urlToSpeech(String urlPath) {
        boolean ok = false;
        try {
            URL url = new URL(urlPath);
            InputStream is = url.openStream();
            ok = streamToSpeech(is);
        } catch (IOException ioe) {
            System.err.println("Can't read data from " + urlPath);
        }
        return ok;
    }

    /**
     * Converts the text contained in the given path to speech.
     *
     * @param filePath the file containing the text to speak
     * @return true if the utterance was played properly
     */
    public boolean fileToSpeech(String filePath) {
        boolean ok = false;
        try {
            InputStream is = new FileInputStream(filePath);
            ok = streamToSpeech(is);
        } catch (IOException ioe) {
            System.err.println("Can't read data from " + filePath);
        }
        return ok;
    }

    /**
     * Gets silent mode.
     *
     * @return true if in silent mode
     * @see #setSilentMode
     */
    public boolean getSilentMode() {
        return this.silent;
    }

    /**
     * Turns audio playing on and off.
     *
     * @param silent if true, don't play audio
     */
    public void setSilentMode(boolean silent) {
        this.silent = silent;
    }

    /**
     * Returns the InputMode.
     *
     * @return the input mode
     * @see #setInputMode
     */
    public InputMode getInputMode() {
        return this.inputMode;
    }

    /**
     * Sets the input mode.
     *
     * @param inputMode the input mode
     */
    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    /**
     * Sets the audio file .
     *
     * @param audioFile the audioFile
     */
    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * Sets multi audio. If true, and an audio file has been set
     * output will be sent to  multiple files
     *
     * @param multiAudio if <code>true</code> send output to multiple
     *                   files.
     */
    public void setMultiAudio(boolean multiAudio) {
        this.multiAudio = multiAudio;
    }

    /**
     * Sets streaming audio. If true, output will be sent to
     *
     * @param streamingAudio if <code>true</code> stream audio
     */
    public void setStreamingAudio(boolean streamingAudio) {
        this.streamingAudio = streamingAudio;
    }
}

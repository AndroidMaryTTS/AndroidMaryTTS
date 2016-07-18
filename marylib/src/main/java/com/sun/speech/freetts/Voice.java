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
package com.sun.speech.freetts;

import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.lexicon.Lexicon;
import com.sun.speech.freetts.relp.LPCResult;
import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.Text;

/**
 * Performs text-to-speech using a series of
 * <code>UtteranceProcessors</code>. It is the main conduit to the FreeTTS
 * speech synthesizer. It can perform TTS on ASCII text,
 * a JSML document, an <code>InputStream</code>, or a
 * <code>FreeTTSSpeakable</code>, by invoking the method <code>speak</code>.
 * <p/>
 * <p>Before a Voice can perform TTS, it must have a
 * <code>Lexicon</code>, from which it gets the vocabulary, and
 * an <code>AudioPlayer</code>, to which it sends the synthesized output.
 * <p/>
 * <p><b>Example</b> (using the <code>CMUDiphoneVoice</code>,
 * <code>CMULexicon</code> and <code>JavaClipAudioPlayer</code>):
 * <p/>
 * <pre>
 * Voice voice = new CMUDiphoneVoice();
 *
 * // sets the Lexicon
 * voice.setLexicon(new CMULexicon());
 *
 * // sets the AudioPlayer
 * voice.setAudioPlayer(new JavaClipAudioPlayer());
 *
 * // loads the Voice
 * voice.allocate();
 *
 * // start talking
 * voice.speak("I can talk forever without getting tired!");
 * </pre>
 * <p/>
 * <p/>
 * <p>A user can override the AudioPlayer to use by defining the
 * "com.sun.speech.freetts.voice.defaultAudioPlayer" system property.
 * The value of this property must be the name of a class that
 * implements the AudioPlayer interface, and which also has a no-arg
 * constructor.
 *
 * @see VoiceManager
 * @see VoiceDirectory
 */
public abstract class Voice implements UtteranceProcessor, Dumpable {

    /**
     * Constant that describes the name of the unit database used by
     * this voice.
     */
    public final static String DATABASE_NAME = "databaseName";
    /**
     * Prefix for System property names.
     */
    public final static String PROP_PREFIX = "com.sun.speech.freetts.voice.";
    /**
     * Feature name for the silence phone string.
     */
    public final static String FEATURE_SILENCE = "silence";
    /**
     * Feature name for the join type string.
     */
    public final static String FEATURE_JOIN_TYPE = "join_type";
    /**
     * Feature name for the default AudioPlayer class to use.
     */
    public final static String DEFAULT_AUDIO_PLAYER =
            PROP_PREFIX + "defaultAudioPlayer";
    /**
     * The default class to use for the DEFAULT_AUDIO_PLAYER.
     */
    public final static String DEFAULT_AUDIO_PLAYER_DEFAULT =
            "com.sun.speech.freetts.audio.JavaStreamingAudioPlayer";
    private List utteranceProcessors;
    private Map featureProcessors;
    private FeatureSetImpl features;
    private boolean verbose = false;
    private boolean metrics = false;
    private boolean detailedMetrics = false;
    private boolean dumpUtterance = false;
    private boolean dumpRelations = false;
    private String runTitle = "unnamed run";
    private Lexicon lexicon = null;
    private AudioPlayer defaultAudioPlayer = null;
    private AudioPlayer audioPlayer = null;
    private UtteranceProcessor audioOutput;
    private OutputQueue outputQueue = null;
    private String waveDumpFile = null;
    private BulkTimer runTimer = new BulkTimer();
    private BulkTimer threadTimer = new BulkTimer();
    private boolean externalOutputQueue = false;
    private boolean externalAudioPlayer = false;
    private float nominalRate = 150;    // nominal speaking rate for this voice
    private float pitch = 100;        // pitch baseline (hertz)
    private float range = 10;        // pitch range (hertz)
    private float pitchShift = 1;    // F0 Shift
    private float volume = 0.8f;    // the volume (range 0 to 1)
    private float durationStretch = 1f;    // the duration stretch
    private boolean loaded = false;
    private String name = "default_name";
    private Age age = Age.DONT_CARE;
    private Gender gender = Gender.DONT_CARE;
    private String description = "default description";
    private Locale locale = Locale.getDefault();
    private String domain = "general";
    private String style = "standard";
    private String organization = "unknown";


    /**
     * Creates a new Voice. Utterances are sent to an
     * output queue to be rendered as audio.  Utterances are placed
     * on the queue by an output thread. This
     * queue is usually created via a call to 'createOutputThread,'
     * which creates a thread that waits on the queue and sends the
     * output to the audio player associated with this voice. If
     * the queue is null, the output is rendered in the calling
     * thread.
     *
     * @see #createOutputThread
     */
    public Voice() {
        /* Make the utteranceProcessors a synchronized list to avoid
         * some threading issues.
         */
        utteranceProcessors = Collections.synchronizedList(new ArrayList());
        features = new FeatureSetImpl();
        featureProcessors = new HashMap();

        try {
            nominalRate = Float.parseFloat(
                    Utilities.getProperty(PROP_PREFIX + "speakingRate", "150"));
            pitch = Float.parseFloat(
                    Utilities.getProperty(PROP_PREFIX + "pitch", "100"));
            range = Float.parseFloat(
                    Utilities.getProperty(PROP_PREFIX + "range", "10"));
            volume = Float.parseFloat(
                    Utilities.getProperty(PROP_PREFIX + "volume", "1.0"));
        } catch (SecurityException se) {
            // can't get properties, just use defaults
        }
        outputQueue = null;
        audioPlayer = null;
        defaultAudioPlayer = null;
    }

    /**
     * Creates a new Voice like above, except that it also
     * stores the properties of the voice.
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
     * @see #Voice()
     */
    public Voice(String name, Gender gender, Age age,
                 String description, Locale locale, String domain,
                 String organization) {
        this();
        setName(name);
        setGender(gender);
        setAge(age);
        setDescription(description);
        setLocale(locale);
        setDomain(domain);
        setOrganization(organization);
    }

    /**
     * Creates an output thread that will asynchronously
     * output utterances that are generated by this voice (and other
     * voices).
     *
     * @return the queue where utterances should be placed.
     */
    public static OutputQueue createOutputThread() {
        final OutputQueue queue = new OutputQueue();
        Thread t = new Thread() {
            public void run() {
                Utterance utterance = null;
                do {
                    utterance = queue.pend();
                    if (utterance != null) {
                        Voice voice = utterance.getVoice();
                        voice.log("OUT: " + utterance.getString("input_text"));
                        voice.outputUtterance(utterance, voice.threadTimer);
                    }
                } while (utterance != null);
            }
        };
        t.setDaemon(true);
        t.start();
        return queue;
    }

    /**
     * Speaks the given text.
     *
     * @param text the text to speak
     * @return <code>true</code> if the given text is spoken properly;
     * otherwise <code>false</code>
     */
    public boolean speak(String text) {
        return speak(new FreeTTSSpeakableImpl(text));
    }

    /**
     * Speaks the given document.
     *
     * @param doc the JSML document to speak
     * @return <code>true</code> if the given document is spoken properly;
     * otherwise <code>false</code>
     */
    public boolean speak(Document doc) {
        return speak(new FreeTTSSpeakableImpl(doc));
    }

    /**
     * Speaks the input stream.
     *
     * @param inputStream the inputStream to speak
     * @return <code>true</code> if the given input stream is spoken properly;
     * otherwise <code>false</code>
     */
    public boolean speak(InputStream inputStream) {
        return speak(new FreeTTSSpeakableImpl(inputStream));
    }

    /**
     * Speak the given queue item. This is a synchronous method that
     * does not return until the speakable is completely
     * spoken or has been cancelled.
     *
     * @param speakable the item to speak
     * @return <code>true</code> if the utterance was spoken properly,
     * <code>false</code> otherwise
     */
    public boolean speak(FreeTTSSpeakable speakable) {
        log("speak(FreeTTSSpeakable) called");
        boolean ok = true;
        boolean posted = false;

        getAudioPlayer().startFirstSampleTimer();

        for (Iterator i = tokenize(speakable);
             !speakable.isCompleted() && i.hasNext(); ) {
            try {
                Utterance utterance = (Utterance) i.next();
                if (utterance != null) {
                    processUtterance(utterance);
                    posted = true;
                }
            } catch (ProcessException pe) {
                ok = false;
            }
        }
        if (ok && posted) {
            runTimer.start("WaitAudio");
            ok = speakable.waitCompleted();
            runTimer.stop("WaitAudio");
        }
        log("speak(FreeTTSSpeakable) completed");
        return ok;
    }

    /**
     * @deprecated As of FreeTTS 1.2, replaced by {@link #allocate}.
     */
    public void load() {
        allocate();
    }

    /**
     * Allocate this Voice. It loads the lexicon and the
     * audio output handler, and creates an audio output thread by
     * invoking <code>createOutputThread()</code>, if
     * one is not already created. It then calls the <code>loader()</code>
     * method to load Voice-specific data, which include utterance processors.
     */
    public void allocate() {
        if (isLoaded()) {
            return;
        }
        BulkTimer.LOAD.start();


        if (!lexicon.isLoaded()) {
            try {
                lexicon.load();
            } catch (IOException ioe) {
                error("Can't load voice " + ioe);
            }
        }

        try {
            audioOutput = getAudioOutput();
        } catch (IOException ioe) {
            error("Can't load audio output handler for voice " + ioe);
        }
        if (outputQueue == null) {
            outputQueue = createOutputThread();
        }
        try {
            loader();
        } catch (IOException ioe) {
            error("Can't load voice " + ioe);
        }
        BulkTimer.LOAD.stop();
        if (isMetrics()) {
            BulkTimer.LOAD.show("loading " + toString() + " for " +
                    getRunTitle());
        }
        setLoaded(true);
    }

    /**
     * Returns true if this voice is loaded.
     *
     * @return <code>true</code> if the voice is loaded;
     * otherwise <code>false</code>
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Sets the loaded state
     *
     * @param loaded the new loaded state
     *               otherwise <code>false</code>
     */
    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Processes the given Utterance by passing it to each
     * UtteranceProcessor managed by this Voice.  The
     * UtteranceProcessors are called in the order they were added to
     * the Voice.
     *
     * @param u the Utterance to process
     * @throws ProcessException if an exception occurred while performing
     *                          operations on the Utterance
     */
    public void processUtterance(Utterance u) throws ProcessException {
        UtteranceProcessor[] processors;

        if (utteranceProcessors == null) {
            return;
        }
        if (u == null) {
            throw new ProcessException("Utterance is null.");
        }

        runTimer.start("processing");
        processors = new UtteranceProcessor[utteranceProcessors.size()];
        processors = (UtteranceProcessor[])
                utteranceProcessors.toArray(processors);

        log("Processing Utterance: " + u.getString("input_text"));
        try {
            for (int i = 0; i < processors.length &&
                    !u.getSpeakable().isCompleted(); i++) {
                runProcessor(processors[i], u, runTimer);
            }
            if (!u.getSpeakable().isCompleted()) {
                if (outputQueue == null) {
                    log("To AudioOutput");
                    outputUtterance(u, runTimer);
                } else {
                    runTimer.start("..post");
                    outputQueue.post(u);
                    runTimer.stop("..post");
                }
            }
        } catch (ProcessException pe) {
            System.err.println("Processing Utterance: " + pe);
        } catch (Exception e) {
            System.err.println("Trouble while processing utterance " + e);
            e.printStackTrace();
            u.getSpeakable().cancelled();
        }

        log("Done Processing Utterance: " + u.getString("input_text"));
        runTimer.stop("processing");

        if (dumpUtterance) {
            u.dump("Utterance");
        }
        if (dumpRelations) {
            u.dumpRelations("Utterance");
        }

        dumpASCII(u);
    }

    /**
     * Dumps the wave for the given utterance.
     *
     * @param utterance the utterance of interest
     */
    private void dumpASCII(Utterance utterance) {
        if (waveDumpFile != null) {
            LPCResult lpcResult =
                    (LPCResult) utterance.getObject("target_lpcres");
            try {
                if (waveDumpFile.equals("-")) {
                    lpcResult.dumpASCII();
                } else {
                    lpcResult.dumpASCII(waveDumpFile);
                }
            } catch (IOException ioe) {
                error("Can't dump file to " + waveDumpFile + " " + ioe);
            }
        }
    }

    /**
     * Sends the given utterance to the audio output processor
     * associated with this voice. If the queue item associated with
     * this utterance is completed, then this set of utterances has
     * been cancelled or otherwise aborted and the utterance should
     * not be output.
     *
     * @param utterance the utterance to be output
     * @param timer     the timer for gathering performance metrics
     * @return true if the utterance was output properly; otherwise
     * false
     */
    private boolean outputUtterance(Utterance utterance, BulkTimer timer) {
        boolean ok = true;
        FreeTTSSpeakable speakable = utterance.getSpeakable();

        if (!speakable.isCompleted()) {
            if (utterance.isFirst()) {
                getAudioPlayer().reset();
                speakable.started();
                log(" --- started ---");
            }

            // log("   utt: " + utterance.getString("input_text"));
            try {
                if (!speakable.isCompleted()) {
                    runProcessor(audioOutput, utterance, timer);
                } else {
                    ok = false;
                }
            } catch (ProcessException pe) {
                ok = false;
            }
            if (ok && utterance.isLast()) {
                getAudioPlayer().drain();
                speakable.completed();
                log(" --- completed ---");
            } else if (!ok) {
                // getAudioPlayer().drain();
                speakable.cancelled();
                log(" --- cancelled ---");
            } else {
                log(" --- not last: " + speakable.getText() + " --- ");
            }
            log("Calling speakable.completed() on " + speakable.getText());
        } else {
            ok = false;
            log("STRANGE: speakable already completed: " + speakable.getText());
        }
        return ok;
    }


    /**
     * Runs the given utterance processor.
     *
     * @param processor the processor to run.   If the processor
     *                  is null, it is ignored
     * @param utterance the utterance to process
     * @throws ProcessException if an exceptin occurs while processing
     *                          the utterance
     */
    private void runProcessor(UtteranceProcessor processor,
                              Utterance utterance, BulkTimer timer)
            throws ProcessException {
        if (processor != null) {
            String processorName = ".." + processor.toString();
            log("   Running " + processorName);
            timer.start(processorName);
            processor.processUtterance(utterance);
            timer.stop(processorName);
        }
    }


    /**
     * Returns the tokenizer associated with this voice.
     *
     * @return the tokenizer
     */
    public abstract Tokenizer getTokenizer();


    /**
     * Return the list of UtteranceProcessor instances.  Applications
     * should use this to obtain and modify the contents of the
     * UtteranceProcessor list.
     *
     * @return a List containing UtteranceProcessor instances
     */
    public List getUtteranceProcessors() {
        return utteranceProcessors;
    }


    /**
     * Returns the feature set associated with this voice.
     *
     * @return the feature set.
     */
    public FeatureSet getFeatures() {
        return features;
    }


    /**
     * Starts a batch of utterances. Utterances are sometimes
     * batched in groups for timing purposes.
     *
     * @see #endBatch
     */
    public void startBatch() {
        runTimer.setVerbose(detailedMetrics);
        runTimer.start();
    }


    /**
     * Ends a batch of utterances.
     *
     * @see #startBatch
     */
    public void endBatch() {
        runTimer.stop();

        if (metrics) {
            runTimer.show(getRunTitle() + " run");
            threadTimer.show(getRunTitle() + " thread");
            getAudioPlayer().showMetrics();
            long totalMemory = Runtime.getRuntime().totalMemory();
            System.out.println
                    ("Memory Use    : "
                            + (totalMemory - Runtime.getRuntime().freeMemory()) / 1024
                            + "k  of " + totalMemory / 1024 + "k");
        }
    }

    /**
     * Returns the output queue associated with this voice.
     *
     * @return the output queue associated with this voice
     */
    public OutputQueue getOutputQueue() {
        return outputQueue;
    }

    /**
     * Sets the output queue for this voice. If no output queue is set
     * for the voice when the voice is loaded, a queue and thread will
     * be created when the voice is loaded.  If the outputQueue is set
     * by an external entity by calling setOutputQueue, the caller is
     * responsible for shutting down the output thread. That is, if
     * you call 'setOutputQueue' then you are responsible for shutting
     * down the output thread on your own. This is necessary since the
     * output queue may be shared by a number of voices.
     * <p/>
     * <p>Utterances are placed on the
     * queue to be output by an output thread. This queue is
     * usually created via a call to 'createOutputThread' which
     * creates a thread that waits on the queue and sends the
     * output to the audio player associated with this voice. If
     * the queue is null, the output is rendered in the calling
     * thread.
     *
     * @param queue the output queue
     */
    public void setOutputQueue(OutputQueue queue) {
        externalOutputQueue = true;
        outputQueue = queue;
    }

    /**
     * Loads voice specific data. Subclasses of voice should
     * implement this to perform class specific loading.
     */
    protected abstract void loader() throws IOException;

    /**
     * tokenizes the given the queue item.
     *
     * @return an iterator that will yield a series of utterances
     */
    private Iterator tokenize(FreeTTSSpeakable speakable) {
        return new FreeTTSSpeakableTokenizer(speakable).iterator();
    }

    /**
     * Converts the document to a string (a placeholder for more
     * sophisticated logic to be done).
     *
     * @param dom the jsml document
     * @return the document as a string.
     */
    private String documentToString(Document dom) {
        StringBuffer buf = new StringBuffer();
        linearize(dom, buf);
        return buf.toString();
    }

    /**
     * Appends the text for this node to the given StringBuffer.
     *
     * @param n   the node to traverse in depth-first order
     * @param buf the buffer to append text to
     */
    private void linearize(Node n, StringBuffer buf) {
        StringBuffer endText = processNode(n, buf);
        for (Node child = n.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            linearize(child, buf);
        }

        if (endText != null) {
            buf.append(endText);
        }
    }

    /**
     * Adds text for just this node and returns any text that might
     * be needed to undo the effects of this node after it is
     * processed.
     *
     * @param n   the node to traverse in depth-first order
     * @param buf the buffer to append text to
     * @return a <code>String</code> containing text to undo the
     * effects of the node
     */
    protected StringBuffer processNode(Node n, StringBuffer buf) {
        StringBuffer endText = null;

        int type = n.getNodeType();
        switch (type) {
            case Node.ATTRIBUTE_NODE:
                break;

            case Node.DOCUMENT_NODE:
                break;

            case Node.ELEMENT_NODE:
                // endText = processElement((Element) n, buf);
                break;

            case Node.TEXT_NODE:
                buf.append(((Text) n).getData());
                break;

            // Pass processing instructions (e.g., <?blah?>
            // right on to the synthesizer.  These types of things
            // probably should not be used.  Instead the 'engine'
            // element is probably the best thing to do.
            //
            case Node.PROCESSING_INSTRUCTION_NODE:
                break;

            // The document type had better be JSML.
            //
            case Node.DOCUMENT_TYPE_NODE:
                break;

            // I think NOTATION nodes are only DTD's.
            //
            case Node.NOTATION_NODE:
                break;

            // Should not get COMMENTS because the JSMLParser
            // ignores them.
            //
            case Node.COMMENT_NODE:
                break;

            // Should not get CDATA because the JSMLParser is
            // coalescing.
            //    
            case Node.CDATA_SECTION_NODE:
                break;

            // Should not get ENTITY related notes because
            // entities are expanded by the JSMLParser
            //
            case Node.ENTITY_NODE:
            case Node.ENTITY_REFERENCE_NODE:
                break;

            // Should not get DOCUMENT_FRAGMENT nodes because I
            // [[[WDW]]] think they are only created via the API's
            // and cannot be defined via content.
            //
            case Node.DOCUMENT_FRAGMENT_NODE:
                break;

            default:
                break;
        }

        return endText;
    }

    /**
     * Dumps the voice in textual form.
     *
     * @param output where to send the formatted output
     * @param pad    the initial padding
     * @param title  the title to print when dumping out
     */
    public void dump(PrintWriter output, int pad, String title) {
        Utilities.dump(output, pad, title);
        features.dump(output, pad + 4, title + " Features");
        dumpProcessors(output, pad + 4, title + " Processors");
    }


    /**
     * Dumps the voice processors.
     *
     * @param output where to send the formatted output
     * @param pad    the initial padding
     * @param title  the title to print when dumping out
     */
    public void dumpProcessors(PrintWriter output, int pad, String title) {
        UtteranceProcessor[] processors;
        if (utteranceProcessors == null) {
            return;
        }

        processors = new UtteranceProcessor[utteranceProcessors.size()];
        processors = (UtteranceProcessor[])
                utteranceProcessors.toArray(processors);

        Utilities.dump(output, pad, title);
        for (int i = 0; i < processors.length; i++) {
            Utilities.dump(output, pad + 4, processors[i].toString());
        }
    }


    /**
     * Returns a language/voice specific Feature Processor.
     *
     * @param name the name of the processor
     * @return the processor associated with the name or null if none
     * could be found
     */
    public FeatureProcessor getFeatureProcessor(String name) {
        return (FeatureProcessor) featureProcessors.get(name);
    }

    /**
     * Adds a language/voice specific Feature Processor to the set of
     * FeatureProcessors supported by this voice.
     *
     * @param name the name of the processor
     * @param fp   the processor
     */
    public void addFeatureProcessor(String name, FeatureProcessor fp) {
        featureProcessors.put(name, fp);
    }

    /**
     * Prints the given message to <code>System.out</code> if "verbose"
     * operation is enabled.
     *
     * @see #setVerbose
     * @see #isVerbose
     */
    public void log(String message) {
        if (verbose) {
            System.out.println(toString() + ": " + message);
        }
    }

    /**
     * Sends the given warning message to stderr.
     */
    public void warn(String message) {
        System.err.println("Warning: " + message);
    }

    /**
     * Sends the given error message to stderr and also throws
     * an error exception.
     */
    public void error(String message) {
        System.err.println("Error: " + message);
        throw new Error(message);
    }

    /**
     * Gets the state of the verbose mode.
     *
     * @return true if verbose mode is on
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Sets the verbose mode.
     *
     * @param verbose true if verbose mode should be on
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
        log("Verbose mode is " + verbose);
    }

    /**
     * Gets the state of the metrics mode.
     *
     * @return true if metrics mode is on
     */
    public boolean isMetrics() {
        return metrics;
    }

    /**
     * Sets the metrics mode.
     *
     * @param metrics true if metrics mode should be on
     */
    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
        log("Metrics mode is " + metrics);
    }

    /**
     * Gets the state of the detailedMetrics mode.
     *
     * @return true if detailedMetrics mode is on
     */
    public boolean isDetailedMetrics() {
        return detailedMetrics;
    }

    /**
     * Sets the state of the detailedMetrics mode.
     *
     * @param detailedMetrics true if detailedMetrics mode should be on
     */
    public void setDetailedMetrics(boolean detailedMetrics) {
        this.detailedMetrics = detailedMetrics;
        log("DetailedMetrics mode is " + detailedMetrics);
    }

    /**
     * Gets the state of the dumpUtterance mode.
     *
     * @return true if dumpUtterance mode is on
     */
    public boolean isDumpUtterance() {
        return dumpUtterance;
    }

    /**
     * Sets the state of the dumpUtterance mode.
     *
     * @param dumpUtterance true if dumpUtterance mode should be on
     */
    public void setDumpUtterance(boolean dumpUtterance) {
        this.dumpUtterance = dumpUtterance;
        log("DumpUtterance mode is " + dumpUtterance);
    }

    /**
     * Gets the state of the dumpRelations mode.
     *
     * @return true if dumpRelations mode is on
     */
    public boolean isDumpRelations() {
        return dumpRelations;
    }

    /**
     * Sets the state of the dumpRelations mode.
     *
     * @param dumpRelations true if dumpRelations mode should be on
     */
    public void setDumpRelations(boolean dumpRelations) {
        this.dumpRelations = dumpRelations;
        log("DumpRelations mode is " + dumpRelations);
    }

    /**
     * Gets the title for this run.
     *
     * @return the title for the run
     */
    public String getRunTitle() {
        return runTitle;
    }

    /**
     * Sets the title for this run.
     *
     * @param runTitle the title for the run
     */
    public void setRunTitle(String runTitle) {
        this.runTitle = runTitle;
    }

    /**
     * Given a phoneme and a feature name, returns the feature.
     *
     * @param phone       the phoneme of interest
     * @param featureName the name of the feature of interest
     * @return the feature with the given name
     */
    public String getPhoneFeature(String phone, String featureName) {
        return null;
    }

    /**
     * Shuts down the voice processing.
     */
    public void deallocate() {
        setLoaded(false);

        if (!externalAudioPlayer) {
            if (audioPlayer != null) {
                audioPlayer.close();
                audioPlayer = null;
            }
        }

        if (!externalOutputQueue) {
            outputQueue.close();
        }
    }

    /**
     * Retreives the baseline pitch.
     *
     * @return the baseline pitch in hertz
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the baseline pitch.
     *
     * @param hertz the baseline pitch in hertz
     */
    public void setPitch(float hertz) {
        this.pitch = hertz;
    }

    /**
     * Gets the pitch range.
     *
     * @return the range in hertz
     */
    public float getPitchRange() {
        return range;
    }

    /**
     * Sets the pitch range.
     *
     * @param range the range in hertz
     */
    public void setPitchRange(float range) {
        this.range = range;
    }

    /**
     * Gets the pitch shift.
     *
     * @return the pitch shift
     */
    public float getPitchShift() {
        return pitchShift;
    }

    /**
     * Sets the pitch shift
     *
     * @param shift the pitch shift (1.0 is no shift)
     */
    public void setPitchShift(float shift) {
        this.pitchShift = shift;
    }

    /**
     * Gets the duration Stretch
     *
     * @return the duration stretch
     */
    public float getDurationStretch() {
        return durationStretch;
    }

    /**
     * Sets the duration  stretch
     *
     * @param stretch the duration stretch (1.0 is no stretch)
     */
    public void setDurationStretch(float stretch) {
        this.durationStretch = stretch;
    }

    /**
     * Gets the rate of speech.
     *
     * @return words per minute
     */
    public float getRate() {
        return durationStretch * nominalRate;
    }

    /**
     * Sets the rate of speech.
     *
     * @param wpm words per minute
     */
    public void setRate(float wpm) {
        if (wpm > 0 && wpm < 1000) {
            setDurationStretch(nominalRate / wpm);
        }
    }

    /**
     * Gets the volume.
     *
     * @return the volume (0 to 1.0)
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the volume.
     *
     * @param vol the volume (0 to 1.0)
     */
    public void setVolume(float vol) {
        volume = vol;
    }

    /**
     * Gets the lexicon for this voice.
     *
     * @return the lexicon (or null if there is no lexicon)
     */
    public Lexicon getLexicon() {
        return lexicon;
    }

    /**
     * Sets the lexicon to be used by this voice.
     *
     * @param lexicon the lexicon to use
     */
    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;

    }

    /**
     * Gets the dumpfile for this voice.
     *
     * @return the dumpfile
     */
    public String getWaveDumpFile() {
        return waveDumpFile;
    }

    /**
     * Sets the dumpfile for this voice.
     *
     * @param waveDumpFile the dumpfile
     */
    public void setWaveDumpFile(String waveDumpFile) {
        this.waveDumpFile = waveDumpFile;
    }

    private void traceStack() {
        new Throwable().printStackTrace();
    }

    /**
     * Gets the default audio player for this voice.  The return
     * value will be non-null only if the DEFAULT_AUDIO_PLAYER
     * system property has been set to the name of an AudioPlayer
     * class, and that class is able to be instantiated via a
     * no arg constructor.  getAudioPlayer will automatically set
     * the audio player for this voice to the default audio player
     * if the audio player has not yet been set.
     *
     * @return the default AudioPlayer
     * @see #DEFAULT_AUDIO_PLAYER
     * @see #getAudioPlayer
     */
    public AudioPlayer getDefaultAudioPlayer() throws InstantiationException {
        if (defaultAudioPlayer != null) {
            return defaultAudioPlayer;
        }

        String className = Utilities.getProperty(
                DEFAULT_AUDIO_PLAYER, DEFAULT_AUDIO_PLAYER_DEFAULT);

        try {
            Class cls = Class.forName(className);
            defaultAudioPlayer = (AudioPlayer) cls.newInstance();
            return defaultAudioPlayer;
        } catch (ClassNotFoundException e) {
            throw new InstantiationException("Can't find class " + className);
        } catch (IllegalAccessException e) {
            throw new InstantiationException("Can't find class " + className);
        } catch (ClassCastException e) {
            throw new InstantiationException(className + " cannot be cast "
                    + "to AudioPlayer");
        }
    }

    /**
     * Gets the audio player associated with this voice.  If the
     * audio player has not yet been set, the value will default
     * to the return value of getDefaultAudioPlayer.
     *
     * @return the audio player
     * @see #getDefaultAudioPlayer
     */
    public AudioPlayer getAudioPlayer() {
        if (audioPlayer == null) {
            try {
                audioPlayer = getDefaultAudioPlayer();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return audioPlayer;
    }

    /**
     * Sets the audio player associated with this voice. The caller is
     * responsible for closing this player.
     *
     * @param player the audio player
     */
    public void setAudioPlayer(AudioPlayer player) {
        audioPlayer = player;
        externalAudioPlayer = true;
    }

    /**
     * Get a resource for this voice.
     * By default, the voice is searched for in the package
     * to which the voice class belongs. Subclasses are free to
     * override this behaviour.
     */
    protected URL getResource(String resource) {
        //return this.getClass().getResource(resource);
        return Utilities.getResourceURL(resource);
    }

    /**
     * Get the name of this voice.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this voice.
     * [[[TODO: any standard format to the name?]]]
     *
     * @param name the name to assign this voice
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this Voice.
     *
     * @return the name of this Voice
     */
    public String toString() {
        return getName();
    }

    /**
     * Get the gender of this voice.
     *
     * @return the gender of this voice
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Set the gender of this voice.
     *
     * @param gender the gender to assign
     */
    protected void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Get the age of this voice.
     *
     * @return the age of this voice
     */
    public Age getAge() {
        return age;
    }

    /**
     * Set the age of this voice.
     *
     * @param age the age to assign
     */
    protected void setAge(Age age) {
        this.age = age;
    }

    /**
     * Get the description of this voice.
     *
     * @return the human readable description of this voice
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this voice.
     *
     * @param description the human readable description to assign
     */
    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the locale of this voice.
     *
     * @return the locale of this voice.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the locale of this voice.
     *
     * @param locale the locale of this voice.
     */
    protected void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Get the domain of this voice.
     *
     * @return the domain of this voice.  For example,
     * &quot;general&quot;, &quot;time&quot;, or
     * &quot;weather&quot;.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set the domain of this voice.
     *
     * @param domain the domain of this voice.  For example,
     *               &quot;general&quot;, &quot;time&quot;, or
     *               &quot;weather&quot;.
     */
    protected void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets the voice style. This parameter is designed for human
     * interpretation. Values might include "business", "casual",
     * "robotic", "breathy".
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the voice style. This parameter is designed for human
     * interpretation. Values might include "business", "casual",
     * "robotic", "breathy"
     *
     * @param style the stile of this voice.
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Gets the organization which created this voice.  For example
     * "cmu", "sun", ...
     *
     * @return the name of the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the organization which created this voice.  For example
     * "cmu", "sun", ...
     *
     * @param organization the name of the organization
     */
    protected void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * Returns the AudioOutput processor to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     *
     * @return the audio output processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    protected abstract UtteranceProcessor getAudioOutput() throws IOException;

    /**
     * Tokenizes a FreeTTSSpeakable
     */
    private class FreeTTSSpeakableTokenizer {
        FreeTTSSpeakable speakable;
        Tokenizer tok = getTokenizer();

        /**
         * Constructor.
         *
         * @param speakable the queue item to be pretokenized
         */
        public FreeTTSSpeakableTokenizer(FreeTTSSpeakable speakable) {
            this.speakable = speakable;
            if (speakable.isPlainText()) {
                tok.setInputText(speakable.getText());
            } else if (speakable.isStream()) {
                Reader reader = new BufferedReader(
                        new InputStreamReader(speakable.getInputStream()));
                tok.setInputReader(reader);
            } else if (speakable.isDocument()) {
                tok.setInputText(documentToString(speakable.getDocument()));
            }
        }

        /**
         * Returns an iterator for this text item.
         */
        public Iterator iterator() {
            return new Iterator() {
                boolean first = true;
                Token savedToken = null;

                /**
                 * Determines if there are more utterances
                 *
                 * @return true if there are more tokens
                 */
                public boolean hasNext() {
                    return savedToken != null || tok.hasMoreTokens();
                }

                /**
                 * Returns the next utterance.
                 *
                 * @return the next utterance (as an object) or
                 *    null if there is are no utterances left
                 */
                public Object next() {
                    ArrayList tokenList = new ArrayList();
                    Utterance utterance = null;

                    if (savedToken != null) {
                        tokenList.add(savedToken);
                        savedToken = null;
                    }

                    while (tok.hasMoreTokens()) {
                        Token token = tok.getNextToken();
                        if ((token.getWord().length() == 0) ||
                                (tokenList.size() > 500) ||
                                tok.isBreak()) {
                            savedToken = token;
                            break;
                        }
                        tokenList.add(token);
                    }
                    utterance = new Utterance(Voice.this, tokenList);
                    utterance.setSpeakable(speakable);
                    utterance.setFirst(first);
                    first = false;
                    boolean isLast =
                            (!tok.hasMoreTokens() &&
                                    (savedToken == null ||
                                            savedToken.getWord().length() == 0));
                    utterance.setLast(isLast);
                    return utterance;
                }

                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }
            };
        }
    }
}

  




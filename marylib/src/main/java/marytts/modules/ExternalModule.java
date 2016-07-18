/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.modules;

//import org.apache.log4j.Logger;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Locale;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioSystem;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.exceptions.NoSuchPropertyException;
import marytts.modules.synthesis.Voice;
import marytts.server.Mary;
import marytts.server.MaryProperties;
import marytts.util.io.StreamLogger;


/**
 * A base class for all external Mary modules.
 * Provides non-specific input/output functionality for
 * communication with an external module.
 * <p/>
 * Any external module extending this class will need to implement
 * a constructor calling this class's constructor.
 * If data input/output requires additional processing, the subclass
 * may override
 * <code>externalIO()</code>, <code>open()</code> and/or <code>close()</code>.
 * <p/>
 * Example for a subclass:
 * <p>Default case (external module reads from stdin and writes to stdout
 * without requiring any particular triggers):
 * <pre>
 * public class Intonation extends ExternalModule
 * {
 *     public Intonation()
 *     {
 *         super("Intonation",
 *               System.getProperty("mary.base") +
 *               File.separator + "src" +
 *               File.separator + "modules" +
 *               File.separator + "intonation" +
 *               File.separator + "intonation_tts",
 *               MaryDataType.INTONISED
 *               );
 *     }
 * }
 * </pre>
 * Non-standard case (external module needs special trigger, data needs to
 * be post-processed):
 * <pre>
 * public class Tokeniser extends ExternalModule
 * {
 *     public Tokeniser()
 *     {
 *         super(...);
 *     }
 *
 *     protected MaryData externalIO(MaryData d)
 *         throws TransformerConfigurationException, TransformerException,
 *                FileNotFoundException, IOException,
 *                ParserConfigurationException, SAXException, Exception
 *     {
 *         MaryData result;
 *         // Write to and read from external module similarly to super class,
 *         // but write e.g. an empty line after writing the data,
 *         // to mark end of input.
 *
 *         // Modify the result tree
 *         myModifications(result);
 *
 *         return result;
 *     }
 * }
 * </pre>
 *
 * @author Marc Schr&ouml;der
 */

public class ExternalModule implements MaryModule {
    protected int state;
    protected Process process;
    protected OutputStream to;
    protected InputStream from;
    protected StreamLogger errorLogger;
    protected ProcessingThread processingThread = null;
    protected RestarterThread restarterThread = null;
    /**
     * The duration given to the module before timeout occurs
     * (in milliseconds).
     */
    protected long timeLimit;
    /**
     * Remember if a retry attempt is undertaken in <code>process()</code>.
     *
     * @see #process
     */
    protected boolean retrying = false;
    /**
     * A regular expression describing what to be ignored in the
     * external module's standard error output. Default is <code>null</code>.
     */
    protected String ignorePattern = null;
    private String name;
    private String cmd;
    private MaryDataType inputType;
    private MaryDataType outputType;
    private Locale locale;

    /**
     * The logger instance to be used by this module.
     * It will identify the origin of the log message in the log file.
     */
    private LinkedList requestQueue;
    private boolean exitRequested = false;
    private boolean needToRestart = false;

    protected ExternalModule(String name, String cmd, MaryDataType inputType, MaryDataType outputType, Locale locale)
            throws NoSuchPropertyException {
        // Exceptions ocurring at this stage should make the program abort.
        this.name = name;
        this.cmd = cmd;
        this.inputType = inputType;
        this.outputType = outputType;
        this.locale = locale;
        this.timeLimit = MaryProperties.needInteger("modules.timeout");
        this.requestQueue = new LinkedList();
        this.state = MODULE_OFFLINE;
    }

    /**
     * Get the process object representing the external module program.
     */
    protected Process getProcess() {
        return process;
    }

    /**
     * Execute the command <code>cmd</code> as an external process.
     * The process's input and output streams are accessible from then
     * on via the <code>from()</code> and <code>to()</code> methods;
     * the process's error stream is logged by a separate
     * <code>StreamLogger</code> thread.
     *
     * @see #to()
     * @see #from()
     * @see marytts.util.io.StreamLogger
     */
    protected void open()
            throws IOException {
        assert cmd != null;
        process = Runtime.getRuntime().exec(cmd);
        // Workaround for Java 1.4.1 bug:
        if (System.getProperty("java.vendor").startsWith("Sun") &&
                System.getProperty("java.version").startsWith("1.4.1")) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        to = process.getOutputStream();
        from = process.getInputStream();
        errorLogger = new StreamLogger(process.getErrorStream(),
                name() + " err",
                ignorePattern);
        errorLogger.start();
    }

    /**
     * Closes the external process's input and output streams,
     * and destroys the process.
     */
    protected void close() {
        try {
            if (to != null)
                to.close();
            if (from != null)
                from.close();
            // ErrorLogger will die when it reads end-of-file.
        } catch (IOException e) {
        }
        if (process != null)
            process.destroy();
        process = null;
        to = null;
        from = null;
        errorLogger = null;
    }

    /**
     * The stream on which data is written to the external process.
     */
    protected OutputStream to() {
        return to;
    }

    /**
     * The stream on which data is read from the external process.
     */
    protected InputStream from() {
        return from;
    }

    /**
     * The command line to execute as an external process.
     */
    protected String cmd() {
        return cmd;
    }

    /**
     * Sets the command line to execute.
     */
    protected void setCmd(String cmd) {
        this.cmd = cmd;
    }

    // Interface MaryModule implementation:
    @Override
    public String name() {
        return name;
    }

    @Override
    public MaryDataType inputType() {
        return inputType;
    }

    @Override
    public MaryDataType outputType() {
        return outputType;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public synchronized void startup() throws Exception {
        assert state == MODULE_OFFLINE;
        setExitRequested(false);
        open();
        setNeedToRestart(false);

        Log.i(Mary.LOG, "Module started (" + inputType() + "->" + outputType() + ", locale " + getLocale() + ").");
        state = MODULE_RUNNING;
    }

    /**
     * Perform a power-on self test by processing some example input data.
     *
     * @throws Error if the module does not work properly.
     */
    @Override
    public synchronized void powerOnSelfTest() throws Error {
        assert state == MODULE_RUNNING;
        Log.i(Mary.LOG, "Starting power-on self test.");
        try {
            MaryData in = new MaryData(inputType, getLocale());
            String example = inputType.exampleText(getLocale());
            if (example != null) {
                in.readFrom(new StringReader(example));
                if (outputType.equals(MaryDataType.get("AUDIO")))
                    in.setAudioFileFormat(new AudioFileFormat(
                                    AudioFileFormat.Type.WAVE, Voice.AF22050, AudioSystem.NOT_SPECIFIED)
                    );
                process(in);
            } else {
                Log.i(Mary.LOG, "1 No example text -- no power-on self test!");
            }
        } catch (Throwable t) {
            throw new Error("Module " + name + ": Power-on self test failed.", t);
        }
        Log.i(Mary.LOG, "Power-on self test complete.");
    }

    @Override
    public void shutdown() {
        assert state == MODULE_RUNNING;
        close();
        setExitRequested(true);
        doNotifyAll();
        try {
            processingThread.join();
            restarterThread.join();
        } catch (InterruptedException e) {
            Log.i(Mary.LOG, e.toString());
        }
        Log.i(Mary.LOG, "Module shut down.");
        state = MODULE_OFFLINE;
    }

    /**
     * The actual external input and output.
     * Write to the module and read from the module in the appropriate
     * ways as determined by input and output data types.
     */
    protected MaryData externalIO(MaryData d)
            throws
            Exception {
        assert !needToRestart();
        Log.i(Mary.LOG, "Writing to module.");
        d.writeTo(to());
        // Read from external module
        Log.i(Mary.LOG, "Reading from module.");
        MaryData result = new MaryData(outputType(), d.getLocale());
        result.readFrom(from(), outputType().endMarker());
        Log.i(Mary.LOG, "Read complete.");
        return result;
    }


    /**
     * Feed the input data into the external module, and return the result.
     * This method is responsible for the timer and timeout handling and is
     * regarded as generic for all external modules, thus <code>final</code>.
     * The actual input and output is performed by <code>externalIO()</code>
     * and may be overridden by subclasses to account for module-specifics.
     * <p/>
     * If timeout occurs, the external module is restarted, and a second attempt
     * is made. If it fails again, an IOException is thrown.
     * Even in case of the second failure, the external process is restarted,
     * because failure may have been provoked by this particular input.
     * <p/>
     * For the time being, external modules are thread-safe simply by
     * this method being <code>synchronized</code>.
     *
     * @return A MaryData object of type
     * <code>outputType()</code> encapsulating the processing result.
     */
    @Override
    public final MaryData process(MaryData d)
            throws
            Exception {
        assert state == MODULE_RUNNING;
        Log.i(Mary.LOG, "Adding request");
        ExternalModuleRequest request = new ExternalModuleRequest(d);
        addRequest(request);
        // In the case that the processor thread is in wait state,
        // wake it up:
        doNotifyAll();
        Log.i(Mary.LOG, "Now waiting for request to be processed");
        long tStart = System.currentTimeMillis();
        while (!request.problemOccurred() &&
                request.getOutput() == null &&
                System.currentTimeMillis() - tStart < timeLimit) {
            doWait(timeLimit);
        }
        if (request.getOutput() == null) {
            if (request.problemOccurred()) {
                Log.i(Mary.LOG, "Problem occurred. Rescheduling request.");
            } else {
                Log.i(Mary.LOG, "Timeout occurred. Requesting module restart and rescheduling request.");
                // We trigger the restart of the module:
                setNeedToRestart(true);
            }
            removeRequest(request);
            request.setProblemOccurred(false);
            addRequest(request);
            doNotifyAll();
            Log.i(Mary.LOG, "Waiting for request to be processed (2nd try)");
            tStart = System.currentTimeMillis();
            while (!request.problemOccurred() &&
                    request.getOutput() == null &&
                    System.currentTimeMillis() - tStart < timeLimit) {
                doWait(timeLimit);
            }
            if (request.getOutput() == null) {
                if (request.problemOccurred()) {
                    Log.i(Mary.LOG, "Problem occurred again. Giving up.");
                } else {
                    Log.i(Mary.LOG, "Timeout occurred again. Requesting module restart, but giving up on this request.");
                    // We trigger the restart of the module:
                    setNeedToRestart(true);
                }
                removeRequest(request);
                throw new IOException("Module " + name() + " cannot process.");
            }
        }
        Log.i(Mary.LOG, "Request processed");



        /*        } catch (Exception e) {
            String reason;
            if (timer.didDestroy()) {
                reason = "Timeout";
            } else {
                reason = "Problem";
            }
            // Remedy for all Exceptions: try to restart the external module.
            if (retrying) { // second failure - abort
                if (timer.didDestroy()) {
                    logger.error(reason + " occurred again. Giving up.");
                }
                shutdown();
                startup();
                retrying = false;
                // at least we are now ready for a different call
                // (problem might have been due to this particular input)
                throw e;
            } else { // first failure for this processing attempt
                logger.error(reason +
                             " occurred during I/O with external module. " +
                             "Trying to restart module.", e);
                shutdown();
                startup();
                timeLimit = 2 * timeLimit;
                retrying = true; // marker for recursive call to process()
                return process(d);
            }
        }
        */
        return request.getOutput();
    }

    protected synchronized void addRequest(Object r) {
        requestQueue.addLast(r);
    }

    protected synchronized ExternalModuleRequest getNextRequest() {
        return (ExternalModuleRequest) requestQueue.removeFirst();
    }

    protected synchronized void removeRequest(ExternalModuleRequest r) {
        requestQueue.remove(r);
    }

    protected synchronized boolean haveWaitingRequests() {
        return !requestQueue.isEmpty();
    }

    protected synchronized void setNeedToRestart(boolean needToRestart) {
        this.needToRestart = needToRestart;
    }

    protected synchronized boolean needToRestart() {
        return needToRestart;
    }

    protected synchronized void doNotifyAll() {
        notifyAll();
    }

    protected synchronized void doWait() {
        try {
            wait();
        } catch (InterruptedException e) {
            Log.i(Mary.LOG, e.toString());
        }
    }

    protected synchronized void doWait(long millis) {
        try {
            wait(millis);
        } catch (InterruptedException e) {
            Log.i(Mary.LOG, e.toString());
        }
    }

    /**
     * Tell all helper threads to exit.
     */
    protected synchronized void setExitRequested(boolean b) {
        exitRequested = b;
    }

    protected synchronized boolean exitRequested() {
        return exitRequested;
    }


    public class ProcessingThread extends Thread {
        @Override
        public void run() {
            while (!exitRequested()) {
                while (needToRestart()) {
                    // Wait until restarter thread has finished restarting
                    Log.i(Mary.LOG, "ProcessingThread waiting for restart.");
                    doWait();
                    if (!needToRestart())
                        Log.i(Mary.LOG, "ProcessingThread noticed restart done.");
                }
                if (haveWaitingRequests()) {
                    ExternalModuleRequest request = getNextRequest();
                    Log.i(Mary.LOG, "Now processing next request.");
                    try {
                        MaryData output = externalIO(request.getInput());
                        request.setOutput(output);
                        doNotifyAll(); // let them know we're done
                    } catch (Exception e) {
                        Log.e(Mary.LOG, "Problem occurred during I/O with external module. " +
                                "Requesting module restart.", e);
                        // Let whoever scheduled this request decide whether
                        // they want to reschedule it:
                        request.setProblemOccurred(true);
                        // We trigger the restart of the module:
                        setNeedToRestart(true);
                        doNotifyAll();
                    }
                } else { // wait for something to process
                    Log.i(Mary.LOG, "Currently nothing to do, waiting");
                    doWait();
                    Log.i(Mary.LOG, "ProcessingThread was woken up.");
                }
            }
        }
    }

    public class RestarterThread extends Thread {
        protected static final int MAX_RESTART_ATTEMPTS = 3;

        @Override
        public void run() {
            // Avoid eternal retries if restarting does not succeed --
            // only retry once in a row:
            int nrFailuresRestarting = 0;
            while (!exitRequested()) {
                if (needToRestart()) {
                    if (nrFailuresRestarting < MAX_RESTART_ATTEMPTS) {
                        Log.i(Mary.LOG, "Restarting module.");
                        try {
                            close();
                            open();
                            setNeedToRestart(false);
                            Log.i(Mary.LOG, "Module restarted");
                            doNotifyAll();
                            nrFailuresRestarting = 0; // succeeded
                        } catch (Exception e) {
                            Log.e(Mary.LOG, "Problem restarting.", e);
                            nrFailuresRestarting++;
                        }
                    } else {
                        Log.e(Mary.LOG, "Restarting has failed " +
                                nrFailuresRestarting +
                                " times, giving up.");
                        setNeedToRestart(false);
                        // Maybe it will work again another time?
                        nrFailuresRestarting = 0;
                        doNotifyAll();
                    }
                } else { // wait for something to process
                    Log.i(Mary.LOG, "Currently no need to restart, waiting");
                    doWait();
                    Log.i(Mary.LOG, "RestarterThread was woken up.");
                }
            }
        }
    }

}


package lib.com.sun.media.sound;

import java.util.Vector;


/**
 * Thread to manage the inner loop of the audio engine.
 * <p/>
 * The start() method of this class is called by the beatnik engine.
 *
 * @author David Rivas, Chris Schardt, Kara Kytle
 * @author Matthias Pfisterer
 * @version %I% %E%
 */
class MixerThread extends Thread {

    // STATIC VARIABLES

    /**
     * Vector of all created instances of this class.
     */
    private static Vector threadObjects = new Vector();

    private static String threadName = "Headspace mixer frame proc thread";


    // INSTANCE VARIABLES

    /**
     * True after runNative() returns.
     * False if the thread is running in the native code.
     * When true, causes this thread to repeatedly call wait(), just after runNative().
     */
    private boolean paused;

    private long frameProc;


    /**
     * Private constructor, invoked by our getNewThreadObject method
     */
    // $$jb:06.24.99: taking the frameProc argument out of this constructor
    // to simplify the 1.2 security privileged block in getNewThreadObject.
    //  private MixerThread() {
    protected MixerThread(ThreadGroup threadGroup) {
        super(threadGroup, "");
        if (Printer.trace) Printer.trace(">> MixerThread() CONSTRUCTOR");
        paused = false;
        if (Printer.trace) Printer.trace(">> MixerThread() CONSTRUCTOR completed");
    }

    private static MixerThread getExistingThreadObject(long frameProc) {

        if (Printer.trace)
            Printer.trace(">> MixerThread: getExistingThreadObject(" + frameProc + ")");

        MixerThread currentThreadObject;

        synchronized (threadObjects) {

            for (int i = 0; i < threadObjects.size(); i++) {

                currentThreadObject = (MixerThread) threadObjects.elementAt(i);

                if (currentThreadObject.frameProc == frameProc) {

                    if (Printer.trace)
                        Printer.trace("<< MixerThread: getExistingThreadObject() returning existing object: " + currentThreadObject);
                    return currentThreadObject;
                }
            }
        }

        return null;
    }

    private static MixerThread getNewThreadObject(long frameProc) {
        if (Printer.trace) Printer.trace(">> MixerThread: getNewThreadObject(" + frameProc + ")");
        MixerThread mixerThread = JSSecurityManager.newMixerThread(threadName);
        mixerThread.setFrameProc(frameProc);
        threadObjects.addElement(mixerThread);

        return mixerThread;
    }

    /*
     * private setter - $$jb:06.24.99: was an argument to private constructor,
     * but I split it to simplify the 1.2 security privileged block in
     * getNewThreadObject
     */
    private void setFrameProc(long frameProc) {
        this.frameProc = frameProc;
    }

    public void run() {

        if (Printer.trace) Printer.trace(">> MixerThread: run()");

        while (true) {

            if (Printer.debug) Printer.debug("MixerThread: run(): calling runNative()");
            runNative(frameProc);
            if (Printer.debug) Printer.debug("MixerThread: run(): runNative() returned");

            synchronized (this) {

                paused = true;

                // repeatedly call wait() until a call to unpause()
                while (paused) {

                    try {
                        if (Printer.debug) Printer.debug("MixerThread: run(): calling wait()");
                        wait();     // paused gets cleared here
                        if (Printer.debug)
                            Printer.debug("MixerThread: run(): returned from wait()");
                    } catch (InterruptedException e) {
                        if (Printer.debug) Printer.debug("MixerThread: run(): wait() interrupted");
                    }
                }

                if (Printer.debug) Printer.debug("MixerThread: run(): exited while(paused)");
            }

            if (Printer.debug) Printer.debug("MixerThread: run(): exited synchronized block");
        }
    }


    // Causes the while loop above to continue by returning from its wait() call
    private synchronized void unpause() {

        if (Printer.trace) Printer.trace(">> MixerThread: unpause() called, notifying...");
        paused = false;
        notify();
        if (Printer.trace) Printer.trace("<< MixerThread: unpause() completed");
    }


    // Processes frames of audio data
    // Returns after HAE_ReleaseAudioCard() is called
    // native private void runNative();
    native private void runNative(long frameProc);
}
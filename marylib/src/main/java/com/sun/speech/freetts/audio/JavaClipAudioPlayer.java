/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.audio;

import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioSystem;
import lib.sound.sampled.Clip;
import lib.sound.sampled.DataLine;
import lib.sound.sampled.FloatControl;
import lib.sound.sampled.LineEvent;
import lib.sound.sampled.LineListener;
import lib.sound.sampled.LineUnavailableException;

/**
 * Provides an implementation of <code>AudioPlayer</code> that creates
 * javax.sound.sampled audio clips and outputs them via the
 * javax.sound API.  The interface provides a highly reliable audio
 * output package. Since audio is batched and not sent to the audio
 * layer until an entire utterance has been processed, this player has
 * higher latency (50 msecs for a typical 4 second utterance).
 * <p/>
 * The system property:
 * <code>
 * com.sun.speech.freetts.audio.AudioPlayer.debug;
 * </code> if set to <code>true</code> will cause this
 * class to emit debugging information (useful to a developer).
 */
public class JavaClipAudioPlayer implements AudioPlayer {

    private volatile boolean paused;
    private volatile boolean cancelled = false;
    private volatile Clip currentClip;
    private Object clipLock = new Object();

    private float volume = 1.0f;  // the current volume
    private boolean debug = false;
    private boolean audioMetrics = false;
    private BulkTimer timer = new BulkTimer();
    private AudioFormat defaultFormat = // default format is 8khz
            new AudioFormat(8000f, 16, 1, true, true);
    private AudioFormat currentFormat = defaultFormat;
    private boolean firstSample = true;
    private boolean firstPlay = true;
    private int curIndex = 0;
    private byte[] outputData;
    private LineListener lineListener = new JavaClipLineListener();

    private long drainDelay;
    private long openFailDelayMs;
    private long totalOpenFailDelayMs;


    /**
     * Constructs a default JavaClipAudioPlayer
     */
    public JavaClipAudioPlayer() {
        debug = Utilities.getBoolean
                ("com.sun.speech.freetts.audio.AudioPlayer.debug");
        drainDelay = Utilities.getLong
                ("com.sun.speech.freetts.audio.AudioPlayer.drainDelay",
                        150L).longValue();
        openFailDelayMs = Utilities.getLong
                ("com.sun.speech.freetts.audio.AudioPlayer.openFailDelayMs",
                        0).longValue();
        totalOpenFailDelayMs = Utilities.getLong
                ("com.sun.speech.freetts.audio.AudioPlayer.totalOpenFailDelayMs",
                        0).longValue();
        audioMetrics = Utilities.getBoolean
                ("com.sun.speech.freetts.audio.AudioPlayer.showAudioMetrics");
        setPaused(false);
    }

    /**
     * Retrieves the audio format for this player
     *
     * @return format the audio format
     */
    public AudioFormat getAudioFormat() {
        return currentFormat;
    }

    /**
     * Sets the audio format for this player
     *
     * @param format the audio format
     * @throws UnsupportedOperationException if the line cannot be opened with
     *                                       the given format
     */
    public synchronized void setAudioFormat(AudioFormat format) {
        currentFormat = format;
    }

    /**
     * Pauses audio output.   All audio output is
     * stopped. Output can be resumed at the
     * current point by calling <code>resume</code>. Output can be
     * aborted by calling <code> cancel </code>
     */
    public void pause() {
        if (!paused) {
            setPaused(true);
            if (currentClip != null) {
                currentClip.stop();
            }
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * Resumes playing audio after a pause.
     */
    public synchronized void resume() {
        if (paused) {
            setPaused(false);
            if (currentClip != null) {
                currentClip.start();
            }
            notifyAll();
        }
    }

    /**
     * Cancels all queued audio. Any 'write' in process will return
     * immediately false.
     */
    public void cancel() {
        if (audioMetrics) {
            timer.start("audioCancel");
        }
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
        }
        synchronized (this) {
            cancelled = true;
            paused = false;
            notifyAll();
        }
        if (audioMetrics) {
            timer.stop("audioCancel");
            timer.getTimer("audioCancel").showTimesShortTitle("");
            timer.getTimer("audioCancel").showTimesShort(0);
        }
    }

    /**
     * Prepares for another batch of output. Larger groups of output
     * (such as all output associated with a single FreeTTSSpeakable)
     * should be grouped between a reset/drain pair.
     */
    public synchronized void reset() {
        timer.start("speakableOut");
    }

    /**
     * Waits for all queued audio to be played
     *
     * @return <code>true</code> if the write completed successfully,
     * <code> false </code>if the write was cancelled.
     */
    public boolean drain() {
        timer.stop("speakableOut");
        return true;
    }

    /**
     * Closes this audio player
     * <p/>
     * [[[ WORKAROUND TODO
     * The javax.sound.sampled drain is almost working properly.  On
     * linux, there is still a little bit of sound that needs to go
     * out, even after drain is called. Thus, the drainDelay. We
     * wait for a few hundred milliseconds while the data is really
     * drained out of the system
     * ]]]
     */
    public synchronized void close() {
        if (currentClip != null) {
            currentClip.drain();
            if (drainDelay > 0L) {
                try {
                    Thread.sleep(drainDelay);
                } catch (InterruptedException e) {
                }
            }
            currentClip.close();
        }
        notifyAll();
    }

    /**
     * Returns the current volume.
     *
     * @return the current volume (between 0 and 1)
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the current volume.
     *
     * @param volume the current volume (between 0 and 1)
     */
    public void setVolume(float volume) {
        if (volume > 1.0f) {
            volume = 1.0f;
        }
        if (volume < 0.0f) {
            volume = 0.0f;
        }
        this.volume = volume;
        if (currentClip != null) {
            setVolume(currentClip, volume);
        }
    }


    /**
     * Sets pause mode
     *
     * @param state true if we are paused
     */
    private void setPaused(boolean state) {
        paused = state;
    }


    /**
     * Sets the volume on the given clip
     *
     * @param line the line to set the volume on
     * @param vol  the volume (range 0 to 1)
     */
    private void setVolume(Clip clip, float vol) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = volumeControl.getMaximum() -
                    volumeControl.getMinimum();
            volumeControl.setValue(vol * range + volumeControl.getMinimum());
        }
    }


    /**
     * Returns the current position in the output stream since the
     * last <code>resetTime</code>
     * <p/>
     * Currently not supported.
     *
     * @return the position in the audio stream in milliseconds
     */
    public synchronized long getTime() {
        return -1L;
    }


    /**
     * Resets the time for this audio stream to zero
     */
    public synchronized void resetTime() {
    }


    /**
     * Starts the output of a set of data. Audio data for a single
     * utterance should be grouped between begin/end pairs.
     *
     * @param size the size of data between now and the end
     */
    public synchronized void begin(int size) {
        timer.start("utteranceOutput");
        cancelled = false;
        curIndex = 0;
        outputData = new byte[size];
    }

    /**
     * Marks the end a set of data. Audio data for a single utterance should
     * be grouped between begin/end pairs.
     *
     * @return <code>true</code> if the audio was output properly,
     * <code>false </code> if the output was cancelled
     * or interrupted.
     */
    public synchronized boolean end() {
        boolean ok = true;

        while (paused && !cancelled) {
            try {
                wait();
            } catch (InterruptedException ie) {
                return false;
            }
        }

        if (cancelled) {
            return false;
        }

        timer.start("clipGeneration");

        DataLine.Info info = new DataLine.Info(Clip.class, currentFormat);

        boolean opened = false;
        long totalDelayMs = 0;
        do {
            // keep trying to open the clip until the specified
            // delay is exceeded
            try {
                currentClip = (Clip) AudioSystem.getLine(info);
                currentClip.addLineListener(lineListener);
                currentClip.open
                        (currentFormat, outputData, 0, outputData.length);
                opened = true;
            } catch (LineUnavailableException lue) {
                System.err.println("LINE UNAVAILABLE: " +
                        "Format is " + currentFormat);
                try {
                    Thread.sleep(openFailDelayMs);
                    totalDelayMs += openFailDelayMs;
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        } while (!opened && totalDelayMs < totalOpenFailDelayMs);

        if (!opened) {
            close();
            ok = false;
        } else {
            setVolume(currentClip, volume);
            if (audioMetrics && firstPlay) {
                firstPlay = false;
                timer.stop("firstPlay");
                timer.getTimer("firstPlay").showTimesShortTitle("");
                timer.getTimer("firstPlay").showTimesShort(0);
            }
            currentClip.start();
            try {
                // wait for audio to complete
                while (currentClip != null &&
                        (currentClip.isRunning() || paused) && !cancelled) {
                    wait();
                }
            } catch (InterruptedException ie) {
                ok = false;
            }
            close();
        }

        timer.stop("clipGeneration");
        timer.stop("utteranceOutput");
        ok &= !cancelled;
        return ok;
    }


    /**
     * Writes the given bytes to the audio stream
     *
     * @param audioData audio data to write to the device
     * @return <code>true</code> if the write completed successfully,
     * <code> false </code>if the write was cancelled.
     */
    public boolean write(byte[] audioData) {
        return write(audioData, 0, audioData.length);
    }

    /**
     * Writes the given bytes to the audio stream
     *
     * @param bytes  audio data to write to the device
     * @param offset the offset into the buffer
     * @param size   the size into the buffer
     * @return <code>true</code> if the write completed successfully,
     * <code> false </code>if the write was cancelled.
     */
    public boolean write(byte[] bytes, int offset, int size) {
        if (firstSample) {
            firstSample = false;
            timer.stop("firstAudio");
            if (audioMetrics) {
                timer.getTimer("firstAudio").showTimesShortTitle("");
                timer.getTimer("firstAudio").showTimesShort(0);
            }
        }
        System.arraycopy(bytes, offset, outputData, curIndex, size);
        curIndex += size;
        return true;
    }


    /**
     * Returns the name of this audio player
     *
     * @return the name of the audio player
     */
    public String toString() {
        return "JavaClipAudioPlayer";
    }


    /**
     * Outputs the given msg if debugging is enabled for this
     * audio player.
     */
    private void debugPrint(String msg) {
        if (debug) {
            System.out.println(toString() + ": " + msg);
        }
    }

    /**
     * Shows metrics for this audio player
     */
    public void showMetrics() {
        timer.show(toString());
    }

    /**
     * Starts the first sample timer
     */
    public void startFirstSampleTimer() {
        timer.start("firstAudio");
        firstSample = true;
        if (audioMetrics) {
            timer.start("firstPlay");
            firstPlay = true;
        }
    }


    /**
     * Provides a LineListener for this clas.
     */
    private class JavaClipLineListener implements LineListener {

        /**
         * Implements update() method of LineListener interface. Responds
         * to the line events as appropriate.
         *
         * @param event the LineEvent to handle
         */
        public void update(LineEvent event) {
            if (event.getType().equals(LineEvent.Type.START)) {
                debugPrint("Event  START");
            } else if (event.getType().equals(LineEvent.Type.STOP)) {
                debugPrint("Event  STOP");
                synchronized (JavaClipAudioPlayer.this) {
                    JavaClipAudioPlayer.this.notifyAll();
                }
            } else if (event.getType().equals(LineEvent.Type.OPEN)) {
                debugPrint("Event OPEN");
            } else if (event.getType().equals(LineEvent.Type.CLOSE)) {
                // When a clip is closed we no longer need it, so
                // set currentClip to null and notify anyone who may
                // be waiting on it.
                debugPrint("EVNT CLOSE");
                synchronized (JavaClipAudioPlayer.this) {
                    // currentClip = null;
                    JavaClipAudioPlayer.this.notifyAll();
                }
            }
        }
    }
}

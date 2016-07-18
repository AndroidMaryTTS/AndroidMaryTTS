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
package com.sun.speech.freetts.relp;

import com.sun.speech.freetts.FreeTTSSpeakable;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.util.Utilities;
import com.sun.speech.freetts.util.WaveUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;

import lib.sound.sampled.AudioFormat;


/**
 * Contains the result of linear predictive coding processing.
 */
public class LPCResult {

    private static final double POST_EMPHASIS = 0.0;
    private final static int MAX_SAMPLE_SIZE =
            Utilities.getInteger("com.sun.speech.freetts.LpcResult.maxSamples",
                    1024).intValue();
    /**
     * Given a residual, maps it using WaveUtils.ulawToShort() to a float.
     */
    private final static float[] residualToFloatMap = new float[256];

    static {
        for (short i = 0; i < residualToFloatMap.length; i++) {
            residualToFloatMap[i] = (float) WaveUtils.ulawToShort(i);
        }
        residualToFloatMap[128] = (float) WaveUtils.ulawToShort((short) 255);
    }

    private int frameSize = 10;
    private int numberOfFrames = 0;
    private short[][] frames = null;
    private int[] times = null;
    private int[] sizes = null;
    /**
     * this is a normalized version of the residuals; to normalize it,
     * add 128 to it
     */
    private byte[] residuals = null;
    private int numberOfChannels;
    private int sampleRate;
    private int residualFold;
    private float lpcMinimum;
    private float lpcRange;


    public LPCResult() {
        residualFold = 1;
    }

    /**
     * Given a 16 bit value (represented as an int), extract
     * the high eight bits and return them
     *
     * @param val the 16 bit value
     * @return the high eight bits
     */
    private final static byte hibyte(int val) {
        return (byte) (val >>> 8);
    }

    /**
     * Given a 16 bit value (represented as an int), extract
     * the low eight bits and return them
     *
     * @param val the 16 bit value
     * @return the low eight bits
     */
    private final static byte lobyte(int val) {
        return (byte) (val & 0x000000FF);
    }

    /**
     * Resets the number of frames in this LPCResult.
     *
     * @param numberOfFrames the number of frames in this LPC result
     */
    public void resizeFrames(int numberOfFrames) {
        times = new int[numberOfFrames];
        frames = new short[numberOfFrames][];
        sizes = new int[numberOfFrames];
        this.numberOfFrames = numberOfFrames;
    }

    /**
     * Resets the number of residuals, and initialize all of them to 255
     * (which is 0 for mulaw).
     *
     * @param numberOfSamples the number of samples in this LPC result
     */
    public void resizeResiduals(int numberOfSamples) {
        residuals = new byte[numberOfSamples];
    }

    /**
     * A convenience method for setting the LPC values.
     *
     * @param numberOfChannels the number of channels
     * @param sampleRate       the sample rate
     * @param lpcMin           the LPC minimum
     * @param lpcRange         the LPC range
     */
    public void setValues(int numberOfChannels,
                          int sampleRate,
                          int residualFold,
                          float lpcMin, float lpcRange) {
        this.numberOfChannels = numberOfChannels;
        this.sampleRate = sampleRate;
        this.lpcMinimum = lpcMin;
        this.lpcRange = lpcRange;
    }

    /**
     * Returns the time difference of the frame at the given position
     * with the frame prior to that. If the frame at the given position is
     * the first frame (position 0), the time of that frame is returned.
     *
     * @param frameIndex the position of the frame
     * @return the time difference of the frame at the given position
     * with the frame prior to that
     */
    public int getFrameShift(int frameIndex) {
        if (0 <= frameIndex && frameIndex < times.length) {
            if (frameIndex > 0) {
                return times[frameIndex] - times[frameIndex - 1];
            } else {
                return times[frameIndex];
            }
        } else {
            return 0;
        }
    }

    /**
     * Returns the sizes of frames in this LPC.
     *
     * @return the sizes of frames
     */
    public int getFrameSize() {
        return frameSize;
    }

    /**
     * Sets the sizes of frames in this LPC to the given size.
     *
     * @param frameSize the new frame size
     */
    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    /**
     * Returns the frame at the given index.
     *
     * @param index the index of interest
     * @return the frame at the given index
     */
    public short[] getFrame(int index) {
        return frames[index];
    }

    /**
     * Returns the array of times.
     *
     * @return the array of times
     */
    public int[] getTimes() {
        return times;
    }

    /**
     * Sets the array of times.
     *
     * @param times the times data
     */
    public void setTimes(int[] times) {
        this.times = times;
    }

    /**
     * Returns the number of frames in this LPCResult.
     *
     * @return the number of frames
     */
    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    /**
     * Sets the number of frames in this LPC Result.
     *
     * @param numberFrames the number of frames in this result
     */
    public void setNumberOfFrames(int numberFrames) {
        this.numberOfFrames = numberFrames;
    }

    /**
     * Returns the number of channels in this LPCResult.
     *
     * @return the number of channels
     */
    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    /**
     * Sets the number of channels.
     *
     * @param numberOfChannels the number of channels
     */
    public void setNumberOfChannels(int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    /**
     * Returns the LPC minimum.
     *
     * @return the LPC minimum
     */
    public float getLPCMin() {
        return lpcMinimum;
    }

    /**
     * Sets the LPC minimum.
     *
     * @param min the LPC minimum
     */
    public void setLPCMin(float min) {
        this.lpcMinimum = min;
    }

    /**
     * Returns the LPC range.
     *
     * @return the LPC range
     */
    public float getLPCRange() {
        return lpcRange;
    }

    /**
     * Sets the LPC range.
     *
     * @param range the LPC range
     */
    public void setLPCRange(float range) {
        this.lpcRange = range;
    }

    /**
     * Returns the number of samples in this LPC result
     *
     * @return the number of samples
     */
    public int getNumberOfSamples() {
        if (residuals == null) {
            return 0;
        } else {
            return residuals.length;
        }
    }

    /**
     * Returns the sample rate.
     *
     * @return the sample rate
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Sets the sample rate.
     *
     * @param rate the sample rate
     */
    public void setSampleRate(int rate) {
        this.sampleRate = rate;
    }

    /**
     * Returns the array of residuals sizes.
     *
     * @return the array of residuals sizes
     */
    public int[] getResidualSizes() {
        return sizes;
    }

    /**
     * Sets the array of residual sizes.
     *
     * @param sizes the new residual sizes
     */
    public void setResidualSizes(int[] sizes) {
        for (int i = 0; i < this.sizes.length && i < sizes.length; i++) {
            this.sizes[i] = sizes[i];
        }
    }

    /**
     * Returns the array of residuals.
     *
     * @return the array of residuals
     */
    public byte[] getResiduals() {
        return residuals;
    }

    /**
     * Sets the frame at the given index.
     *
     * @param index     the position of the frame to set
     * @param newFrames new frame data
     */
    public void setFrame(int index, short[] newFrames) {
        frames[index] = newFrames;
    }

    /**
     * Copies the information in the given unit to the array of residuals,
     * starting at the given index, up until targetSize chars.
     *
     * @param source         the unit that holds the information source
     * @param targetPosition start position in the array of residuals
     * @param targetSize     the maximum number of characters to copy
     */
    public void copyResiduals(byte[] source,
                              int targetPosition,
                              int targetSize) {
        int unitSize = source.length;
        if (unitSize < targetSize) {
            int targetStart = (targetSize - unitSize) / 2;
            System.arraycopy(source, 0,
                    residuals, targetPosition + targetStart,
                    source.length);
        } else {
            int sourcePosition = (unitSize - targetSize) / 2;
            System.arraycopy(source, sourcePosition,
                    residuals, targetPosition,
                    targetSize);
        }
    }

    /**
     * Copies the residual puse in the given unit to the array of residuals,
     * starting at the given index, up until targetSize chars.
     *
     * @param source         the unit that holds the information source
     * @param targetPosition start position in the array of residuals
     * @param targetSize     the maximum number of characters to copy
     */
    public void copyResidualsPulse(byte[] source,
                                   int targetPosition, int targetSize) {
        int unitSize = source.length;
        short sample = (short) (source[0] + 128);
        if (unitSize < targetSize) {
            residuals[(targetSize - unitSize) / 2] = WaveUtils.shortToUlaw(sample);
        } else {
            residuals[(unitSize - targetSize) / 2] = WaveUtils.shortToUlaw(sample);
        }
    }

    /**
     * Synthesize a Wave  from this LPCResult
     *
     * @return the wave
     */
    public boolean playWave(AudioPlayer player, Utterance utterance) {
        return playWaveSamples(player, utterance.getSpeakable(),
                getNumberOfSamples() * 2);
    }


    public byte[] getWaveSamples() {
        return getWaveSamples(2 * getNumberOfSamples(), null);
    }

    /**
     * get the samples for this utterance
     *
     * @param numberSamples the number of samples desirred
     * @param utterance     the utterance
     *                      <p/>
     *                      [[[ TODO: well there is a bunch of duplicated code here ..
     *                      these should be combined into one routine.
     *                      ]]]
     */
    private byte[] getWaveSamples(int numberSamples,
                                  Utterance utterance) {
        int numberChannels = getNumberOfChannels();
        int pmSizeSamples;
        float pp = 0;

        byte[] samples = new byte[numberSamples];
        byte[] residuals = getResiduals();
        int[] residualSizes = getResidualSizes();

        FloatList outBuffer = FloatList.createList(numberChannels + 1);
        FloatList lpcCoefficients = FloatList.createList(numberChannels);

        double multiplier = (double) getLPCRange() / 65535.0;
        int s = 0;
        boolean firstPlay = true;

        // for each frame in the LPC result
        for (int r = 0, i = 0; i < numberOfFrames; i++) {

            // unpack the LPC coefficients
            short[] frame = getFrame(i);

            FloatList lpcCoeffs = lpcCoefficients;
            for (int k = 0; k < numberChannels; k++) {
                lpcCoeffs.value = (float) ((frame[k] + 32768.0)
                        * multiplier) + lpcMinimum;
                lpcCoeffs = lpcCoeffs.next;
            }

            pmSizeSamples = residualSizes[i];

            // resynthesis the signal, pmSizeSamples ~= 90
            // what's in the loop is done for each residual
            for (int j = 0; j < pmSizeSamples; j++, r++) {

                FloatList backBuffer = outBuffer.prev;
                float ob = residualToFloatMap[residuals[r] + 128];

                lpcCoeffs = lpcCoefficients;
                do {
                    ob += lpcCoeffs.value * backBuffer.value;
                    backBuffer = backBuffer.prev;
                    lpcCoeffs = lpcCoeffs.next;
                } while (lpcCoeffs != lpcCoefficients);

                int sample = (int) (ob + (pp * POST_EMPHASIS));
                samples[s++] = hibyte(sample);
                samples[s++] = lobyte(sample);


                outBuffer.value = pp = ob;
                outBuffer = outBuffer.next;
            }
        }
        return samples;
    }

    /**
     * Play the sample data on the given player
     *
     * @param player        where to send the audio
     * @param numberSamples the number of samples
     */
    private boolean playWaveSamples(AudioPlayer player,
                                    FreeTTSSpeakable speakable,
                                    int numberSamples) {
        boolean ok = true;
        int numberChannels = getNumberOfChannels();
        int pmSizeSamples;
        float pp = 0;

        byte[] samples = new byte[MAX_SAMPLE_SIZE];
        byte[] residuals = getResiduals();
        int[] residualSizes = getResidualSizes();

        FloatList outBuffer = FloatList.createList(numberChannels + 1);
        FloatList lpcCoefficients = FloatList.createList(numberChannels);

        double multiplier = (double) getLPCRange() / 65535.0;
        int s = 0;
        boolean firstPlay = true;

        // for each frame in the LPC result
        player.begin(numberSamples);
        for (int r = 0, i = 0;
             (ok &= !speakable.isCompleted()) &&
                     i < numberOfFrames; i++) {

            // unpack the LPC coefficients
            short[] frame = getFrame(i);

            FloatList lpcCoeffs = lpcCoefficients;
            for (int k = 0; k < numberChannels; k++) {
                lpcCoeffs.value = (float) ((frame[k] + 32768.0)
                        * multiplier) + lpcMinimum;
                lpcCoeffs = lpcCoeffs.next;
            }

            pmSizeSamples = residualSizes[i];

            // resynthesis the signal, pmSizeSamples ~= 90
            // what's in the loop is done for each residual
            for (int j = 0; j < pmSizeSamples; j++, r++) {

                FloatList backBuffer = outBuffer.prev;
                float ob = residualToFloatMap[residuals[r] + 128];

                lpcCoeffs = lpcCoefficients;
                do {
                    ob += lpcCoeffs.value * backBuffer.value;
                    backBuffer = backBuffer.prev;
                    lpcCoeffs = lpcCoeffs.next;
                } while (lpcCoeffs != lpcCoefficients);

                int sample = (int) (ob + (pp * POST_EMPHASIS));
                samples[s++] = hibyte(sample);
                samples[s++] = lobyte(sample);

                if (s >= MAX_SAMPLE_SIZE) {
                    if ((ok &= !speakable.isCompleted()) &&
                            !player.write(samples)) {
                        ok = false;
                    }
                    s = 0;
                }

                outBuffer.value = pp = ob;
                outBuffer = outBuffer.next;
            }
        }

        // write out the very last samples
        if ((ok &= !speakable.isCompleted()) && s > 0) {
            ok = player.write(samples, 0, s);
            s = 0;
        }

        // tell the AudioPlayer it is the end of Utterance
        if (ok &= !speakable.isCompleted()) {
            ok = player.end();
        }

        return ok;
    }

    /**
     * Dumps this LPCResult to standard out
     */
    public void dump() {
        dump(new OutputStreamWriter(System.out));
    }

    /**
     * Dumps this LPCResult to the given stream.
     *
     * @param writer the output stream
     */
    public void dump(Writer writer) {
        DecimalFormat numberFormat = new DecimalFormat();
        numberFormat.setMaximumFractionDigits(6);
        numberFormat.setMinimumFractionDigits(6);
        PrintWriter pw = new PrintWriter(new BufferedWriter(writer));

        if (getNumberOfFrames() == 0) {
            pw.println("# ========== LPCResult ==========");
            pw.println("# Num_of_Frames: " + getNumberOfFrames());
            pw.flush();
            return;
        }
        pw.println("========== LPCResult ==========");
        pw.println("Num_of_Frames: " + getNumberOfFrames());
        pw.println("Num_of_Channels: " + getNumberOfChannels());
        pw.println("Num_of_Samples: " + getNumberOfSamples());
        pw.println("Sample_Rate: " + sampleRate);
        pw.println("LPC_Minimum: " + numberFormat.format(lpcMinimum));
        pw.println("LPC_Range: " + numberFormat.format(lpcRange));
        pw.println("Residual_Fold: " + residualFold);
        pw.println("Post_Emphasis: " + numberFormat.format(POST_EMPHASIS));

        int i;
        pw.print("Times:\n");
        for (i = 0; i < getNumberOfFrames(); i++) {
            pw.print(times[i] + " ");
        }
        pw.print("\nFrames: ");
        for (i = 0; i < getNumberOfFrames(); i++) {
            // for each frame, print all elements
            short[] frame = getFrame(i);
            for (int j = 0; j < frame.length; j++) {
                pw.print((((int) frame[j]) + 32768) + "\n");
            }
        }
        pw.print("\nSizes: ");
        for (i = 0; i < getNumberOfFrames(); i++) {
            pw.print(sizes[i] + " ");
        }
        pw.print("\nResiduals: ");
        for (i = 0; i < getNumberOfSamples(); i++) {
            if (residuals[i] == 0) {
                pw.print(255);
            } else {
                pw.print((((int) residuals[i]) + 128));
            }
            pw.print("\n");
            pw.flush();
        }
        pw.flush();
    }


    /**
     * Dumps the wave data associated with this result
     */
    public void dumpASCII() {
        dumpASCII(new OutputStreamWriter(System.out));
    }

    /**
     * Dumps the wave data associated with this result
     *
     * @param path the path where the wave data is appended to
     * @throws IOException if an IO error occurs
     */
    public void dumpASCII(String path) throws IOException {
        Writer writer = new FileWriter(path, true);
        getWave().dump(writer);
    }

    /**
     * Synthesize a Wave  from this LPCResult
     *
     * @return the wave
     */
    private Wave getWave() {
        // construct a new wave object
        AudioFormat audioFormat = new AudioFormat
                (getSampleRate(),
                        Wave.DEFAULT_SAMPLE_SIZE_IN_BITS, 1,
                        Wave.DEFAULT_SIGNED, true);
        return new Wave(audioFormat,
                getWaveSamples(getNumberOfSamples() * 2, null));
    }

    /**
     * Dumps the wave out to the given stream
     *
     * @param writer the output stream
     */
    public void dumpASCII(Writer writer) {
        Wave wave = getWave();
        wave.dump(writer);
    }

    /**
     * A Wave is an immutable class that contains the AudioFormat and
     * the actual wave samples, which currently is in the form
     * of AudioInputStream.
     */
    private static class Wave {
        /**
         * The default sample size of the Wave, which is 16.
         */
        public static final int DEFAULT_SAMPLE_SIZE_IN_BITS = 16;

        /**
         * A boolean indicating that the Wave is signed, i.e.,
         * this value is true.
         */
        public static final boolean DEFAULT_SIGNED = true;

        /**
         * A boolean indicating that the Wave samples are represented as
         * little endian, i.e., this value is false.
         */
        public static final boolean DEFAULT_BIG_ENDIAN = false;


        private byte[] samples = null;
        private AudioFormat audioFormat = null;

        /**
         * Constructs a Wave with the given audio format and wave samples.
         *
         * @param audioFormat the audio format of the wave
         * @param samples     the wave samples
         */
        Wave(AudioFormat audioFormat, byte[] samples) {
            this.audioFormat = audioFormat;
            this.samples = samples;
        }


        /**
         * Dumps the wave out to the given stream
         *
         * @param writer the output stream
         */
        public void dump(Writer writer) {
            PrintWriter pw = new PrintWriter(new BufferedWriter(writer));
            pw.println("#========== Wave ==========");
            pw.println("#Type: NULL");
            pw.println("#Sample_Rate: " + (int) audioFormat.getSampleRate());
            pw.println("#Num_of_Samples: " + samples.length / 2);
            pw.println("#Num_of_Channels: " + audioFormat.getChannels());
            if (samples != null) {
                for (int i = 0; i < samples.length; i += 2) {
                    pw.println(
                            WaveUtils.bytesToShort(samples[i], samples[i + 1]));
                }
            }
            pw.flush();
        }
    }
}


/**
 * FloatList is used to maintain a circular buffer of float values.
 * It is essentially an index-free array of floats that can easily be
 * iterated through forwards or backwards. Keeping values in an index
 * free list like this eliminates index bounds checking which can
 * save us some time.
 */
class FloatList {
    float value;
    FloatList next;
    FloatList prev;

    /**
     * Creates a new node
     */
    FloatList() {
        value = 0.0F;
        next = null;
        prev = null;
    }

    /**
     * Creates a circular list of nodes of the given size
     *
     * @param size the number of nodes in the list
     * @return an entry in the list.
     */
    static FloatList createList(int size) {
        FloatList prev = null;
        FloatList first = null;

        for (int i = 0; i < size; i++) {
            FloatList cur = new FloatList();
            cur.prev = prev;
            if (prev == null) {
                first = cur;
            } else {
                prev.next = cur;
            }
            prev = cur;
        }
        first.prev = prev;
        prev.next = first;

        return first;
    }

    /**
     * prints out the contents of this list
     *
     * @param title the title of the dump
     * @param list  the list to dump
     */
    static void dump(String title, FloatList list) {
        System.out.println(title);

        FloatList cur = list;
        do {
            System.out.println("Item: " + cur.value);
            cur = cur.next;
        } while (cur != list);
    }
}






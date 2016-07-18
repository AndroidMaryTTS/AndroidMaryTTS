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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Represents the frame and residual data
 * used by the diphone database
 * used Residual Excited Linear Predictive synthesizer
 */
public class SampleSet {
    private Sample[] samples;
    private SampleInfo sampleInfo;

    /**
     * Reads a SampleSet from the input reader.
     *
     * @param tok    tokenizer that holds parameters for this SampleSet
     * @param reader the input reader to read the data from
     */
    public SampleSet(StringTokenizer tok, BufferedReader reader) {
        try {
            int numSamples = Integer.parseInt(tok.nextToken());
            int numChannels = Integer.parseInt(tok.nextToken());
            int sampleRate = Integer.parseInt(tok.nextToken());
            float coeffMin = Float.parseFloat(tok.nextToken());
            float coeffRange = Float.parseFloat(tok.nextToken());
            float postEmphasis = Float.parseFloat(tok.nextToken());
            int residualFold = Integer.parseInt(tok.nextToken());

            samples = new Sample[numSamples];
            sampleInfo = new SampleInfo(sampleRate, numChannels,
                    residualFold, coeffMin, coeffRange, postEmphasis);

            for (int i = 0; i < numSamples; i++) {
                samples[i] = new Sample(reader, numChannels);
            }
        } catch (NoSuchElementException nse) {
            throw new Error("Parsing sample error " + nse.getMessage());
        }
    }

    /**
     * Creates a SampleSet by reading it from the given byte buffer
     *
     * @param bb source of the Unit data
     * @throws IOException if an IO error occurs
     */
    public SampleSet(ByteBuffer bb) throws IOException {
        int numSamples;
        sampleInfo = new SampleInfo(bb);
        numSamples = bb.getInt();
        this.samples = new Sample[numSamples];
        for (int i = 0; i < numSamples; i++) {
            samples[i] = Sample.loadBinary(bb);
        }
    }

    /**
     * Creates a SampleSet by reading it from the given input stream
     *
     * @param is source of the Unit data
     * @throws IOException if an IO error occurs
     */
    public SampleSet(DataInputStream is) throws IOException {
        int numSamples;
        sampleInfo = new SampleInfo(is);
        numSamples = is.readInt();
        this.samples = new Sample[numSamples];
        for (int i = 0; i < numSamples; i++) {
            samples[i] = Sample.loadBinary(is);
        }
    }

    /**
     * Dumps this sample set to the given stream
     *
     * @param os the output stream
     * @throws IOException if an error occurs.
     */
    public void dumpBinary(DataOutputStream os) throws IOException {
        sampleInfo.dumpBinary(os);
        os.writeInt(samples.length);
        for (int i = 0; i < samples.length; i++) {
            samples[i].dumpBinary(os);
        }
    }


    /**
     * return the sample associated with the index
     *
     * @param index the index of the sample
     * @return the sample.
     */
    public Sample getSample(int index) {
        return samples[index];
    }

    /**
     * Retrieves the info on this SampleSet
     *
     * @return the sample info
     */
    public SampleInfo getSampleInfo() {
        return sampleInfo;
    }


    /**
     * Returns the size of the unit represented
     * by the given start and end points
     *
     * @param start the start of the unit
     * @param end   the end of the unit
     * @return the size of the unit
     */
    public int getUnitSize(int start, int end) {
        int size = 0;

        for (int i = start; i < end; i++) {
            size += getFrameSize(i);
        }
        return size;
    }


    /**
     * Gets the size of the given frame
     *
     * @param frame the frame of interest
     * @return the size of the frame
     */
    public int getFrameSize(int frame) {
        return samples[frame].getResidualSize();
    }
}
    

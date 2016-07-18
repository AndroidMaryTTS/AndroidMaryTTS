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


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Describes global sample parameters. A sample info is generally added
 * to an utterance to describe the type of unit data that has been
 * generated.
 */
public class SampleInfo {
    public final static String UTT_NAME = "SampleInfo";

    private final int sampleRate;
    private final int numberOfChannels;
    private final int residualFold;
    private final float coeffMin;
    private final float coeffRange;
    private final float postEmphasis;

    /**
     * Creates a new sample info.
     *
     * @param sampleRate       the sample rate
     * @param numberOfChannels the number of channels
     * @param residualFold     the residual fold
     * @param coeffMin         the minimum coefficient
     * @param coeffRange       the range of coefficients
     */
    public SampleInfo(int sampleRate, int numberOfChannels,
                      int residualFold, float coeffMin,
                      float coeffRange, float postEmphasis) {
        this.sampleRate = sampleRate;
        this.numberOfChannels = numberOfChannels;
        this.residualFold = residualFold;
        this.coeffMin = coeffMin;
        this.coeffRange = coeffRange;
        this.postEmphasis = postEmphasis;
    }

    /**
     * Constructs a sample info from the given byte buffer.
     *
     * @param bb the byte buffer
     * @throws IOException if an input error occurs
     */
    public SampleInfo(ByteBuffer bb) throws IOException {
        numberOfChannels = bb.getInt();
        sampleRate = bb.getInt();
        coeffMin = bb.getFloat();
        coeffRange = bb.getFloat();
        postEmphasis = bb.getFloat();
        residualFold = bb.getInt();
    }

    /**
     * Constructs a sample info from the given input stream
     *
     * @param is the input stream
     * @throws IOException if an input error occurs
     */
    public SampleInfo(DataInputStream is) throws IOException {
        numberOfChannels = is.readInt();
        sampleRate = is.readInt();
        coeffMin = is.readFloat();
        coeffRange = is.readFloat();
        postEmphasis = is.readFloat();
        residualFold = is.readInt();
    }

    /**
     * Returns the sample rate.
     *
     * @return the sample rate
     */
    public final int getSampleRate() {
        return sampleRate;
    }

    /**
     * Returns the number of channels.
     *
     * @return the number of channels.
     */
    public final int getNumberOfChannels() {
        return numberOfChannels;
    }

    /**
     * Returns the residual fold.
     *
     * @return the residual fold
     */
    public final int getResidualFold() {
        return residualFold;
    }

    /**
     * Returns the minimum for linear predictive coding.
     *
     * @return the minimum for linear predictive coding.
     */
    public final float getCoeffMin() {
        return coeffMin;
    }

    /**
     * Returns the range for linear predictive coding.
     *
     * @return the range for linear predictive coding.
     */
    public final float getCoeffRange() {
        return coeffRange;
    }

    /**
     * Returns the post emphasis
     *
     * @return the post emphasis
     */
    public final float getPostEmphasis() {
        return postEmphasis;
    }


    /**
     * Dump a binary form of the sample rate
     * to the given output stream
     *
     * @param os the output stream
     * @throws IOException if an error occurs
     */
    public void dumpBinary(DataOutputStream os) throws IOException {
        os.writeInt(numberOfChannels);
        os.writeInt(sampleRate);
        os.writeFloat(coeffMin);
        os.writeFloat(coeffRange);
        os.writeFloat(postEmphasis);
        os.writeInt(residualFold);
    }
}

    

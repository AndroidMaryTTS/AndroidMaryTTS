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
 * A single short term sample containing Residual Excited Linear Predictive
 * (RELP) frame and residual voice data.
 */
public class Sample {
    private short[] frameData;
    private byte[] residualData;
    private int residualSize;


    /**
     * Constructs a RELP Sample from its component parts
     *
     * @param frameData    the framedata
     * @param residualData the residual data
     */
    public Sample(short[] frameData, byte[] residualData) {
        this.frameData = frameData;
        this.residualData = residualData;
    }

    /**
     * Constructs a Sample from its component parts
     *
     * @param frameData    the framedata
     * @param residualData the residual data
     */
    public Sample(short[] frameData, byte[] residualData, int residualSize) {
        this.frameData = frameData;
        this.residualData = residualData;
        this.residualSize = residualSize;
    }

    /**
     * Reads a sample from the input reader.
     *
     * @param reader      the input reader to read the data from
     * @param numChannels the number of channels per frame
     */
    public Sample(BufferedReader reader, int numChannels) {
        try {
            String line = reader.readLine();

            StringTokenizer tok = new StringTokenizer(line);
            if (!tok.nextToken().equals("FRAME")) {
                throw new Error("frame Parsing sample error");
            }

            frameData = new short[numChannels];

            for (int i = 0; i < numChannels; i++) {
                int svalue = Integer.parseInt(tok.nextToken()) - 32768;

                if (svalue < -32768 || svalue > 32767) {
                    throw new Error("data out of short range");
                }
                frameData[i] = (short) svalue;
            }

            line = reader.readLine();
            tok = new StringTokenizer(line);
            if (!tok.nextToken().equals("RESIDUAL")) {
                throw new Error("residual Parsing sample error");
            }

            residualSize = Integer.parseInt(tok.nextToken());
            residualData = new byte[residualSize];

            for (int i = 0; i < residualSize; i++) {
                int bvalue = Integer.parseInt(tok.nextToken()) - 128;

                if (bvalue < -128 || bvalue > 127) {
                    throw new Error("data out of byte range");
                }
                residualData[i] = (byte) bvalue;
            }
        } catch (NoSuchElementException nse) {
            throw new Error("Parsing sample error " + nse.getMessage());
        } catch (IOException ioe) {
            throw new Error("IO error while parsing sample" + ioe.getMessage());
        }
    }

    /**
     * Loads the samples from the byte bufer
     *
     * @param bb the byte buffer to read the data from.
     * @throws IOException if IO error occurs
     */
    public static Sample loadBinary(ByteBuffer bb) throws IOException {
        int frameDataSize = bb.getInt();

        short[] frameData = new short[frameDataSize];

        for (int i = 0; i < frameData.length; i++) {
            frameData[i] = bb.getShort();
        }

        int residualDataSize = bb.getInt();
        byte[] residualData = new byte[residualDataSize];

        for (int i = 0; i < residualData.length; i++) {
            residualData[i] = bb.get();
        }

        return new Sample(frameData, residualData, residualDataSize);
    }

    /**
     * Loads the samples from the given channel
     *
     * @param dis the DataInputStream to read the data from.
     * @throws IOException if IO error occurs
     */
    public static Sample loadBinary(DataInputStream dis)
            throws IOException {
        int frameDataSize = dis.readInt();

        short[] frameData = new short[frameDataSize];

        for (int i = 0; i < frameData.length; i++) {
            frameData[i] = dis.readShort();
        }

        int residualDataSize = dis.readInt();
        byte[] residualData = new byte[residualDataSize];

        for (int i = 0; i < residualData.length; i++) {
            residualData[i] = dis.readByte();
        }

        return new Sample(frameData, residualData, residualDataSize);
    }

    /**
     * Gets the frame data associated with this sample
     *
     * @return the frame data associated with this sample
     */
    public short[] getFrameData() {
        return frameData;
    }

    /**
     * Gets the residual data associated with this sample
     *
     * @return the residual data associated with this sample
     */
    public byte[] getResidualData() {
        return residualData;
    }

    /**
     * Returns the number of residuals in this Sample.
     *
     * @return the number of residuals in this sample
     */
    public int getResidualSize() {
        return residualSize;
    }

    /**
     * Returns the normalized residual data. You may not want to
     * call this function because of the overhead involved.
     *
     * @param which the index of the data of interest
     * @return the normalized data.
     */
    public int getResidualData(int which) {
        return ((int) residualData[which]) + 128;
    }

    /**
     * Returns the normalized frame data. You may not want to
     * call this function because of the overhead involved.
     *
     * @param which the index of the data of interest
     * @return the normalized data.
     */
    public int getFrameData(int which) {
        return ((int) frameData[which]) + 32768;
    }

    /**
     * Dumps the sample:
     */
    public void dump() {
        System.out.println(" FD Count: " + getFrameData().length);
        for (int i = 0; i < getFrameData().length; i++) {
            System.out.print(" " + getFrameData(i));
        }
        System.out.println();
        System.out.println(" RD Count: " + getResidualSize());
        // getResidualData().length);
        for (int i = 0; i < getResidualData().length; i++) {
            System.out.print(" " + getResidualData(i));
        }
        System.out.println();
    }

    /**
     * Dumps the samples to the given ByteBuffer
     *
     * @param bb the ByteBuffer to write the data to.
     * @throws IOException if IO error occurs
     */
    public void dumpBinary(ByteBuffer bb) throws IOException {
        bb.putInt(frameData.length);
        for (int i = 0; i < frameData.length; i++) {
            bb.putShort(frameData[i]);
        }
        bb.putInt(residualData.length);
        bb.put(residualData);
    }

    /**
     * Dumps the samples to the given stream
     *
     * @param os the DataOutputStream to write the data to.
     * @throws IOException if IO error occurs
     */
    public void dumpBinary(DataOutputStream os) throws IOException {
        os.writeInt(frameData.length);
        for (int i = 0; i < frameData.length; i++) {
            os.writeShort(frameData[i]);
        }
        os.writeInt(residualData.length);
        for (int i = 0; i < residualData.length; i++) {
            os.writeByte(residualData[i]);
        }
    }

    /**
     * Compares two samples. Note that this is not the same as
     * "equals"
     *
     * @param other the other sample to compare this one to
     * @return <code>true</code> if they compare; otherwise
     * <code>false</code>
     */
    public boolean compare(Sample other) {

        if (frameData.length != other.getFrameData().length) {
            return false;
        }

        for (int i = 0; i < frameData.length; i++) {
            if (frameData[i] != other.frameData[i]) {
                return false;
            }
        }

        if (residualData.length != other.residualData.length) {
            return false;
        }

        for (int i = 0; i < residualData.length; i++) {
            if (residualData[i] != other.residualData[i]) {
                return false;
            }
        }
        return true;
    }
}
    



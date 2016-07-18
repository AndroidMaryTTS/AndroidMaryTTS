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
package com.sun.speech.freetts.util;

/**
 * Provides a set of utilities for prrocessing wave/audio data.
 */
public class WaveUtils {

    private static final boolean ZEROTRAP = true;
    private static final int CLIP = 32625;
    private static final int BIAS = 0x84;
    private final static int[] expLut = {0, 132, 396, 924, 1980, 4092,
            8316, 16764};
    private static int[] exp_lut2 = {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3,
            4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};

    /**
     * Converts a raw short to ulaw.
     *
     * @param sampleData signed 16-bit linear sample
     * @return 8-bit ulaw sample, normalized
     */
    public static final byte shortToUlaw(short sampleData) {
        int sign, exponent, mantissa;
        int ulawByte;
        int sample = (int) sampleData;

        sign = (sample >> 8) & 0x80; // set aside the sign
        if (sign != 0) {             // get the magnitude
            sample = -sample;
        }
        if (sample > CLIP) {         // clip the magnitude
            sample = CLIP;
        }
        sample = sample + BIAS;
        exponent = exp_lut2[(sample >> 7) & 0xFF];
        mantissa = (sample >> (exponent + 3)) & 0x0F;
        ulawByte = ~(sign | (exponent << 4) | mantissa);

        if (ZEROTRAP) {
            if (ulawByte == 0) { // optional CCITT trap
                ulawByte = 0x02;
            }
        }

        return (byte) ((ulawByte - 128) & 0xFF);
    }


    /**
     * Converts from ulaw to 16 bit linear.
     * <p/>
     * Craig Reese: IDA/Supercomputing Research Center
     * <br>
     * 29 September 1989
     * <p/>
     * References:
     * <br>
     * 1) CCITT Recommendation G.711  (very difficult to follow)
     * <br>
     * 2) MIL-STD-188-113,"Interoperability and Performance Standards
     * for Analog-to_Digital Conversion Techniques," 17 February 1987
     *
     * @param uByte 8 bit ulaw sample
     * @return signed 16 bit linear sample
     */
    public static final short ulawToShort(short uByte) {
        int sign, exponent, mantissa;
        int sample;
        int ulawByte = uByte;

        ulawByte = ~(ulawByte);
        sign = (ulawByte & 0x80);
        exponent = (ulawByte >> 4) & 0x07;
        mantissa = ulawByte & 0x0F;
        sample = expLut[exponent] + (mantissa << (exponent + 3));
        if (sign != 0) {
            sample = -sample;
        }

        return (short) sample;
    }

    /**
     * Reconstructs a short from its hi and low bytes.
     *
     * @param hiByte the high byte
     * @param loByte the low byte
     * @return the short value
     */
    public static final short bytesToShort(byte hiByte, byte loByte) {
        int result = (0x000000FF & hiByte);
        result = result << 8;
        result |= (0x000000FF & loByte);
        return (short) result;
    }

    /**
     * Provides test program for method ulawToShort().
     *
     * @param args not used
     */
    public static void main(String[] args) {
        for (int i = 0; i < 256; i++) {
            System.out.println("" + i + "=" + ulawToShort((short) i));
        }
    }

}

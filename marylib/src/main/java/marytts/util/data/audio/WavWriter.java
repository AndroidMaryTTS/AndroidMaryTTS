/**
 * Copyright 2000-2009 DFKI GmbH.
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
package marytts.util.data.audio;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WavWriter {

    private byte[] buf = null;
    private int nBytesPerSample = 0;

    /**
     * Byte swapping for int values.
     *
     * @param val
     * @return
     */
    private static int byteswap(int val) {
        return (((val & 0xff000000) >>> 24)
                + ((val & 0x00ff0000) >>> 8)
                + ((val & 0x0000ff00) << 8)
                + ((val & 0x000000ff) << 24));
    }

    /**
     * Byte swapping for short values.
     *
     * @param val
     * @return
     */
    private static short byteswap(short val) {
        return ((short) (
                (((val) & 0xff00) >>> 8)
                        +
                        (((val) & 0x00ff) << 8)
        )
        );
    }

    /**
     * Outputs the data in wav format.
     *
     * @throws IOException
     */
    private void doWrite(String fileName, int sampleRate) throws IOException {

// PCM_SIGNED 16000.0 Hz, 16 bit, mono, 2 bytes/frame, little-endian


        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(fileName)));

        dos.writeBytes("RIFF"); // "RIFF" in ascii
        dos.writeInt(byteswap(36 + buf.length)); // Chunk size
        dos.writeBytes("WAVEfmt ");
        dos.writeInt(byteswap(16)); // chunk size, 16 for PCM
        dos.writeShort(byteswap((short) 1)); // PCM format
        dos.writeShort(byteswap((short) 1)); // Mono, one channel

        System.out.println("Sample rate: " + sampleRate + ", nBytes:" + nBytesPerSample);
        dos.writeInt(byteswap(sampleRate)); // Samplerate
        dos.writeInt(byteswap(sampleRate * nBytesPerSample)); // Byte-rate
        dos.writeShort(byteswap((short) (nBytesPerSample)));   // Nbr of bytes per samples x nbr of channels
        dos.writeShort(byteswap((short) (nBytesPerSample * 8))); // nbr of bits per sample
        dos.writeBytes("data");
        dos.writeInt(byteswap(buf.length));
        dos.writeByte(0);
        dos.write(buf); // <= This buffer should already be byte-swapped at this stage
        dos.close();
    }

    /**
     * Export an array of shorts to a wav file.
     *
     * @param fileName   The name of the wav file.
     * @param sampleRate The sample rate.
     * @param samples    The array of short samples.
     * @throws IOException
     */
    public void export(String fileName, int sampleRate, short[] samples) throws IOException {
        nBytesPerSample = 2;
        buf = new byte[samples.length * 2];
        // Cast the samples, and byte-swap them in the same loop
        for (int i = 0; i < samples.length; i++) {
            buf[2 * i] = (byte) ((samples[i] & 0xff00) >>> 8);
            buf[2 * i + 1] = (byte) ((samples[i] & 0x00ff));
        }
        // Do the write
        doWrite(fileName, sampleRate);
    }

    /**
     * Export an array of bytes to a wav file.
     *
     * @param fileName   The name of the wav file.
     * @param sampleRate The sample rate.
     * @param samples    The array of short samples, given as a byte array
     *                   (with low and hi bytes separated).
     * @throws IOException
     */
    public void export(String fileName, int sampleRate, byte[] samples) throws IOException {
        nBytesPerSample = 2;
        buf = new byte[samples.length];
        System.arraycopy(samples, 0, buf, 0, samples.length);
        // Byte-swap the samples
        byte b = 0;
        for (int j = 0; j < buf.length; j += 2) {
            b = buf[j];
            buf[j] = buf[j + 1];
            buf[j + 1] = b;
        }
        // Do the write
        doWrite(fileName, sampleRate);
    }


}


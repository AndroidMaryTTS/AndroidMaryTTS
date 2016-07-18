/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package lib.com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.UnsupportedAudioFileException;
import lib.sound.sampled.spi.AudioFileReader;


/**
 * Abstract File Reader class.
 *
 * @author Jan Borgersen
 * @version %I% %E%
 */
abstract class SunFileReader extends AudioFileReader {

    // buffer size for temporary input streams
    protected static final int bisBufferSize = 4096;

    /**
     * Constructs a new SunFileReader object.
     */
    public SunFileReader() {
    }


    // METHODS TO IMPLEMENT AudioFileReader

    /**
     * Calculates the frame size for PCM frames.
     * Note that this method is appropriate for non-packed samples.
     * For instance, 12 bit, 2 channels will return 4 bytes, not 3.
     *
     * @param sampleSizeInBits the size of a single sample in bits
     * @param channels         the number of channels
     * @return the size of a PCM frame in bytes.
     */
    protected static int calculatePCMFrameSize(int sampleSizeInBits,
                                               int channels) {
        return ((sampleSizeInBits + 7) / 8) * channels;
    }

    /**
     * Obtains the audio file format of the input stream provided.  The stream must
     * point to valid audio file data.  In general, audio file providers may
     * need to read some data from the stream before determining whether they
     * support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support this, this method may fail
     * with an IOException.
     *
     * @param stream the input stream from which file format information should be
     *               extracted
     * @return an <code>AudioFileFormat</code> object describing the audio file format
     * @throws UnsupportedAudioFileException if the stream does not point to valid audio
     *                                       file data recognized by the system
     * @throws IOException                   if an I/O exception occurs
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    abstract public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException;

    /**
     * Obtains the audio file format of the URL provided.  The URL must
     * point to valid audio file data.
     *
     * @param url the URL from which file format information should be
     *            extracted
     * @return an <code>AudioFileFormat</code> object describing the audio file format
     * @throws UnsupportedAudioFileException if the URL does not point to valid audio
     *                                       file data recognized by the system
     * @throws IOException                   if an I/O exception occurs
     */
    abstract public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException;

    /**
     * Obtains the audio file format of the File provided.  The File must
     * point to valid audio file data.
     *
     * @param file the File from which file format information should be
     *             extracted
     * @return an <code>AudioFileFormat</code> object describing the audio file format
     * @throws UnsupportedAudioFileException if the File does not point to valid audio
     *                                       file data recognized by the system
     * @throws IOException                   if an I/O exception occurs
     */
    abstract public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException;

    /**
     * Obtains an audio stream from the input stream provided.  The stream must
     * point to valid audio file data.  In general, audio file providers may
     * need to read some data from the stream before determining whether they
     * support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support this, this method may fail
     * with an IOException.
     *
     * @param stream the input stream from which the <code>AudioInputStream</code> should be
     *               constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data contained
     * in the input stream.
     * @throws UnsupportedAudioFileException if the stream does not point to valid audio
     *                                       file data recognized by the system
     * @throws IOException                   if an I/O exception occurs
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    abstract public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException;

    /**
     * Obtains an audio stream from the URL provided.  The URL must
     * point to valid audio file data.
     *
     * @param url the URL for which the <code>AudioInputStream</code> should be
     *            constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data pointed
     * to by the URL
     * @throws UnsupportedAudioFileException if the URL does not point to valid audio
     *                                       file data recognized by the system
     * @throws IOException                   if an I/O exception occurs
     */
    abstract public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException;


    // HELPER METHODS

    /**
     * Obtains an audio stream from the File provided.  The File must
     * point to valid audio file data.
     *
     * @param file the File for which the <code>AudioInputStream</code> should be
     *             constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data pointed
     * to by the File
     * @throws UnsupportedAudioFileException if the File does not point to valid audio
     *                                       file data recognized by the system
     * @throws IOException                   if an I/O exception occurs
     */
    abstract public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException;

    /**
     * rllong
     * Protected helper method to read 64 bits and changing the order of
     * each bytes.
     *
     * @param DataInputStream
     * @return 32 bits swapped value.
     * @throws IOException
     */
    protected int rllong(DataInputStream dis) throws IOException {

        int b1, b2, b3, b4;
        int i = 0;

        i = dis.readInt();

        b1 = (i & 0xFF) << 24;
        b2 = (i & 0xFF00) << 8;
        b3 = (i & 0xFF0000) >> 8;
        b4 = (i & 0xFF000000) >>> 24;

        i = (b1 | b2 | b3 | b4);

        return i;
    }

    /**
     * big2little
     * Protected helper method to swap the order of bytes in a 32 bit int
     *
     * @param int
     * @return 32 bits swapped value
     */
    protected int big2little(int i) {

        int b1, b2, b3, b4;

        b1 = (i & 0xFF) << 24;
        b2 = (i & 0xFF00) << 8;
        b3 = (i & 0xFF0000) >> 8;
        b4 = (i & 0xFF000000) >>> 24;

        i = (b1 | b2 | b3 | b4);

        return i;
    }

    /**
     * rlshort
     * Protected helper method to read 16 bits value. Swap high with low byte.
     *
     * @param DataInputStream
     * @return the swapped value.
     * @throws IOException
     */
    protected short rlshort(DataInputStream dis) throws IOException {

        short s = 0;
        short high, low;

        s = dis.readShort();

        high = (short) ((s & 0xFF) << 8);
        low = (short) ((s & 0xFF00) >>> 8);

        s = (short) (high | low);

        return s;
    }

    /**
     * big2little
     * Protected helper method to swap the order of bytes in a 16 bit short
     *
     * @param int
     * @return 16 bits swapped value
     */
    protected short big2littleShort(short i) {

        short high, low;

        high = (short) ((i & 0xFF) << 8);
        low = (short) ((i & 0xFF00) >>> 8);

        i = (short) (high | low);

        return i;
    }
}
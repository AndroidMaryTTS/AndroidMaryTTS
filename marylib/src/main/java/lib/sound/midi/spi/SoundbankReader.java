/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package lib.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import lib.sound.midi.InvalidMidiDataException;
import lib.sound.midi.Soundbank;


/**
 * A <code>SoundbankReader</code> supplies soundbank file-reading services.
 * Concrete subclasses of <code>SoundbankReader</code> parse a given
 * soundbank file, producing a {@link lib.sound.midi.Soundbank}
 * object that can be loaded into a {@link lib.sound.midi.Synthesizer}.
 *
 * @author Kara Kytle
 * @since 1.3
 */
public abstract class SoundbankReader {


    /**
     * Obtains a soundbank object from the URL provided.
     *
     * @param url URL representing the soundbank.
     * @return soundbank object
     * @throws InvalidMidiDataException if the URL does not point to
     *                                  valid MIDI soundbank data recognized by this soundbank reader
     * @throws IOException              if an I/O error occurs
     */
    public abstract Soundbank getSoundbank(URL url) throws InvalidMidiDataException, IOException;


    /**
     * Obtains a soundbank object from the <code>InputStream</code> provided.
     *
     * @param stream <code>InputStream</code> representing the soundbank
     * @return soundbank object
     * @throws InvalidMidiDataException if the stream does not point to
     *                                  valid MIDI soundbank data recognized by this soundbank reader
     * @throws IOException              if an I/O error occurs
     */
    public abstract Soundbank getSoundbank(InputStream stream) throws InvalidMidiDataException, IOException;


    /**
     * Obtains a soundbank object from the <code>File</code> provided.
     *
     * @param file the <code>File</code> representing the soundbank
     * @return soundbank object
     * @throws InvalidMidiDataException if the file does not point to
     *                                  valid MIDI soundbank data recognized by this soundbank reader
     * @throws IOException              if an I/O error occurs
     */
    public abstract Soundbank getSoundbank(File file) throws InvalidMidiDataException, IOException;


}

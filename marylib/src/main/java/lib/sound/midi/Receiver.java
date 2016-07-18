/*
 * Copyright (c) 1999, 2010, Oracle and/or its affiliates. All rights reserved.
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

package lib.sound.midi;

import lib.lang.AutoCloseable;


/**
 * A <code>Receiver</code> receives <code>{@link MidiEvent}</code> objects and
 * typically does something useful in response, such as interpreting them to
 * generate sound or raw MIDI output.  Common MIDI receivers include
 * synthesizers and MIDI Out ports.
 *
 * @author Kara Kytle
 * @see MidiDevice
 * @see Synthesizer
 * @see Transmitter
 */
public interface Receiver extends AutoCloseable {


    //$$fb 2002-04-12: fix for 4662090: Contradiction in Receiver specification

    /**
     * Sends a MIDI message and time-stamp to this receiver.
     * If time-stamping is not supported by this receiver, the time-stamp
     * value should be -1.
     *
     * @param message   the MIDI message to send
     * @param timeStamp the time-stamp for the message, in microseconds.
     * @throws IllegalStateException if the receiver is closed
     */
    void send(MidiMessage message, long timeStamp);

    /**
     * Indicates that the application has finished using the receiver, and
     * that limited resources it requires may be released or made available.
     * <p/>
     * <p>If the creation of this <code>Receiver</code> resulted in
     * implicitly opening the underlying device, the device is
     * implicitly closed by this method. This is true unless the device is
     * kept open by other <code>Receiver</code> or <code>Transmitter</code>
     * instances that opened the device implicitly, and unless the device
     * has been opened explicitly. If the device this
     * <code>Receiver</code> is retrieved from is closed explicitly by
     * calling {@link MidiDevice#close MidiDevice.close}, the
     * <code>Receiver</code> is closed, too.  For a detailed
     * description of open/close behaviour see the class description
     * of {@link lib.sound.midi.MidiDevice MidiDevice}.
     *
     * @see lib.sound.midi.MidiSystem#getReceiver
     */
    void close();
}

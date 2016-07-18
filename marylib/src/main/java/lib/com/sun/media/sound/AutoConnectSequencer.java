/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package lib.com.sun.media.sound;

import lib.sound.midi.Receiver;

/**
 * Interface for Sequencers that are able to do the auto-connect
 * as required by MidiSystem.getSequencer()
 *
 * @author Florian Bomers
 * @version %I%, %E%
 */
public interface AutoConnectSequencer {

    /**
     * Set the receiver that this device is
     * auto-connected. If non-null, the device
     * needs to re-connect itself to a suitable
     * device in open().
     */
    void setAutoConnect(Receiver autoConnectReceiver);

}
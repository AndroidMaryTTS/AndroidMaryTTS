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
package com.sun.speech.freetts;

import java.io.InputStream;

import mf.org.w3c.dom.Document;

/**
 * Represents something that should be spoken.
 */
public interface FreeTTSSpeakable {

    /**
     * Indicates that this speakable has been started.
     */
    void started();

    /**
     * Indicates that this speakable has been completed.
     */
    void completed();

    /**
     * Indicates that this speakable has been cancelled.
     */
    void cancelled();

    /**
     * Returns <code>true</code> if this queue item has been
     * processed.
     *
     * @return true if it has been processed
     */
    boolean isCompleted();

    /**
     * Waits for this speakable item to be completed.
     *
     * @return true if the item was completed successfully, false if
     * the speakable was cancelled or an error occurred.
     */
    boolean waitCompleted();

    /**
     * Returns <code>true</code> if the item contains plain text
     * (not Java Speech Markup Language text).
     *
     * @return true if the item contains plain text
     */
    boolean isPlainText();

    /**
     * Returns <code>true</code> if the item is an input stream.
     *
     * @return true if the item is an input stream
     */
    boolean isStream();

    /**
     * Returns <code>true</code> if the item is a JSML document
     * (Java Speech Markup Language).
     *
     * @return true if the item is a document
     */
    boolean isDocument();

    /**
     * Returns the text corresponding to this Playable.
     *
     * @return the Playable text
     */
    String getText();

    /**
     * Gets the DOM document for this object.
     *
     * @return the DOM document for this object
     */
    Document getDocument();

    /**
     * Gets the input stream
     *
     * @return the input stream
     */
    InputStream getInputStream();
}

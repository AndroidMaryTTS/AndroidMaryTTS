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

/**
 * Specifies the input mode of FreeTTS.
 */
public class InputMode {

    /**
     * Indicates that there is no input mode
     */
    public static final InputMode NONE = new InputMode("none");
    /**
     * Indicates that the input is from a file.
     */
    public static final InputMode FILE = new InputMode("file");
    /**
     * Indicates that the input is from text.
     */
    public static final InputMode TEXT = new InputMode("text");
    /**
     * Indicates that the input is from a URL.
     */
    public static final InputMode URL = new InputMode("url");
    /**
     * Indicates that the input is a set of lines in a file..
     */
    public static final InputMode LINES = new InputMode("lines");
    /**
     * Indicates that the input is from the keyboard.
     */
    public static final InputMode INTERACTIVE = new InputMode("interactive");
    private final String name;

    private InputMode(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}

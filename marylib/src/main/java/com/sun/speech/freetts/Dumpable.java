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

import java.io.PrintWriter;

/**
 * Tags an object that can be dumped for debugging purposes.
 */
public interface Dumpable {

    /**
     * Dumps the object to a PrintWriter.
     *
     * @param pw      the stream to send the output
     * @param padding the number of spaces in the string
     * @param title   the title for the dump
     */
    void dump(PrintWriter pw, int padding, String title);
}

  

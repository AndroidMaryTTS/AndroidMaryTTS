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
 * Performs a specific type of processing on an item and returns an
 * object.
 */
public interface FeatureProcessor {

    /**
     * Performs some processing on the given item.
     *
     * @param item the item to process
     * @throws ProcessException if an exception occurred during the
     *                          processing
     */
    String process(Item item) throws ProcessException;
}


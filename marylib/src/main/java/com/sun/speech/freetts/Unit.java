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


import com.sun.speech.freetts.relp.Sample;

/**
 * Defines a generic interface to a Unit.
 */
public interface Unit {

    /**
     * Returns the name of this Unit.
     *
     * @return the name of this Unit
     */
    String getName();

    /**
     * Returns the size of this unit.
     *
     * @return the size of this unit
     */
    int getSize();

    /**
     * Retrieves the nearest sample.
     *
     * @param index the ideal index
     * @return the nearest Sample
     */
    Sample getNearestSample(float index);


    /**
     * Dumps this unit.
     */
    void dump();
}

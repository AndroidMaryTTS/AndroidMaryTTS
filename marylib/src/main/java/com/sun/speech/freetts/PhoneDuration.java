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
 * Maintains the mean duration and standard deviation about a phone.
 * These are meant to be used by the code that calculates segment
 * durations via statistical methods, and are paired with the phone
 * by <code>PhoneDurations</code>.
 *
 * @see PhoneDurations
 */
public class PhoneDuration {
    /**
     * The mean duration.
     */
    private float mean;

    /**
     * The standard deviation from the mean.
     */
    private float standardDeviation;

    /**
     * Creates a new <code>PhoneDuration</code> with the given mean
     * and standard deviation.
     *
     * @param mean              mean duration, typically in seconds
     * @param standardDeviation standardDeviation from the mean
     */
    public PhoneDuration(float mean, float standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    /**
     * Gets the mean.  The return value is typically in seconds.
     *
     * @return the mean
     */
    public float getMean() {
        return mean;
    }

    /**
     * Gets the standard deviation from the mean.
     *
     * @return the standard deviation from the mean
     */
    public float getStandardDeviation() {
        return standardDeviation;
    }
}


  

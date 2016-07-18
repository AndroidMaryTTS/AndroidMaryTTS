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
 * Maintains a set of <code>PhoneDuration</code> instances indexed by
 * phone.
 *
 * @see PhoneDuration
 */
public interface PhoneDurations {
    /**
     * Gets the <code>PhoneDuration</code> for the given phone.  If no
     * duration is applicable, returns <code>null</code>.  Note that
     * 'applicable' implementation dependent; some implementations
     * may return a default value.
     *
     * @param phone the phone to get duration information for
     * @return the duration information for the phone
     */
    PhoneDuration getPhoneDuration(String phone);
}

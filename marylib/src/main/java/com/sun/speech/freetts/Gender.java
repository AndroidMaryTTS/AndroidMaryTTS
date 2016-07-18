/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;

/**
 * Provides an enumeration of Gender, following the JSAPI style.
 * (http://java.sun.com/products/java-media/speech/forDevelopers/jsapi-doc/)
 * <p/>
 * These are intended for use to define properties about FreeTTS
 * voices.
 *
 * @see Voice
 */
public class Gender implements Comparable {
    /**
     * Male.
     */
    public static final Gender MALE = new Gender("MALE");
    /**
     * Female.
     */
    public static final Gender FEMALE = new Gender("FEMALE");
    /**
     * Neutral such as a robot or artificial.
     */
    public static final Gender NEUTRAL = new Gender("NEUTRAL");
    /**
     * Match against all other genders.
     */
    public static final Gender DONT_CARE = new Gender("DONT_CARE");
    // Ordinal of next created
    private static int nextOrdinal = 0;
    private final String name;
    // Assign an ordinal to this gender
    private final int ordinal = nextOrdinal++;

    private Gender(String name) {
        this.name = name;
    }

    /**
     * Generates a human readable name describing the gender.
     *
     * @return the name of the gender
     */
    public String toString() {
        return name;
    }

    /**
     * Compare two genders.  If either is DONT_CARE, then returns 0.
     */
    public int compareTo(Object o) {
        if ((o == DONT_CARE) || (this == DONT_CARE)) {
            return 0;
        } else {
            return ordinal - ((Gender) o).ordinal;
        }
    }
}

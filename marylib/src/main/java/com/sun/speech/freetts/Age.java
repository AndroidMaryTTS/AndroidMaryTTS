/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;

/**
 * Provides an enumeration of Age, following JSAPI style.
 * (http://java.sun.com/products/java-media/speech/forDevelopers/jsapi-doc/)
 * <p/>
 * This is intended for use to define properties about FreeTTS voices.
 *
 * @see Voice
 */
public class Age implements Comparable {
    /**
     * Age roughly up to 12 years.
     */
    public static final Age CHILD = new Age("CHILD");
    /**
     * Age roughly 13 to 19 years.
     */
    public static final Age TEENAGER = new Age("TEENAGER");
    /**
     * Age roughly 20 to 40 years.
     */
    public static final Age YOUNGER_ADULT = new Age("YOUNGER_ADULT");
    /**
     * Age roughly 40 to 60 years.
     */
    public static final Age MIDDLE_ADULT = new Age("MIDDLE_ADULT");
    /**
     * Age roughly 60 years and up.
     */
    public static final Age OLDER_ADULT = new Age("OLDER_ADULT");
    /**
     * An Age that is indeterminate.
     */
    public static final Age NEUTRAL = new Age("NEUTRAL");
    /**
     * Matches against any Age.
     */
    public static final Age DONT_CARE = new Age("DONT_CARE");
    // Ordinal of next created
    private static int nextOrdinal = 0;
    private final String name;
    // Assign an ordinal to this age
    private final int ordinal = nextOrdinal++;

    private Age(String name) {
        this.name = name;
    }

    /**
     * Provide a human readable string that describes the age.
     *
     * @return the name of the age
     */
    public String toString() {
        return name;
    }

    /**
     * Compare two ages.  CHILD is less than TEENAGER, and so on.  If
     * either age is DONT_CARE, then they are equal.
     */
    public int compareTo(Object o) {
        if ((o == DONT_CARE) || (this == DONT_CARE)) {
            return 0;
        } else {
            return ordinal - ((Age) o).ordinal;
        }
    }
}

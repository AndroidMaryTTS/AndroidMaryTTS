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
 * Maintains a list of phones with various features for those phones.
 */
public interface PhoneSet {

    /**
     * Vowel or consonant:  + = vowel, - = consonant.
     */
    String VC = "vc";

    /**
     * Vowel length:  s = short, l = long, d = dipthong, a = schwa.
     */
    String VLNG = "vlng";

    /**
     * Vowel height:  1 = high,  2 = mid,  3 = low.
     */
    String VHEIGHT = "vheight";

    /**
     * Vowel frontness:  1 = front, 2 = mid, 3 = back.
     */
    String VFRONT = "vfront";

    /**
     * Lip rounding:  + = on, - = off.
     */
    String VRND = "vrnd";

    /**
     * Consonant type:  s = stop, f = fricative,  a = affricative,
     * n = nasal, l = liquid.
     */
    String CTYPE = "ctype";

    /**
     * Consonant cplace:  l = labial, a = alveolar, p = palatal,
     * b = labio_dental, d = dental, v = velar
     */
    String CPLACE = "cplace";

    /**
     * Consonant voicing:  + = on, - = off
     */
    String CVOX = "cvox";

    /**
     * Given a phoneme and a feature name, return the feature.
     *
     * @param phone       the phoneme of interest
     * @param featureName the name of the feature of interest
     * @return the feature with the given name
     */
    String getPhoneFeature(String phone, String featureName);
}

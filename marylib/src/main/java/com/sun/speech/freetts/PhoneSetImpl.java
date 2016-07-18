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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Implementation of a <code>PhoneSet</code> that reads the info from
 * a file.  The format of the file is as follows:
 * <p/>
 * <pre>
 * phone feature value
 * phone feature value
 * phone feature value
 * ...
 * </pre>
 * <p/>
 * Where <code>phone</code> is the phone name, <code>feature</code> is
 * the phone feature such as "vc," "vlng," "vheight," and so on, and
 * "value" is the value of the feature.  There can be multiple lines
 * for the same phone to describe various features of that phone.
 */
public class PhoneSetImpl implements PhoneSet {
    /**
     * Used for informational purposes if there's a bad line in the
     * file.
     */
    private int lineCount = 0;

    /**
     * The set of phone features indexed by phone.
     */
    private Map phonesetMap;

    /**
     * Create a new <code>PhoneSetImpl</code> by reading from the
     * given URL.
     *
     * @param url the input source
     * @throws IOException if an error occurs
     */
    public PhoneSetImpl(URL url) throws IOException {
        BufferedReader reader;
        String line;

        phonesetMap = new HashMap();
        reader = new BufferedReader(new
                InputStreamReader(url.openStream()));
        line = reader.readLine();
        lineCount++;
        while (line != null) {
            if (!line.startsWith("***")) {
                parseAndAdd(line);
            }
            line = reader.readLine();
        }
        reader.close();
    }

    /**
     * Creates a word from the given input line and add it to the map.
     *
     * @param line the input line
     */
    private void parseAndAdd(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        try {
            String phoneme = tokenizer.nextToken();
            String feature = tokenizer.nextToken();
            String value = tokenizer.nextToken();
            phonesetMap.put(getKey(phoneme, feature), value);
        } catch (NoSuchElementException nse) {
            throw new Error("part of speech data in bad format at line "
                    + lineCount);
        }
    }

    /**
     * Given a phoneme and a feature, returns the key that
     * will obtain the value.
     *
     * @param phoneme the phoneme
     * @param feature the name of the feature
     * @return the key used to obtain the value
     */
    private String getKey(String phoneme, String feature) {
        return phoneme + feature;
    }

    /**
     * Given a phoneme and a feature name, returns the feature.
     *
     * @param phone       the phoneme of interest
     * @param featureName the name of the feature of interest
     * @return the feature with the given name
     */
    public String getPhoneFeature(String phone, String featureName) {
        return (String) phonesetMap.get(getKey(phone, featureName));
    }
}

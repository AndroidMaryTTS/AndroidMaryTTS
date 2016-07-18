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
import java.util.StringTokenizer;

/**
 * Maintains set of PhoneDuration instances read in from a file.  The
 * format of the file is as follows:
 * <p/>
 * <pre>
 * phone mean stddev
 * phone mean stddev
 * phone mean stddev
 * ...
 * </pre>
 * <p/>
 * Where <code>phone</code> is the phone name, <code>mean</code> is
 * a <code>float</code> representing the mean duration of the phone
 * (typically in seconds), and <code>stddev</code> is a
 * <code>float</code> representing the standard deviation from the
 * mean.
 */
public class PhoneDurationsImpl implements PhoneDurations {
    /**
     * The set of PhoneDuration instances indexed by phone.
     */
    private HashMap phoneDurations;

    /**
     * Creates a new PhoneDurationsImpl by reading from the given URL.
     *
     * @param url the input source
     * @throws IOException if an error occurs
     */
    public PhoneDurationsImpl(URL url) throws IOException {
        BufferedReader reader;
        String line;

        phoneDurations = new HashMap();
        reader = new BufferedReader(new
                InputStreamReader(url.openStream()));
        line = reader.readLine();
        while (line != null) {
            if (!line.startsWith("***")) {
                parseAndAdd(line);
            }
            line = reader.readLine();
        }
        reader.close();
    }

    /**
     * Creates a word from the given input line and adds it to the
     * map.
     *
     * @param line the input line
     */
    private void parseAndAdd(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        String phone = tokenizer.nextToken();
        float mean = Float.parseFloat(tokenizer.nextToken());
        float stddev = Float.parseFloat(tokenizer.nextToken());
        phoneDurations.put(phone, new PhoneDuration(mean, stddev));
    }

    /**
     * Gets the <code>PhoneDuration</code> for the given phone.  If no
     * duration is applicable, returns <code>null</code>.
     *
     * @param phone the phone
     * @return the <code>PhoneDuration</code> for <code>phone</code>
     */
    public PhoneDuration getPhoneDuration(String phone) {
        return (PhoneDuration) phoneDurations.get(phone);
    }
}

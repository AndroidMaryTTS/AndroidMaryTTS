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

import com.sun.speech.freetts.util.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Implementation of a <code>PartOfSpeech</code> that reads the info
 * from a file.  The format of the file is as follows:
 * <p/>
 * <pre>
 * word pos
 * word pos
 * word pos
 * ...
 * </pre>
 * <p/>
 * Where <code>word</code> is the word and <code>pos</code> is the
 * part of speech for the word.  The part of speech is implementation
 * dependent.
 */
public class PartOfSpeechImpl implements PartOfSpeech {
    /**
     * Used for informational purposes if there's a bad line in the
     * file.
     */
    private int lineCount = 0;

    /**
     * A map from words to their part of speech.
     */
    private Map partOfSpeechMap;

    /**
     * Default part of speech.
     */
    private String defaultPartOfSpeech;

    /**
     * Creates a new PartOfSpeechImpl by reading from the given URL.
     *
     * @param url                 the input source
     * @param defaultPartOfSpeech the default part of speech
     * @throws IOException if an error occurs
     */
    public PartOfSpeechImpl(URL url, String defaultPartOfSpeech)
            throws IOException {

        BufferedReader reader;
        String line;

        partOfSpeechMap = new HashMap();
        this.defaultPartOfSpeech = defaultPartOfSpeech;
        reader = new BufferedReader(new
                InputStreamReader(Utilities.getInputStream(url)));
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
     * Creates a word from the given input line and adds it to the map.
     *
     * @param line the input line
     */
    private void parseAndAdd(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        try {
            String word = tokenizer.nextToken();
            String pos = tokenizer.nextToken();
            partOfSpeechMap.put(word, pos);
        } catch (NoSuchElementException nse) {
            System.err.println("part of speech data in bad format at line "
                    + lineCount);
        }
    }

    /**
     * Returns a description of the part of speech given a word.
     * If the given word cannot be found, the part of speech will be the
     * <code>defaultPartOfSpeech</code> parameter passed to the constructor.
     *
     * @param word the word to classify
     * @return an implementation dependent part of speech for the word
     */
    public String getPartOfSpeech(String word) {
        String pos = (String) partOfSpeechMap.get(word);
        if (pos == null) {
            pos = defaultPartOfSpeech;
        }
        return pos;
    }
}

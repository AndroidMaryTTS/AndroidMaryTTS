/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2014  DFKI Language Technology Lab http://www.dfki.de/lt
 *   Author: Joerg Steffen, steffen@dfki.de
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.dfki.lt.tools.tokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dfki.lt.tools.tokenizer.regexp.RegExp;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Provides methods to collect abbreviations from corpora containing a single sentence per line.
 *
 * @author Joerg Steffen, DFKI
 */
public final class AbbrevCollector {

    // the logger
    // private static final Logger logger = LoggerFactory.getLogger(AbbrevCollector.class);


    // would create a new instance of {@link AbbrevCollector}; not to be used
    private AbbrevCollector() {

        // private constructor to enforce noninstantiability
    }


    /**
     * Scans the given directory recursively for files with the given suffix. It is assumed that each
     * of these files contains one sentence per line. It extracts all abbreviations from these files
     * and stores them under the given result file name using UTF-8 encoding.
     *
     * @param dir            the directory to scan
     * @param suffix         the file name suffix
     * @param encoding       the encoding of the files
     * @param resultFileName the result file name
     * @param lang           the language of the files
     * @throws IOException if there is a problem when reading or writing the files
     */
    public static void collect(
            String dir, String suffix, String encoding, String resultFileName, String lang)
            throws IOException {

        // init tokenizer and get the relevant language resource
        JTok jtok = new JTok();
        LanguageResource langRes = jtok.getLanguageResource(lang);

        // get matchers and lists used to filter the abbreviations

        // the lists contains known abbreviations and titles
        Map<String, Set<String>> abbrevLists = langRes.getAbbrevLists();

        // this contains the word that only start with a capital letter at
        // the beginning of a sentence; we want to avoid to extract abbreviations
        // consisting of such a word followed by a punctuation
        Set<String> nonCapTerms = langRes.getNonCapTerms();

        // this are the matcher for abbreviations
        RegExp abbrevMatcher = langRes.getAllAbbrevMatcher();

        Set<String> abbrevs = new HashSet<String>();

        // get all training files
        List<String> trainingFiles = FileTools.getFilesFromDir(dir, suffix);

        // iterate over corpus files
        for (String oneFileName : trainingFiles) {
            System.out.println("processing " + oneFileName + " ...");

            // init reader
            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(oneFileName), encoding));
            String sent;
            // read lines from file
            while ((sent = in.readLine()) != null) {

                // split the sentence using as separator whitespaces and
                // ... .. ' ` \ \\ |
                String[] tokens = sent.split(" |\\.\\.\\.|\\.\\.|'|`|\\(|\\)|[|]");

                for (int i = 0; i < (tokens.length - 1); i++) {
                    // we skip the last token with the final sentence punctuation
                    String oneTok = tokens[i];
                    if ((oneTok.length() > 1) && oneTok.endsWith(".")) {

                        // if the abbreviation contains a hyphen, it's sufficient to check
                        // the part after the hyphen
                        int hyphenPos = oneTok.lastIndexOf("-");
                        if (hyphenPos != -1) {
                            oneTok = oneTok.substring(hyphenPos + 1);
                        }

                        // check with matchers
                        if (abbrevMatcher.matches(oneTok)) {
                            continue;
                        }

                        // check with lists
                        boolean found = false;
                        for (Map.Entry<String, Set<String>> oneEntry : abbrevLists.entrySet()) {
                            Set<String> oneList = oneEntry.getValue();
                            if (oneList.contains(oneTok)) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            continue;
                        }

                        // check with terms;
                        // convert first letter to upper case because this is the format of
                        // the terms in the list and remove the punctuation
                        char firstChar = oneTok.charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        String tempTok = firstChar + oneTok.substring(1, oneTok.length() - 1);
                        if (nonCapTerms.contains(tempTok)) {
                            continue;
                        }

                        // we found a new abbreviation
                        abbrevs.add(oneTok);
                    }
                }
            }
            in.close();
        }

        // sort collected abbreviations
        List<String> sortedAbbrevs = new ArrayList<String>(abbrevs);
        Collections.sort(sortedAbbrevs);

        // save results
        PrintWriter out = null;
        try {
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(resultFileName), "utf-8")));
            for (String oneAbbrev : sortedAbbrevs) {
                out.println(oneAbbrev);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }


    /**
     * This is the main method. It requires 5 arguments:
     * <ul>
     * <li>the parent folder of the corpus
     * <li>the file extension of the corpus files to use
     * <li>the file encoding
     * <li>the result file name
     * <li>the language of the corpus
     * </ul>
     *
     * @param args an array with the arguments
     */
    public static void main(String[] args) {

        if (args.length != 5) {
            System.err.println("wrong number of arguments");
            System.exit(1);
        }

        try {
            AbbrevCollector.collect(args[0], args[1], args[2], args[3], args[4]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

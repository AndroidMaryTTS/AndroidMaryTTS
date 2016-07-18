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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import marytts.server.Mary;

/**
 * Manages the content of a abbreviation description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class AbbrevDescription extends Description {

    /**
     * class name for breaking abbreviation
     */
    public static final String B_ABBREVIATION = "B_ABBREVIATION";

    /**
     * name of the all abbreviation rule
     */
    protected static final String ALL_RULE = "ALL_RULE";

    // name suffix of the resource file with the abbreviations description
    private static final String ABBREV_DESCR = "_abbrev.cfg";


    // the most common terms that only start with a capital letter when they are at the beginning
    // of a sentence
    private Set<String> nonCapTerms;


    /**
     * Creates a new instance of {@link AbbrevDescription} for the given language.
     *
     * @param resourceDir path to the folder with the language resources
     * @param lang        the language
     * @param macrosMap   a map of macro names to regular expression strings
     * @throws IOException if there is an error when reading the configuration
     */
    public AbbrevDescription(String resourceDir, String lang, Map<String, String> macrosMap)
            throws IOException {

        super.setDefinitionsMap(new HashMap<String, RegExp>());
        super.setRulesMap(new HashMap<String, RegExp>());
        super.setRegExpMap(new HashMap<RegExp, String>());
        super.setClassMembersMap(new HashMap<String, Set<String>>());


        //classpathLocation=classpathLocation.substring(1, classpathLocation.length());

        //    stream = MainActivity.getContext().getAssets().open(classpathLocation);
//abbrDescrPath

        String abbrDescrPath = resourceDir + "/" + lang + ABBREV_DESCR;
        Log.d(Mary.LOG, "jtok, abbrDescrPath = " + abbrDescrPath);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(FileTools.openResourceFileAsStream(abbrDescrPath)));

        // read config file to lists start
        readToLists(in);

        // read lists
        super.loadLists(in, resourceDir);

        // read definitions
        Map<String, String> defsMap = new HashMap<>();
        super.loadDefinitions(in, macrosMap, defsMap);

        getRulesMap().put(ALL_RULE, createAllRule(defsMap));

        in.close();

        // load list of terms that only start with a capital letter when they are
        // at the beginning of a sentence
        String nonCapTermsPath = resourceDir + "/" + lang + "_nonCapTerms.txt";
        Log.d(Mary.LOG, "jtok, nonCapTermsPath=" + nonCapTermsPath);
        BufferedReader nonCopTermsIn = new BufferedReader(
                new InputStreamReader(
                        FileTools.openResourceFileAsStream(nonCapTermsPath.toString()),
                        "utf-8"));

        readNonCapTerms(nonCopTermsIn);

        nonCopTermsIn.close();
    }


    /**
     * Returns the set of the most common terms that only start with a capital letter when they are at
     * the beginning of a sentence.
     *
     * @return a set with the terms
     */
    protected Set<String> getNonCapTerms() {

        return this.nonCapTerms;
    }


    /**
     * Reads the list of terms that only start with a capital letter when they are at the beginning of
     * a sentence from the given reader.<br>
     * Immediately returns if the reader is {@code null}.
     *
     * @param in the reader
     * @throws IOException if there is an error when reading
     */
    private void readNonCapTerms(BufferedReader in)
            throws IOException {

        if (null == in) {
            return;
        }

        // init set where to store the terms
        this.nonCapTerms = new HashSet<String>();

        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            // ignore lines starting with #
            if (line.startsWith("#") || (line.length() == 0)) {
                continue;
            }
            // extract the term and add it to the set
            int end = line.indexOf('#');
            if (-1 != end) {
                line = line.substring(0, end).trim();
                if (line.length() == 0) {
                    continue;
                }
            }

            // convert first letter to upper case to make runtime comparison more
            // efficient
            char firstChar = line.charAt(0);
            firstChar = Character.toUpperCase(firstChar);
            this.nonCapTerms.add(firstChar + line.substring(1));
            // also add a version completely in upper case letters
            this.nonCapTerms.add(line.toUpperCase());
        }
    }
}

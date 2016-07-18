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
import java.util.Map;

import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import marytts.server.Mary;

/**
 * Manages the content of a punctuation description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class PunctDescription extends Description {

    /**
     * class name for opening punctuation
     */
    public static final String OPEN_PUNCT = "OPEN_PUNCT";

    /**
     * class name for closing punctuation
     */
    public static final String CLOSE_PUNCT = "CLOSE_PUNCT";

    /**
     * class name for opening brackets
     */
    public static final String OPEN_BRACKET = "OPEN_BRACKET";

    /**
     * class name for closing brackets
     */
    public static final String CLOSE_BRACKET = "CLOSE_BRACKET";

    /**
     * class name for terminal punctuation
     */
    public static final String TERM_PUNCT = "TERM_PUNCT";

    /**
     * class name for possible terminal punctuation
     */
    public static final String TERM_PUNCT_P = "TERM_PUNCT_P";


    /**
     * name of the all punctuation rule
     */
    protected static final String ALL_RULE = "ALL_PUNCT_RULE";

    /**
     * name of the internal punctuation rule
     */
    protected static final String INTERNAL_RULE = "INTERNAL_PUNCT_RULE";

    /**
     * name of the sentence internal punctuation rule
     */
    protected static final String INTERNAL_TU_RULE = "INTERNAL_TU_PUNCT_RULE";

    /**
     * class name for ambiguous open/close punctuation
     */
    protected static final String OPEN_CLOSE_PUNCT = "OPENCLOSE_PUNCT";

    // name suffix of the resource file with the punctuation description
    private static final String PUNCT_DESCR = "_punct.cfg";


    /**
     * Creates a new instance of {@link PunctDescription} for the given language.
     *
     * @param resourceDir path to the folder with the language resources
     * @param lang        the language
     * @param macrosMap   a map of macro names to regular expression strings
     * @throws IOException if there is an error when reading the configuration
     */
    public PunctDescription(String resourceDir, String lang, Map<String, String> macrosMap)
            throws IOException {

        super.setDefinitionsMap(new HashMap<String, RegExp>());
        super.setRulesMap(new HashMap<String, RegExp>());
        super.setRegExpMap(new HashMap<RegExp, String>());

        String punctDescrPath = resourceDir + "/" + lang + PUNCT_DESCR;
        Log.d(Mary.LOG, "jtok, punctDescrPath=" + punctDescrPath);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        FileTools.openResourceFileAsStream(punctDescrPath.toString()),
                        "utf-8"));

        // read config file to definitions start
        readToDefinitions(in);

        // read definitions
        Map<String, String> defsMap = new HashMap<>();
        super.loadDefinitions(in, macrosMap, defsMap);

        // when loadDefinitions returns the reader has reached the rules section;
        // read rules
        super.loadRules(in, defsMap, macrosMap);

        getRulesMap().put(ALL_RULE, createAllRule(defsMap));

        in.close();
    }
}

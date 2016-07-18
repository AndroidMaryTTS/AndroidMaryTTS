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
 * Manages the content of a clitics description file.
 *
 * @author Joerg Steffen, DFKI
 */
public class CliticsDescription extends Description {

    /**
     * name of the proclitic rule
     */
    protected static final String PROCLITIC_RULE = "PROCLITIC_RULE";

    /**
     * name of the enclitic rule
     */
    protected static final String ENCLITIC_RULE = "ENCLITIC_RULE";


    // name suffix of the resource file with the clitic description
    private static final String CLITIC_DESCR = "_clitics.cfg";


    /**
     * Creates a new instance of {@link CliticsDescription} for the given language.
     *
     * @param resourceDir path to the folder with the language resources
     * @param lang        the language
     * @param macrosMap   a map of macro names to regular expression strings
     * @throws IOException if there is an error when reading the configuration
     */
    public CliticsDescription(String resourceDir, String lang, Map<String, String> macrosMap)
            throws IOException {

        super.setDefinitionsMap(new HashMap<String, RegExp>());
        super.setRulesMap(new HashMap<String, RegExp>());
        super.setRegExpMap(new HashMap<RegExp, String>());

        String clitDescrPath = resourceDir + "/" + lang + CLITIC_DESCR;
        Log.d(Mary.LOG, "jtok, clitDescrPath=" + clitDescrPath);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        FileTools.openResourceFileAsStream(clitDescrPath),
                        "utf-8"));

        // read config file to definitions start
        readToDefinitions(in);

        // read definitions
        Map<String, String> defsMap = new HashMap<>();
        super.loadDefinitions(in, macrosMap, defsMap);

        // when loadDefinitions returns the reader has reached the rules section;
        // read rules
        super.loadRules(in, defsMap, macrosMap);

        in.close();
    }
}

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

package de.dfki.lt.tools.tokenizer.regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;

/**
 * Implements the {@link RegExp} interface for regular expressions of the java.util.regex package.
 *
 * @author Joerg Steffen, DFKI
 */
public class JavaRegExp implements RegExp {

    // instance of a regular expression in the java.util.regex package
    private Pattern re;


    /**
     * Creates a new instance of {@link JavaRegExp} for the given regular expression string.
     *
     * @param regExpString a regular expression string
     * @throws InitializationException if regular expression is not well formed
     */
    public JavaRegExp(String regExpString) {

        try {
            this.re = Pattern.compile(regExpString);
        } catch (PatternSyntaxException pse) {
            throw new InitializationException(pse.getLocalizedMessage(), pse);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Match> getAllMatches(String input) {

        // create Matcher for input
        Matcher matcher = this.re.matcher(input);
        // convert matches and collect them in a list
        List<Match> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new Match(matcher.start(), matcher.end(), matcher.group()));
        }
        // return result
        return matches;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String input) {

        // create Matcher for input
        Matcher matcher = this.re.matcher(input);
        return matcher.matches();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Match contains(String input) {

        // create Matcher for input
        Matcher matcher = this.re.matcher(input);
        if (matcher.find()) {
            return new Match(matcher.start(), matcher.end(), matcher.group());
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Match starts(String input) {

        // create Matcher for input
        Matcher matcher = this.re.matcher(input);
        if (matcher.find() && matcher.start() == 0) {
            return new Match(matcher.start(), matcher.end(), matcher.group());
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Match ends(String input) {

        // create Matcher for input
        Matcher matcher = this.re.matcher(input);
        // get the last match
        Match match = null;
        while (matcher.find()) {
            match = new Match(matcher.start(), matcher.end(), matcher.group());
        }
        if (match != null && match.getEndIndex() == input.length()) {
            return match;
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return this.re.toString();
    }
}

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

import java.util.List;

/**
 * Interface for regular expression patterns.
 *
 * @author Joerg Steffen, DFKI
 */
public interface RegExp {

    /**
     * Returns a list with all matches for the regular expression in the given input.
     *
     * @param input the string where to look for matches
     * @return a list of matches
     */
    List<Match> getAllMatches(String input);


    /**
     * Checks if the regular expression matches the given input in its entirety.
     *
     * @param input the string to check
     * @return a flag indicating the match
     */
    boolean matches(String input);


    /**
     * Checks if the given input contains a match for the regular expression. If yes, the first match
     * is returned, {@code null} otherwise.
     *
     * @param input the string to check
     * @return a match or {@code null}
     */
    Match contains(String input);


    /**
     * Checks if the given input contains a match for the regular expression at the start of the
     * input. If yes, the match is returned, {@code null} otherwise.
     *
     * @param input the string to check
     * @return a match or {@code null}
     */
    Match starts(String input);


    /**
     * Checks if the given input contains a match for the regular expression at the end of the input.
     * If yes, the match is returned, {@code null} otherwise.
     *
     * @param input the string to check
     * @return a match or {@code null}
     */
    Match ends(String input);
}

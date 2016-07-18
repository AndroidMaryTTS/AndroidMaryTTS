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

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;

/**
 * Abstract class for creating objects that fit the {@link RegExp} interface.
 *
 * @author Joerg Steffen, DFKI
 */
public abstract class RegExpFactory {

    /**
     * Creates a regular expression object from the given regular expression string.
     *
     * @param regExpString a regular expression string
     * @return a regular expression build from the regular expression string
     * @throws InitializationException if regular expression is not well formed
     */
    public abstract RegExp createRegExp(String regExpString);
}

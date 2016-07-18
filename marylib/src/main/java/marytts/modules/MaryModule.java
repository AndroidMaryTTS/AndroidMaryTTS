/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.modules;

// DOM classes

import java.util.Locale;

import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;

/**
 * A generic interface for Mary Modules.
 * This interface defines the communication of the Mary.java main program
 * with the individual modules.
 * <p/>
 * The main program calls
 * <ul>
 * <li> <code>startup()</code> once after module instantiation, </li>
 * <li> <code>process()</code> many times, from different threads,
 * possibly at the same time, during the lifetime of the server </li>
 * <li> <code>shutdown()</code> once, at the end of the program. </li>
 * </ul>
 *
 * @author Marc Schr&ouml;der
 */
public interface MaryModule {
    int MODULE_OFFLINE = 0;
    int MODULE_RUNNING = 1;

    /**
     * This module's name, as free text, for example "Tokeniser"
     */
    String name();

    /**
     * The type of input data needed by this module.
     */
    MaryDataType inputType();

    /**
     * The type of output data produced by this module.
     */
    MaryDataType outputType();

    /**
     * The locale of this module, i.e. the locale of data that this
     * module can process. If null, indicates that the module can use
     * data of any locale (i.e., the module is language-independent.)
     *
     * @return the locale of this module, if any, or null
     */
    Locale getLocale();

    /**
     * Allow the module to start up, performing whatever is necessary
     * to become operational. After successful completion, getState()
     * should return MODULE_RUNNING.
     */
    void startup() throws Exception;

    /**
     * Inform about the state of this module.
     *
     * @return an int identifying the state of this module, either MODULE_OFFLINE or MODULE_RUNNING.
     */
    int getState();


    /**
     * Perform a power-on self test by processing some example input data.
     *
     * @throws Error if the module does not work properly.
     */
    void powerOnSelfTest() throws Error;

    /**
     * Allow the module to shut down cleanly. After this has successfully completed,
     * getState() should return MODULE_OFFLINE.
     */
    void shutdown();


    /**
     * Perform this module's processing on abstract "MaryData" input
     * <code>d</code>.
     * Classes implementing this interface need to make the
     * <code>process()</code>
     * method thread-safe, because in server-mode,
     * it will be called from different
     * threads at the same time.
     * <p/>
     * The result is returned encapsulated in a MaryData object of type
     * <code>outputType()</code>.
     * <p/>
     * This method should never return <code> null </code>; in case of a
     * failure, an exception should be thrown.
     */
    MaryData process(MaryData d) throws Exception;
}


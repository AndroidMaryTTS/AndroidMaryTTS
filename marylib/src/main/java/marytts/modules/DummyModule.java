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

import java.util.Locale;

import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;


/**
 * A dummy module doing nothing.
 *
 * @author Marc Schr&ouml;der
 */

public class DummyModule implements MaryModule {
    @Override
    public String name() {
        return "Dummy";
    }

    @Override
    public MaryDataType inputType() {
        return MaryDataType.MBROLA;
    }

    @Override
    public MaryDataType outputType() {
        return MaryDataType.AUDIO;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void startup() throws Exception {
    }

    @Override
    public void powerOnSelfTest() throws Error {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public int getState() {
        return MODULE_OFFLINE;
    }

    @Override
    public MaryData process(MaryData d) throws Exception {
        return d;
    }

}


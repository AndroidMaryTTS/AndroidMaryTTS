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

import marytts.datatypes.MaryData;

/**
 * @author Marc Schr&ouml;der
 */
public class ExternalModuleRequest {
    private MaryData input = null;
    private MaryData output = null;
    private boolean problem = false;

    public ExternalModuleRequest(MaryData input) {
        setInput(input);
    }

    public synchronized MaryData getInput() {
        return input;
    }

    private synchronized void setInput(MaryData input) {
        this.input = input;
    }

    public synchronized MaryData getOutput() {
        return output;
    }

    public synchronized void setOutput(MaryData output) {
        this.output = output;
    }

    public synchronized boolean problemOccurred() {
        return problem;
    }

    public synchronized void setProblemOccurred(boolean problem) {
        this.problem = problem;
    }

}


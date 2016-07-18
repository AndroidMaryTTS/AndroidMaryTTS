/**
 * Copyright 2004-2006 DFKI GmbH.
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
package marytts.signalproc.analysis;

import marytts.signalproc.process.InlineDataProcessor;
import marytts.util.data.BufferedDoubleDataSource;
import marytts.util.data.DoubleDataSource;

/**
 * @author Marc Schr&ouml;der
 *         <p/>
 *         Computes energy of the input signal
 */
public class Signal2EnergyConverter extends BufferedDoubleDataSource {
    protected static InlineDataProcessor processor = new InlineDataProcessor() {
        /**
         * For each signal sample, compute the signal energy as the square of the signal sample. 
         */
        @Override
        public void applyInline(double[] buf, int off, int len) {
            for (int i = off; i < off + len; i++) {
                buf[i] = buf[i] * buf[i];
                assert buf[i] >= 0;
            }
        }
    };

    public Signal2EnergyConverter(double[] signal) {
        super(signal, processor);
    }

    public Signal2EnergyConverter(DoubleDataSource signal) {
        super(signal, processor);
    }

    /**
     * For each signal sample, compute the signal energy as the square of the signal sample.
     *
     * @param nNew the number of items in buf preceding writePos to process
     */
    public void processNewData(int off, int len) {
        for (int i = off; i < off + len; i++) {
            buf[i] = buf[i] * buf[i];
            assert buf[i] >= 0;
        }
    }

}


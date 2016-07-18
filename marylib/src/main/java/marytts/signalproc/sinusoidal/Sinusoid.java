/**
 * Copyright 2007 DFKI GmbH.
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
package marytts.signalproc.sinusoidal;

/**
 * A single sinusoid
 *
 * @author Oytun T&uumlrk
 */
public class Sinusoid {
    public static int NON_EXISTING_FRAME_INDEX = -1; //Used for sinusoid components added for parameter smoothing
    public float amp; //Amplitude (Total amplitude, i.e. excitation x system)
    public float freq; //Frequency in radians
    public float phase; //Phase
    public int frameIndex; //Frame index

    public Sinusoid(float freqIn) {
        this(1.0f, freqIn, 0.0f);
    }

    public Sinusoid(float ampIn, float freqIn, float phaseIn) {
        this(ampIn, freqIn, phaseIn, NON_EXISTING_FRAME_INDEX);
    }

    public Sinusoid(Sinusoid existingSinusoid) {
        this(existingSinusoid.amp, existingSinusoid.freq, existingSinusoid.phase, existingSinusoid.frameIndex);
    }

    public Sinusoid(float ampIn, float freqIn, float phaseIn, int frameIndexIn) {
        this.amp = ampIn;
        this.freq = freqIn;
        this.phase = phaseIn;
        this.frameIndex = frameIndexIn;
    }
}

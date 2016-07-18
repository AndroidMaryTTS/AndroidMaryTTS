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
package marytts.modules.synthesis;

import java.util.List;

import lib.sound.sampled.AudioInputStream;
import marytts.exceptions.SynthesisException;
import mf.org.w3c.dom.Element;

/**
 * Provide a common interface for all waveform synthesizers, to be called from
 * within the "wrapping" Synthesis module.
 */
public interface WaveformSynthesizer {
    /**
     * Start up the waveform synthesizer. This must be called once before
     * calling synthesize().
     */
    void startup() throws Exception;

    /**
     * Perform a power-on self test by processing some example input data.
     *
     * @throws Error if the module does not work properly.
     */
    void powerOnSelfTest() throws Error;

    /**
     * Synthesize a given part of a MaryXML document. This method is expected
     * to be thread-safe.
     *
     * @param tokensAndBoundaries the part of the MaryXML document to
     *                            synthesize; a list containing a number of adjacent <t> and <boundary>
     *                            elements.
     * @param voice               the Voice to use for synthesis
     * @param outputParams        any specified output parameters; may be null
     * @return an AudioInputStream in synthesizer-native audio format.
     * @throws IllegalArgumentException if the voice requested for this section
     *                                  is incompatible with this WaveformSynthesizer.
     */
    AudioInputStream synthesize(List<Element> tokensAndBoundaries, Voice voice, String outputParams)
            throws SynthesisException;
}


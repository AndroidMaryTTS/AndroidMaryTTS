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
package marytts.util.data.audio;

import java.io.ByteArrayInputStream;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;

public class SilenceAudioInputStream extends AudioInputStream {
    /**
     * Construct an audio input stream from which <code>duration</code> seconds
     * of silence can be read.
     *
     * @param duration the desired duration of the silence, in seconds
     * @param format   the desired audio format of the audio input stream. getFrameSize() and getFrameRate()
     *                 must return meaningful values.
     */
    public SilenceAudioInputStream(double duration, AudioFormat format) {
        super(new ByteArrayInputStream(new byte[(int) (format.getFrameSize() * format.getFrameRate() * duration)]),
                format, (long) (format.getFrameRate() * duration));
    }

}


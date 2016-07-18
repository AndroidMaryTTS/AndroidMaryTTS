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

import java.io.File;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import marytts.util.data.audio.AudioDoubleDataSource;
import marytts.util.string.PrintfFormat;


/**
 * @author Marc Schr&ouml;der
 *         <p/>
 *         Prints durations of audio files that are specified as fullpath arguments in String[] args
 */
public class AudioFileDuration {

    public static void main(String[] args) throws Exception {
        PrintfFormat format = new PrintfFormat("%.4f");
        for (int file = 0; file < args.length; file++) {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(args[file]));
            if (!ais.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
                ais = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, ais);
            }
            float samplingRate = ais.getFormat().getSampleRate();
            double[] signal = new AudioDoubleDataSource(ais).getAllData();
            float duration = signal.length / samplingRate;
            System.out.println(args[file] + ": " + format.sprintf(duration) + " s");
        }
    }
}


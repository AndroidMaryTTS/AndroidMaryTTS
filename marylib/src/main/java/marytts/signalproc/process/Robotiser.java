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
package marytts.signalproc.process;

import java.io.File;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import marytts.signalproc.window.Window;
import marytts.util.data.BufferedDoubleDataSource;
import marytts.util.data.DoubleDataSource;
import marytts.util.data.audio.AudioDoubleDataSource;
import marytts.util.data.audio.DDSAudioInputStream;
import marytts.util.signal.SignalProcUtils;


/**
 * @author Marc Schr&ouml;der
 *         <p/>
 *         Create a robot-like impression on the output, by setting all phases to zero in each frame. This effectively
 *         creates a pitch equalling the output frame shift.
 */
public class Robotiser extends FrameOverlapAddSource {
    /*
     * @param inputSource
     * @param frameLength
     * @param samplingRate
     * @param rateChangeFactor the factor by which to speed up or slow down the source.
     * Values greater than one will speed up, values smaller than one will slow down the original.
     */
    public Robotiser(DoubleDataSource inputSource, int samplingRate, float amount) {
        //int frameLength = Integer.getInteger("signalproc.robotiser.framelength", 256).intValue();
        int frameLength = SignalProcUtils.getDFTSize(samplingRate);
        initialise(inputSource, Window.HANNING, true, frameLength, samplingRate, new PhaseRemover(frameLength, amount));
    }

    public Robotiser(DoubleDataSource inputSource, int samplingRate) {
        this(inputSource, samplingRate, 1.0f);
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            AudioInputStream inputAudio = AudioSystem.getAudioInputStream(new File(args[i]));
            int samplingRate = (int) inputAudio.getFormat().getSampleRate();
            AudioDoubleDataSource signal = new AudioDoubleDataSource(inputAudio);
            Robotiser pv = new Robotiser(signal, samplingRate);
            DDSAudioInputStream outputAudio = new DDSAudioInputStream(new BufferedDoubleDataSource(pv), inputAudio.getFormat());
            String outFileName = args[i].substring(0, args[i].length() - 4) + "_robotised.wav";
            AudioSystem.write(outputAudio, AudioFileFormat.Type.WAVE, new File(outFileName));
        }
    }

    public static class PhaseRemover extends PolarFrequencyProcessor {
        public PhaseRemover(int fftSize, double amount) {
            super(fftSize, amount);
        }

        public PhaseRemover(int fftSize) {
            this(fftSize, 1.0);
        }

        /**
         * Perform the random manipulation.
         *
         * @param r
         * @param phi
         */
        @Override
        protected void processPolar(double[] r, double[] phi) {
            for (int i = 0; i < phi.length; i++) {
                phi[i] = 0;
            }
        }

    }
}


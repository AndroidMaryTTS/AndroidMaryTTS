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
package marytts.signalproc.filter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import lib.sound.sampled.UnsupportedAudioFileException;
import marytts.util.data.BufferedDoubleDataSource;
import marytts.util.data.audio.AudioDoubleDataSource;
import marytts.util.data.audio.DDSAudioInputStream;
import marytts.util.signal.SignalProcUtils;

/**
 * @author Oytun T&uumlrk
 */
public class FIRWaveletFilterBankSynthesiser {
    public FIRWaveletFilterBankSynthesiser() {

    }

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        AudioInputStream inputAudio = AudioSystem.getAudioInputStream(new File(args[0]));
        int samplingRate = (int) inputAudio.getFormat().getSampleRate();
        AudioDoubleDataSource signal = new AudioDoubleDataSource(inputAudio);
        double[] x = signal.getAllData();

        int i;
        int numLevels = 3;
        boolean bNormalizeInOverlappingRegions = false;

        FIRWaveletFilterBankAnalyser analyser = new FIRWaveletFilterBankAnalyser(numLevels, samplingRate);
        Subband[] subbands = analyser.apply(x);

        DDSAudioInputStream outputAudio;
        AudioFormat outputFormat;
        String outFileName;

        //Write highpass components 0 to numLevels-1
        for (i = 0; i < subbands.length; i++) {
            outputFormat = new AudioFormat((int) (subbands[i].samplingRate), inputAudio.getFormat().getSampleSizeInBits(), inputAudio.getFormat().getChannels(), true, true);
            outputAudio = new DDSAudioInputStream(new BufferedDoubleDataSource(subbands[i].waveform), outputFormat);
            outFileName = args[0].substring(0, args[0].length() - 4) + "_band" + String.valueOf(i + 1) + ".wav";
            AudioSystem.write(outputAudio, AudioFileFormat.Type.WAVE, new File(outFileName));
        }

        FIRWaveletFilterBankSynthesiser synthesiser = new FIRWaveletFilterBankSynthesiser();
        double[] y = synthesiser.apply(analyser, subbands, bNormalizeInOverlappingRegions);

        outputFormat = new AudioFormat(samplingRate, inputAudio.getFormat().getSampleSizeInBits(), inputAudio.getFormat().getChannels(), true, true);
        outputAudio = new DDSAudioInputStream(new BufferedDoubleDataSource(y), outputFormat);
        outFileName = args[0].substring(0, args[0].length() - 4) + "_resynthesis" + ".wav";
        AudioSystem.write(outputAudio, AudioFileFormat.Type.WAVE, new File(outFileName));
    }

    public double[] apply(FIRWaveletFilterBankAnalyser analyser, Subband[] subbands, boolean bNormalizeInOverlappingRegions) {
        double[] x = null;
        double[] lowBandIntFilt = null;
        double[] highBandIntFilt = null;

        if (analyser != null && analyser.filters != null && subbands != null) {
            int i, j;
            for (i = subbands.length - 2; i >= 0; i--) {
                if (i == subbands.length - 2)
                    lowBandIntFilt = SignalProcUtils.interpolate(subbands[i + 1].waveform, 2.0);
                else
                    lowBandIntFilt = SignalProcUtils.interpolate(x, 2.0);

                lowBandIntFilt = analyser.filters[i][0].apply(lowBandIntFilt);

                highBandIntFilt = SignalProcUtils.interpolate(subbands[i].waveform, 2.0);
                highBandIntFilt = analyser.filters[i][1].apply(highBandIntFilt);

                x = new double[Math.max(lowBandIntFilt.length, highBandIntFilt.length)];
                Arrays.fill(x, 0.0);
                System.arraycopy(lowBandIntFilt, 0, x, 0, lowBandIntFilt.length);
                for (j = 0; j < highBandIntFilt.length; j++)
                    x[j] += highBandIntFilt[j];

                if (bNormalizeInOverlappingRegions)
                    x = SignalProcUtils.filterfd(analyser.normalizationFilterTransformedIRs[i], x, 2.0 * analyser.samplingRates[i]);
            }
        }

        return x;
    }
}


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
package marytts.signalproc.effects;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioSystem;
import marytts.signalproc.process.FrameOverlapAddSource;
import marytts.signalproc.process.VocalTractScalingProcessor;
import marytts.signalproc.window.Window;
import marytts.util.data.BufferedDoubleDataSource;
import marytts.util.data.DoubleDataSource;
import marytts.util.data.audio.AudioDoubleDataSource;
import marytts.util.data.audio.DDSAudioInputStream;
import marytts.util.math.MathUtils;
import marytts.util.signal.SignalProcUtils;

/**
 * @author Oytun T&uumlrk
 */
public class VocalTractLinearScalerEffect extends BaseAudioEffect {

    public static float MAX_AMOUNT = 4.0f;
    public static float MIN_AMOUNT = 0.25f;
    public static float DEFAULT_AMOUNT = 1.5f;
    float amount;

    public VocalTractLinearScalerEffect() {
        this(16000);
    }

    public VocalTractLinearScalerEffect(int samplingRate) {
        super(samplingRate);

        setExampleParameters("amount" + chParamEquals + Float.toString(DEFAULT_AMOUNT) + chParamSeparator);

        strHelpText = getHelpText();
    }

    /**
     * Command line interface to the vocal tract linear scaler effect.
     *
     * @param args the command line arguments. Exactly two arguments are expected:
     *             (1) the factor by which to scale the vocal tract (between 0.25 = very long and 4.0 = very short vocal tract);
     *             (2) the filename of the wav file to modify.
     *             Will produce a file basename_factor.wav, where basename is the filename without the extension.
     * @throws Exception if processing fails for some reason.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java " + VocalTractLinearScalerEffect.class.getName() + " <factor> <filename>");
            System.exit(1);
        }
        float factor = Float.parseFloat(args[0]);
        String filename = args[1];
        AudioDoubleDataSource input = new AudioDoubleDataSource(AudioSystem.getAudioInputStream(new File(filename)));
        AudioFormat format = input.getAudioFormat();
        VocalTractLinearScalerEffect effect = new VocalTractLinearScalerEffect((int) format.getSampleRate());
        DoubleDataSource output = effect.apply(input, "amount:" + factor);
        DDSAudioInputStream audioOut = new DDSAudioInputStream(output, format);
        String outFilename = FilenameUtils.removeExtension(filename) + "_" + factor + ".wav";
        AudioSystem.write(audioOut, AudioFileFormat.Type.WAVE, new File(outFilename));
        System.out.println("Created file " + outFilename);
    }

    @Override
    public void parseParameters(String param) {
        super.parseParameters(param);

        amount = expectFloatParameter("amount");

        if (amount == NULL_FLOAT_PARAM)
            amount = DEFAULT_AMOUNT;
    }

    @Override
    public DoubleDataSource process(DoubleDataSource inputAudio) {
        amount = MathUtils.CheckLimits(amount, MIN_AMOUNT, MAX_AMOUNT);

        double[] vscales = {amount};

        int frameLength = SignalProcUtils.getDFTSize(fs);
        int predictionOrder = SignalProcUtils.getLPOrder(fs);

        VocalTractScalingProcessor p = new VocalTractScalingProcessor(predictionOrder, fs, frameLength, vscales);
        FrameOverlapAddSource foas = new FrameOverlapAddSource(inputAudio, Window.HANNING, true, frameLength, fs, p);

        return new BufferedDoubleDataSource(foas);
    }

    @Override
    public String getHelpText() {

        String strHelp = "Vocal Tract Linear Scaling Effect:" + strLineBreak +
                "Creates a shortened or lengthened vocal tract effect by shifting the formants." + strLineBreak +
                "Parameter:" + strLineBreak +
                "   <amount>" +
                "   Definition : The amount of formant shifting" + strLineBreak +
                "   Range      : [" + String.valueOf(MIN_AMOUNT) + "," + String.valueOf(MAX_AMOUNT) + "]" + strLineBreak +
                "   For values of <amount> less than 1.0, the formants are shifted to lower frequencies" + strLineBreak +
                "       resulting in a longer vocal tract (i.e. a deeper voice)." + strLineBreak +
                "   Values greater than 1.0 shift the formants to higher frequencies." + strLineBreak +
                "       The result is a shorter vocal tract.\n" + strLineBreak +
                "Example:" + strLineBreak +
                getExampleParameters();

        return strHelp;
    }

    @Override
    public String getName() {
        return "TractScaler";
    }
}


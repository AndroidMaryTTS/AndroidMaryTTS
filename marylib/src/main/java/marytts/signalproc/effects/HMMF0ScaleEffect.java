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

import marytts.util.data.DoubleDataSource;
import marytts.util.math.MathUtils;

/**
 * @author Oytun T&uumlrk
 */
public class HMMF0ScaleEffect extends BaseAudioEffect {
    public static float NO_MODIFICATION = 1.0f;
    public static float DEFAULT_F0_SCALE = 2.0f;
    public static float MAX_F0_SCALE = 3.0f;
    public static float MIN_F0_SCALE = 0.0f;
    public float f0Scale;

    public HMMF0ScaleEffect() {
        super(16000);

        setHMMEffect(true);

        setExampleParameters("f0Scale" + chParamEquals + Float.toString(DEFAULT_F0_SCALE) + chParamSeparator);
    }

    @Override
    public void parseParameters(String param) {
        super.parseParameters(param);

        f0Scale = expectFloatParameter("f0Scale");

        if (f0Scale == NULL_FLOAT_PARAM)
            f0Scale = DEFAULT_F0_SCALE;

        f0Scale = MathUtils.CheckLimits(f0Scale, MIN_F0_SCALE, MAX_F0_SCALE);
    }

    //Actual processing is done wthin the HMM synthesizer so do nothing here
    @Override
    public DoubleDataSource process(DoubleDataSource input) {
        return input;
    }

    @Override
    public String getHelpText() {

        String strHelp = "F0 scaling effect for HMM voices:" + strLineBreak +
                "All voiced f0 values are multiplied by <f0Scale> for HMM voices." + strLineBreak +
                "This operation effectively scales the range of f0 values." + strLineBreak +
                "Note that mean f0 is preserved during the operation." + strLineBreak +
                "Parameter:" + strLineBreak +
                "   <f0Scale>" +
                "   Definition : Scale ratio for modifying the dynamic range of the f0 contour" + strLineBreak +
                "                If f0Scale>1.0, the range is expanded (i.e. voice with more variable pitch)" + strLineBreak +
                "                If f0Scale<1.0, the range is compressed (i.e. more monotonic voice)" + strLineBreak +
                "                If f0Scale=1.0 results in no changes in range" + strLineBreak +
                "   Range      : [" + String.valueOf(MIN_F0_SCALE) + "," + String.valueOf(MAX_F0_SCALE) + "]" + strLineBreak +
                "Example:" + strLineBreak +
                getExampleParameters();

        return strHelp;
    }

    @Override
    public String getName() {
        return "F0Scale";
    }
}


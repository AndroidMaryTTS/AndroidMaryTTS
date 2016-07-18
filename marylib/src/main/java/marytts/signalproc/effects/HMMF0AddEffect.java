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
public class HMMF0AddEffect extends BaseAudioEffect {
    public static float NO_MODIFICATION = 0.0f;
    public static float DEFAULT_F0_ADD = 50.0f;
    public static float MAX_F0_ADD = 300.0f;
    public static float MIN_F0_ADD = -300.0f;
    public float f0Add;

    public HMMF0AddEffect() {
        super(16000);

        setHMMEffect(true);

        setExampleParameters("f0Add" + chParamEquals + Float.toString(DEFAULT_F0_ADD) + chParamSeparator);
    }

    @Override
    public void parseParameters(String param) {
        super.parseParameters(param);

        f0Add = expectFloatParameter("f0Add");

        if (f0Add == NULL_FLOAT_PARAM)
            f0Add = DEFAULT_F0_ADD;

        f0Add = MathUtils.CheckLimits(f0Add, MIN_F0_ADD, MAX_F0_ADD);
    }

    //Actual processing is done within the HMM synthesizer so do nothing here
    @Override
    public DoubleDataSource process(DoubleDataSource input) {
        return input;
    }

    @Override
    public String getHelpText() {

        String strHelp = "F0 mean shifting effect for HMM voices:" + strLineBreak +
                "Shifts the mean F0 value by <f0Add> Hz for HMM voices." + strLineBreak +
                "Parameter:" + strLineBreak +
                "   <f0Add>" +
                "   Definition : F0 shift of mean value in Hz for synthesized speech output" + strLineBreak +
                "   Range      : [" + String.valueOf(MIN_F0_ADD) + "," + String.valueOf(MAX_F0_ADD) + "]" + strLineBreak +
                "Example:" + strLineBreak +
                getExampleParameters();

        return strHelp;
    }

    @Override
    public String getName() {
        return "F0Add";
    }
}


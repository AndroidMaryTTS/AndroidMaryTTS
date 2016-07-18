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

import marytts.signalproc.process.Robotiser;
import marytts.util.data.BufferedDoubleDataSource;
import marytts.util.data.DoubleDataSource;
import marytts.util.math.MathUtils;

/**
 * @author Oytun T&uumlrk
 */
public class RobotiserEffect extends BaseAudioEffect {
    public static float DEFAULT_AMOUNT = 100.0f;
    public static float MAX_AMOUNT = 100.0f;
    public static float MIN_AMOUNT = 0.0f;
    float amount;

    public RobotiserEffect() {
        this(16000);
    }

    public RobotiserEffect(int samplingRate) {
        super(samplingRate);

        //setExampleParameters("amount" + chEquals + "100.0" + chSeparator);
        setExampleParameters("amount=100.0,");

        strHelpText = getHelpText();
    }

    @Override
    public void parseParameters(String param) {
        super.parseParameters(param);

        amount = expectFloatParameter("amount");

        if (amount == NULL_FLOAT_PARAM)
            amount = DEFAULT_AMOUNT;

        amount = MathUtils.CheckLimits(amount, MIN_AMOUNT, MAX_AMOUNT);
    }

    @Override
    public DoubleDataSource process(DoubleDataSource input) {
        Robotiser robotiser = new Robotiser(input, fs, amount / 100.0f);
        return new BufferedDoubleDataSource(robotiser);
    }

    @Override
    public String getHelpText() {

        String strHelp = "Robotiser Effect:" + strLineBreak +
                "Creates a robotic voice by setting all phases to zero." + strLineBreak +
                "Parameter:" + strLineBreak +
                "   <amount>" +
                "   Definition : The amount of robotic voice at the output" + strLineBreak +
                "   Range      : [" + String.valueOf(MIN_AMOUNT) + "," + String.valueOf(MAX_AMOUNT) + "]" + strLineBreak +
                "Example:" + strLineBreak +
                getExampleParameters();

        return strHelp;
    }

    @Override
    public String getName() {
        return "Robot";
    }
}


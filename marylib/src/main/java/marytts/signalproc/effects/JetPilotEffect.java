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

/**
 * @author Oytun T&uumlrk
 */
public class JetPilotEffect extends FilterEffectBase {

    public JetPilotEffect() {
        this(16000);
    }

    public JetPilotEffect(int samplingRate) {
        super(500.0, 2000.0, samplingRate, BANDPASS_FILTER);

        setExampleParameters("");

        strHelpText = getHelpText();
    }


    @Override
    public void parseParameters(String param) {
        initialise();
    }

    @Override
    public String getHelpText() {

        String strHelp = "Jet pilot effect:" + strLineBreak +
                "Filters the input signal using an FIR bandpass filter." + strLineBreak +
                "Parameters: NONE" + strLineBreak;

        return strHelp;
    }

    @Override
    public String getName() {
        return "JetPilot";
    }
}


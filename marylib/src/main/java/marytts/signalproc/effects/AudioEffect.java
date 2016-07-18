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

/**
 * @author Oytun T&uumlrk
 */
public interface AudioEffect {
    String getName(); //Returns the unique name of the effect

    void setName(String strName); //Sets the unique name of the effect

    String getExampleParameters(); //Returns typical parameters for the effect

    void setExampleParameters(String strExampleParams); //Sets typical parameters for the effect

    String getHelpText(); //Returns the help text for the effect

    String getParamsAsString(); //Returns current parameters with parameter names and values

    //  separated by a parameter separator character and surrounded by
    //  parameter field start and end characters
    String getParamsAsString(boolean bWithParantheses);

    String getFullEffectAsString(); //Returns effect name, current parameters and their values

    String getFullEffectWithExampleParametersAsString(); //Returns name with example parameters and values

    float expectFloatParameter(String strParamName); //Return a float valued parameter from a string in the form param1=val1

    double expectDoubleParameter(String strParamName); //Return a double valued parameter from a string in the form param1=val1

    int expectIntParameter(String strParamName); //Return an integer valued parameter from a string in the form param1=val1

    DoubleDataSource apply(DoubleDataSource input, String param);

    DoubleDataSource process(DoubleDataSource input);

    void setParams(String params);

    String preprocessParams(String params);

    void parseParameters(String param);

    void checkParameters();

    boolean isHMMEffect();
}


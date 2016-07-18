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
import marytts.util.string.StringUtils;

/**
 * @author Oytun T&uumlrk
 */
//Baseline audio effect class
//It serves as a null effect
//It´s main functionality is providing base functions for parsing parameter strings
public class BaseAudioEffect implements AudioEffect {
    public static String strLineBreak = "\n";

    public static float NULL_FLOAT_PARAM = -100000.0f;
    public static double NULL_DOUBLE_PARAM = -100000;
    public static int NULL_INT_PARAM = -100000;
    public static String NULL_STRING_PARAM = "";
    public static char chParamEquals = ':';
    public static char chParamSeparator = ';';
    public static char chEffectParamStart = '(';
    public static char chEffectParamEnd = ')';
    public String strEffectName = "";
    public String strHelpText = "";
    public String strExampleParameters = "";
    public String[] paramNames;
    public float[] paramVals;
    public String strParams; //To set the parameters using a String generated from outside
    public int fs;
    private boolean isHMMEffect;

    public BaseAudioEffect(BaseAudioEffect existing) {
        int i;
        fs = existing.fs;
        if (existing.paramNames != null && existing.paramNames.length > 0) {
            paramNames = new String[existing.paramNames.length];

            for (i = 0; i < paramNames.length; i++)
                paramNames[i] = existing.paramNames[i];
        } else
            paramNames = null;

        if (existing.paramVals != null && existing.paramVals.length > 0) {
            paramVals = new float[existing.paramVals.length];

            for (i = 0; i < paramVals.length; i++)
                paramVals[i] = existing.paramVals[i];
        } else
            paramVals = null;

        strEffectName = existing.strEffectName;
        strParams = existing.strParams;
        strExampleParameters = existing.strExampleParameters;
        strHelpText = existing.strHelpText;

        setHMMEffect(false); //By default all effects are available to all voices
        //Set to true if the effect is only available to HMM voices
    }

    public BaseAudioEffect(int samplingRate) {
        fs = samplingRate;
        parseParameters(getExampleParameters());
    }

    public BaseAudioEffect(int samplingRate, String strParams) {
        fs = samplingRate;
        parseParameters(strParams);
    }

    public static void main(String[] args) throws Exception {
        BaseAudioEffect b = new BaseAudioEffect(16000, "a1:1.1 ; a2 :2.02; a3: 3.003; a4  :  4.0004  ;a5:5.12345; a6: 6.6   ; a7:7.0a");

        for (int i = 0; i < b.paramNames.length; i++)
            System.out.println(b.paramNames[i] + "=" + String.valueOf(b.paramVals[i]));
    }

    public DoubleDataSource apply(DoubleDataSource input) {
        return apply(input, strParams);
    }

    @Override
    public DoubleDataSource apply(DoubleDataSource input, String param) {
        strParams = param;

        parseParameters(strParams);

        return process(input);
    }

    //This baseline version does nothing, implement functionality in derived classes
    @Override
    public DoubleDataSource process(DoubleDataSource input) {
        return input;
    }

    @Override
    public void setParams(String params) {
        String params2 = preprocessParams(params);

        parseParameters(params2);

        checkParameters();
    }

    //Preprocess to replace "=" with chParamEquals and ";" with chParamSeparator
    @Override
    public String preprocessParams(String params) {
        String params2 = params;
        params2 = params2.replace('=', chParamEquals);
        params2 = params2.replace(',', chParamSeparator);
        params2 = params2.replaceAll(" ", "");

        return params2;
    }

    @Override
    public void parseParameters(String param) {
        if (param != null && param != "") {
            int count = 0;
            int ind = -1;

            while (true) {
                ind = param.indexOf(chParamEquals, ind + 1);
                if (ind > -1)
                    count++;
                else
                    break;
            }

            if (count > 0) {
                int stName = 0;
                int enName = -1;
                int stVal = 0;
                int enVal = -1;
                int ind1 = -1;
                int ind2 = -1;

                paramNames = new String[count];
                paramVals = new float[count];
                String strTmp;

                for (int i = 0; i < count; i++) {
                    ind1 = param.indexOf(chParamEquals, ind2 + 1);
                    ind2 = param.indexOf(chParamSeparator, ind1 + 1);

                    if (ind1 > -1) {
                        enName = ind1 - 1;
                        stVal = ind1 + 1;

                        if (ind2 > -1)
                            enVal = ind2 - 1;
                        else
                            enVal = param.length() - 1;

                        //Extract param and val
                        strTmp = param.substring(stName, enName + 1);
                        strTmp = StringUtils.deblank(strTmp);
                        paramNames[i] = strTmp;

                        strTmp = param.substring(stVal, enVal + 1);
                        strTmp = StringUtils.deblank(strTmp);

                        try {
                            paramVals[i] = StringUtils.string2float(strTmp);
                        } catch (NumberFormatException e) {
                            System.out.println("Error! The parameter should be numeric...");
                        }
                        //

                        stName = ind2 + 1;

                        if (stName > param.length() - 1)
                            break;
                    }
                }

                strParams = this.getParamsAsString(false);
            }
        }
    }

    //Perform parameter range controls in the derived classes
    @Override
    public void checkParameters() {

    }

    //Should return a string containing exemplar parameters in the following form:
    // "param1=1.2; param2=0.5; param3=5;"
    @Override
    public String getExampleParameters() {
        return strExampleParameters;
    }

    @Override
    public void setExampleParameters(String strExampleParams) {

        strExampleParameters = strExampleParams;

        strExampleParameters = preprocessParams(strExampleParameters);
    }

    //Should return a unique name for each derived effect class
    @Override
    public String getName() {
        return strEffectName;
    }

    @Override
    public void setName(String strName) {
        strEffectName = strName;
    }

    //Should return a help text explaining what the effect does and what parameters it has with information on the ranges of parameters
    @Override
    public String getHelpText() {
        return strHelpText;
    }

    @Override
    public String getParamsAsString() {
        return getParamsAsString(true);
    }

    @Override
    public String getParamsAsString(boolean bWithParantheses) {
        String strRet = "";

        if (paramNames != null && paramNames.length > 0) {
            if (bWithParantheses)
                strRet += chEffectParamStart;

            for (int i = 0; i < paramNames.length; i++) {
                strRet += paramNames[i] + chParamEquals + String.valueOf(paramVals[i]);

                if (i < paramNames.length - 1)
                    strRet += chParamSeparator;
            }

            if (bWithParantheses)
                strRet += chEffectParamEnd;
        }

        return strRet;
    }

    @Override
    public String getFullEffectAsString() {
        return getName() + getParamsAsString(true);
    }

    public String setParamsFromFullName(String fullEffectAsString) {
        int ind = fullEffectAsString.indexOf(strEffectName);

        if (ind > -1) {
            String strNewParams = fullEffectAsString.substring(ind + strEffectName.length(), fullEffectAsString.length());
            setParams(strNewParams);
        }

        return getParamsAsString();
    }

    @Override
    public String getFullEffectWithExampleParametersAsString() {
        if (strExampleParameters != null && strExampleParameters != "")
            return getName() + "(" + strExampleParameters + ")";
        else
            return getName();
    }

    @Override
    public float expectFloatParameter(String strParamName) {
        float ret = NULL_FLOAT_PARAM;

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if (strParamName.compareToIgnoreCase(paramNames[i]) == 0) {
                    ret = paramVals[i];
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public double expectDoubleParameter(String strParamName) {
        double ret = NULL_DOUBLE_PARAM;

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if (strParamName.compareToIgnoreCase(paramNames[i]) == 0) {
                    ret = paramVals[i];
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public int expectIntParameter(String strParamName) {
        int ret = NULL_INT_PARAM;

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if (strParamName.compareToIgnoreCase(paramNames[i]) == 0) {
                    ret = (int) paramVals[i];
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public boolean isHMMEffect() {
        return isHMMEffect;
    }

    public void setHMMEffect(boolean bHMMEffect) {
        isHMMEffect = bHMMEffect;
    }
}


/**
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.relp;

import com.sun.speech.freetts.FeatureSet;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Unit;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.util.Utilities;

/**
 * Concatenates the Units in the given Utterance to the target_lpc
 * result. This class is an UtteranceProcessor. It defines a method
 * <code> processUtterance </code> that helps populate the
 * target_lpcres relation.
 *
 * @see LPCResult
 */
public class UnitConcatenator implements UtteranceProcessor {
    public final static String PROP_OUTPUT_LPC =
            "com.sun.speech.freetts.outputLPC";
    static private final int ADD_RESIDUAL_PULSE = 1;
    static private final int ADD_RESIDUAL_WINDOWED = 2;
    static private final int ADD_RESIDUAL = 3;
    private boolean outputLPC = Utilities.getBoolean(PROP_OUTPUT_LPC);


    /**
     * Concatenate the Units in the given Utterance to the target_lpc
     * result.
     *
     * @param utterance the Utterance to do concatenation
     * @throws ProcessException if an error occurs while processing
     *                          the utterance
     * @see LPCResult
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        float uIndex = 0, m;
        int pmI = 0, targetResidualPosition = 0, nearestPM,
                unitPart, targetStart = 0, targetEnd, residualSize, numberFrames;
        Relation unitRelation = utterance.getRelation(Relation.UNIT);
        boolean debug = false;

        SampleInfo sampleInfo;


        int addResidualMethod = ADD_RESIDUAL;

        String residualType = utterance.getString("residual_type");
        if (residualType != null) {
            if (residualType.equals("pulse")) {
                addResidualMethod = ADD_RESIDUAL_PULSE;
            } else if (residualType.equals("windowed")) {
                addResidualMethod = ADD_RESIDUAL_WINDOWED;
            }
        }

        sampleInfo = (SampleInfo) utterance.getObject(SampleInfo.UTT_NAME);
        if (sampleInfo == null) {
            throw new IllegalStateException
                    ("UnitConcatenator: SampleInfo does not exist");
        }

        LPCResult lpcResult = (LPCResult) utterance.getObject("target_lpcres");
        lpcResult.setValues(sampleInfo.getNumberOfChannels(),
                sampleInfo.getSampleRate(),
                sampleInfo.getResidualFold(),
                sampleInfo.getCoeffMin(),
                sampleInfo.getCoeffRange());

        // create the array of final residual sizes
        int[] targetTimes = lpcResult.getTimes();
        int[] residualSizes = lpcResult.getResidualSizes();

        int samplesSize = 0;
        if (lpcResult.getNumberOfFrames() > 0) {
            samplesSize = targetTimes[lpcResult.getNumberOfFrames() - 1];
        }
        lpcResult.resizeResiduals(samplesSize);

        for (Item unitItem = unitRelation.getHead(); unitItem != null;
             unitItem = unitItem.getNext()) {
            FeatureSet featureSet = unitItem.getFeatures();

            String unitName = featureSet.getString("name");
            targetEnd = featureSet.getInt("target_end");
            Unit unit = (Unit) featureSet.getObject("unit");
            int unitSize = unit.getSize();

            uIndex = 0;
            m = (float) unitSize / (float) (targetEnd - targetStart);
            numberFrames = lpcResult.getNumberOfFrames();

            // for all the pitchmarks that are required
            for (; (pmI < numberFrames) &&
                    (targetTimes[pmI] <= targetEnd); pmI++) {

                Sample sample = unit.getNearestSample(uIndex);

                // Get LPC coefficients by copying
                lpcResult.setFrame(pmI, sample.getFrameData());

                // Get residual by copying
                residualSize = lpcResult.getFrameShift(pmI);

                residualSizes[pmI] = residualSize;
                byte[] residualData = sample.getResidualData();

                if (addResidualMethod == ADD_RESIDUAL_PULSE) {
                    lpcResult.copyResidualsPulse
                            (residualData, targetResidualPosition, residualSize);
                } else {
                    lpcResult.copyResiduals
                            (residualData, targetResidualPosition, residualSize);
                }

                targetResidualPosition += residualSize;
                uIndex += ((float) residualSize * m);
            }
            targetStart = targetEnd;
        }
        lpcResult.setNumberOfFrames(pmI);

        if (outputLPC) {
            lpcResult.dump();
        }
    }

    /**
     * Converts this object to a string
     *
     * @return the string form of this object.
     */
    public String toString() {
        return "UnitConcatenator";
    }
}


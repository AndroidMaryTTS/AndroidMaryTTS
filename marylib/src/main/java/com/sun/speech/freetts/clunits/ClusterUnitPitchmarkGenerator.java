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
package com.sun.speech.freetts.clunits;

import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.relp.LPCResult;
import com.sun.speech.freetts.relp.SampleSet;

/**
 * Calculates the pitchmarks. This class is an UtteranceProcessor that
 * calculates target pitchmarks for the given utterance and adds the
 * <i>target_lpcres</i> relation to the utterance with the pitchmark
 * information.
 *
 * @see LPCResult
 */
public class ClusterUnitPitchmarkGenerator implements UtteranceProcessor {

    /**
     * Calculates the pitchmarks for the utterance and adds them as
     * an LPCResult to the Utterance in a relation named
     * "target_lpcres".
     *
     * @param utterance the utterance to process
     * @throws ProcessException if an error occurs while processing
     *                          the utterance
     * @see LPCResult
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        LPCResult lpcResult;
        int pitchmarks = 0;
        int uttSize = 0;
        int unitEntry;
        int unitStart;
        int unitEnd;

        SampleSet sts = (SampleSet) utterance.getObject("sts_list");
        lpcResult = new LPCResult();

        for (Item unit = utterance.getRelation(Relation.UNIT).getHead();
             unit != null; unit = unit.getNext()) {
            unitEntry = unit.getFeatures().getInt("unit_entry");
            unitStart = unit.getFeatures().getInt("unit_start");
            unitEnd = unit.getFeatures().getInt("unit_end");
            uttSize += sts.getUnitSize(unitStart, unitEnd);
            pitchmarks += unitEnd - unitStart;
            unit.getFeatures().setInt("target_end", uttSize);
        }

        lpcResult.resizeFrames(pitchmarks);

        pitchmarks = 0;
        uttSize = 0;

        int[] targetTimes = lpcResult.getTimes();

        for (Item unit = utterance.getRelation(Relation.UNIT).getHead();
             unit != null; unit = unit.getNext()) {
            unitEntry = unit.getFeatures().getInt("unit_entry");
            unitStart = unit.getFeatures().getInt("unit_start");
            unitEnd = unit.getFeatures().getInt("unit_end");
            for (int i = unitStart; i < unitEnd; i++, pitchmarks++) {
                uttSize += sts.getSample(i).getResidualSize();
                targetTimes[pitchmarks] = uttSize;
            }
        }
        utterance.setObject("target_lpcres", lpcResult);
    }

    /**
     * Retrieves the name of this utteranceProcessor.
     *
     * @return the name of the utteranceProcessor
     */
    public String toString() {
        return "ClusterUnitPitchmarkGenerator";
    }
}

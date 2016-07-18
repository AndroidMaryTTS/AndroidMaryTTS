/**
 * Copyright 2002 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package de.dfki.lt.freetts.mbrola;

import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;

/**
 * Utterance processor converting phoneme timing and f0-time targets
 * into MBROLA format.
 */
public class ParametersToMbrolaConverter implements UtteranceProcessor {

    /**
     * Convert phoneme timing and f0-time targets
     * into MBROLA format. The results are saved in the Segment relations,
     * as features <code>"mbr_dur"</code> (int-valued) and
     * <code>"mbr_targets"</code> (String-valued). MBROLA-converted targets
     * are saved in the Segment relation because in MBROLA input, targets
     * are represented as properties of the segments during which they occur.
     *
     * @param utterance the utterance to process
     * @throws ProcessException if an error occurs while
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        Relation segmentRelation = utterance.getRelation(Relation.SEGMENT);
        Relation targetRelation = utterance.getRelation(Relation.TARGET);

        Item segment = segmentRelation.getHead();
        Item target = null;
        if (targetRelation != null) target = targetRelation.getHead();
        float prevEnd = 0f;
        while (segment != null) {
            String name = segment.getFeatures().getString("name");
            // Accumulated duration of all segments in the utterance,
            // in seconds:
            float end = segment.getFeatures().getFloat("end");
            // Individual duration of segment, in milliseconds:
            int dur = (int) ((end - prevEnd) * 1000);
            StringBuffer targetStringBuffer = new StringBuffer();
            while (target != null &&
                    target.getFeatures().getFloat("pos") <= end) {
                float pos = target.getFeatures().getFloat("pos");
                // time axis as percentage of segment duration:
                int percentage = ((int) ((pos - prevEnd) * 1000)) * 100 / dur;
                // f0 as an integer:
                int f0 = (int) target.getFeatures().getFloat("f0");
                targetStringBuffer.append(" ");
                targetStringBuffer.append(percentage);
                targetStringBuffer.append(" ");
                targetStringBuffer.append(f0);
                target = target.getNext();
            }
            // System.err.println(name + " " + dur + targetStringBuffer);
            segment.getFeatures().setInt("mbr_dur", dur);
            segment.getFeatures().setString("mbr_targets",
                    targetStringBuffer.toString().trim());
            prevEnd = end;
            segment = segment.getNext();
        }
    }

    public String toString() {
        return "ParametersToMbrolaConverter";
    }
}

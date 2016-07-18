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
package com.sun.speech.freetts.cart;

import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.PathExtractor;
import com.sun.speech.freetts.PathExtractorImpl;
import com.sun.speech.freetts.PhoneDuration;
import com.sun.speech.freetts.PhoneDurations;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;

/**
 * Determines duration timing for
 * <code>Relation.SEGMENT</code> relations in an utterance.
 * Annotates the <code>Relation.SEGMENT</code> relation with an
 * "end" time feature.
 * <p/>
 * <p>[[[TODO: The mean words-per-minute rate should become part
 * of the CART data.  For now, it is passed into the constructor.]]]
 *
 * @see Relation#SEGMENT
 */
public class Durator implements UtteranceProcessor {
    private static final PathExtractor DURATION_STRETCH_PATH =
            new PathExtractorImpl(
                    "R:SylStructure.parent.parent.R:Token.parent.local_duration_stretch",
                    true);
    /**
     * The CART used for this duration UtteranceProcessor.  It is
     * passed into the constructor.
     */
    protected CART cart;

    /**
     * The PhoneDurations used for this duration UtteranceProcessor.
     * It is passed into the constructor.
     */
    protected PhoneDurations durations;
    /**
     * The nominal speaking rate in words per minute.  Set in the
     * constructor.
     */
    private float meanRate;

    /**
     * Creates a new duration UtteranceProcessor with the given
     * CART and phone durations.
     *
     * @param cart      contains zscore duration data
     * @param meanRate  the mean words per minute rate of the CART data
     * @param durations contains mean and standard deviation phone durations
     */
    public Durator(CART cart, float meanRate, PhoneDurations durations) {
        this.cart = cart;
        this.meanRate = meanRate;
        this.durations = durations;
    }

    /**
     * Annotates the <code>Relation.SEGMENT</code> relations with
     * cumulative "end" time
     * features based on phone durations.  Expects the CART to return
     * a zscore for each phone, which specifies the number of standard
     * deviations from the mean.  This is coupled with a phone
     * durations table that returns the mean and standard deviation
     * for phones.
     *
     * @param utterance the utterance to process
     * @throws ProcessException if a problem is encountered during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        float durStretch;
        PhoneDuration durStat;
        float durationStretch = utterance.getVoice().getDurationStretch();
        float zdur;
        float dur;
        float end = 0.0f;
        float localDurationStretch;

        // Figure out how far to stretch the durations (speed things
        // up or slow them down.
        //

        durStretch = meanRate / utterance.getVoice().getRate();

        // Go through each of the segments and calculate a duration
        // for it.  Store the cumulative end time for the duration in
        // the "end" feature of the segment.
        //
        for (Item segment =
             utterance.getRelation(Relation.SEGMENT).getHead();
             segment != null; segment = segment.getNext()) {
            zdur = ((Float) cart.interpret(segment)).floatValue();
            durStat = durations.getPhoneDuration(
                    segment.getFeatures().getString("name"));

            Object tval = DURATION_STRETCH_PATH.findFeature(segment);
            localDurationStretch = Float.parseFloat(tval.toString());

            if (localDurationStretch == 0.0) {
                localDurationStretch = durationStretch;
            } else {
                localDurationStretch *= durationStretch;
            }

            dur = localDurationStretch * ((zdur * durStat.getStandardDeviation())
                    + durStat.getMean());
            end += dur;
            segment.getFeatures().setFloat("end", end);
        }
    }

    // inherited from Object
    public String toString() {
        return "CARTDurator";
    }
}

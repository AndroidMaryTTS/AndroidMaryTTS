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
package com.sun.speech.freetts.en;

import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.PathExtractor;
import com.sun.speech.freetts.PathExtractorImpl;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.Voice;


/**
 * Annotates an utterance with pause information.
 */
public class PauseGenerator implements UtteranceProcessor {
    private final static PathExtractor segmentPath = new
            PathExtractorImpl("R:SylStructure.daughtern.daughtern.R:Segment", false);
    private final static PathExtractor puncPath =
            new PathExtractorImpl("R:Token.parent.punc", true);

    /**
     * Constructs a PauseGenerator
     */
    public PauseGenerator() {
    }

    /**
     * Annotates an utterance with pause information.
     *
     * @param utterance the utterance to process
     * @throws ProcessException if an error occurs while
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        String silence = utterance.getVoice().getFeatures().
                getString(Voice.FEATURE_SILENCE);

        Item phraseHead = utterance.getRelation(Relation.PHRASE).getHead();

        // If there are not any phrases at all, then just skip
        // the whole thing.

        if (phraseHead == null) {
            return;
        }

        // insert initial silence
        Relation segment = utterance.getRelation(Relation.SEGMENT);
        Item s = segment.getHead();
        if (s == null) {
            s = segment.appendItem(null);
        } else {
            s = s.prependItem(null);
        }
        s.getFeatures().setString("name", silence);

        for (Item phrase = phraseHead;
             phrase != null;
             phrase = phrase.getNext()) {
            Item word = phrase.getLastDaughter();
            while (word != null) {
                Item seg = segmentPath.findItem(word);
                // was this an explicit change or a lost bug fix
                //if (seg != null && !"".equals(puncPath.findFeature(word))) {
                if (seg != null) {
                    Item pause = seg.appendItem(null);
                    pause.getFeatures().setString("name", silence);
                    break;
                }
                word = word.getPrevious();
            }
        }
    }

    /**
     * Returns the string representation of the object
     *
     * @return the string representation of the object
     */
    public String toString() {
        return "PauseGenerator";
    }
}

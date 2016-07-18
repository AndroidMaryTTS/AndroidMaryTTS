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
 * Annotates the utterance with post lexical information.
 */
public class PostLexicalAnalyzer implements UtteranceProcessor {
    private static final PathExtractor wordPath =
            new PathExtractorImpl("R:SylStructure.parent.parent.name", true);
    private static final PathExtractor P_PH_VC =
            new PathExtractorImpl("p.ph_vc", true);
    private static final PathExtractor N_PH_VC =
            new PathExtractorImpl("n.ph_vc", true);

    /**
     * Constructs a PostLexicalAnalyzer
     */
    public PostLexicalAnalyzer() {
    }

    /**
     * Prepends a schwa to the given item
     *
     * @param item the item to prepend the schwa to.
     */
    private static void prependSchwa(Item item) {
        Item schwa = item.prependItem(null);
        schwa.getFeatures().setString("name", "ax");
        item.getItemAs(
                Relation.SYLLABLE_STRUCTURE).prependItem(schwa);
    }

    /**
     * Performs the post lexical processing.
     *
     * @param utterance the utterance to process
     * @throws ProcessException if an error occurs while
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        fixApostrophe(utterance);
        fixTheIy(utterance);
    }

    /**
     * Fixes apostrophe s segments.
     *
     * @param utterance the utterance to fix
     */
    private void fixApostrophe(Utterance utterance) {
        Voice voice = utterance.getVoice();
        for (Item item = utterance.getRelation(Relation.SEGMENT).getHead();
             item != null;
             item = item.getNext()) {
            String word = wordPath.findFeature(item).toString();

            if (word.equals("'s")) {

                String pname = item.getPrevious().toString();

                if (("fa".indexOf(
                        voice.getPhoneFeature(pname, "ctype")) != -1) &&
                        ("dbg".indexOf(
                                voice.getPhoneFeature(pname, "cplace")) == -1)) {
                    prependSchwa(item);
                } else if (voice.getPhoneFeature(pname, "cvox").equals("-")) {
                    item.getFeatures().setString("name", "s");
                }
            } else if (word.equals("'ve") ||
                    word.equals("'ll") || word.equals("'d")) {
                if ("-".equals(P_PH_VC.findFeature(item))) {
                    prependSchwa(item);
                }
            }
        }
    }

    /**
     * Changes the pronunciation of "the" from 'd ax'  to 'd iy' if
     * the following word starts with a vowel. "The every" is a good
     * example.
     *
     * @param utterance the utterance to process
     */
    private void fixTheIy(Utterance utterance) {
        Voice voice = utterance.getVoice();
        for (Item item = utterance.getRelation(Relation.SEGMENT).getHead();
             item != null; item = item.getNext()) {

            if ("ax".equals(item.toString())) {
                String word = wordPath.findFeature(item).toString();
                if ("the".equals(word) &&
                        ("+".equals(N_PH_VC.findFeature(item)))) {
                    item.getFeatures().setString("name", "iy");
                }
            }
        }
    }

    /**
     * Returns the string representation of the object
     *
     * @return the string representation of the object
     */
    public String toString() {
        return "PostLexicalAnalyzer";
    }
}


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
package com.sun.speech.freetts;

import com.sun.speech.freetts.lexicon.Lexicon;

import java.util.ArrayList;
import java.util.List;

/**
 * Annotates an utterance with <code>Relation.SYLLABLE</code>,
 * <code>Relation.SYLLABLE_STRUCTURE</code>, and
 * <code>Relation.SEGMENT</code>.
 * To determine stress, the <code>isStressed</code> method relies upon
 * a phone ending in the number "1".  Subclasses should override
 * <code>isStressed</code> and <code>deStress</code> if stresses are
 * determined in other ways.
 *
 * @see Relation#SEGMENT
 * @see Relation#SYLLABLE
 * @see Relation#SYLLABLE_STRUCTURE
 */
public class Segmenter implements UtteranceProcessor {
    private final static String STRESS = "1";
    private final static String NO_STRESS = "0";

    /**
     * Annotates an utterance with <code>Relation.SYLLABLE</code>,
     * <code>Relation.SYLLABLE_STRUCTURE</code>, and
     * <code>Relation.SEGMENT</code>.
     *
     * @param utterance the utterance to process/tokenize
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     * @see Relation#SEGMENT
     * @see Relation#SYLLABLE
     * @see Relation#SYLLABLE_STRUCTURE
     */
    public void processUtterance(Utterance utterance) throws ProcessException {

        // preconditions
        if (utterance.getRelation(Relation.WORD) == null) {
            throw new IllegalStateException(
                    "Word relation has not been set");
        } else if (utterance.getRelation(Relation.SYLLABLE) != null) {
            throw new IllegalStateException(
                    "Syllable relation has already been set");
        } else if (utterance.getRelation(Relation.SYLLABLE_STRUCTURE)
                != null) {
            throw new IllegalStateException(
                    "SylStructure relation has already been set");
        } else if (utterance.getRelation(Relation.SEGMENT) != null) {
            throw new IllegalStateException(
                    "Segment relation has already been set");
        }

        String stress = NO_STRESS;
        Relation syl = utterance.createRelation(Relation.SYLLABLE);
        Relation sylstructure =
                utterance.createRelation(Relation.SYLLABLE_STRUCTURE);
        Relation seg = utterance.createRelation(Relation.SEGMENT);
        Lexicon lex = utterance.getVoice().getLexicon();
        List syllableList = null;

        for (Item word = utterance.getRelation(Relation.WORD).getHead();
             word != null; word = word.getNext()) {
            Item ssword = sylstructure.appendItem(word);
            Item sylItem = null;   // item denoting syllable boundaries
            Item segItem = null;   // item denoting phonelist (segments)
            Item sssyl = null;     // item denoting syl in word

            String[] phones = null;

            Item token = word.getItemAs("Token");
            FeatureSet featureSet = null;

            if (token != null) {
                Item parent = token.getParent();
                featureSet = parent.getFeatures();
            }

            if (featureSet != null && featureSet.isPresent("phones")) {
                phones = (String[]) featureSet.getObject("phones");
            } else {
                phones = lex.getPhones(word.toString(), null);
            }

            assert phones != null;

            for (int j = 0; j < phones.length; j++) {
                if (sylItem == null) {
                    sylItem = syl.appendItem();
                    sssyl = ssword.addDaughter(sylItem);
                    stress = NO_STRESS;
                    syllableList = new ArrayList();
                }
                segItem = seg.appendItem();
                if (isStressed(phones[j])) {
                    stress = STRESS;
                    phones[j] = deStress(phones[j]);
                }
                segItem.getFeatures().setString("name", phones[j]);
                sssyl.addDaughter(segItem);
                syllableList.add(phones[j]);
                if (lex.isSyllableBoundary(syllableList, phones, j + 1)) {
                    sylItem = null;
                    if (sssyl != null) {
                        sssyl.getFeatures().setString("stress", stress);
                    }
                }
            }
        }

        assert utterance.getRelation(Relation.WORD) != null;
        assert utterance.getRelation(Relation.SYLLABLE) != null;
        assert utterance.getRelation(Relation.SYLLABLE_STRUCTURE) != null;
        assert utterance.getRelation(Relation.SEGMENT) != null;
    }

    /**
     * Determines if the given phonemene is stressed.
     * To determine stress, this method relies upon
     * a phone ending in the number "1".  Subclasses should override this
     * method if stresses are determined in other ways.
     *
     * @param phone the phone to check
     * @return true if the phone is stressed, otherwise false
     */
    protected boolean isStressed(String phone) {
        return phone.endsWith("1");
    }

    /**
     * Converts stressed phoneme to regular phoneme.  This method
     * merely removes the last character of the phone.  Subclasses
     * should override this if another method is to be used.
     *
     * @param phone the phone to convert
     * @return de-stressed phone
     */
    protected String deStress(String phone) {
        String retPhone = phone;
        if (isStressed(phone)) {
            retPhone = phone.substring(0, phone.length() - 1);
        }
        return retPhone;
    }

    /**
     * Returns the simple name of this class.
     *
     * @return the simple name of this class
     */
    public String toString() {
        return "Segmenter";
    }
}


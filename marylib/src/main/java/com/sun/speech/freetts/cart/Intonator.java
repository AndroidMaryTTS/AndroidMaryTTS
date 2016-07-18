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
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;

/**
 * Annotates the <code>Relation.SYLLABLE</code> relations of an
 * utterance with "accent"
 * and "endtone" features.  Though not required, a typical use of
 * this is to use the ToBI (tones and break indeces) scheme for
 * transcribing intonation and accent in English, developed by Janet
 * Pierrehumbert and Mary Beckman.  This implementation is independent
 * of the ToBI scheme:  ToBI annotations are not
 * used by this class, but are merely copied from the CART result
 * to the "accent" and "endtone" features of the
 * <code>Relation.SYLLABLE</code> relation.
 */
public class Intonator implements UtteranceProcessor {

    /**
     * The accent CART used for this Intonation UtteranceProcessor.  It is
     * passed into the constructor.
     */
    protected CART accentCart;

    /**
     * The tone CART used for this Intonation UtteranceProcessor.  It is
     * passed into the constructor.
     */
    protected CART toneCart;

    /**
     * Creates a new Intonation UtteranceProcessor with the given
     * CARTs.
     *
     * @param accentCart the CART for doing accents
     * @param toneCart   the CART for doing end tones
     */
    public Intonator(CART accentCart, CART toneCart) {
        this.accentCart = accentCart;
        this.toneCart = toneCart;
    }

    /**
     * Annotates the <code>Relation.SYLLABLE</code> relations of an
     * utterance with "accent"
     * and "endtone" features.  Depends upon "NONE" being returned by
     * either the accent or tone CART to indicate there isn't an
     * intonation feature for a syllable.
     *
     * @param utterance the utterance to process/tokenize
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        String results;
        for (Item syllable =
             utterance.getRelation(Relation.SYLLABLE).getHead();
             syllable != null;
             syllable = syllable.getNext()) {
            results = (String) accentCart.interpret(syllable);
            if (!results.equals("NONE")) {
                syllable.getFeatures().setString("accent", results);
            }
            results = (String) toneCart.interpret(syllable);
            if (!results.equals("NONE")) {
                syllable.getFeatures().setString("endtone", results);
            }
        }
    }

    // inherited from Object
    public String toString() {
        return "CARTIntonator";
    }
}

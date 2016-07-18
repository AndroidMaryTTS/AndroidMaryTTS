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
 * Creates a <code>Relation.PHRASE</code> relation, grouping
 * <code>Relation.WORD</code> relations by breaks.
 *
 * @see Relation#PHRASE
 * @see Relation#WORD
 */
public class Phraser implements UtteranceProcessor {
    /**
     * The CART used for this Phrasing UtteranceProcessor.  It is
     * passed into the constructor.
     */
    protected CART cart;

    /**
     * Creates a new Phrasing UtteranceProcessor with the given
     * CART.  The phrasing CART is expected to return "BB" values
     * for big breaks.
     *
     * @param cart a phrasing CART
     */
    public Phraser(CART cart) {
        this.cart = cart;
    }

    /**
     * Creates a <code>Relation.PHRASE</code> relation, grouping
     * <code>Relation.WORD</code> relations by breaks.
     * Depends upon a phrasing CART that returns strings containing
     * "BB" for big breaks.
     *
     * @param utterance the utterance to process
     * @throws ProcessException if a problem is encountered during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        Relation relation = utterance.createRelation(Relation.PHRASE);
        Item p = null;
        for (Item w = utterance.getRelation(Relation.WORD).getHead();
             w != null; w = w.getNext()) {
            if (p == null) {
                p = relation.appendItem();
                p.getFeatures().setString("name", "BB");
            }
            p.addDaughter(w);
            String results = (String) cart.interpret(w);
            //System.out.println("word: " + w + ", results: " + results);
            if (results.equals("BB")) {
                p = null;
            }
        }
    }

    // inherited from Object
    public String toString() {
        return "CARTPhraser";
    }
}

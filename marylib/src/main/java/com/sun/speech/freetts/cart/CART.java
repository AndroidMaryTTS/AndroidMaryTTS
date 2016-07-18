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

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Generic interface for Classification and Regression Trees (CARTs) based
 * on the Breiman, Friedman, Olshen, and Stone document "Classification and
 * Regression Trees."  Wadsworth, Belmont, CA, 1984.
 */
public interface CART {
    /**
     * Passes the given item through this CART and returns the
     * interpretation.
     *
     * @param item the item to analyze
     * @return the interpretation
     */
    Object interpret(Item item);

    /**
     * Dumps this CART to the output stream.
     *
     * @param os the output stream
     * @throws IOException if an error occurs during output
     */
    void dumpBinary(DataOutputStream os) throws IOException;
}


  

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

/**
 * Thrown by a Validator if any errors
 * are encountered while validating.
 */
public class ValidationException extends Exception {

    /**
     * Class constructor.
     *
     * @param s the reason why the exception was thrown
     */
    public ValidationException(String s) {
        super(s);
    }
}

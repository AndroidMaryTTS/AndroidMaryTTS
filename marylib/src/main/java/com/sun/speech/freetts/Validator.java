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
 * Implementors of this interface can be validated via the validate()
 * method.
 */
public interface Validator {

    /**
     * Validates a certain condition.
     *
     * @throws a ValidationException if the condition is invalid.
     */
    void validate() throws ValidationException;
}

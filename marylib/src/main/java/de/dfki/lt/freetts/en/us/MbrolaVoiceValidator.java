/**
 * Copyright 2002 DFKI GmbH.
 * Portions Copyright 2002 Sun Microsystems, Inc.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package de.dfki.lt.freetts.en.us;

import com.sun.speech.freetts.ValidationException;
import com.sun.speech.freetts.Validator;
import com.sun.speech.freetts.util.Utilities;

import java.io.File;

/**
 * Shows this MbrolaVoice is valid (or usable). It tests for
 * the following:
 * <p/>
 * <ol>
 * <li> Check that the "mbrola.base" System property is defined,
 * and that directory exists.
 * <li> Check that the $(mbrola.base)/mbrola binary exists.
 * <li> Check that the transition table exists. It is assumed
 * to be at $(mbrola.base)/us1/us1mrpa.
 * <li> Check that its voice database exists.
 * </ol>
 */
public class MbrolaVoiceValidator implements Validator {

    private MbrolaVoice mbrolaVoice;

    public MbrolaVoiceValidator(MbrolaVoice mbrolaVoice) {
        this.mbrolaVoice = mbrolaVoice;
    }

    /**
     * Validates this MbrolaVoice.
     *
     * @throws ValidationException if this MbrolaVoice is invalid
     */
    public void validate() throws ValidationException {
        String mbrolaBase = Utilities.getProperty("mbrola.base", null);
        File mbrolaBinary = new File(mbrolaVoice.getMbrolaBinary());
        File mbrolaVoiceDB = new File(mbrolaVoice.getDatabase());

        if (mbrolaBase == null || mbrolaBase.length() == 0) {
            throw new ValidationException
                    ("System property \"mbrola.base\" is undefined. " +
                            "You might need to set the MBROLA_DIR environment variable.");
        }
        if (!mbrolaBinary.exists()) {
            throw new ValidationException
                    ("No MBROLA binary at: " + mbrolaVoice.getMbrolaBinary());
        }
        if (!mbrolaVoiceDB.exists()) {
            throw new ValidationException
                    ("No voice database for " + mbrolaVoice.getName() +
                            " at: " + mbrolaVoice.getDatabase());
        }
    }

    /**
     * Returns the name of this validator.
     *
     * @return the name of this Validator
     */
    public String toString() {
        return (mbrolaVoice.toString() + "Validator");
    }
}


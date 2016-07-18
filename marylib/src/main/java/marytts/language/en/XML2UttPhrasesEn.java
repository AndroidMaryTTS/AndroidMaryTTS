/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.language.en;

import com.sun.speech.freetts.Utterance;

import java.util.Locale;

import marytts.language.en_US.datatypes.USEnglishDataTypes;
import marytts.modules.XML2UttBase;
import mf.org.w3c.dom.Element;


/**
 * Convert a MaryXML DOM tree into FreeTTS utterances
 * (phrases, English).
 *
 * @author Marc Schr&ouml;der
 */

public class XML2UttPhrasesEn extends XML2UttBase {
    public XML2UttPhrasesEn() {
        super("XML2Utt PhrasesEn",
                USEnglishDataTypes.PHRASES_US,
                USEnglishDataTypes.FREETTS_PHRASES,
                Locale.ENGLISH);
    }

    /**
     * Depending on the data type, find the right information in the sentence
     * and insert it into the utterance.
     */
    @Override
    protected void fillUtterance(Utterance utterance, Element sentence) {
        fillUtterance(utterance, sentence,
                true, // create word relation
                false, // do not create sylstruct relation
                false); // don't create target relation

    }

}


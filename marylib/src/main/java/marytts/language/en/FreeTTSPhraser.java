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
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.cart.CARTImpl;
import com.sun.speech.freetts.cart.Phraser;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import marytts.datatypes.MaryData;
import marytts.language.en_US.datatypes.USEnglishDataTypes;
import marytts.modules.InternalModule;
import marytts.modules.synthesis.FreeTTSVoices;


/**
 * Use an individual FreeTTS module for English synthesis.
 *
 * @author Marc Schr&ouml;der
 */

public class FreeTTSPhraser extends InternalModule {
    private UtteranceProcessor processor;

    public FreeTTSPhraser() {
        super("Phraser",
                USEnglishDataTypes.FREETTS_POS,
                USEnglishDataTypes.FREETTS_PHRASES,
                Locale.ENGLISH
        );
    }

    @Override
    public void startup() throws Exception {
        super.startup();

        // Initialise FreeTTS
        FreeTTSVoices.load();
        CARTImpl phrasingCart = new CARTImpl(
                com.sun.speech.freetts.en.us.CMUVoice.class.getResource("phrasing_cart.txt"));
        processor = new Phraser(phrasingCart);
    }

    @Override
    public MaryData process(MaryData d)
            throws Exception {
        List utterances = d.getUtterances();
        Iterator it = utterances.iterator();
        while (it.hasNext()) {
            Utterance utterance = (Utterance) it.next();
            processor.processUtterance(utterance);
        }
        MaryData output = new MaryData(outputType(), d.getLocale());
        output.setUtterances(utterances);
        return output;
    }


}


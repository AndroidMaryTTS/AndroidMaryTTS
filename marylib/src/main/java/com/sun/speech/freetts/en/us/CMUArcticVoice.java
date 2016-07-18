package com.sun.speech.freetts.en.us;

import com.sun.speech.freetts.Age;
import com.sun.speech.freetts.Gender;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.clunits.ClusterUnitSelector;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import de.dfki.lt.freetts.ClusterUnitNamer;

/**
 * Experimental class that selects units for the
 * <a href="http://festvox.org/cmu_arctic/">CMU ARCTIC voices</a>.
 */
public class CMUArcticVoice extends CMUClusterUnitVoice {

    /**
     * Creates a simple cluster unit voice for the ARCTIC voices
     *
     * @param name         the name of the voice
     * @param gender       the gender of the voice
     * @param age          the age of the voice
     * @param description  a human-readable string providing a
     *                     description that can be displayed for the users.
     * @param locale       the locale of the voice
     * @param domain       the domain of this voice.  For example,
     * @param organization the organization which created the voice
     *                     &quot;general&quot;, &quot;time&quot;, or
     *                     &quot;weather&quot;.
     * @param lexicon      the lexicon to load
     * @param database     the url to the database containing unit data
     *                     for this voice.
     */
    public CMUArcticVoice(String name, Gender gender, Age age,
                          String description, Locale locale, String domain,
                          String organization, CMULexicon lexicon, URL database) {
        super(name, gender, age, description, locale,
                domain, organization, lexicon, database);
    }

    /**
     * Returns the unit selector to be used by this voice.
     * Derived voices typically override this to customize behaviors.
     * This voice uses  a cluster unit selector as the unit selector.
     *
     * @return the post lexical processor
     * @throws IOException if an IO error occurs while getting
     *                     processor
     */
    public UtteranceProcessor getUnitSelector() throws IOException {
        ClusterUnitNamer unitNamer = new ClusterUnitNamer() {
            public void setUnitName(Item seg) {
                String VOWELS = "aeiou";
                String cname = null;

                String segName = seg.getFeatures().getString("name");
                
                /*
                 * If we have a vowel, then the unit name is the segment name
                 * plus a 0 or 1, depending upon the stress of the parent.
                 * Otherwise, the unit name is the segment name plus "coda" or
                 * "onset" based upon the seg_onsetcoda feature processor.
                 */
                if (segName.equals("pau")) {
                    cname = segName;
                } else if (VOWELS.indexOf(segName.charAt(0)) >= 0) {
                    cname = segName + seg.findFeature("R:SylStructure.parent.stress");
                } else {
                    cname = segName + seg.findFeature("seg_onsetcoda");
                }

                seg.getFeatures().setString("clunit_name", cname);
            }

        };
        return new ClusterUnitSelector(getDatabase(), unitNamer);
    }
}


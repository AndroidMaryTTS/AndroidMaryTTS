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
package com.sun.speech.freetts.en.us;

import com.sun.speech.freetts.FeatureProcessor;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.PartOfSpeech;
import com.sun.speech.freetts.PathExtractor;
import com.sun.speech.freetts.PathExtractorImpl;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Voice;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Provides the set of feature processors that are used by this
 * language as part of the CART processing.
 */
public class FeatureProcessors {

    private final static PathExtractor FIRST_SYLLABLE_PATH =
            new PathExtractorImpl(
                    "R:SylStructure.parent.R:Phrase.parent.daughter.R:SylStructure.daughter",
                    false);

    private final static PathExtractor LAST_SYLLABLE_PATH =
            new PathExtractorImpl(
                    "R:SylStructure.parent.R:Phrase.parent.daughtern.R:SylStructure.daughter",
                    false);

    private final static PathExtractor LAST_LAST_SYLLABLE_PATH =
            new PathExtractorImpl(
                    "R:SylStructure.parent.R:Phrase.parent.daughtern.R:SylStructure.daughtern",
                    false);

    private final static PathExtractor SUB_PHRASE_PATH =
            new PathExtractorImpl("R:SylStructure.parent.R:Phrase.parent.p", false);

    private final static Pattern DOUBLE_PATTERN
            = Pattern.compile(USEnglish.RX_DOUBLE);

    private final static Pattern DIGITS_PATTERN
            = Pattern.compile(USEnglish.RX_DIGITS);

    private static Set months;
    private static Set days;

    // the set of month names
    static {
        months = new HashSet();
        months.add("jan");
        months.add("january");
        months.add("feb");
        months.add("february");
        months.add("mar");
        months.add("march");
        months.add("apr");
        months.add("april");
        months.add("may");
        months.add("jun");
        months.add("june");
        months.add("jul");
        months.add("july");
        months.add("aug");
        months.add("august");
        months.add("sep");
        months.add("september");
        months.add("oct");
        months.add("october");
        months.add("nov");
        months.add("november");
        months.add("dec");
        months.add("december");
    }

    // the set of week neames
    static {
        days = new HashSet();
        days.add("sun");
        days.add("sunday");
        days.add("mon");
        days.add("monday");
        days.add("tue");
        days.add("tuesday");
        days.add("wed");
        days.add("wednesday");
        days.add("thu");
        days.add("thursday");
        days.add("fri");
        days.add("friday");
        days.add("sat");
        days.add("saturday");
    }

    // no instances
    private FeatureProcessors() {
    }

    /**
     * Gets the phoneset feature with the given name
     *
     * @param item        item the phoneme of interest
     * @param featureName the feature of interest
     * @return the phone feature for the item
     */

    public static String getPhoneFeature(Item item, String featureName) {
        Voice voice = item.getUtterance().getVoice();
        String feature = voice.getPhoneFeature(item.toString(), featureName);
        return feature;
    }

    /**
     * Classifies the type of word break
     *
     * @param item the item to process
     * @return "4" for a big break, "3" for  a break; otherwise "1"
     * @throws ProcessException if an exception occurred during the
     *                          processing
     */
    public static String wordBreak(Item item) throws ProcessException {
        Item ww = item.getItemAs(Relation.PHRASE);
        if (ww == null || ww.getNext() != null) {
            return "1";
        } else {
            String pname = ww.getParent().toString();
            if (pname.equals("BB")) {
                return "4";
            } else if (pname.equals("B")) {
                return "3";
            } else {
                return "1";
            }
        }
    }

    /**
     * Gets the punctuation associated with the word
     *
     * @param item the word to process
     * @return the punctuation associated with the word
     * @throws ProcessException if an exception occurred during the
     *                          processing
     */
    public static String wordPunc(Item item) throws ProcessException {
        Item ww = item.getItemAs(Relation.TOKEN);
        if (ww != null && ww.getNext() != null) {
            return "";
        } else {
            if (ww != null && ww.getParent() != null) {
                return ww.getParent().getFeatures().getString("punc");
            } else {
                return "";
            }
        }
    }

    /**
     * Tests the coda ctype of the given segment.
     *
     * @param seg   the segment to test
     * @param ctype the ctype to check for
     * @return "1" on match "0" on no match
     */
    private static String segCodaCtype(Item seg, String ctype) {
        Item daughter
                = seg.getItemAs(
                Relation.SYLLABLE_STRUCTURE).getParent().getLastDaughter();

        while (daughter != null) {
            if ("+".equals(getPhoneFeature(daughter, "vc"))) {
                return "0";
            }
            if (ctype.equals(getPhoneFeature(daughter, "ctype"))) {
                return "1";
            }

            daughter = daughter.getPrevious();
        }
        return "0";
    }

    /**
     * Tests the onset ctype of the given segment.
     *
     * @param seg   the segment to test to process
     * @param ctype the ctype to check for
     * @return if Onset Stop "1"; otherwise "0"
     */
    private static String segOnsetCtype(Item seg, String ctype) {
        Item daughter = seg.getItemAs(
                Relation.SYLLABLE_STRUCTURE).getParent().getDaughter();

        while (daughter != null) {
            if ("+".equals(getPhoneFeature(daughter, "vc"))) {
                return "0";
            }
            if (ctype.equals(getPhoneFeature(daughter, "ctype"))) {
                return "1";
            }

            daughter = daughter.getNext();
        }
        return "0";
    }

    /**
     * Determines if the given item is accented
     *
     * @param item the item of interest
     * @return <code>true</code> if the item is accented, otherwise
     * <code>false</code>
     */
    private static boolean isAccented(Item item) {
        return (item.getFeatures().isPresent("accent") ||
                item.getFeatures().isPresent("endtone"));
    }

    /**
     * Rails an int. flite never returns an int more than 19 from
     * a feature processor, we duplicate that behavior
     * here so that our tests will match.
     *
     * @param val the value to rail
     * @return val clipped to be betweein 0 and 19
     */
    private static int rail(int val) {
        return val > 19 ? 19 : val;
    }

    /**
     * Returns a guess of the part-of-speech.
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class Gpos implements FeatureProcessor {
        PartOfSpeech pos;

        /**
         * Creates a GPOS with the given part-of-speech table
         *
         * @param pos part of speech determiner
         */
        public Gpos(PartOfSpeech pos) {
            this.pos = pos;
        }

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return a guess at the part-of-speech for the item
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return pos.getPartOfSpeech(item.toString());
        }
    }

    /**
     * Returns as an Integer the number of syllables in the given
     * word.  This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class WordNumSyls implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of syllables in the given word
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item daughter = item.getItemAs(
                    Relation.SYLLABLE_STRUCTURE).getDaughter();
            while (daughter != null) {
                count++;
                daughter = daughter.getNext();
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Counts the number of accented syllables since the last major break.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class AccentedSylIn implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of accented syllables since the last
         * major break
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item ss = item.getItemAs(Relation.SYLLABLE);
            Item firstSyllable = FIRST_SYLLABLE_PATH.findItem(item);

            for (Item p = ss; p != null; p = p.getPrevious()) {
                if (isAccented(p)) {
                    count++;
                }
                if (p.equalsShared(firstSyllable)) {
                    break;
                }
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Counts the number of stressed syllables since the last major break.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class StressedSylIn implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of stresses syllables since the last
         * major break
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item ss = item.getItemAs(Relation.SYLLABLE);
            Item firstSyllable = FIRST_SYLLABLE_PATH.findItem(item);

            // this should include the first syllable, but
            // flite 1.1 and festival don't.

            for (Item p = ss.getPrevious();
                 p != null && !p.equalsShared(firstSyllable);
                 p = p.getPrevious()) {
                if ("1".equals(p.getFeatures().getString("stress"))) {
                    count++;
                }
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Counts the number of stressed syllables until the next major break.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class StressedSylOut implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of stressed syllables until the next major break
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item ss = item.getItemAs(Relation.SYLLABLE);
            Item lastSyllable = LAST_SYLLABLE_PATH.findItem(item);

            for (Item p = ss.getNext(); p != null; p = p.getNext()) {
                if ("1".equals(p.getFeatures().getString("stress"))) {
                    count++;
                }
                if (p.equalsShared(lastSyllable)) {
                    break;
                }
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Returns the length of the string. (generally this is a digit
     * string)
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class NumDigits implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the length of the string
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            String name = item.getFeatures().getString("name");
            return Integer.toString(rail(name.length()));
        }
    }

    /**
     * Returns true ("1") if the given item is a number between 0 and
     * 32 exclusive, otherwise, returns "0".
     * string)
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class MonthRange implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return returns "1" if the given item is a number between 0
         * and 32 (exclusive) otherwise returns "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int v = Integer.parseInt(item.getFeatures().getString("name"));
            if ((v > 0) && (v < 32)) {
                return "1";
            } else {
                return "0";
            }
        }
    }

    /**
     * Attempts to guess the part of speech.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class TokenPosGuess implements FeatureProcessor {
        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return a guess at the part of speech
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            String name = item.getFeatures().getString("name");
            String dc = name.toLowerCase();
            if (DIGITS_PATTERN.matcher(dc).matches()) {
                return "numeric";
            } else if (DOUBLE_PATTERN.matcher(dc).matches()) {
                return "number";
            } else if (months.contains(dc)) {
                return "month";
            } else if (days.contains(dc)) {
                return "day";
            } else if (dc.equals("a")) {
                return "a";
            } else if (dc.equals("flight")) {
                return "flight";
            } else if (dc.equals("to")) {
                return "to";
            } else {
                return "_other_";
            }
        }
    }

    /**
     * Checks to see if the given syllable is accented.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class Accented implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return "1" if the syllable is accented; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            if (isAccented(item)) {
                return "1";
            } else {
                return "0";
            }
        }
    }

    /**
     * Find the last accented syllable
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class LastAccent implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the count of the last accented syllable
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;

            for (Item p = item.getItemAs(Relation.SYLLABLE);
                 p != null; p = p.getPrevious(), count++) {
                if (isAccented(p)) {
                    break;
                }
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Finds the position of the phoneme in the syllable
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PosInSyl implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the position of the phoneme in the syllable
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = -1;

            for (Item p = item.getItemAs(Relation.SYLLABLE_STRUCTURE);
                 p != null; p = p.getPrevious()) {
                count++;
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Classifies the the syllable as single, initial, mid or final.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PositionType implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return classifies the syllable as "single", "final",
         * "initial" or "mid"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            String type;

            Item s = item.getItemAs(Relation.SYLLABLE_STRUCTURE);
            if (s == null) {
                type = "single";
            } else if (s.getNext() == null) {
                if (s.getPrevious() == null) {
                    type = "single";
                } else {
                    type = "final";
                }
            } else if (s.getPrevious() == null) {
                type = "initial";
            } else {
                type = "mid";
            }
            return type;
        }
    }

    /**
     * Counts the number of stressed syllables since the last major break.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SylIn implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of stressed syllables since the last
         * major break
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item ss = item.getItemAs(Relation.SYLLABLE);
            Item firstSyllable = FIRST_SYLLABLE_PATH.findItem(item);

            for (Item p = ss; p != null; p = p.getPrevious(), count++) {
                if (p.equalsShared(firstSyllable)) {
                    break;
                }
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Counts the number of stressed syllables since the last major break.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SylOut implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of stressed syllables since the last
         * major break
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item ss = item.getItemAs(Relation.SYLLABLE);
            Item firstSyllable = LAST_LAST_SYLLABLE_PATH.findItem(item);

            for (Item p = ss; p != null; p = p.getNext()) {
                if (p.equalsShared(firstSyllable)) {
                    break;
                }
                count++;
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Determines the break level after this syllable
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SylBreak implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param syl the item to process
         * @return the break level after this syllable
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item syl) throws ProcessException {
            Item ss = syl.getItemAs(Relation.SYLLABLE_STRUCTURE);
            if (ss == null) {
                return "1";
            } else if (ss.getNext() != null) {
                return "0";
            } else if (ss.getParent() == null) {
                return "1";
            } else {
                return wordBreak(ss.getParent());
            }
        }
    }

    /**
     * Determines the word break.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class WordBreak implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param word the item to process
         * @return the break level for this word
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item word) throws ProcessException {
            return wordBreak(word);
        }
    }

    /**
     * Determines the word punctuation.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class WordPunc implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param word the item to process
         * @return the punctuation for this word
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item word) throws ProcessException {
            return wordPunc(word);
        }
    }

    /**
     * Return consonant cplace
     * l-labial a-alveolar p-palatal b-labio_dental d-dental v-velar
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_CPlace implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return consonant cplace
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "cplace");
        }
    }

    /**
     * Return consonant type
     * s-stop f-fricative a-affricative n-nasal * l-liquid
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_CType implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return consonant type
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "ctype");
        }
    }

    /**
     * Return consonant voicing
     * +=on -=off
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_CVox implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return consonant voicing
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "cvox");
        }
    }

    /**
     * Return vowel or consonant
     * +=on -=off
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_VC implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return vowel or consonant
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "vc");
        }
    }

    /**
     * Return vowel frontness
     * 1-front  2-mid 3-back
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_VFront implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return vowel frontness
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "vfront");
        }
    }

    /**
     * Return vowel height
     * 1-high 2-mid 3-low
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_VHeight implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return vowel height
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "vheight");
        }
    }

    /**
     * Return vowel length
     * s-short l-long d-dipthong a-schwa
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_VLength implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return vowel length
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "vlng");
        }
    }

    /**
     * Return vowel rnd (lip rounding)
     * lip rounding  +=on -=off
     * <p/>
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class PH_VRnd implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return volwel rnd
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            return getPhoneFeature(item, "vrnd");
        }
    }

    /**
     * Determines the onset size of this syllable
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SylOnsetSize implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param syl the item to process
         * @return onset size of this syllable
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item syl) throws ProcessException {
            int count = 0;
            Item daughter = syl.getItemAs(
                    Relation.SYLLABLE_STRUCTURE).getDaughter();
            while (daughter != null) {
                if ("+".equals(getPhoneFeature(daughter, "vc"))) {
                    break;
                }
                count++;
                daughter = daughter.getNext();
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Determines the coda size
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SylCodaSize implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param syl the item to process
         * @return coda size
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item syl) throws ProcessException {
            int count = 0;
            Item daughter = syl.getItemAs(
                    Relation.SYLLABLE_STRUCTURE).getLastDaughter();

            while (daughter != null) {
                if ("+".equals(getPhoneFeature(daughter, "vc"))) {
                    break;
                }

                daughter = daughter.getPrevious();
                count++;
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Checks for fricative
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegCodaFric implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return "1" if fricative; else "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            return segCodaCtype(seg, "f");
        }
    }

    /**
     * Checks for fricative
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegOnsetFric implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return "1" if fricative; else "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            return segOnsetCtype(seg, "f");
        }
    }

    /**
     * Checks for coda stop
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegCodaStop implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if coda stop "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            return segCodaCtype(seg, "s");
        }
    }

    /**
     * Checks for onset stop
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegOnsetStop implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if Onset Stop "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            return segOnsetCtype(seg, "s");
        }
    }

    /**
     * Checks for coda nasal
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegCodaNasal implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if coda stop "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            return segCodaCtype(seg, "n");
        }
    }

    /**
     * Checks for onset nasal
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegOnsetNasal implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if Onset Stop "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            return segOnsetCtype(seg, "n");
        }
    }

    /**
     * Checks for coda glide
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegCodaGlide implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if coda stop "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            if (segCodaCtype(seg, "r").equals("0")) {
                return segCodaCtype(seg, "l");
            }
            return "1";
        }
    }

    /**
     * Checks for onset glide
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegOnsetGlide implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if coda stop "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            if (segOnsetCtype(seg, "r").equals("0")) {
                return segOnsetCtype(seg, "l");
            }
            return "1";
        }
    }

    /**
     * Checks for onset coda
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegOnsetCoda implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return if onset coda "1"; otherwise "0"
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            Item s = seg.getItemAs(Relation.SYLLABLE_STRUCTURE);
            if (s == null) {
                return "coda";
            }

            s = s.getNext();
            while (s != null) {
                if ("+".equals(getPhoneFeature(s, "vc"))) {
                    return "onset";
                }

                s = s.getNext();
            }

            return "coda";
        }
    }

    /**
     * Counts the number of phrases before this one.
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SubPhrases implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param item the item to process
         * @return the number of phrases before this one
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item item) throws ProcessException {
            int count = 0;
            Item inPhrase = SUB_PHRASE_PATH.findItem(item);

            for (Item p = inPhrase; p != null; p = p.getPrevious()) {
                count++;
            }
            return Integer.toString(rail(count));
        }
    }

    /**
     * Returns the duration of the given segment
     * This is a feature processor. A feature processor takes an item,
     * performs some sort of processing on the item and returns an object.
     */
    public static class SegmentDuration implements FeatureProcessor {

        /**
         * Performs some processing on the given item.
         *
         * @param seg the item to process
         * @return the duration of the segment as a string.
         * @throws ProcessException if an exception occurred during the
         *                          processing
         */
        public String process(Item seg) throws ProcessException {
            if (seg == null) {
                return "0";
            } else if (seg.getPrevious() == null) {
                return seg.getFeatures().getObject("end").toString();
            } else {
                return Float.toString(
                        seg.getFeatures().getFloat("end") -
                                seg.getPrevious().getFeatures().getFloat("end")
                );
            }
        }
    }
}

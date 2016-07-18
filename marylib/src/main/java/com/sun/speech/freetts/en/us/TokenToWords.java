/**
 * Portions Copyright 2001-2003 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.en.us;

import com.sun.speech.freetts.FeatureSet;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.cart.CART;
import com.sun.speech.freetts.util.Utilities;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Converts the Tokens (in US English words) in an
 * Utterance into a list of words. It puts the produced list back
 * into the Utterance. Usually, the tokens that gets expanded are numbers
 * like "23" (to "twenty" "three").
 * <p> * It translates the following code from flite:
 * <br>
 * <code>
 * lang/usenglish/us_text.c
 * </code>
 */
public class TokenToWords implements UtteranceProcessor {

    /**
     * Regular expression for something that has a vowel
     */
    private static final String RX_HAS_VOWEL = ".*[aeiouAEIOU].*";

    // Patterns for regular expression matching
    private static final Pattern alphabetPattern;
    private static final Pattern commaIntPattern;
    private static final Pattern digits2DashPattern;
    private static final Pattern digitsPattern;
    private static final Pattern digitsSlashDigitsPattern;
    private static final Pattern dottedAbbrevPattern;
    private static final Pattern doublePattern;
    private static final Pattern drStPattern;
    private static final Pattern fourDigitsPattern;
    private static final Pattern hasVowelPattern;
    private static final Pattern illionPattern;
    private static final Pattern numberTimePattern;
    private static final Pattern numessPattern;
    private static final Pattern ordinalPattern;
    private static final Pattern romanNumbersPattern;
    private static final Pattern sevenPhoneNumberPattern;
    private static final Pattern threeDigitsPattern;
    private static final Pattern usMoneyPattern;
    // King-like words
    private static final String[] kingNames = {
            "louis", "henry", "charles", "philip", "george",
            "edward", "pius", "william", "richard", "ptolemy",
            "john", "paul", "peter", "nicholas", "frederick",
            "james", "alfonso", "ivan", "napoleon", "leo",
            "gregory", "catherine", "alexandria", "pierre", "elizabeth",
            "mary"};
    private static final String[] kingTitles = {
            "king", "queen", "pope", "duke", "tsar",
            "emperor", "shah", "caesar", "duchess", "tsarina",
            "empress", "baron", "baroness", "sultan", "count",
            "countess"};
    // Section-like words
    private static final String[] sectionTypes = {
            "section", "chapter", "part", "phrase", "verse",
            "scene", "act", "book", "volume", "chap",
            "war", "apollo", "trek", "fortran"};
    private static final String KING_NAMES = "kingNames";
    private static final String KING_TITLES = "kingTitles";
    private static final String SECTION_TYPES = "sectionTypes";
    private static final String[] postrophes = {
            "'s", "'ll", "'ve", "'d"};
    // List of US states abbreviations and their full names
    private static final String[][] usStates =
            {
                    {"AL", "ambiguous", "alabama"},
                    {"Al", "ambiguous", "alabama"},
                    {"Ala", "", "alabama"},
                    {"AK", "", "alaska"},
                    {"Ak", "", "alaska"},
                    {"AZ", "", "arizona"},
                    {"Az", "", "arizona"},
                    {"CA", "", "california"},
                    {"Ca", "", "california"},
                    {"Cal", "ambiguous", "california"},
                    {"Calif", "", "california"},
                    {"CO", "ambiguous", "colorado"},
                    {"Co", "ambiguous", "colorado"},
                    {"Colo", "", "colorado"},
                    {"DC", "", "d", "c"},
                    {"DE", "", "delaware"},
                    {"De", "ambiguous", "delaware"},
                    {"Del", "ambiguous", "delaware"},
                    {"FL", "", "florida"},
                    {"Fl", "ambiguous", "florida"},
                    {"Fla", "", "florida"},
                    {"GA", "", "georgia"},
                    {"Ga", "", "georgia"},
                    {"HI", "ambiguous", "hawaii"},
                    {"Hi", "ambiguous", "hawaii"},
                    {"IA", "", "iowa"},
                    {"Ia", "ambiguous", "iowa"},
                    {"IN", "ambiguous", "indiana"},
                    {"In", "ambiguous", "indiana"},
                    {"Ind", "ambiguous", "indiana"},
                    {"ID", "ambiguous", "idaho"},
                    {"IL", "ambiguous", "illinois"},
                    {"Il", "ambiguous", "illinois"},
                    {"ILL", "ambiguous", "illinois"},
                    {"KS", "", "kansas"},
                    {"Ks", "", "kansas"},
                    {"Kans", "", "kansas"},
                    {"KY", "ambiguous", "kentucky"},
                    {"Ky", "ambiguous", "kentucky"},
                    {"LA", "ambiguous", "louisiana"},
                    {"La", "ambiguous", "louisiana"},
                    {"Lou", "ambiguous", "louisiana"},
                    {"Lous", "ambiguous", "louisiana"},
                    {"MA", "ambiguous", "massachusetts"},
                    {"Mass", "ambiguous", "massachusetts"},
                    {"Ma", "ambiguous", "massachusetts"},
                    {"MD", "ambiguous", "maryland"},
                    {"Md", "ambiguous", "maryland"},
                    {"ME", "ambiguous", "maine"},
                    {"Me", "ambiguous", "maine"},
                    {"MI", "", "michigan"},
                    {"Mi", "ambiguous", "michigan"},
                    {"Mich", "ambiguous", "michigan"},
                    {"MN", "ambiguous", "minnestota"},
                    {"Minn", "ambiguous", "minnestota"},
                    {"MS", "ambiguous", "mississippi"},
                    {"Miss", "ambiguous", "mississippi"},
                    {"MT", "ambiguous", "montanna"},
                    {"Mt", "ambiguous", "montanna"},
                    {"MO", "ambiguous", "missouri"},
                    {"Mo", "ambiguous", "missouri"},
                    {"NC", "ambiguous", "north", "carolina"},
                    {"ND", "ambiguous", "north", "dakota"},
                    {"NE", "ambiguous", "nebraska"},
                    {"Ne", "ambiguous", "nebraska"},
                    {"Neb", "ambiguous", "nebraska"},
                    {"NH", "ambiguous", "new", "hampshire"},
                    {"NV", "", "nevada"},
                    {"Nev", "", "nevada"},
                    {"NY", "", "new", "york"},
                    {"OH", "ambiguous", "ohio"},
                    {"OK", "ambiguous", "oklahoma"},
                    {"Okla", "", "oklahoma"},
                    {"OR", "ambiguous", "oregon"},
                    {"Or", "ambiguous", "oregon"},
                    {"Ore", "ambiguous", "oregon"},
                    {"PA", "ambiguous", "pennsylvania"},
                    {"Pa", "ambiguous", "pennsylvania"},
                    {"Penn", "ambiguous", "pennsylvania"},
                    {"RI", "ambiguous", "rhode", "island"},
                    {"SC", "ambiguous", "south", "carlolina"},
                    {"SD", "ambiguous", "south", "dakota"},
                    {"TN", "ambiguous", "tennesee"},
                    {"Tn", "ambiguous", "tennesee"},
                    {"Tenn", "ambiguous", "tennesee"},
                    {"TX", "ambiguous", "texas"},
                    {"Tx", "ambiguous", "texas"},
                    {"Tex", "ambiguous", "texas"},
                    {"UT", "ambiguous", "utah"},
                    {"VA", "ambiguous", "virginia"},
                    {"WA", "ambiguous", "washington"},
                    {"Wa", "ambiguous", "washington"},
                    {"Wash", "ambiguous", "washington"},
                    {"WI", "ambiguous", "wisconsin"},
                    {"Wi", "ambiguous", "wisconsin"},
                    {"WV", "ambiguous", "west", "virginia"},
                    {"WY", "ambiguous", "wyoming"},
                    {"Wy", "ambiguous", "wyoming"},
                    {"Wyo", "", "wyoming"},
                    {"PR", "ambiguous", "puerto", "rico"}
            };
    /**
     * Here we use a hashtable for constant time matching, instead of using
     * if (A.equals(B) || A.equals(C) || ...) to match Strings
     */
    private static Hashtable kingSectionLikeHash = new Hashtable();
    // Again hashtable for constant time searching
    private static Hashtable usStatesHash = new Hashtable();

    static {
        alphabetPattern = Pattern.compile(USEnglish.RX_ALPHABET);
        commaIntPattern = Pattern.compile(USEnglish.RX_COMMAINT);
        digits2DashPattern = Pattern.compile(USEnglish.RX_DIGITS2DASH);
        digitsPattern = Pattern.compile(USEnglish.RX_DIGITS);
        digitsSlashDigitsPattern = Pattern.compile(USEnglish.RX_DIGITSSLASHDIGITS);
        dottedAbbrevPattern = Pattern.compile(USEnglish.RX_DOTTED_ABBREV);
        doublePattern = Pattern.compile(USEnglish.RX_DOUBLE);
        drStPattern = Pattern.compile(USEnglish.RX_DRST);
        fourDigitsPattern = Pattern.compile(USEnglish.RX_FOUR_DIGIT);
        hasVowelPattern = Pattern.compile(USEnglish.RX_HAS_VOWEL);
        illionPattern = Pattern.compile(USEnglish.RX_ILLION);
        numberTimePattern = Pattern.compile(USEnglish.RX_NUMBER_TIME);
        numessPattern = Pattern.compile(USEnglish.RX_NUMESS);
        ordinalPattern = Pattern.compile(USEnglish.RX_ORDINAL_NUMBER);
        romanNumbersPattern = Pattern.compile(USEnglish.RX_ROMAN_NUMBER);
        sevenPhoneNumberPattern = Pattern.compile(USEnglish.RX_SEVEN_DIGIT_PHONE_NUMBER);
        threeDigitsPattern = Pattern.compile(USEnglish.RX_THREE_DIGIT);
        usMoneyPattern = Pattern.compile(USEnglish.RX_US_MONEY);
    }

    // Hashtable initialization
    static {
        for (int i = 0; i < kingNames.length; i++) {
            kingSectionLikeHash.put(kingNames[i], KING_NAMES);
        }
        for (int i = 0; i < kingTitles.length; i++) {
            kingSectionLikeHash.put(kingTitles[i], KING_TITLES);
        }
        for (int i = 0; i < sectionTypes.length; i++) {
            kingSectionLikeHash.put(sectionTypes[i], SECTION_TYPES);
        }
    }

    // initialize the Hashtable for usStates
    static {
        for (int i = 0; i < usStates.length; i++) {
            usStatesHash.put(usStates[i][0], usStates[i]);
        }
    }

    // Finite state machines to check if a Token is pronounceable
    private PronounceableFSM prefixFSM = null;
    private PronounceableFSM suffixFSM = null;


    // class variables
    // the word relation that we are building
    private WordRelation wordRelation;

    // the current token Item
    private Item tokenItem;

    // a CART for classifying numbers
    private CART cart;


    /**
     * Constructs a default USTokenWordProcessor. It uses the USEnglish
     * regular expression set (USEngRegExp) by default.
     *
     * @param usNumbersCART the cart to use to classify numbers
     */
    public TokenToWords(CART usNumbersCART,
                        PronounceableFSM prefixFSM,
                        PronounceableFSM suffixFSM) {
        this.cart = usNumbersCART;
        this.prefixFSM = prefixFSM;
        this.suffixFSM = suffixFSM;
    }

    /**
     * Returns true if the given string is in the given string array.
     *
     * @param value       the string to check
     * @param stringArray the array to check
     * @return true if the string is in the array, false otherwise
     */
    private static boolean inStringArray(String value, String[] stringArray) {
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given key is in the kingSectionLikeHash
     * Hashtable, and the value is the same as the given value.
     *
     * @param key   key to look for in the hashtable
     * @param value the value to match
     * @return true if it matches, or false if it does not or if
     * the key is not mapped to any value in the hashtable.
     */
    private static boolean inKingSectionLikeHash(String key, String value) {
        String hashValue = (String) kingSectionLikeHash.get(key);
        if (hashValue != null) {
            return (hashValue.equals(value));
        } else {
            return false;
        }
    }

    /**
     * Returns true if the given token item contains a token that is
     * in a king-like context, e.g., "King" or "Louis".
     *
     * @param tokenItem the token item to check
     * @return true or false
     */
    public static boolean kingLike(Item tokenItem) {
        String kingName =
                ((String) tokenItem.findFeature("p.name")).toLowerCase();
        if (inKingSectionLikeHash(kingName, KING_NAMES)) {
            return true;
        } else {
            String kingTitle =
                    ((String) tokenItem.findFeature("p.p.name")).toLowerCase();
            return inKingSectionLikeHash(kingTitle, KING_TITLES);
        }
    }

    /**
     * Returns true if the given token item contains a token that is
     * in a section-like context, e.g., "chapter" or "act".
     *
     * @param tokenItem the token item to check
     * @return true or false
     */
    public static boolean sectionLike(Item tokenItem) {
        String sectionType =
                ((String) tokenItem.findFeature("p.name")).toLowerCase();
        return inKingSectionLikeHash(sectionType, SECTION_TYPES);
    }

    /**
     * Determines if the given input matches the given Pattern.
     *
     * @param pattern the pattern to match
     * @param input   the string to test
     * @return <code>true</code> if the input string matches the given Pattern;
     * <code>false</code> otherwise
     */
    private static boolean matches(Pattern pattern, String input) {
        Matcher m = pattern.matcher(input);
        return m.matches();
    }

    /**
     * Determines if the character at the given position of the given
     * input text is splittable. A character is splittable if:
     * <p/>
     * 1) the character and the following character are not letters
     * in the English alphabet (A-Z and a-z)
     * <p/>
     * 2) the character and the following character are not digits (0-9)
     * <p/>
     *
     * @param text  the text containing the character of interest
     * @param index the index of the character of interest
     * @return true if the position of the given text is splittable
     * false otherwise
     */
    private static boolean isTextSplitable(String text, int index) {

        char c0 = text.charAt(index);
        char c1 = text.charAt(index + 1);

        if (isLetter(c0) && isLetter(c1)) {
            return false;
        } else return !(NumberExpander.isDigit(c0) && NumberExpander.isDigit(c1));
    }

    /**
     * Returns true if the given character is a letter (a-z or A-Z).
     *
     * @param ch the character to test
     * @return true or false
     */
    private static boolean isLetter(char ch) {
        return (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z'));
    }

    /**
     * Returns true if the given character is an uppercase letter (A-Z).
     *
     * @param ch the character to test
     * @return true or false
     */
    private static boolean isUppercaseLetter(char ch) {
        return ('A' <= ch && ch <= 'Z');
    }

    /**
     * Returns true if the given character is a lowercase letter (a-z).
     *
     * @param ch the character to test
     * @return true or false
     */
    private static boolean isLowercaseLetter(char ch) {
        return ('a' <= ch && ch <= 'z');
    }

    /**
     * Returns the currently processing token Item.
     *
     * @return the current token Item; null if no item
     */
    public Item getTokenItem() {
        return tokenItem;
    }

    /**
     * process the utterance
     *
     * @param utterance the utterance contain the tokens
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        Relation tokenRelation;
        if ((tokenRelation = utterance.getRelation(Relation.TOKEN)) == null) {
            throw new IllegalStateException
                    ("TokenToWords: Token relation does not exist");
        }

        Item wordItem;
        wordRelation = WordRelation.createWordRelation(utterance, this);

        for (tokenItem = tokenRelation.getHead();
             tokenItem != null;
             tokenItem = tokenItem.getNext()) {

            FeatureSet featureSet = tokenItem.getFeatures();
            String tokenVal = featureSet.getString("name");

            // convert the token into a list of words
            tokenToWords(tokenVal);
        }
    }

    /**
     * Returns true if the given token matches part of a phone number
     *
     * @param tokenItem the token
     * @param tokenVal  the string value of the token
     * @return true or false
     */
    private boolean matchesPartPhoneNumber(String tokenVal) {

        String n_name = (String) tokenItem.findFeature("n.name");
        String n_n_name = (String) tokenItem.findFeature("n.n.name");
        String p_name = (String) tokenItem.findFeature("p.name");
        String p_p_name = (String) tokenItem.findFeature("p.p.name");

        boolean matches3DigitsP_name = matches(threeDigitsPattern, p_name);

        return ((matches(threeDigitsPattern, tokenVal) &&
                ((!matches(digitsPattern, p_name)
                        && matches(threeDigitsPattern, n_name)
                        && matches(fourDigitsPattern, n_n_name)) ||
                        (matches(sevenPhoneNumberPattern, n_name)) ||
                        (!matches(digitsPattern, p_p_name)
                                && matches3DigitsP_name
                                && matches(fourDigitsPattern, n_name)))) ||
                (matches(fourDigitsPattern, tokenVal) &&
                        (!matches(digitsPattern, n_name)
                                && matches3DigitsP_name
                                && matches(threeDigitsPattern, p_p_name))));
    }

    /**
     * Converts the given Token into (word) Items in the WordRelation.
     *
     * @param tokenVal the String value of the token, which may or may not be
     *                 same as the one in called "name" in flite
     */
    private void tokenToWords(String tokenVal) {

        FeatureSet tokenFeatures = tokenItem.getFeatures();
        String itemName = tokenFeatures.getString("name");
        int tokenLength = tokenVal.length();

        if (tokenFeatures.isPresent("phones")) {
            wordRelation.addWord(tokenVal);

        } else if ((tokenVal.equals("a") || tokenVal.equals("A")) &&
                ((tokenItem.getNext() == null) ||
                        !(tokenVal.equals(itemName)) ||
                        !(tokenItem.findFeature("punc").equals("")))) {
        /* if A is a sub part of a token, then its ey not ah */
            wordRelation.addWord("_a");

        } else if (matches(alphabetPattern, tokenVal)) {

            if (matches(romanNumbersPattern, tokenVal)) {

		/* XVIII */
                romanToWords(tokenVal);

            } else if (matches(illionPattern, tokenVal) &&
                    matches(usMoneyPattern,
                            (String) tokenItem.findFeature("p.name"))) {
        /* $ X -illion */
                wordRelation.addWord(tokenVal);
                wordRelation.addWord("dollars");

            } else if (matches(drStPattern, tokenVal)) {

		/* St Andrew's St, Dr King Dr */
                drStToWords(tokenVal);

            } else if (tokenVal.equals("Mr")) {

                tokenItem.getFeatures().setString("punc", "");
                wordRelation.addWord("mister");

            } else if (tokenVal.equals("Mrs")) {

                tokenItem.getFeatures().setString("punc", "");
                wordRelation.addWord("missus");

            } else if (tokenLength == 1
                    && isUppercaseLetter(tokenVal.charAt(0))
                    && tokenItem.findFeature("n.whitespace").equals(" ")
                    && isUppercaseLetter
                    (((String) tokenItem.findFeature("n.name")).charAt(0))) {

                tokenFeatures.setString("punc", "");
                String aaa = tokenVal.toLowerCase();
                if (aaa.equals("a")) {
                    wordRelation.addWord("_a");
                } else {
                    wordRelation.addWord(aaa);
                }
            } else if (isStateName(tokenVal)) {
        /*
          The name of a US state
		  isStateName() has already added the full name of the
		  state, so we're all set.
		*/
            } else if (tokenLength > 1 && !isPronounceable(tokenVal)) {
        /* Need common exception list */
		/* unpronouncable list of alphas */
                NumberExpander.expandLetters
                        (tokenVal, wordRelation);

            } else {
		/* just a word */
                wordRelation.addWord(tokenVal.toLowerCase());
            }

        } else if (matches(dottedAbbrevPattern, tokenVal)) {

	    /* U.S.A. */
            // remove all dots
            String aaa = Utilities.deleteChar(tokenVal, '.');
            NumberExpander.expandLetters(aaa, wordRelation);

        } else if (matches(commaIntPattern, tokenVal)) {

	    /* 99,999,999 */
            String aaa = Utilities.deleteChar(tokenVal, ',');
            NumberExpander.expandReal(aaa, wordRelation);

        } else if (matches(sevenPhoneNumberPattern, tokenVal)) {

	    /* 234-3434  telephone numbers */
            int dashIndex = tokenVal.indexOf('-');
            String aaa = tokenVal.substring(0, dashIndex);
            String bbb = tokenVal.substring(dashIndex + 1);

            NumberExpander.expandDigits(aaa, wordRelation);
            wordRelation.addBreak();
            NumberExpander.expandDigits(bbb, wordRelation);

        } else if (matchesPartPhoneNumber(tokenVal)) {

	    /* part of a telephone number */
            String punctuation = (String) tokenItem.findFeature("punc");
            if (punctuation.equals("")) {
                tokenItem.getFeatures().setString("punc", ",");
            }
            NumberExpander.expandDigits(tokenVal, wordRelation);
            wordRelation.addBreak();

        } else if (matches(numberTimePattern, tokenVal)) {

	    /* 12:35 */
            int colonIndex = tokenVal.indexOf(':');
            String aaa = tokenVal.substring(0, colonIndex);
            String bbb = tokenVal.substring(colonIndex + 1);

            NumberExpander.expandNumber(aaa, wordRelation);
            if (!(bbb.equals("00"))) {
                NumberExpander.expandID(bbb, wordRelation);
            }

        } else if (matches(digits2DashPattern, tokenVal)) {

	    /* 999-999-999 */
            digitsDashToWords(tokenVal);

        } else if (matches(digitsPattern, tokenVal)) {

            digitsToWords(tokenVal);

        } else if (tokenLength == 1
                && isUppercaseLetter(tokenVal.charAt(0))
                && tokenItem.findFeature("n.whitespace").equals
                (" ")
                && isUppercaseLetter
                (((String) tokenItem.findFeature("n.name")).charAt(0))) {

            tokenFeatures.setString("punc", "");
            String aaa = tokenVal.toLowerCase();
            if (aaa.equals("a")) {
                wordRelation.addWord("_a");
            } else {
                wordRelation.addWord(aaa);
            }
        } else if (matches(doublePattern, tokenVal)) {

            NumberExpander.expandReal(tokenVal, wordRelation);

        } else if (matches(ordinalPattern, tokenVal)) {

	    /* explicit ordinals */
            String aaa = tokenVal.substring(0, tokenLength - 2);
            NumberExpander.expandOrdinal(aaa, wordRelation);

        } else if (matches(usMoneyPattern, tokenVal)) {

	    /* US money */
            usMoneyToWords(tokenVal);

        } else if (tokenLength > 0
                && tokenVal.charAt(tokenLength - 1) == '%') {

	    /* Y% */
            tokenToWords(tokenVal.substring(0, tokenLength - 1));
            wordRelation.addWord("per");
            wordRelation.addWord("cent");

        } else if (matches(numessPattern, tokenVal)) {

	    /* 60s and 7s and 9s */
            tokenToWords(tokenVal.substring(0, tokenLength - 1));
            wordRelation.addWord("'s");

        } else if (tokenVal.indexOf('\'') != -1) {

            postropheToWords(tokenVal);

        } else if (matches(digitsSlashDigitsPattern, tokenVal) &&
                tokenVal.equals(itemName)) {

            digitsSlashDigitsToWords(tokenVal);

        } else if (tokenVal.indexOf('-') != -1) {

            dashToWords(tokenVal);

        } else if (tokenLength > 1 &&
                !matches(alphabetPattern, tokenVal)) {

            notJustAlphasToWords(tokenVal);

        } else {
	    /* just a word */
            wordRelation.addWord(tokenVal.toLowerCase());
        }
    }

    /**
     * Convert the given digit token with dashes (e.g. 999-999-999)
     * into (word) Items in the WordRelation.
     *
     * @param tokenVal the digit string
     */
    private void digitsDashToWords(String tokenVal) {
        int tokenLength = tokenVal.length();
        int a = 0;
        for (int p = 0; p <= tokenLength; p++) {
            if (p == tokenLength || tokenVal.charAt(p) == '-') {
                String aaa = tokenVal.substring(a, p);
                NumberExpander.expandDigits(aaa, wordRelation);
                wordRelation.addBreak();
                a = p + 1;
            }
        }
    }

    /**
     * Convert the given digit token into (word) Items in the WordRelation.
     *
     * @param tokenVal the digit string
     */
    private void digitsToWords(String tokenVal) {
        FeatureSet featureSet = tokenItem.getFeatures();
        String nsw = "";
        if (featureSet.isPresent("nsw")) {
            nsw = featureSet.getString("nsw");
        }

        if (nsw.equals("nide")) {
            NumberExpander.expandID(tokenVal, wordRelation);
        } else {
            String rName = featureSet.getString("name");
            String digitsType = null;

            if (tokenVal.equals(rName)) {
                digitsType = (String) cart.interpret(tokenItem);
            } else {
                featureSet.setString("name", tokenVal);
                digitsType = (String) cart.interpret(tokenItem);
                featureSet.setString("name", rName);
            }

            if (digitsType.equals("ordinal")) {
                NumberExpander.expandOrdinal(tokenVal, wordRelation);
            } else if (digitsType.equals("digits")) {
                NumberExpander.expandDigits(tokenVal, wordRelation);
            } else if (digitsType.equals("year")) {
                NumberExpander.expandID(tokenVal, wordRelation);
            } else {
                NumberExpander.expandNumber(tokenVal, wordRelation);
            }
        }
    }

    /**
     * Converts the given Roman numeral string into (word) Items in the
     * WordRelation.
     *
     * @param romanString the roman numeral string
     */
    private void romanToWords(String romanString) {
        String punctuation = (String) tokenItem.findFeature("p.punc");

        if (punctuation.equals("")) {
	    /* no preceeding punctuation */
            String n = String.valueOf(NumberExpander.expandRoman(romanString));

            if (kingLike(tokenItem)) {
                wordRelation.addWord("the");
                NumberExpander.expandOrdinal(n, wordRelation);
            } else if (sectionLike(tokenItem)) {
                NumberExpander.expandNumber(n, wordRelation);
            } else {
                NumberExpander.expandLetters(romanString, wordRelation);
            }
        } else {
            NumberExpander.expandLetters(romanString, wordRelation);
        }
    }

    /**
     * Converts the given string containing "St" and "Dr" to (word) Items
     * in the WordRelation.
     *
     * @param drStString the string with "St" and "Dr"
     */
    private void drStToWords(String drStString) {
        String street = null;
        String saint = null;
        char c0 = drStString.charAt(0);

        if (c0 == 's' || c0 == 'S') {
            street = "street";
            saint = "saint";
        } else {
            street = "drive";
            saint = "doctor";
        }

        FeatureSet featureSet = tokenItem.getFeatures();
        String punctuation = featureSet.getString("punc");

        String featPunctuation = (String) tokenItem.findFeature("punc");

        if (tokenItem.getNext() == null ||
                punctuation.indexOf(',') != -1) {
            wordRelation.addWord(street);
        } else if (featPunctuation.equals(",")) {
            wordRelation.addWord(saint);
        } else {
            String pName = (String) tokenItem.findFeature("p.name");
            String nName = (String) tokenItem.findFeature("n.name");

            char p0 = pName.charAt(0);
            char n0 = nName.charAt(0);

            if (isUppercaseLetter(p0) && isLowercaseLetter(n0)) {
                wordRelation.addWord(street);
            } else if (NumberExpander.isDigit(p0) && isLowercaseLetter(n0)) {
                wordRelation.addWord(street);
            } else if (isLowercaseLetter(p0) && isUppercaseLetter(n0)) {
                wordRelation.addWord(saint);
            } else {
                String whitespace = (String) tokenItem.findFeature("n.whitespace");
                if (whitespace.equals(" ")) {
                    wordRelation.addWord(saint);
                } else {
                    wordRelation.addWord(street);
                }
            }
        }

        if (punctuation != null && punctuation.equals(".")) {
            featureSet.setString("punc", "");
        }
    }

    /**
     * Converts US money string into (word) Items in the WordRelation.
     *
     * @param tokenVal the US money string
     */
    private void usMoneyToWords(String tokenVal) {

        int dotIndex = tokenVal.indexOf('.');

        if (matches(illionPattern,
                (String) tokenItem.findFeature("n.name"))) {
            NumberExpander.expandReal(tokenVal.substring(1), wordRelation);
        } else if (dotIndex == -1) {

            String aaa = tokenVal.substring(1);
            tokenToWords(aaa);

            if (aaa.equals("1")) {
                wordRelation.addWord("dollar");
            } else {
                wordRelation.addWord("dollars");
            }
        } else if (dotIndex == (tokenVal.length() - 1) ||
                (tokenVal.length() - dotIndex) > 3) {
	    /* simply read as mumble point mumble */
            NumberExpander.expandReal(tokenVal.substring(1), wordRelation);
            wordRelation.addWord("dollars");
        } else {
            String aaa = tokenVal.substring(1, dotIndex);
            aaa = Utilities.deleteChar(aaa, ',');
            String bbb = tokenVal.substring(dotIndex + 1);

            NumberExpander.expandNumber(aaa, wordRelation);

            if (aaa.equals("1")) {
                wordRelation.addWord("dollar");
            } else {
                wordRelation.addWord("dollars");
            }

            if (bbb.equals("00")) {
                // add nothing to the word list
            } else {
                NumberExpander.expandNumber(bbb, wordRelation);
                if (bbb.equals("01")) {
                    wordRelation.addWord("cent");
                } else {
                    wordRelation.addWord("cents");
                }
            }
        }
    }

    /**
     * Convert the given apostrophed word into (word) Items in the Word
     * Relation.
     *
     * @param tokenVal the apostrophed word string
     */
    private void postropheToWords(String tokenVal) {
        int index = tokenVal.indexOf('\'');
        String bbb = tokenVal.substring(index).toLowerCase();

        if (inStringArray(bbb, postrophes)) {
            String aaa = tokenVal.substring(0, index);
            tokenToWords(aaa);
            wordRelation.addWord(bbb);

        } else if (bbb.equals("'tve")) {
            String aaa = tokenVal.substring(0, index - 2);
            tokenToWords(aaa);
            wordRelation.addWord("'ve");

        } else {
	    /* internal single quote deleted */
            StringBuffer buffer = new StringBuffer(tokenVal);
            buffer.deleteCharAt(index);
            tokenToWords(buffer.toString());
        }
    }

    /**
     * Convert the given digits/digits string into word (Items) in the
     * WordRelation.
     *
     * @param tokenVal the digits/digits string
     */
    private void digitsSlashDigitsToWords(String tokenVal) {

	/* might be fraction, or not */
        int index = tokenVal.indexOf('/');
        String aaa = tokenVal.substring(0, index);
        String bbb = tokenVal.substring(index + 1);
        int a, b;

        // if the previous token is a number, add an "and"
        if (matches(digitsPattern, (String) tokenItem.findFeature("p.name"))
                && tokenItem.getPrevious() != null) {
            wordRelation.addWord("and");
        }

        if (aaa.equals("1") && bbb.equals("2")) {
            wordRelation.addWord("a");
            wordRelation.addWord("half");
        } else if ((a = Integer.parseInt(aaa)) < (b = Integer.parseInt(bbb))) {
            NumberExpander.expandNumber(aaa, wordRelation);
            NumberExpander.expandOrdinal(bbb, wordRelation);
            if (a > 1) {
                wordRelation.addWord("'s");
            }
        } else {
            NumberExpander.expandNumber(aaa, wordRelation);
            wordRelation.addWord("slash");
            NumberExpander.expandNumber(bbb, wordRelation);
        }
    }

    /**
     * Convert the given dashed string (e.g. "aaa-bbb") into (word) Items
     * in the WordRelation.
     *
     * @param tokenVal the dashed string
     */
    private void dashToWords(String tokenVal) {

        int index = tokenVal.indexOf('-');
        String aaa = tokenVal.substring(0, index);
        String bbb = tokenVal.substring(index + 1, tokenVal.length());

        if (matches(digitsPattern, aaa) && matches(digitsPattern, bbb)) {
            FeatureSet featureSet = tokenItem.getFeatures();
            featureSet.setString("name", aaa);
            tokenToWords(aaa);
            wordRelation.addWord("to");
            featureSet.setString("name", bbb);
            tokenToWords(bbb);
            featureSet.setString("name", "");
        } else {
            tokenToWords(aaa);
            tokenToWords(bbb);
        }
    }

    /**
     * Convert the given string (which does not only consist of alphabet)
     * into (word) Items in the WordRelation.
     *
     * @param tokenVal the string
     */
    private void notJustAlphasToWords(String tokenVal) {

	/* its not just alphas */
        int index = 0;
        int tokenLength = tokenVal.length();

        for (; index < tokenLength; index++) {
            if (isTextSplitable(tokenVal, index)) {
                break;
            }
        }

        String aaa = tokenVal.substring(0, index + 1);
        String bbb = tokenVal.substring(index + 1, tokenLength);

        FeatureSet featureSet = tokenItem.getFeatures();
        featureSet.setString("nsw", "nide");
        tokenToWords(aaa);
        tokenToWords(bbb);
    }

    /**
     * Returns true if the given word is pronounceable.
     * This method is originally called us_aswd() in Flite 1.1.
     *
     * @param word the word to test
     * @return true if the word is pronounceable, false otherwise
     */
    public boolean isPronounceable(String word) {
        String lowerCaseWord = word.toLowerCase();
        return (prefixFSM.accept(lowerCaseWord) &&
                suffixFSM.accept(lowerCaseWord));
    }

    /**
     * Returns true if the given token is the name of a US state.
     * If it is, it will add the name of the state to (word) Items in the
     * WordRelation.
     *
     * @param tokenVal the token string
     */
    private boolean isStateName(String tokenVal) {
        String[] state = (String[]) usStatesHash.get(tokenVal);
        if (state != null) {
            boolean expandState = false;

            // check to see if the state initials are ambiguous
            // in the English language
            if (state[1].equals("ambiguous")) {
                String previous = (String) tokenItem.findFeature("p.name");
                String next = (String) tokenItem.findFeature("n.name");

                // System.out.println("previous = " + previous);
                // System.out.println("next = " + next);

                int nextLength = next.length();
                FeatureSet featureSet = tokenItem.getFeatures();

                // check if the previous word starts with a capital letter,
                // is at least 3 letters long, is an alphabet sequence,
                // and has a comma.
                boolean previousIsCity =
                        (isUppercaseLetter(previous.charAt(0))
                                && previous.length() > 2
                                && matches(alphabetPattern, previous)
                                && tokenItem.findFeature("p.punc").equals(","));

                // check if next token starts with a lower case, or
                // this is the end of sentence, or if next token
                // is a period (".") or a zip code (5 or 10 digits).
                boolean nextIsGood =
                        (isLowercaseLetter(next.charAt(0))
                                || tokenItem.getNext() == null
                                || featureSet.getString("punc").equals(".")
                                || ((nextLength == 5 || nextLength == 10) &&
                                matches(digitsPattern, next)));

                expandState = previousIsCity && nextIsGood;
            } else {
                expandState = true;
            }
            if (expandState) {
                for (int j = 2; j < state.length; j++) {
                    if (state[j] != null) {
                        wordRelation.addWord(state[j]);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Converts this object to its String representation
     *
     * @return the string representation of this object
     */
    public String toString() {
        return "TokenToWords";
    }
}

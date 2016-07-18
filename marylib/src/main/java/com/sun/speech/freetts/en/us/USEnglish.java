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

/**
 * Provides the definitions for US English whitespace, punctuations,
 * prepunctuation, and postpunctuation symbols. It also contains a set of
 * Regular Expressions for the US English language.
 * With regular expressions, it specifies what are whitespaces,
 * letters in the alphabet, uppercase and lowercase letters, alphanumeric
 * characters, identifiers, integers, doubles, digits, and 'comma and int'.
 * <p/>
 * It translates the following code from flite:
 * src/regex/cst_regex.c
 * lang/usenglish/us_text.c
 */
public class USEnglish {

    /**
     * default whitespace regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_WHITESPACE = "[ \n\t\r]+";
    /**
     * default letter regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_ALPHABET = "[A-Za-z]+";
    /**
     * default uppercase regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_UPPERCASE = "[A-Z]+";
    /**
     * default lowercase regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_LOWERCASE = "[a-z]+";
    /**
     * default alpha-numeric regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_ALPHANUMERIC = "[0-9A-Za-z]+";
    /**
     * default identifier regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_IDENTIFIER = "[A-Za-z_][0-9A-Za-z_]+";
    /**
     * default integer regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_INT = "-?[0-9]+";
    /**
     * default double regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_DOUBLE = "-?[0-9]+\\.[0-9]*";
    //"-?(([0-9]+\\.[0-9]*)|([0-9]+)|(\\.[0-9]+))([eE][---+]?[0-9]+)?";
    /**
     * default integer with commas  regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_COMMAINT =
            "[0-9][0-9]?[0-9]?,([0-9][0-9][0-9],)*[0-9][0-9][0-9](\\.[0-9]+)?";
    /**
     * default digits regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_DIGITS = "[0-9][0-9]*";
    /**
     * default dotted abbreviation  regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_DOTTED_ABBREV = "([A-Za-z]\\.)*[A-Za-z]";
    /**
     * default ordinal number regular expression pattern
     */
    public static final String RX_DEFAULT_US_EN_ORDINAL_NUMBER =
            "[0-9][0-9,]*(th|TH|st|ST|nd|ND|rd|RD)";
    /**
     * default has-vowel regular expression
     */
    public static final String RX_DEFAULT_HAS_VOWEL = ".*[aeiouAEIOU].*";
    /**
     * default US money regular expression
     */
    public static final String RX_DEFAULT_US_MONEY = "\\$[0-9,]+(\\.[0-9]+)?";
    /**
     * default -illion regular expression
     */
    public static final String RX_DEFAULT_ILLION = ".*illion";
    /**
     * default digits2dash (e.g. 999-999-999) regular expression
     */
    public static final String RX_DEFAULT_DIGITS2DASH = "[0-9]+(-[0-9]+)(-[0-9]+)+";
    /**
     * default digits/digits (e.g. 999/999) regular expression
     */
    public static final String RX_DEFAULT_DIGITSSLASHDIGITS = "[0-9]+/[0-9]+";
    /**
     * default number time regular expression
     */
    public static final String RX_DEFAULT_NUMBER_TIME = "((0[0-2])|(1[0-9])):([0-5][0-9])";
    /**
     * default Roman numerals regular expression
     */
    public static final String RX_DEFAULT_ROMAN_NUMBER =
            "(II?I?|IV|VI?I?I?|IX|X[VIX]*)";
    /**
     * default drst "Dr. St" regular expression
     */
    public static final String RX_DEFAULT_DRST = "([dD][Rr]|[Ss][Tt])";
    /**
     * default numess
     */
    public static final String RX_DEFAULT_NUMESS = "[0-9]+s";
    /**
     * default 7-digit phone number
     */
    public static final String RX_DEFAULT_SEVEN_DIGIT_PHONE_NUMBER =
            "[0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]";
    /**
     * default 4-digit number
     */
    public static final String RX_DEFAULT_FOUR_DIGIT =
            "[0-9][0-9][0-9][0-9]";
    /**
     * default 3-digit number
     */
    public static final String RX_DEFAULT_THREE_DIGIT =
            "[0-9][0-9][0-9]";
    /**
     * has-vowel regular expression
     */
    public static final String RX_HAS_VOWEL = RX_DEFAULT_HAS_VOWEL;
    /**
     * US money regular expression
     */
    public static final String RX_US_MONEY = RX_DEFAULT_US_MONEY;
    /**
     * -illion regular expression
     */
    public static final String RX_ILLION = RX_DEFAULT_ILLION;
    /**
     * digits2dash (e.g. 999-999-999) regular expression
     */
    public static final String RX_DIGITS2DASH = RX_DEFAULT_DIGITS2DASH;
    /**
     * digits/digits (e.g. 999/999) regular expression
     */
    public static final String RX_DIGITSSLASHDIGITS = RX_DEFAULT_DIGITSSLASHDIGITS;
    /**
     * number time regular expression
     */
    public static final String RX_NUMBER_TIME = RX_DEFAULT_NUMBER_TIME;
    /**
     * Roman numerals regular expression
     */
    public static final String RX_ROMAN_NUMBER = RX_DEFAULT_ROMAN_NUMBER;
    /**
     * drst "Dr. St" regular expression
     */
    public static final String RX_DRST = RX_DEFAULT_DRST;
    /**
     * default numess
     */
    public static final String RX_NUMESS = RX_DEFAULT_NUMESS;
    /**
     * 7-digit phone number
     */
    public static final String RX_SEVEN_DIGIT_PHONE_NUMBER = RX_DEFAULT_SEVEN_DIGIT_PHONE_NUMBER;
    /**
     * 4-digit number
     */
    public static final String RX_FOUR_DIGIT = RX_DEFAULT_FOUR_DIGIT;
    /**
     * 3-digit number
     */
    public static final String RX_THREE_DIGIT = RX_DEFAULT_THREE_DIGIT;
    /**
     * punctuation regular expression pattern
     */
    public static final String PUNCTUATION_SYMBOLS = "\"'`.,:;!?(){}[]";
    /**
     * pre-punctuation regular expression pattern
     */
    public static final String PREPUNCTUATION_SYMBOLS = "\"'`({[";
    /**
     * single char symbols  regular expression pattern
     */
    public static final String SINGLE_CHAR_SYMBOLS = "";
    /**
     * whitespace symbols  regular expression pattern
     */
    public static final String WHITESPACE_SYMBOLS = " \t\n\r";
    /**
     * whitespace regular expression pattern
     */
    public static String RX_WHITESPACE = RX_DEFAULT_US_EN_WHITESPACE;
    /**
     * letter  regular expression pattern
     */
    public static String RX_ALPHABET = RX_DEFAULT_US_EN_ALPHABET;
    /**
     * uppercase  regular expression pattern
     */
    public static String RX_UPPERCASE = RX_DEFAULT_US_EN_UPPERCASE;
    /**
     * lowercase  regular expression pattern
     */
    public static String RX_LOWERCASE = RX_DEFAULT_US_EN_LOWERCASE;
    /**
     * alphanumeric  regular expression pattern
     */
    public static String RX_ALPHANUMERIC = RX_DEFAULT_US_EN_ALPHANUMERIC;
    /**
     * identifier  regular expression pattern
     */
    public static String RX_IDENTIFIER = RX_DEFAULT_US_EN_IDENTIFIER;
    /**
     * integer  regular expression pattern
     */
    public static String RX_INT = RX_DEFAULT_US_EN_INT;
    /**
     * double  regular expression pattern
     */
    public static String RX_DOUBLE = RX_DEFAULT_US_EN_DOUBLE;


    // the following symbols are from lang/usenglish/us_text.c
    /**
     * comma separated integer  regular expression pattern
     */
    public static String RX_COMMAINT = RX_DEFAULT_US_EN_COMMAINT;
    /**
     * digits regular expression pattern
     */
    public static String RX_DIGITS = RX_DEFAULT_US_EN_DIGITS;
    /**
     * dotted abbreviation  regular expression pattern
     */
    public static String RX_DOTTED_ABBREV = RX_DEFAULT_US_EN_DOTTED_ABBREV;
    /**
     * ordinal number regular expression pattern
     */
    public static String RX_ORDINAL_NUMBER = RX_DEFAULT_US_EN_ORDINAL_NUMBER;


    /**
     * Not constructable
     */
    private USEnglish() {
    }
}


/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2014  DFKI Language Technology Lab http://www.dfki.de/lt
 *   Author: Joerg Steffen, steffen@dfki.de
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.dfki.lt.tools.tokenizer;

import android.util.Log;

import java.io.IOException;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.dfki.lt.tools.tokenizer.annotate.AnnotatedString;
import de.dfki.lt.tools.tokenizer.annotate.FastAnnotatedString;
import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;
import de.dfki.lt.tools.tokenizer.regexp.Match;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import marytts.server.Mary;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Tokenizer tool that recognizes paragraphs, sentences, tokens, punctuation, numbers,
 * abbreviations, etc.
 *
 * @author Joerg Steffen, DFKI
 */
public class JTok {

    /**
     * annotation key for the token class
     */
    public static final String CLASS_ANNO = "class";

    /**
     * annotation key for sentences and paragraph borders
     */
    public static final String BORDER_ANNO = "border";

    /**
     * annotation value for text unit borders
     */
    public static final String TU_BORDER = "tu";

    /**
     * annotation value for paragraph borders
     */
    public static final String P_BORDER = "p";


    // the logger
//  private static final Logger logger = LoggerFactory.getLogger(JTok.class);

    // identifier of the default configuration
    private static final String DEFAULT = "default";


    // maps each supported language to a language resource
    private Map<String, LanguageResource> langResources;


    /**
     * Creates a new instance of {@link JTok}.
     *
     * @throws IOException if there is an error reading the configuration
     */
    public JTok()
            throws IOException {

        // load tokenizer configuration
        Properties props = new Properties();
        props.load(FileTools.openResourceFileAsStream("jtok/jtok.cfg"));
        //Log.d(Mary.LOG,props.toString());
        this.init(props);
    }


    /**
     * Creates a new instance of {@link JTok} using the given properties.
     *
     * @param configProps properties that contain data about the supported languages
     * @throws InitializationException if initialization fails
     */
    public JTok(Properties configProps) {

        this.init(configProps);
    }


    /**
     * Initializes the tokenizer.
     *
     * @param configProps properties with the the configuration
     */
    private void init(Properties configProps) {

        Log.d(Mary.LOG, configProps.toString());

//    if (configProps.get(DEFAULT) == null) {
//      System.out.println("missing default language resources");
//    }

        this.langResources = new HashMap<>();

        for (Map.Entry<Object, Object> oneEntry : configProps.entrySet()) {
            // get language
            String oneLanguage = (String) oneEntry.getKey();
            // add language resources for that language
            String langDir = (String) oneEntry.getValue();
            System.out.println(String.format("loading language resources for %s from %s", oneLanguage, langDir));
            this.langResources.put(oneLanguage, new LanguageResource(oneLanguage, langDir));
        }
    }


    /**
     * Returns the language resource for the given language if available.
     *
     * @param lang the language
     * @return the language resource or the default configuration if language is not supported
     */
    public LanguageResource getLanguageResource(String lang) {

        Object probe = this.langResources.get(lang);
        if (null != probe) {
            return (LanguageResource) probe;
        }
        System.out.println(String.format("language %s not supported, using default configuration", lang));
        return this.langResources.get(DEFAULT);
    }


    /**
     * Tokenizes the given text in the given language. Returns an annotated string containing the
     * identified paragraphs with their text units and tokens.<br>
     * This method is thread-safe.
     *
     * @param inputText the text to tokenize
     * @param lang      the language of the text
     * @return an annotated string
     * @throws ProcessingException if input data causes an error e.g. if language is not supported
     */
    public AnnotatedString tokenize(String inputText, String lang) {

        // get language resource for language
        LanguageResource langRes = this.getLanguageResource(lang);

        // init attributed string for annotation
        AnnotatedString input = new FastAnnotatedString(inputText);

        // identify tokens
        this.identifyTokens(input, langRes);

        // identify punctuation
        this.identifyPunct(input, langRes);

        // identify abbreviations
        this.identifyAbbrev(input, langRes);

        // identify sentences and paragraphs
        this.identifyTus(input, langRes);

        // return result
        return input;
    }


    /**
     * Identifies tokens and annotates them. Tokens are sequences of non-whitespaces.
     *
     * @param input   an annotated string
     * @param langRes the language resource to use
     */
    private void identifyTokens(AnnotatedString input, LanguageResource langRes) {

        // init token start index
        int tokenStart = 0;
        // flag for indicating if new token was found
        boolean tokenFound = false;

        // get classes root annotation
        String rootClass = langRes.getClassesRoot().getTagName();

        // iterate over input
        for (char c = input.first(); c != CharacterIterator.DONE; c = input.next()) {
            if (Character.isWhitespace(c) || (c == '\u00a0')) {
                if (tokenFound) {
                    // annotate newly identified token
                    this.annotate(
                            input, CLASS_ANNO, rootClass, tokenStart, input.getIndex(),
                            input.substring(tokenStart, input.getIndex()), langRes);
                    tokenFound = false;
                }
            } else if (!tokenFound) {
                // a new token starts here, after some whitespaces
                tokenFound = true;
                tokenStart = input.getIndex();
            }
        }
        // annotate last token
        if (tokenFound) {
            this.annotate(
                    input, CLASS_ANNO, rootClass, tokenStart, input.getIndex(),
                    input.substring(tokenStart, input.getIndex()), langRes);
        }
    }


    /**
     * Identifies punctuations in the annotated tokens of the given annotated string
     *
     * @param input   an annotated string
     * @param langRes the language resource to use
     * @throws ProcessingException if an error occurs
     */
    private void identifyPunct(AnnotatedString input, LanguageResource langRes) {

        // get the matchers needed
        RegExp allPunctMatcher = langRes.getAllPunctMatcher();
        RegExp internalMatcher = langRes.getInternalMatcher();

        // get the class of the root element of the class hierarchy;
        // only tokens with this type are further examined
        String rootClass = langRes.getClassesRoot().getTagName();

        // iterate over tokens
        char c = input.setIndex(0);
        // move to first non-whitespace
        if (null == input.getAnnotation(CLASS_ANNO)) {
            c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
        }
        while (c != CharacterIterator.DONE) {

            // only check tokens
            if (null == input.getAnnotation(CLASS_ANNO)) {
                c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
                continue;
            }

            // get class of token
            String tokClass = (String) input.getAnnotation(CLASS_ANNO);
            // only check tokens with the most general class
            if (tokClass != rootClass) {
                c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
                continue;
            }

            // save the next token start position;
            // required because the input index might be changed later in this method
            int nextTokenStart = input.findNextAnnotation(CLASS_ANNO);

            // split punctuation on the left and right side of the token
            this.splitPunctuation(input, langRes);

            // update current token annotation
            tokClass = (String) input.getAnnotation(CLASS_ANNO);
            // only check tokens with the most general class
            if (tokClass != rootClass) {
                c = input.setIndex(nextTokenStart);
                continue;
            }

            // split clitics from left and right side of the token
            this.splitClitics(input, langRes);

            // update current token annotation
            tokClass = (String) input.getAnnotation(CLASS_ANNO);
            // only check tokens with the most general class
            if (tokClass != rootClass) {
                c = input.setIndex(nextTokenStart);
                continue;
            }

            // get the start index of the token
            int tokenStart = input.getIndex();
            // get the end index of the token c belongs to
            int tokenEnd = input.getRunLimit(CLASS_ANNO);
            // get the token content
            String image = input.substring(tokenStart, tokenEnd);

            // use the all rule to split image in parts consisting of
            // punctuation and non-punctuation
            List<Match> matches = allPunctMatcher.getAllMatches(image);
            // if there is no punctuation just continue
            if (0 == matches.size()) {
                c = input.setIndex(nextTokenStart);
                continue;
            }

            // this is the relative start position of current token within
            // the image
            int index = 0;
            // iterator over matches
            for (int i = 0; i < matches.size(); i++) {
                // get next match
                Match oneMatch = matches.get(i);

                // check if we have some non-punctuation before the current
                // punctuation
                if (index != oneMatch.getStartIndex()) {
                    // check for internal punctuation:
                    if (internalMatcher.matches(oneMatch.getImage())) {
                        // punctuation is internal;
                        // check for right context
                        if (this.hasRightContextEnd(oneMatch, matches, image, i)) {
                            // token not complete yet
                            continue;
                        }
                    }

                    // we have a breaking punctuation; create token for
                    // non-punctuation before the current punctuation
                    this.annotate(input, CLASS_ANNO, tokClass,
                            tokenStart + index,
                            tokenStart + oneMatch.getStartIndex(),
                            image.substring(index, oneMatch.getStartIndex()), langRes);
                    index = oneMatch.getStartIndex();
                }

                // punctuation is not internal:
                // get the class of the punctuation and create token for it
                String punctClass = this.identifyPunctClass(oneMatch, null, image, langRes);
                input.annotate(CLASS_ANNO, punctClass,
                        tokenStart + index,
                        tokenStart + oneMatch.getEndIndex());
                index = oneMatch.getEndIndex();
            }

            // cleanup after all matches have been processed
            if (index != image.length()) {
                // create a token from rest of image
                this.annotate(input, CLASS_ANNO, tokClass,
                        tokenStart + index,
                        tokenStart + image.length(),
                        image.substring(index), langRes);
            }

            // set iterator to next non-whitespace token
            c = input.setIndex(nextTokenStart);
        }
    }


    /**
     * Splits punctuation from the left and right side of the token if possible.
     *
     * @param input   the annotate string
     * @param langRes the language resource to use
     */
    private void splitPunctuation(AnnotatedString input, LanguageResource langRes) {

        // get the matchers needed
        RegExp allPunctMatcher = langRes.getAllPunctMatcher();

        // get the class of the root element of the class hierarchy;
        // only tokens with this type are further examined
        String rootClass = langRes.getClassesRoot().getTagName();

        // get the start index of the token
        int tokenStart = input.getIndex();
        // get the end index of the token
        int tokenEnd = input.getRunLimit(CLASS_ANNO);
        // get the token content
        String image = input.substring(tokenStart, tokenEnd);
        // get current token annotation
        String tokClass = (String) input.getAnnotation(CLASS_ANNO);

        // check for punctuation at the beginning of the token
        Match startMatch = allPunctMatcher.starts(image);
        while (null != startMatch) {
            // create token for punctuation
            String punctClass = this.identifyPunctClass(startMatch, null, image, langRes);
            input.annotate(CLASS_ANNO, punctClass,
                    tokenStart + startMatch.getStartIndex(),
                    tokenStart + startMatch.getEndIndex());
            tokenStart = tokenStart + startMatch.getEndIndex();
            image = input.substring(tokenStart, tokenEnd);
            input.setIndex(tokenStart);
            if (image.length() > 0) {
                this.annotate(input, CLASS_ANNO, tokClass,
                        tokenStart, tokenEnd, image, langRes);
                tokClass = (String) input.getAnnotation(CLASS_ANNO);
                if (tokClass != rootClass) {
                    // the remaining token could be matched with a non-root class,
                    // so stop splitting punctuation
                    break;
                }
                startMatch = allPunctMatcher.starts(image);
            } else {
                startMatch = null;
            }
        }

        // check for punctuation at the end of the token
        Match endMatch = allPunctMatcher.ends(image);
        while (null != endMatch) {
            // create token for punctuation
            String punctClass = this.identifyPunctClass(endMatch, null, image, langRes);
            input.annotate(CLASS_ANNO, punctClass,
                    tokenStart + endMatch.getStartIndex(),
                    tokenStart + endMatch.getEndIndex());
            tokenEnd = tokenStart + endMatch.getStartIndex();
            image = input.substring(tokenStart, tokenEnd);
            if (image.length() > 0) {
                this.annotate(input, CLASS_ANNO, tokClass,
                        tokenStart, tokenEnd, image, langRes);
                tokClass = (String) input.getAnnotation(CLASS_ANNO);
                if (tokClass != rootClass) {
                    // the remaining token could be matched with a non-root class,
                    // so stop splitting punctuation
                    break;
                }
                endMatch = allPunctMatcher.ends(image);
            } else {
                endMatch = null;
            }
        }
    }


    /**
     * Splits pro- and enclitics from the left and right side of the token if possible.
     *
     * @param input   the annotate string
     * @param langRes the language resource to use
     */
    private void splitClitics(AnnotatedString input, LanguageResource langRes) {

        // get matchers needed for clitics recognition
        RegExp proclitMatcher = langRes.getProcliticsMatcher();
        RegExp enclitMatcher = langRes.getEncliticsMatcher();

        // get the class of the root element of the class hierarchy;
        // only tokens with this type are further examined
        String rootClass = langRes.getClassesRoot().getTagName();

        // get the start index of the token
        int tokenStart = input.getIndex();
        // get the end index of the token c belongs to
        int tokenEnd = input.getRunLimit(CLASS_ANNO);
        // get the token content
        String image = input.substring(tokenStart, tokenEnd);
        // get current token annotation
        String tokClass = (String) input.getAnnotation(CLASS_ANNO);

        // check for proclitics
        Match proclit = proclitMatcher.starts(image);
        // create token for proclitic
        while (null != proclit) {
            String clitClass =
                    this.identifyClass(proclit.getImage(), proclitMatcher, langRes.getClitDescr());
            input.annotate(CLASS_ANNO, clitClass,
                    tokenStart + proclit.getStartIndex(),
                    tokenStart + proclit.getEndIndex());
            tokenStart = tokenStart + proclit.getEndIndex();
            image = input.substring(tokenStart, tokenEnd);
            input.setIndex(tokenStart);
            if (image.length() > 0) {
                this.annotate(input, CLASS_ANNO, tokClass,
                        tokenStart, tokenEnd, image, langRes);
                tokClass = (String) input.getAnnotation(CLASS_ANNO);
                if (tokClass != rootClass) {
                    // the remaining token could be matched with a non-root class,
                    // so stop splitting proclitics
                    break;
                }
                proclit = proclitMatcher.starts(image);
            } else {
                proclit = null;
            }
        }

        // check for enclitics
        Match enclit = enclitMatcher.ends(image);
        while (null != enclit) {
            // create tokens for enclitic
            String clitClass =
                    this.identifyClass(enclit.getImage(), enclitMatcher, langRes.getClitDescr());
            input.annotate(CLASS_ANNO, clitClass,
                    tokenStart + enclit.getStartIndex(),
                    tokenStart + enclit.getEndIndex());
            tokenEnd = tokenStart + enclit.getStartIndex();
            image = input.substring(tokenStart, tokenEnd);
            if (image.length() > 0) {
                this.annotate(input, CLASS_ANNO, tokClass,
                        tokenStart, tokenEnd, image, langRes);
                tokClass = (String) input.getAnnotation(CLASS_ANNO);
                if (tokClass != rootClass) {
                    // the remaining token could be matched with a non-root class,
                    // so stop splitting enclitics
                    break;
                }
                enclit = enclitMatcher.ends(image);
            } else {
                enclit = null;
            }
        }
    }


    /**
     * Returns {@code true} if there is a right context after the punctuation matched by the given
     * match or {@code false} when there is no right context.
     *
     * @param oneMatch a match matching a punctuation
     * @param matches  a list of all punctuation matching matches
     * @param i        the index of the match in the matches list
     * @param image    the string on which the punctuation matchers have been applied
     * @return a flag indicating if there is a right context
     */
    private boolean hasRightContextEnd(Match oneMatch, List<Match> matches, String image, int i) {

        if (i < (matches.size() - 1)) {
            // there is another punctuation later in the image
            Match nextMatch = matches.get(i + 1);
            return nextMatch.getStartIndex() != oneMatch.getEndIndex();
        }
        return oneMatch.getEndIndex() != image.length();
    }


    /**
     * Annotates the given input with the given key value pair at the given range. Also checks if a
     * more specific annotation can be found using the token classes matcher.
     *
     * @param input      the annotated string
     * @param key        the annotation key
     * @param value      the annotation value
     * @param beginIndex the index of the first character of the range
     * @param endIndex   the index of the character following the last character of the range
     * @param image      the surface image
     * @param langRes    the language resource to use
     */
    private void annotate(
            AnnotatedString input, String key, Object value,
            int beginIndex, int endIndex, String image, LanguageResource langRes) {

        // get matcher needed for token classes recognition
        RegExp allClassesMatcher = langRes.getAllClassesMatcher();

        if (allClassesMatcher.matches(image)) {
            String tokenClass = this.identifyClass(image, allClassesMatcher, langRes.getClassesDescr());
            input.annotate(key, tokenClass, beginIndex, endIndex);
        } else {
            input.annotate(key, value, beginIndex, endIndex);
        }
    }


    /**
     * Checks the class of a punctuation and returns the corresponding class name for annotation.
     *
     * @param punct   the match for which to find the class name
     * @param regExp  the regular expression that found the punctuation as a match, {@code null} if
     *                punctuation wasn't found via a regular expression
     * @param image   a string with the original token containing the punctuation
     * @param langRes a language resource that contains everything needed for identifying the class
     * @return the class name
     * @throws ProcessingException if class of punctuation can't be identified
     */
    private String identifyPunctClass(
            Match punct, RegExp regExp, String image, LanguageResource langRes) {

        String oneClass = this.identifyClass(punct.getImage(), regExp, langRes.getPunctDescr());
        // check if we have an ambiguous open/close punctuation; if
        // yes, resolve it
        if (langRes.isAncestor(PunctDescription.OPEN_CLOSE_PUNCT, oneClass)) {

            int nextIndex = punct.getEndIndex();
            if ((nextIndex >= image.length())
                    || !Character.isLetter(image.charAt(nextIndex))) {
                oneClass = PunctDescription.CLOSE_PUNCT;
            } else {
                int prevIndex = punct.getStartIndex() - 1;
                if ((prevIndex < 0)
                        || !Character.isLetter(image.charAt(prevIndex))) {
                    oneClass = PunctDescription.OPEN_PUNCT;
                }
            }
        }
        // return class name
        return oneClass;
    }


    /**
     * Identifies abbreviations in the annotated token of the given annotated string. Candidates are
     * tokens with a followed by a period.
     *
     * @param input   an annotated string
     * @param langRes the language resource to use
     * @throws ProcessingException if an error occurs
     */
    private void identifyAbbrev(AnnotatedString input, LanguageResource langRes) {

        // get matchers needed for abbreviation recognition
        RegExp allAbbrevMatcher = langRes.getAllAbbrevMatcher();

        // get map with abbreviation lists
        Map<String, Set<String>> abbrevLists = langRes.getAbbrevLists();

        // iterate over tokens
        char c = input.setIndex(0);
        // move to first non-whitespace
        if (null == input.getAnnotation(CLASS_ANNO)) {
            c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));
        }
        while (c != CharacterIterator.DONE) {

            // get the end index of the token c belongs to
            int tokenEnd = input.getRunLimit(CLASS_ANNO);

            // get the start index of the token
            int tokenStart = input.getIndex();
            // set iterator to next non-whitespace token
            c = input.setIndex(input.findNextAnnotation(CLASS_ANNO));

            // if the next token is a period immediately following the current token,
            // we have found a candidate for an abbreviation
            if (c == '.' && tokenEnd == input.getIndex()) {
                // get the token content WITH the following period
                tokenEnd = tokenEnd + 1;
                String image = input.substring(tokenStart, tokenEnd);

                // if the abbreviation contains a hyphen, it's sufficient to check
                // the part after the hyphen
                int hyphenPos = image.lastIndexOf("-");
                if (hyphenPos != -1) {
                    String afterHyphen = image.substring(hyphenPos + 1);
                    if (afterHyphen.matches("[^0-9]{2,}")) {
                        image = afterHyphen;
                    }
                }

                // check if token is in abbreviation lists
                boolean found = false;
                for (Map.Entry<String, Set<String>> oneEntry : abbrevLists.entrySet()) {
                    String abbrevClass = oneEntry.getKey();
                    Set<String> oneList = oneEntry.getValue();
                    if (oneList.contains(image)) {
                        // annotate abbreviation
                        input.annotate(CLASS_ANNO, abbrevClass, tokenStart, tokenEnd);
                        // stop looking for this abbreviation
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                // check if token is matched by abbreviation matcher
                if (allAbbrevMatcher.matches(image)) {
                    String abbrevClass =
                            this.identifyClass(image, allAbbrevMatcher, langRes.getAbbrevDescr());
                    input.annotate(CLASS_ANNO, abbrevClass, tokenStart, tokenEnd);
                    continue;
                }
            }
        }
    }


    /**
     * Identifies text units and paragraphs in the given annotated string and annotates them under the
     * annotation key BORDER_ANNO.
     *
     * @param input   an annotated string
     * @param langRes the language resource to use
     * @throws ProcessingException if an undefined class name is found
     */
    private void identifyTus(AnnotatedString input, LanguageResource langRes) {

        // get matcher needed for text unit identification
        RegExp intPunctMatcher = langRes.getInternalTuMatcher();

        // init end-of-sentence-mode flag; when in this mode, every token
        // that is not PTERM, PTERM_P, CLOSE_PUNCT or CLOSE_BRACKET initiates the
        // annotation of a new text unit.
        boolean eosMode = false;
        boolean abbrevMode = false;

        // iterate over tokens
        char c = input.setIndex(0);
        while (c != CharacterIterator.DONE) {

            int tokenStart = input.getRunStart(CLASS_ANNO);
            int tokenEnd = input.getRunLimit(CLASS_ANNO);
            // check if c belongs to a token
            if (null != input.getAnnotation(CLASS_ANNO)) {
                // check if we are in end-of-sentence mode
                if (eosMode) {
                    // if we find terminal punctuation or closing brackets,
                    // continue with the current sentence
                    if (langRes.isAncestor(
                            PunctDescription.TERM_PUNCT,
                            (String) input.getAnnotation(CLASS_ANNO))
                            || langRes.isAncestor(
                            PunctDescription.TERM_PUNCT_P,
                            (String) input.getAnnotation(CLASS_ANNO))
                            || langRes.isAncestor(
                            PunctDescription.CLOSE_PUNCT,
                            (String) input.getAnnotation(CLASS_ANNO))
                            || langRes.isAncestor(
                            PunctDescription.CLOSE_BRACKET,
                            (String) input.getAnnotation(CLASS_ANNO))) {
                        // do nothing
                    } else if (Character.isLowerCase(c)
                            || intPunctMatcher.matches(input.substring(input.getIndex(), input.getIndex() + 1))) {
                        // if we find a lower case letter or a punctuation that can
                        // only appear within a text unit, it was wrong alert, the
                        // sentence hasn't ended yet
                        eosMode = false;
                    } else {
                        // otherwise, we just found the first element of the next sentence
                        input.annotate(BORDER_ANNO, TU_BORDER, tokenStart, tokenStart + 1);
                        eosMode = false;
                    }
                } else if (abbrevMode) {
                    String image = input.substring(tokenStart, tokenEnd);
                    if (langRes.getNonCapTerms().contains(image)
                            || langRes.isAncestor(
                            PunctDescription.OPEN_PUNCT,
                            (String) input.getAnnotation(CLASS_ANNO))) {
                        // there is a term that only starts with a capital letter at the
                        // beginning of a sentence OR
                        // an opening punctuation;
                        // so we just found the first element of the next sentence
                        input.annotate(BORDER_ANNO, TU_BORDER, tokenStart, tokenStart + 1);
                    }
                    abbrevMode = false;
                    // continue without going to the next token;
                    // it's possible that after an abbreviation follows a
                    // end-of-sentence marker
                    continue;
                } else {
                    // check if token is a end-of-sentence marker
                    if (langRes.isAncestor(
                            PunctDescription.TERM_PUNCT,
                            (String) input.getAnnotation(CLASS_ANNO))
                            || langRes.isAncestor(
                            PunctDescription.TERM_PUNCT_P,
                            (String) input.getAnnotation(CLASS_ANNO))) {
                        // check if next token is a whitespace or a sentence
                        // continuing token
                        input.setIndex(tokenEnd);
                        if (null == input.getAnnotation(CLASS_ANNO)
                                || langRes.isAncestor(
                                PunctDescription.TERM_PUNCT,
                                (String) input.getAnnotation(CLASS_ANNO))
                                || langRes.isAncestor(
                                PunctDescription.TERM_PUNCT_P,
                                (String) input.getAnnotation(CLASS_ANNO))
                                || langRes.isAncestor(
                                PunctDescription.CLOSE_PUNCT,
                                (String) input.getAnnotation(CLASS_ANNO))
                                || langRes.isAncestor(
                                PunctDescription.CLOSE_BRACKET,
                                (String) input.getAnnotation(CLASS_ANNO))) {
                            eosMode = true;
                        }
                    } else if (langRes.isAncestor(
                            AbbrevDescription.B_ABBREVIATION,
                            (String) input.getAnnotation(CLASS_ANNO))) {
                        // check if token is a breaking abbreviation
                        abbrevMode = true;
                    }
                }
                // set iterator to next token
                c = input.setIndex(tokenEnd);
            } else {
                // check for paragraph change in whitespace sequence
                if (this.isParagraphChange(input.substring(tokenStart, tokenEnd))) {
                    eosMode = false;
                    abbrevMode = false;
                    // set iterator to next token
                    c = input.setIndex(tokenEnd);
                    // next token starts a new paragraph
                    if (c != CharacterIterator.DONE) {
                        input.annotate(BORDER_ANNO, P_BORDER,
                                input.getIndex(), input.getIndex() + 1);
                    }
                } else {
                    // just set iterator to next token
                    c = input.setIndex(tokenEnd);
                }
            }
        }
    }


    /**
     * Called with a sequence of whitespaces. It returns a flag indicating if the sequence contains a
     * paragraph change. A paragraph change is defined as a sequence of whitespaces that contains two
     * line breaks.
     *
     * @param wSpaces a string consisting only of whitespaces
     * @return a flag indicating a paragraph change
     */
    private boolean isParagraphChange(String wSpaces) {

        int len = wSpaces.length();
        for (int i = 0; i < len; i++) {
            char c = wSpaces.charAt(i);
            if (('\n' == c) || ('\r' == c)) {
                // possible continuations for a paragraph change:
                // - another \n -> paragraph change in Unix or Windows
                // the second \n must no be the next character!
                // this way we catch \n\n for Unix and \r\n\r\n for Windows
                // - another \r -> paragraph change in MacOs or Windows
                // the second \r must no be the next character!
                // this way we catch \r\r for MacOs and \r\n\r\n for Windows
                // we just look for a second occurrence of the c just found
                for (int j = i + 1; j < len; j++) {
                    if (c == wSpaces.charAt(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Identifies the class of the given string and returns the corresponding class name for
     * annotation.
     *
     * @param string the string for which to find the class name
     * @param regExp the regular expression that found the string as a match, {@code null} if string wasn't
     *               found via a regular expression
     * @param descr  a description that contains everything needed for identifying the class
     * @return the class name
     * @throws ProcessingException if class of string can't be identified
     */
    private String identifyClass(String string, RegExp regExp, Description descr) {

        // first try to identify class via the regular expression
        if (null != regExp) {
            Map<RegExp, String> regExpMap = descr.getRegExpMap();
            String oneClass = regExpMap.get(regExp);
            if (null != oneClass) {
                return oneClass;
            }
        }

        // get hash map with classes
        Map<String, RegExp> definitionsMap = descr.getDefinitionsMap();
        // iterate over classes
        for (Map.Entry<String, RegExp> oneEntry : definitionsMap.entrySet()) {
            // check if string is of that class
            String oneClass = oneEntry.getKey();
            RegExp oneRe = oneEntry.getValue();
            if (oneRe.matches(string)) {
                // return class name
                return oneClass;
            }
        }
        // throw exception if no class for string was found
        throw new ProcessingException(String.format("could not find class for %s", string));
    }


}

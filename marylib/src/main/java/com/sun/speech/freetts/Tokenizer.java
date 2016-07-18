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

import java.io.Reader;

/**
 * Chops a string or text file into Token instances.
 */
public interface Tokenizer {
    /**
     * Sets the text to be tokenized by this tokenizer.
     *
     * @param textToTokenize the text to tokenize
     */
    void setInputText(String textToTokenize);

    /**
     * Sets the input reader.
     *
     * @param reader the input source
     */
    void setInputReader(Reader reader);


    /**
     * Returns the next token.
     *
     * @return the next token if it exists; otherwise null
     */
    Token getNextToken();


    /**
     * Returns true if there are more tokens, false otherwise.
     *
     * @return true if there are more tokens; otherwise false
     */
    boolean hasMoreTokens();

    /**
     * Returns true if there were errors while reading tokens.
     *
     * @return true if there were errors; otherwise false
     */
    boolean hasErrors();

    /**
     * If hasErrors returns true, returns a description of the error
     * encountered.  Otherwise returns null.
     *
     * @return a description of the last error that occurred
     */
    String getErrorDescription();

    /**
     * Sets the whitespace symbols of this Tokenizer to the given
     * symbols.
     *
     * @param symbols the whitespace symbols
     */
    void setWhitespaceSymbols(String symbols);

    /**
     * Sets the single character symbols of this Tokenizer to the given
     * symbols.
     *
     * @param symbols the single character symbols
     */
    void setSingleCharSymbols(String symbols);

    /**
     * Sets the prepunctuation symbols of this Tokenizer to the given
     * symbols.
     *
     * @param symbols the prepunctuation symbols
     */
    void setPrepunctuationSymbols(String symbols);

    /**
     * Sets the postpunctuation symbols of this Tokenizer to the given
     * symbols.
     *
     * @param symbols the postpunctuation symbols
     */
    void setPostpunctuationSymbols(String symbols);

    /**
     * Determines if the current token should start a new sentence.
     *
     * @return true if a new sentence should be started
     */
    boolean isBreak();
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mf.org.apache.xerces.impl;

import java.io.CharConversionException;
import java.io.EOFException;
import java.io.IOException;

import mf.org.apache.xerces.impl.io.MalformedByteSequenceException;
import mf.org.apache.xerces.impl.msg.XMLMessageFormatter;
import mf.org.apache.xerces.util.SymbolTable;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLComponentManager;
import mf.org.apache.xerces.xni.parser.XMLConfigurationException;
import mf.org.apache.xerces.xni.parser.XMLInputSource;


/**
 * This class scans the version of the document to determine
 * which scanner to use: XML 1.1 or XML 1.0.
 * The version is scanned using XML 1.1. scanner.
 *
 * @author Neil Graham, IBM
 * @author Elena Litani, IBM
 * @version $Id: XMLVersionDetector.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @xerces.internal
 */
public class XMLVersionDetector {

    //
    // Constants
    //

    /**
     * Property identifier: symbol table.
     */
    protected static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    // property identifiers
    /**
     * Property identifier: error reporter.
     */
    protected static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
    /**
     * Property identifier: entity manager.
     */
    protected static final String ENTITY_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;
    /**
     * Symbol: "version".
     */
    protected static final String fVersionSymbol = "version".intern();

    //
    // Data
    //
    // symbol:  [xml]:
    protected static final String fXMLSymbol = "[xml]".intern();
    private static final char[] XML11_VERSION = new char[]{'1', '.', '1'};
    private final char[] fExpectedVersionString = {'<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's',
            'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' '};
    /**
     * Symbol table.
     */
    protected SymbolTable fSymbolTable;
    /**
     * Error reporter.
     */
    protected XMLErrorReporter fErrorReporter;
    /**
     * Entity manager.
     */
    protected XMLEntityManager fEntityManager;
    protected String fEncoding = null;

    /**
     * @param componentManager The component manager.
     * @throws XNIException Throws exception if required features and
     *                      properties cannot be found.
     */
    public void reset(XMLComponentManager componentManager)
            throws XMLConfigurationException {

        // Xerces properties
        fSymbolTable = (SymbolTable) componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter) componentManager.getProperty(ERROR_REPORTER);
        fEntityManager = (XMLEntityManager) componentManager.getProperty(ENTITY_MANAGER);
        for (int i = 14; i < fExpectedVersionString.length; i++)
            fExpectedVersionString[i] = ' ';
    } // reset(XMLComponentManager)

    /**
     * Reset the reference to the appropriate scanner given the version of the
     * document and start document scanning.
     *
     * @param scanner - the scanner to use
     * @param version - the version of the document (XML 1.1 or XML 1.0).
     */
    public void startDocumentParsing(XMLEntityHandler scanner, short version) {

        if (version == Constants.XML_VERSION_1_0) {
            fEntityManager.setScannerVersion(Constants.XML_VERSION_1_0);
        } else {
            fEntityManager.setScannerVersion(Constants.XML_VERSION_1_1);
        }
        // Make sure the locator used by the error reporter is the current entity scanner.
        fErrorReporter.setDocumentLocator(fEntityManager.getEntityScanner());

        // Note: above we reset fEntityScanner in the entity manager, thus in startEntity
        // in each scanner fEntityScanner field must be reset to reflect the change.
        // 
        fEntityManager.setEntityHandler(scanner);

        scanner.startEntity(fXMLSymbol, fEntityManager.getCurrentResourceIdentifier(), fEncoding, null);
    }


    /**
     * This methods scans the XML declaration to find out the version
     * (and provisional encoding)  of the document.
     * The scanning is doing using XML 1.1 scanner.
     *
     * @param inputSource
     * @return short - Constants.XML_VERSION_1_1 if document version 1.1,
     * otherwise Constants.XML_VERSION_1_0
     * @throws IOException
     */
    public short determineDocVersion(XMLInputSource inputSource) throws IOException {
        fEncoding = fEntityManager.setupCurrentEntity(fXMLSymbol, inputSource, false, true);

        // Must use XML 1.0 scanner to handle whitespace correctly
        // in the XML declaration.
        fEntityManager.setScannerVersion(Constants.XML_VERSION_1_0);
        XMLEntityScanner scanner = fEntityManager.getEntityScanner();
        try {
            if (!scanner.skipString("<?xml")) {
                // definitely not a well-formed 1.1 doc!
                return Constants.XML_VERSION_1_0;
            }
            if (!scanner.skipDeclSpaces()) {
                fixupCurrentEntity(fEntityManager, fExpectedVersionString, 5);
                return Constants.XML_VERSION_1_0;
            }
            if (!scanner.skipString("version")) {
                fixupCurrentEntity(fEntityManager, fExpectedVersionString, 6);
                return Constants.XML_VERSION_1_0;
            }
            scanner.skipDeclSpaces();
            // Check if the next character is '='. If it is then consume it.
            if (scanner.peekChar() != '=') {
                fixupCurrentEntity(fEntityManager, fExpectedVersionString, 13);
                return Constants.XML_VERSION_1_0;
            }
            scanner.scanChar();
            scanner.skipDeclSpaces();
            int quoteChar = scanner.scanChar();
            fExpectedVersionString[14] = (char) quoteChar;
            for (int versionPos = 0; versionPos < XML11_VERSION.length; versionPos++) {
                fExpectedVersionString[15 + versionPos] = (char) scanner.scanChar();
            }
            // REVISIT:  should we check whether this equals quoteChar? 
            fExpectedVersionString[18] = (char) scanner.scanChar();
            fixupCurrentEntity(fEntityManager, fExpectedVersionString, 19);
            int matched = 0;
            for (; matched < XML11_VERSION.length; matched++) {
                if (fExpectedVersionString[15 + matched] != XML11_VERSION[matched])
                    break;
            }
            return (matched == XML11_VERSION.length) ?
                    Constants.XML_VERSION_1_1 :
                    Constants.XML_VERSION_1_0;
        }
        // encoding errors
        catch (MalformedByteSequenceException e) {
            fErrorReporter.reportError(e.getDomain(), e.getKey(),
                    e.getArguments(), XMLErrorReporter.SEVERITY_FATAL_ERROR, e);
            return Constants.XML_VERSION_ERROR;
        } catch (CharConversionException e) {
            fErrorReporter.reportError(
                    XMLMessageFormatter.XML_DOMAIN,
                    "CharConversionFailure",
                    null,
                    XMLErrorReporter.SEVERITY_FATAL_ERROR, e);
            return Constants.XML_VERSION_ERROR;
        }
        // premature end of file
        catch (EOFException e) {
            fErrorReporter.reportError(
                    XMLMessageFormatter.XML_DOMAIN,
                    "PrematureEOF",
                    null,
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
            return Constants.XML_VERSION_ERROR;
        }
    }

    // This method prepends "length" chars from the char array,
    // from offset 0, to the manager's fCurrentEntity.ch.
    private void fixupCurrentEntity(XMLEntityManager manager,
                                    char[] scannedChars, int length) {
        XMLEntityManager.ScannedEntity currentEntity = manager.getCurrentEntity();
        if (currentEntity.count - currentEntity.position + length > currentEntity.ch.length) {
            //resize array; this case is hard to imagine...
            char[] tempCh = currentEntity.ch;
            currentEntity.ch = new char[length + currentEntity.count - currentEntity.position + 1];
            System.arraycopy(tempCh, 0, currentEntity.ch, 0, tempCh.length);
        }
        if (currentEntity.position < length) {
            // have to move sensitive stuff out of the way...
            System.arraycopy(currentEntity.ch, currentEntity.position, currentEntity.ch, length, currentEntity.count - currentEntity.position);
            currentEntity.count += length - currentEntity.position;
        } else {
            // have to reintroduce some whitespace so this parses:
            for (int i = length; i < currentEntity.position; i++)
                currentEntity.ch[i] = ' ';
        }
        // prepend contents...
        System.arraycopy(scannedChars, 0, currentEntity.ch, 0, length);
        currentEntity.position = 0;
        currentEntity.baseCharOffset = 0;
        currentEntity.startPosition = 0;
        currentEntity.columnNumber = currentEntity.lineNumber = 1;
    }

} // class XMLVersionDetector


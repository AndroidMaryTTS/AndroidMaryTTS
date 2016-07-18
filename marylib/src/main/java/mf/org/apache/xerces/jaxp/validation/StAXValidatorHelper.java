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

package mf.org.apache.xerces.jaxp.validation;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLEventReader;
import mf.javax.xml.stream.XMLStreamConstants;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.XMLStreamReader;
import mf.javax.xml.stream.XMLStreamWriter;
import mf.javax.xml.stream.events.Attribute;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.Comment;
import mf.javax.xml.stream.events.DTD;
import mf.javax.xml.stream.events.EndDocument;
import mf.javax.xml.stream.events.EndElement;
import mf.javax.xml.stream.events.EntityDeclaration;
import mf.javax.xml.stream.events.EntityReference;
import mf.javax.xml.stream.events.Namespace;
import mf.javax.xml.stream.events.ProcessingInstruction;
import mf.javax.xml.stream.events.StartDocument;
import mf.javax.xml.stream.events.StartElement;
import mf.javax.xml.stream.events.XMLEvent;
import mf.javax.xml.transform.Result;
import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stax.StAXResult;
import mf.javax.xml.transform.stax.StAXSource;
import mf.org.apache.xerces.impl.Constants;
import mf.org.apache.xerces.impl.XMLErrorReporter;
import mf.org.apache.xerces.impl.validation.EntityState;
import mf.org.apache.xerces.impl.validation.ValidationManager;
import mf.org.apache.xerces.impl.xs.XMLSchemaValidator;
import mf.org.apache.xerces.util.JAXPNamespaceContextWrapper;
import mf.org.apache.xerces.util.StAXLocationWrapper;
import mf.org.apache.xerces.util.SymbolTable;
import mf.org.apache.xerces.util.XMLAttributesImpl;
import mf.org.apache.xerces.util.XMLStringBuffer;
import mf.org.apache.xerces.util.XMLSymbols;
import mf.org.apache.xerces.xni.QName;
import mf.org.apache.xerces.xni.XMLString;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLParseException;

/**
 * <p>A validator helper for <code>StAXSource</code>s.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: StAXValidatorHelper.java 713638 2008-11-13 04:42:18Z mrglavas $
 */
final class StAXValidatorHelper implements ValidatorHelper, EntityState {

    // property identifiers

    /**
     * Property identifier: string interning.
     */
    private static final String STRING_INTERNING = "javax.xml.stream.isInterning";

    /**
     * Property identifier: error reporter.
     */
    private static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    /**
     * Property identifier: XML Schema validator.
     */
    private static final String SCHEMA_VALIDATOR =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;

    /**
     * Property identifier: symbol table.
     */
    private static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    /**
     * Property identifier: validation manager.
     */
    private static final String VALIDATION_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;

    //
    // Data
    //
    /**
     * Fields for start element, end element and characters.
     **/
    final QName fElementQName = new QName();
    final QName fAttributeQName = new QName();
    final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    final ArrayList fDeclaredPrefixes = new ArrayList();
    final XMLString fTempString = new XMLString();
    final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    /**
     * Error reporter.
     */
    private final XMLErrorReporter fErrorReporter;
    /**
     * Schema validator.
     **/
    private final XMLSchemaValidator fSchemaValidator;
    /**
     * Symbol table
     **/
    private final SymbolTable fSymbolTable;
    /**
     * Validation manager.
     **/
    private final ValidationManager fValidationManager;
    /**
     * Component manager.
     **/
    private final XMLSchemaValidatorComponentManager fComponentManager;
    /**
     * The namespace context of this document: stores namespaces in scope.
     **/
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    /**
     * XML Locator wrapper for StAX.
     **/
    private final StAXLocationWrapper fStAXLocationWrapper = new StAXLocationWrapper();
    /**
     * On demand reader of the Location from an XMLStreamReader.
     **/
    private final XMLStreamReaderLocation fXMLStreamReaderLocation = new XMLStreamReaderLocation();
    /**
     * Map for tracking entity declarations.
     */
    private HashMap fEntities = null;
    /**
     * Flag used to track whether XML names and Namespace URIs have been internalized.
     */
    private boolean fStringsInternalized = false;
    /**
     * Validator helper for XMLStreamReaders.
     **/
    private StreamHelper fStreamHelper;
    /**
     * Validator helper for XMLEventReaders.
     **/
    private EventHelper fEventHelper;
    /**
     * StAX document handler.
     **/
    private StAXDocumentHandler fStAXValidatorHandler;
    /**
     * StAX stream result builder.
     **/
    private StAXStreamResultBuilder fStAXStreamResultBuilder;
    /**
     * StAX event result builder.
     **/
    private StAXEventResultBuilder fStAXEventResultBuilder;
    /**
     * Document depth.
     **/
    private int fDepth = 0;
    /**
     * Current event.
     **/
    private XMLEvent fCurrentEvent = null;

    public StAXValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
        fComponentManager = componentManager;
        fErrorReporter = (XMLErrorReporter) fComponentManager.getProperty(ERROR_REPORTER);
        fSchemaValidator = (XMLSchemaValidator) fComponentManager.getProperty(SCHEMA_VALIDATOR);
        fSymbolTable = (SymbolTable) fComponentManager.getProperty(SYMBOL_TABLE);
        fValidationManager = (ValidationManager) fComponentManager.getProperty(VALIDATION_MANAGER);
        fNamespaceContext = new JAXPNamespaceContextWrapper(fSymbolTable);
        fNamespaceContext.setDeclaredPrefixes(fDeclaredPrefixes);
    }
    
    /*
     * ValidatorHelper methods
     */

    @Override
    public void validate(Source source, Result result) throws SAXException,
            IOException {
        if (result instanceof StAXResult || result == null) {
            StAXSource staxSource = (StAXSource) source;
            StAXResult staxResult = (StAXResult) result;
            try {
                XMLStreamReader streamReader = staxSource.getXMLStreamReader();
                if (streamReader != null) {
                    // Hand off to XMLStreamReader helper.
                    if (fStreamHelper == null) {
                        fStreamHelper = new StreamHelper();
                    }
                    fStreamHelper.validate(streamReader, staxResult);
                } else {
                    // Hand off to XMLEventReader helper.
                    if (fEventHelper == null) {
                        fEventHelper = new EventHelper();
                    }
                    fEventHelper.validate(staxSource.getXMLEventReader(), staxResult);
                }
            } catch (XMLStreamException e) {
                throw new SAXException(e);
            } catch (XMLParseException e) {
                throw Util.toSAXParseException(e);
            } catch (XNIException e) {
                throw Util.toSAXException(e);
            } finally {
                // Release references to application objects
                fCurrentEvent = null;
                fStAXLocationWrapper.setLocation(null);
                fXMLStreamReaderLocation.setXMLStreamReader(null);
                if (fStAXValidatorHandler != null) {
                    fStAXValidatorHandler.setStAXResult(null);
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                "SourceResultMismatch",
                new Object[]{source.getClass().getName(), result.getClass().getName()}));
    }

    /*
     * EntityState methods
     */

    @Override
    public boolean isEntityDeclared(String name) {
        if (fEntities != null) {
            return fEntities.containsKey(name);
        }
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String name) {
        if (fEntities != null) {
            EntityDeclaration entityDecl = (EntityDeclaration) fEntities.get(name);
            // If the entity is associated with a notation then it must be an unparsed entity.
            if (entityDecl != null) {
                return (entityDecl.getNotationName() != null);
            }
        }
        return false;
    }
    
    /*
     * Other methods.
     */

    final EntityDeclaration getEntityDeclaration(String name) {
        return (fEntities != null) ? (EntityDeclaration) fEntities.get(name) : null;
    }

    final XMLEvent getCurrentEvent() {
        return fCurrentEvent;
    }

    /**
     * Fills in a QName object.
     */
    final void fillQName(QName toFill, String uri, String localpart, String prefix) {
        if (!fStringsInternalized) {
            uri = (uri != null && uri.length() > 0) ? fSymbolTable.addSymbol(uri) : null;
            localpart = (localpart != null) ? fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING;
            prefix = (prefix != null && prefix.length() > 0) ? fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
        } else {
            if (uri != null && uri.length() == 0) {
                uri = null;
            }
            if (localpart == null) {
                localpart = XMLSymbols.EMPTY_STRING;
            }
            if (prefix == null) {
                prefix = XMLSymbols.EMPTY_STRING;
            }
        }
        String raw = localpart;
        if (prefix != XMLSymbols.EMPTY_STRING) {
            fStringBuffer.clear();
            fStringBuffer.append(prefix);
            fStringBuffer.append(':');
            fStringBuffer.append(localpart);
            raw = fSymbolTable.addSymbol(fStringBuffer.ch, fStringBuffer.offset, fStringBuffer.length);
        }
        toFill.setValues(prefix, localpart, raw, uri);
    }

    /**
     * Setup for validation.
     **/
    final void setup(Location location, StAXResult result, boolean stringsInternalized) {
        fDepth = 0;
        fComponentManager.reset();
        setupStAXResultHandler(result);
        fValidationManager.setEntityState(this);
        if (fEntities != null && !fEntities.isEmpty()) {
            // should only clear this if the last document contained unparsed entities
            fEntities.clear();
        }
        fStAXLocationWrapper.setLocation(location);
        fErrorReporter.setDocumentLocator(fStAXLocationWrapper);
        fStringsInternalized = stringsInternalized;
    }

    /**
     * Copies entity declarations into a hash map.
     */
    final void processEntityDeclarations(List entityDecls) {
        int size = (entityDecls != null) ? entityDecls.size() : 0;
        if (size > 0) {
            if (fEntities == null) {
                fEntities = new HashMap();
            }
            for (int i = 0; i < size; ++i) {
                EntityDeclaration decl = (EntityDeclaration) entityDecls.get(i);
                fEntities.put(decl.getName(), decl);
            }
        }
    }

    /**
     * Sets up handler for <code>StAXResult</code>.
     */
    private void setupStAXResultHandler(StAXResult result) {
        // If there's no StAXResult, unset the validator handler
        if (result == null) {
            fStAXValidatorHandler = null;
            fSchemaValidator.setDocumentHandler(null);
            return;
        }
        XMLStreamWriter writer = result.getXMLStreamWriter();
        if (writer != null) {
            if (fStAXStreamResultBuilder == null) {
                fStAXStreamResultBuilder = new StAXStreamResultBuilder(fNamespaceContext);
            }
            fStAXValidatorHandler = fStAXStreamResultBuilder;
            fStAXStreamResultBuilder.setStAXResult(result);
        } else {
            if (fStAXEventResultBuilder == null) {
                fStAXEventResultBuilder = new StAXEventResultBuilder(this, fNamespaceContext);
            }
            fStAXValidatorHandler = fStAXEventResultBuilder;
            fStAXEventResultBuilder.setStAXResult(result);
        }
        fSchemaValidator.setDocumentHandler(fStAXValidatorHandler);
    }

    /**
     * On demand reader of the Location from an XMLStreamReader.
     */
    static final class XMLStreamReaderLocation implements Location {

        private XMLStreamReader reader;

        public XMLStreamReaderLocation() {
        }

        @Override
        public int getCharacterOffset() {
            Location loc = getLocation();
            if (loc != null) {
                return loc.getCharacterOffset();
            }
            return -1;
        }

        @Override
        public int getColumnNumber() {
            Location loc = getLocation();
            if (loc != null) {
                return loc.getColumnNumber();
            }
            return -1;
        }

        @Override
        public int getLineNumber() {
            Location loc = getLocation();
            if (loc != null) {
                return loc.getLineNumber();
            }
            return -1;
        }

        @Override
        public String getPublicId() {
            Location loc = getLocation();
            if (loc != null) {
                return loc.getPublicId();
            }
            return null;
        }

        @Override
        public String getSystemId() {
            Location loc = getLocation();
            if (loc != null) {
                return loc.getSystemId();
            }
            return null;
        }

        public void setXMLStreamReader(XMLStreamReader reader) {
            this.reader = reader;
        }

        private Location getLocation() {
            return reader != null ? reader.getLocation() : null;
        }
    }

    /**
     * Helper for <code>XMLStreamReader</code>s.
     */
    final class StreamHelper {

        StreamHelper() {
        }

        final void validate(XMLStreamReader reader, StAXResult result)
                throws SAXException, XMLStreamException {
            if (reader.hasNext()) {
                int eventType = reader.getEventType();
                if (eventType != XMLStreamConstants.START_DOCUMENT &&
                        eventType != XMLStreamConstants.START_ELEMENT) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                            "StAXIllegalInitialState", null));
                }
                fXMLStreamReaderLocation.setXMLStreamReader(reader);
                setup(fXMLStreamReaderLocation, result, Boolean.TRUE.equals(reader.getProperty(STRING_INTERNING)));
                fSchemaValidator.startDocument(fStAXLocationWrapper, null, fNamespaceContext, null);
                do {
                    switch (eventType) {
                        case XMLStreamConstants.START_ELEMENT:
                            ++fDepth;
                            fillQName(fElementQName, reader.getNamespaceURI(),
                                    reader.getLocalName(), reader.getPrefix());
                            fillXMLAttributes(reader);
                            fillDeclaredPrefixes(reader);
                            fNamespaceContext.setNamespaceContext(reader.getNamespaceContext());
                            fSchemaValidator.startElement(fElementQName, fAttributes, null);
                            break;
                        case XMLStreamConstants.END_ELEMENT:
                            fillQName(fElementQName, reader.getNamespaceURI(),
                                    reader.getLocalName(), reader.getPrefix());
                            fillDeclaredPrefixes(reader);
                            fNamespaceContext.setNamespaceContext(reader.getNamespaceContext());
                            fSchemaValidator.endElement(fElementQName, null);
                            --fDepth;
                            break;
                        case XMLStreamConstants.CHARACTERS:
                        case XMLStreamConstants.SPACE:
                            fTempString.setValues(reader.getTextCharacters(),
                                    reader.getTextStart(), reader.getTextLength());
                            fSchemaValidator.characters(fTempString, null);
                            break;
                        case XMLStreamConstants.CDATA:
                            fSchemaValidator.startCDATA(null);
                            fTempString.setValues(reader.getTextCharacters(),
                                    reader.getTextStart(), reader.getTextLength());
                            fSchemaValidator.characters(fTempString, null);
                            fSchemaValidator.endCDATA(null);
                            break;
                        case XMLStreamConstants.START_DOCUMENT:
                            ++fDepth;
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.startDocument(reader);
                            }
                            break;
                        case XMLStreamConstants.PROCESSING_INSTRUCTION:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.processingInstruction(reader);
                            }
                            break;
                        case XMLStreamConstants.COMMENT:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.comment(reader);
                            }
                            break;
                        case XMLStreamConstants.ENTITY_REFERENCE:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.entityReference(reader);
                            }
                            break;
                        case XMLStreamConstants.DTD:
                            processEntityDeclarations((List) reader.getProperty("javax.xml.stream.entities"));
                            break;
                    }
                    eventType = reader.next();
                }
                while (reader.hasNext() && fDepth > 0);
                fSchemaValidator.endDocument(null);
                if (eventType == XMLStreamConstants.END_DOCUMENT && fStAXValidatorHandler != null) {
                    fStAXValidatorHandler.endDocument(reader);
                }
            }
        }

        /**
         * Fills in the XMLAttributes object.
         */
        private void fillXMLAttributes(XMLStreamReader reader) {
            fAttributes.removeAllAttributes();
            final int len = reader.getAttributeCount();
            for (int i = 0; i < len; ++i) {
                fillQName(fAttributeQName, reader.getAttributeNamespace(i),
                        reader.getAttributeLocalName(i), reader.getAttributePrefix(i));
                String type = reader.getAttributeType(i);
                fAttributes.addAttributeNS(fAttributeQName,
                        (type != null) ? type : XMLSymbols.fCDATASymbol, reader.getAttributeValue(i));
                fAttributes.setSpecified(i, reader.isAttributeSpecified(i));
            }
        }

        /**
         * Fills in the list of declared prefixes.
         */
        private void fillDeclaredPrefixes(XMLStreamReader reader) {
            fDeclaredPrefixes.clear();
            final int len = reader.getNamespaceCount();
            for (int i = 0; i < len; ++i) {
                String prefix = reader.getNamespacePrefix(i);
                fDeclaredPrefixes.add(prefix != null ? prefix : "");
            }
        }
    }

    /**
     * Helper for <code>XMLEventReader</code>s.
     */
    final class EventHelper {

        //
        // Constants
        //

        /**
         * Chunk size (1024).
         */
        private static final int CHUNK_SIZE = (1 << 10);

        /**
         * Chunk mask (CHUNK_SIZE - 1).
         */
        private static final int CHUNK_MASK = CHUNK_SIZE - 1;

        //
        // Data
        //

        /**
         * Array for holding character data.
         **/
        private final char[] fCharBuffer = new char[CHUNK_SIZE];

        EventHelper() {
        }

        final void validate(XMLEventReader reader, StAXResult result)
                throws SAXException, XMLStreamException {
            fCurrentEvent = reader.peek();
            if (fCurrentEvent != null) {
                int eventType = fCurrentEvent.getEventType();
                if (eventType != XMLStreamConstants.START_DOCUMENT &&
                        eventType != XMLStreamConstants.START_ELEMENT) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                            "StAXIllegalInitialState", null));
                }
                setup(null, result, false);
                fSchemaValidator.startDocument(fStAXLocationWrapper, null, fNamespaceContext, null);
                loop:
                while (reader.hasNext()) {
                    fCurrentEvent = reader.nextEvent();
                    eventType = fCurrentEvent.getEventType();
                    switch (eventType) {
                        case XMLStreamConstants.START_ELEMENT:
                            ++fDepth;
                            StartElement start = fCurrentEvent.asStartElement();
                            fillQName(fElementQName, start.getName());
                            fillXMLAttributes(start);
                            fillDeclaredPrefixes(start);
                            fNamespaceContext.setNamespaceContext(start.getNamespaceContext());
                            fStAXLocationWrapper.setLocation(start.getLocation());
                            fSchemaValidator.startElement(fElementQName, fAttributes, null);
                            break;
                        case XMLStreamConstants.END_ELEMENT:
                            EndElement end = fCurrentEvent.asEndElement();
                            fillQName(fElementQName, end.getName());
                            fillDeclaredPrefixes(end);
                            fStAXLocationWrapper.setLocation(end.getLocation());
                            fSchemaValidator.endElement(fElementQName, null);
                            if (--fDepth <= 0) {
                                break loop;
                            }
                            break;
                        case XMLStreamConstants.CHARACTERS:
                        case XMLStreamConstants.SPACE:
                            if (fStAXValidatorHandler != null) {
                                Characters chars = fCurrentEvent.asCharacters();
                                fStAXValidatorHandler.setIgnoringCharacters(true);
                                sendCharactersToValidator(chars.getData());
                                fStAXValidatorHandler.setIgnoringCharacters(false);
                                fStAXValidatorHandler.characters(chars);
                            } else {
                                sendCharactersToValidator(fCurrentEvent.asCharacters().getData());
                            }
                            break;
                        case XMLStreamConstants.CDATA:
                            if (fStAXValidatorHandler != null) {
                                Characters chars = fCurrentEvent.asCharacters();
                                fStAXValidatorHandler.setIgnoringCharacters(true);
                                fSchemaValidator.startCDATA(null);
                                sendCharactersToValidator(fCurrentEvent.asCharacters().getData());
                                fSchemaValidator.endCDATA(null);
                                fStAXValidatorHandler.setIgnoringCharacters(false);
                                fStAXValidatorHandler.cdata(chars);
                            } else {
                                fSchemaValidator.startCDATA(null);
                                sendCharactersToValidator(fCurrentEvent.asCharacters().getData());
                                fSchemaValidator.endCDATA(null);
                            }
                            break;
                        case XMLStreamConstants.START_DOCUMENT:
                            ++fDepth;
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.startDocument((StartDocument) fCurrentEvent);
                            }
                            break;
                        case XMLStreamConstants.END_DOCUMENT:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.endDocument((EndDocument) fCurrentEvent);
                            }
                            break;
                        case XMLStreamConstants.PROCESSING_INSTRUCTION:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.processingInstruction((ProcessingInstruction) fCurrentEvent);
                            }
                            break;
                        case XMLStreamConstants.COMMENT:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.comment((Comment) fCurrentEvent);
                            }
                            break;
                        case XMLStreamConstants.ENTITY_REFERENCE:
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.entityReference((EntityReference) fCurrentEvent);
                            }
                            break;
                        case XMLStreamConstants.DTD:
                            DTD dtd = (DTD) fCurrentEvent;
                            processEntityDeclarations(dtd.getEntities());
                            if (fStAXValidatorHandler != null) {
                                fStAXValidatorHandler.doctypeDecl(dtd);
                            }
                            break;
                    }
                }
                fSchemaValidator.endDocument(null);
            }
        }

        /**
         * Fills in a QName object.
         */
        private void fillQName(QName toFill, mf.javax.xml.namespace.QName toCopy) {
            StAXValidatorHelper.this.fillQName(toFill, toCopy.getNamespaceURI(), toCopy.getLocalPart(), toCopy.getPrefix());
        }

        /**
         * Fills in the XMLAttributes object.
         */
        private void fillXMLAttributes(StartElement event) {
            fAttributes.removeAllAttributes();
            final Iterator attrs = event.getAttributes();
            while (attrs.hasNext()) {
                Attribute attr = (Attribute) attrs.next();
                fillQName(fAttributeQName, attr.getName());
                String type = attr.getDTDType();
                int idx = fAttributes.getLength();
                fAttributes.addAttributeNS(fAttributeQName,
                        (type != null) ? type : XMLSymbols.fCDATASymbol, attr.getValue());
                fAttributes.setSpecified(idx, attr.isSpecified());
            }
        }

        /**
         * Fills in the list of declared prefixes.
         */
        private void fillDeclaredPrefixes(StartElement event) {
            fillDeclaredPrefixes(event.getNamespaces());
        }

        /**
         * Fills in the list of declared prefixes.
         */
        private void fillDeclaredPrefixes(EndElement event) {
            fillDeclaredPrefixes(event.getNamespaces());
        }

        /**
         * Fills in the list of declared prefixes.
         */
        private void fillDeclaredPrefixes(Iterator namespaces) {
            fDeclaredPrefixes.clear();
            while (namespaces.hasNext()) {
                Namespace ns = (Namespace) namespaces.next();
                String prefix = ns.getPrefix();
                fDeclaredPrefixes.add(prefix != null ? prefix : "");
            }
        }

        /**
         * Send characters to the validator in CHUNK_SIZE character chunks.
         */
        private void sendCharactersToValidator(String str) {
            if (str != null) {
                final int length = str.length();
                final int remainder = length & CHUNK_MASK;
                if (remainder > 0) {
                    str.getChars(0, remainder, fCharBuffer, 0);
                    fTempString.setValues(fCharBuffer, 0, remainder);
                    fSchemaValidator.characters(fTempString, null);
                }
                int i = remainder;
                while (i < length) {
                    str.getChars(i, i += CHUNK_SIZE, fCharBuffer, 0);
                    fTempString.setValues(fCharBuffer, 0, CHUNK_SIZE);
                    fSchemaValidator.characters(fTempString, null);
                }
            }
        }
    }

} // StAXValidatorHelper

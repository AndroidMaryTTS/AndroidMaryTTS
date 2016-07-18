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

import java.util.Iterator;
import java.util.NoSuchElementException;

import mf.javax.xml.stream.XMLEventFactory;
import mf.javax.xml.stream.XMLEventWriter;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.XMLStreamReader;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.Comment;
import mf.javax.xml.stream.events.DTD;
import mf.javax.xml.stream.events.EndDocument;
import mf.javax.xml.stream.events.EntityReference;
import mf.javax.xml.stream.events.ProcessingInstruction;
import mf.javax.xml.stream.events.StartDocument;
import mf.javax.xml.stream.events.XMLEvent;
import mf.javax.xml.transform.stax.StAXResult;
import mf.org.apache.xerces.util.JAXPNamespaceContextWrapper;
import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.NamespaceContext;
import mf.org.apache.xerces.xni.QName;
import mf.org.apache.xerces.xni.XMLAttributes;
import mf.org.apache.xerces.xni.XMLLocator;
import mf.org.apache.xerces.xni.XMLResourceIdentifier;
import mf.org.apache.xerces.xni.XMLString;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLDocumentSource;


/**
 * <p>StAX event result builder.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: StAXEventResultBuilder.java 699763 2008-09-28 02:58:24Z mrglavas $
 */
final class StAXEventResultBuilder implements StAXDocumentHandler {

    //
    // Data
    //

    /**
     * An iterator for an empty collection.
     */
    private static final Iterator EMPTY_COLLECTION_ITERATOR = new Iterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };
    private final XMLEventFactory fEventFactory;
    private final StAXValidatorHelper fStAXValidatorHelper;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private final QName fAttrName = new QName();
    private XMLEventWriter fEventWriter;
    private boolean fIgnoreChars;
    private boolean fInCDATA;
    
    /*
     * StAXDocumentHandler methods
     */

    public StAXEventResultBuilder(StAXValidatorHelper helper, JAXPNamespaceContextWrapper context) {
        fStAXValidatorHelper = helper;
        fNamespaceContext = context;
        fEventFactory = XMLEventFactory.newInstance();
    }

    @Override
    public void setStAXResult(StAXResult result) {
        fIgnoreChars = false;
        fInCDATA = false;
        fEventWriter = (result != null) ? result.getXMLEventWriter() : null;
    }

    @Override
    public void startDocument(XMLStreamReader reader) throws XMLStreamException {
        String version = reader.getVersion();
        String encoding = reader.getCharacterEncodingScheme();
        boolean standalone = reader.standaloneSet();
        fEventWriter.add(fEventFactory.createStartDocument(encoding != null ? encoding : "UTF-8",
                version != null ? version : "1.0", standalone));
    }

    @Override
    public void endDocument(XMLStreamReader reader) throws XMLStreamException {
        fEventWriter.add(fEventFactory.createEndDocument());
        fEventWriter.flush();
    }

    @Override
    public void comment(XMLStreamReader reader) throws XMLStreamException {
        fEventWriter.add(fEventFactory.createComment(reader.getText()));
    }

    @Override
    public void processingInstruction(XMLStreamReader reader)
            throws XMLStreamException {
        String data = reader.getPIData();
        fEventWriter.add(fEventFactory.createProcessingInstruction(reader.getPITarget(),
                data != null ? data : ""));
    }

    @Override
    public void entityReference(XMLStreamReader reader)
            throws XMLStreamException {
        String name = reader.getLocalName();
        fEventWriter.add(fEventFactory.createEntityReference(name,
                fStAXValidatorHelper.getEntityDeclaration(name)));
    }

    @Override
    public void startDocument(StartDocument event) throws XMLStreamException {
        fEventWriter.add(event);
    }

    @Override
    public void endDocument(EndDocument event) throws XMLStreamException {
        fEventWriter.add(event);
        fEventWriter.flush();
    }

    @Override
    public void doctypeDecl(DTD event) throws XMLStreamException {
        fEventWriter.add(event);
    }

    @Override
    public void characters(Characters event) throws XMLStreamException {
        fEventWriter.add(event);
    }

    @Override
    public void cdata(Characters event) throws XMLStreamException {
        fEventWriter.add(event);
    }

    @Override
    public void comment(Comment event) throws XMLStreamException {
        fEventWriter.add(event);
    }

    @Override
    public void processingInstruction(ProcessingInstruction event)
            throws XMLStreamException {
        fEventWriter.add(event);
    }

    @Override
    public void entityReference(EntityReference event)
            throws XMLStreamException {
        fEventWriter.add(event);
    }
    
    /*
     * XMLDocumentHandler methods
     */

    @Override
    public void setIgnoringCharacters(boolean ignore) {
        fIgnoreChars = ignore;
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext namespaceContext, Augmentations augs)
            throws XNIException {
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone,
                        Augmentations augs) throws XNIException {
    }

    @Override
    public void doctypeDecl(String rootElement, String publicId,
                            String systemId, Augmentations augs) throws XNIException {
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
    }

    @Override
    public void processingInstruction(String target, XMLString data,
                                      Augmentations augs) throws XNIException {
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes,
                             Augmentations augs) throws XNIException {
        try {
            int length = attributes.getLength();
            if (length == 0) {
                /** Avoid creating a new StartElement event object (if possible). */
                XMLEvent start = fStAXValidatorHelper.getCurrentEvent();
                if (start != null) {
                    fEventWriter.add(start);
                    return;
                }
            }
            fEventWriter.add(fEventFactory.createStartElement(element.prefix,
                    element.uri != null ? element.uri : "", element.localpart,
                    getAttributeIterator(attributes, length), getNamespaceIterator(),
                    fNamespaceContext.getNamespaceContext()));
        } catch (XMLStreamException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes,
                             Augmentations augs) throws XNIException {
        startElement(element, attributes, augs);
        endElement(element, augs);
    }

    @Override
    public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier, String encoding,
                                   Augmentations augs) throws XNIException {
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs)
            throws XNIException {
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs)
            throws XNIException {
    }

    @Override
    public void characters(XMLString text, Augmentations augs)
            throws XNIException {
        if (!fIgnoreChars) {
            try {
                if (!fInCDATA) {
                    fEventWriter.add(fEventFactory.createCharacters(text.toString()));
                } else {
                    fEventWriter.add(fEventFactory.createCData(text.toString()));
                }
            } catch (XMLStreamException e) {
                throw new XNIException(e);
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs)
            throws XNIException {
        characters(text, augs);
    }

    @Override
    public void endElement(QName element, Augmentations augs)
            throws XNIException {
        try {
            /** Avoid creating a new EndElement event object (if possible). */
            XMLEvent end = fStAXValidatorHelper.getCurrentEvent();
            if (end != null) {
                fEventWriter.add(end);
            } else {
                fEventWriter.add(fEventFactory.createEndElement(element.prefix,
                        element.uri, element.localpart, getNamespaceIterator()));
            }
        } catch (XMLStreamException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        fInCDATA = true;
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        fInCDATA = false;
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    /*
     * Other methods.
     */

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
    }

    private Iterator getAttributeIterator(XMLAttributes attributes, int length) {
        return (length > 0) ? new AttributeIterator(attributes, length) : EMPTY_COLLECTION_ITERATOR;
    }

    private Iterator getNamespaceIterator() {
        int length = fNamespaceContext.getDeclaredPrefixCount();
        return (length > 0) ? new NamespaceIterator(length) : EMPTY_COLLECTION_ITERATOR;
    }

    /**
     * An iterator over XMLAttributes which returns Attribute event objects.
     */
    final class AttributeIterator implements Iterator {

        XMLAttributes fAttributes;
        int fIndex;
        int fEnd;

        AttributeIterator(XMLAttributes attributes, int length) {
            fAttributes = attributes;
            fIndex = 0;
            fEnd = length;
        }

        @Override
        public boolean hasNext() {
            if (fIndex < fEnd) {
                return true;
            }
            fAttributes = null;
            return false;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            fAttributes.getName(fIndex, fAttrName);
            return fEventFactory.createAttribute(fAttrName.prefix,
                    fAttrName.uri != null ? fAttrName.uri : "",
                    fAttrName.localpart, fAttributes.getValue(fIndex++));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * An iterator over the current context of a NamespaceContext
     * which returns Namespace event objects.
     */
    final class NamespaceIterator implements Iterator {

        mf.javax.xml.namespace.NamespaceContext fNC;
        int fIndex;
        int fEnd;

        NamespaceIterator(int length) {
            fNC = fNamespaceContext.getNamespaceContext();
            fIndex = 0;
            fEnd = length;
        }

        @Override
        public boolean hasNext() {
            if (fIndex < fEnd) {
                return true;
            }
            fNC = null;
            return false;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String prefix = fNamespaceContext.getDeclaredPrefixAt(fIndex++);
            String uri = fNC.getNamespaceURI(prefix);
            if (prefix.length() == 0) {
                return fEventFactory.createNamespace(uri != null ? uri : "");
            } else {
                return fEventFactory.createNamespace(prefix, uri != null ? uri : "");
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

} // StAXEventResultBuilder

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

import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.XMLStreamReader;
import mf.javax.xml.stream.XMLStreamWriter;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.Comment;
import mf.javax.xml.stream.events.DTD;
import mf.javax.xml.stream.events.EndDocument;
import mf.javax.xml.stream.events.EntityReference;
import mf.javax.xml.stream.events.ProcessingInstruction;
import mf.javax.xml.stream.events.StartDocument;
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
 * <p>StAX stream result builder.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: StAXStreamResultBuilder.java 699763 2008-09-28 02:58:24Z mrglavas $
 */
final class StAXStreamResultBuilder implements StAXDocumentHandler {

    //
    // Data
    //

    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private final QName fAttrName = new QName();
    private XMLStreamWriter fStreamWriter;
    private boolean fIgnoreChars;
    private boolean fInCDATA;

    public StAXStreamResultBuilder(JAXPNamespaceContextWrapper context) {
        fNamespaceContext = context;
    }
    
    /*
     * StAXDocumentHandler methods
     */

    @Override
    public void setStAXResult(StAXResult result) {
        fIgnoreChars = false;
        fInCDATA = false;
        fAttrName.clear();
        fStreamWriter = (result != null) ? result.getXMLStreamWriter() : null;
    }

    @Override
    public void startDocument(XMLStreamReader reader) throws XMLStreamException {
        String version = reader.getVersion();
        String encoding = reader.getCharacterEncodingScheme();
        fStreamWriter.writeStartDocument(encoding != null ? encoding : "UTF-8",
                version != null ? version : "1.0");
    }

    @Override
    public void endDocument(XMLStreamReader reader) throws XMLStreamException {
        fStreamWriter.writeEndDocument();
        fStreamWriter.flush();
    }

    @Override
    public void comment(XMLStreamReader reader) throws XMLStreamException {
        fStreamWriter.writeComment(reader.getText());
    }

    @Override
    public void processingInstruction(XMLStreamReader reader)
            throws XMLStreamException {
        String data = reader.getPIData();
        if (data != null && data.length() > 0) {
            fStreamWriter.writeProcessingInstruction(reader.getPITarget(), data);
        } else {
            fStreamWriter.writeProcessingInstruction(reader.getPITarget());
        }
    }

    @Override
    public void entityReference(XMLStreamReader reader) throws XMLStreamException {
        fStreamWriter.writeEntityRef(reader.getLocalName());
    }

    @Override
    public void startDocument(StartDocument event) throws XMLStreamException {
        String version = event.getVersion();
        String encoding = event.getCharacterEncodingScheme();
        fStreamWriter.writeStartDocument(encoding != null ? encoding : "UTF-8",
                version != null ? version : "1.0");
    }

    @Override
    public void endDocument(EndDocument event) throws XMLStreamException {
        fStreamWriter.writeEndDocument();
        fStreamWriter.flush();
    }

    @Override
    public void doctypeDecl(DTD event) throws XMLStreamException {
        fStreamWriter.writeDTD(event.getDocumentTypeDeclaration());
    }

    @Override
    public void characters(Characters event) throws XMLStreamException {
        fStreamWriter.writeCharacters(event.getData());
    }

    @Override
    public void cdata(Characters event) throws XMLStreamException {
        fStreamWriter.writeCData(event.getData());
    }

    @Override
    public void comment(Comment event) throws XMLStreamException {
        fStreamWriter.writeComment(event.getText());
    }

    @Override
    public void processingInstruction(ProcessingInstruction event)
            throws XMLStreamException {
        String data = event.getData();
        if (data != null && data.length() > 0) {
            fStreamWriter.writeProcessingInstruction(event.getTarget(), data);
        } else {
            fStreamWriter.writeProcessingInstruction(event.getTarget());
        }
    }

    @Override
    public void entityReference(EntityReference event) throws XMLStreamException {
        fStreamWriter.writeEntityRef(event.getName());

    }

    @Override
    public void setIgnoringCharacters(boolean ignore) {
        fIgnoreChars = ignore;
    }
    
    /*
     * XMLDocumentHandler methods
     */

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
            if (element.prefix.length() > 0) {
                fStreamWriter.writeStartElement(element.prefix,
                        element.localpart, element.uri != null ? element.uri : "");
            } else if (element.uri != null) {
                fStreamWriter.writeStartElement(element.uri, element.localpart);
            } else {
                fStreamWriter.writeStartElement(element.localpart);
            }
            int size = fNamespaceContext.getDeclaredPrefixCount();
            final mf.javax.xml.namespace.NamespaceContext nc = fNamespaceContext.getNamespaceContext();
            for (int i = 0; i < size; ++i) {
                String prefix = fNamespaceContext.getDeclaredPrefixAt(i);
                String uri = nc.getNamespaceURI(prefix);
                if (prefix.length() == 0) {
                    fStreamWriter.writeDefaultNamespace(uri != null ? uri : "");
                } else {
                    fStreamWriter.writeNamespace(prefix, uri != null ? uri : "");
                }
            }
            size = attributes.getLength();
            for (int i = 0; i < size; ++i) {
                attributes.getName(i, fAttrName);
                if (fAttrName.prefix.length() > 0) {
                    fStreamWriter.writeAttribute(fAttrName.prefix,
                            fAttrName.uri != null ? fAttrName.uri : "",
                            fAttrName.localpart, attributes.getValue(i));
                } else if (fAttrName.uri != null) {
                    fStreamWriter.writeAttribute(fAttrName.uri,
                            fAttrName.localpart, attributes.getValue(i));
                } else {
                    fStreamWriter.writeAttribute(fAttrName.localpart, attributes.getValue(i));
                }
            }
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
                    fStreamWriter.writeCharacters(text.ch, text.offset, text.length);
                } else {
                    fStreamWriter.writeCData(text.toString());
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
            fStreamWriter.writeEndElement();
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

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
    }

} // StAXStreamResultBuilder

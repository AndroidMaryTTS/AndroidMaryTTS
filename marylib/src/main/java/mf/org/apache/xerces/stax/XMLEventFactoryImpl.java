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

package mf.org.apache.xerces.stax;

import java.util.Iterator;

import mf.javax.xml.XMLConstants;
import mf.javax.xml.namespace.NamespaceContext;
import mf.javax.xml.namespace.QName;
import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLEventFactory;
import mf.javax.xml.stream.XMLStreamConstants;
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
import mf.org.apache.xerces.stax.events.AttributeImpl;
import mf.org.apache.xerces.stax.events.CharactersImpl;
import mf.org.apache.xerces.stax.events.CommentImpl;
import mf.org.apache.xerces.stax.events.DTDImpl;
import mf.org.apache.xerces.stax.events.EndDocumentImpl;
import mf.org.apache.xerces.stax.events.EndElementImpl;
import mf.org.apache.xerces.stax.events.EntityReferenceImpl;
import mf.org.apache.xerces.stax.events.NamespaceImpl;
import mf.org.apache.xerces.stax.events.ProcessingInstructionImpl;
import mf.org.apache.xerces.stax.events.StartDocumentImpl;
import mf.org.apache.xerces.stax.events.StartElementImpl;

/**
 * <p>Implementation of XMLEventFactory.</p>
 *
 * @version $Id: XMLEventFactoryImpl.java 730796 2009-01-02 17:36:23Z mrglavas $
 * @xerces.internal
 */
public final class XMLEventFactoryImpl extends XMLEventFactory {

    private Location fLocation;

    public XMLEventFactoryImpl() {
    }

    @Override
    public void setLocation(Location location) {
        fLocation = location;
    }

    @Override
    public Attribute createAttribute(String prefix, String namespaceURI,
                                     String localName, String value) {
        return createAttribute(new QName(namespaceURI, localName, prefix), value);
    }

    @Override
    public Attribute createAttribute(String localName, String value) {
        return createAttribute(new QName(localName), value);
    }

    @Override
    public Attribute createAttribute(QName name, String value) {
        return new AttributeImpl(name, value, "CDATA", true, fLocation);
    }

    @Override
    public Namespace createNamespace(String namespaceURI) {
        return createNamespace(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
    }

    @Override
    public Namespace createNamespace(String prefix, String namespaceUri) {
        return new NamespaceImpl(prefix, namespaceUri, fLocation);
    }

    @Override
    public StartElement createStartElement(QName name, Iterator attributes,
                                           Iterator namespaces) {
        return createStartElement(name, attributes, namespaces, null);
    }

    @Override
    public StartElement createStartElement(String prefix, String namespaceUri,
                                           String localName) {
        return createStartElement(new QName(namespaceUri, localName, prefix), null, null);
    }

    @Override
    public StartElement createStartElement(String prefix, String namespaceUri,
                                           String localName, Iterator attributes, Iterator namespaces) {
        return createStartElement(new QName(namespaceUri, localName, prefix), attributes, namespaces);
    }

    @Override
    public StartElement createStartElement(String prefix, String namespaceUri,
                                           String localName, Iterator attributes, Iterator namespaces,
                                           NamespaceContext context) {
        return createStartElement(new QName(namespaceUri, localName, prefix), attributes, namespaces, context);
    }

    private StartElement createStartElement(QName name, Iterator attributes,
                                            Iterator namespaces, NamespaceContext context) {
        return new StartElementImpl(name, attributes, namespaces, context, fLocation);
    }

    @Override
    public EndElement createEndElement(QName name, Iterator namespaces) {
        return new EndElementImpl(name, namespaces, fLocation);
    }

    @Override
    public EndElement createEndElement(String prefix, String namespaceUri,
                                       String localName) {
        return createEndElement(new QName(namespaceUri, localName, prefix), null);
    }

    @Override
    public EndElement createEndElement(String prefix, String namespaceUri,
                                       String localName, Iterator namespaces) {
        return createEndElement(new QName(namespaceUri, localName, prefix), namespaces);
    }

    @Override
    public Characters createCharacters(String content) {
        return new CharactersImpl(content, XMLStreamConstants.CHARACTERS, fLocation);
    }

    @Override
    public Characters createCData(String content) {
        return new CharactersImpl(content, XMLStreamConstants.CDATA, fLocation);
    }

    @Override
    public Characters createSpace(String content) {
        return createCharacters(content);
    }

    @Override
    public Characters createIgnorableSpace(String content) {
        return new CharactersImpl(content, XMLStreamConstants.SPACE, fLocation);
    }

    @Override
    public StartDocument createStartDocument() {
        return createStartDocument(null, null);
    }

    @Override
    public StartDocument createStartDocument(String encoding, String version,
                                             boolean standalone) {
        return new StartDocumentImpl(encoding, encoding != null, standalone, true, version, fLocation);
    }

    @Override
    public StartDocument createStartDocument(String encoding, String version) {
        return new StartDocumentImpl(encoding, encoding != null, false, false, version, fLocation);
    }

    @Override
    public StartDocument createStartDocument(String encoding) {
        return createStartDocument(encoding, null);
    }

    @Override
    public EndDocument createEndDocument() {
        return new EndDocumentImpl(fLocation);
    }

    @Override
    public EntityReference createEntityReference(String name,
                                                 EntityDeclaration declaration) {
        return new EntityReferenceImpl(name, declaration, fLocation);
    }

    @Override
    public Comment createComment(String text) {
        return new CommentImpl(text, fLocation);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target,
                                                             String data) {
        return new ProcessingInstructionImpl(target, data, fLocation);
    }

    @Override
    public DTD createDTD(String dtd) {
        return new DTDImpl(dtd, fLocation);
    }
}

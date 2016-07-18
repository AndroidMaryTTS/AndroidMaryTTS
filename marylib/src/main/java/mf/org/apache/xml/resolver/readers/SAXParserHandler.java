// SAXParserHandler.java - An entity-resolving DefaultHandler

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

package mf.org.apache.xml.resolver.readers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

/**
 * An entity-resolving DefaultHandler.
 * <p/>
 * <p>This class provides a SAXParser DefaultHandler that performs
 * entity resolution.
 * </p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 */
public class SAXParserHandler extends DefaultHandler {
    private EntityResolver er = null;
    private ContentHandler ch = null;

    public SAXParserHandler() {
        super();
    }

    public void setEntityResolver(EntityResolver er) {
        this.er = er;
    }

    public void setContentHandler(ContentHandler ch) {
        this.ch = ch;
    }

    // Entity Resolver
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {

        if (er != null) {
            try {
                return er.resolveEntity(publicId, systemId);
            } catch (IOException e) {
                System.out.println("resolveEntity threw IOException!");
                return null;
            }
        } else {
            return null;
        }
    }

    // Content Handler
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (this.ch != null) {
            this.ch.characters(ch, start, length);
        }
    }

    @Override
    public void endDocument()
            throws SAXException {
        if (ch != null) {
            ch.endDocument();
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (ch != null) {
            ch.endElement(namespaceURI, localName, qName);
        }
    }

    @Override
    public void endPrefixMapping(String prefix)
            throws SAXException {
        if (ch != null) {
            ch.endPrefixMapping(prefix);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (this.ch != null) {
            this.ch.ignorableWhitespace(ch, start, length);
        }
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        if (ch != null) {
            ch.processingInstruction(target, data);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        if (ch != null) {
            ch.setDocumentLocator(locator);
        }
    }

    @Override
    public void skippedEntity(String name)
            throws SAXException {
        if (ch != null) {
            ch.skippedEntity(name);
        }
    }

    @Override
    public void startDocument()
            throws SAXException {
        if (ch != null) {
            ch.startDocument();
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)
            throws SAXException {
        if (ch != null) {
            ch.startElement(namespaceURI, localName, qName, atts);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (ch != null) {
            ch.startPrefixMapping(prefix, uri);
        }
    }
}

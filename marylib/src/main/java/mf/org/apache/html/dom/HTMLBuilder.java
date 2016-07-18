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
package mf.org.apache.html.dom;


import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.Vector;

import mf.org.apache.xerces.dom.ElementImpl;
import mf.org.apache.xerces.dom.ProcessingInstructionImpl;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.html.HTMLDocument;


/**
 * This is a SAX document handler that is used to build an HTML document.
 * It can build a document from any SAX parser, but is specifically tuned
 * for working with the OpenXML HTML parser.
 *
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 */
public class HTMLBuilder
        implements DocumentHandler {


    /**
     * The document that is being built.
     */
    protected HTMLDocumentImpl _document;


    /**
     * The current node in the document into which elements, text and
     * other nodes will be inserted. This starts as the document itself
     * and reflects each element that is currently being parsed.
     */
    protected ElementImpl _current;
    /**
     * The document is only created the same time as the document element, however, certain
     * nodes may precede the document element (comment and PI), and they are accumulated
     * in this vector.
     */
    protected Vector _preRootNodes;
    /**
     * Applies only to whitespace appearing between element tags in element content,
     * as per the SAX definition, and true by default.
     */
    private boolean _ignoreWhitespace = true;
    /**
     * Indicates whether finished building a document. If so, can start building
     * another document. Must be initially true to get the first document processed.
     */
    private boolean _done = true;

    @Override
    public void startDocument()
            throws SAXException {
        if (!_done)
            throw new SAXException("HTM001 State error: startDocument fired twice on one builder.");
        _document = null;
        _done = false;
    }


    @Override
    public void endDocument()
            throws SAXException {
        if (_document == null)
            throw new SAXException("HTM002 State error: document never started or missing document element.");
        if (_current != null)
            throw new SAXException("HTM003 State error: document ended before end of document element.");
        _current = null;
        _done = true;
    }


    @Override
    public synchronized void startElement(String tagName, AttributeList attrList)
            throws SAXException {
        ElementImpl elem;
        int i;

        if (tagName == null)
            throw new SAXException("HTM004 Argument 'tagName' is null.");

        // If this is the root element, this is the time to create a new document,
        // because only know we know the document element name and namespace URI.
        if (_document == null) {
            // No need to create the element explicitly.
            _document = new HTMLDocumentImpl();
            elem = (ElementImpl) _document.getDocumentElement();
            _current = elem;
            if (_current == null)
                throw new SAXException("HTM005 State error: Document.getDocumentElement returns null.");

            // Insert nodes (comment and PI) that appear before the root element.
            if (_preRootNodes != null) {
                for (i = _preRootNodes.size(); i-- > 0; )
                    _document.insertBefore((Node) _preRootNodes.elementAt(i), elem);
                _preRootNodes = null;
            }

        } else {
            // This is a state error, indicates that document has been parsed in full,
            // or that there are two root elements.
            if (_current == null)
                throw new SAXException("HTM006 State error: startElement called after end of document element.");
            elem = (ElementImpl) _document.createElement(tagName);
            _current.appendChild(elem);
            _current = elem;
        }

        // Add the attributes (specified and not-specified) to this element.
        if (attrList != null) {
            for (i = 0; i < attrList.getLength(); ++i)
                elem.setAttribute(attrList.getName(i), attrList.getValue(i));
        }
    }


    @Override
    public void endElement(String tagName)
            throws SAXException {
        if (_current == null)
            throw new SAXException("HTM007 State error: endElement called with no current node.");
        if (!_current.getNodeName().equalsIgnoreCase(tagName))
            throw new SAXException("HTM008 State error: mismatch in closing tag name " + tagName + "\n" + tagName);

        // Move up to the parent element. When you reach the top (closing the root element).
        // the parent is document and current is null.
        if (_current.getParentNode() == _current.getOwnerDocument())
            _current = null;
        else
            _current = (ElementImpl) _current.getParentNode();
    }


    public void characters(String text)
            throws SAXException {
        if (_current == null)
            throw new SAXException("HTM009 State error: character data found outside of root element.");
        _current.appendChild(_document.createTextNode(text));
    }


    @Override
    public void characters(char[] text, int start, int length)
            throws SAXException {
        if (_current == null)
            throw new SAXException("HTM010 State error: character data found outside of root element.");
        _current.appendChild(_document.createTextNode(new String(text, start, length)));
    }


    @Override
    public void ignorableWhitespace(char[] text, int start, int length)
            throws SAXException {
        if (!_ignoreWhitespace)
            _current.appendChild(_document.createTextNode(new String(text, start, length)));
    }


    @Override
    public void processingInstruction(String target, String instruction)
            throws SAXException {
        // Processing instruction may appear before the document element (in fact, before the
        // document has been created, or after the document element has been closed.
        if (_current == null && _document == null) {
            if (_preRootNodes == null)
                _preRootNodes = new Vector();
            _preRootNodes.addElement(new ProcessingInstructionImpl(null, target, instruction));
        } else if (_current == null && _document != null)
            _document.appendChild(_document.createProcessingInstruction(target, instruction));
        else
            _current.appendChild(_document.createProcessingInstruction(target, instruction));
    }


    public HTMLDocument getHTMLDocument() {
        return _document;
    }


    @Override
    public void setDocumentLocator(Locator locator) {
        // ignored
    }


}

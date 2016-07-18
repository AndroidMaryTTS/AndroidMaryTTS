/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package mf.javax.xml.stream.util;

import mf.javax.xml.namespace.NamespaceContext;
import mf.javax.xml.namespace.QName;
import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.XMLStreamReader;

/**
 * This is the base class for deriving an XMLStreamReader filter
 * <p/>
 * This class is designed to sit between an XMLStreamReader and an
 * application's XMLStreamReader.   By default each method
 * does nothing but call the corresponding method on the
 * parent interface.
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @see javax.xml.stream.XMLStreamReader
 * @see EventReaderDelegate
 * @since 1.6
 */

public class StreamReaderDelegate implements XMLStreamReader {
    private XMLStreamReader reader;

    /**
     * Construct an empty filter with no parent.
     */
    public StreamReaderDelegate() {
    }

    /**
     * Construct an filter with the specified parent.
     *
     * @param reader the parent
     */
    public StreamReaderDelegate(XMLStreamReader reader) {
        this.reader = reader;
    }

    /**
     * Get the parent of this instance.
     *
     * @return the parent or null if none is set
     */
    public XMLStreamReader getParent() {
        return reader;
    }

    /**
     * Set the parent of this instance.
     *
     * @param reader the new parent
     */
    public void setParent(XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public int next()
            throws XMLStreamException {
        return reader.next();
    }

    @Override
    public int nextTag()
            throws XMLStreamException {
        return reader.nextTag();
    }

    @Override
    public String getElementText()
            throws XMLStreamException {
        return reader.getElementText();
    }

    @Override
    public void require(int type, String namespaceURI, String localName)
            throws XMLStreamException {
        reader.require(type, namespaceURI, localName);
    }

    @Override
    public boolean hasNext()
            throws XMLStreamException {
        return reader.hasNext();
    }

    @Override
    public void close()
            throws XMLStreamException {
        reader.close();
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return reader.getNamespaceURI(prefix);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return reader.getNamespaceContext();
    }

    @Override
    public boolean isStartElement() {
        return reader.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return reader.isEndElement();
    }

    @Override
    public boolean isCharacters() {
        return reader.isCharacters();
    }

    @Override
    public boolean isWhiteSpace() {
        return reader.isWhiteSpace();
    }

    @Override
    public String getAttributeValue(String namespaceUri,
                                    String localName) {
        return reader.getAttributeValue(namespaceUri, localName);
    }

    @Override
    public int getAttributeCount() {
        return reader.getAttributeCount();
    }

    @Override
    public QName getAttributeName(int index) {
        return reader.getAttributeName(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return reader.getAttributePrefix(index);
    }

    @Override
    public String getAttributeNamespace(int index) {
        return reader.getAttributeNamespace(index);
    }

    @Override
    public String getAttributeLocalName(int index) {
        return reader.getAttributeLocalName(index);
    }

    @Override
    public String getAttributeType(int index) {
        return reader.getAttributeType(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return reader.getAttributeValue(index);
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        return reader.isAttributeSpecified(index);
    }

    @Override
    public int getNamespaceCount() {
        return reader.getNamespaceCount();
    }

    @Override
    public String getNamespacePrefix(int index) {
        return reader.getNamespacePrefix(index);
    }

    @Override
    public String getNamespaceURI(int index) {
        return reader.getNamespaceURI(index);
    }

    @Override
    public int getEventType() {
        return reader.getEventType();
    }

    @Override
    public String getText() {
        return reader.getText();
    }

    @Override
    public int getTextCharacters(int sourceStart,
                                 char[] target,
                                 int targetStart,
                                 int length)
            throws XMLStreamException {
        return reader.getTextCharacters(sourceStart,
                target,
                targetStart,
                length);
    }


    @Override
    public char[] getTextCharacters() {
        return reader.getTextCharacters();
    }

    @Override
    public int getTextStart() {
        return reader.getTextStart();
    }

    @Override
    public int getTextLength() {
        return reader.getTextLength();
    }

    @Override
    public String getEncoding() {
        return reader.getEncoding();
    }

    @Override
    public boolean hasText() {
        return reader.hasText();
    }

    @Override
    public Location getLocation() {
        return reader.getLocation();
    }

    @Override
    public QName getName() {
        return reader.getName();
    }

    @Override
    public String getLocalName() {
        return reader.getLocalName();
    }

    @Override
    public boolean hasName() {
        return reader.hasName();
    }

    @Override
    public String getNamespaceURI() {
        return reader.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return reader.getPrefix();
    }

    @Override
    public String getVersion() {
        return reader.getVersion();
    }

    @Override
    public boolean isStandalone() {
        return reader.isStandalone();
    }

    @Override
    public boolean standaloneSet() {
        return reader.standaloneSet();
    }

    @Override
    public String getCharacterEncodingScheme() {
        return reader.getCharacterEncodingScheme();
    }

    @Override
    public String getPITarget() {
        return reader.getPITarget();
    }

    @Override
    public String getPIData() {
        return reader.getPIData();
    }

    @Override
    public Object getProperty(String name) {
        return reader.getProperty(name);
    }
}

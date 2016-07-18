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

package mf.org.apache.xerces.impl.xs.opti;

import mf.org.w3c.dom.Attr;
import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.NodeList;
import mf.org.w3c.dom.TypeInfo;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @version $Id: DefaultElement.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @xerces.internal
 */
public class DefaultElement extends NodeImpl
        implements Element {

    // default constructor
    public DefaultElement() {
    }


    public DefaultElement(String prefix, String localpart, String rawname, String uri, short nodeType) {
        super(prefix, localpart, rawname, uri, nodeType);
    }


    //
    // org.w3c.dom.Element methods
    //

    // getter methods
    @Override
    public String getTagName() {
        return null;
    }


    @Override
    public String getAttribute(String name) {
        return null;
    }


    @Override
    public Attr getAttributeNode(String name) {
        return null;
    }


    @Override
    public NodeList getElementsByTagName(String name) {
        return null;
    }


    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        return null;
    }


    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return null;
    }


    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return null;
    }


    @Override
    public boolean hasAttribute(String name) {
        return false;
    }


    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return false;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }


    // setter methods
    @Override
    public void setAttribute(String name, String value) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public void removeAttribute(String name) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    @Override
    public void setIdAttributeNode(Attr at, boolean makeId) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    @Override
    public void setIdAttribute(String name, boolean makeId) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName,
                                 boolean makeId) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

}

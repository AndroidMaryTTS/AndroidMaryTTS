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
import mf.org.w3c.dom.NamedNodeMap;
import mf.org.w3c.dom.Node;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @version $Id: NamedNodeMapImpl.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @xerces.internal
 */
public class NamedNodeMapImpl implements NamedNodeMap {

    Attr[] attrs;

    public NamedNodeMapImpl(Attr[] attrs) {
        this.attrs = attrs;
    }

    @Override
    public Node getNamedItem(String name) {
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i].getName().equals(name)) {
                return attrs[i];
            }
        }
        return null;
    }

    @Override
    public Node item(int index) {
        if (index < 0 && index > getLength()) {
            return null;
        }
        return attrs[index];
    }

    @Override
    public int getLength() {
        return attrs.length;
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) {
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i].getName().equals(localName) && attrs[i].getNamespaceURI().equals(namespaceURI)) {
                return attrs[i];
            }
        }
        return null;
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
}
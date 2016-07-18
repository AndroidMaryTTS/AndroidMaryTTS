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

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import mf.javax.xml.XMLConstants;
import mf.javax.xml.namespace.NamespaceContext;

/**
 * <p>A <code>NamespaceContext</code> which only
 * contains bindings for the xml and xmlns prefixes.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: DefaultNamespaceContext.java 730672 2009-01-02 06:15:08Z mrglavas $
 * @xerces.internal
 */
public final class DefaultNamespaceContext implements NamespaceContext {

    /**
     * Singleton instance.
     */
    private static final DefaultNamespaceContext DEFAULT_NAMESPACE_CONTEXT_INSTANCE
            = new DefaultNamespaceContext();

    private DefaultNamespaceContext() {
    }

    /**
     * Returns the one and only instance of this class.
     */
    public static DefaultNamespaceContext getInstance() {
        return DEFAULT_NAMESPACE_CONTEXT_INSTANCE;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        } else if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            return XMLConstants.XML_NS_URI;
        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        return XMLConstants.NULL_NS_URI;
    } // getNamespaceURI(String)

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Namespace URI cannot be null.");
        } else if (XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        return null;
    } // getPrefix(String)

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Namespace URI cannot be null.");
        } else if (XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            return new Iterator() {
                boolean more = true;

                @Override
                public boolean hasNext() {
                    return more;
                }

                @Override
                public Object next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    more = false;
                    return XMLConstants.XML_NS_PREFIX;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            return new Iterator() {
                boolean more = true;

                @Override
                public boolean hasNext() {
                    return more;
                }

                @Override
                public Object next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    more = false;
                    return XMLConstants.XMLNS_ATTRIBUTE;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return Collections.EMPTY_LIST.iterator();
    } // getPrefixes(String)
}

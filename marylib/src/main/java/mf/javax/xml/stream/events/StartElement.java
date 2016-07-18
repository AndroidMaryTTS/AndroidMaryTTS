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

package mf.javax.xml.stream.events;

import java.util.Iterator;

import mf.javax.xml.namespace.NamespaceContext;
import mf.javax.xml.namespace.QName;

/**
 * The StartElement interface provides access to information about
 * start elements.  A StartElement is reported for each Start Tag
 * in the document.
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @since 1.6
 */
public interface StartElement extends XMLEvent {

    /**
     * Get the name of this event
     *
     * @return the qualified name of this event
     */
    QName getName();

    /**
     * Returns an Iterator of non-namespace declared attributes declared on
     * this START_ELEMENT,
     * returns an empty iterator if there are no attributes.  The
     * iterator must contain only implementations of the javax.xml.stream.Attribute
     * interface.   Attributes are fundamentally unordered and may not be reported
     * in any order.
     *
     * @return a readonly Iterator over Attribute interfaces, or an
     * empty iterator
     */
    Iterator getAttributes();

    /**
     * Returns an Iterator of namespaces declared on this element.
     * This Iterator does not contain previously declared namespaces
     * unless they appear on the current START_ELEMENT.
     * Therefore this list may contain redeclared namespaces and duplicate namespace
     * declarations. Use the getNamespaceContext() method to get the
     * current context of namespace declarations.
     * <p/>
     * <p>The iterator must contain only implementations of the
     * javax.xml.stream.Namespace interface.
     * <p/>
     * <p>A Namespace isA Attribute.  One
     * can iterate over a list of namespaces as a list of attributes.
     * However this method returns only the list of namespaces
     * declared on this START_ELEMENT and does not
     * include the attributes declared on this START_ELEMENT.
     * <p/>
     * Returns an empty iterator if there are no namespaces.
     *
     * @return a readonly Iterator over Namespace interfaces, or an
     * empty iterator
     */
    Iterator getNamespaces();

    /**
     * Returns the attribute referred to by this name
     *
     * @param name the qname of the desired name
     * @return the attribute corresponding to the name value or null
     */
    Attribute getAttributeByName(QName name);

    /**
     * Gets a read-only namespace context. If no context is
     * available this method will return an empty namespace context.
     * The NamespaceContext contains information about all namespaces
     * in scope for this StartElement.
     *
     * @return the current namespace context
     */
    NamespaceContext getNamespaceContext();

    /**
     * Gets the value that the prefix is bound to in the
     * context of this element.  Returns null if
     * the prefix is not bound in this context
     *
     * @param prefix the prefix to lookup
     * @return the uri bound to the prefix or null
     */
    String getNamespaceURI(String prefix);
}

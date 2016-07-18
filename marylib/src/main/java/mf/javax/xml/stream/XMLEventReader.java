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

package mf.javax.xml.stream;

import java.util.Iterator;

import mf.javax.xml.stream.events.XMLEvent;

/**
 * This is the top level interface for parsing XML Events.  It provides
 * the ability to peek at the next event and returns configuration
 * information through the property interface.
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @see XMLInputFactory
 * @see XMLEventWriter
 * @since 1.6
 */
public interface XMLEventReader extends Iterator {
    /**
     * Get the next XMLEvent
     *
     * @throws XMLStreamException     if there is an error with the underlying XML.
     * @throws NoSuchElementException iteration has no more elements.
     * @see XMLEvent
     */
    XMLEvent nextEvent() throws XMLStreamException;

    /**
     * Check if there are more events.
     * Returns true if there are more events and false otherwise.
     *
     * @return true if the event reader has more events, false otherwise
     */
    @Override
    boolean hasNext();

    /**
     * Check the next XMLEvent without reading it from the stream.
     * Returns null if the stream is at EOF or has no more XMLEvents.
     * A call to peek() will be equal to the next return of next().
     *
     * @throws XMLStreamException
     * @see XMLEvent
     */
    XMLEvent peek() throws XMLStreamException;

    /**
     * Reads the content of a text-only element. Precondition:
     * the current event is START_ELEMENT. Postcondition:
     * The current event is the corresponding END_ELEMENT.
     *
     * @throws XMLStreamException if the current event is not a START_ELEMENT
     *                            or if a non text element is encountered
     */
    String getElementText() throws XMLStreamException;

    /**
     * Skips any insignificant space events until a START_ELEMENT or
     * END_ELEMENT is reached. If anything other than space characters are
     * encountered, an exception is thrown. This method should
     * be used when processing element-only content because
     * the parser is not able to recognize ignorable whitespace if
     * the DTD is missing or not interpreted.
     *
     * @throws XMLStreamException if anything other than space characters are encountered
     */
    XMLEvent nextTag() throws XMLStreamException;

    /**
     * Get the value of a feature/property from the underlying implementation
     *
     * @param name The name of the property
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException;

    /**
     * Frees any resources associated with this Reader.  This method does not close the
     * underlying input source.
     *
     * @throws XMLStreamException if there are errors freeing associated resources
     */
    void close() throws XMLStreamException;
}

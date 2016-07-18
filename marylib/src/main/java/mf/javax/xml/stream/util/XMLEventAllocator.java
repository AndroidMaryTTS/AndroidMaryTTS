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

import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.XMLStreamReader;
import mf.javax.xml.stream.events.XMLEvent;

/**
 * This interface defines a class that allows a user to register
 * a way to allocate events given an XMLStreamReader.  An implementation
 * is not required to use the XMLEventFactory implementation but this
 * is recommended.  The XMLEventAllocator can be set on an XMLInputFactory
 * using the property "javax.xml.stream.allocator"
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @see javax.xml.stream.XMLInputFactory
 * @see javax.xml.stream.XMLEventFactory
 * @since 1.6
 */
public interface XMLEventAllocator {

    /**
     * This method creates an instance of the XMLEventAllocator. This
     * allows the XMLInputFactory to allocate a new instance per reader.
     */
    XMLEventAllocator newInstance();

    /**
     * This method allocates an event given the current
     * state of the XMLStreamReader.  If this XMLEventAllocator
     * does not have a one-to-one mapping between reader states
     * and events this method will return null.  This method
     * must not modify the state of the XMLStreamReader.
     *
     * @param reader The XMLStreamReader to allocate from
     * @return the event corresponding to the current reader state
     */
    XMLEvent allocate(XMLStreamReader reader)
            throws XMLStreamException;

    /**
     * This method allocates an event or set of events
     * given the current
     * state of the XMLStreamReader and adds the event
     * or set of events to the
     * consumer that was passed in.  This method can be used
     * to expand or contract reader states into event states.
     * This method may modify the state of the XMLStreamReader.
     *
     * @param reader   The XMLStreamReader to allocate from
     * @param consumer The XMLEventConsumer to add to.
     */
    void allocate(XMLStreamReader reader, XMLEventConsumer consumer)
            throws XMLStreamException;

}

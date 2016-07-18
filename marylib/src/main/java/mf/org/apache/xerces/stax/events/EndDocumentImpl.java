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

package mf.org.apache.xerces.stax.events;

import java.io.Writer;

import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.events.EndDocument;

/**
 * @author Lucian Holland
 * @version $Id: EndDocumentImpl.java 730471 2008-12-31 20:45:32Z mrglavas $
 * @xerces.internal
 */
public final class EndDocumentImpl extends XMLEventImpl implements EndDocument {

    /**
     * Constructor.
     *
     * @param location Location object for this event.
     */
    public EndDocumentImpl(Location location) {
        super(END_DOCUMENT, location);
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }
}

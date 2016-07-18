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

import java.io.StringWriter;

import mf.javax.xml.namespace.QName;
import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.EndElement;
import mf.javax.xml.stream.events.StartElement;
import mf.javax.xml.stream.events.XMLEvent;
import mf.org.apache.xerces.stax.EmptyLocation;
import mf.org.apache.xerces.stax.ImmutableLocation;

/**
 * @author Lucian Holland
 * @version $Id: XMLEventImpl.java 956312 2010-06-20 00:47:28Z mrglavas $
 * @xerces.internal
 */
abstract class XMLEventImpl implements XMLEvent {

    /**
     * Constant representing the type of this event.
     * {@see javax.xml.stream.XMLStreamConstants}
     */
    private int fEventType;

    /**
     * Location object for this event.
     */
    private Location fLocation;

    /**
     * Constructor.
     */
    XMLEventImpl(final int eventType, final Location location) {
        fEventType = eventType;
        if (location != null) {
            fLocation = new ImmutableLocation(location);
        } else {
            fLocation = EmptyLocation.getInstance();
        }
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#getEventType()
     */
    @Override
    public final int getEventType() {
        return fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#getLocation()
     */
    @Override
    public final Location getLocation() {
        return fLocation;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isStartElement()
     */
    @Override
    public final boolean isStartElement() {
        return START_ELEMENT == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isAttribute()
     */
    @Override
    public final boolean isAttribute() {
        return ATTRIBUTE == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isNamespace()
     */
    @Override
    public final boolean isNamespace() {
        return NAMESPACE == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isEndElement()
     */
    @Override
    public final boolean isEndElement() {
        return END_ELEMENT == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isEntityReference()
     */
    @Override
    public final boolean isEntityReference() {
        return ENTITY_REFERENCE == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isProcessingInstruction()
     */
    @Override
    public final boolean isProcessingInstruction() {
        return PROCESSING_INSTRUCTION == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isCharacters()
     */
    @Override
    public final boolean isCharacters() {
        return CHARACTERS == fEventType ||
                CDATA == fEventType ||
                SPACE == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isStartDocument()
     */
    @Override
    public final boolean isStartDocument() {
        return START_DOCUMENT == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#isEndDocument()
     */
    @Override
    public final boolean isEndDocument() {
        return END_DOCUMENT == fEventType;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#asStartElement()
     */
    @Override
    public final StartElement asStartElement() {
        return (StartElement) this;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#asEndElement()
     */
    @Override
    public final EndElement asEndElement() {
        return (EndElement) this;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#asCharacters()
     */
    @Override
    public final Characters asCharacters() {
        return (Characters) this;
    }

    /**
     * @see javax.xml.stream.events.XMLEvent#getSchemaType()
     */
    @Override
    public final QName getSchemaType() {
        return null;
    }

    @Override
    public final String toString() {
        final StringWriter writer = new StringWriter();
        try {
            writeAsEncodedUnicode(writer);
        } catch (XMLStreamException xse) {
        }
        return writer.toString();
    }
}

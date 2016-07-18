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

import java.io.Writer;

import mf.javax.xml.namespace.QName;

/**
 * This is the base event interface for handling markup events.
 * Events are value objects that are used to communicate the
 * XML 1.0 InfoSet to the Application.  Events may be cached
 * and referenced after the parse has completed.
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @see javax.xml.stream.XMLEventReader
 * @see Characters
 * @see ProcessingInstruction
 * @see StartElement
 * @see EndElement
 * @see StartDocument
 * @see EndDocument
 * @see EntityReference
 * @see EntityDeclaration
 * @see NotationDeclaration
 * @since 1.6
 */
public interface XMLEvent extends mf.javax.xml.stream.XMLStreamConstants {

    /**
     * Returns an integer code for this event.
     *
     * @see #START_ELEMENT
     * @see #END_ELEMENT
     * @see #CHARACTERS
     * @see #ATTRIBUTE
     * @see #NAMESPACE
     * @see #PROCESSING_INSTRUCTION
     * @see #COMMENT
     * @see #START_DOCUMENT
     * @see #END_DOCUMENT
     * @see #DTD
     */
    int getEventType();

    /**
     * Return the location of this event.  The Location
     * returned from this method is non-volatile and
     * will retain its information.
     *
     * @see javax.xml.stream.Location
     */
    mf.javax.xml.stream.Location getLocation();

    /**
     * A utility function to check if this event is a StartElement.
     *
     * @see StartElement
     */
    boolean isStartElement();

    /**
     * A utility function to check if this event is an Attribute.
     *
     * @see Attribute
     */
    boolean isAttribute();

    /**
     * A utility function to check if this event is a Namespace.
     *
     * @see Namespace
     */
    boolean isNamespace();


    /**
     * A utility function to check if this event is a EndElement.
     *
     * @see EndElement
     */
    boolean isEndElement();

    /**
     * A utility function to check if this event is an EntityReference.
     *
     * @see EntityReference
     */
    boolean isEntityReference();

    /**
     * A utility function to check if this event is a ProcessingInstruction.
     *
     * @see ProcessingInstruction
     */
    boolean isProcessingInstruction();

    /**
     * A utility function to check if this event is Characters.
     *
     * @see Characters
     */
    boolean isCharacters();

    /**
     * A utility function to check if this event is a StartDocument.
     *
     * @see StartDocument
     */
    boolean isStartDocument();

    /**
     * A utility function to check if this event is an EndDocument.
     *
     * @see EndDocument
     */
    boolean isEndDocument();

    /**
     * Returns this event as a start element event, may result in
     * a class cast exception if this event is not a start element.
     */
    StartElement asStartElement();

    /**
     * Returns this event as an end  element event, may result in
     * a class cast exception if this event is not a end element.
     */
    EndElement asEndElement();

    /**
     * Returns this event as Characters, may result in
     * a class cast exception if this event is not Characters.
     */
    Characters asCharacters();

    /**
     * This method is provided for implementations to provide
     * optional type information about the associated event.
     * It is optional and will return null if no information
     * is available.
     */
    QName getSchemaType();

    /**
     * This method will write the XMLEvent as per the XML 1.0 specification as Unicode characters.
     * No indentation or whitespace should be outputted.
     * <p/>
     * Any user defined event type SHALL have this method
     * called when being written to on an output stream.
     * Built in Event types MUST implement this method,
     * but implementations MAY choose not call these methods
     * for optimizations reasons when writing out built in
     * Events to an output stream.
     * The output generated MUST be equivalent in terms of the
     * infoset expressed.
     *
     * @param writer The writer that will output the data
     * @throws XMLStreamException if there is a fatal error writing the event
     */
    void writeAsEncodedUnicode(Writer writer)
            throws mf.javax.xml.stream.XMLStreamException;

}

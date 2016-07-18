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

/**
 * This interface declares the constants used in this API.
 * Numbers in the range 0 to 256 are reserved for the specification,
 * user defined events must use event codes outside that range.
 *
 * @since 1.6
 */

public interface XMLStreamConstants {
    /**
     * Indicates an event is a start element
     *
     * @see javax.xml.stream.events.StartElement
     */
    int START_ELEMENT = 1;
    /**
     * Indicates an event is an end element
     *
     * @see javax.xml.stream.events.EndElement
     */
    int END_ELEMENT = 2;
    /**
     * Indicates an event is a processing instruction
     *
     * @see javax.xml.stream.events.ProcessingInstruction
     */
    int PROCESSING_INSTRUCTION = 3;

    /**
     * Indicates an event is characters
     *
     * @see javax.xml.stream.events.Characters
     */
    int CHARACTERS = 4;

    /**
     * Indicates an event is a comment
     *
     * @see javax.xml.stream.events.Comment
     */
    int COMMENT = 5;

    /**
     * The characters are white space
     * (see [XML], 2.10 "White Space Handling").
     * Events are only reported as SPACE if they are ignorable white
     * space.  Otherwise they are reported as CHARACTERS.
     *
     * @see javax.xml.stream.events.Characters
     */
    int SPACE = 6;

    /**
     * Indicates an event is a start document
     *
     * @see javax.xml.stream.events.StartDocument
     */
    int START_DOCUMENT = 7;

    /**
     * Indicates an event is an end document
     *
     * @see javax.xml.stream.events.EndDocument
     */
    int END_DOCUMENT = 8;

    /**
     * Indicates an event is an entity reference
     *
     * @see javax.xml.stream.events.EntityReference
     */
    int ENTITY_REFERENCE = 9;

    /**
     * Indicates an event is an attribute
     *
     * @see javax.xml.stream.events.Attribute
     */
    int ATTRIBUTE = 10;

    /**
     * Indicates an event is a DTD
     *
     * @see javax.xml.stream.events.DTD
     */
    int DTD = 11;

    /**
     * Indicates an event is a CDATA section
     *
     * @see javax.xml.stream.events.Characters
     */
    int CDATA = 12;

    /**
     * Indicates the event is a namespace declaration
     *
     * @see javax.xml.stream.events.Namespace
     */
    int NAMESPACE = 13;

    /**
     * Indicates a Notation
     *
     * @see javax.xml.stream.events.NotationDeclaration
     */
    int NOTATION_DECLARATION = 14;

    /**
     * Indicates a Entity Declaration
     *
     * @see javax.xml.stream.events.NotationDeclaration
     */
    int ENTITY_DECLARATION = 15;
}

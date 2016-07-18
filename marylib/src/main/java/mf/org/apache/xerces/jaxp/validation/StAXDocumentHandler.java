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

package mf.org.apache.xerces.jaxp.validation;

import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.XMLStreamReader;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.Comment;
import mf.javax.xml.stream.events.DTD;
import mf.javax.xml.stream.events.EndDocument;
import mf.javax.xml.stream.events.EntityReference;
import mf.javax.xml.stream.events.ProcessingInstruction;
import mf.javax.xml.stream.events.StartDocument;
import mf.javax.xml.transform.stax.StAXResult;
import mf.org.apache.xerces.xni.XMLDocumentHandler;

/**
 * <p>An extension to XMLDocumentHandler for building StAX structures.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: StAXDocumentHandler.java 683688 2008-08-07 19:37:02Z mrglavas $
 */
interface StAXDocumentHandler extends XMLDocumentHandler {

    /**
     * <p>Sets the <code>StAXResult</code> object which will be receiving the output.</p>
     */
    void setStAXResult(StAXResult result);

    void startDocument(XMLStreamReader reader) throws XMLStreamException;

    void endDocument(XMLStreamReader reader) throws XMLStreamException;

    void comment(XMLStreamReader reader) throws XMLStreamException;

    void processingInstruction(XMLStreamReader reader) throws XMLStreamException;

    void entityReference(XMLStreamReader reader) throws XMLStreamException;

    void startDocument(StartDocument event) throws XMLStreamException;

    void endDocument(EndDocument event) throws XMLStreamException;

    void doctypeDecl(DTD event) throws XMLStreamException;

    void characters(Characters event) throws XMLStreamException;

    void cdata(Characters event) throws XMLStreamException;

    void comment(Comment event) throws XMLStreamException;

    void processingInstruction(ProcessingInstruction event) throws XMLStreamException;

    void entityReference(EntityReference event) throws XMLStreamException;

    void setIgnoringCharacters(boolean ignore);

} // StAXDocumentHandler

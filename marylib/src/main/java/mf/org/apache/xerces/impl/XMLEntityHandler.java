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

package mf.org.apache.xerces.impl;

import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.XMLResourceIdentifier;
import mf.org.apache.xerces.xni.XNIException;

/**
 * The entity handler interface defines methods to report information
 * about the start and end of entities.
 *
 * @author Andy Clark, IBM
 * @version $Id: XMLEntityHandler.java 446761 2006-09-15 21:59:29Z mrglavas $
 * @xerces.internal
 * @see mf.org.apache.xerces.impl.XMLEntityScanner
 */
public interface XMLEntityHandler {

    //
    // XMLEntityHandler methods
    //

    /**
     * This method notifies of the start of an entity. The DTD has the
     * pseudo-name of "[dtd]" parameter entity names start with '%'; and
     * general entities are just specified by their name.
     *
     * @param name       The name of the entity.
     * @param identifier The resource identifier.
     * @param encoding   The auto-detected IANA encoding name of the entity
     *                   stream. This value will be null in those situations
     *                   where the entity encoding is not auto-detected (e.g.
     *                   internal entities or a document entity that is
     *                   parsed from a java.io.Reader).
     * @param augs       Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    void startEntity(String name,
                     XMLResourceIdentifier identifier,
                     String encoding, Augmentations augs) throws XNIException;

    /**
     * This method notifies the end of an entity. The DTD has the pseudo-name
     * of "[dtd]" parameter entity names start with '%'; and general entities
     * are just specified by their name.
     *
     * @param name The name of the entity.
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    void endEntity(String name, Augmentations augs) throws XNIException;

} // interface XMLEntityHandler

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

package mf.org.apache.xerces.jaxp;

import java.util.HashMap;

import mf.org.apache.xerces.impl.validation.EntityState;
import mf.org.apache.xerces.impl.validation.ValidationManager;
import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.XMLDTDHandler;
import mf.org.apache.xerces.xni.XMLLocator;
import mf.org.apache.xerces.xni.XMLResourceIdentifier;
import mf.org.apache.xerces.xni.XMLString;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLDTDFilter;
import mf.org.apache.xerces.xni.parser.XMLDTDSource;


/**
 * <p>This filter records which unparsed entities have been
 * declared in the DTD and provides this information to a ValidationManager.
 * Events are forwarded to the registered XMLDTDHandler without modification.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: UnparsedEntityHandler.java 520055 2007-03-19 19:25:57Z mrglavas $
 */
final class UnparsedEntityHandler implements XMLDTDFilter, EntityState {

    /**
     * Validation manager.
     */
    private final ValidationManager fValidationManager;
    /**
     * DTD source and handler.
     **/
    private XMLDTDSource fDTDSource;
    private XMLDTDHandler fDTDHandler;
    /**
     * Map for tracking unparsed entities.
     */
    private HashMap fUnparsedEntities = null;

    UnparsedEntityHandler(ValidationManager manager) {
        fValidationManager = manager;
    }
    
    /*
     * XMLDTDHandler methods
     */

    @Override
    public void startDTD(XMLLocator locator, Augmentations augmentations)
            throws XNIException {
        fValidationManager.setEntityState(this);
        if (fDTDHandler != null) {
            fDTDHandler.startDTD(locator, augmentations);
        }
    }

    @Override
    public void startParameterEntity(String name,
                                     XMLResourceIdentifier identifier, String encoding,
                                     Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.startParameterEntity(name, identifier, encoding, augmentations);
        }
    }

    @Override
    public void textDecl(String version, String encoding,
                         Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.textDecl(version, encoding, augmentations);
        }
    }

    @Override
    public void endParameterEntity(String name, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.endParameterEntity(name, augmentations);
        }
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier identifier,
                                    Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.startExternalSubset(identifier, augmentations);
        }
    }

    @Override
    public void endExternalSubset(Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.endExternalSubset(augmentations);
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.comment(text, augmentations);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data,
                                      Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.processingInstruction(target, data, augmentations);
        }
    }

    @Override
    public void elementDecl(String name, String contentModel,
                            Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.elementDecl(name, contentModel, augmentations);
        }
    }

    @Override
    public void startAttlist(String elementName, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.startAttlist(elementName, augmentations);
        }
    }

    @Override
    public void attributeDecl(String elementName, String attributeName,
                              String type, String[] enumeration, String defaultType,
                              XMLString defaultValue, XMLString nonNormalizedDefaultValue,
                              Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.attributeDecl(elementName, attributeName,
                    type, enumeration, defaultType,
                    defaultValue, nonNormalizedDefaultValue,
                    augmentations);
        }
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.endAttlist(augmentations);
        }
    }

    @Override
    public void internalEntityDecl(String name, XMLString text,
                                   XMLString nonNormalizedText, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.internalEntityDecl(name, text,
                    nonNormalizedText, augmentations);
        }
    }

    @Override
    public void externalEntityDecl(String name,
                                   XMLResourceIdentifier identifier, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.externalEntityDecl(name, identifier, augmentations);
        }
    }

    @Override
    public void unparsedEntityDecl(String name,
                                   XMLResourceIdentifier identifier, String notation,
                                   Augmentations augmentations) throws XNIException {
        if (fUnparsedEntities == null) {
            fUnparsedEntities = new HashMap();
        }
        fUnparsedEntities.put(name, name);
        if (fDTDHandler != null) {
            fDTDHandler.unparsedEntityDecl(name, identifier, notation, augmentations);
        }
    }

    @Override
    public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.notationDecl(name, identifier, augmentations);
        }
    }

    @Override
    public void startConditional(short type, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.startConditional(type, augmentations);
        }
    }

    @Override
    public void ignoredCharacters(XMLString text, Augmentations augmentations)
            throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.ignoredCharacters(text, augmentations);
        }

    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.endConditional(augmentations);
        }
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        if (fDTDHandler != null) {
            fDTDHandler.endDTD(augmentations);
        }
    }

    @Override
    public XMLDTDSource getDTDSource() {
        return fDTDSource;
    }

    @Override
    public void setDTDSource(XMLDTDSource source) {
        fDTDSource = source;
    }
    
    /*
     * XMLDTDSource methods
     */

    @Override
    public XMLDTDHandler getDTDHandler() {
        return fDTDHandler;
    }

    @Override
    public void setDTDHandler(XMLDTDHandler handler) {
        fDTDHandler = handler;
    }
    
    /*
     * EntityState methods
     */

    @Override
    public boolean isEntityDeclared(String name) {
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String name) {
        if (fUnparsedEntities != null) {
            return fUnparsedEntities.containsKey(name);
        }
        return false;
    }
    
    /*
     * Other methods
     */

    public void reset() {
        if (fUnparsedEntities != null && !fUnparsedEntities.isEmpty()) {
            // should only clear this if the last document contained unparsed entities
            fUnparsedEntities.clear();
        }
    }
}

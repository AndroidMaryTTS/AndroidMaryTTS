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

package mf.org.apache.xerces.util;

import org.xml.sax.AttributeList;
import org.xml.sax.ext.Attributes2;

import mf.org.apache.xerces.impl.Constants;
import mf.org.apache.xerces.xni.XMLAttributes;

/**
 * Wraps {@link XMLAttributes} and makes it look like
 * {@link AttributeList} and {@link Attributes2}.
 *
 * @author Arnaud Le Hors, IBM
 * @author Andy Clark, IBM
 * @version $Id: AttributesProxy.java 449487 2006-09-24 21:11:28Z mrglavas $
 */
public final class AttributesProxy
        implements AttributeList, Attributes2 {

    //
    // Data
    //

    /**
     * XML attributes.
     */
    private XMLAttributes fAttributes;

    //
    // Constructors
    //

    public AttributesProxy(XMLAttributes attributes) {
        fAttributes = attributes;
    }

    //
    // Public methods
    //

    public XMLAttributes getAttributes() {
        return fAttributes;
    }

    /**
     * Sets the XML attributes to be wrapped.
     */
    public void setAttributes(XMLAttributes attributes) {
        fAttributes = attributes;
    } // setAttributes(XMLAttributes)

    /*
     * Attributes methods
     */

    @Override
    public int getLength() {
        return fAttributes.getLength();
    }

    @Override
    public String getQName(int index) {
        return fAttributes.getQName(index);
    }

    @Override
    public String getURI(int index) {
        // This hides the fact that internally we use null instead of empty string
        // SAX requires the URI to be a string or an empty string
        String uri = fAttributes.getURI(index);
        return uri != null ? uri : XMLSymbols.EMPTY_STRING;
    }

    @Override
    public String getLocalName(int index) {
        return fAttributes.getLocalName(index);
    }

    @Override
    public String getType(int i) {
        return fAttributes.getType(i);
    }

    @Override
    public String getType(String name) {
        return fAttributes.getType(name);
    }

    @Override
    public String getType(String uri, String localName) {
        return uri.equals(XMLSymbols.EMPTY_STRING) ?
                fAttributes.getType(null, localName) :
                fAttributes.getType(uri, localName);
    }

    @Override
    public String getValue(int i) {
        return fAttributes.getValue(i);
    }

    @Override
    public String getValue(String name) {
        return fAttributes.getValue(name);
    }

    @Override
    public String getValue(String uri, String localName) {
        return uri.equals(XMLSymbols.EMPTY_STRING) ?
                fAttributes.getValue(null, localName) :
                fAttributes.getValue(uri, localName);
    }

    @Override
    public int getIndex(String qName) {
        return fAttributes.getIndex(qName);
    }

    @Override
    public int getIndex(String uri, String localPart) {
        return uri.equals(XMLSymbols.EMPTY_STRING) ?
                fAttributes.getIndex(null, localPart) :
                fAttributes.getIndex(uri, localPart);
    }
    
    /*
     * Attributes2 methods
     */

    @Override
    public boolean isDeclared(int index) {
        if (index < 0 || index >= fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return Boolean.TRUE.equals(
                fAttributes.getAugmentations(index).getItem(
                        Constants.ATTRIBUTE_DECLARED));
    }

    @Override
    public boolean isDeclared(String qName) {
        int index = getIndex(qName);
        if (index == -1) {
            throw new IllegalArgumentException(qName);
        }
        return Boolean.TRUE.equals(
                fAttributes.getAugmentations(index).getItem(
                        Constants.ATTRIBUTE_DECLARED));
    }

    @Override
    public boolean isDeclared(String uri, String localName) {
        int index = getIndex(uri, localName);
        if (index == -1) {
            throw new IllegalArgumentException(localName);
        }
        return Boolean.TRUE.equals(
                fAttributes.getAugmentations(index).getItem(
                        Constants.ATTRIBUTE_DECLARED));
    }

    @Override
    public boolean isSpecified(int index) {
        if (index < 0 || index >= fAttributes.getLength()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return fAttributes.isSpecified(index);
    }

    @Override
    public boolean isSpecified(String qName) {
        int index = getIndex(qName);
        if (index == -1) {
            throw new IllegalArgumentException(qName);
        }
        return fAttributes.isSpecified(index);
    }

    @Override
    public boolean isSpecified(String uri, String localName) {
        int index = getIndex(uri, localName);
        if (index == -1) {
            throw new IllegalArgumentException(localName);
        }
        return fAttributes.isSpecified(index);
    }
    
    /*
     * AttributeList methods
     */

    @Override
    public String getName(int i) {
        return fAttributes.getQName(i);
    }

}
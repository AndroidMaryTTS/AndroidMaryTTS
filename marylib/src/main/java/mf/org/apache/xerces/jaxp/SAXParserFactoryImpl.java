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

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;

import mf.javax.xml.XMLConstants;
import mf.javax.xml.parsers.ParserConfigurationException;
import mf.javax.xml.parsers.SAXParser;
import mf.javax.xml.parsers.SAXParserFactory;
import mf.javax.xml.validation.Schema;
import mf.org.apache.xerces.impl.Constants;

/**
 * This is the implementation specific class for the
 * <code>javax.xml.parsers.SAXParserFactory</code>. This is the platform
 * default implementation for the platform.
 *
 * @author Rajiv Mordani
 * @author Edwin Goei
 * @version $Id: SAXParserFactoryImpl.java 447237 2006-09-18 05:03:10Z mrglavas $
 */
public class SAXParserFactoryImpl extends SAXParserFactory {

    /**
     * Feature identifier: namespaces.
     */
    private static final String NAMESPACES_FEATURE =
            Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;

    /**
     * Feature identifier: validation.
     */
    private static final String VALIDATION_FEATURE =
            Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;

    /**
     * Feature identifier: XInclude processing
     */
    private static final String XINCLUDE_FEATURE =
            Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_FEATURE;

    private Hashtable features;
    private Schema grammar;
    private boolean isXIncludeAware;

    /**
     * State of the secure processing feature, initially <code>false</code>
     */
    private boolean fSecureProcess = false;

    /**
     * Creates a new instance of <code>SAXParser</code> using the currently
     * configured factory parameters.
     *
     * @return javax.xml.parsers.SAXParser
     */
    @Override
    public SAXParser newSAXParser()
            throws ParserConfigurationException {

        SAXParser saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, features, fSecureProcess);
        } catch (SAXException se) {
            // Translate to ParserConfigurationException
            throw new ParserConfigurationException(se.getMessage());
        }
        return saxParserImpl;
    }

    /**
     * Common code for translating exceptions
     */
    private SAXParserImpl newSAXParserImpl()
            throws ParserConfigurationException, SAXNotRecognizedException,
            SAXNotSupportedException {

        SAXParserImpl saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, features);
        } catch (SAXNotSupportedException e) {
            throw e;
        } catch (SAXNotRecognizedException e) {
            throw e;
        } catch (SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
        return saxParserImpl;
    }

    /**
     * Sets the particular feature in the underlying implementation of
     * org.xml.sax.XMLReader.
     */
    @Override
    public void setFeature(String name, boolean value)
            throws ParserConfigurationException, SAXNotRecognizedException,
            SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        // If this is the secure processing feature, save it then return.
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            fSecureProcess = value;
            return;
        }
        // Keep built-in settings in synch with the feature values.
        else if (name.equals(NAMESPACES_FEATURE)) {
            setNamespaceAware(value);
            return;
        } else if (name.equals(VALIDATION_FEATURE)) {
            setValidating(value);
            return;
        } else if (name.equals(XINCLUDE_FEATURE)) {
            setXIncludeAware(value);
            return;
        }

        // XXX This is ugly.  We have to collect the features and then
        // later create an XMLReader to verify the features.
        if (features == null) {
            features = new Hashtable();
        }
        features.put(name, value ? Boolean.TRUE : Boolean.FALSE);

        // Test the feature by possibly throwing SAX exceptions
        try {
            newSAXParserImpl();
        } catch (SAXNotSupportedException e) {
            features.remove(name);
            throw e;
        } catch (SAXNotRecognizedException e) {
            features.remove(name);
            throw e;
        }
    }

    /**
     * returns the particular property requested for in the underlying
     * implementation of org.xml.sax.XMLReader.
     */
    @Override
    public boolean getFeature(String name)
            throws ParserConfigurationException, SAXNotRecognizedException,
            SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            return fSecureProcess;
        } else if (name.equals(NAMESPACES_FEATURE)) {
            return isNamespaceAware();
        } else if (name.equals(VALIDATION_FEATURE)) {
            return isValidating();
        } else if (name.equals(XINCLUDE_FEATURE)) {
            return isXIncludeAware();
        }
        // Check for valid name by creating a dummy XMLReader to get
        // feature value
        return newSAXParserImpl().getXMLReader().getFeature(name);
    }

    @Override
    public Schema getSchema() {
        return grammar;
    }

    @Override
    public void setSchema(Schema grammar) {
        this.grammar = grammar;
    }

    @Override
    public boolean isXIncludeAware() {
        return this.isXIncludeAware;
    }

    @Override
    public void setXIncludeAware(boolean state) {
        this.isXIncludeAware = state;
    }
}

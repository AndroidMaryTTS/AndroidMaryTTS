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

package mf.org.apache.xerces.xni.grammars;

import java.io.IOException;
import java.util.Locale;

import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLConfigurationException;
import mf.org.apache.xerces.xni.parser.XMLEntityResolver;
import mf.org.apache.xerces.xni.parser.XMLErrorHandler;
import mf.org.apache.xerces.xni.parser.XMLInputSource;

/**
 * The intention of this interface is to provide a generic means
 * by which Grammar objects may be created without parsing instance
 * documents.  Implementations of this interface will know how to load
 * specific types of grammars (e.g., DTD's or schemas); a wrapper
 * will be provided for user applications to interact with these implementations.
 *
 * @author Neil Graham, IBM
 * @version $Id: XMLGrammarLoader.java 699892 2008-09-28 21:08:27Z mrglavas $
 */

public interface XMLGrammarLoader {

    /**
     * Returns a list of feature identifiers that are recognized by
     * this XMLGrammarLoader.  This method may return null if no features
     * are recognized.
     */
    String[] getRecognizedFeatures();

    /**
     * Returns the state of a feature.
     *
     * @param featureId The feature identifier.
     * @throws XMLConfigurationException Thrown on configuration error.
     */
    boolean getFeature(String featureId)
            throws XMLConfigurationException;

    /**
     * Sets the state of a feature.
     *
     * @param featureId The feature identifier.
     * @param state     The state of the feature.
     * @throws XMLConfigurationException Thrown when a feature is not
     *                                   recognized or cannot be set.
     */
    void setFeature(String featureId,
                    boolean state) throws XMLConfigurationException;

    /**
     * Returns a list of property identifiers that are recognized by
     * this XMLGrammarLoader.  This method may return null if no properties
     * are recognized.
     */
    String[] getRecognizedProperties();

    /**
     * Returns the state of a property.
     *
     * @param propertyId The property identifier.
     * @throws XMLConfigurationException Thrown on configuration error.
     */
    Object getProperty(String propertyId)
            throws XMLConfigurationException;

    /**
     * Sets the state of a property.
     *
     * @param propertyId The property identifier.
     * @param state      The state of the property.
     * @throws XMLConfigurationException Thrown when a property is not
     *                                   recognized or cannot be set.
     */
    void setProperty(String propertyId,
                     Object state) throws XMLConfigurationException;

    /**
     * Return the Locale the XMLGrammarLoader is using.
     */
    Locale getLocale();

    /**
     * Set the locale to use for messages.
     *
     * @param locale The locale object to use for localization of messages.
     * @throws XNIException Thrown if the parser does not support the
     *                      specified locale.
     */
    void setLocale(Locale locale);

    /**
     * Returns the registered error handler.
     */
    XMLErrorHandler getErrorHandler();

    /**
     * Sets the error handler.
     *
     * @param errorHandler The error handler.
     */
    void setErrorHandler(XMLErrorHandler errorHandler);

    /**
     * Returns the registered entity resolver.
     */
    XMLEntityResolver getEntityResolver();

    /**
     * Sets the entity resolver.
     *
     * @param entityResolver The new entity resolver.
     */
    void setEntityResolver(XMLEntityResolver entityResolver);

    /**
     * Returns a Grammar object by parsing the contents of the
     * entity pointed to by source.
     *
     * @param source the location of the entity which forms
     *               the starting point of the grammar to be constructed.
     * @throws IOException When a problem is encountered reading the entity
     *                     XNIException    When a condition arises (such as a FatalError) that requires parsing
     *                     of the entity be terminated.
     */
    Grammar loadGrammar(XMLInputSource source)
            throws IOException, XNIException;
} // XMLGrammarLoader


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

import java.util.Iterator;

import mf.javax.xml.namespace.NamespaceContext;
import mf.javax.xml.namespace.QName;
import mf.javax.xml.stream.events.Attribute;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.Comment;
import mf.javax.xml.stream.events.DTD;
import mf.javax.xml.stream.events.EndDocument;
import mf.javax.xml.stream.events.EndElement;
import mf.javax.xml.stream.events.EntityDeclaration;
import mf.javax.xml.stream.events.EntityReference;
import mf.javax.xml.stream.events.Namespace;
import mf.javax.xml.stream.events.ProcessingInstruction;
import mf.javax.xml.stream.events.StartDocument;
import mf.javax.xml.stream.events.StartElement;

/**
 * This interface defines a utility class for creating instances of
 * XMLEvents
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.2
 * @see javax.xml.stream.events.StartElement
 * @see javax.xml.stream.events.EndElement
 * @see javax.xml.stream.events.ProcessingInstruction
 * @see javax.xml.stream.events.Comment
 * @see javax.xml.stream.events.Characters
 * @see javax.xml.stream.events.StartDocument
 * @see javax.xml.stream.events.EndDocument
 * @see javax.xml.stream.events.DTD
 * @since 1.6
 */
public abstract class XMLEventFactory {
    static final String JAXPFACTORYID = "mf.javax.xml.stream.XMLEventFactory";
    static final String DEFAULIMPL = "com.sun.xml.internal.stream.events.XMLEventFactoryImpl";

    protected XMLEventFactory() {
    }

    /**
     * Create a new instance of the factory
     *
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     */
    public static XMLEventFactory newInstance()
            throws FactoryConfigurationError {
        return (XMLEventFactory) FactoryFinder.find(
                JAXPFACTORYID,
                DEFAULIMPL);
    }

    /**
     * Create a new instance of the factory.
     * This static method creates a new factory instance.
     * This method uses the following ordered lookup procedure to determine
     * the XMLEventFactory implementation class to load:
     * Use the javax.xml.stream.XMLEventFactory system property.
     * Use the properties file "lib/stax.properties" in the JRE directory.
     * This configuration file is in standard java.util.Properties format
     * and contains the fully qualified name of the implementation class
     * with the key being the system property defined above.
     * Use the Services API (as detailed in the JAR specification), if available,
     * to determine the classname. The Services API will look for a classname
     * in the file META-INF/services/javax.xml.stream.XMLEventFactory in jars
     * available to the runtime.
     * Platform default XMLEventFactory instance.
     * <p/>
     * Once an application has obtained a reference to a XMLEventFactory it
     * can use the factory to configure and obtain stream instances.
     * <p/>
     * Note that this is a new method that replaces the deprecated newInstance() method.
     * No changes in behavior are defined by this replacement method relative to
     * the deprecated method.
     *
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     */
    public static XMLEventFactory newFactory()
            throws FactoryConfigurationError {
        return (XMLEventFactory) FactoryFinder.find(
                JAXPFACTORYID,
                DEFAULIMPL);
    }

    /**
     * Create a new instance of the factory
     *
     * @param factoryId   Name of the factory to find, same as
     *                    a property name
     * @param classLoader classLoader to use
     * @return the factory implementation
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     * @deprecated This method has been deprecated to maintain API consistency.
     * All newInstance methods have been replaced with corresponding
     * newFactory methods. The replacement {@link
     * #newFactory(java.lang.String, java.lang.ClassLoader)}
     * method defines no changes in behavior.
     */
    @Deprecated
    public static XMLEventFactory newInstance(String factoryId,
                                              ClassLoader classLoader)
            throws FactoryConfigurationError {
        try {
            //do not fallback if given classloader can't find the class, throw exception
            return (XMLEventFactory) FactoryFinder.find(factoryId, classLoader, null);
        } catch (FactoryFinder.ConfigurationError e) {
            throw new FactoryConfigurationError(e.getException(),
                    e.getMessage());
        }
    }

    /**
     * Create a new instance of the factory.
     * If the classLoader argument is null, then the ContextClassLoader is used.
     * <p/>
     * Note that this is a new method that replaces the deprecated
     * newInstance(String factoryId, ClassLoader classLoader) method.
     * No changes in behavior are defined by this replacement method relative
     * to the deprecated method.
     *
     * @param factoryId   Name of the factory to find, same as
     *                    a property name
     * @param classLoader classLoader to use
     * @return the factory implementation
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     */
    public static XMLEventFactory newFactory(String factoryId,
                                             ClassLoader classLoader)
            throws FactoryConfigurationError {
        try {
            //do not fallback if given classloader can't find the class, throw exception
            return (XMLEventFactory) FactoryFinder.find(factoryId, classLoader, null);
        } catch (FactoryFinder.ConfigurationError e) {
            throw new FactoryConfigurationError(e.getException(),
                    e.getMessage());
        }
    }

    /**
     * This method allows setting of the Location on each event that
     * is created by this factory.  The values are copied by value into
     * the events created by this factory.  To reset the location
     * information set the location to null.
     *
     * @param location the location to set on each event created
     */
    public abstract void setLocation(Location location);

    /**
     * Create a new Attribute
     *
     * @param prefix       the prefix of this attribute, may not be null
     * @param namespaceURI the attribute value is set to this value, may not be null
     * @param localName    the local name of the XML name of the attribute, localName cannot be null
     * @param value        the attribute value to set, may not be null
     * @return the Attribute with specified values
     */
    public abstract Attribute createAttribute(String prefix, String namespaceURI, String localName, String value);

    /**
     * Create a new Attribute
     *
     * @param localName the local name of the XML name of the attribute, localName cannot be null
     * @param value     the attribute value to set, may not be null
     * @return the Attribute with specified values
     */
    public abstract Attribute createAttribute(String localName, String value);

    /**
     * Create a new Attribute
     *
     * @param name  the qualified name of the attribute, may not be null
     * @param value the attribute value to set, may not be null
     * @return the Attribute with specified values
     */
    public abstract Attribute createAttribute(QName name, String value);

    /**
     * Create a new default Namespace
     *
     * @param namespaceURI the default namespace uri
     * @return the Namespace with the specified value
     */
    public abstract Namespace createNamespace(String namespaceURI);

    /**
     * Create a new Namespace
     *
     * @param prefix       the prefix of this namespace, may not be null
     * @param namespaceUri the attribute value is set to this value, may not be null
     * @return the Namespace with the specified values
     */
    public abstract Namespace createNamespace(String prefix, String namespaceUri);

    /**
     * Create a new StartElement.  Namespaces can be added to this StartElement
     * by passing in an Iterator that walks over a set of Namespace interfaces.
     * Attributes can be added to this StartElement by passing an iterator
     * that walks over a set of Attribute interfaces.
     *
     * @param name       the qualified name of the attribute, may not be null
     * @param attributes an optional unordered set of objects that
     *                   implement Attribute to add to the new StartElement, may be null
     * @param namespaces an optional unordered set of objects that
     *                   implement Namespace to add to the new StartElement, may be null
     * @return an instance of the requested StartElement
     */
    public abstract StartElement createStartElement(QName name,
                                                    Iterator attributes,
                                                    Iterator namespaces);

    /**
     * Create a new StartElement.  This defaults the NamespaceContext to
     * an empty NamespaceContext.  Querying this event for its namespaces or
     * attributes will result in an empty iterator being returned.
     *
     * @param namespaceUri the uri of the QName of the new StartElement
     * @param localName    the local name of the QName of the new StartElement
     * @param prefix       the prefix of the QName of the new StartElement
     * @return an instance of the requested StartElement
     */
    public abstract StartElement createStartElement(String prefix,
                                                    String namespaceUri,
                                                    String localName);

    /**
     * Create a new StartElement.  Namespaces can be added to this StartElement
     * by passing in an Iterator that walks over a set of Namespace interfaces.
     * Attributes can be added to this StartElement by passing an iterator
     * that walks over a set of Attribute interfaces.
     *
     * @param namespaceUri the uri of the QName of the new StartElement
     * @param localName    the local name of the QName of the new StartElement
     * @param prefix       the prefix of the QName of the new StartElement
     * @param attributes   an unordered set of objects that implement
     *                     Attribute to add to the new StartElement
     * @param namespaces   an unordered set of objects that implement
     *                     Namespace to add to the new StartElement
     * @return an instance of the requested StartElement
     */
    public abstract StartElement createStartElement(String prefix,
                                                    String namespaceUri,
                                                    String localName,
                                                    Iterator attributes,
                                                    Iterator namespaces
    );

    /**
     * Create a new StartElement.  Namespaces can be added to this StartElement
     * by passing in an Iterator that walks over a set of Namespace interfaces.
     * Attributes can be added to this StartElement by passing an iterator
     * that walks over a set of Attribute interfaces.
     *
     * @param namespaceUri the uri of the QName of the new StartElement
     * @param localName    the local name of the QName of the new StartElement
     * @param prefix       the prefix of the QName of the new StartElement
     * @param attributes   an unordered set of objects that implement
     *                     Attribute to add to the new StartElement, may be null
     * @param namespaces   an unordered set of objects that implement
     *                     Namespace to add to the new StartElement, may be null
     * @param context      the namespace context of this element
     * @return an instance of the requested StartElement
     */
    public abstract StartElement createStartElement(String prefix,
                                                    String namespaceUri,
                                                    String localName,
                                                    Iterator attributes,
                                                    Iterator namespaces,
                                                    NamespaceContext context
    );

    /**
     * Create a new EndElement
     *
     * @param name       the qualified name of the EndElement
     * @param namespaces an optional unordered set of objects that
     *                   implement Namespace that have gone out of scope, may be null
     * @return an instance of the requested EndElement
     */
    public abstract EndElement createEndElement(QName name,
                                                Iterator namespaces);

    /**
     * Create a new EndElement
     *
     * @param namespaceUri the uri of the QName of the new StartElement
     * @param localName    the local name of the QName of the new StartElement
     * @param prefix       the prefix of the QName of the new StartElement
     * @return an instance of the requested EndElement
     */
    public abstract EndElement createEndElement(String prefix,
                                                String namespaceUri,
                                                String localName);

    /**
     * Create a new EndElement
     *
     * @param namespaceUri the uri of the QName of the new StartElement
     * @param localName    the local name of the QName of the new StartElement
     * @param prefix       the prefix of the QName of the new StartElement
     * @param namespaces   an unordered set of objects that implement
     *                     Namespace that have gone out of scope, may be null
     * @return an instance of the requested EndElement
     */
    public abstract EndElement createEndElement(String prefix,
                                                String namespaceUri,
                                                String localName,
                                                Iterator namespaces);

    /**
     * Create a Characters event, this method does not check if the content
     * is all whitespace.  To create a space event use #createSpace(String)
     *
     * @param content the string to create
     * @return a Characters event
     */
    public abstract Characters createCharacters(String content);

    /**
     * Create a Characters event with the CData flag set to true
     *
     * @param content the string to create
     * @return a Characters event
     */
    public abstract Characters createCData(String content);

    /**
     * Create a Characters event with the isSpace flag set to true
     *
     * @param content the content of the space to create
     * @return a Characters event
     */
    public abstract Characters createSpace(String content);

    /**
     * Create an ignorable space
     *
     * @param content the space to create
     * @return a Characters event
     */
    public abstract Characters createIgnorableSpace(String content);

    /**
     * Creates a new instance of a StartDocument event
     *
     * @return a StartDocument event
     */
    public abstract StartDocument createStartDocument();

    /**
     * Creates a new instance of a StartDocument event
     *
     * @param encoding   the encoding style
     * @param version    the XML version
     * @param standalone the status of standalone may be set to "true" or "false"
     * @return a StartDocument event
     */
    public abstract StartDocument createStartDocument(String encoding,
                                                      String version,
                                                      boolean standalone);

    /**
     * Creates a new instance of a StartDocument event
     *
     * @param encoding the encoding style
     * @param version  the XML version
     * @return a StartDocument event
     */
    public abstract StartDocument createStartDocument(String encoding,
                                                      String version);

    /**
     * Creates a new instance of a StartDocument event
     *
     * @param encoding the encoding style
     * @return a StartDocument event
     */
    public abstract StartDocument createStartDocument(String encoding);

    /**
     * Creates a new instance of an EndDocument event
     *
     * @return an EndDocument event
     */
    public abstract EndDocument createEndDocument();

    /**
     * Creates a new instance of a EntityReference event
     *
     * @param name        The name of the reference
     * @param declaration the declaration for the event
     * @return an EntityReference event
     */
    public abstract EntityReference createEntityReference(String name,
                                                          EntityDeclaration declaration);

    /**
     * Create a comment
     *
     * @param text The text of the comment
     *             a Comment event
     */
    public abstract Comment createComment(String text);

    /**
     * Create a processing instruction
     *
     * @param target The target of the processing instruction
     * @param data   The text of the processing instruction
     * @return a ProcessingInstruction event
     */
    public abstract ProcessingInstruction createProcessingInstruction(String target,
                                                                      String data);

    /**
     * Create a document type definition event
     * This string contains the entire document type declaration that matches
     * the doctypedecl in the XML 1.0 specification
     *
     * @param dtd the text of the document type definition
     * @return a DTD event
     */
    public abstract DTD createDTD(String dtd);
}

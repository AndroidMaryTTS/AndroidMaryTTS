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

/*
 * $Id: SAXTransformerFactory.java,v 1.5 2010-11-01 04:36:12 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform.sax;

import org.xml.sax.XMLFilter;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.Templates;
import mf.javax.xml.transform.TransformerConfigurationException;
import mf.javax.xml.transform.TransformerFactory;

/**
 * This class extends TransformerFactory to provide SAX-specific
 * factory methods.  It provides two types of ContentHandlers,
 * one for creating Transformers, the other for creating Templates
 * objects.
 * <p/>
 * <p>If an application wants to set the ErrorHandler or EntityResolver
 * for an XMLReader used during a transformation, it should use a URIResolver
 * to return the SAXSource which provides (with getXMLReader) a reference to
 * the XMLReader.</p>
 */
public abstract class SAXTransformerFactory extends TransformerFactory {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the TransformerFactory returned from
     * {@link javax.xml.transform.TransformerFactory#newInstance} may
     * be safely cast to a SAXTransformerFactory.
     */
    public static final String FEATURE =
            "http://javax.xml.transform.sax.SAXTransformerFactory/feature";

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the {@link #newXMLFilter(Source src)}
     * and {@link #newXMLFilter(Templates templates)} methods are supported.
     */
    public static final String FEATURE_XMLFILTER =
            "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter";

    /**
     * The default constructor is protected on purpose.
     */
    protected SAXTransformerFactory() {
    }

    /**
     * Get a TransformerHandler object that can process SAX
     * ContentHandler events into a Result, based on the transformation
     * instructions specified by the argument.
     *
     * @param src The Source of the transformation instructions.
     * @return TransformerHandler ready to transform SAX events.
     * @throws TransformerConfigurationException If for some reason the
     *                                           TransformerHandler can not be created.
     */
    public abstract TransformerHandler newTransformerHandler(Source src)
            throws TransformerConfigurationException;

    /**
     * Get a TransformerHandler object that can process SAX
     * ContentHandler events into a Result, based on the Templates argument.
     *
     * @param templates The compiled transformation instructions.
     * @return TransformerHandler ready to transform SAX events.
     * @throws TransformerConfigurationException If for some reason the
     *                                           TransformerHandler can not be created.
     */
    public abstract TransformerHandler newTransformerHandler(
            Templates templates) throws TransformerConfigurationException;

    /**
     * Get a TransformerHandler object that can process SAX
     * ContentHandler events into a Result. The transformation
     * is defined as an identity (or copy) transformation, for example
     * to copy a series of SAX parse events into a DOM tree.
     *
     * @return A non-null reference to a TransformerHandler, that may
     * be used as a ContentHandler for SAX parse events.
     * @throws TransformerConfigurationException If for some reason the
     *                                           TransformerHandler cannot be created.
     */
    public abstract TransformerHandler newTransformerHandler()
            throws TransformerConfigurationException;

    /**
     * Get a TemplatesHandler object that can process SAX
     * ContentHandler events into a Templates object.
     *
     * @return A non-null reference to a TransformerHandler, that may
     * be used as a ContentHandler for SAX parse events.
     * @throws TransformerConfigurationException If for some reason the
     *                                           TemplatesHandler cannot be created.
     */
    public abstract TemplatesHandler newTemplatesHandler()
            throws TransformerConfigurationException;

    /**
     * Create an XMLFilter that uses the given Source as the
     * transformation instructions.
     *
     * @param src The Source of the transformation instructions.
     * @return An XMLFilter object, or null if this feature is not supported.
     * @throws TransformerConfigurationException If for some reason the
     *                                           TemplatesHandler cannot be created.
     */
    public abstract XMLFilter newXMLFilter(Source src)
            throws TransformerConfigurationException;

    /**
     * Create an XMLFilter, based on the Templates argument..
     *
     * @param templates The compiled transformation instructions.
     * @return An XMLFilter object, or null if this feature is not supported.
     * @throws TransformerConfigurationException If for some reason the
     *                                           TemplatesHandler cannot be created.
     */
    public abstract XMLFilter newXMLFilter(Templates templates)
            throws TransformerConfigurationException;
}

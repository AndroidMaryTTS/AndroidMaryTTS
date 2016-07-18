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

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import mf.org.apache.xerces.xni.XMLResourceIdentifier;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLEntityResolver;
import mf.org.apache.xerces.xni.parser.XMLInputSource;

/**
 * This class wraps a SAX entity resolver in an XNI entity resolver.
 *
 * @author Andy Clark, IBM
 * @version $Id: EntityResolverWrapper.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @see EntityResolver
 */
public class EntityResolverWrapper
        implements XMLEntityResolver {

    //
    // Data
    //

    /**
     * The SAX entity resolver.
     */
    protected EntityResolver fEntityResolver;

    //
    // Constructors
    //

    /**
     * Default constructor.
     */
    public EntityResolverWrapper() {
    }

    /**
     * Wraps the specified SAX entity resolver.
     */
    public EntityResolverWrapper(EntityResolver entityResolver) {
        setEntityResolver(entityResolver);
    } // <init>(EntityResolver)

    //
    // Public methods
    //

    /**
     * Returns the SAX entity resolver.
     */
    public EntityResolver getEntityResolver() {
        return fEntityResolver;
    } // getEntityResolver():EntityResolver

    /**
     * Sets the SAX entity resolver.
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        fEntityResolver = entityResolver;
    } // setEntityResolver(EntityResolver)

    //
    // XMLEntityResolver methods
    //

    /**
     * Resolves an external parsed entity. If the entity cannot be
     * resolved, this method should return null.
     *
     * @param resourceIdentifier contains the physical co-ordinates of the resource to be resolved
     * @throws XNIException Thrown on general error.
     * @throws IOException  Thrown if resolved entity stream cannot be
     *                      opened or some other i/o error occurs.
     */
    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
            throws XNIException, IOException {

        // When both pubId and sysId are null, the user's entity resolver
        // can do nothing about it. We'd better not bother calling it.
        // This happens when the resourceIdentifier is a GrammarDescription,
        // which describes a schema grammar of some namespace, but without
        // any schema location hint. -Sg
        String pubId = resourceIdentifier.getPublicId();
        String sysId = resourceIdentifier.getExpandedSystemId();
        if (pubId == null && sysId == null)
            return null;

        // resolve entity using SAX entity resolver
        if (fEntityResolver != null && resourceIdentifier != null) {
            try {
                InputSource inputSource = fEntityResolver.resolveEntity(pubId, sysId);
                if (inputSource != null) {
                    String publicId = inputSource.getPublicId();
                    String systemId = inputSource.getSystemId();
                    String baseSystemId = resourceIdentifier.getBaseSystemId();
                    InputStream byteStream = inputSource.getByteStream();
                    Reader charStream = inputSource.getCharacterStream();
                    String encoding = inputSource.getEncoding();
                    XMLInputSource xmlInputSource =
                            new XMLInputSource(publicId, systemId, baseSystemId);
                    xmlInputSource.setByteStream(byteStream);
                    xmlInputSource.setCharacterStream(charStream);
                    xmlInputSource.setEncoding(encoding);
                    return xmlInputSource;
                }
            }

            // error resolving entity
            catch (SAXException e) {
                Exception ex = e.getException();
                if (ex == null) {
                    ex = e;
                }
                throw new XNIException(ex);
            }
        }

        // unable to resolve entity
        return null;

    } // resolveEntity(String,String,String):XMLInputSource
}

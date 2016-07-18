// BootstrapResolver.java - Resolve entities and URIs internally

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

package mf.org.apache.xml.resolver.helpers;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.TransformerException;
import mf.javax.xml.transform.URIResolver;
import mf.javax.xml.transform.sax.SAXSource;

/**
 * A simple bootstrapping resolver.
 * <p/>
 * <p>This class is used as the entity resolver when reading XML Catalogs.
 * It searches for the OASIS XML Catalog DTD, Relax NG Grammar and W3C XML Schema
 * as resources (e.g., in the resolver jar file).</p>
 * <p/>
 * <p>If you have your own DTDs or schemas, you can extend this class and
 * set the BootstrapResolver in your CatalogManager.</p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 * @see mf.org.apache.xml.resolver.CatalogManager
 */
public class BootstrapResolver implements EntityResolver, URIResolver {
    /**
     * URI of the W3C XML Schema for OASIS XML Catalog files.
     */
    public static final String xmlCatalogXSD = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd";

    /**
     * URI of the RELAX NG Grammar for OASIS XML Catalog files.
     */
    public static final String xmlCatalogRNG = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng";

    /**
     * Public identifier for OASIS XML Catalog files.
     */
    public static final String xmlCatalogPubId = "-//OASIS//DTD XML Catalogs V1.0//EN";

    /**
     * System identifier for OASIS XML Catalog files.
     */
    public static final String xmlCatalogSysId = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";

    /**
     * Public identifier for legacy Apache XCatalog files.  There is no official system identifier for XCatalog files.
     */
    public static final String xCatalogPubId = "-//DTD XCatalog//EN";

    /**
     * Private hash used for public identifiers.
     */
    private Hashtable publicMap = new Hashtable();

    /**
     * Private hash used for system identifiers.
     */
    private Hashtable systemMap = new Hashtable();

    /**
     * Private hash used for URIs.
     */
    private Hashtable uriMap = new Hashtable();

    /**
     * Constructor.
     */
    public BootstrapResolver() {
        URL url = this.getClass().getResource("/org/apache/xml/resolver/etc/catalog.dtd");
        if (url != null) {
            publicMap.put(xmlCatalogPubId, url.toString());
            systemMap.put(xmlCatalogSysId, url.toString());
        }

        url = this.getClass().getResource("/org/apache/xml/resolver/etc/catalog.rng");
        if (url != null) {
            uriMap.put(xmlCatalogRNG, url.toString());
        }

        url = this.getClass().getResource("/org/apache/xml/resolver/etc/catalog.xsd");
        if (url != null) {
            uriMap.put(xmlCatalogXSD, url.toString());
        }

        url = this.getClass().getResource("/org/apache/xml/resolver/etc/xcatalog.dtd");
        if (url != null) {
            publicMap.put(xCatalogPubId, url.toString());
        }
    }

    /**
     * SAX resolveEntity API.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        String resolved = null;

        if (systemId != null && systemMap.containsKey(systemId)) {
            resolved = (String) systemMap.get(systemId);
        } else if (publicId != null && publicMap.containsKey(publicId)) {
            resolved = (String) publicMap.get(publicId);
        }

        if (resolved != null) {
            try {
                InputSource iSource = new InputSource(resolved);
                iSource.setPublicId(publicId);

                // Ideally this method would not attempt to open the
                // InputStream, but there is a bug (in Xerces, at least)
                // that causes the parser to mistakenly open the wrong
                // system identifier if the returned InputSource does
                // not have a byteStream.
                //
                // It could be argued that we still shouldn't do this here,
                // but since the purpose of calling the entityResolver is
                // almost certainly to open the input stream, it seems to
                // do little harm.
                //
                URL url = new URL(resolved);
                InputStream iStream = url.openStream();
                iSource.setByteStream(iStream);

                return iSource;
            } catch (Exception e) {
                // FIXME: silently fail?
                return null;
            }
        }

        return null;
    }

    /**
     * Transformer resolve API.
     */
    @Override
    public Source resolve(String href, String base)
            throws TransformerException {

        String uri = href;
        String fragment = null;
        int hashPos = href.indexOf("#");
        if (hashPos >= 0) {
            uri = href.substring(0, hashPos);
            fragment = href.substring(hashPos + 1);
        }

        String result = null;
        if (href != null && uriMap.containsKey(href)) {
            result = (String) uriMap.get(href);
        }

        if (result == null) {
            try {
                URL url = null;

                if (base == null) {
                    url = new URL(uri);
                    result = url.toString();
                } else {
                    URL baseURL = new URL(base);
                    url = (href.length() == 0 ? baseURL : new URL(baseURL, uri));
                    result = url.toString();
                }
            } catch (java.net.MalformedURLException mue) {
                // try to make an absolute URI from the current base
                String absBase = makeAbsolute(base);
                if (!absBase.equals(base)) {
                    // don't bother if the absBase isn't different!
                    return resolve(href, absBase);
                } else {
                    throw new TransformerException("Malformed URL "
                            + href + "(base " + base + ")",
                            mue);
                }
            }
        }

        SAXSource source = new SAXSource();
        source.setInputSource(new InputSource(result));
        return source;
    }

    /**
     * Attempt to construct an absolute URI
     */
    private String makeAbsolute(String uri) {
        if (uri == null) {
            uri = "";
        }

        try {
            URL url = new URL(uri);
            return url.toString();
        } catch (MalformedURLException mue) {
            try {
                URL fileURL = FileURL.makeURL(uri);
                return fileURL.toString();
            } catch (MalformedURLException mue2) {
                // bail
                return uri;
            }
        }
    }
}

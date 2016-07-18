// ResolvingXMLFilter.java - An XMLFilter that performs catalog resolution

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

package mf.org.apache.xml.resolver.tools;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import mf.org.apache.xml.resolver.Catalog;
import mf.org.apache.xml.resolver.CatalogManager;
import mf.org.apache.xml.resolver.helpers.FileURL;


/**
 * A SAX XMLFilter that performs catalog-based entity resolution.
 * <p/>
 * <p>This class implements a SAX XMLFilter that performs entity resolution
 * using the CatalogResolver. The actual, underlying parser is obtained
 * from a SAXParserFactory.</p>
 * </p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 * @see CatalogResolver
 * @see org.xml.sax.XMLFilter
 */
public class ResolvingXMLFilter extends XMLFilterImpl {
    /**
     * Suppress explanatory message?
     *
     * @see #parse(InputSource)
     */
    public static boolean suppressExplanation = false;

    /**
     * The manager for the underlying resolver.
     */
    private CatalogManager catalogManager = CatalogManager.getStaticManager();

    /**
     * The underlying catalog resolver.
     */
    private CatalogResolver catalogResolver = null;

    /**
     * A separate resolver for oasis-xml-pi catalogs.
     */
    private CatalogResolver piCatalogResolver = null;

    /**
     * Are we in the prolog? Is an oasis-xml-catalog PI valid now?
     */
    private boolean allowXMLCatalogPI = false;

    /**
     * Has an oasis-xml-catalog PI been seen?
     */
    private boolean oasisXMLCatalogPI = false;

    /**
     * The base URI of the input document, if known.
     */
    private URL baseURL = null;

    /**
     * Construct an empty XML Filter with no parent.
     */
    public ResolvingXMLFilter() {
        super();
        catalogResolver = new CatalogResolver(catalogManager);
    }

    /**
     * Construct an XML filter with the specified parent.
     */
    public ResolvingXMLFilter(XMLReader parent) {
        super(parent);
        catalogResolver = new CatalogResolver(catalogManager);
    }

    /**
     * Construct an XML filter with the specified parent.
     */
    public ResolvingXMLFilter(CatalogManager manager) {
        super();
        catalogManager = manager;
        catalogResolver = new CatalogResolver(catalogManager);
    }

    /**
     * Construct an XML filter with the specified parent.
     */
    public ResolvingXMLFilter(XMLReader parent, CatalogManager manager) {
        super(parent);
        catalogManager = manager;
        catalogResolver = new CatalogResolver(catalogManager);
    }

    /**
     * Provide accessto the underlying Catalog.
     */
    public Catalog getCatalog() {
        return catalogResolver.getCatalog();
    }

    /**
     * SAX XMLReader API.
     * <p/>
     * <p>Note that the JAXP 1.1ea2 parser crashes with an InternalError if
     * it encounters a system identifier that appears to be a relative URI
     * that begins with a slash. For example, the declaration:</p>
     * <p/>
     * <pre>
     * &lt;!DOCTYPE book SYSTEM "/path/to/dtd/on/my/system/docbookx.dtd">
     * </pre>
     * <p/>
     * <p>would cause such an error. As a convenience, this method catches
     * that error and prints an explanation. (Unfortunately, it's not possible
     * to identify the particular system identifier that causes the problem.)
     * </p>
     * <p/>
     * <p>The underlying error is forwarded after printing the explanatory
     * message. The message is only every printed once and if
     * <code>suppressExplanation</code> is set to <code>false</code> before
     * parsing, it will never be printed.</p>
     */
    @Override
    public void parse(InputSource input)
            throws IOException, SAXException {
        allowXMLCatalogPI = true;

        setupBaseURI(input.getSystemId());

        try {
            super.parse(input);
        } catch (InternalError ie) {
            explain(input.getSystemId());
            throw ie;
        }
    }

    /**
     * SAX XMLReader API.
     *
     * @see #parse(InputSource)
     */
    @Override
    public void parse(String systemId)
            throws IOException, SAXException {
        allowXMLCatalogPI = true;

        setupBaseURI(systemId);

        try {
            super.parse(systemId);
        } catch (InternalError ie) {
            explain(systemId);
            throw ie;
        }
    }

    /**
     * Implements the <code>resolveEntity</code> method
     * for the SAX interface, using an underlying CatalogResolver
     * to do the real work.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        allowXMLCatalogPI = false;
        String resolved = catalogResolver.getResolvedEntity(publicId, systemId);

        if (resolved == null && piCatalogResolver != null) {
            resolved = piCatalogResolver.getResolvedEntity(publicId, systemId);
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
                catalogManager.debug.message(1,
                        "Failed to create InputSource ("
                                + e.toString()
                                + ")", resolved);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * SAX DTDHandler API.
     * <p/>
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    @Override
    public void notationDecl(String name, String publicId, String systemId)
            throws SAXException {
        allowXMLCatalogPI = false;
        super.notationDecl(name, publicId, systemId);
    }

    /**
     * SAX DTDHandler API.
     * <p/>
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    @Override
    public void unparsedEntityDecl(String name,
                                   String publicId,
                                   String systemId,
                                   String notationName)
            throws SAXException {
        allowXMLCatalogPI = false;
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    /**
     * SAX ContentHandler API.
     * <p/>
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts)
            throws SAXException {
        allowXMLCatalogPI = false;
        super.startElement(uri, localName, qName, atts);
    }

    /**
     * SAX ContentHandler API.
     * <p/>
     * <p>Detect and use the oasis-xml-catalog PI if it occurs.</p>
     */
    @Override
    public void processingInstruction(String target, String pidata)
            throws SAXException {
        if (target.equals("oasis-xml-catalog")) {
            URL catalog = null;
            String data = pidata;

            int pos = data.indexOf("catalog=");
            if (pos >= 0) {
                data = data.substring(pos + 8);
                if (data.length() > 1) {
                    String quote = data.substring(0, 1);
                    data = data.substring(1);
                    pos = data.indexOf(quote);
                    if (pos >= 0) {
                        data = data.substring(0, pos);
                        try {
                            if (baseURL != null) {
                                catalog = new URL(baseURL, data);
                            } else {
                                catalog = new URL(data);
                            }
                        } catch (MalformedURLException mue) {
                            // nevermind
                        }
                    }
                }
            }

            if (allowXMLCatalogPI) {
                if (catalogManager.getAllowOasisXMLCatalogPI()) {
                    catalogManager.debug.message(4, "oasis-xml-catalog PI", pidata);

                    if (catalog != null) {
                        try {
                            catalogManager.debug.message(4, "oasis-xml-catalog", catalog.toString());
                            oasisXMLCatalogPI = true;

                            if (piCatalogResolver == null) {
                                piCatalogResolver = new CatalogResolver(true);
                            }

                            piCatalogResolver.getCatalog().parseCatalog(catalog.toString());
                        } catch (Exception e) {
                            catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: "
                                    + catalog.toString());
                        }
                    } else {
                        catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + pidata);
                    }
                } else {
                    catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + pidata);
                }
            } else {
                catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: "
                        + pidata);
            }
        } else {
            super.processingInstruction(target, pidata);
        }
    }

    /**
     * Save the base URI of the document being parsed.
     */
    private void setupBaseURI(String systemId) {
        URL cwd = null;

        try {
            cwd = FileURL.makeURL("basename");
        } catch (MalformedURLException mue) {
            cwd = null;
        }

        try {
            baseURL = new URL(systemId);
        } catch (MalformedURLException mue) {
            if (cwd != null) {
                try {
                    baseURL = new URL(cwd, systemId);
                } catch (MalformedURLException mue2) {
                    // give up
                    baseURL = null;
                }
            } else {
                // give up
                baseURL = null;
            }
        }
    }

    /**
     * Provide one possible explanation for an InternalError.
     */
    private void explain(String systemId) {
        if (!suppressExplanation) {
            System.out.println("XMLReader probably encountered bad URI in " + systemId);
            System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
        }
        suppressExplanation = true;
    }
}


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
 * $Id: DocumentBuilder.java,v 1.8 2010-11-01 04:36:09 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.parsers;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import mf.javax.xml.validation.Schema;
import mf.org.w3c.dom.DOMImplementation;
import mf.org.w3c.dom.Document;

/**
 * Defines the API to obtain DOM Document instances from an XML
 * document. Using this class, an application programmer can obtain a
 * {@link Document} from XML.<p>
 * <p/>
 * An instance of this class can be obtained from the
 * {@link DocumentBuilderFactory#newDocumentBuilder()} method. Once
 * an instance of this class is obtained, XML can be parsed from a
 * variety of input sources. These input sources are InputStreams,
 * Files, URLs, and SAX InputSources.<p>
 * <p/>
 * Note that this class reuses several classes from the SAX API. This
 * does not require that the implementor of the underlying DOM
 * implementation use a SAX parser to parse XML document into a
 * <code>Document</code>. It merely requires that the implementation
 * communicate with the application using these existing APIs.
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.8 $, $Date: 2010-11-01 04:36:09 $
 */

public abstract class DocumentBuilder {


    /**
     * Protected constructor
     */
    protected DocumentBuilder() {
    }

    /**
     * <p>Reset this <code>DocumentBuilder</code> to its original configuration.</p>
     * <p/>
     * <p><code>DocumentBuilder</code> is reset to the same state as when it was created with
     * {@link DocumentBuilderFactory#newDocumentBuilder()}.
     * <code>reset()</code> is designed to allow the reuse of existing <code>DocumentBuilder</code>s
     * thus saving resources associated with the creation of new <code>DocumentBuilder</code>s.</p>
     * <p/>
     * <p>The reset <code>DocumentBuilder</code> is not guaranteed to have the same {@link EntityResolver} or {@link ErrorHandler}
     * <code>Object</code>s, e.g. {@link Object#equals(Object obj)}.  It is guaranteed to have a functionally equal
     * <code>EntityResolver</code> and <code>ErrorHandler</code>.</p>
     *
     * @throws UnsupportedOperationException When implementation does not
     *                                       override this method.
     * @since 1.5
     */
    public void reset() {

        // implementors should override this method
        throw new UnsupportedOperationException(
                "This DocumentBuilder, \"" + this.getClass().getName() + "\", does not support the reset functionality."
                        + "  Specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\""
                        + " version \"" + this.getClass().getPackage().getSpecificationVersion() + "\""
        );
    }

    /**
     * Parse the content of the given <code>InputStream</code> as an XML
     * document and return a new DOM {@link Document} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * <code>InputStream</code> is null.
     *
     * @param is InputStream containing the content to be parsed.
     * @return <code>Document</code> result of parsing the
     * <code>InputStream</code>
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
     * @see org.xml.sax.DocumentHandler
     */

    public Document parse(InputStream is)
            throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        InputSource in = new InputSource(is);
        return parse(in);
    }

    /**
     * Parse the content of the given <code>InputStream</code> as an
     * XML document and return a new DOM {@link Document} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * <code>InputStream</code> is null.
     *
     * @param is       InputStream containing the content to be parsed.
     * @param systemId Provide a base for resolving relative URIs.
     * @return A new DOM Document object.
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
     * @see org.xml.sax.DocumentHandler
     */

    public Document parse(InputStream is, String systemId)
            throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        InputSource in = new InputSource(is);
        in.setSystemId(systemId);
        return parse(in);
    }

    /**
     * Parse the content of the given URI as an XML document
     * and return a new DOM {@link Document} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * URI is <code>null</code> null.
     *
     * @param uri The location of the content to be parsed.
     * @return A new DOM Document object.
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws IllegalArgumentException When <code>uri</code> is <code>null</code>
     * @see org.xml.sax.DocumentHandler
     */

    public Document parse(String uri)
            throws SAXException, IOException {
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }

        InputSource in = new InputSource(uri);
        return parse(in);
    }

    /**
     * Parse the content of the given file as an XML document
     * and return a new DOM {@link Document} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * <code>File</code> is <code>null</code> null.
     *
     * @param f The file containing the XML to parse.
     * @return A new DOM Document object.
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws IllegalArgumentException When <code>f</code> is <code>null</code>
     * @see org.xml.sax.DocumentHandler
     */

    public Document parse(File f) throws SAXException, IOException {
        if (f == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        //convert file to appropriate URI, f.toURI().toASCIIString()
        //converts the URI to string as per rule specified in
        //RFC 2396,
        InputSource in = new InputSource(f.toURI().toASCIIString());
        return parse(in);
    }

    /**
     * Parse the content of the given input source as an XML document
     * and return a new DOM {@link Document} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * <code>InputSource</code> is <code>null</code> null.
     *
     * @param is InputSource containing the content to be parsed.
     * @return A new DOM Document object.
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
     * @see org.xml.sax.DocumentHandler
     */

    public abstract Document parse(InputSource is)
            throws SAXException, IOException;


    /**
     * Indicates whether or not this parser is configured to
     * understand namespaces.
     *
     * @return true if this parser is configured to understand
     * namespaces; false otherwise.
     */

    public abstract boolean isNamespaceAware();

    /**
     * Indicates whether or not this parser is configured to
     * validate XML documents.
     *
     * @return true if this parser is configured to validate
     * XML documents; false otherwise.
     */

    public abstract boolean isValidating();

    /**
     * Specify the {@link EntityResolver} to be used to resolve
     * entities present in the XML document to be parsed. Setting
     * this to <code>null</code> will result in the underlying
     * implementation using it's own default implementation and
     * behavior.
     *
     * @param er The <code>EntityResolver</code> to be used to resolve entities
     *           present in the XML document to be parsed.
     */

    public abstract void setEntityResolver(EntityResolver er);

    /**
     * Specify the {@link ErrorHandler} to be used by the parser.
     * Setting this to <code>null</code> will result in the underlying
     * implementation using it's own default implementation and
     * behavior.
     *
     * @param eh The <code>ErrorHandler</code> to be used by the parser.
     */

    public abstract void setErrorHandler(ErrorHandler eh);

    /**
     * Obtain a new instance of a DOM {@link Document} object
     * to build a DOM tree with.
     *
     * @return A new instance of a DOM Document object.
     */

    public abstract Document newDocument();

    /**
     * Obtain an instance of a {@link DOMImplementation} object.
     *
     * @return A new instance of a <code>DOMImplementation</code>.
     */

    public abstract DOMImplementation getDOMImplementation();

    /** <p>Get current state of canonicalization.</p>
     *
     * @return current state canonicalization control
     */
    /*
    public boolean getCanonicalization() {
        return canonicalState;
    }
    */

    /**
     * <p>Get a reference to the the {@link Schema} being used by
     * the XML processor.</p>
     * <p/>
     * <p>If no schema is being used, <code>null</code> is returned.</p>
     *
     * @return {@link Schema} being used or <code>null</code>
     * if none in use
     * @throws UnsupportedOperationException When implementation does not
     *                                       override this method
     * @since 1.5
     */
    public Schema getSchema() {
        throw new UnsupportedOperationException(
                "This parser does not support specification \""
                        + this.getClass().getPackage().getSpecificationTitle()
                        + "\" version \""
                        + this.getClass().getPackage().getSpecificationVersion()
                        + "\""
        );
    }


    /**
     * <p>Get the XInclude processing mode for this parser.</p>
     *
     * @return the return value of
     * the {@link DocumentBuilderFactory#isXIncludeAware()}
     * when this parser was created from factory.
     * @throws UnsupportedOperationException When implementation does not
     *                                       override this method
     * @see DocumentBuilderFactory#setXIncludeAware(boolean)
     * @since 1.5
     */
    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException(
                "This parser does not support specification \""
                        + this.getClass().getPackage().getSpecificationTitle()
                        + "\" version \""
                        + this.getClass().getPackage().getSpecificationVersion()
                        + "\""
        );
    }
}

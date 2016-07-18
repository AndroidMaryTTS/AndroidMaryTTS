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
 * $Id: OutputKeys.java,v 1.5 2010-11-01 04:36:11 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform;

/**
 * Provides string constants that can be used to set
 * output properties for a Transformer, or to retrieve
 * output properties from a Transformer or Templates object.
 * <p>All the fields in this class are read-only.</p>
 *
 * @see <a href="http://www.w3.org/TR/xslt#output">
 * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
 */
public class OutputKeys {

    /**
     * method = "xml" | "html" | "text" | <var>expanded name</var>.
     * <p/>
     * <p>The value of the method property identifies the overall method that
     * should be used for outputting the result tree.  Other non-namespaced
     * values may be used, such as "xhtml", but, if accepted, the handling
     * of such values is implementation defined.  If any of the method values
     * are not accepted and are not namespace qualified,
     * then {@link javax.xml.transform.Transformer#setOutputProperty}
     * or {@link javax.xml.transform.Transformer#setOutputProperties} will
     * throw a {@link java.lang.IllegalArgumentException}.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String METHOD = "method";
    /**
     * version = <var>nmtoken</var>.
     * <p/>
     * <p><code>version</code> specifies the version of the output
     * method.</p>
     * <p>When the output method is "xml", the version value specifies the
     * version of XML to be used for outputting the result tree. The default
     * value for the xml output method is 1.0. When the output method is
     * "html", the version value indicates the version of the HTML.
     * The default value for the xml output method is 4.0, which specifies
     * that the result should be output as HTML conforming to the HTML 4.0
     * Recommendation [HTML].  If the output method is "text", the version
     * property is ignored.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String VERSION = "version";
    /**
     * encoding = <var>string</var>.
     * <p/>
     * <p><code>encoding</code> specifies the preferred character
     * encoding that the Transformer should use to encode sequences of
     * characters as sequences of bytes. The value of the encoding property should be
     * treated case-insensitively. The value must only contain characters in
     * the range #x21 to #x7E (i.e., printable ASCII characters). The value
     * should either be a <code>charset</code> registered with the Internet
     * Assigned Numbers Authority <a href="http://www.iana.org/">[IANA]</a>,
     * <a href="http://www.ietf.org/rfc/rfc2278.txt">[RFC2278]</a>
     * or start with <code>X-</code>.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String ENCODING = "encoding";
    /**
     * omit-xml-declaration = "yes" | "no".
     * <p/>
     * <p><code>omit-xml-declaration</code> specifies whether the XSLT
     * processor should output an XML declaration; the value must be
     * <code>yes</code> or <code>no</code>.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String OMIT_XML_DECLARATION = "omit-xml-declaration";
    /**
     * standalone = "yes" | "no".
     * <p/>
     * <p><code>standalone</code> specifies whether the Transformer
     * should output a standalone document declaration; the value must be
     * <code>yes</code> or <code>no</code>.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String STANDALONE = "standalone";
    /**
     * doctype-public = <var>string</var>.
     * <p>See the documentation for the {@link #DOCTYPE_SYSTEM} property
     * for a description of what the value of the key should be.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String DOCTYPE_PUBLIC = "doctype-public";
    /**
     * doctype-system = <var>string</var>.
     * <p><code>doctype-system</code> specifies the system identifier
     * to be used in the document type declaration.</p>
     * <p>If the doctype-system property is specified, the xml output method
     * should output a document type declaration immediately before the first
     * element. The name following &lt;!DOCTYPE should be the name of the first
     * element. If doctype-public property is also specified, then the xml
     * output method should output PUBLIC followed by the public identifier
     * and then the system identifier; otherwise, it should output SYSTEM
     * followed by the system identifier. The internal subset should be empty.
     * The value of the doctype-public property should be ignored unless the doctype-system
     * property is specified.</p>
     * <p>If the doctype-public or doctype-system properties are specified,
     * then the html output method should output a document type declaration
     * immediately before the first element. The name following &lt;!DOCTYPE
     * should be HTML or html. If the doctype-public property is specified,
     * then the output method should output PUBLIC followed by the specified
     * public identifier; if the doctype-system property is also specified,
     * it should also output the specified system identifier following the
     * public identifier. If the doctype-system property is specified but
     * the doctype-public property is not specified, then the output method
     * should output SYSTEM followed by the specified system identifier.</p>
     * <p/>
     * <p><code>doctype-system</code> specifies the system identifier
     * to be used in the document type declaration.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String DOCTYPE_SYSTEM = "doctype-system";
    /**
     * cdata-section-elements = <var>expanded names</var>.
     * <p/>
     * <p><code>cdata-section-elements</code> specifies a whitespace delimited
     * list of the names of elements whose text node children should be output
     * using CDATA sections. Note that these names must use the format
     * described in the section Qualfied Name Representation in
     * {@link javax.xml.transform}.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation.</a>
     */
    public static final String CDATA_SECTION_ELEMENTS =
            "cdata-section-elements";
    /**
     * indent = "yes" | "no".
     * <p/>
     * <p><code>indent</code> specifies whether the Transformer may
     * add additional whitespace when outputting the result tree; the value
     * must be <code>yes</code> or <code>no</code>.  </p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">
     * section 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String INDENT = "indent";
    /**
     * media-type = <var>string</var>.
     * <p/>
     * <p><code>media-type</code> specifies the media type (MIME
     * content type) of the data that results from outputting the result
     * tree. The <code>charset</code> parameter should not be specified
     * explicitly; instead, when the top-level media type is
     * <code>text</code>, a <code>charset</code> parameter should be added
     * according to the character encoding actually used by the output
     * method.  </p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#output">s
     * ection 16 of the XSL Transformations (XSLT) W3C Recommendation</a>
     */
    public static final String MEDIA_TYPE = "media-type";

    /**
     * Default constructor is private on purpose.  This class is
     * only for static variable access, and should never be constructed.
     */
    private OutputKeys() {
    }
}

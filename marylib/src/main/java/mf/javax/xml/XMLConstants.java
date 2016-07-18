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
 * $Id: XMLConstants.java,v 1.9 2010-11-01 04:36:08 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml;

/**
 * <p>Utility class to contain basic XML values as constants.</p>
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.9 $, $Date: 2010-11-01 04:36:08 $
 * @see <a href="http://www.w3.org/TR/xml11/">Extensible Markup Language (XML) 1.1</a>
 * @see <a href="http://www.w3.org/TR/REC-xml">Extensible Markup Language (XML) 1.0 (Second Edition)</a>
 * @see <a href="http://www.w3.org/XML/xml-V10-2e-errata">XML 1.0 Second Edition Specification Errata</a>
 * @see <a href="http://www.w3.org/TR/xml-names11/">Namespaces in XML 1.1</a>
 * @see <a href="http://www.w3.org/TR/REC-xml-names">Namespaces in XML</a>
 * @see <a href="http://www.w3.org/XML/xml-names-19990114-errata">Namespaces in XML Errata</a>
 * @see <a href="http://www.w3.org/TR/xmlschema-1/">XML Schema Part 1: Structures</a>
 * @since 1.5
 **/

public final class XMLConstants {

    /**
     * <p>Namespace URI to use to represent that there is no Namespace.</p>
     * <p/>
     * <p>Defined by the Namespace specification to be "".</p>
     *
     * @see <a href="http://www.w3.org/TR/REC-xml-names/#defaulting">
     * Namespaces in XML, 5.2 Namespace Defaulting</a>
     */
    public static final String NULL_NS_URI = "";
    /**
     * <p>Prefix to use to represent the default XML Namespace.</p>
     * <p/>
     * <p>Defined by the XML specification to be "".</p>
     *
     * @see <a
     * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     * Namespaces in XML, 3. Qualified Names</a>
     */
    public static final String DEFAULT_NS_PREFIX = "";
    /**
     * <p>The official XML Namespace name URI.</p>
     * <p/>
     * <p>Defined by the XML specification to be
     * "<code>http://www.w3.org/XML/1998/namespace</code>".</p>
     *
     * @see <a
     * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     * Namespaces in XML, 3. Qualified Names</a>
     */
    public static final String XML_NS_URI =
            "http://www.w3.org/XML/1998/namespace";
    /**
     * <p>The official XML Namespace prefix.</p>
     * <p/>
     * <p>Defined by the XML specification to be "<code>xml</code>".</p>
     *
     * @see <a
     * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     * Namespaces in XML, 3. Qualified Names<</a>
     */
    public static final String XML_NS_PREFIX = "xml";
    /**
     * <p>The official XML attribute used for specifying XML Namespace
     * declarations, {@link #XMLNS_ATTRIBUTE
     * XMLConstants.XMLNS_ATTRIBUTE}, Namespace name URI.</p>
     * <p/>
     * <p>Defined by the XML specification to be
     * "<code>http://www.w3.org/2000/xmlns/</code>".</p>
     *
     * @see <a
     * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     * Namespaces in XML, 3. Qualified Names</a>
     * @see <a
     * href="http://www.w3.org/XML/xml-names-19990114-errata">
     * Namespaces in XML Errata</a>
     */
    public static final String XMLNS_ATTRIBUTE_NS_URI =
            "http://www.w3.org/2000/xmlns/";
    /**
     * <p>The official XML attribute used for specifying XML Namespace
     * declarations.</p>
     * <p/>
     * <p>It is <strong><em>NOT</em></strong> valid to use as a
     * prefix.  Defined by the XML specification to be
     * "<code>xmlns</code>".</p>
     *
     * @see <a
     * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     * Namespaces in XML, 3. Qualified Names</a>
     */
    public static final String XMLNS_ATTRIBUTE = "xmlns";
    /**
     * <p>W3C XML Schema Namespace URI.</p>
     * <p/>
     * <p>Defined to be "<code>http://www.w3.org/2001/XMLSchema</code>".
     *
     * @see <a href=
     * "http://www.w3.org/TR/xmlschema-1/#Instance_Document_Constructions">
     * XML Schema Part 1:
     * Structures, 2.6 Schema-Related Markup in Documents Being Validated</a>
     */
    public static final String W3C_XML_SCHEMA_NS_URI =
            "http://www.w3.org/2001/XMLSchema";
    /**
     * <p>W3C XML Schema Instance Namespace URI.</p>
     * <p/>
     * <p>Defined to be "<code>http://www.w3.org/2001/XMLSchema-instance</code>".</p>
     *
     * @see <a href=
     * "http://www.w3.org/TR/xmlschema-1/#Instance_Document_Constructions">
     * XML Schema Part 1:
     * Structures, 2.6 Schema-Related Markup in Documents Being Validated</a>
     */
    public static final String W3C_XML_SCHEMA_INSTANCE_NS_URI =
            "http://www.w3.org/2001/XMLSchema-instance";
    /**
     * <p>W3C XPath Datatype Namespace URI.</p>
     * <p/>
     * <p>Defined to be "<code>http://www.w3.org/2003/11/xpath-datatypes</code>".</p>
     *
     * @see <a href="http://www.w3.org/TR/xpath-datamodel">XQuery 1.0 and XPath 2.0 Data Model</a>
     */
    public static final String W3C_XPATH_DATATYPE_NS_URI = "http://www.w3.org/2003/11/xpath-datatypes";
    /**
     * <p>XML Document Type Declaration Namespace URI as an arbitrary value.</p>
     * <p/>
     * <p>Since not formally defined by any existing standard, arbitrarily define to be "<code>http://www.w3.org/TR/REC-xml</code>".
     */
    public static final String XML_DTD_NS_URI = "http://www.w3.org/TR/REC-xml";
    /**
     * <p>RELAX NG Namespace URI.</p>
     * <p/>
     * <p>Defined to be "<code>http://relaxng.org/ns/structure/1.0</code>".</p>
     *
     * @see <a href="http://relaxng.org/spec-20011203.html">RELAX NG Specification</a>
     */
    public static final String RELAXNG_NS_URI = "http://relaxng.org/ns/structure/1.0";
    /**
     * <p>Feature for secure processing.</p>
     * <p/>
     * <ul>
     * <li>
     * <code>true</code> instructs the implementation to process XML securely.
     * This may set limits on XML constructs to avoid conditions such as denial of service attacks.
     * </li>
     * <li>
     * <code>false</code> instructs the implementation to process XML acording the letter of the XML specifications
     * ingoring security issues such as limits on XML constructs to avoid conditions such as denial of service attacks.
     * </li>
     * </ul>
     */
    public static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";

    /**
     * <p>Private constructor to prevent instantiation.</p>
     */
    private XMLConstants() {
    }
}

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
 * $Id: XPathConstants.java,v 1.5 2010-11-01 04:36:13 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.xpath;

import mf.javax.xml.namespace.QName;

/**
 * <p>XPath constants.</p>
 *
 * @author <a href="mailto:Norman.Walsh@Sun.COM">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.COM">Jeff Suttor</a>
 * @version $Revision: 1.5 $, $Date: 2010-11-01 04:36:13 $
 * @see <a href="http://www.w3.org/TR/xpath">XML Path Language (XPath) Version 1.0</a>
 * @since 1.5
 */
public class XPathConstants {

    /**
     * <p>The XPath 1.0 number data type.</p>
     * <p/>
     * <p>Maps to Java {@link Double}.</p>
     */
    public static final QName NUMBER = new QName("http://www.w3.org/1999/XSL/Transform", "NUMBER");
    /**
     * <p>The XPath 1.0 string data type.</p>
     * <p/>
     * <p>Maps to Java {@link String}.</p>
     */
    public static final QName STRING = new QName("http://www.w3.org/1999/XSL/Transform", "STRING");
    /**
     * <p>The XPath 1.0 boolean data type.</p>
     * <p/>
     * <p>Maps to Java {@link Boolean}.</p>
     */
    public static final QName BOOLEAN = new QName("http://www.w3.org/1999/XSL/Transform", "BOOLEAN");
    /**
     * <p>The XPath 1.0 NodeSet data type.</p>
     * <p/>
     * <p>Maps to Java {@link org.w3c.dom.NodeList}.</p>
     */
    public static final QName NODESET = new QName("http://www.w3.org/1999/XSL/Transform", "NODESET");
    /**
     * <p>The XPath 1.0 NodeSet data type.
     * <p/>
     * <p>Maps to Java {@link org.w3c.dom.Node}.</p>
     */
    public static final QName NODE = new QName("http://www.w3.org/1999/XSL/Transform", "NODE");
    /**
     * <p>The URI for the DOM object model, "http://java.sun.com/jaxp/xpath/dom".</p>
     */
    public static final String DOM_OBJECT_MODEL = "http://java.sun.com/jaxp/xpath/dom";

    /**
     * <p>Private constructor to prevent instantiation.</p>
     */
    private XPathConstants() {
    }
}

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
 * $Id: XPathFunctionResolver.java,v 1.5 2010-11-01 04:36:14 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.xpath;

import mf.javax.xml.namespace.QName;

/**
 * <p><code>XPathFunctionResolver</code> provides access to the set of user defined <code>XPathFunction</code>s.</p>
 * <p/>
 * <p>XPath functions are resolved by name and arity.
 * The resolver is not needed for XPath built-in functions and the resolver
 * <strong><em>cannot</em></strong> be used to override those functions.</p>
 * <p/>
 * <p>In particular, the resolver is only called for functions in an another
 * namespace (functions with an explicit prefix). This means that you cannot
 * use the <code>XPathFunctionResolver</code> to implement specifications
 * like <a href="http://www.w3.org/TR/xmldsig-core/">XML-Signature Syntax
 * and Processing</a> which extend the function library of XPath 1.0 in the
 * same namespace. This is a consequence of the design of the resolver.</p>
 * <p/>
 * <p>If you wish to implement additional built-in functions, you will have to
 * extend the underlying implementation directly.</p>
 *
 * @author <a href="mailto:Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.5 $, $Date: 2010-11-01 04:36:14 $
 * @see <a href="http://www.w3.org/TR/xpath#corelib">XML Path Language (XPath) Version 1.0, Core Function Library</a>
 * @since 1.5
 */
public interface XPathFunctionResolver {
    /**
     * <p>Find a function in the set of available functions.</p>
     * <p/>
     * <p>If <code>functionName</code> or <code>arity</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param functionName The function name.
     * @param arity        The number of arguments that the returned function must accept.
     * @return The function or <code>null</code> if no function named <code>functionName</code> with <code>arity</code> arguments exists.
     * @throws NullPointerException If <code>functionName</code> or <code>arity</code> is <code>null</code>.
     */
    XPathFunction resolveFunction(QName functionName, int arity);
}

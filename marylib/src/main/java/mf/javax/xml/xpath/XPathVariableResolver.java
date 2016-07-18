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
 * $Id: XPathVariableResolver.java,v 1.6 2010-11-01 04:36:14 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.xpath;

import mf.javax.xml.namespace.QName;

/**
 * <p><code>XPathVariableResolver</code> provides access to the set of user defined XPath variables.</p>
 * <p/>
 * <p>The <code>XPathVariableResolver</code> and the XPath evaluator must adhere to a contract that
 * cannot be directly enforced by the API.  Although variables may be mutable,
 * that is, an application may wish to evaluate the same XPath expression more
 * than once with different variable values, in the course of evaluating any
 * single XPath expression, a variable's value <strong><em>must</em></strong>
 * not change.</p>
 *
 * @author <a href="mailto:Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.6 $, $Date: 2010-11-01 04:36:14 $
 * @since 1.5
 */
public interface XPathVariableResolver {
    /**
     * <p>Find a variable in the set of available variables.</p>
     * <p/>
     * <p>If <code>variableName</code> is <code>null</code>, then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param variableName The <code>QName</code> of the variable name.
     * @return The variables value, or <code>null</code> if no variable named <code>variableName</code>
     * exists.  The value returned must be of a type appropriate for the underlying object model.
     * @throws NullPointerException If <code>variableName</code> is <code>null</code>.
     */
    Object resolveVariable(QName variableName);
}

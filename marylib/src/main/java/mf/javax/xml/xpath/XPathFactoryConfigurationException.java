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
 * $Id: XPathFactoryConfigurationException.java,v 1.5 2010-11-01 04:36:14 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.xpath;

/**
 * <code>XPathFactoryConfigurationException</code> represents a configuration error in a <code>XPathFactory</code> environment.</p>
 *
 * @author <a href="mailto:Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.5 $, $Date: 2010-11-01 04:36:14 $
 * @since 1.5
 */
public class XPathFactoryConfigurationException extends XPathException {

    /**
     * <p>Stream Unique Identifier.</p>
     */
    private static final long serialVersionUID = -1837080260374986980L;

    /**
     * <p>Constructs a new <code>XPathFactoryConfigurationException</code> with the specified detail <code>message</code>.</p>
     * <p/>
     * <p>The <code>cause</code> is not initialized.</p>
     * <p/>
     * <p>If <code>message</code> is <code>null</code>,
     * then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param message The detail message.
     * @throws NullPointerException When <code>message</code> is
     *                              <code>null</code>.
     */
    public XPathFactoryConfigurationException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a new <code>XPathFactoryConfigurationException</code>
     * with the specified <code>cause</code>.</p>
     * <p/>
     * <p>If <code>cause</code> is <code>null</code>,
     * then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param cause The cause.
     * @throws NullPointerException if <code>cause</code> is <code>null</code>.
     */
    public XPathFactoryConfigurationException(Throwable cause) {
        super(cause);
    }
}

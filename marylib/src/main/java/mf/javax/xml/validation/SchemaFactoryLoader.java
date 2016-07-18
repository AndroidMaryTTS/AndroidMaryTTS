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
 * $Id: SchemaFactoryLoader.java,v 1.4 2010-11-01 04:36:13 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.validation;

/**
 * <p>Factory that creates {@link SchemaFactory}.</p>
 * <p/>
 * <p><b>DO NOT USE THIS CLASS</b></p>
 * <p/>
 * <p>
 * This class was introduced as a part of an early proposal during the
 * JSR-206 standardization process. The proposal was eventually abandoned
 * but this class accidentally remained in the source tree, and made its
 * way into the final version.
 * </p><p>
 * This class does not participate in any JAXP 1.3 or JAXP 1.4 processing.
 * It must not be used by users or JAXP implementations.
 * </p>
 *
 * @author <a href="Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.4 $, $Date: 2010-11-01 04:36:13 $
 * @since 1.5
 */
public abstract class SchemaFactoryLoader {

    /**
     * A do-nothing constructor.
     */
    protected SchemaFactoryLoader() {
    }

    /**
     * Creates a new {@link SchemaFactory} object for the specified
     * schema language.
     *
     * @param schemaLanguage See <a href="../SchemaFactory.html#schemaLanguage">
     *                       the list of available schema languages</a>.
     * @return <code>null</code> if the callee fails to create one.
     * @throws NullPointerException If the <tt>schemaLanguage</tt> parameter is null.
     */
    public abstract SchemaFactory newFactory(String schemaLanguage);
}

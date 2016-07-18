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
 * $Id: Schema.java,v 1.6 2010-11-01 04:36:13 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.validation;

/**
 * Immutable in-memory representation of grammar.
 * <p/>
 * <p/>
 * This object represents a set of constraints that can be checked/
 * enforced against an XML document.
 * <p/>
 * <p/>
 * A {@link Schema} object is thread safe and applications are
 * encouraged to share it across many parsers in many threads.
 * <p/>
 * <p/>
 * A {@link Schema} object is immutable in the sense that it shouldn't
 * change the set of constraints once it is created. In other words,
 * if an application validates the same document twice against the same
 * {@link Schema}, it must always produce the same result.
 * <p/>
 * <p/>
 * A {@link Schema} object is usually created from {@link SchemaFactory}.
 * <p/>
 * <p/>
 * Two kinds of validators can be created from a {@link Schema} object.
 * One is {@link Validator}, which provides highly-level validation
 * operations that cover typical use cases. The other is
 * {@link ValidatorHandler}, which works on top of SAX for better
 * modularity.
 * <p/>
 * <p/>
 * This specification does not refine
 * the {@link java.lang.Object#equals(java.lang.Object)} method.
 * In other words, if you parse the same schema twice, you may
 * still get <code>!schemaA.equals(schemaB)</code>.
 *
 * @author <a href="mailto:Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.6 $, $Date: 2010-11-01 04:36:13 $
 * @see <a href="http://www.w3.org/TR/xmlschema-1/">XML Schema Part 1: Structures</a>
 * @see <a href="http://www.w3.org/TR/xml11/">Extensible Markup Language (XML) 1.1</a>
 * @see <a href="http://www.w3.org/TR/REC-xml">Extensible Markup Language (XML) 1.0 (Second Edition)</a>
 * @since 1.5
 */
public abstract class Schema {

    /**
     * Constructor for the derived class.
     * <p/>
     * <p/>
     * The constructor does nothing.
     */
    protected Schema() {
    }

    /**
     * Creates a new {@link Validator} for this {@link Schema}.
     * <p/>
     * <p>A validator enforces/checks the set of constraints this object
     * represents.</p>
     * <p/>
     * <p>Implementors should assure that the properties set on the
     * {@link SchemaFactory} that created this {@link Schema} are also
     * set on the {@link Validator} constructed.</p>
     *
     * @return Always return a non-null valid object.
     */
    public abstract Validator newValidator();

    /**
     * Creates a new {@link ValidatorHandler} for this {@link Schema}.
     * <p/>
     * <p>Implementors should assure that the properties set on the
     * {@link SchemaFactory} that created this {@link Schema} are also
     * set on the {@link ValidatorHandler} constructed.</p>
     *
     * @return Always return a non-null valid object.
     */
    public abstract ValidatorHandler newValidatorHandler();
}

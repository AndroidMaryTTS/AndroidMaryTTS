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
 * $Id: TypeInfoProvider.java,v 1.6 2010-11-01 04:36:13 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.validation;

import mf.org.w3c.dom.TypeInfo;

/**
 * This class provides access to the type information determined
 * by {@link ValidatorHandler}.
 * <p/>
 * <p/>
 * Some schema languages, such as W3C XML Schema, encourages a validator
 * to report the "type" it assigns to each attribute/element.
 * Those applications who wish to access this type information can invoke
 * methods defined on this "interface" to access such type information.
 * <p/>
 * <p/>
 * Implementation of this "interface" can be obtained through the
 * {@link ValidatorHandler#getTypeInfoProvider()} method.
 *
 * @author <a href="mailto:Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.6 $, $Date: 2010-11-01 04:36:13 $
 * @see org.w3c.dom.TypeInfo
 * @since 1.5
 */
public abstract class TypeInfoProvider {

    /**
     * Constructor for the derived class.
     * <p/>
     * <p/>
     * The constructor does nothing.
     */
    protected TypeInfoProvider() {
    }

    /**
     * <p>Returns the immutable {@link TypeInfo} object for the current
     * element.</p>
     * <p/>
     * <p>The method may only be called by the startElement event
     * or the endElement event
     * of the {@link org.xml.sax.ContentHandler} that the application sets to
     * the {@link ValidatorHandler}.</p>
     * <p/>
     * <p>When W3C XML Schema validation is being performed, in the
     * case where an element has a union type, the {@link TypeInfo}
     * returned by a call to <code>getElementTypeInfo()</code> from the
     * startElement
     * event will be the union type. The <code>TypeInfo</code>
     * returned by a call
     * from the endElement event will be the actual member type used
     * to validate the element.</p>
     *
     * @return An immutable {@link TypeInfo} object that represents the
     * type of the current element.
     * Note that the caller can keep references to the obtained
     * {@link TypeInfo} longer than the callback scope.
     * <p/>
     * Otherwise, this method returns
     * null if the validator is unable to
     * determine the type of the current element for some reason
     * (for example, if the validator is recovering from
     * an earlier error.)
     * @throws IllegalStateException If this method is called from other {@link org.xml.sax.ContentHandler}
     *                               methods.
     */
    public abstract TypeInfo getElementTypeInfo();

    /**
     * Returns the immutable {@link TypeInfo} object for the specified
     * attribute of the current element.
     * <p/>
     * <p>
     * The method may only be called by the startElement event of
     * the {@link org.xml.sax.ContentHandler} that the application sets to the
     * {@link ValidatorHandler}.</p>
     *
     * @param index The index of the attribute. The same index for
     *              the {@link org.xml.sax.Attributes} object passed to the
     *              <code>startElement</code> callback.
     * @return An immutable {@link TypeInfo} object that represents the
     * type of the specified attribute.
     * Note that the caller can keep references to the obtained
     * {@link TypeInfo} longer than the callback scope.
     * <p/>
     * Otherwise, this method returns
     * null if the validator is unable to
     * determine the type.
     * @throws IndexOutOfBoundsException If the index is invalid.
     * @throws IllegalStateException     If this method is called from other {@link org.xml.sax.ContentHandler}
     *                                   methods.
     */
    public abstract TypeInfo getAttributeTypeInfo(int index);

    /**
     * Returns <code>true</code> if the specified attribute is determined
     * to be ID.
     * <p/>
     * <p/>
     * Exacly how an attribute is "determined to be ID" is up to the
     * schema language. In case of W3C XML Schema, this means
     * that the actual type of the attribute is the built-in ID type
     * or its derived type.
     * <p/>
     * <p/>
     * A {@link javax.xml.parsers.DocumentBuilder} uses this information
     * to properly implement {@link org.w3c.dom.Attr#isId()}.
     * <p/>
     * <p/>
     * The method may only be called by the startElement event of
     * the {@link org.xml.sax.ContentHandler} that the application sets to the
     * {@link ValidatorHandler}.
     *
     * @param index The index of the attribute. The same index for
     *              the {@link org.xml.sax.Attributes} object passed to the
     *              <code>startElement</code> callback.
     * @return true
     * if the type of the specified attribute is ID.
     * @throws IndexOutOfBoundsException If the index is invalid.
     * @throws IllegalStateException     If this method is called from other {@link org.xml.sax.ContentHandler}
     *                                   methods.
     */
    public abstract boolean isIdAttribute(int index);

    /**
     * Returns <code>false</code> if the attribute was added by the validator.
     * <p/>
     * <p/>
     * This method provides information necessary for
     * a {@link javax.xml.parsers.DocumentBuilder} to determine what
     * the DOM tree should return from the {@link org.w3c.dom.Attr#getSpecified()} method.
     * <p/>
     * <p/>
     * The method may only be called by the startElement event of
     * the {@link org.xml.sax.ContentHandler} that the application sets to the
     * {@link ValidatorHandler}.
     * <p/>
     * <p/>
     * A general guideline for validators is to return true if
     * the attribute was originally present in the pipeline, and
     * false if it was added by the validator.
     *
     * @param index The index of the attribute. The same index for
     *              the {@link org.xml.sax.Attributes} object passed to the
     *              <code>startElement</code> callback.
     * @return <code>true</code> if the attribute was present before the validator
     * processes input. <code>false</code> if the attribute was added
     * by the validator.
     * @throws IndexOutOfBoundsException If the index is invalid.
     * @throws IllegalStateException     If this method is called from other {@link org.xml.sax.ContentHandler}
     *                                   methods.
     */
    public abstract boolean isSpecified(int index);
}

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
 * $Id: ErrorListener.java,v 1.6 2010-11-01 04:36:11 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform;

/**
 * <p>To provide customized error handling, implement this interface and
 * use the <code>setErrorListener</code> method to register an instance of the
 * implmentation with the {@link javax.xml.transform.Transformer}.  The
 * <code>Transformer</code> then reports all errors and warnings through this
 * interface.</p>
 * <p/>
 * <p>If an application does <em>not</em> register its own custom
 * <code>ErrorListener</code>, the default <code>ErrorListener</code>
 * is used which reports all warnings and errors to <code>System.err</code>
 * and does not throw any <code>Exception</code>s.
 * Applications are <em>strongly</em> encouraged to register and use
 * <code>ErrorListener</code>s that insure proper behavior for warnings and
 * errors.</p>
 * <p/>
 * <p>For transformation errors, a <code>Transformer</code> must use this
 * interface instead of throwing an <code>Exception</code>: it is up to the
 * application to decide whether to throw an <code>Exception</code> for
 * different types of errors and warnings.  Note however that the
 * <code>Transformer</code> is not required to continue with the transformation
 * after a call to {@link #fatalError(TransformerException exception)}.</p>
 * <p/>
 * <p><code>Transformer</code>s may use this mechanism to report XML parsing
 * errors as well as transformation errors.</p>
 */
public interface ErrorListener {

    /**
     * Receive notification of a warning.
     * <p/>
     * <p>{@link javax.xml.transform.Transformer} can use this method to report
     * conditions that are not errors or fatal errors.  The default behaviour
     * is to take no action.</p>
     * <p/>
     * <p>After invoking this method, the Transformer must continue with
     * the transformation. It should still be possible for the
     * application to process the document through to the end.</p>
     *
     * @param exception The warning information encapsulated in a
     *                  transformer exception.
     * @throws javax.xml.transform.TransformerException if the application
     *                                                  chooses to discontinue the transformation.
     * @see javax.xml.transform.TransformerException
     */
    void warning(TransformerException exception)
            throws TransformerException;

    /**
     * Receive notification of a recoverable error.
     * <p/>
     * <p>The transformer must continue to try and provide normal transformation
     * after invoking this method.  It should still be possible for the
     * application to process the document through to the end if no other errors
     * are encountered.</p>
     *
     * @param exception The error information encapsulated in a
     *                  transformer exception.
     * @throws javax.xml.transform.TransformerException if the application
     *                                                  chooses to discontinue the transformation.
     * @see javax.xml.transform.TransformerException
     */
    void error(TransformerException exception)
            throws TransformerException;

    /**
     * <p>Receive notification of a non-recoverable error.</p>
     * <p/>
     * <p>The processor may choose to continue, but will not normally
     * proceed to a successful completion.</p>
     * <p/>
     * <p>The method should throw an exception if it is unable to
     * process the error, or if it wishes execution to terminate
     * immediately. The processor will not necessarily honor this
     * request.</p>
     *
     * @param exception The error information encapsulated in a
     *                  <code>TransformerException</code>.
     * @throws javax.xml.transform.TransformerException if the application
     *                                                  chooses to discontinue the transformation.
     * @see javax.xml.transform.TransformerException
     */
    void fatalError(TransformerException exception)
            throws TransformerException;
}

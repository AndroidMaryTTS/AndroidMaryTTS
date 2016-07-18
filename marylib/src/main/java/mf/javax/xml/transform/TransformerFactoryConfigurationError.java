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
 * $Id: TransformerFactoryConfigurationError.java,v 1.7 2010-11-01 04:36:11 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform;

/**
 * Thrown when a problem with configuration with the Transformer Factories
 * exists. This error will typically be thrown when the class of a
 * transformation factory specified in the system properties cannot be found
 * or instantiated.
 */
public class TransformerFactoryConfigurationError extends Error {
    private static final long serialVersionUID = -6527718720676281516L;

    /**
     * <code>Exception</code> for the
     * <code>TransformerFactoryConfigurationError</code>.
     */
    private Exception exception;

    /**
     * Create a new <code>TransformerFactoryConfigurationError</code> with no
     * detail mesage.
     */
    public TransformerFactoryConfigurationError() {

        super();

        this.exception = null;
    }

    /**
     * Create a new <code>TransformerFactoryConfigurationError</code> with
     * the <code>String</code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    public TransformerFactoryConfigurationError(String msg) {

        super(msg);

        this.exception = null;
    }

    /**
     * Create a new <code>TransformerFactoryConfigurationError</code> with a
     * given <code>Exception</code> base cause of the error.
     *
     * @param e The exception to be encapsulated in a
     *          TransformerFactoryConfigurationError.
     */
    public TransformerFactoryConfigurationError(Exception e) {

        super(e.toString());

        this.exception = e;
    }

    /**
     * Create a new <code>TransformerFactoryConfigurationError</code> with the
     * given <code>Exception</code> base cause and detail message.
     *
     * @param e   The exception to be encapsulated in a
     *            TransformerFactoryConfigurationError
     * @param msg The detail message.
     */
    public TransformerFactoryConfigurationError(Exception e, String msg) {

        super(msg);

        this.exception = e;
    }

    /**
     * Return the message (if any) for this error . If there is no
     * message for the exception and there is an encapsulated
     * exception then the message of that exception will be returned.
     *
     * @return The error message.
     */
    @Override
    public String getMessage() {

        String message = super.getMessage();

        if ((message == null) && (exception != null)) {
            return exception.getMessage();
        }

        return message;
    }

    /**
     * Return the actual exception (if any) that caused this exception to
     * be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * use the exception chaining mechanism of JDK1.4
     */
    @Override
    public Throwable getCause() {
        return exception;
    }
}

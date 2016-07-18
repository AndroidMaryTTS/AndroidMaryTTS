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

package mf.javax.xml.stream;

/**
 * An error class for reporting factory configuration errors.
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @since 1.6
 */
public class FactoryConfigurationError extends Error {
    private static final long serialVersionUID = -2994412584589975744L;

    Exception nested;

    /**
     * Default constructor
     */
    public FactoryConfigurationError() {
    }

    /**
     * Construct an exception with a nested inner exception
     *
     * @param e the exception to nest
     */
    public FactoryConfigurationError(java.lang.Exception e) {
        nested = e;
    }

    /**
     * Construct an exception with a nested inner exception
     * and a message
     *
     * @param e   the exception to nest
     * @param msg the message to report
     */
    public FactoryConfigurationError(java.lang.Exception e, java.lang.String msg) {
        super(msg);
        nested = e;
    }

    /**
     * Construct an exception with a nested inner exception
     * and a message
     *
     * @param msg the message to report
     * @param e   the exception to nest
     */
    public FactoryConfigurationError(java.lang.String msg, java.lang.Exception e) {
        super(msg);
        nested = e;
    }

    /**
     * Construct an exception with associated message
     *
     * @param msg the message to report
     */
    public FactoryConfigurationError(java.lang.String msg) {
        super(msg);
    }

    /**
     * Return the nested exception (if any)
     *
     * @return the nested exception or null
     */
    public Exception getException() {
        return nested;
    }

    /**
     * use the exception chaining mechanism of JDK1.4
     */
    @Override
    public Throwable getCause() {
        return nested;
    }

    /**
     * Report the message associated with this error
     *
     * @return the string value of the message
     */
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null)
            return msg;
        if (nested != null) {
            msg = nested.getMessage();
            if (msg == null)
                msg = nested.getClass().toString();
        }
        return msg;
    }


}

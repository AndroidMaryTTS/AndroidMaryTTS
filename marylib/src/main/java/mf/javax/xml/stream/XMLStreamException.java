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
 * The base exception for unexpected processing errors.  This Exception
 * class is used to report well-formedness errors as well as unexpected
 * processing conditions.
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @since 1.6
 */

public class XMLStreamException extends Exception {

    protected Throwable nested;
    protected Location location;

    /**
     * Default constructor
     */
    public XMLStreamException() {
        super();
    }

    /**
     * Construct an exception with the assocated message.
     *
     * @param msg the message to report
     */
    public XMLStreamException(String msg) {
        super(msg);
    }

    /**
     * Construct an exception with the assocated exception
     *
     * @param th a nested exception
     */
    public XMLStreamException(Throwable th) {
        super(th);
        nested = th;
    }

    /**
     * Construct an exception with the assocated message and exception
     *
     * @param th  a nested exception
     * @param msg the message to report
     */
    public XMLStreamException(String msg, Throwable th) {
        super(msg, th);
        nested = th;
    }

    /**
     * Construct an exception with the assocated message, exception and location.
     *
     * @param th       a nested exception
     * @param msg      the message to report
     * @param location the location of the error
     */
    public XMLStreamException(String msg, Location location, Throwable th) {
        super("ParseError at [row,col]:[" + location.getLineNumber() + "," +
                location.getColumnNumber() + "]\n" +
                "Message: " + msg);
        nested = th;
        this.location = location;
    }

    /**
     * Construct an exception with the assocated message, exception and location.
     *
     * @param msg      the message to report
     * @param location the location of the error
     */
    public XMLStreamException(String msg,
                              Location location) {
        super("ParseError at [row,col]:[" + location.getLineNumber() + "," +
                location.getColumnNumber() + "]\n" +
                "Message: " + msg);
        this.location = location;
    }


    /**
     * Gets the nested exception.
     *
     * @return Nested exception
     */
    public Throwable getNestedException() {
        return nested;
    }

    /**
     * Gets the location of the exception
     *
     * @return the location of the exception, may be null if none is available
     */
    public Location getLocation() {
        return location;
    }

}

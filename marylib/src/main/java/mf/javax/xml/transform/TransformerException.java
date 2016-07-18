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
 * $Id: TransformerException.java,v 1.8 2010-11-01 04:36:11 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class specifies an exceptional condition that occured
 * during the transformation process.
 */
public class TransformerException extends Exception {

    /**
     * Field locator specifies where the error occured
     */
    SourceLocator locator;
    /**
     * Field containedException specifies a wrapped exception.  May be null.
     */
    Throwable containedException;

    /**
     * Create a new TransformerException.
     *
     * @param message The error or warning message.
     */
    public TransformerException(String message) {

        super(message);

        this.containedException = null;
        this.locator = null;
    }

    /**
     * Create a new TransformerException wrapping an existing exception.
     *
     * @param e The exception to be wrapped.
     */
    public TransformerException(Throwable e) {

        super(e.toString());

        this.containedException = e;
        this.locator = null;
    }

    /**
     * Wrap an existing exception in a TransformerException.
     * <p/>
     * <p>This is used for throwing processor exceptions before
     * the processing has started.</p>
     *
     * @param message The error or warning message, or null to
     *                use the message from the embedded exception.
     * @param e       Any exception
     */
    public TransformerException(String message, Throwable e) {

        super(((message == null) || (message.length() == 0))
                ? e.toString()
                : message);

        this.containedException = e;
        this.locator = null;
    }

    /**
     * Create a new TransformerException from a message and a Locator.
     * <p/>
     * <p>This constructor is especially useful when an application is
     * creating its own exception from within a DocumentHandler
     * callback.</p>
     *
     * @param message The error or warning message.
     * @param locator The locator object for the error or warning.
     */
    public TransformerException(String message, SourceLocator locator) {

        super(message);

        this.containedException = null;
        this.locator = locator;
    }

    /**
     * Wrap an existing exception in a TransformerException.
     *
     * @param message The error or warning message, or null to
     *                use the message from the embedded exception.
     * @param locator The locator object for the error or warning.
     * @param e       Any exception
     */
    public TransformerException(String message, SourceLocator locator,
                                Throwable e) {

        super(message);

        this.containedException = e;
        this.locator = locator;
    }

    /**
     * Method getLocator retrieves an instance of a SourceLocator
     * object that specifies where an error occured.
     *
     * @return A SourceLocator object, or null if none was specified.
     */
    public SourceLocator getLocator() {
        return locator;
    }

    /**
     * Method setLocator sets an instance of a SourceLocator
     * object that specifies where an error occured.
     *
     * @param location A SourceLocator object, or null to clear the location.
     */
    public void setLocator(SourceLocator location) {
        locator = location;
    }

    /**
     * This method retrieves an exception that this exception wraps.
     *
     * @return An Throwable object, or null.
     * @see #getCause
     */
    public Throwable getException() {
        return containedException;
    }

    /**
     * Returns the cause of this throwable or <code>null</code> if the
     * cause is nonexistent or unknown.  (The cause is the throwable that
     * caused this throwable to get thrown.)
     */
    @Override
    public Throwable getCause() {

        return ((containedException == this)
                ? null
                : containedException);
    }

    /**
     * Initializes the <i>cause</i> of this throwable to the specified value.
     * (The cause is the throwable that caused this throwable to get thrown.)
     * <p/>
     * <p>This method can be called at most once.  It is generally called from
     * within the constructor, or immediately after creating the
     * throwable.  If this throwable was created
     * with {@link #TransformerException(Throwable)} or
     * {@link #TransformerException(String, Throwable)}, this method cannot be called
     * even once.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <code>null</code> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @return a reference to this <code>Throwable</code> instance.
     * @throws IllegalArgumentException if <code>cause</code> is this
     *                                  throwable.  (A throwable cannot
     *                                  be its own cause.)
     * @throws IllegalStateException    if this throwable was
     *                                  created with {@link #TransformerException(Throwable)} or
     *                                  {@link #TransformerException(String, Throwable)}, or this method has already
     *                                  been called on this throwable.
     */
    @Override
    public synchronized Throwable initCause(Throwable cause) {

        if (this.containedException != null) {
            throw new IllegalStateException("Can't overwrite cause");
        }

        if (cause == this) {
            throw new IllegalArgumentException(
                    "Self-causation not permitted");
        }

        this.containedException = cause;

        return this;
    }

    /**
     * Get the error message with location information
     * appended.
     *
     * @return A <code>String</code> representing the error message with
     * location information appended.
     */
    public String getMessageAndLocation() {

        StringBuffer sbuffer = new StringBuffer();
        String message = super.getMessage();

        if (null != message) {
            sbuffer.append(message);
        }

        if (null != locator) {
            String systemID = locator.getSystemId();
            int line = locator.getLineNumber();
            int column = locator.getColumnNumber();

            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }

            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }

            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }
        }

        return sbuffer.toString();
    }

    /**
     * Get the location information as a string.
     *
     * @return A string with location info, or null
     * if there is no location information.
     */
    public String getLocationAsString() {

        if (null != locator) {
            StringBuffer sbuffer = new StringBuffer();
            String systemID = locator.getSystemId();
            int line = locator.getLineNumber();
            int column = locator.getColumnNumber();

            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }

            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }

            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }

            return sbuffer.toString();
        } else {
            return null;
        }
    }

    /**
     * Print the the trace of methods from where the error
     * originated.  This will trace all nested exception
     * objects, as well as this object.
     */
    @Override
    public void printStackTrace() {
        printStackTrace(new java.io.PrintWriter(System.err, true));
    }

    /**
     * Print the the trace of methods from where the error
     * originated.  This will trace all nested exception
     * objects, as well as this object.
     *
     * @param s The stream where the dump will be sent to.
     */
    @Override
    public void printStackTrace(java.io.PrintStream s) {
        printStackTrace(new java.io.PrintWriter(s));
    }

    /**
     * Print the the trace of methods from where the error
     * originated.  This will trace all nested exception
     * objects, as well as this object.
     *
     * @param s The writer where the dump will be sent to.
     */
    @Override
    public void printStackTrace(java.io.PrintWriter s) {

        if (s == null) {
            s = new java.io.PrintWriter(System.err, true);
        }

        try {
            String locInfo = getLocationAsString();

            if (null != locInfo) {
                s.println(locInfo);
            }

            super.printStackTrace(s);
        } catch (Throwable e) {
        }

        Throwable exception = getException();

        for (int i = 0; (i < 10) && (null != exception); i++) {
            s.println("---------");

            try {
                if (exception instanceof TransformerException) {
                    String locInfo =
                            ((TransformerException) exception)
                                    .getLocationAsString();

                    if (null != locInfo) {
                        s.println(locInfo);
                    }
                }

                exception.printStackTrace(s);
            } catch (Throwable e) {
                s.println("Could not print stack trace...");
            }

            try {
                Method meth =
                        ((Object) exception).getClass().getMethod("getException",
                                (Class[]) null);

                if (null != meth) {
                    Throwable prev = exception;

                    exception = (Throwable) meth.invoke(exception, (Object[]) null);

                    if (prev == exception) {
                        break;
                    }
                } else {
                    exception = null;
                }
            } catch (InvocationTargetException ite) {
                exception = null;
            } catch (IllegalAccessException iae) {
                exception = null;
            } catch (NoSuchMethodException nsme) {
                exception = null;
            }
        }
        // insure output is written
        s.flush();
    }
}

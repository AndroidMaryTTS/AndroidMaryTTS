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
 * $Id: SecuritySupport.java,v 1.6 2010-11-01 04:36:13 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

/**
 * This class is duplicated for each JAXP subpackage so keep it in sync.
 * It is package private and therefore is not exposed as part of the JAXP
 * API.
 * <p/>
 * Security related methods that only work on J2SE 1.2 and newer.
 */
class SecuritySupport {


    ClassLoader getContextClassLoader() {
        return (ClassLoader)
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        ClassLoader cl = null;
                        try {
                            cl = Thread.currentThread().getContextClassLoader();
                        } catch (SecurityException ex) {
                        }
                        return cl;
                    }
                });
    }

    String getSystemProperty(final String propName) {
        return (String)
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        return System.getProperty(propName);
                    }
                });
    }

    FileInputStream getFileInputStream(final File file)
            throws FileNotFoundException {
        try {
            return (FileInputStream)
                    AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        @Override
                        public Object run() throws FileNotFoundException {
                            return new FileInputStream(file);
                        }
                    });
        } catch (PrivilegedActionException e) {
            throw (FileNotFoundException) e.getException();
        }
    }

    InputStream getURLInputStream(final URL url)
            throws IOException {
        try {
            return (InputStream)
                    AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        @Override
                        public Object run() throws IOException {
                            return url.openStream();
                        }
                    });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    URL getResourceAsURL(final ClassLoader cl,
                         final String name) {
        return (URL)
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        URL url;
                        if (cl == null) {
                            url = Object.class.getResource(name);
                        } else {
                            url = cl.getResource(name);
                        }
                        return url;
                    }
                });
    }

    Enumeration getResources(final ClassLoader cl,
                             final String name) throws IOException {
        try {
            return (Enumeration)
                    AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        @Override
                        public Object run() throws IOException {
                            Enumeration enumeration;
                            if (cl == null) {
                                enumeration = ClassLoader.getSystemResources(name);
                            } else {
                                enumeration = cl.getResources(name);
                            }
                            return enumeration;
                        }
                    });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    InputStream getResourceAsStream(final ClassLoader cl,
                                    final String name) {
        return (InputStream)
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        InputStream ris;
                        if (cl == null) {
                            ris = Object.class.getResourceAsStream(name);
                        } else {
                            ris = cl.getResourceAsStream(name);
                        }
                        return ris;
                    }
                });
    }

    boolean doesFileExist(final File f) {
        return ((Boolean)
                AccessController.doPrivileged(new PrivilegedAction() {
                    @Override
                    public Object run() {
                        return new Boolean(f.exists());
                    }
                })).booleanValue();
    }

}

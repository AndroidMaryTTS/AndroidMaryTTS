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
 * $Id: SAXResult.java,v 1.5 2010-11-01 04:36:12 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

import mf.javax.xml.transform.Result;

/**
 * <p>Acts as an holder for a transformation Result.</p>
 *
 * @author <a href="Jeff.Suttor@Sun.com">Jeff Suttor</a>
 */
public class SAXResult implements Result {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the Transformer supports Result output of this type.
     */
    public static final String FEATURE =
            "http://javax.xml.transform.sax.SAXResult/feature";
    /**
     * The handler for parse events.
     */
    private ContentHandler handler;
    /**
     * The handler for lexical events.
     */
    private LexicalHandler lexhandler;
    /**
     * The systemID that may be used in association
     * with the node.
     */
    private String systemId;

    /**
     * Zero-argument default constructor.
     */
    public SAXResult() {
    }

    /**
     * Create a SAXResult that targets a SAX2 {@link org.xml.sax.ContentHandler}.
     *
     * @param handler Must be a non-null ContentHandler reference.
     */
    public SAXResult(ContentHandler handler) {
        setHandler(handler);
    }

    /**
     * Get the {@link org.xml.sax.ContentHandler} that is the Result.
     *
     * @return The ContentHandler that is to be transformation output.
     */
    public ContentHandler getHandler() {
        return handler;
    }

    /**
     * Set the target to be a SAX2 {@link org.xml.sax.ContentHandler}.
     *
     * @param handler Must be a non-null ContentHandler reference.
     */
    public void setHandler(ContentHandler handler) {
        this.handler = handler;
    }

    /**
     * Get a SAX2 {@link org.xml.sax.ext.LexicalHandler} for the output.
     *
     * @return A <code>LexicalHandler</code>, or null.
     */
    public LexicalHandler getLexicalHandler() {
        return lexhandler;
    }

    //////////////////////////////////////////////////////////////////////
    // Internal state.
    //////////////////////////////////////////////////////////////////////

    /**
     * Set the SAX2 {@link org.xml.sax.ext.LexicalHandler} for the output.
     * <p/>
     * <p>This is needed to handle XML comments and the like.  If the
     * lexical handler is not set, an attempt should be made by the
     * transformer to cast the {@link org.xml.sax.ContentHandler} to a
     * <code>LexicalHandler</code>.</p>
     *
     * @param handler A non-null <code>LexicalHandler</code> for
     *                handling lexical parse events.
     */
    public void setLexicalHandler(LexicalHandler handler) {
        this.lexhandler = handler;
    }

    /**
     * Get the system identifier that was set with setSystemId.
     *
     * @return The system identifier that was set with setSystemId, or null
     * if setSystemId was not called.
     */
    @Override
    public String getSystemId() {
        return systemId;
    }

    /**
     * Method setSystemId Set the systemID that may be used in association
     * with the {@link org.xml.sax.ContentHandler}.
     *
     * @param systemId The system identifier as a URI string.
     */
    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}

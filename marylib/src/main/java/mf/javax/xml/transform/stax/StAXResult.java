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
 * $Id: StAXResult.java,v 1.7 2010-11-01 04:36:12 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform.stax;

import mf.javax.xml.stream.XMLEventWriter;
import mf.javax.xml.stream.XMLStreamWriter;
import mf.javax.xml.transform.Result;

/**
 * <p>Acts as a holder for an XML {@link Result} in the
 * form of a StAX writer,i.e.
 * {@link XMLStreamWriter} or {@link XMLEventWriter}.
 * <code>StAXResult</code> can be used in all cases that accept
 * a <code>Result</code>, e.g. {@link javax.xml.transform.Transformer},
 * {@link javax.xml.validation.Validator} which accept
 * <code>Result</code> as input.
 *
 * @author <a href="mailto:Neeraj.Bajaj@Sun.com">Neeraj Bajaj</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.7 $, $Date: 2010-11-01 04:36:12 $
 * @see <a href="http://jcp.org/en/jsr/detail?id=173">
 * JSR 173: Streaming API for XML</a>
 * @see XMLStreamWriter
 * @see XMLEventWriter
 * @since 1.6
 */
public class StAXResult implements Result {
    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature(String name)}
     * returns true when passed this value as an argument,
     * the Transformer supports Result output of this type.
     */
    public static final String FEATURE =
            "http://javax.xml.transform.stax.StAXResult/feature";

    /**
     * <p><code>XMLEventWriter</code> to be used for
     * <code>Result</code> output.</p>
     */
    private XMLEventWriter xmlEventWriter = null;

    /**
     * <p><code>XMLStreamWriter</code> to be used for
     * <code>Result</code> output.</p>
     */
    private XMLStreamWriter xmlStreamWriter = null;

    /**
     * <p>System identifier for this <code>StAXResult</code>.<p>
     */
    private String systemId = null;

    /**
     * <p>Creates a new instance of a <code>StAXResult</code>
     * by supplying an {@link XMLEventWriter}.</p>
     * <p/>
     * <p><code>XMLEventWriter</code> must be a
     * non-<code>null</code> reference.</p>
     *
     * @param xmlEventWriter <code>XMLEventWriter</code> used to create
     *                       this <code>StAXResult</code>.
     * @throws IllegalArgumentException If <code>xmlEventWriter</code> ==
     *                                  <code>null</code>.
     */
    public StAXResult(final XMLEventWriter xmlEventWriter) {

        if (xmlEventWriter == null) {
            throw new IllegalArgumentException(
                    "StAXResult(XMLEventWriter) with XMLEventWriter == null");
        }

        this.xmlEventWriter = xmlEventWriter;
    }

    /**
     * <p>Creates a new instance of a <code>StAXResult</code>
     * by supplying an {@link XMLStreamWriter}.</p>
     * <p/>
     * <p><code>XMLStreamWriter</code> must be a
     * non-<code>null</code> reference.</p>
     *
     * @param xmlStreamWriter <code>XMLStreamWriter</code> used to create
     *                        this <code>StAXResult</code>.
     * @throws IllegalArgumentException If <code>xmlStreamWriter</code> ==
     *                                  <code>null</code>.
     */
    public StAXResult(final XMLStreamWriter xmlStreamWriter) {

        if (xmlStreamWriter == null) {
            throw new IllegalArgumentException(
                    "StAXResult(XMLStreamWriter) with XMLStreamWriter == null");
        }

        this.xmlStreamWriter = xmlStreamWriter;
    }

    /**
     * <p>Get the <code>XMLEventWriter</code> used by this
     * <code>StAXResult</code>.</p>
     * <p/>
     * <p><code>XMLEventWriter</code> will be <code>null</code>
     * if this <code>StAXResult</code> was created with a
     * <code>XMLStreamWriter</code>.</p>
     *
     * @return <code>XMLEventWriter</code> used by this
     * <code>StAXResult</code>.
     */
    public XMLEventWriter getXMLEventWriter() {

        return xmlEventWriter;
    }

    /**
     * <p>Get the <code>XMLStreamWriter</code> used by this
     * <code>StAXResult</code>.</p>
     * <p/>
     * <p><code>XMLStreamWriter</code> will be <code>null</code>
     * if this <code>StAXResult</code> was created with a
     * <code>XMLEventWriter</code>.</p>
     *
     * @return <code>XMLStreamWriter</code> used by this
     * <code>StAXResult</code>.
     */
    public XMLStreamWriter getXMLStreamWriter() {

        return xmlStreamWriter;
    }

    /**
     * <p>The returned system identifier is always <code>null</code>.</p>
     *
     * @return The returned system identifier is always <code>null</code>.
     */
    @Override
    public String getSystemId() {

        return null;
    }

    /**
     * <p>In the context of a <code>StAXResult</code>, it is not appropriate
     * to explicitly set the system identifier.
     * The <code>XMLEventWriter</code> or <code>XMLStreamWriter</code>
     * used to construct this <code>StAXResult</code> determines the
     * system identifier of the XML result.</p>
     * <p/>
     * <p>An {@link UnsupportedOperationException} is <strong>always</strong>
     * thrown by this method.</p>
     *
     * @param systemId Ignored.
     * @throws UnsupportedOperationException Is <strong>always</strong>
     *                                       thrown by this method.
     */
    @Override
    public void setSystemId(final String systemId) {

        throw new UnsupportedOperationException(
                "StAXResult#setSystemId(systemId) cannot set the "
                        + "system identifier for a StAXResult");
    }
}

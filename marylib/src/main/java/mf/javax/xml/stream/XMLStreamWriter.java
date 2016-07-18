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

import mf.javax.xml.namespace.NamespaceContext;

/**
 * The XMLStreamWriter interface specifies how to write XML.  The XMLStreamWriter  does
 * not perform well formedness checking on its input.  However
 * the writeCharacters method is required to escape &amp; , &lt; and &gt;
 * For attribute values the writeAttribute method will escape the
 * above characters plus &quot; to ensure that all character content
 * and attribute values are well formed.
 * <p/>
 * Each NAMESPACE
 * and ATTRIBUTE must be individually written.
 * <p/>
 * <table border="1" cellpadding="2" cellspacing="0">
 * <thead>
 * <tr>
 * <th colspan="5">XML Namespaces, <code>javax.xml.stream.isRepairingNamespaces</code> and write method behaviour</th>
 * </tr>
 * <tr>
 * <th>Method</th> <!-- method -->
 * <th colspan="2"><code>isRepairingNamespaces</code> == true</th>
 * <th colspan="2"><code>isRepairingNamespaces</code> == false</th>
 * </tr>
 * <tr>
 * <th></th> <!-- method -->
 * <th>namespaceURI bound</th>
 * <th>namespaceURI unbound</th>
 * <th>namespaceURI bound</th>
 * <th>namespaceURI unbound</th>
 * </tr>
 * </thead>
 * <p/>
 * <tbody>
 * <tr>
 * <th><code>writeAttribute(namespaceURI, localName, value)</code></th>
 * <!-- isRepairingNamespaces == true -->
 * <td>
 * <!-- namespaceURI bound -->
 * prefix:localName="value"&nbsp;<sup>[1]</sup>
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * xmlns:{generated}="namespaceURI" {generated}:localName="value"
 * </td>
 * <!-- isRepairingNamespaces == false -->
 * <td>
 * <!-- namespaceURI bound -->
 * prefix:localName="value"&nbsp;<sup>[1]</sup>
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * <code>XMLStreamException</code>
 * </td>
 * </tr>
 * <p/>
 * <tr>
 * <th><code>writeAttribute(prefix, namespaceURI, localName, value)</code></th>
 * <!-- isRepairingNamespaces == true -->
 * <td>
 * <!-- namespaceURI bound -->
 * bound to same prefix:<br />
 * prefix:localName="value"&nbsp;<sup>[1]</sup><br />
 * <br />
 * bound to different prefix:<br />
 * xmlns:{generated}="namespaceURI" {generated}:localName="value"
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * xmlns:prefix="namespaceURI" prefix:localName="value"&nbsp;<sup>[3]</sup>
 * </td>
 * <!-- isRepairingNamespaces == false -->
 * <td>
 * <!-- namespaceURI bound -->
 * bound to same prefix:<br />
 * prefix:localName="value"&nbsp;<sup>[1][2]</sup><br />
 * <br />
 * bound to different prefix:<br />
 * <code>XMLStreamException</code><sup>[2]</sup>
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * xmlns:prefix="namespaceURI" prefix:localName="value"&nbsp;<sup>[2][5]</sup>
 * </td>
 * </tr>
 * <p/>
 * <tr>
 * <th><code>writeStartElement(namespaceURI, localName)</code><br />
 * <br />
 * <code>writeEmptyElement(namespaceURI, localName)</code></th>
 * <!-- isRepairingNamespaces == true -->
 * <td >
 * <!-- namespaceURI bound -->
 * &lt;prefix:localName&gt;&nbsp;<sup>[1]</sup>
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * &lt;{generated}:localName xmlns:{generated}="namespaceURI"&gt;
 * </td>
 * <!-- isRepairingNamespaces == false -->
 * <td>
 * <!-- namespaceURI bound -->
 * &lt;prefix:localName&gt;&nbsp;<sup>[1]</sup>
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * <code>XMLStreamException</code>
 * </td>
 * </tr>
 * <p/>
 * <tr>
 * <th><code>writeStartElement(prefix, localName, namespaceURI)</code><br />
 * <br />
 * <code>writeEmptyElement(prefix, localName, namespaceURI)</code></th>
 * <!-- isRepairingNamespaces == true -->
 * <td>
 * <!-- namespaceURI bound -->
 * bound to same prefix:<br />
 * &lt;prefix:localName&gt;&nbsp;<sup>[1]</sup><br />
 * <br />
 * bound to different prefix:<br />
 * &lt;{generated}:localName xmlns:{generated}="namespaceURI"&gt;
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * &lt;prefix:localName xmlns:prefix="namespaceURI"&gt;&nbsp;<sup>[4]</sup>
 * </td>
 * <!-- isRepairingNamespaces == false -->
 * <td>
 * <!-- namespaceURI bound -->
 * bound to same prefix:<br />
 * &lt;prefix:localName&gt;&nbsp;<sup>[1]</sup><br />
 * <br />
 * bound to different prefix:<br />
 * <code>XMLStreamException</code>
 * </td>
 * <td>
 * <!-- namespaceURI unbound -->
 * &lt;prefix:localName&gt;&nbsp;
 * </td>
 * </tr>
 * </tbody>
 * <tfoot>
 * <tr>
 * <td colspan="5">
 * Notes:
 * <ul>
 * <li>[1] if namespaceURI == default Namespace URI, then no prefix is written</li>
 * <li>[2] if prefix == "" || null && namespaceURI == "", then no prefix or Namespace declaration is generated or written</li>
 * <li>[3] if prefix == "" || null, then a prefix is randomly generated</li>
 * <li>[4] if prefix == "" || null, then it is treated as the default Namespace and no prefix is generated or written, an xmlns declaration is generated and written if the namespaceURI is unbound</li>
 * <li>[5] if prefix == "" || null, then it is treated as an invalid attempt to define the default Namespace and an XMLStreamException is thrown</li>
 * </ul>
 * </td>
 * </tr>
 * </tfoot>
 * </table>
 *
 * @author Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 * @version 1.0
 * @see XMLOutputFactory
 * @see XMLStreamReader
 * @since 1.6
 */
public interface XMLStreamWriter {

    /**
     * Writes a start tag to the output.  All writeStartElement methods
     * open a new scope in the internal namespace context.  Writing the
     * corresponding EndElement causes the scope to be closed.
     *
     * @param localName local name of the tag, may not be null
     * @throws XMLStreamException
     */
    void writeStartElement(String localName)
            throws XMLStreamException;

    /**
     * Writes a start tag to the output
     *
     * @param namespaceURI the namespaceURI of the prefix to use, may not be null
     * @param localName    local name of the tag, may not be null
     * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
     *                            javax.xml.stream.isRepairingNamespaces has not been set to true
     */
    void writeStartElement(String namespaceURI, String localName)
            throws XMLStreamException;

    /**
     * Writes a start tag to the output
     *
     * @param localName    local name of the tag, may not be null
     * @param prefix       the prefix of the tag, may not be null
     * @param namespaceURI the uri to bind the prefix to, may not be null
     * @throws XMLStreamException
     */
    void writeStartElement(String prefix,
                           String localName,
                           String namespaceURI)
            throws XMLStreamException;

    /**
     * Writes an empty element tag to the output
     *
     * @param namespaceURI the uri to bind the tag to, may not be null
     * @param localName    local name of the tag, may not be null
     * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
     *                            javax.xml.stream.isRepairingNamespaces has not been set to true
     */
    void writeEmptyElement(String namespaceURI, String localName)
            throws XMLStreamException;

    /**
     * Writes an empty element tag to the output
     *
     * @param prefix       the prefix of the tag, may not be null
     * @param localName    local name of the tag, may not be null
     * @param namespaceURI the uri to bind the tag to, may not be null
     * @throws XMLStreamException
     */
    void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException;

    /**
     * Writes an empty element tag to the output
     *
     * @param localName local name of the tag, may not be null
     * @throws XMLStreamException
     */
    void writeEmptyElement(String localName)
            throws XMLStreamException;

    /**
     * Writes string data to the output without checking for well formedness.
     * The data is opaque to the XMLStreamWriter, i.e. the characters are written
     * blindly to the underlying output.  If the method cannot be supported
     * in the currrent writing context the implementation may throw a
     * UnsupportedOperationException.  For example note that any
     * namespace declarations, end tags, etc. will be ignored and could
     * interfere with proper maintanence of the writers internal state.
     *
     * @param data the data to write
     */
    //  public void writeRaw(String data) throws XMLStreamException;

    /**
     * Writes an end tag to the output relying on the internal
     * state of the writer to determine the prefix and local name
     * of the event.
     *
     * @throws XMLStreamException
     */
    void writeEndElement()
            throws XMLStreamException;

    /**
     * Closes any start tags and writes corresponding end tags.
     *
     * @throws XMLStreamException
     */
    void writeEndDocument()
            throws XMLStreamException;

    /**
     * Close this writer and free any resources associated with the
     * writer.  This must not close the underlying output stream.
     *
     * @throws XMLStreamException
     */
    void close()
            throws XMLStreamException;

    /**
     * Write any cached data to the underlying output mechanism.
     *
     * @throws XMLStreamException
     */
    void flush()
            throws XMLStreamException;

    /**
     * Writes an attribute to the output stream without
     * a prefix.
     *
     * @param localName the local name of the attribute
     * @param value     the value of the attribute
     * @throws IllegalStateException if the current state does not allow Attribute writing
     * @throws XMLStreamException
     */
    void writeAttribute(String localName, String value)
            throws XMLStreamException;

    /**
     * Writes an attribute to the output stream
     *
     * @param prefix       the prefix for this attribute
     * @param namespaceURI the uri of the prefix for this attribute
     * @param localName    the local name of the attribute
     * @param value        the value of the attribute
     * @throws IllegalStateException if the current state does not allow Attribute writing
     * @throws XMLStreamException    if the namespace URI has not been bound to a prefix and
     *                               javax.xml.stream.isRepairingNamespaces has not been set to true
     */

    void writeAttribute(String prefix,
                        String namespaceURI,
                        String localName,
                        String value)
            throws XMLStreamException;

    /**
     * Writes an attribute to the output stream
     *
     * @param namespaceURI the uri of the prefix for this attribute
     * @param localName    the local name of the attribute
     * @param value        the value of the attribute
     * @throws IllegalStateException if the current state does not allow Attribute writing
     * @throws XMLStreamException    if the namespace URI has not been bound to a prefix and
     *                               javax.xml.stream.isRepairingNamespaces has not been set to true
     */
    void writeAttribute(String namespaceURI,
                        String localName,
                        String value)
            throws XMLStreamException;

    /**
     * Writes a namespace to the output stream
     * If the prefix argument to this method is the empty string,
     * "xmlns", or null this method will delegate to writeDefaultNamespace
     *
     * @param prefix       the prefix to bind this namespace to
     * @param namespaceURI the uri to bind the prefix to
     * @throws IllegalStateException if the current state does not allow Namespace writing
     * @throws XMLStreamException
     */
    void writeNamespace(String prefix, String namespaceURI)
            throws XMLStreamException;

    /**
     * Writes the default namespace to the stream
     *
     * @param namespaceURI the uri to bind the default namespace to
     * @throws IllegalStateException if the current state does not allow Namespace writing
     * @throws XMLStreamException
     */
    void writeDefaultNamespace(String namespaceURI)
            throws XMLStreamException;

    /**
     * Writes an xml comment with the data enclosed
     *
     * @param data the data contained in the comment, may be null
     * @throws XMLStreamException
     */
    void writeComment(String data)
            throws XMLStreamException;

    /**
     * Writes a processing instruction
     *
     * @param target the target of the processing instruction, may not be null
     * @throws XMLStreamException
     */
    void writeProcessingInstruction(String target)
            throws XMLStreamException;

    /**
     * Writes a processing instruction
     *
     * @param target the target of the processing instruction, may not be null
     * @param data   the data contained in the processing instruction, may not be null
     * @throws XMLStreamException
     */
    void writeProcessingInstruction(String target,
                                    String data)
            throws XMLStreamException;

    /**
     * Writes a CData section
     *
     * @param data the data contained in the CData Section, may not be null
     * @throws XMLStreamException
     */
    void writeCData(String data)
            throws XMLStreamException;

    /**
     * Write a DTD section.  This string represents the entire doctypedecl production
     * from the XML 1.0 specification.
     *
     * @param dtd the DTD to be written
     * @throws XMLStreamException
     */
    void writeDTD(String dtd)
            throws XMLStreamException;

    /**
     * Writes an entity reference
     *
     * @param name the name of the entity
     * @throws XMLStreamException
     */
    void writeEntityRef(String name)
            throws XMLStreamException;

    /**
     * Write the XML Declaration. Defaults the XML version to 1.0, and the encoding to utf-8
     *
     * @throws XMLStreamException
     */
    void writeStartDocument()
            throws XMLStreamException;

    /**
     * Write the XML Declaration. Defaults the XML version to 1.0
     *
     * @param version version of the xml document
     * @throws XMLStreamException
     */
    void writeStartDocument(String version)
            throws XMLStreamException;

    /**
     * Write the XML Declaration.  Note that the encoding parameter does
     * not set the actual encoding of the underlying output.  That must
     * be set when the instance of the XMLStreamWriter is created using the
     * XMLOutputFactory
     *
     * @param encoding encoding of the xml declaration
     * @param version  version of the xml document
     * @throws XMLStreamException If given encoding does not match encoding
     *                            of the underlying stream
     */
    void writeStartDocument(String encoding,
                            String version)
            throws XMLStreamException;

    /**
     * Write text to the output
     *
     * @param text the value to write
     * @throws XMLStreamException
     */
    void writeCharacters(String text)
            throws XMLStreamException;

    /**
     * Write text to the output
     *
     * @param text  the value to write
     * @param start the starting position in the array
     * @param len   the number of characters to write
     * @throws XMLStreamException
     */
    void writeCharacters(char[] text, int start, int len)
            throws XMLStreamException;

    /**
     * Gets the prefix the uri is bound to
     *
     * @return the prefix or null
     * @throws XMLStreamException
     */
    String getPrefix(String uri)
            throws XMLStreamException;

    /**
     * Sets the prefix the uri is bound to.  This prefix is bound
     * in the scope of the current START_ELEMENT / END_ELEMENT pair.
     * If this method is called before a START_ELEMENT has been written
     * the prefix is bound in the root scope.
     *
     * @param prefix the prefix to bind to the uri, may not be null
     * @param uri    the uri to bind to the prefix, may be null
     * @throws XMLStreamException
     */
    void setPrefix(String prefix, String uri)
            throws XMLStreamException;


    /**
     * Binds a URI to the default namespace
     * This URI is bound
     * in the scope of the current START_ELEMENT / END_ELEMENT pair.
     * If this method is called before a START_ELEMENT has been written
     * the uri is bound in the root scope.
     *
     * @param uri the uri to bind to the default namespace, may be null
     * @throws XMLStreamException
     */
    void setDefaultNamespace(String uri)
            throws XMLStreamException;

    /**
     * Returns the current namespace context.
     *
     * @return the current NamespaceContext
     */
    NamespaceContext getNamespaceContext();

    /**
     * Sets the current namespace context for prefix and uri bindings.
     * This context becomes the root namespace context for writing and
     * will replace the current root namespace context.  Subsequent calls
     * to setPrefix and setDefaultNamespace will bind namespaces using
     * the context passed to the method as the root context for resolving
     * namespaces.  This method may only be called once at the start of
     * the document.  It does not cause the namespaces to be declared.
     * If a namespace URI to prefix mapping is found in the namespace
     * context it is treated as declared and the prefix may be used
     * by the StreamWriter.
     *
     * @param context the namespace context to use for this writer, may not be null
     * @throws XMLStreamException
     */
    void setNamespaceContext(NamespaceContext context)
            throws XMLStreamException;

    /**
     * Get the value of a feature/property from the underlying implementation
     *
     * @param name The name of the property, may not be null
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     * @throws NullPointerException     if the name is null
     */
    Object getProperty(java.lang.String name) throws IllegalArgumentException;

}

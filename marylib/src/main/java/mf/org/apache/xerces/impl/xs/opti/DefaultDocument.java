/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mf.org.apache.xerces.impl.xs.opti;

import mf.org.w3c.dom.Attr;
import mf.org.w3c.dom.CDATASection;
import mf.org.w3c.dom.Comment;
import mf.org.w3c.dom.DOMConfiguration;
import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.DOMImplementation;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.DocumentFragment;
import mf.org.w3c.dom.DocumentType;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.EntityReference;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;
import mf.org.w3c.dom.ProcessingInstruction;
import mf.org.w3c.dom.Text;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @version $Id: DefaultDocument.java 705596 2008-10-17 13:05:10Z mrglavas $
 * @xerces.internal
 */
public class DefaultDocument extends NodeImpl
        implements Document {

    private String fDocumentURI = null;

    // default constructor
    public DefaultDocument() {
        this.nodeType = Node.DOCUMENT_NODE;
    }

    //
    // org.w3c.dom.Node methods
    //

    @Override
    public String getNodeName() {
        return "#document";
    }

    //
    // org.w3c.dom.Document methods
    //

    @Override
    public DocumentType getDoctype() {
        return null;
    }


    @Override
    public DOMImplementation getImplementation() {
        return null;
    }


    @Override
    public Element getDocumentElement() {
        return null;
    }


    @Override
    public NodeList getElementsByTagName(String tagname) {
        return null;
    }


    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return null;
    }


    @Override
    public Element getElementById(String elementId) {
        return null;
    }


    @Override
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Element createElement(String tagName) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public DocumentFragment createDocumentFragment() {
        return null;
    }


    @Override
    public Text createTextNode(String data) {
        return null;
    }

    @Override
    public Comment createComment(String data) {
        return null;
    }


    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Attr createAttribute(String name) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    // DOM Level 3 methods.

    @Override
    public String getInputEncoding() {
        return null;
    }

    /**
     * public void setInputEncoding(String actualEncoding){
     * throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
     * }
     */

    @Override
    public String getXmlEncoding() {
        return null;
    }


    /**
     * An attribute specifying, as part of the XML declaration, the encoding 
     * of this document. This is <code>null</code> when unspecified.
     * @since DOM Level 3
    public void setXmlEncoding(String encoding){
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
     */

    /**
     * An attribute specifying, as part of the XML declaration, whether this
     * document is standalone.
     * <br> This attribute represents the property [standalone] defined in .
     *
     * @since DOM Level 3
     */
    @Override
    public boolean getXmlStandalone() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * An attribute specifying, as part of the XML declaration, whether this
     * document is standalone.
     * <br> This attribute represents the property [standalone] defined in .
     *
     * @since DOM Level 3
     */
    @Override
    public void setXmlStandalone(boolean standalone) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * An attribute specifying, as part of the XML declaration, the version
     * number of this document. This is <code>null</code> when unspecified.
     * <br> This attribute represents the property [version] defined in .
     *
     * @throws DOMException NOT_SUPPORTED_ERR: Raised if the version is set to a value that is
     *                      not supported by this <code>Document</code>.
     * @since DOM Level 3
     */
    @Override
    public String getXmlVersion() {
        return null;
    }

    /**
     * An attribute specifying, as part of the XML declaration, the version
     * number of this document. This is <code>null</code> when unspecified.
     * <br> This attribute represents the property [version] defined in .
     *
     * @throws DOMException NOT_SUPPORTED_ERR: Raised if the version is set to a value that is
     *                      not supported by this <code>Document</code>.
     * @since DOM Level 3
     */
    @Override
    public void setXmlVersion(String version) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * An attribute specifying whether errors checking is enforced or not.
     * When set to <code>false</code>, the implementation is free to not
     * test every possible error case normally defined on DOM operations,
     * and not raise any <code>DOMException</code>. In case of error, the
     * behavior is undefined. This attribute is <code>true</code> by
     * defaults.
     *
     * @since DOM Level 3
     */
    @Override
    public boolean getStrictErrorChecking() {
        return false;
    }

    /**
     * An attribute specifying whether errors checking is enforced or not.
     * When set to <code>false</code>, the implementation is free to not
     * test every possible error case normally defined on DOM operations,
     * and not raise any <code>DOMException</code>. In case of error, the
     * behavior is undefined. This attribute is <code>true</code> by
     * defaults.
     *
     * @since DOM Level 3
     */
    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * The location of the document or <code>null</code> if undefined.
     * <br>Beware that when the <code>Document</code> supports the feature
     * "HTML" , the href attribute of the HTML BASE element takes precedence
     * over this attribute.
     *
     * @since DOM Level 3
     */
    @Override
    public String getDocumentURI() {
        return fDocumentURI;
    }

    /**
     * The location of the document or <code>null</code> if undefined.
     * <br>Beware that when the <code>Document</code> supports the feature
     * "HTML" , the href attribute of the HTML BASE element takes precedence
     * over this attribute.
     *
     * @since DOM Level 3
     */
    @Override
    public void setDocumentURI(String documentURI) {
        fDocumentURI = documentURI;
    }

    /**
     * DOM Level 3
     */
    @Override
    public Node adoptNode(Node source) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * DOM Level 3
     */
    @Override
    public void normalizeDocument() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * The configuration used when <code>Document.normalizeDocument</code> is
     * invoked.
     *
     * @since DOM Level 3
     */
    @Override
    public DOMConfiguration getDomConfig() {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * DOM Level 3
     */
    @Override
    public Node renameNode(Node n, String namespaceURI, String name) throws DOMException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


}

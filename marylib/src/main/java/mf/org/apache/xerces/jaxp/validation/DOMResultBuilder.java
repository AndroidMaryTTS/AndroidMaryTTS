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

package mf.org.apache.xerces.jaxp.validation;

import java.util.ArrayList;

import mf.javax.xml.transform.dom.DOMResult;
import mf.org.apache.xerces.dom.AttrImpl;
import mf.org.apache.xerces.dom.CoreDocumentImpl;
import mf.org.apache.xerces.dom.DOMMessageFormatter;
import mf.org.apache.xerces.dom.DocumentTypeImpl;
import mf.org.apache.xerces.dom.ElementImpl;
import mf.org.apache.xerces.dom.ElementNSImpl;
import mf.org.apache.xerces.dom.EntityImpl;
import mf.org.apache.xerces.dom.NotationImpl;
import mf.org.apache.xerces.dom.PSVIAttrNSImpl;
import mf.org.apache.xerces.dom.PSVIDocumentImpl;
import mf.org.apache.xerces.dom.PSVIElementNSImpl;
import mf.org.apache.xerces.impl.Constants;
import mf.org.apache.xerces.impl.dv.XSSimpleType;
import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.NamespaceContext;
import mf.org.apache.xerces.xni.QName;
import mf.org.apache.xerces.xni.XMLAttributes;
import mf.org.apache.xerces.xni.XMLLocator;
import mf.org.apache.xerces.xni.XMLResourceIdentifier;
import mf.org.apache.xerces.xni.XMLString;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLDocumentSource;
import mf.org.apache.xerces.xs.AttributePSVI;
import mf.org.apache.xerces.xs.ElementPSVI;
import mf.org.apache.xerces.xs.XSTypeDefinition;
import mf.org.w3c.dom.CDATASection;
import mf.org.w3c.dom.Comment;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.DocumentType;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.Entity;
import mf.org.w3c.dom.NamedNodeMap;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.Notation;
import mf.org.w3c.dom.ProcessingInstruction;
import mf.org.w3c.dom.Text;


/**
 * <p>DOM result builder.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: DOMResultBuilder.java 699892 2008-09-28 21:08:27Z mrglavas $
 */
final class DOMResultBuilder implements DOMDocumentHandler {

    /**
     * Table for quick check of child insertion.
     */
    private final static int[] kidOK;

    static {
        kidOK = new int[13];
        kidOK[Node.DOCUMENT_NODE] =
                1 << Node.ELEMENT_NODE | 1 << Node.PROCESSING_INSTRUCTION_NODE |
                        1 << Node.COMMENT_NODE | 1 << Node.DOCUMENT_TYPE_NODE;
        kidOK[Node.DOCUMENT_FRAGMENT_NODE] =
                kidOK[Node.ENTITY_NODE] =
                        kidOK[Node.ENTITY_REFERENCE_NODE] =
                                kidOK[Node.ELEMENT_NODE] =
                                        1 << Node.ELEMENT_NODE | 1 << Node.PROCESSING_INSTRUCTION_NODE |
                                                1 << Node.COMMENT_NODE | 1 << Node.TEXT_NODE |
                                                1 << Node.CDATA_SECTION_NODE | 1 << Node.ENTITY_REFERENCE_NODE;
        kidOK[Node.ATTRIBUTE_NODE] = 1 << Node.TEXT_NODE | 1 << Node.ENTITY_REFERENCE_NODE;
        kidOK[Node.DOCUMENT_TYPE_NODE] = 0;
        kidOK[Node.PROCESSING_INSTRUCTION_NODE] = 0;
        kidOK[Node.COMMENT_NODE] = 0;
        kidOK[Node.TEXT_NODE] = 0;
        kidOK[Node.CDATA_SECTION_NODE] = 0;
        kidOK[Node.NOTATION_NODE] = 0;
    } // static

    //
    // Data
    //

    private final ArrayList fTargetChildren = new ArrayList();
    private final QName fAttributeQName = new QName();
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private Node fTarget;
    private Node fNextSibling;
    private Node fCurrentNode;
    private Node fFragmentRoot;
    private boolean fIgnoreChars;

    public DOMResultBuilder() {
    }

    /*
     * DOMDocumentHandler methods
     */

    @Override
    public void setDOMResult(DOMResult result) {
        fCurrentNode = null;
        fFragmentRoot = null;
        fIgnoreChars = false;
        fTargetChildren.clear();
        if (result != null) {
            //MF
            fTarget = result.getNode();
            //MF
            fNextSibling = result.getNextSibling();
            fDocument = (fTarget.getNodeType() == Node.DOCUMENT_NODE) ? (Document) fTarget : fTarget.getOwnerDocument();
            fDocumentImpl = (fDocument instanceof CoreDocumentImpl) ? (CoreDocumentImpl) fDocument : null;
            fStorePSVI = (fDocument instanceof PSVIDocumentImpl);
            return;
        }
        fTarget = null;
        fNextSibling = null;
        fDocument = null;
        fDocumentImpl = null;
        fStorePSVI = false;
    }

    @Override
    public void doctypeDecl(DocumentType node) throws XNIException {
        /** Create new DocumentType node for the target. */
        if (fDocumentImpl != null) {
            DocumentType docType = fDocumentImpl.createDocumentType(node.getName(), node.getPublicId(), node.getSystemId());
            final String internalSubset = node.getInternalSubset();
            /** Copy internal subset. */
            if (internalSubset != null) {
                ((DocumentTypeImpl) docType).setInternalSubset(internalSubset);
            }
            /** Copy entities. */
            NamedNodeMap oldMap = node.getEntities();
            NamedNodeMap newMap = docType.getEntities();
            int length = oldMap.getLength();
            for (int i = 0; i < length; ++i) {
                Entity oldEntity = (Entity) oldMap.item(i);
                EntityImpl newEntity = (EntityImpl) fDocumentImpl.createEntity(oldEntity.getNodeName());
                newEntity.setPublicId(oldEntity.getPublicId());
                newEntity.setSystemId(oldEntity.getSystemId());
                newEntity.setNotationName(oldEntity.getNotationName());
                newMap.setNamedItem(newEntity);
            }
            /** Copy notations. */
            oldMap = node.getNotations();
            newMap = docType.getNotations();
            length = oldMap.getLength();
            for (int i = 0; i < length; ++i) {
                Notation oldNotation = (Notation) oldMap.item(i);
                NotationImpl newNotation = (NotationImpl) fDocumentImpl.createNotation(oldNotation.getNodeName());
                newNotation.setPublicId(oldNotation.getPublicId());
                newNotation.setSystemId(oldNotation.getSystemId());
                newMap.setNamedItem(newNotation);
            }
            append(docType);
        }
    }

    @Override
    public void characters(Text node) throws XNIException {
        /** Create new Text node for the target. */
        append(fDocument.createTextNode(node.getNodeValue()));
    }

    @Override
    public void cdata(CDATASection node) throws XNIException {
        /** Create new CDATASection node for the target. */
        append(fDocument.createCDATASection(node.getNodeValue()));
    }

    @Override
    public void comment(Comment node) throws XNIException {
        /** Create new Comment node for the target. */
        append(fDocument.createComment(node.getNodeValue()));
    }

    @Override
    public void processingInstruction(ProcessingInstruction node)
            throws XNIException {
        /** Create new ProcessingInstruction node for the target. */
        append(fDocument.createProcessingInstruction(node.getTarget(), node.getData()));
    }

    @Override
    public void setIgnoringCharacters(boolean ignore) {
        fIgnoreChars = ignore;
    }
    
    /*
     * XMLDocumentHandler methods
     */

    @Override
    public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext namespaceContext, Augmentations augs)
            throws XNIException {
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone,
                        Augmentations augs) throws XNIException {
    }

    @Override
    public void doctypeDecl(String rootElement, String publicId,
                            String systemId, Augmentations augs) throws XNIException {
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
    }

    @Override
    public void processingInstruction(String target, XMLString data,
                                      Augmentations augs) throws XNIException {
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes,
                             Augmentations augs) throws XNIException {
        Element elem;
        int attrCount = attributes.getLength();
        if (fDocumentImpl == null) {
            elem = fDocument.createElementNS(element.uri, element.rawname);
            for (int i = 0; i < attrCount; ++i) {
                attributes.getName(i, fAttributeQName);
                elem.setAttributeNS(fAttributeQName.uri, fAttributeQName.rawname, attributes.getValue(i));
            }
        }
        // If it's a Xerces DOM store type information for attributes, set idness, etc..
        else {
            elem = fDocumentImpl.createElementNS(element.uri, element.rawname, element.localpart);
            for (int i = 0; i < attrCount; ++i) {
                attributes.getName(i, fAttributeQName);
                AttrImpl attr = (AttrImpl) fDocumentImpl.createAttributeNS(fAttributeQName.uri,
                        fAttributeQName.rawname, fAttributeQName.localpart);
                attr.setValue(attributes.getValue(i));
                elem.setAttributeNodeNS(attr);

                // write type information to this attribute
                AttributePSVI attrPSVI = (AttributePSVI) attributes.getAugmentations(i).getItem(Constants.ATTRIBUTE_PSVI);
                if (attrPSVI != null) {
                    if (fStorePSVI) {
                        ((PSVIAttrNSImpl) attr).setPSVI(attrPSVI);
                    }
                    Object type = attrPSVI.getMemberTypeDefinition();
                    if (type == null) {
                        type = attrPSVI.getTypeDefinition();
                        if (type != null) {
                            attr.setType(type);
                            if (((XSSimpleType) type).isIDType()) {
                                ((ElementImpl) elem).setIdAttributeNode(attr, true);
                            }
                        }
                    } else {
                        attr.setType(type);
                        if (((XSSimpleType) type).isIDType()) {
                            ((ElementImpl) elem).setIdAttributeNode(attr, true);
                        }
                    }
                }
                attr.setSpecified(attributes.isSpecified(i));
            }
        }
        append(elem);
        fCurrentNode = elem;
        if (fFragmentRoot == null) {
            fFragmentRoot = elem;
        }
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes,
                             Augmentations augs) throws XNIException {
        startElement(element, attributes, augs);
        endElement(element, augs);
    }

    @Override
    public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier, String encoding,
                                   Augmentations augs) throws XNIException {
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs)
            throws XNIException {
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs)
            throws XNIException {
    }

    @Override
    public void characters(XMLString text, Augmentations augs)
            throws XNIException {
        if (!fIgnoreChars) {
            append(fDocument.createTextNode(text.toString()));
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs)
            throws XNIException {
        characters(text, augs);
    }

    @Override
    public void endElement(QName element, Augmentations augs)
            throws XNIException {
        // write type information to this element
        if (augs != null && fDocumentImpl != null) {
            ElementPSVI elementPSVI = (ElementPSVI) augs.getItem(Constants.ELEMENT_PSVI);
            if (elementPSVI != null) {
                if (fStorePSVI) {
                    ((PSVIElementNSImpl) fCurrentNode).setPSVI(elementPSVI);
                }
                XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
                if (type == null) {
                    type = elementPSVI.getTypeDefinition();
                }
                ((ElementNSImpl) fCurrentNode).setType(type);
            }
        }

        // adjust current node reference
        if (fCurrentNode == fFragmentRoot) {
            fCurrentNode = null;
            fFragmentRoot = null;
            return;
        }
        fCurrentNode = fCurrentNode.getParentNode();
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
        final int length = fTargetChildren.size();
        if (fNextSibling == null) {
            for (int i = 0; i < length; ++i) {
                fTarget.appendChild((Node) fTargetChildren.get(i));
            }
        } else {
            for (int i = 0; i < length; ++i) {
                fTarget.insertBefore((Node) fTargetChildren.get(i), fNextSibling);
            }
        }
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
    }
    
    /*
     * Other methods
     */

    private void append(Node node) throws XNIException {
        if (fCurrentNode != null) {
            fCurrentNode.appendChild(node);
        } else {
            /** Check if this node can be attached to the target. */
            if ((kidOK[fTarget.getNodeType()] & (1 << node.getNodeType())) == 0) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "HIERARCHY_REQUEST_ERR", null);
                throw new XNIException(msg);
            }
            fTargetChildren.add(node);
        }
    }

} // DOMResultBuilder

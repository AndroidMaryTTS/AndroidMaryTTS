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

import mf.javax.xml.transform.dom.DOMResult;
import mf.org.apache.xerces.dom.AttrImpl;
import mf.org.apache.xerces.dom.CoreDocumentImpl;
import mf.org.apache.xerces.dom.ElementImpl;
import mf.org.apache.xerces.dom.ElementNSImpl;
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
import mf.org.w3c.dom.NamedNodeMap;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.ProcessingInstruction;
import mf.org.w3c.dom.Text;


/**
 * <p>DOM result augmentor.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: DOMResultAugmentor.java 542522 2007-05-29 13:59:56Z mrglavas $
 */
final class DOMResultAugmentor implements DOMDocumentHandler {

    //
    // Data
    //

    private final DOMValidatorHelper fDOMValidatorHelper;
    private final QName fAttributeQName = new QName();
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private boolean fIgnoreChars;

    public DOMResultAugmentor(DOMValidatorHelper helper) {
        fDOMValidatorHelper = helper;
    }

    @Override
    public void setDOMResult(DOMResult result) {
        fIgnoreChars = false;
        if (result != null) {
            //MF - Renamed - Resourced
            final Node target = result.getNode();
            fDocument = (target.getNodeType() == Node.DOCUMENT_NODE) ? (Document) target : target.getOwnerDocument();
            fDocumentImpl = (fDocument instanceof CoreDocumentImpl) ? (CoreDocumentImpl) fDocument : null;
            fStorePSVI = (fDocument instanceof PSVIDocumentImpl);
            return;
        }
        fDocument = null;
        fDocumentImpl = null;
        fStorePSVI = false;
    }

    @Override
    public void doctypeDecl(DocumentType node) throws XNIException {
    }

    @Override
    public void characters(Text node) throws XNIException {
    }

    @Override
    public void cdata(CDATASection node) throws XNIException {
    }

    @Override
    public void comment(Comment node) throws XNIException {
    }

    @Override
    public void processingInstruction(ProcessingInstruction node)
            throws XNIException {
    }

    @Override
    public void setIgnoringCharacters(boolean ignore) {
        fIgnoreChars = ignore;
    }

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
        final Element currentElement = (Element) fDOMValidatorHelper.getCurrentElement();
        final NamedNodeMap attrMap = currentElement.getAttributes();

        final int oldLength = attrMap.getLength();
        // If it's a Xerces DOM store type information for attributes, set idness, etc..
        if (fDocumentImpl != null) {
            AttrImpl attr;
            for (int i = 0; i < oldLength; ++i) {
                attr = (AttrImpl) attrMap.item(i);

                // write type information to this attribute
                AttributePSVI attrPSVI = (AttributePSVI) attributes.getAugmentations(i).getItem(Constants.ATTRIBUTE_PSVI);
                if (attrPSVI != null) {
                    if (processAttributePSVI(attr, attrPSVI)) {
                        ((ElementImpl) currentElement).setIdAttributeNode(attr, true);
                    }
                }
            }
        }

        final int newLength = attributes.getLength();
        // Add default/fixed attributes
        if (newLength > oldLength) {
            if (fDocumentImpl == null) {
                for (int i = oldLength; i < newLength; ++i) {
                    attributes.getName(i, fAttributeQName);
                    currentElement.setAttributeNS(fAttributeQName.uri, fAttributeQName.rawname, attributes.getValue(i));
                }
            }
            // If it's a Xerces DOM store type information for attributes, set idness, etc..
            else {
                for (int i = oldLength; i < newLength; ++i) {
                    attributes.getName(i, fAttributeQName);
                    AttrImpl attr = (AttrImpl) fDocumentImpl.createAttributeNS(fAttributeQName.uri,
                            fAttributeQName.rawname, fAttributeQName.localpart);
                    attr.setValue(attributes.getValue(i));
                    currentElement.setAttributeNodeNS(attr);

                    // write type information to this attribute
                    AttributePSVI attrPSVI = (AttributePSVI) attributes.getAugmentations(i).getItem(Constants.ATTRIBUTE_PSVI);
                    if (attrPSVI != null) {
                        if (processAttributePSVI(attr, attrPSVI)) {
                            ((ElementImpl) currentElement).setIdAttributeNode(attr, true);
                        }
                    }
                    attr.setSpecified(false);
                }
            }
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
            final Element currentElement = (Element) fDOMValidatorHelper.getCurrentElement();
            currentElement.appendChild(fDocument.createTextNode(text.toString()));
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
        final Node currentElement = fDOMValidatorHelper.getCurrentElement();
        // Write type information to this element
        if (augs != null && fDocumentImpl != null) {
            ElementPSVI elementPSVI = (ElementPSVI) augs.getItem(Constants.ELEMENT_PSVI);
            if (elementPSVI != null) {
                if (fStorePSVI) {
                    ((PSVIElementNSImpl) currentElement).setPSVI(elementPSVI);
                }
                XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
                if (type == null) {
                    type = elementPSVI.getTypeDefinition();
                }
                ((ElementNSImpl) currentElement).setType(type);
            }
        }
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
    }

    /**
     * Returns whether the given attribute is an ID type.
     **/
    private boolean processAttributePSVI(AttrImpl attr, AttributePSVI attrPSVI) {
        if (fStorePSVI) {
            ((PSVIAttrNSImpl) attr).setPSVI(attrPSVI);
        }
        Object type = attrPSVI.getMemberTypeDefinition();
        if (type == null) {
            type = attrPSVI.getTypeDefinition();
            if (type != null) {
                attr.setType(type);
                return ((XSSimpleType) type).isIDType();
            }
        } else {
            attr.setType(type);
            return ((XSSimpleType) type).isIDType();
        }
        return false;
    }

} // DOMResultAugmentor

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

import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.NamespaceContext;
import mf.org.apache.xerces.xni.QName;
import mf.org.apache.xerces.xni.XMLAttributes;
import mf.org.apache.xerces.xni.XMLDTDContentModelHandler;
import mf.org.apache.xerces.xni.XMLDTDHandler;
import mf.org.apache.xerces.xni.XMLDocumentHandler;
import mf.org.apache.xerces.xni.XMLLocator;
import mf.org.apache.xerces.xni.XMLResourceIdentifier;
import mf.org.apache.xerces.xni.XMLString;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import mf.org.apache.xerces.xni.parser.XMLDTDSource;
import mf.org.apache.xerces.xni.parser.XMLDocumentSource;

/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Sandy Gao, IBM
 * @version $Id: DefaultXMLDocumentHandler.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @xerces.internal
 */
public class DefaultXMLDocumentHandler implements XMLDocumentHandler,
        XMLDTDHandler,
        XMLDTDContentModelHandler {

    private XMLDocumentSource fDocumentSource;

    //
    // XMLDocumentHandler methods
    //
    private XMLDTDSource fDTDSource;
    private XMLDTDContentModelSource fCMSource;

    /**
     * Default Constructor
     */
    public DefaultXMLDocumentHandler() {
    }

    /**
     * The start of the document.
     *
     * @param locator  The document locator, or null if the document
     *                 location cannot be reported during the parsing
     *                 of this document. However, it is <em>strongly</em>
     *                 recommended that a locator be supplied that can
     *                 at least report the system identifier of the
     *                 document.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal entities or a document entity that is
     *                 parsed from a java.io.Reader).
     * @param augs     Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext context, Augmentations augs)
            throws XNIException {
    }

    /**
     * Notifies of the presence of an XMLDecl line in the document. If
     * present, this method will be called immediately following the
     * startDocument call.
     *
     * @param version    The XML version.
     * @param encoding   The IANA encoding name of the document, or null if
     *                   not specified.
     * @param standalone The standalone value, or null if not specified.
     * @param augs       Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
            throws XNIException {
    }

    /**
     * Notifies of the presence of the DOCTYPE line in the document.
     *
     * @param rootElement The name of the root element.
     * @param publicId    The public identifier if an external DTD or null
     *                    if the external DTD is specified using SYSTEM.
     * @param systemId    The system identifier if an external DTD, null
     *                    otherwise.
     * @param augs        Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
            throws XNIException {
    }

    /**
     * A comment.
     *
     * @param text The text in the comment.
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by application to signal an error.
     */
    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
    }

    /**
     * A processing instruction. Processing instructions consist of a
     * target name and, optionally, text data. The data is only meaningful
     * to the application.
     * <p/>
     * Typically, a processing instruction's data will contain a series
     * of pseudo-attributes. These pseudo-attributes follow the form of
     * element attributes but are <strong>not</strong> parsed or presented
     * to the application as anything other than text. The application is
     * responsible for parsing the data.
     *
     * @param target The target.
     * @param data   The data or null if none specified.
     * @param augs   Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs)
            throws XNIException {
    }

    /**
     * The start of a namespace prefix mapping. This method will only be
     * called when namespace processing is enabled.
     *
     * @param prefix The namespace prefix.
     * @param uri    The URI bound to the prefix.
     * @param augs   Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startPrefixMapping(String prefix, String uri, Augmentations augs)
            throws XNIException {
    }

    /**
     * The start of an element.
     *
     * @param element    The name of the element.
     * @param attributes The element attributes.
     * @param augs       Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
            throws XNIException {
    }

    /**
     * An empty element.
     *
     * @param element    The name of the element.
     * @param attributes The element attributes.
     * @param augs       Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
            throws XNIException {
    }

    /**
     * This method notifies the start of a general entity.
     * <p/>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     *
     * @param name       The name of the general entity.
     * @param identifier The resource identifier.
     * @param encoding   The auto-detected IANA encoding name of the entity
     *                   stream. This value will be null in those situations
     *                   where the entity encoding is not auto-detected (e.g.
     *                   internal entities or a document entity that is
     *                   parsed from a java.io.Reader).
     * @param augs       Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier,
                                   String encoding,
                                   Augmentations augs) throws XNIException {
    }

    /**
     * Notifies of the presence of a TextDecl line in an entity. If present,
     * this method will be called immediately following the startEntity call.
     * <p/>
     * <strong>Note:</strong> This method will never be called for the
     * document entity; it is only called for external general entities
     * referenced in document content.
     * <p/>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     *
     * @param version  The XML version, or null if not specified.
     * @param encoding The IANA encoding name of the entity.
     * @param augs     Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
    }

    /**
     * This method notifies the end of a general entity.
     * <p/>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     *
     * @param name The name of the entity.
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
    }

    /**
     * Character content.
     *
     * @param text The content.
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
    }

    /**
     * Ignorable whitespace. For this method to be called, the document
     * source must have some way of determining that the text containing
     * only whitespace characters should be considered ignorable. For
     * example, the validator can determine if a length of whitespace
     * characters in the document are ignorable based on the element
     * content model.
     *
     * @param text The ignorable whitespace.
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
    }

    /**
     * The end of an element.
     *
     * @param element The name of the element.
     * @param augs    Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
    }

    /**
     * The end of a namespace prefix mapping. This method will only be
     * called when namespace processing is enabled.
     *
     * @param prefix The namespace prefix.
     * @param augs   Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
    }


    //
    // XMLDTDHandler methods
    //

    /**
     * The start of a CDATA section.
     *
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
    }

    /**
     * The end of a CDATA section.
     *
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
    }

    /**
     * Notifies of the presence of a TextDecl line in an entity. If present,
     * this method will be called immediately following the startEntity call.
     * <p>
     * <strong>Note:</strong> This method is only called for external
     * parameter entities referenced in the DTD.
     *
     * @param version  The XML version, or null if not specified.
     * @param encoding The IANA encoding name of the entity.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
/*
    public void textDecl(String version, String encoding,
                         Augmentations augmentations) throws XNIException {
    }
*/

    /**
     * The end of the document.
     *
     * @param augs Additional information that may include infoset augmentations
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endDocument(Augmentations augs) throws XNIException {
    }

    /**
     * The start of the DTD.
     *
     * @param locator       The document locator, or null if the document
     *                      location cannot be reported during the parsing of
     *                      the document DTD. However, it is <em>strongly</em>
     *                      recommended that a locator be supplied that can
     *                      at least report the base system identifier of the
     *                      DTD.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startDTD(XMLLocator locator, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * This method notifies of the start of a parameter entity. The parameter
     * entity name start with a '%' character.
     *
     * @param name          The name of the parameter entity.
     * @param identifier    The resource identifier.
     * @param encoding      The auto-detected IANA encoding name of the entity
     *                      stream. This value will be null in those situations
     *                      where the entity encoding is not auto-detected (e.g.
     *                      internal parameter entities).
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startParameterEntity(String name,
                                     XMLResourceIdentifier identifier,
                                     String encoding,
                                     Augmentations augmentations) throws XNIException {
    }

    /**
     * A comment.
     *
     * @param text The text in the comment.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by application to signal an error.
     */
/*
    public void comment(XMLString text, Augmentations augmentations) 
        throws XNIException {
    }
*/

    /**
     * A processing instruction. Processing instructions consist of a
     * target name and, optionally, text data. The data is only meaningful
     * to the application.
     * <p>
     * Typically, a processing instruction's data will contain a series
     * of pseudo-attributes. These pseudo-attributes follow the form of
     * element attributes but are <strong>not</strong> parsed or presented
     * to the application as anything other than text. The application is
     * responsible for parsing the data.
     *
     * @param target The target.
     * @param data   The data or null if none specified.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
/*
    public void processingInstruction(String target, XMLString data,
                                      Augmentations augmentations)
        throws XNIException {
    }
*/

    /**
     * This method notifies the end of a parameter entity. Parameter entity
     * names begin with a '%' character.
     *
     * @param name          The name of the parameter entity.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endParameterEntity(String name, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The start of the DTD external subset.
     *
     * @param identifier    The resource identifier.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startExternalSubset(XMLResourceIdentifier identifier,
                                    Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The end of the DTD external subset.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endExternalSubset(Augmentations augmentations)
            throws XNIException {
    }

    /**
     * An element declaration.
     *
     * @param name          The name of the element.
     * @param contentModel  The element content model.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void elementDecl(String name, String contentModel,
                            Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The start of an attribute list.
     *
     * @param elementName   The name of the element that this attribute
     *                      list is associated with.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startAttlist(String elementName,
                             Augmentations augmentations) throws XNIException {
    }

    /**
     * An attribute declaration.
     *
     * @param elementName               The name of the element that this attribute
     *                                  is associated with.
     * @param attributeName             The name of the attribute.
     * @param type                      The attribute type. This value will be one of
     *                                  the following: "CDATA", "ENTITY", "ENTITIES",
     *                                  "ENUMERATION", "ID", "IDREF", "IDREFS",
     *                                  "NMTOKEN", "NMTOKENS", or "NOTATION".
     * @param enumeration               If the type has the value "ENUMERATION" or
     *                                  "NOTATION", this array holds the allowed attribute
     *                                  values; otherwise, this array is null.
     * @param defaultType               The attribute default type. This value will be
     *                                  one of the following: "#FIXED", "#IMPLIED",
     *                                  "#REQUIRED", or null.
     * @param defaultValue              The attribute default value, or null if no
     *                                  default value is specified.
     * @param nonNormalizedDefaultValue The attribute default value with no normalization
     *                                  performed, or null if no default value is specified.
     * @param augmentations             Additional information that may include infoset
     *                                  augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void attributeDecl(String elementName, String attributeName,
                              String type, String[] enumeration,
                              String defaultType, XMLString defaultValue,
                              XMLString nonNormalizedDefaultValue, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The end of an attribute list.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
    }

    /**
     * An internal entity declaration.
     *
     * @param name              The name of the entity. Parameter entity names start with
     *                          '%', whereas the name of a general entity is just the
     *                          entity name.
     * @param text              The value of the entity.
     * @param nonNormalizedText The non-normalized value of the entity. This
     *                          value contains the same sequence of characters that was in
     *                          the internal entity declaration, without any entity
     *                          references expanded.
     * @param augmentations     Additional information that may include infoset
     *                          augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void internalEntityDecl(String name, XMLString text,
                                   XMLString nonNormalizedText,
                                   Augmentations augmentations)
            throws XNIException {
    }

    /**
     * An external entity declaration.
     *
     * @param name          The name of the entity. Parameter entity names start
     *                      with '%', whereas the name of a general entity is just
     *                      the entity name.
     * @param identifier    An object containing all location information
     *                      pertinent to this external entity.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void externalEntityDecl(String name,
                                   XMLResourceIdentifier identifier,
                                   Augmentations augmentations)
            throws XNIException {
    }

    /**
     * An unparsed entity declaration.
     *
     * @param name          The name of the entity.
     * @param identifier    An object containing all location information
     *                      pertinent to this unparsed entity declaration.
     * @param notation      The name of the notation.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void unparsedEntityDecl(String name,
                                   XMLResourceIdentifier identifier,
                                   String notation, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * A notation declaration
     *
     * @param name          The name of the notation.
     * @param identifier    An object containing all location information
     *                      pertinent to this notation.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augmentations) throws XNIException {
    }

    /**
     * The start of a conditional section.
     *
     * @param type          The type of the conditional section. This value will
     *                      either be CONDITIONAL_INCLUDE or CONDITIONAL_IGNORE.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #CONDITIONAL_INCLUDE
     * @see #CONDITIONAL_IGNORE
     */
    @Override
    public void startConditional(short type, Augmentations augmentations)
            throws XNIException {
    }


    //
    // XMLDTDContentModelHandler methods
    //

    /**
     * Characters within an IGNORE conditional section.
     *
     * @param text          The ignored text.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void ignoredCharacters(XMLString text, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The end of a conditional section.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
    }

    /**
     * The end of the DTD.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
    }

    /**
     * The start of a content model. Depending on the type of the content
     * model, specific methods may be called between the call to the
     * startContentModel method and the call to the endContentModel method.
     *
     * @param elementName   The name of the element.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void startContentModel(String elementName, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * A content model of ANY.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #empty
     * @see #startGroup
     */
    @Override
    public void any(Augmentations augmentations) throws XNIException {
    }

    /**
     * A content model of EMPTY.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #any
     * @see #startGroup
     */
    @Override
    public void empty(Augmentations augmentations) throws XNIException {
    }

    /**
     * A start of either a mixed or children content model. A mixed
     * content model will immediately be followed by a call to the
     * <code>pcdata()</code> method. A children content model will
     * contain additional groups and/or elements.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #any
     * @see #empty
     */
    @Override
    public void startGroup(Augmentations augmentations) throws XNIException {
    }

    /**
     * The appearance of "#PCDATA" within a group signifying a
     * mixed content model. This method will be the first called
     * following the content model's <code>startGroup()</code>.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #startGroup
     */
    @Override
    public void pcdata(Augmentations augmentations) throws XNIException {
    }

    /**
     * A referenced element in a mixed or children content model.
     *
     * @param elementName   The name of the referenced element.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void element(String elementName, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The separator between choices or sequences of a mixed or children
     * content model.
     *
     * @param separator     The type of children separator.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #SEPARATOR_CHOICE
     * @see #SEPARATOR_SEQUENCE
     */
    @Override
    public void separator(short separator, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The occurrence count for a child in a children content model or
     * for the mixed content model group.
     *
     * @param occurrence    The occurrence count for the last element
     *                      or group.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ZERO_OR_MORE
     * @see #OCCURS_ONE_OR_MORE
     */
    @Override
    public void occurrence(short occurrence, Augmentations augmentations)
            throws XNIException {
    }

    /**
     * The end of a group for mixed or children content models.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endGroup(Augmentations augmentations) throws XNIException {
    }

    /**
     * The end of a content model.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     * @throws XNIException Thrown by handler to signal an error.
     */
    @Override
    public void endContentModel(Augmentations augmentations) throws XNIException {
    }

    /**
     * Returns the document source.
     */
    @Override
    public XMLDocumentSource getDocumentSource() {
        return fDocumentSource;
    }

    /**
     * Sets the document source.
     */
    @Override
    public void setDocumentSource(XMLDocumentSource source) {
        fDocumentSource = source;
    }

    // return the source from which this handler derives its events
    @Override
    public XMLDTDSource getDTDSource() {
        return fDTDSource;
    }

    // set the source of this handler
    @Override
    public void setDTDSource(XMLDTDSource source) {
        fDTDSource = source;
    }

    // get content model source
    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return fCMSource;
    }

    // set content model source
    @Override
    public void setDTDContentModelSource(XMLDTDContentModelSource source) {
        fCMSource = source;
    }

}
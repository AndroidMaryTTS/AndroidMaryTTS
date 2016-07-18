/*
 * Copyright (c) 2001 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package mf.org.apache.xerces.dom3.as;

/**
 * @deprecated The element name along with the content specification in the context of an
 * <code>ASObject</code>.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ASElementDeclaration extends ASObject {
    // CONTENT_MODEL_TYPES
    /**
     * Represents an EMPTY content type for an Element declaration.
     */
    short EMPTY_CONTENTTYPE = 1;
    /**
     * Represents an ANY content type for an Element declaration.
     */
    short ANY_CONTENTTYPE = 2;
    /**
     * Represents a MIXED content type for an Element declaration. Note that
     * <code>isPCDataOnly</code> would also need to checked, in addition to
     * this, if an element's content model was simply text, as an example.
     */
    short MIXED_CONTENTTYPE = 3;
    /**
     * Represents an ELEMENTS only content type for an Element declaration.
     */
    short ELEMENTS_CONTENTTYPE = 4;

    /**
     * A boolean defining whether the element order and number of the child
     * elements for mixed content type has to be respected or not. For
     * example XML Schema defined mixed content types the order is important
     * and needs to be respected whether for DTD based AS the order and
     * number of child elements are not important.
     */
    boolean getStrictMixedContent();

    /**
     * A boolean defining whether the element order and number of the child
     * elements for mixed content type has to be respected or not. For
     * example XML Schema defined mixed content types the order is important
     * and needs to be respected whether for DTD based AS the order and
     * number of child elements are not important.
     */
    void setStrictMixedContent(boolean strictMixedContent);

    /**
     * Datatype of the element.
     */
    ASDataType getElementType();

    /**
     * Datatype of the element.
     */
    void setElementType(ASDataType elementType);

    /**
     * Boolean defining whether the element type contains child elements and
     * PCDATA or PCDATA only for mixed element types. <code>true</code> if
     * the element is of type PCDATA only. Relevant only for mixed content
     * type elements.
     */
    boolean getIsPCDataOnly();

    /**
     * Boolean defining whether the element type contains child elements and
     * PCDATA or PCDATA only for mixed element types. <code>true</code> if
     * the element is of type PCDATA only. Relevant only for mixed content
     * type elements.
     */
    void setIsPCDataOnly(boolean isPCDataOnly);

    /**
     * The content type of the element. One of <code>EMPTY_CONTENTTYPE</code>,
     * <code>ANY_CONTENTTYPE</code>, <code>MIXED_CONTENTTYPE</code>,
     * <code>ELEMENTS_CONTENTTYPE</code>.
     */
    short getContentType();

    /**
     * The content type of the element. One of <code>EMPTY_CONTENTTYPE</code>,
     * <code>ANY_CONTENTTYPE</code>, <code>MIXED_CONTENTTYPE</code>,
     * <code>ELEMENTS_CONTENTTYPE</code>.
     */
    void setContentType(short contentType);

    /**
     * the URI reference representing the system identifier for the notation
     * declaration, if present, <code>null</code> otherwise.
     */
    String getSystemId();

    /**
     * the URI reference representing the system identifier for the notation
     * declaration, if present, <code>null</code> otherwise.
     */
    void setSystemId(String systemId);

    /**
     * The content model of element.
     */
    ASContentModel getAsCM();

    /**
     * The content model of element.
     */
    void setAsCM(ASContentModel asCM);

    /**
     * The<code>ASNamedObjectMap</code> containing
     * <code>ASAttributeDeclarations</code> for all the attributes that can
     * appear on this type of element.
     */
    ASNamedObjectMap getASAttributeDecls();

    /**
     * The<code>ASNamedObjectMap</code> containing
     * <code>ASAttributeDeclarations</code> for all the attributes that can
     * appear on this type of element.
     */
    void setASAttributeDecls(ASNamedObjectMap ASAttributeDecls);

    /**
     * Adds an <code>ASAttributeDeclaration</code> for the element being
     * declared.
     *
     * @param attributeDecl The new attribute to add. If the attribute
     *                      declaration already exists for the element, the call does not have
     *                      any effect.
     */
    void addASAttributeDecl(ASAttributeDeclaration attributeDecl);

    /**
     * Removes an <code>ASAttributeDeclaration</code> from the element being
     * declared.
     *
     * @param attributeDecl The attribute declaraition to be removed. If the
     *                      attribute declaration does not exist for the element, the call does
     *                      not have any effect.
     * @return <code>null</code> if the attribute does not exist. Otherwise
     * returns the attribute being removed.
     */
    ASAttributeDeclaration removeASAttributeDecl(ASAttributeDeclaration attributeDecl);

}

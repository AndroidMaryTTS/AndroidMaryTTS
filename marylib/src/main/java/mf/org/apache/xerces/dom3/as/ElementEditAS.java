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

import mf.org.w3c.dom.Attr;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;

/**
 * @deprecated This interface extends the <code>Element</code> interface with additional
 * methods for guided document editing. An object implementing this
 * interface must also implement NodeEditAS interface.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ElementEditAS extends NodeEditAS {
    /**
     * The list of qualified element names defined in the abstract schema.
     */
    NodeList getDefinedElementTypes();

    /**
     * Determines element content type.
     *
     * @return Constant for one of EMPTY_CONTENTTYPE, ANY_CONTENTTYPE,
     * MIXED_CONTENTTYPE, ELEMENTS_CONTENTTYPE.
     */
    short contentType();

    /**
     * Determines if the value for specified attribute can be set.
     *
     * @param attrname Name of attribute.
     * @param attrval  Value to be assigned to the attribute.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canSetAttribute(String attrname,
                            String attrval);

    /**
     * Determines if an attribute node can be added with respect to the
     * validity check level.This is an attribute node, there is no need for
     * canSetAttributreNodeNS!
     *
     * @param attrNode <code>Node</code> in which the attribute can possibly
     *                 be set.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canSetAttributeNode(Attr attrNode);

    /**
     * Determines if the attribute with given namespace and qualified name can
     * be created if not already present in the attribute list of the
     * element. If the attribute with same qualified name and namespaceURI
     * is already present in the elements attribute list it tests for the
     * value of the attribute and its prefix to the new value. See DOM core
     * <code>setAttributeNS</code>.
     *
     * @param name         Qualified name of attribute.
     * @param attrval      Value to be assigned to the attribute.
     * @param namespaceURI <code>namespaceURI</code> of namespace.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canSetAttributeNS(String name,
                              String attrval,
                              String namespaceURI);

    /**
     * Verifies if an attribute by the given name can be removed.
     *
     * @param attrname Name of attribute.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canRemoveAttribute(String attrname);

    /**
     * Verifies if an attribute by the given local name and namespace can be
     * removed.
     *
     * @param attrname     Local name of the attribute to be removed.
     * @param namespaceURI The namespace URI of the attribute to remove.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canRemoveAttributeNS(String attrname,
                                 String namespaceURI);

    /**
     * Determines if an attribute node can be removed.
     *
     * @param attrNode The <code>Attr</code> node to remove from the
     *                 attribute list.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canRemoveAttributeNode(Node attrNode);

    /**
     * Returns an <code>NodeList</code> containing the possible
     * <code>Element</code> names that can appear as children of this type
     * of element.
     *
     * @return List of possible children element types of this element.
     */
    NodeList getChildElements();

    /**
     * Returns an <code>NodeList</code> containing the possible
     * <code>Element</code> names that can appear as a parent of this type
     * of element.
     *
     * @return List of possible parent element types of this element.
     */
    NodeList getParentElements();

    /**
     * Returns an <code>NodeList</code> containing all the possible
     * <code>Attr</code>s that can appear with this type of element.
     *
     * @return List of possible attributes of this element.
     */
    NodeList getAttributeList();

    /**
     * Determines if this element is defined in the currently active AS.
     *
     * @param elemTypeName Name of element.
     * @return A boolean that is <code>true</code> if the element is defined,
     * <code>false</code> otherwise.
     */
    boolean isElementDefined(String elemTypeName);

    /**
     * Determines if this element in this namespace is defined in the
     * currently active AS.
     *
     * @param elemTypeName Name of element.
     * @param namespaceURI <code>namespaceURI</code> of namespace.
     * @param name         Qualified name of namespace. This is for sub-elements.
     * @return A boolean that is <code>true</code> if the element is defined,
     * <code>false</code> otherwise.
     */
    boolean isElementDefinedNS(String elemTypeName,
                               String namespaceURI,
                               String name);

}

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

import mf.org.w3c.dom.DOMException;

/**
 * @deprecated To begin with, an abstract schema is a generic structure that could
 * contain both internal and external subsets. An <code>ASModel</code> is an
 * abstract object that could map to a DTD , an XML Schema , a database
 * schema, etc. An <code>ASModel</code> could represent either an internal
 * or an external subset; hence an abstract schema could be composed of an
 * <code>ASModel</code> representing the internal subset and an
 * <code>ASModel</code> representing the external subset. Note that the
 * <code>ASModel</code> representing the external subset could consult the
 * <code>ASModel</code> representing the internal subset. Furthermore, the
 * <code>ASModel</code> representing the internal subset could be set to
 * null by the <code>setInternalAS</code> method as a mechanism for
 * "removal". In addition, only one <code>ASModel</code> representing the
 * external subset can be specified as "active" and it is possible that none
 * are "active". Finally, the <code>ASModel</code> contains the factory
 * methods needed to create a various types of ASObjects like
 * <code>ASElementDeclaration</code>, <code>ASAttributeDeclaration</code>,
 * etc.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ASModel extends ASObject {
    /**
     * <code>true</code> if this <code>ASModel</code> defines the document
     * structure in terms of namespaces and local names ; <code>false</code>
     * if the document structure is defined only in terms of
     * <code>QNames</code>.
     */
    boolean getIsNamespaceAware();

    /**
     * 0 if used internally, 1 if used externally, 2 if not all. An exception
     * will be raised if it is incompatibly shared or in use as an internal
     * subset.
     */
    short getUsageLocation();

    /**
     * The URI reference.
     */
    String getAsLocation();

    /**
     * The URI reference.
     */
    void setAsLocation(String asLocation);

    /**
     * The hint to locating an ASModel.
     */
    String getAsHint();

    /**
     * The hint to locating an ASModel.
     */
    void setAsHint(String asHint);

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * element declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    ASNamedObjectMap getElementDeclarations();

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * attribute declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    ASNamedObjectMap getAttributeDeclarations();

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * notation declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    ASNamedObjectMap getNotationDeclarations();

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * entity declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    ASNamedObjectMap getEntityDeclarations();

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global
     * content model declarations. If one attempts to add, set, or remove a
     * node type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    ASNamedObjectMap getContentModelDeclarations();

    /**
     * This method will allow the nesting or "importation" of ASModels.
     *
     * @param abstractSchema ASModel to be set. Subsequent calls will nest
     *                       the ASModels within the specified <code>ownerASModel</code>.
     */
    void addASModel(ASModel abstractSchema);

    /**
     * To retrieve a list of nested ASModels without reference to names.
     *
     * @return A list of ASModels.
     */
    ASObjectList getASModels();

    /**
     * Removes only the specified <code>ASModel</code> from the list of
     * <code>ASModel</code>s.
     *
     * @param as AS to be removed.
     */
    void removeAS(ASModel as);

    /**
     * Determines if an <code>ASModel</code> itself is valid, i.e., confirming
     * that it's well-formed and valid per its own formal grammar.
     *
     * @return <code>true</code> if the <code>ASModel</code> is valid,
     * <code>false</code> otherwise.
     */
    boolean validate();

    /**
     * Creates an element declaration for the element type specified.
     *
     * @param namespaceURI The <code>namespace URI</code> of the element type
     *                     being declared.
     * @param name         The name of the element. The format of the name could be
     *                     an NCName as defined by XML Namespaces or a Name as defined by XML
     *                     1.0; it's ASModel-dependent.
     * @return A new <code>ASElementDeclaration</code> object with
     * <code>name</code> attribute set to <code>tagname</code> and
     * <code>namespaceURI</code> set to <code>systemId</code>. Other
     * attributes of the element declaration are set through
     * <code>ASElementDeclaration</code> interface methods.
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *                      illegal character.
     */
    ASElementDeclaration createASElementDeclaration(String namespaceURI,
                                                    String name)
            throws DOMException;

    /**
     * Creates an attribute declaration.
     *
     * @param namespaceURI The namespace URI of the attribute being declared.
     * @param name         The name of the attribute. The format of the name could be
     *                     an NCName as defined by XML Namespaces or a Name as defined by XML
     *                     1.0; it's ASModel-dependent.
     * @return A new <code>ASAttributeDeclaration</code> object with
     * appropriate attributes set by input parameters.
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the input <code>name</code>
     *                      parameter contains an illegal character.
     */
    ASAttributeDeclaration createASAttributeDeclaration(String namespaceURI,
                                                        String name)
            throws DOMException;

    /**
     * Creates a new notation declaration.
     *
     * @param namespaceURI The namespace URI of the notation being declared.
     * @param name         The name of the notation. The format of the name could be
     *                     an NCName as defined by XML Namespaces or a Name as defined by XML
     *                     1.0; it's ASModel-dependent.
     * @param systemId     The system identifier for the notation declaration.
     * @param publicId     The public identifier for the notation declaration.
     * @return A new <code>ASNotationDeclaration</code> object with
     * <code>notationName</code> attribute set to <code>name</code> and
     * <code>publicId</code> and <code>systemId</code> set to the
     * corresponding fields.
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *                      illegal character.
     */
    ASNotationDeclaration createASNotationDeclaration(String namespaceURI,
                                                      String name,
                                                      String systemId,
                                                      String publicId)
            throws DOMException;

    /**
     * Creates an ASEntityDeclaration.
     *
     * @param name The name of the entity being declared.
     * @return A new <code>ASEntityDeclaration</code> object with
     * <code>entityName</code> attribute set to name.
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *                      illegal character.
     */
    ASEntityDeclaration createASEntityDeclaration(String name)
            throws DOMException;

    /**
     * Creates an object which describes part of an
     * <code>ASElementDeclaration</code>'s content model.
     *
     * @param minOccurs The minimum occurrence for the subModels of this
     *                  <code>ASContentModel</code>.
     * @param maxOccurs The maximum occurrence for the subModels of this
     *                  <code>ASContentModel</code>.
     * @param operator  operator of type <code>AS_CHOICE</code>,
     *                  <code>AS_SEQUENCE</code>, <code>AS_ALL</code> or
     *                  <code>AS_NONE</code>.
     * @return A new <code>ASContentModel</code> object.
     * @throws DOMASException A DOMASException, e.g., <code>minOccurs &gt; maxOccurs</code>.
     */
    ASContentModel createASContentModel(int minOccurs,
                                        int maxOccurs,
                                        short operator)
            throws DOMASException;

}

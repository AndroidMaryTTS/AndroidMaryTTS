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

package mf.org.apache.xerces.dom;

import java.util.Vector;

import mf.org.apache.xerces.dom3.as.ASAttributeDeclaration;
import mf.org.apache.xerces.dom3.as.ASContentModel;
import mf.org.apache.xerces.dom3.as.ASElementDeclaration;
import mf.org.apache.xerces.dom3.as.ASEntityDeclaration;
import mf.org.apache.xerces.dom3.as.ASModel;
import mf.org.apache.xerces.dom3.as.ASNamedObjectMap;
import mf.org.apache.xerces.dom3.as.ASNotationDeclaration;
import mf.org.apache.xerces.dom3.as.ASObject;
import mf.org.apache.xerces.dom3.as.ASObjectList;
import mf.org.apache.xerces.dom3.as.DOMASException;
import mf.org.apache.xerces.impl.xs.SchemaGrammar;
import mf.org.w3c.dom.DOMException;


/**
 * To begin with, an abstract schema is a generic structure that could
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
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>
 * Document Object Model (DOM) Level 3 Abstract Schemas and Load and Save Specification</a>.
 *
 * @author Pavani Mukthipudi
 * @author Neil Graham
 * @version $Id: ASModelImpl.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @deprecated
 */
@Deprecated
public class ASModelImpl implements ASModel {

    // conceptually, an ASModel may contain grammar information and/or
    // other ASModels.  These two fields divide that function.
    protected Vector fASModels;
    protected SchemaGrammar fGrammar = null;
    //
    // Data
    //
    boolean fNamespaceAware = true;

    //
    // Constructors
    //

    public ASModelImpl() {
        fASModels = new Vector();
    }

    public ASModelImpl(boolean isNamespaceAware) {
        fASModels = new Vector();
        fNamespaceAware = isNamespaceAware;
    }

    //
    // ASObject methods
    //

    /**
     * A code representing the underlying object as defined above.
     */
    @Override
    public short getAsNodeType() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The <code>ASModel</code> object associated with this
     * <code>ASObject</code>. For a node of type <code>AS_MODEL</code>, this
     * is <code>null</code>.
     */
    @Override
    public ASModel getOwnerASModel() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The <code>ASModel</code> object associated with this
     * <code>ASObject</code>. For a node of type <code>AS_MODEL</code>, this
     * is <code>null</code>.
     */
    @Override
    public void setOwnerASModel(ASModel ownerASModel) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The <code>name</code> of this <code>ASObject</code> depending on the
     * <code>ASObject</code> type.
     */
    @Override
    public String getNodeName() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The <code>name</code> of this <code>ASObject</code> depending on the
     * <code>ASObject</code> type.
     */
    @Override
    public void setNodeName(String nodeName) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     */
    @Override
    public String getPrefix() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     */
    @Override
    public void setPrefix(String prefix) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Returns the local part of the qualified name of this
     * <code>ASObject</code>.
     */
    @Override
    public String getLocalName() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Returns the local part of the qualified name of this
     * <code>ASObject</code>.
     */
    @Override
    public void setLocalName(String localName) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  defines how a namespace URI is attached to schema
     * components.
     */
    @Override
    public String getNamespaceURI() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  defines how a namespace URI is attached to schema
     * components.
     */
    @Override
    public void setNamespaceURI(String namespaceURI) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Creates a copy of this <code>ASObject</code>. See text for
     * <code>cloneNode</code> off of <code>Node</code> but substitute AS
     * functionality.
     *
     * @param deep Setting the <code>deep</code> flag on, causes the whole
     *             subtree to be duplicated. Setting it to <code>false</code> only
     *             duplicates its immediate child nodes.
     * @return Cloned <code>ASObject</code>.
     */
    @Override
    public ASObject cloneASObject(boolean deep) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    //
    // ASModel methods
    //

    /**
     * <code>true</code> if this <code>ASModel</code> defines the document
     * structure in terms of namespaces and local names ; <code>false</code>
     * if the document structure is defined only in terms of
     * <code>QNames</code>.
     */
    @Override
    public boolean getIsNamespaceAware() {
        return fNamespaceAware;
    }

    /**
     * 0 if used internally, 1 if used externally, 2 if not all. An exception
     * will be raised if it is incompatibly shared or in use as an internal
     * subset.
     */
    @Override
    public short getUsageLocation() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The URI reference.
     */
    @Override
    public String getAsLocation() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The URI reference.
     */
    @Override
    public void setAsLocation(String asLocation) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The hint to locating an ASModel.
     */
    @Override
    public String getAsHint() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * The hint to locating an ASModel.
     */
    @Override
    public void setAsHint(String asHint) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * If <code>usage</code> is EXTERNAL_SUBSET or NOT_USED, and the
     * <code>ASModel</code> is simply a container of other ASModels.
     */
    public boolean getContainer() {
        return (fGrammar != null);
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * element declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    @Override
    public ASNamedObjectMap getElementDeclarations() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * attribute declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    @Override
    public ASNamedObjectMap getAttributeDeclarations() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * notation declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    @Override
    public ASNamedObjectMap getNotationDeclarations() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global"
     * entity declarations. If one attempts to add, set, or remove a node
     * type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    @Override
    public ASNamedObjectMap getEntityDeclarations() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with
     * <code>ASModel</code> methods, have discernible top-level/"global
     * content model declarations. If one attempts to add, set, or remove a
     * node type other than the intended one, a hierarchy exception (or
     * equivalent is thrown).
     */
    @Override
    public ASNamedObjectMap getContentModelDeclarations() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * This method will allow the nesting or "importation" of ASModels.
     *
     * @param abstractSchema ASModel to be set. Subsequent calls will nest
     *                       the ASModels within the specified <code>ownerASModel</code>.
     */
    @Override
    public void addASModel(ASModel abstractSchema) {
        fASModels.addElement(abstractSchema);
    }

    /**
     * To retrieve a list of nested ASModels without reference to names.
     *
     * @return A list of ASModels.
     */
    @Override
    public ASObjectList getASModels() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Removes only the specified <code>ASModel</code> from the list of
     * <code>ASModel</code>s.
     *
     * @param as AS to be removed.
     */
    @Override
    public void removeAS(ASModel as) {
        fASModels.removeElement(as);
    }

    /**
     * Determines if an <code>ASModel</code> itself is valid, i.e., confirming
     * that it's well-formed and valid per its own formal grammar.
     *
     * @return <code>true</code> if the <code>ASModel</code> is valid,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean validate() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Imports <code>ASObject</code> into ASModel.
     *
     * @param asobject <code>ASObject</code> to be imported.
     */
    public void importASObject(ASObject asobject) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Inserts <code>ASObject</code> into ASModel.
     *
     * @param asobject <code>ASObject</code> to be inserted.
     */
    public void insertASObject(ASObject asobject) {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

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
    @Override
    public ASElementDeclaration createASElementDeclaration(String namespaceURI,
                                                           String name)
            throws DOMException {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

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
    @Override
    public ASAttributeDeclaration createASAttributeDeclaration(String namespaceURI,
                                                               String name)
            throws DOMException {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

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
    @Override
    public ASNotationDeclaration createASNotationDeclaration(String namespaceURI, String name,
                                                             String systemId, String publicId)
            throws DOMException {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

    /**
     * Creates an ASEntityDeclaration.
     *
     * @param name The name of the entity being declared.
     * @return A new <code>ASEntityDeclaration</code> object with
     * <code>entityName</code> attribute set to name.
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name contains an
     *                      illegal character.
     */
    @Override
    public ASEntityDeclaration createASEntityDeclaration(String name)
            throws DOMException {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }

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
    @Override
    public ASContentModel createASContentModel(int minOccurs, int maxOccurs,
                                               short operator) throws DOMASException {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }


    // convenience methods
    public SchemaGrammar getGrammar() {
        return fGrammar;
    }

    public void setGrammar(SchemaGrammar grammar) {
        fGrammar = grammar;
    }

    public Vector getInternalASModels() {
        return fASModels;
    }

}

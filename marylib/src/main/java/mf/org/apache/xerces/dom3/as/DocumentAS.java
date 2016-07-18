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
 * @deprecated This interface extends the <code>Document</code> interface with additional
 * methods for both document and AS editing.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface DocumentAS {
    /**
     * The active external ASModel. Note that the active external
     * <code>ASModel</code> is responsible for consulting the internal
     * ASModel, so if an attribute is declared in the internal
     * <code>ASModel</code> and the corresponding <code>ownerElements</code>
     * points to a <code>ASElementDeclaration</code>s defined in the active
     * external ASModel, changing the active external ASModel will cause the
     * <code>ownerElements</code> to be recomputed. If the
     * <code>ownerElements</code> is not defined in the newly active
     * external ASModel, the <code>ownerElements</code> will be an empty
     * node list.
     */
    ASModel getActiveASModel();

    /**
     * The active external ASModel. Note that the active external
     * <code>ASModel</code> is responsible for consulting the internal
     * ASModel, so if an attribute is declared in the internal
     * <code>ASModel</code> and the corresponding <code>ownerElements</code>
     * points to a <code>ASElementDeclaration</code>s defined in the active
     * external ASModel, changing the active external ASModel will cause the
     * <code>ownerElements</code> to be recomputed. If the
     * <code>ownerElements</code> is not defined in the newly active
     * external ASModel, the <code>ownerElements</code> will be an empty
     * node list.
     */
    void setActiveASModel(ASModel activeASModel);

    /**
     * A list of <code>ASObject</code>s of type <code>AS_MODEL</code>s
     * associated with a document. The <code>addAS</code> method associates
     * a <code>ASModel</code> with a document.
     */
    ASObjectList getBoundASModels();

    /**
     * A list of <code>ASObject</code>s of type <code>AS_MODEL</code>s
     * associated with a document. The <code>addAS</code> method associates
     * a <code>ASModel</code> with a document.
     */
    void setBoundASModels(ASObjectList boundASModels);

    /**
     * Retrieve the internal <code>ASModel</code> of a document.
     *
     * @return <code>ASModel</code>.
     */
    ASModel getInternalAS();

    /**
     * Sets the internal subset <code>ASModel</code> of a document. This could
     * be null as a mechanism for "removal".
     *
     * @param as <code>ASModel</code> to be the internal subset of the
     *           document.
     */
    void setInternalAS(ASModel as);

    /**
     * Associate a <code>ASModel</code> with a document. Can be invoked
     * multiple times to result in a list of <code>ASModel</code>s. Note
     * that only one internal <code>ASModel</code> is associated with the
     * document, however, and that only one of the possible list of
     * <code>ASModel</code>s is active at any one time.
     *
     * @param as <code>ASModel</code> to be associated with the document.
     */
    void addAS(ASModel as);

    /**
     * Removes a <code>ASModel</code> associated with a document. Can be
     * invoked multiple times to remove a number of these in the list of
     * <code>ASModel</code>s.
     *
     * @param as The <code>ASModel</code> to be removed.
     */
    void removeAS(ASModel as);

    /**
     * Gets the AS editing object describing this elementThis method needs to
     * be changed and others added.
     *
     * @return ASElementDeclaration object if the implementation supports "
     * <code>AS-EDIT</code>" feature. Otherwise <code>null</code>.
     * @throws DOMException NOT_FOUND_ERR: Raised if no <code>ASModel</code> is present.
     */
    ASElementDeclaration getElementDeclaration()
            throws DOMException;

    /**
     * Validates the document against the <code>ASModel</code>.
     *
     * @throws DOMASException
     */
    void validate()
            throws DOMASException;

}

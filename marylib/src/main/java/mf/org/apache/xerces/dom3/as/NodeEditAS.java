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

import mf.org.w3c.dom.Node;

/**
 * @deprecated This interface extends a <code>Node</code> from  with additional methods
 * for guided document editing. The expectation is that an instance of the
 * <code>DOMImplementationAS</code> interface can be obtained by using
 * binding-specific casting methods on an instance of the
 * <code>DOMImplementation</code> interface when the DOM implementation
 * supports the feature "<code>AS-DOC</code>".
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface NodeEditAS {
    // ASCheckType
    /**
     * Check for well-formedness of this node.
     */
    short WF_CHECK = 1;
    /**
     * Check for namespace well-formedness includes <code>WF_CHECK</code>.
     */
    short NS_WF_CHECK = 2;
    /**
     * Checks for whether this node is partially valid. It includes
     * <code>NS_WF_CHECK</code>.
     */
    short PARTIAL_VALIDITY_CHECK = 3;
    /**
     * Checks for strict validity of the node with respect to active AS which
     * by definition includes <code>NS_WF_CHECK</code>.
     */
    short STRICT_VALIDITY_CHECK = 4;

    /**
     * Determines whether the <code>insertBefore</code> operation from the
     * <code>Node</code> interface would make this document invalid with
     * respect to the currently active AS. Describe "valid" when referring
     * to partially completed documents.
     *
     * @param newChild <code>Node</code> to be inserted.
     * @param refChild Reference <code>Node</code>.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canInsertBefore(Node newChild,
                            Node refChild);

    /**
     * Has the same arguments as <code>RemoveChild</code>.
     *
     * @param oldChild <code>Node</code> to be removed.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canRemoveChild(Node oldChild);

    /**
     * Has the same arguments as <code>ReplaceChild</code>.
     *
     * @param newChild New <code>Node</code>.
     * @param oldChild <code>Node</code> to be replaced.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canReplaceChild(Node newChild,
                            Node oldChild);

    /**
     * Has the same arguments as <code>AppendChild</code>.
     *
     * @param newChild <code>Node</code> to be appended.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canAppendChild(Node newChild);

    /**
     * Determines if the Node is valid relative to currently active AS. It
     * doesn't normalize before checking if the document is valid. To do so,
     * one would need to explicitly call a normalize method.
     *
     * @param deep                 Setting the <code>deep</code> flag on causes the
     *                             <code>isNodeValid</code> method to check for the whole subtree of
     *                             the current node for validity. Setting it to <code>false</code>
     *                             only checks the current node and its immediate child nodes. The
     *                             <code>validate</code> method on the <code>DocumentAS</code>
     *                             interface, however, checks to determine whether the entire document
     *                             is valid.
     * @param wFValidityCheckLevel Flag to tell at what level validity and
     *                             well-formedness checking is done.
     * @return <code>true</code> if the node is valid/well-formed in the
     * current context and check level defined by
     * <code>wfValidityCheckLevel</code>, <code>false</code> if not.
     * @throws DOMASException <code>NO_AS_AVAILABLE</code>: Raised if the
     *                        <code>DocumentEditAS</code> related to this node does not have any
     *                        active <code>ASModel</code> and <code>wfValidityCheckLevel</code>
     *                        is set to <code>PARTIAL</code> or <code>STRICT_VALIDITY_CHECK</code>
     *                        .
     */
    boolean isNodeValid(boolean deep,
                        short wFValidityCheckLevel)
            throws DOMASException;

}

/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package mf.org.w3c.dom.ranges;

import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.DocumentFragment;
import mf.org.w3c.dom.Node;

/**
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>Document Object Model (DOM) Level 2 Traversal and Range Specification</a>.
 *
 * @since DOM Level 2
 */
public interface Range {
    /**
     * Compare start boundary-point of <code>sourceRange</code> to start
     * boundary-point of Range on which <code>compareBoundaryPoints</code>
     * is invoked.
     */
    short START_TO_START = 0;
    /**
     * Compare start boundary-point of <code>sourceRange</code> to end
     * boundary-point of Range on which <code>compareBoundaryPoints</code>
     * is invoked.
     */
    short START_TO_END = 1;
    /**
     * Compare end boundary-point of <code>sourceRange</code> to end
     * boundary-point of Range on which <code>compareBoundaryPoints</code>
     * is invoked.
     */
    short END_TO_END = 2;
    /**
     * Compare end boundary-point of <code>sourceRange</code> to start
     * boundary-point of Range on which <code>compareBoundaryPoints</code>
     * is invoked.
     */
    short END_TO_START = 3;

    /**
     * Node within which the Range begins
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    Node getStartContainer()
            throws DOMException;

    /**
     * Offset within the starting node of the Range.
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    int getStartOffset()
            throws DOMException;

    /**
     * Node within which the Range ends
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    Node getEndContainer()
            throws DOMException;

    /**
     * Offset within the ending node of the Range.
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    int getEndOffset()
            throws DOMException;

    /**
     * TRUE if the Range is collapsed
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    boolean getCollapsed()
            throws DOMException;

    /**
     * The deepest common ancestor container of the Range's two
     * boundary-points.
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    Node getCommonAncestorContainer()
            throws DOMException;

    /**
     * Sets the attributes describing the start of the Range.
     *
     * @param refNode The <code>refNode</code> value. This parameter must be
     *                different from <code>null</code>.
     * @param offset  The <code>startOffset</code> value.
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if <code>refNode</code> or an ancestor
     *                        of <code>refNode</code> is an Entity, Notation, or DocumentType
     *                        node.
     * @throws DOMException   INDEX_SIZE_ERR: Raised if <code>offset</code> is negative or greater
     *                        than the number of child units in <code>refNode</code>. Child units
     *                        are 16-bit units if <code>refNode</code> is a type of CharacterData
     *                        node (e.g., a Text or Comment node) or a ProcessingInstruction
     *                        node. Child units are Nodes in all other cases.
     *                        <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                        been invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void setStart(Node refNode,
                  int offset)
            throws RangeException, DOMException;

    /**
     * Sets the attributes describing the end of a Range.
     *
     * @param refNode The <code>refNode</code> value. This parameter must be
     *                different from <code>null</code>.
     * @param offset  The <code>endOffset</code> value.
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if <code>refNode</code> or an ancestor
     *                        of <code>refNode</code> is an Entity, Notation, or DocumentType
     *                        node.
     * @throws DOMException   INDEX_SIZE_ERR: Raised if <code>offset</code> is negative or greater
     *                        than the number of child units in <code>refNode</code>. Child units
     *                        are 16-bit units if <code>refNode</code> is a type of CharacterData
     *                        node (e.g., a Text or Comment node) or a ProcessingInstruction
     *                        node. Child units are Nodes in all other cases.
     *                        <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                        been invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void setEnd(Node refNode,
                int offset)
            throws RangeException, DOMException;

    /**
     * Sets the start position to be before a node
     *
     * @param refNode Range starts before <code>refNode</code>
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if the root container of
     *                        <code>refNode</code> is not an Attr, Document, or DocumentFragment
     *                        node or if <code>refNode</code> is a Document, DocumentFragment,
     *                        Attr, Entity, or Notation node.
     * @throws DOMException   INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                        invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void setStartBefore(Node refNode)
            throws RangeException, DOMException;

    /**
     * Sets the start position to be after a node
     *
     * @param refNode Range starts after <code>refNode</code>
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if the root container of
     *                        <code>refNode</code> is not an Attr, Document, or DocumentFragment
     *                        node or if <code>refNode</code> is a Document, DocumentFragment,
     *                        Attr, Entity, or Notation node.
     * @throws DOMException   INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                        invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void setStartAfter(Node refNode)
            throws RangeException, DOMException;

    /**
     * Sets the end position to be before a node.
     *
     * @param refNode Range ends before <code>refNode</code>
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if the root container of
     *                        <code>refNode</code> is not an Attr, Document, or DocumentFragment
     *                        node or if <code>refNode</code> is a Document, DocumentFragment,
     *                        Attr, Entity, or Notation node.
     * @throws DOMException   INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                        invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void setEndBefore(Node refNode)
            throws RangeException, DOMException;

    // CompareHow

    /**
     * Sets the end of a Range to be after a node
     *
     * @param refNode Range ends after <code>refNode</code>.
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if the root container of
     *                        <code>refNode</code> is not an Attr, Document or DocumentFragment
     *                        node or if <code>refNode</code> is a Document, DocumentFragment,
     *                        Attr, Entity, or Notation node.
     * @throws DOMException   INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                        invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void setEndAfter(Node refNode)
            throws RangeException, DOMException;

    /**
     * Collapse a Range onto one of its boundary-points
     *
     * @param toStart If TRUE, collapses the Range onto its start; if FALSE,
     *                collapses it onto its end.
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    void collapse(boolean toStart)
            throws DOMException;

    /**
     * Select a node and its contents
     *
     * @param refNode The node to select.
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if an ancestor of <code>refNode</code>
     *                        is an Entity, Notation or DocumentType node or if
     *                        <code>refNode</code> is a Document, DocumentFragment, Attr, Entity,
     *                        or Notation node.
     * @throws DOMException   INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                        invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void selectNode(Node refNode)
            throws RangeException, DOMException;

    /**
     * Select the contents within a node
     *
     * @param refNode Node to select from
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if <code>refNode</code> or an ancestor
     *                        of <code>refNode</code> is an Entity, Notation or DocumentType node.
     * @throws DOMException   INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                        invoked on this object.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>refNode</code> was created
     *                        from a different document than the one that created this range.
     */
    void selectNodeContents(Node refNode)
            throws RangeException, DOMException;

    /**
     * Compare the boundary-points of two Ranges in a document.
     *
     * @param how         A code representing the type of comparison, as defined
     *                    above.
     * @param sourceRange The <code>Range</code> on which this current
     *                    <code>Range</code> is compared to.
     * @return -1, 0 or 1 depending on whether the corresponding
     * boundary-point of the Range is respectively before, equal to, or
     * after the corresponding boundary-point of <code>sourceRange</code>.
     * @throws DOMException WRONG_DOCUMENT_ERR: Raised if the two Ranges are not in the same
     *                      Document or DocumentFragment.
     *                      <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                      been invoked on this object.
     */
    short compareBoundaryPoints(short how,
                                Range sourceRange)
            throws DOMException;

    /**
     * Removes the contents of a Range from the containing document or
     * document fragment without returning a reference to the removed
     * content.
     *
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if any portion of the content of
     *                      the Range is read-only or any of the nodes that contain any of the
     *                      content of the Range are read-only.
     *                      <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                      been invoked on this object.
     */
    void deleteContents()
            throws DOMException;

    /**
     * Moves the contents of a Range from the containing document or document
     * fragment to a new DocumentFragment.
     *
     * @return A DocumentFragment containing the extracted contents.
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if any portion of the content of
     *                      the Range is read-only or any of the nodes which contain any of the
     *                      content of the Range are read-only.
     *                      <br>HIERARCHY_REQUEST_ERR: Raised if a DocumentType node would be
     *                      extracted into the new DocumentFragment.
     *                      <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                      been invoked on this object.
     */
    DocumentFragment extractContents()
            throws DOMException;

    /**
     * Duplicates the contents of a Range
     *
     * @return A DocumentFragment that contains content equivalent to this
     * Range.
     * @throws DOMException HIERARCHY_REQUEST_ERR: Raised if a DocumentType node would be
     *                      extracted into the new DocumentFragment.
     *                      <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                      been invoked on this object.
     */
    DocumentFragment cloneContents()
            throws DOMException;

    /**
     * Inserts a node into the Document or DocumentFragment at the start of
     * the Range. If the container is a Text node, this will be split at the
     * start of the Range (as if the Text node's splitText method was
     * performed at the insertion point) and the insertion will occur
     * between the two resulting Text nodes. Adjacent Text nodes will not be
     * automatically merged. If the node to be inserted is a
     * DocumentFragment node, the children will be inserted rather than the
     * DocumentFragment node itself.
     *
     * @param newNode The node to insert at the start of the Range
     * @throws DOMException   NO_MODIFICATION_ALLOWED_ERR: Raised if an ancestor container of the
     *                        start of the Range is read-only.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code>newNode</code> and the
     *                        container of the start of the Range were not created from the same
     *                        document.
     *                        <br>HIERARCHY_REQUEST_ERR: Raised if the container of the start of
     *                        the Range is of a type that does not allow children of the type of
     *                        <code>newNode</code> or if <code>newNode</code> is an ancestor of
     *                        the container.
     *                        <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                        been invoked on this object.
     * @throws RangeException INVALID_NODE_TYPE_ERR: Raised if <code>newNode</code> is an Attr,
     *                        Entity, Notation, or Document node.
     */
    void insertNode(Node newNode)
            throws DOMException, RangeException;

    /**
     * Reparents the contents of the Range to the given node and inserts the
     * node at the position of the start of the Range.
     *
     * @param newParent The node to surround the contents with.
     * @throws DOMException   NO_MODIFICATION_ALLOWED_ERR: Raised if an ancestor container of
     *                        either boundary-point of the Range is read-only.
     *                        <br>WRONG_DOCUMENT_ERR: Raised if <code> newParent</code> and the
     *                        container of the start of the Range were not created from the same
     *                        document.
     *                        <br>HIERARCHY_REQUEST_ERR: Raised if the container of the start of
     *                        the Range is of a type that does not allow children of the type of
     *                        <code>newParent</code> or if <code>newParent</code> is an ancestor
     *                        of the container or if <code>node</code> would end up with a child
     *                        node of a type not allowed by the type of <code>node</code>.
     *                        <br>INVALID_STATE_ERR: Raised if <code>detach()</code> has already
     *                        been invoked on this object.
     * @throws RangeException BAD_BOUNDARYPOINTS_ERR: Raised if the Range partially selects a
     *                        non-text node.
     *                        <br>INVALID_NODE_TYPE_ERR: Raised if <code> node</code> is an Attr,
     *                        Entity, DocumentType, Notation, Document, or DocumentFragment node.
     */
    void surroundContents(Node newParent)
            throws DOMException, RangeException;

    /**
     * Produces a new Range whose boundary-points are equal to the
     * boundary-points of the Range.
     *
     * @return The duplicated Range.
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    Range cloneRange()
            throws DOMException;

    /**
     * Returns the contents of a Range as a string. This string contains only
     * the data characters, not any markup.
     *
     * @return The contents of the Range.
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    @Override
    String toString()
            throws DOMException;

    /**
     * Called to indicate that the Range is no longer in use and that the
     * implementation may relinquish any resources associated with this
     * Range. Subsequent calls to any methods or attribute getters on this
     * Range will result in a <code>DOMException</code> being thrown with an
     * error code of <code>INVALID_STATE_ERR</code>.
     *
     * @throws DOMException INVALID_STATE_ERR: Raised if <code>detach()</code> has already been
     *                      invoked on this object.
     */
    void detach()
            throws DOMException;

}

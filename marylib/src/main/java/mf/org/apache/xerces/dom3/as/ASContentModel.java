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
 * @deprecated The content model of a declared element.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ASContentModel extends ASObject {
    /**
     * Signifies unbounded upper limit. The MAX_VALUE value is
     * <code>0xFFFFFFFF FFFFFFFF</code>. This needs to be better defined in
     * the generated bindings.
     */
    int AS_UNBOUNDED = Integer.MAX_VALUE;
    // ASContentModelType
    /**
     * This constant value signifies a sequence operator. For example, in a
     * DTD, this would be the '<code>,</code>' operator.
     */
    short AS_SEQUENCE = 0;
    /**
     * This constant value signifies a choice operator. For example, in a DTD,
     * this would be the '<code>|</code>' operator.
     */
    short AS_CHOICE = 1;
    /**
     * All of the above.
     */
    short AS_ALL = 2;
    /**
     * None of the above, i.e., neither a choice nor sequence operator.
     */
    short AS_NONE = 3;

    /**
     * One of <code>AS_CHOICE</code>, <code>AS_SEQUENCE</code>,
     * <code>AS_ALL</code> or <code>AS_NONE</code>. The operator is applied
     * to all the components(ASObjects) in the <code>subModels</code>. For
     * example, if the list operator is <code>AS_CHOICE</code> and the
     * components in subModels are a, b and c then the abstract schema for
     * the element being declared is <code>(a|b|c)</code>.
     */
    short getListOperator();

    /**
     * One of <code>AS_CHOICE</code>, <code>AS_SEQUENCE</code>,
     * <code>AS_ALL</code> or <code>AS_NONE</code>. The operator is applied
     * to all the components(ASObjects) in the <code>subModels</code>. For
     * example, if the list operator is <code>AS_CHOICE</code> and the
     * components in subModels are a, b and c then the abstract schema for
     * the element being declared is <code>(a|b|c)</code>.
     */
    void setListOperator(short listOperator);

    /**
     * min occurrence for this content particle. Its value may be 0 or a
     * positive integer.
     */
    int getMinOccurs();

    /**
     * min occurrence for this content particle. Its value may be 0 or a
     * positive integer.
     */
    void setMinOccurs(int minOccurs);

    /**
     * maximum occurrence for this content particle. Its value may be
     * <code>0</code>, a positive integer, or <code>AS_UNBOUNDED</code> to
     * indicate that no upper limit has been set.
     */
    int getMaxOccurs();

    /**
     * maximum occurrence for this content particle. Its value may be
     * <code>0</code>, a positive integer, or <code>AS_UNBOUNDED</code> to
     * indicate that no upper limit has been set.
     */
    void setMaxOccurs(int maxOccurs);

    /**
     * Pointers to <code>ASObject</code>s such as
     * <code> ASElementDeclaration</code>s and further
     * <code>ASContentModel</code>s.
     */
    ASObjectList getSubModels();

    /**
     * Pointers to <code>ASObject</code>s such as
     * <code> ASElementDeclaration</code>s and further
     * <code>ASContentModel</code>s.
     */
    void setSubModels(ASObjectList subModels);

    /**
     * Removes the <code>ASObject</code> in the submodel. Nodes that already
     * exist in the list are moved as needed.
     *
     * @param oldNode The node to be removed.
     */
    void removesubModel(ASObject oldNode);

    /**
     * Inserts a new node in the submodel. Nodes that already exist in the
     * list are moved as needed.
     *
     * @param newNode The new node to be inserted.
     * @throws DOMASException <code>DUPLICATE_NAME_ERR</code>: Raised if a element declaration
     *                        already exists with the same name within an <code>AS_CHOICE</code>
     *                        operator.
     */
    void insertsubModel(ASObject newNode)
            throws DOMASException;

    /**
     * Appends a new node to the end of the list representing the
     * <code>subModels</code>.
     *
     * @param newNode The new node to be appended.
     * @return the length of the <code>subModels</code>.
     * @throws DOMASException <code>DUPLICATE_NAME_ERR</code>: Raised if a element declaration
     *                        already exists with the same name within an <code>AS_CHOICE</code>
     *                        operator.
     *                        <br> <code>TYPE_ERR</code>: Raised if type is neither an
     *                        <code>ASContentModel</code> nor an <code>ASElementDeclaration</code>
     *                        .
     */
    int appendsubModel(ASObject newNode)
            throws DOMASException;

}

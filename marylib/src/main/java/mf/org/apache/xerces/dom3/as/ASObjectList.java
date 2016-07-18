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
 * @deprecated The <code>ASObjectList</code> interface provides the abstraction of an
 * ordered collection of AS nodes, without defining or constraining how this
 * collection is implemented. <code>ASObjectList</code> objects in the DOM
 * AS are live.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ASObjectList {
    /**
     * The number of <code>ASObjects</code> in the list. The range of valid
     * child node indices is 0 to <code>length-1</code> inclusive.
     */
    int getLength();

    /**
     * Returns the <code>index</code>th item in the collection. The index
     * starts at 0. If <code>index</code> is greater than or equal to the
     * number of nodes in the list, this returns <code>null</code>.
     *
     * @param index index into the collection.
     * @return The <code>ASObject</code> at the <code>index</code>th position
     * in the <code>ASObjectList</code>, or <code>null</code> if that is
     * not a valid index.
     */
    ASObject item(int index);

}

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

package mf.org.apache.html.dom;

import java.io.Serializable;

import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.html.HTMLAnchorElement;
import mf.org.w3c.dom.html.HTMLAppletElement;
import mf.org.w3c.dom.html.HTMLAreaElement;
import mf.org.w3c.dom.html.HTMLCollection;
import mf.org.w3c.dom.html.HTMLElement;
import mf.org.w3c.dom.html.HTMLFormElement;
import mf.org.w3c.dom.html.HTMLImageElement;
import mf.org.w3c.dom.html.HTMLObjectElement;
import mf.org.w3c.dom.html.HTMLOptionElement;
import mf.org.w3c.dom.html.HTMLTableCellElement;
import mf.org.w3c.dom.html.HTMLTableRowElement;
import mf.org.w3c.dom.html.HTMLTableSectionElement;

/**
 * Implements {@link mf.org.w3c.dom.html.HTMLCollection} to traverse any named
 * elements on a {@link mf.org.w3c.dom.html.HTMLDocument}. The elements type to
 * look for is identified in the constructor by code. This collection is not
 * optimized for traversing large trees.
 * <p/>
 * The collection has to meet two requirements: it has to be live, and it has
 * to traverse depth first and always return results in that order. As such,
 * using an object container (such as {@link java.util.Vector}) is expensive on
 * insert/remove operations. Instead, the collection has been implemented using
 * three traversing functions. As a result, operations on large documents will
 * result in traversal of the entire document tree and consume a considerable
 * amount of time.
 * <p/>
 * Note that synchronization on the traversed document cannot be achieved.
 * The document itself cannot be locked, and locking each traversed node is
 * likely to lead to a dead lock condition. Therefore, there is a chance of the
 * document being changed as results are fetched; in all likelihood, the results
 * might be out dated, but not erroneous.
 *
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1035042 $ $Date: 2010-11-14 13:41:49 -0500 (Sun, 14 Nov 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLCollection
 */
class HTMLCollectionImpl
        implements HTMLCollection, Serializable {

    /**
     * Request collection of all anchors in document: &lt;A&gt; elements that
     * have a <code>name</code> attribute.
     */
    static final short ANCHOR = 1;
    /**
     * Request collection of all forms in document: &lt;FORM&gt; elements.
     */
    static final short FORM = 2;
    /**
     * Request collection of all images in document: &lt;IMG&gt; elements.
     */
    static final short IMAGE = 3;
    /**
     * Request collection of all Applets in document: &lt;APPLET&gt; and
     * &lt;OBJECT&gt; elements (&lt;OBJECT&gt; must contain an Applet).
     */
    static final short APPLET = 4;
    /**
     * Request collection of all links in document: &lt;A&gt; and &lt;AREA&gt;
     * elements (must have a <code>href</code> attribute).
     */
    static final short LINK = 5;
    /**
     * Request collection of all options in selection: &lt;OPTION&gt; elements in
     * &lt;SELECT&gt; or &lt;OPTGROUP&gt;.
     */
    static final short OPTION = 6;
    /**
     * Request collection of all rows in table: &lt;TR&gt; elements in table or
     * table section.
     */
    static final short ROW = 7;
    /**
     * Request collection of all form elements: &lt;INPUT&gt;, &lt;BUTTON&gt;,
     * &lt;SELECT&gt;, and &lt;TEXTAREA&gt; elements inside form &lt;FORM&gt;.
     */
    static final short ELEMENT = 8;
    /**
     * Request collection of all areas in map: &lt;AREA&gt; element in &lt;MAP&gt;
     * (non recursive).
     */
    static final short AREA = -1;
    /**
     * Request collection of all table bodies in table: &lt;TBODY&gt; element in
     * table &lt;TABLE&gt; (non recursive).
     */
    static final short TBODY = -2;
    /**
     * Request collection of all cells in row: &lt;TD&gt; and &lt;TH&gt;
     * elements in &lt;TR&gt; (non recursive).
     */
    static final short CELL = -3;
    private static final long serialVersionUID = 9112122196669185082L;
    /**
     * Indicates what this collection is looking for. Holds one of the enumerated
     * values and used by {@link #collectionMatch}. Set by the constructor and
     * determine the collection's use for its life time.
     */
    private short _lookingFor;


    /**
     * This is the top level element underneath which the collection exists.
     */
    private Element _topLevel;


    /**
     * Construct a new collection that retrieves element of the specific type
     * (<code>lookingFor</code>) from the specific document portion
     * (<code>topLevel</code>).
     *
     * @param topLevel   The element underneath which the collection exists
     * @param lookingFor Code indicating what elements to look for
     */
    HTMLCollectionImpl(HTMLElement topLevel, short lookingFor) {
        if (topLevel == null)
            throw new NullPointerException("HTM011 Argument 'topLevel' is null.");
        _topLevel = topLevel;
        _lookingFor = lookingFor;
    }


    /**
     * Returns the length of the collection. This method might traverse the
     * entire document tree.
     *
     * @return Length of the collection
     */
    @Override
    public final int getLength() {
        // Call recursive function on top-level element.
        return getLength(_topLevel);
    }


    /**
     * Retrieves the indexed node from the collection. Nodes are numbered in
     * tree order - depth-first traversal order. This method might traverse
     * the entire document tree.
     *
     * @param index The index of the node to return
     * @return The specified node or null if no such node found
     */
    @Override
    public final Node item(int index) {
        if (index < 0)
            throw new IllegalArgumentException("HTM012 Argument 'index' is negative.");
        // Call recursive function on top-level element.
        return item(_topLevel, new CollectionIndex(index));
    }


    /**
     * Retrieves the named node from the collection. The name is matched case
     * sensitive against the <TT>id</TT> attribute of each element in the
     * collection, returning the first match. The tree is traversed in
     * depth-first order. This method might traverse the entire document tree.
     *
     * @param name The name of the node to return
     * @return The specified node or null if no such node found
     */
    @Override
    public final Node namedItem(String name) {
        if (name == null)
            throw new NullPointerException("HTM013 Argument 'name' is null.");
        // Call recursive function on top-level element.
        return namedItem(_topLevel, name);
    }


    /**
     * Recursive function returns the number of elements of a particular type
     * that exist under the top level element. This is a recursive function
     * and the top level element is passed along.
     *
     * @param topLevel Top level element from which to scan
     * @return Number of elements
     */
    private int getLength(Element topLevel) {
        int length;
        Node node;

        synchronized (topLevel) {
            // Always count from zero and traverse all the childs of the
            // current element in the order they appear.
            length = 0;
            node = topLevel.getFirstChild();
            while (node != null) {
                // If a particular node is an element (could be HTML or XML),
                // do two things: if it's the one we're looking for, count
                // another matched element; at any rate, traverse it's
                // children as well.
                if (node instanceof Element) {
                    if (collectionMatch((Element) node, null))
                        ++length;
                    else if (recurse())
                        length += getLength((Element) node);
                }
                node = node.getNextSibling();
            }
        }
        return length;
    }


    /**
     * Recursive function returns the numbered element of a particular type
     * that exist under the top level element. This is a recursive function
     * and the top level element is passed along.
     * <p/>
     * Note that this function must call itself with an index and get back both
     * the element (if one was found) and the new index which is decremeneted
     * for any like element found. Since integers are only passed by value,
     * this function makes use of a separate class ({@link CollectionIndex})
     * to hold that index.
     *
     * @param topLevel Top level element from which to scan
     * @param index    The index of the item to retreive
     * @return Number of elements
     * @see CollectionIndex
     */
    private Node item(Element topLevel, CollectionIndex index) {
        Node node;
        Node result;

        synchronized (topLevel) {
            // Traverse all the childs of the current element in the order
            // they appear. Count from the index backwards until you reach
            // matching element with an index of zero. Return that element.
            node = topLevel.getFirstChild();
            while (node != null) {
                // If a particular node is an element (could be HTML or XML),
                // do two things: if it's the one we're looking for, decrease
                // the index and if zero, return this node; at any rate,
                // traverse it's children as well.
                if (node instanceof Element) {
                    if (collectionMatch((Element) node, null)) {
                        if (index.isZero())
                            return node;
                        index.decrement();
                    } else if (recurse()) {
                        result = item((Element) node, index);
                        if (result != null)
                            return result;
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }


    /**
     * Recursive function returns an element of a particular type with the
     * specified name (<TT>id</TT> attribute).
     *
     * @param topLevel Top level element from which to scan
     * @param name     The named element to look for
     * @return The first named element found
     */
    private Node namedItem(Element topLevel, String name) {
        Node node;
        Node result;

        synchronized (topLevel) {
            // Traverse all the childs of the current element in the order
            // they appear.
            node = topLevel.getFirstChild();
            while (node != null) {
                // If a particular node is an element (could be HTML or XML),
                // do two things: if it's the one we're looking for, and the
                // name (id attribute) attribute is the one we're looking for,
                // return this element; otherwise, traverse it's children.
                if (node instanceof Element) {
                    if (collectionMatch((Element) node, name))
                        return node;
                    else if (recurse()) {
                        result = namedItem((Element) node, name);
                        if (result != null)
                            return result;
                    }
                }
                node = node.getNextSibling();
            }
            return node;
        }
    }


    /**
     * Returns true if scanning methods should iterate through the collection.
     * When looking for elements in the document, recursing is needed to traverse
     * the full document tree. When looking inside a specific element (e.g. for a
     * cell inside a row), recursing can lead to erroneous results.
     *
     * @return True if methods should recurse to traverse entire tree
     */
    protected boolean recurse() {
        return _lookingFor > 0;
    }


    /**
     * Determines if current element matches based on what we're looking for.
     * The element is passed along with an optional identifier name. If the
     * element is the one we're looking for, return true. If the name is also
     * specified, the name must match the <code>id</code> attribute
     * (match <code>name</code> first for anchors).
     *
     * @param elem The current element
     * @param name The identifier name or null
     * @return The element matches what we're looking for
     */
    protected boolean collectionMatch(Element elem, String name) {
        boolean match;

        synchronized (elem) {
            // Begin with no matching. Depending on what we're looking for,
            // attempt to match based on the element type. This is the quickest
            // way to match involving only a cast. Do the expensive string
            // comparison later on.
            match = false;
            switch (_lookingFor) {
                case ANCHOR:
                    // Anchor is an <A> element with a 'name' attribute. Otherwise, it's
                    // just a link.
                    match = (elem instanceof HTMLAnchorElement) &&
                            elem.getAttribute("name").length() > 0;
                    break;
                case FORM:
                    // Any <FORM> element.
                    match = (elem instanceof HTMLFormElement);
                    break;
                case IMAGE:
                    // Any <IMG> element. <OBJECT> elements with images are not returned.
                    match = (elem instanceof HTMLImageElement);
                    break;
                case APPLET:
                    // Any <APPLET> element, and any <OBJECT> element which represents an
                    // Applet. This is determined by 'codetype' attribute being
                    // 'application/java' or 'classid' attribute starting with 'java:'.
                    match = (elem instanceof HTMLAppletElement) ||
                            (elem instanceof HTMLObjectElement &&
                                    ("application/java".equals(elem.getAttribute("codetype")) ||
                                            elem.getAttribute("classid").startsWith("java:")));
                    break;
                case ELEMENT:
                    // All form elements implement HTMLFormControl for easy identification.
                    match = (elem instanceof HTMLFormControl);
                    break;
                case LINK:
                    // Any <A> element, and any <AREA> elements with an 'href' attribute.
                    match = ((elem instanceof HTMLAnchorElement ||
                            elem instanceof HTMLAreaElement) &&
                            elem.getAttribute("href").length() > 0);
                    break;
                case AREA:
                    // Any <AREA> element.
                    match = (elem instanceof HTMLAreaElement);
                    break;
                case OPTION:
                    // Any <OPTION> element.
                    match = (elem instanceof HTMLOptionElement);
                    break;
                case ROW:
                    // Any <TR> element.
                    match = (elem instanceof HTMLTableRowElement);
                    break;
                case TBODY:
                    // Any <TBODY> element (one of three table section types).
                    match = (elem instanceof HTMLTableSectionElement &&
                            elem.getTagName().equals("TBODY"));
                    break;
                case CELL:
                    // Any <TD> or <TH> element.
                    match = (elem instanceof HTMLTableCellElement);
                    break;
            }

            // If element type was matched and a name was specified, must also match
            // the name against either the 'id' or the 'name' attribute. The 'name'
            // attribute is relevant only for <A> elements for backward compatibility.
            if (match && name != null) {
                // If an anchor and 'name' attribute matches, return true. Otherwise,
                // try 'id' attribute.
                if (elem instanceof HTMLAnchorElement &&
                        name.equals(elem.getAttribute("name")))
                    return true;
                match = name.equals(elem.getAttribute("id"));
            }
        }
        return match;
    }


}


/**
 * {@link CollectionImpl#item} must traverse down the tree and decrement the
 * index until it matches an element who's index is zero. Since integers are
 * passed by value, this class servers to pass the index into each recursion
 * by reference. It encompasses all the operations that need be performed on
 * the index, although direct access is possible.
 *
 * @xerces.internal
 * @see CollectionImpl#item
 */
class CollectionIndex {


    /**
     * Holds the actual value that is passed by reference using this class.
     */
    private int _index;


    /**
     * Constructs a new index with the specified initial value. The index will
     * then be decremeneted until it reaches zero.
     *
     * @param index The initial value
     */
    CollectionIndex(int index) {
        _index = index;
    }

    /**
     * Returns the current index.
     *
     * @return Current index
     */
    int getIndex() {
        return _index;
    }

    /**
     * Decrements the index by one.
     */
    void decrement() {
        --_index;
    }

    /**
     * Returns true if index is zero (or negative).
     *
     * @return True if index is zero
     */
    boolean isZero() {
        return _index <= 0;
    }


}

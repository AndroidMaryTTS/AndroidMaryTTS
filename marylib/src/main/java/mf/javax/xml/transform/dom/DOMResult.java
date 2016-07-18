/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: DOMResult.java,v 1.5 2010-11-01 04:36:12 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.transform.dom;

import mf.javax.xml.transform.Result;
import mf.org.w3c.dom.Node;

/**
 * <p>Acts as a holder for a transformation result tree in the form of a Document Object Model (DOM) tree.</p>
 * <p/>
 * <p>If no output DOM source is set, the transformation will create a Document node as the holder for the result of the transformation,
 * which may be retrieved with {@link #getNode()}.</p>
 *
 * @author <a href="Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.5 $, $Date: 2010-11-01 04:36:12 $
 */
public class DOMResult implements Result {

    /**
     * <p>If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns <code>true</code> when passed this value as an argument,
     * the <code>Transformer</code> supports <code>Result</code> output of this type.</p>
     */
    public static final String FEATURE = "http://javax.xml.transform.dom.DOMResult/feature";
    /**
     * <p>The node to which the transformation will be appended.</p>
     */
    private Node node = null;
    /**
     * <p>The child node before which the result nodes will be inserted.</p>
     *
     * @since 1.5
     */
    private Node nextSibling = null;
    /**
     * <p>The System ID that may be used in association with the node.</p>
     */
    private String systemId = null;

    /**
     * <p>Zero-argument default constructor.</p>
     * <p/>
     * <p><code>node</code>,
     * <code>siblingNode</code> and
     * <code>systemId</code>
     * will be set to <code>null</code>.</p>
     */
    public DOMResult() {
        setNode(null);
        setNextSibling(null);
        setSystemId(null);
    }

    /**
     * <p>Use a DOM node to create a new output target.</p>
     * <p/>
     * <p>In practice, the node should be
     * a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or
     * a {@link org.w3c.dom.Element} node.
     * In other words, a node that accepts children.</p>
     * <p/>
     * <p><code>siblingNode</code> and
     * <code>systemId</code>
     * will be set to <code>null</code>.</p>
     *
     * @param node The DOM node that will contain the result tree.
     */
    public DOMResult(Node node) {
        setNode(node);
        setNextSibling(null);
        setSystemId(null);
    }

    /**
     * <p>Use a DOM node to create a new output target with the specified System ID.<p>
     * <p/>
     * <p>In practice, the node should be
     * a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or
     * a {@link org.w3c.dom.Element} node.
     * In other words, a node that accepts children.</p>
     * <p/>
     * <p><code>siblingNode</code> will be set to <code>null</code>.</p>
     *
     * @param node     The DOM node that will contain the result tree.
     * @param systemId The system identifier which may be used in association with this node.
     */
    public DOMResult(Node node, String systemId) {
        setNode(node);
        setNextSibling(null);
        setSystemId(systemId);
    }

    /**
     * <p>Use a DOM node to create a new output target specifying the child node where the result nodes should be inserted before.</p>
     * <p/>
     * <p>In practice, <code>node</code> and <code>nextSibling</code> should be
     * a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or
     * a {@link org.w3c.dom.Element} node.
     * In other words, a node that accepts children.</p>
     * <p/>
     * <p>Use <code>nextSibling</code> to specify the child node
     * where the result nodes should be inserted before.
     * If <code>nextSibling</code> is not a sibling of <code>node</code>,
     * then an <code>IllegalArgumentException</code> is thrown.
     * If <code>node</code> is <code>null</code> and <code>nextSibling</code> is not <code>null</code>,
     * then an <code>IllegalArgumentException</code> is thrown.
     * If <code>nextSibling</code> is <code>null</code>,
     * then the behavior is the same as calling {@link #DOMResult(Node node)},
     * i.e. append the result nodes as the last child of the specified <code>node</code>.</p>
     * <p/>
     * <p><code>systemId</code> will be set to <code>null</code>.</p>
     *
     * @param node        The DOM node that will contain the result tree.
     * @param nextSibling The child node where the result nodes should be inserted before.
     * @throws IllegalArgumentException If <code>nextSibling</code> is not a sibling of <code>node</code> or
     *                                  <code>node</code> is <code>null</code> and <code>nextSibling</code>
     *                                  is not <code>null</code>.
     * @since 1.5
     */
    public DOMResult(Node node, Node nextSibling) {

        // does the corrent parent/child relationship exist?
        if (nextSibling != null) {
            // cannot be a sibling of a null node
            if (node == null) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }

            // nextSibling contained by node?
            if ((node.compareDocumentPosition(nextSibling) & Node.DOCUMENT_POSITION_CONTAINED_BY) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }

        setNode(node);
        setNextSibling(nextSibling);
        setSystemId(null);
    }

    /**
     * <p>Use a DOM node to create a new output target specifying the child node where the result nodes should be inserted before and
     * the specified System ID.</p>
     * <p/>
     * <p>In practice, <code>node</code> and <code>nextSibling</code> should be
     * a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or a
     * {@link org.w3c.dom.Element} node.
     * In other words, a node that accepts children.</p>
     * <p/>
     * <p>Use <code>nextSibling</code> to specify the child node
     * where the result nodes should be inserted before.
     * If <code>nextSibling</code> is not a sibling of <code>node</code>,
     * then an <code>IllegalArgumentException</code> is thrown.
     * If <code>node</code> is <code>null</code> and <code>nextSibling</code> is not <code>null</code>,
     * then an <code>IllegalArgumentException</code> is thrown.
     * If <code>nextSibling</code> is <code>null</code>,
     * then the behavior is the same as calling {@link #DOMResult(Node node, String systemId)},
     * i.e. append the result nodes as the last child of the specified node and use the specified System ID.</p>
     *
     * @param node        The DOM node that will contain the result tree.
     * @param nextSibling The child node where the result nodes should be inserted before.
     * @param systemId    The system identifier which may be used in association with this node.
     * @throws IllegalArgumentException If <code>nextSibling</code> is not a
     *                                  sibling of <code>node</code> or
     *                                  <code>node</code> is <code>null</code> and <code>nextSibling</code>
     *                                  is not <code>null</code>.
     * @since 1.5
     */
    public DOMResult(Node node, Node nextSibling, String systemId) {

        // does the corrent parent/child relationship exist?
        if (nextSibling != null) {
            // cannot be a sibling of a null node
            if (node == null) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }

            // nextSibling contained by node?
            if ((node.compareDocumentPosition(nextSibling) & Node.DOCUMENT_POSITION_CONTAINED_BY) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }

        setNode(node);
        setNextSibling(nextSibling);
        setSystemId(systemId);
    }

    /**
     * <p>Get the node that will contain the result DOM tree.</p>
     * <p/>
     * <p>If no node was set via
     * {@link #DOMResult(Node node)},
     * {@link #DOMResult(Node node, String systeId)},
     * {@link #DOMResult(Node node, Node nextSibling)},
     * {@link #DOMResult(Node node, Node nextSibling, String systemId)} or
     * {@link #setNode(Node node)},
     * then the node will be set by the transformation, and may be obtained from this method once the transformation is complete.
     * Calling this method before the transformation will return <code>null</code>.</p>
     *
     * @return The node to which the transformation will be appended.
     */
    public Node getNode() {
        return node;
    }

    /**
     * <p>Set the node that will contain the result DOM tree.<p>
     * <p/>
     * <p>In practice, the node should be
     * a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or
     * a {@link org.w3c.dom.Element} node.
     * In other words, a node that accepts children.</p>
     * <p/>
     * <p>An <code>IllegalStateException</code> is thrown if
     * <code>nextSibling</code> is not <code>null</code> and
     * <code>node</code> is not a parent of <code>nextSibling</code>.
     * An <code>IllegalStateException</code> is thrown if <code>node</code> is <code>null</code> and
     * <code>nextSibling</code> is not <code>null</code>.</p>
     *
     * @param node The node to which the transformation will be appended.
     * @throws IllegalStateException If <code>nextSibling</code> is not
     *                               <code>null</code> and
     *                               <code>nextSibling</code> is not a child of <code>node</code> or
     *                               <code>node</code> is <code>null</code> and
     *                               <code>nextSibling</code> is not <code>null</code>.
     */
    public void setNode(Node node) {
        // does the corrent parent/child relationship exist?
        if (nextSibling != null) {
            // cannot be a sibling of a null node
            if (node == null) {
                throw new IllegalStateException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }

            // nextSibling contained by node?
            if ((node.compareDocumentPosition(nextSibling) & Node.DOCUMENT_POSITION_CONTAINED_BY) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }

        this.node = node;
    }

    /**
     * <p>Get the child node before which the result nodes will be inserted.</p>
     * <p/>
     * <p>If no node was set via
     * {@link #DOMResult(Node node, Node nextSibling)},
     * {@link #DOMResult(Node node, Node nextSibling, String systemId)} or
     * {@link #setNextSibling(Node nextSibling)},
     * then <code>null</code> will be returned.</p>
     *
     * @return The child node before which the result nodes will be inserted.
     * @since 1.5
     */
    public Node getNextSibling() {
        return nextSibling;
    }

    //////////////////////////////////////////////////////////////////////
    // Internal state.
    //////////////////////////////////////////////////////////////////////

    /**
     * <p>Set the child node before which the result nodes will be inserted.</p>
     * <p/>
     * <p>Use <code>nextSibling</code> to specify the child node
     * before which the result nodes should be inserted.
     * If <code>nextSibling</code> is not a descendant of <code>node</code>,
     * then an <code>IllegalArgumentException</code> is thrown.
     * If <code>node</code> is <code>null</code> and <code>nextSibling</code> is not <code>null</code>,
     * then an <code>IllegalStateException</code> is thrown.
     * If <code>nextSibling</code> is <code>null</code>,
     * then the behavior is the same as calling {@link #DOMResult(Node node)},
     * i.e. append the result nodes as the last child of the specified <code>node</code>.</p>
     *
     * @param nextSibling The child node before which the result nodes will be inserted.
     * @throws IllegalArgumentException If <code>nextSibling</code> is not a
     *                                  descendant of <code>node</code>.
     * @throws IllegalStateException    If <code>node</code> is <code>null</code>
     *                                  and <code>nextSibling</code> is not <code>null</code>.
     * @since 1.5
     */
    public void setNextSibling(Node nextSibling) {

        // does the corrent parent/child relationship exist?
        if (nextSibling != null) {
            // cannot be a sibling of a null node
            if (node == null) {
                throw new IllegalStateException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }

            // nextSibling contained by node?
            if ((node.compareDocumentPosition(nextSibling) & Node.DOCUMENT_POSITION_CONTAINED_BY) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }

        this.nextSibling = nextSibling;
    }

    /**
     * <p>Get the System Identifier.</p>
     * <p/>
     * <p>If no System ID was set via
     * {@link #DOMResult(Node node, String systemId)},
     * {@link #DOMResult(Node node, Node nextSibling, String systemId)} or
     * {@link #setSystemId(String systemId)},
     * then <code>null</code> will be returned.</p>
     *
     * @return The system identifier.
     */
    @Override
    public String getSystemId() {
        return systemId;
    }

    /**
     * <p>Set the systemId that may be used in association with the node.</p>
     *
     * @param systemId The system identifier as a URI string.
     */
    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}

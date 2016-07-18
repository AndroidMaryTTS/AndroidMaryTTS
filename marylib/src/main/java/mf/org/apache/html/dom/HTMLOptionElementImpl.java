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

import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;
import mf.org.w3c.dom.Text;
import mf.org.w3c.dom.html.HTMLElement;
import mf.org.w3c.dom.html.HTMLOptionElement;
import mf.org.w3c.dom.html.HTMLSelectElement;

/**
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLOptionElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLOptionElementImpl
        extends HTMLElementImpl
        implements HTMLOptionElement {

    private static final long serialVersionUID = -4486774554137530907L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLOptionElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public boolean getDefaultSelected() {
        // ! NOT FULLY IMPLEMENTED !
        return getBinary("default-selected");
    }

    @Override
    public void setDefaultSelected(boolean defaultSelected) {
        // ! NOT FULLY IMPLEMENTED !
        setAttribute("default-selected", defaultSelected);
    }

    @Override
    public String getText() {
        Node child;
        StringBuffer text = new StringBuffer();

        // Find the Text nodes contained within this element and return their
        // concatenated value. Required to go around comments, entities, etc.
        child = getFirstChild();
        while (child != null) {
            if (child instanceof Text) {
                text.append(((Text) child).getData());
            }
            child = child.getNextSibling();
        }
        return text.toString();
    }

    public void setText(String text) {
        Node child;
        Node next;

        // Delete all the nodes and replace them with a single Text node.
        // This is the only approach that can handle comments and other nodes.
        child = getFirstChild();
        while (child != null) {
            next = child.getNextSibling();
            removeChild(child);
            child = next;
        }
        insertBefore(getOwnerDocument().createTextNode(text), getFirstChild());
    }

    @Override
    public int getIndex() {
        Node parent;
        NodeList options;
        int i;

        // Locate the parent SELECT. Note that this OPTION might be inside a
        // OPTGROUP inside the SELECT. Or it might not have a parent SELECT.
        // Everything is possible. If no parent is found, return -1.
        parent = getParentNode();
        while (parent != null && !(parent instanceof HTMLSelectElement))
            parent = parent.getParentNode();
        if (parent != null) {
            // Use getElementsByTagName() which creates a snapshot of all the
            // OPTION elements under the SELECT. Access to the returned NodeList
            // is very fast and the snapshot solves many synchronization problems.
            options = ((HTMLElement) parent).getElementsByTagName("OPTION");
            for (i = 0; i < options.getLength(); ++i)
                if (options.item(i) == this)
                    return i;
        }
        return -1;
    }

    @Override
    public void setIndex(int index) {
        Node parent;
        NodeList options;
        Node item;

        // Locate the parent SELECT. Note that this OPTION might be inside a
        // OPTGROUP inside the SELECT. Or it might not have a parent SELECT.
        // Everything is possible. If no parent is found, just return.
        parent = getParentNode();
        while (parent != null && !(parent instanceof HTMLSelectElement))
            parent = parent.getParentNode();
        if (parent != null) {
            // Use getElementsByTagName() which creates a snapshot of all the
            // OPTION elements under the SELECT. Access to the returned NodeList
            // is very fast and the snapshot solves many synchronization problems.
            // Make sure this OPTION is not replacing itself.
            options = ((HTMLElement) parent).getElementsByTagName("OPTION");
            if (options.item(index) != this) {
                // Remove this OPTION from its parent. Place this OPTION right
                // before indexed OPTION underneath it's direct parent (might
                // be an OPTGROUP).
                getParentNode().removeChild(this);
                item = options.item(index);
                item.getParentNode().insertBefore(this, item);
            }
        }
    }

    @Override
    public boolean getDisabled() {
        return getBinary("disabled");
    }

    @Override
    public void setDisabled(boolean disabled) {
        setAttribute("disabled", disabled);
    }

    @Override
    public String getLabel() {
        return capitalize(getAttribute("label"));
    }

    @Override
    public void setLabel(String label) {
        setAttribute("label", label);
    }

    @Override
    public boolean getSelected() {
        return getBinary("selected");
    }

    public void setSelected(boolean selected) {
        setAttribute("selected", selected);
    }

    @Override
    public String getValue() {
        return getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        setAttribute("value", value);
    }


}


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
import mf.org.w3c.dom.html.HTMLCollection;
import mf.org.w3c.dom.html.HTMLElement;
import mf.org.w3c.dom.html.HTMLOptionElement;
import mf.org.w3c.dom.html.HTMLSelectElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLSelectElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLSelectElementImpl
        extends HTMLElementImpl
        implements HTMLSelectElement, HTMLFormControl {

    private static final long serialVersionUID = -6998282711006968187L;
    private HTMLCollection _options;


    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLSelectElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public String getType() {
        return getAttribute("type");
    }

    @Override
    public String getValue() {
        return getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        setAttribute("value", value);
    }

    @Override
    public int getSelectedIndex() {
        NodeList options;
        int i;

        // Use getElementsByTagName() which creates a snapshot of all the
        // OPTION elements under this SELECT. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        // Locate the first selected OPTION and return its index. Note that
        // the OPTION might be under an OPTGROUP.
        options = getElementsByTagName("OPTION");
        for (i = 0; i < options.getLength(); ++i)
            if (((HTMLOptionElement) options.item(i)).getSelected())
                return i;
        return -1;
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        NodeList options;
        int i;

        // Use getElementsByTagName() which creates a snapshot of all the
        // OPTION elements under this SELECT. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        // Change the select so all OPTIONs are off, except for the
        // selectIndex-th one.
        options = getElementsByTagName("OPTION");
        for (i = 0; i < options.getLength(); ++i)
            ((HTMLOptionElementImpl) options.item(i)).setSelected(i == selectedIndex);
    }

    @Override
    public HTMLCollection getOptions() {
        if (_options == null)
            _options = new HTMLCollectionImpl(this, HTMLCollectionImpl.OPTION);
        return _options;
    }

    @Override
    public int getLength() {
        return getOptions().getLength();
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
    public boolean getMultiple() {
        return getBinary("multiple");
    }

    @Override
    public void setMultiple(boolean multiple) {
        setAttribute("multiple", multiple);
    }

    @Override
    public String getName() {
        return getAttribute("name");
    }

    @Override
    public void setName(String name) {
        setAttribute("name", name);
    }

    @Override
    public int getSize() {
        return getInteger(getAttribute("size"));
    }

    @Override
    public void setSize(int size) {
        setAttribute("size", String.valueOf(size));
    }

    @Override
    public int getTabIndex() {
        return getInteger(getAttribute("tabindex"));
    }

    @Override
    public void setTabIndex(int tabIndex) {
        setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public void add(HTMLElement element, HTMLElement before) {
        insertBefore(element, before);
    }

    @Override
    public void remove(int index) {
        NodeList options;
        Node removed;

        // Use getElementsByTagName() which creates a snapshot of all the
        // OPTION elements under this SELECT. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        // Remove the indexed OPTION from it's parent, this might be this
        // SELECT or an OPTGROUP.
        options = getElementsByTagName("OPTION");
        removed = options.item(index);
        if (removed != null)
            removed.getParentNode().removeChild(removed);
    }

    @Override
    public void blur() {
        // No scripting in server-side DOM. This method is moot.
    }

    @Override
    public void focus() {
        // No scripting in server-side DOM. This method is moot.
    }

    /**
     * Explicit implementation of getChildNodes() to avoid problems with
     * overriding the getLength() method hidden in the super class.
     */
    @Override
    public NodeList getChildNodes() {
        return getChildNodesUnoptimized();
    }

    /**
     * Explicit implementation of cloneNode() to ensure that cache used
     * for getOptions() gets cleared.
     */
    @Override
    public Node cloneNode(boolean deep) {
        HTMLSelectElementImpl clonedNode = (HTMLSelectElementImpl) super.cloneNode(deep);
        clonedNode._options = null;
        return clonedNode;
    }


}


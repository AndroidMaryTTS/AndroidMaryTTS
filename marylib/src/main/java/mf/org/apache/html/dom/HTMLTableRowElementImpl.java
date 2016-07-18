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
import mf.org.w3c.dom.html.HTMLTableCellElement;
import mf.org.w3c.dom.html.HTMLTableElement;
import mf.org.w3c.dom.html.HTMLTableRowElement;
import mf.org.w3c.dom.html.HTMLTableSectionElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLTableRowElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLTableRowElementImpl
        extends HTMLElementImpl
        implements HTMLTableRowElement {

    private static final long serialVersionUID = 5409562635656244263L;
    HTMLCollection _cells;


    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLTableRowElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public int getRowIndex() {
        Node parent;

        parent = getParentNode();
        if (parent instanceof HTMLTableSectionElement) {
            parent = parent.getParentNode();
        }
        if (parent instanceof HTMLTableElement) {
            return getRowIndex(parent);
        }
        return -1;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        Node parent;

        parent = getParentNode();
        if (parent instanceof HTMLTableSectionElement) {
            parent = parent.getParentNode();
        }
        if (parent instanceof HTMLTableElement) {
            ((HTMLTableElementImpl) parent).insertRowX(rowIndex, this);
        }
    }

    @Override
    public int getSectionRowIndex() {
        Node parent;

        parent = getParentNode();
        if (parent instanceof HTMLTableSectionElement) {
            return getRowIndex(parent);
        }
        return -1;
    }

    @Override
    public void setSectionRowIndex(int sectionRowIndex) {
        Node parent;

        parent = getParentNode();
        if (parent instanceof HTMLTableSectionElement) {
            ((HTMLTableSectionElementImpl) parent).insertRowX(sectionRowIndex, this);
        }
    }

    int getRowIndex(Node parent) {
        NodeList rows;
        int i;

        // Use getElementsByTagName() which creates a snapshot of all the
        // TR elements under the TABLE/section. Access to the returned NodeList
        // is very fast and the snapshot solves many synchronization problems.
        rows = ((HTMLElement) parent).getElementsByTagName("TR");
        for (i = 0; i < rows.getLength(); ++i) {
            if (rows.item(i) == this) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public HTMLCollection getCells() {
        if (_cells == null) {
            _cells = new HTMLCollectionImpl(this, HTMLCollectionImpl.CELL);
        }
        return _cells;
    }

    @Override
    public void setCells(HTMLCollection cells) {
        Node child;
        int i;

        child = getFirstChild();
        while (child != null) {
            removeChild(child);
            child = child.getNextSibling();
        }
        i = 0;
        child = cells.item(i);
        while (child != null) {
            appendChild(child);
            ++i;
            child = cells.item(i);
        }
    }

    @Override
    public HTMLElement insertCell(int index) {
        Node child;
        HTMLElement newCell;

        newCell = new HTMLTableCellElementImpl((HTMLDocumentImpl) getOwnerDocument(), "TD");
        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableCellElement) {
                if (index == 0) {
                    insertBefore(newCell, child);
                    return newCell;
                }
                --index;
            }
            child = child.getNextSibling();
        }
        appendChild(newCell);
        return newCell;
    }

    @Override
    public void deleteCell(int index) {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableCellElement) {
                if (index == 0) {
                    removeChild(child);
                    return;
                }
                --index;
            }
            child = child.getNextSibling();
        }
    }

    @Override
    public String getAlign() {
        return capitalize(getAttribute("align"));
    }

    @Override
    public void setAlign(String align) {
        setAttribute("align", align);
    }

    @Override
    public String getBgColor() {
        return getAttribute("bgcolor");
    }

    @Override
    public void setBgColor(String bgColor) {
        setAttribute("bgcolor", bgColor);
    }

    @Override
    public String getCh() {
        String ch;

        // Make sure that the access key is a single character.
        ch = getAttribute("char");
        if (ch != null && ch.length() > 1) {
            ch = ch.substring(0, 1);
        }
        return ch;
    }

    @Override
    public void setCh(String ch) {
        // Make sure that the access key is a single character.
        if (ch != null && ch.length() > 1) {
            ch = ch.substring(0, 1);
        }
        setAttribute("char", ch);
    }

    @Override
    public String getChOff() {
        return getAttribute("charoff");
    }

    @Override
    public void setChOff(String chOff) {
        setAttribute("charoff", chOff);
    }

    @Override
    public String getVAlign() {
        return capitalize(getAttribute("valign"));
    }

    @Override
    public void setVAlign(String vAlign) {
        setAttribute("valign", vAlign);
    }

    /**
     * Explicit implementation of cloneNode() to ensure that cache used
     * for getCells() gets cleared.
     */
    @Override
    public Node cloneNode(boolean deep) {
        HTMLTableRowElementImpl clonedNode = (HTMLTableRowElementImpl) super.cloneNode(deep);
        clonedNode._cells = null;
        return clonedNode;
    }


}


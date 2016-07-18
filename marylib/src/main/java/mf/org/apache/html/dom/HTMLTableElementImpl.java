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
import mf.org.w3c.dom.html.HTMLCollection;
import mf.org.w3c.dom.html.HTMLElement;
import mf.org.w3c.dom.html.HTMLTableCaptionElement;
import mf.org.w3c.dom.html.HTMLTableElement;
import mf.org.w3c.dom.html.HTMLTableRowElement;
import mf.org.w3c.dom.html.HTMLTableSectionElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLAnchorElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLTableElementImpl
        extends HTMLElementImpl
        implements HTMLTableElement {

    private static final long serialVersionUID = -1824053099870917532L;
    private HTMLCollectionImpl _rows;
    private HTMLCollectionImpl _bodies;


    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLTableElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public synchronized HTMLTableCaptionElement getCaption() {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableCaptionElement &&
                    child.getNodeName().equals("CAPTION"))
                return (HTMLTableCaptionElement) child;
            child = child.getNextSibling();
        }
        return null;
    }

    @Override
    public synchronized void setCaption(HTMLTableCaptionElement caption) {
        if (caption != null && !caption.getTagName().equals("CAPTION"))
            throw new IllegalArgumentException("HTM016 Argument 'caption' is not an element of type <CAPTION>.");
        deleteCaption();
        if (caption != null)
            appendChild(caption);
    }

    @Override
    public synchronized HTMLElement createCaption() {
        HTMLElement section;

        section = getCaption();
        if (section != null)
            return section;
        section = new HTMLTableCaptionElementImpl((HTMLDocumentImpl) getOwnerDocument(), "CAPTION");
        appendChild(section);
        return section;
    }

    @Override
    public synchronized void deleteCaption() {
        Node old;

        old = getCaption();
        if (old != null)
            removeChild(old);
    }

    @Override
    public synchronized HTMLTableSectionElement getTHead() {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableSectionElement &&
                    child.getNodeName().equals("THEAD"))
                return (HTMLTableSectionElement) child;
            child = child.getNextSibling();
        }
        return null;
    }

    @Override
    public synchronized void setTHead(HTMLTableSectionElement tHead) {
        if (tHead != null && !tHead.getTagName().equals("THEAD"))
            throw new IllegalArgumentException("HTM017 Argument 'tHead' is not an element of type <THEAD>.");
        deleteTHead();
        if (tHead != null)
            appendChild(tHead);
    }

    @Override
    public synchronized HTMLElement createTHead() {
        HTMLElement section;

        section = getTHead();
        if (section != null)
            return section;
        section = new HTMLTableSectionElementImpl((HTMLDocumentImpl) getOwnerDocument(), "THEAD");
        appendChild(section);
        return section;
    }

    @Override
    public synchronized void deleteTHead() {
        Node old;

        old = getTHead();
        if (old != null)
            removeChild(old);
    }

    @Override
    public synchronized HTMLTableSectionElement getTFoot() {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableSectionElement &&
                    child.getNodeName().equals("TFOOT"))
                return (HTMLTableSectionElement) child;
            child = child.getNextSibling();
        }
        return null;
    }

    @Override
    public synchronized void setTFoot(HTMLTableSectionElement tFoot) {
        if (tFoot != null && !tFoot.getTagName().equals("TFOOT"))
            throw new IllegalArgumentException("HTM018 Argument 'tFoot' is not an element of type <TFOOT>.");
        deleteTFoot();
        if (tFoot != null)
            appendChild(tFoot);
    }

    @Override
    public synchronized HTMLElement createTFoot() {
        HTMLElement section;

        section = getTFoot();
        if (section != null)
            return section;
        section = new HTMLTableSectionElementImpl((HTMLDocumentImpl) getOwnerDocument(), "TFOOT");
        appendChild(section);
        return section;
    }

    @Override
    public synchronized void deleteTFoot() {
        Node old;

        old = getTFoot();
        if (old != null)
            removeChild(old);
    }

    @Override
    public HTMLCollection getRows() {
        if (_rows == null)
            _rows = new HTMLCollectionImpl(this, HTMLCollectionImpl.ROW);
        return _rows;
    }

    @Override
    public HTMLCollection getTBodies() {
        if (_bodies == null)
            _bodies = new HTMLCollectionImpl(this, HTMLCollectionImpl.TBODY);
        return _bodies;
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
    public String getBorder() {
        return getAttribute("border");
    }

    @Override
    public void setBorder(String border) {
        setAttribute("border", border);
    }

    @Override
    public String getCellPadding() {
        return getAttribute("cellpadding");
    }

    @Override
    public void setCellPadding(String cellPadding) {
        setAttribute("cellpadding", cellPadding);
    }

    @Override
    public String getCellSpacing() {
        return getAttribute("cellspacing");
    }

    @Override
    public void setCellSpacing(String cellSpacing) {
        setAttribute("cellspacing", cellSpacing);
    }

    @Override
    public String getFrame() {
        return capitalize(getAttribute("frame"));
    }

    @Override
    public void setFrame(String frame) {
        setAttribute("frame", frame);
    }

    @Override
    public String getRules() {
        return capitalize(getAttribute("rules"));
    }

    @Override
    public void setRules(String rules) {
        setAttribute("rules", rules);
    }

    @Override
    public String getSummary() {
        return getAttribute("summary");
    }

    @Override
    public void setSummary(String summary) {
        setAttribute("summary", summary);
    }

    @Override
    public String getWidth() {
        return getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        setAttribute("width", width);
    }

    @Override
    public HTMLElement insertRow(int index) {
        HTMLTableRowElementImpl newRow;

        newRow = new HTMLTableRowElementImpl((HTMLDocumentImpl) getOwnerDocument(), "TR");
        //newRow.insertCell( 0 );
        insertRowX(index, newRow);
        return newRow;
    }

    void insertRowX(int index, HTMLTableRowElementImpl newRow) {
        Node child;
        Node lastSection = null;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableRowElement) {
                if (index == 0) {
                    insertBefore(newRow, child);
                    return;
                }
            } else if (child instanceof HTMLTableSectionElementImpl) {
                lastSection = child;
                index = ((HTMLTableSectionElementImpl) child).insertRowX(index, newRow);
                if (index < 0)
                    return;
            }
            child = child.getNextSibling();
        }
        if (lastSection != null)
            lastSection.appendChild(newRow);
        else
            appendChild(newRow);
    }

    @Override
    public synchronized void deleteRow(int index) {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableRowElement) {
                if (index == 0) {
                    removeChild(child);
                    return;
                }
                --index;
            } else if (child instanceof HTMLTableSectionElementImpl) {
                index = ((HTMLTableSectionElementImpl) child).deleteRowX(index);
                if (index < 0)
                    return;
            }
            child = child.getNextSibling();
        }
    }

    /**
     * Explicit implementation of cloneNode() to ensure that cache used
     * for getRows() and getTBodies() gets cleared.
     */
    @Override
    public Node cloneNode(boolean deep) {
        HTMLTableElementImpl clonedNode = (HTMLTableElementImpl) super.cloneNode(deep);
        clonedNode._rows = null;
        clonedNode._bodies = null;
        return clonedNode;
    }


}


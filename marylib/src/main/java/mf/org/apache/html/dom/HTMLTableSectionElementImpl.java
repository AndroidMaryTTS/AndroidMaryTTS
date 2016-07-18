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
import mf.org.w3c.dom.html.HTMLTableRowElement;
import mf.org.w3c.dom.html.HTMLTableSectionElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLTableSectionElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLTableSectionElementImpl
        extends HTMLElementImpl
        implements HTMLTableSectionElement {

    private static final long serialVersionUID = 1016412997716618027L;
    private HTMLCollectionImpl _rows;


    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLTableSectionElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
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
    public String getCh() {
        String ch;

        // Make sure that the access key is a single character.
        ch = getAttribute("char");
        if (ch != null && ch.length() > 1)
            ch = ch.substring(0, 1);
        return ch;
    }

    @Override
    public void setCh(String ch) {
        // Make sure that the access key is a single character.
        if (ch != null && ch.length() > 1)
            ch = ch.substring(0, 1);
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

    @Override
    public HTMLCollection getRows() {
        if (_rows == null)
            _rows = new HTMLCollectionImpl(this, HTMLCollectionImpl.ROW);
        return _rows;
    }

    @Override
    public HTMLElement insertRow(int index) {
        HTMLTableRowElementImpl newRow;

        newRow = new HTMLTableRowElementImpl((HTMLDocumentImpl) getOwnerDocument(), "TR");
        newRow.insertCell(0);
        if (insertRowX(index, newRow) >= 0)
            appendChild(newRow);
        return newRow;
    }

    int insertRowX(int index, HTMLTableRowElementImpl newRow) {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableRowElement) {
                if (index == 0) {
                    insertBefore(newRow, child);
                    return -1;
                }
                --index;
            }
            child = child.getNextSibling();
        }
        return index;
    }

    @Override
    public void deleteRow(int index) {
        deleteRowX(index);
    }

    int deleteRowX(int index) {
        Node child;

        child = getFirstChild();
        while (child != null) {
            if (child instanceof HTMLTableRowElement) {
                if (index == 0) {
                    removeChild(child);
                    return -1;
                }
                --index;
            }
            child = child.getNextSibling();
        }
        return index;
    }

    /**
     * Explicit implementation of cloneNode() to ensure that cache used
     * for getRows() gets cleared.
     */
    @Override
    public Node cloneNode(boolean deep) {
        HTMLTableSectionElementImpl clonedNode = (HTMLTableSectionElementImpl) super.cloneNode(deep);
        clonedNode._rows = null;
        return clonedNode;
    }


}


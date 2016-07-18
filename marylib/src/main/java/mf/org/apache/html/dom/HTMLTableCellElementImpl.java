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
import mf.org.w3c.dom.html.HTMLTableCellElement;
import mf.org.w3c.dom.html.HTMLTableRowElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLTableCellElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLTableCellElementImpl
        extends HTMLElementImpl
        implements HTMLTableCellElement {

    private static final long serialVersionUID = -2406518157464313922L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLTableCellElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public int getCellIndex() {
        Node parent;
        Node child;
        int index;

        parent = getParentNode();
        index = 0;
        if (parent instanceof HTMLTableRowElement) {
            child = parent.getFirstChild();
            while (child != null) {
                if (child instanceof HTMLTableCellElement) {
                    if (child == this)
                        return index;
                    ++index;
                }
                child = child.getNextSibling();
            }
        }
        return -1;
    }

    @Override
    public void setCellIndex(int cellIndex) {
        Node parent;
        Node child;

        parent = getParentNode();
        if (parent instanceof HTMLTableRowElement) {
            child = parent.getFirstChild();
            while (child != null) {
                if (child instanceof HTMLTableCellElement) {
                    if (cellIndex == 0) {
                        if (this != child)
                            parent.insertBefore(this, child);
                        return;
                    }
                    --cellIndex;
                }
                child = child.getNextSibling();
            }
        }
        parent.appendChild(this);
    }

    @Override
    public String getAbbr() {
        return getAttribute("abbr");
    }

    @Override
    public void setAbbr(String abbr) {
        setAttribute("abbr", abbr);
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
    public String getAxis() {
        return getAttribute("axis");
    }

    @Override
    public void setAxis(String axis) {
        setAttribute("axis", axis);
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
    public int getColSpan() {
        return getInteger(getAttribute("colspan"));
    }

    @Override
    public void setColSpan(int colspan) {
        setAttribute("colspan", String.valueOf(colspan));
    }

    @Override
    public String getHeaders() {
        return getAttribute("headers");
    }

    @Override
    public void setHeaders(String headers) {
        setAttribute("headers", headers);
    }

    @Override
    public String getHeight() {
        return getAttribute("height");
    }

    @Override
    public void setHeight(String height) {
        setAttribute("height", height);
    }

    @Override
    public boolean getNoWrap() {
        return getBinary("nowrap");
    }

    @Override
    public void setNoWrap(boolean noWrap) {
        setAttribute("nowrap", noWrap);
    }

    @Override
    public int getRowSpan() {
        return getInteger(getAttribute("rowspan"));
    }

    @Override
    public void setRowSpan(int rowspan) {
        setAttribute("rowspan", String.valueOf(rowspan));
    }

    @Override
    public String getScope() {
        return getAttribute("scope");
    }

    @Override
    public void setScope(String scope) {
        setAttribute("scope", scope);
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
    public String getWidth() {
        return getAttribute("width");
    }

    @Override
    public void setWidth(String width) {
        setAttribute("width", width);
    }


}


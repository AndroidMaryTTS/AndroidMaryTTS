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

import mf.org.w3c.dom.html.HTMLTableColElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLTableColElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLTableColElementImpl
        extends HTMLElementImpl
        implements HTMLTableColElement {

    private static final long serialVersionUID = -6189626162811911792L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLTableColElementImpl(HTMLDocumentImpl owner, String name) {
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
    public int getSpan() {
        return getInteger(getAttribute("span"));
    }

    @Override
    public void setSpan(int span) {
        setAttribute("span", String.valueOf(span));
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


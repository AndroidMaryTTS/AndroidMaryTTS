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

import mf.org.w3c.dom.html.HTMLObjectElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLObjectElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLObjectElementImpl
        extends HTMLElementImpl
        implements HTMLObjectElement, HTMLFormControl {

    private static final long serialVersionUID = 2276953229932965067L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLObjectElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public String getCode() {
        return getAttribute("code");
    }

    @Override
    public void setCode(String code) {
        setAttribute("code", code);
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
    public String getArchive() {
        return getAttribute("archive");
    }

    @Override
    public void setArchive(String archive) {
        setAttribute("archive", archive);
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
    public String getCodeBase() {
        return getAttribute("codebase");
    }

    @Override
    public void setCodeBase(String codeBase) {
        setAttribute("codebase", codeBase);
    }

    @Override
    public String getCodeType() {
        return getAttribute("codetype");
    }

    @Override
    public void setCodeType(String codeType) {
        setAttribute("codetype", codeType);
    }

    @Override
    public String getData() {
        return getAttribute("data");
    }

    @Override
    public void setData(String data) {
        setAttribute("data", data);
    }

    @Override
    public boolean getDeclare() {
        return getBinary("declare");
    }

    @Override
    public void setDeclare(boolean declare) {
        setAttribute("declare", declare);
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
    public String getHspace() {
        return getAttribute("hspace");
    }

    @Override
    public void setHspace(String hspace) {
        setAttribute("hspace", hspace);
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
    public String getStandby() {
        return getAttribute("standby");
    }

    @Override
    public void setStandby(String standby) {
        setAttribute("standby", standby);
    }

    @Override
    public int getTabIndex() {
        try {
            return Integer.parseInt(getAttribute("tabindex"));
        } catch (NumberFormatException except) {
            return 0;
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
        setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public String getType() {
        return getAttribute("type");
    }

    @Override
    public void setType(String type) {
        setAttribute("type", type);
    }

    @Override
    public String getUseMap() {
        return getAttribute("useMap");
    }

    @Override
    public void setUseMap(String useMap) {
        setAttribute("useMap", useMap);
    }

    @Override
    public String getVspace() {
        return getAttribute("vspace");
    }

    @Override
    public void setVspace(String vspace) {
        setAttribute("vspace", vspace);
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


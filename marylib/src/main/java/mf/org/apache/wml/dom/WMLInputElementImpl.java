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
package mf.org.apache.wml.dom;

import mf.org.apache.wml.WMLInputElement;

/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLInputElementImpl.java 661560 2008-05-30 03:22:25Z mrglavas $
 * @xerces.internal
 */
public class WMLInputElementImpl extends WMLElementImpl implements WMLInputElement {

    private static final long serialVersionUID = 2897319793637966619L;

    public WMLInputElementImpl(WMLDocumentImpl owner, String tagName) {
        super(owner, tagName);
    }

    @Override
    public int getSize() {
        return getAttribute("size", 0);
    }

    @Override
    public void setSize(int newValue) {
        setAttribute("size", newValue);
    }

    @Override
    public String getFormat() {
        return getAttribute("format");
    }

    @Override
    public void setFormat(String newValue) {
        setAttribute("format", newValue);
    }

    @Override
    public String getValue() {
        return getAttribute("value");
    }

    @Override
    public void setValue(String newValue) {
        setAttribute("value", newValue);
    }

    @Override
    public int getMaxLength() {
        return getAttribute("maxlength", 0);
    }

    @Override
    public void setMaxLength(int newValue) {
        setAttribute("maxlength", newValue);
    }

    @Override
    public int getTabIndex() {
        return getAttribute("tabindex", 0);
    }

    @Override
    public void setTabIndex(int newValue) {
        setAttribute("tabindex", newValue);
    }

    @Override
    public String getClassName() {
        return getAttribute("class");
    }

    @Override
    public void setClassName(String newValue) {
        setAttribute("class", newValue);
    }

    @Override
    public String getXmlLang() {
        return getAttribute("xml:lang");
    }

    @Override
    public void setXmlLang(String newValue) {
        setAttribute("xml:lang", newValue);
    }

    @Override
    public boolean getEmptyOk() {
        return getAttribute("emptyok", false);
    }

    @Override
    public void setEmptyOk(boolean newValue) {
        setAttribute("emptyok", newValue);
    }

    @Override
    public String getTitle() {
        return getAttribute("title");
    }

    @Override
    public void setTitle(String newValue) {
        setAttribute("title", newValue);
    }

    @Override
    public String getId() {
        return getAttribute("id");
    }

    @Override
    public void setId(String newValue) {
        setAttribute("id", newValue);
    }

    @Override
    public String getType() {
        return getAttribute("type");
    }

    @Override
    public void setType(String newValue) {
        setAttribute("type", newValue);
    }

    @Override
    public String getName() {
        return getAttribute("name");
    }

    @Override
    public void setName(String newValue) {
        setAttribute("name", newValue);
    }

}

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

import mf.org.apache.wml.WMLSelectElement;

/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLSelectElementImpl.java 661560 2008-05-30 03:22:25Z mrglavas $
 * @xerces.internal
 */
public class WMLSelectElementImpl extends WMLElementImpl implements WMLSelectElement {

    private static final long serialVersionUID = 6489112443257247261L;

    public WMLSelectElementImpl(WMLDocumentImpl owner, String tagName) {
        super(owner, tagName);
    }

    @Override
    public boolean getMultiple() {
        return getAttribute("multiple", false);
    }

    @Override
    public void setMultiple(boolean newValue) {
        setAttribute("multiple", newValue);
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
    public String getTitle() {
        return getAttribute("title");
    }

    @Override
    public void setTitle(String newValue) {
        setAttribute("title", newValue);
    }

    @Override
    public String getIValue() {
        return getAttribute("ivalue");
    }

    @Override
    public void setIValue(String newValue) {
        setAttribute("ivalue", newValue);
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
    public String getIName() {
        return getAttribute("iname");
    }

    @Override
    public void setIName(String newValue) {
        setAttribute("iname", newValue);
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

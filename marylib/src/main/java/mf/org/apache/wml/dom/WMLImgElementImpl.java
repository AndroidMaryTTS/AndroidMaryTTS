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

import mf.org.apache.wml.WMLImgElement;

/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLImgElementImpl.java 661560 2008-05-30 03:22:25Z mrglavas $
 * @xerces.internal
 */
public class WMLImgElementImpl extends WMLElementImpl implements WMLImgElement {

    private static final long serialVersionUID = -500092034867051550L;

    public WMLImgElementImpl(WMLDocumentImpl owner, String tagName) {
        super(owner, tagName);
    }

    @Override
    public String getWidth() {
        return getAttribute("width");
    }

    @Override
    public void setWidth(String newValue) {
        setAttribute("width", newValue);
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
    public String getLocalSrc() {
        return getAttribute("localsrc");
    }

    @Override
    public void setLocalSrc(String newValue) {
        setAttribute("localsrc", newValue);
    }

    @Override
    public String getHeight() {
        return getAttribute("height");
    }

    @Override
    public void setHeight(String newValue) {
        setAttribute("height", newValue);
    }

    @Override
    public String getAlign() {
        return getAttribute("align");
    }

    @Override
    public void setAlign(String newValue) {
        setAttribute("align", newValue);
    }

    @Override
    public String getVspace() {
        return getAttribute("vspace");
    }

    @Override
    public void setVspace(String newValue) {
        setAttribute("vspace", newValue);
    }

    @Override
    public String getAlt() {
        return getAttribute("alt");
    }

    @Override
    public void setAlt(String newValue) {
        setAttribute("alt", newValue);
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
    public String getHspace() {
        return getAttribute("hspace");
    }

    @Override
    public void setHspace(String newValue) {
        setAttribute("hspace", newValue);
    }

    @Override
    public String getSrc() {
        return getAttribute("src");
    }

    @Override
    public void setSrc(String newValue) {
        setAttribute("src", newValue);
    }

}

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

import mf.org.apache.wml.WMLMetaElement;

/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLMetaElementImpl.java 661560 2008-05-30 03:22:25Z mrglavas $
 * @xerces.internal
 */
public class WMLMetaElementImpl extends WMLElementImpl implements WMLMetaElement {

    private static final long serialVersionUID = -2791663042188681846L;

    public WMLMetaElementImpl(WMLDocumentImpl owner, String tagName) {
        super(owner, tagName);
    }

    @Override
    public boolean getForua() {
        return getAttribute("forua", false);
    }

    @Override
    public void setForua(boolean newValue) {
        setAttribute("forua", newValue);
    }

    @Override
    public String getScheme() {
        return getAttribute("scheme");
    }

    @Override
    public void setScheme(String newValue) {
        setAttribute("scheme", newValue);
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
    public String getHttpEquiv() {
        return getAttribute("http-equiv");
    }

    @Override
    public void setHttpEquiv(String newValue) {
        setAttribute("http-equiv", newValue);
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
    public String getContent() {
        return getAttribute("content");
    }

    @Override
    public void setContent(String newValue) {
        setAttribute("content", newValue);
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

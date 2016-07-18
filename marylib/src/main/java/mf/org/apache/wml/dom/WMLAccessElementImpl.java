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

import mf.org.apache.wml.WMLAccessElement;

/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLAccessElementImpl.java 661560 2008-05-30 03:22:25Z mrglavas $
 * @xerces.internal
 */
public class WMLAccessElementImpl extends WMLElementImpl implements WMLAccessElement {

    private static final long serialVersionUID = -235627181817012806L;

    public WMLAccessElementImpl(WMLDocumentImpl owner, String tagName) {
        super(owner, tagName);
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
    public String getDomain() {
        return getAttribute("domain");
    }

    @Override
    public void setDomain(String newValue) {
        setAttribute("domain", newValue);
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
    public String getPath() {
        return getAttribute("path");
    }

    @Override
    public void setPath(String newValue) {
        setAttribute("path", newValue);
    }

}

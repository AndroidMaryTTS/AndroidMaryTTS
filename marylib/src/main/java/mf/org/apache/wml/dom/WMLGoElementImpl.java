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

import mf.org.apache.wml.WMLGoElement;

/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLGoElementImpl.java 661560 2008-05-30 03:22:25Z mrglavas $
 * @xerces.internal
 */
public class WMLGoElementImpl extends WMLElementImpl implements WMLGoElement {

    private static final long serialVersionUID = -2052250142899797905L;

    public WMLGoElementImpl(WMLDocumentImpl owner, String tagName) {
        super(owner, tagName);
    }

    @Override
    public String getSendreferer() {
        return getAttribute("sendreferer");
    }

    @Override
    public void setSendreferer(String newValue) {
        setAttribute("sendreferer", newValue);
    }

    @Override
    public String getAcceptCharset() {
        return getAttribute("accept-charset");
    }

    @Override
    public void setAcceptCharset(String newValue) {
        setAttribute("accept-charset", newValue);
    }

    @Override
    public String getHref() {
        return getAttribute("href");
    }

    @Override
    public void setHref(String newValue) {
        setAttribute("href", newValue);
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
    public String getId() {
        return getAttribute("id");
    }

    @Override
    public void setId(String newValue) {
        setAttribute("id", newValue);
    }

    @Override
    public String getMethod() {
        return getAttribute("method");
    }

    @Override
    public void setMethod(String newValue) {
        setAttribute("method", newValue);
    }

}

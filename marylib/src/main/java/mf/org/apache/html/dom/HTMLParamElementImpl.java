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

import mf.org.w3c.dom.html.HTMLParamElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLParamElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLParamElementImpl
        extends HTMLElementImpl
        implements HTMLParamElement {

    private static final long serialVersionUID = -8513050483880341412L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLParamElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
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
    public String getType() {
        return getAttribute("type");
    }

    @Override
    public void setType(String type) {
        setAttribute("type", type);
    }

    @Override
    public String getValue() {
        return getAttribute("value");
    }

    @Override
    public void setValue(String value) {
        setAttribute("value", value);
    }

    @Override
    public String getValueType() {
        return capitalize(getAttribute("valuetype"));
    }

    @Override
    public void setValueType(String valueType) {
        setAttribute("valuetype", valueType);
    }


}


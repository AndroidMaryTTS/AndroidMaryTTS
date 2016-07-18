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

import mf.org.w3c.dom.html.HTMLBaseFontElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLBaseFontElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLBaseFontElementImpl
        extends HTMLElementImpl
        implements HTMLBaseFontElement {

    private static final long serialVersionUID = -3650249921091097229L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLBaseFontElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public String getColor() {
        return capitalize(getAttribute("color"));
    }

    @Override
    public void setColor(String color) {
        setAttribute("color", color);
    }

    @Override
    public String getFace() {
        return capitalize(getAttribute("face"));
    }

    @Override
    public void setFace(String face) {
        setAttribute("face", face);
    }

    @Override
    public String getSize() {
        return getAttribute("size");
    }

    @Override
    public void setSize(String size) {
        setAttribute("size", size);
    }


}


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

import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;
import mf.org.w3c.dom.html.HTMLCollection;
import mf.org.w3c.dom.html.HTMLFormElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLFormElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLFormElementImpl
        extends HTMLElementImpl
        implements HTMLFormElement {

    private static final long serialVersionUID = -7324749629151493210L;
    /**
     * Collection of all elements contained in this FORM.
     */
    private HTMLCollectionImpl _elements;


    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLFormElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public HTMLCollection getElements() {
        if (_elements == null)
            _elements = new HTMLCollectionImpl(this, HTMLCollectionImpl.ELEMENT);
        return _elements;
    }

    @Override
    public int getLength() {
        return getElements().getLength();
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
    public String getAcceptCharset() {
        return getAttribute("accept-charset");
    }

    @Override
    public void setAcceptCharset(String acceptCharset) {
        setAttribute("accept-charset", acceptCharset);
    }

    @Override
    public String getAction() {
        return getAttribute("action");
    }

    @Override
    public void setAction(String action) {
        setAttribute("action", action);
    }

    @Override
    public String getEnctype() {
        return getAttribute("enctype");
    }

    @Override
    public void setEnctype(String enctype) {
        setAttribute("enctype", enctype);
    }

    @Override
    public String getMethod() {
        return capitalize(getAttribute("method"));
    }

    @Override
    public void setMethod(String method) {
        setAttribute("method", method);
    }

    @Override
    public String getTarget() {
        return getAttribute("target");
    }

    @Override
    public void setTarget(String target) {
        setAttribute("target", target);
    }

    @Override
    public void submit() {
        // No scripting in server-side DOM. This method is moot.
    }

    @Override
    public void reset() {
        // No scripting in server-side DOM. This method is moot.
    }

    /*
     * Explicit implementation of getChildNodes() to avoid problems with
     * overriding the getLength() method hidden in the super class.
     */
    @Override
    public NodeList getChildNodes() {
        return getChildNodesUnoptimized();
    }

    /**
     * Explicit implementation of cloneNode() to ensure that cache used
     * for getElements() gets cleared.
     */
    @Override
    public Node cloneNode(boolean deep) {
        HTMLFormElementImpl clonedNode = (HTMLFormElementImpl) super.cloneNode(deep);
        clonedNode._elements = null;
        return clonedNode;
    }

}


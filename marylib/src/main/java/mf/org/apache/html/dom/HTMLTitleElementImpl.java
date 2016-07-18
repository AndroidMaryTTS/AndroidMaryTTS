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
import mf.org.w3c.dom.Text;
import mf.org.w3c.dom.html.HTMLTitleElement;

/**
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 1029415 $ $Date: 2010-10-31 13:02:22 -0400 (Sun, 31 Oct 2010) $
 * @xerces.internal
 * @see mf.org.w3c.dom.html.HTMLTitleElement
 * @see mf.org.apache.xerces.dom.ElementImpl
 */
public class HTMLTitleElementImpl
        extends HTMLElementImpl
        implements HTMLTitleElement {

    private static final long serialVersionUID = 879646303512367875L;

    /**
     * Constructor requires owner document.
     *
     * @param owner The owner HTML document
     */
    public HTMLTitleElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }

    @Override
    public String getText() {
        Node child;
        StringBuffer text = new StringBuffer();

        // Find the Text nodes contained within this element and return their
        // concatenated value. Required to go around comments, entities, etc.
        child = getFirstChild();
        while (child != null) {
            if (child instanceof Text) {
                text.append(((Text) child).getData());
            }
            child = child.getNextSibling();
        }
        return text.toString();
    }

    @Override
    public void setText(String text) {
        Node child;
        Node next;

        // Delete all the nodes and replace them with a single Text node.
        // This is the only approach that can handle comments and other nodes.
        child = getFirstChild();
        while (child != null) {
            next = child.getNextSibling();
            removeChild(child);
            child = next;
        }
        insertBefore(getOwnerDocument().createTextNode(text), getFirstChild());
    }


}


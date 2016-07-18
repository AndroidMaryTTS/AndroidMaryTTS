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


import mf.org.apache.xerces.dom.DOMImplementationImpl;
import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.html.HTMLDOMImplementation;
import mf.org.w3c.dom.html.HTMLDocument;


/**
 * Provides number of methods for performing operations that are independent
 * of any particular instance of the document object model. This class is
 * unconstructable, the only way to obtain an instance of a DOM implementation
 * is by calling the static method {@link #getDOMImplementation}.
 *
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @version $Revision: 645327 $ $Date: 2008-04-06 19:17:54 -0400 (Sun, 06 Apr 2008) $
 * @xerces.internal
 * @see mf.org.w3c.dom.DOMImplementation
 */
public class HTMLDOMImplementationImpl
        extends DOMImplementationImpl
        implements HTMLDOMImplementation {


    /**
     * Holds a reference to the single instance of the DOM implementation.
     * Only one instance is required since this class is multiple entry.
     */
    private static final HTMLDOMImplementation _instance = new HTMLDOMImplementationImpl();


    /**
     * Private constructor assures that an object of this class cannot
     * be created. The only way to obtain an object is by calling {@link
     * #getDOMImplementation}.
     */
    private HTMLDOMImplementationImpl() {
    }

    /**
     * Returns an instance of a {@link HTMLDOMImplementation} that can be
     * used to perform operations that are not specific to a particular
     * document instance, e.g. to create a new document.
     *
     * @return Reference to a valid DOM implementation
     */
    public static HTMLDOMImplementation getHTMLDOMImplementation() {
        return _instance;
    }

    /**
     * Create a new HTML document of the specified <TT>TITLE</TT> text.
     *
     * @param title The document title text
     * @return New HTML document
     */
    @Override
    public final HTMLDocument createHTMLDocument(String title)
            throws DOMException {
        HTMLDocument doc;

        if (title == null)
            throw new NullPointerException("HTM014 Argument 'title' is null.");
        doc = new HTMLDocumentImpl();
        doc.setTitle(title);
        return doc;
    }


}

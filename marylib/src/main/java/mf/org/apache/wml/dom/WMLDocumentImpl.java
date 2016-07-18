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

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import mf.org.apache.wml.WMLDocument;
import mf.org.apache.xerces.dom.DocumentImpl;
import mf.org.apache.xerces.dom.ElementImpl;
import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.DocumentType;
import mf.org.w3c.dom.Element;


/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLDocumentImpl.java 890457 2009-12-14 19:47:46Z mrglavas $
 * @xerces.internal
 */
public class WMLDocumentImpl extends DocumentImpl implements WMLDocument {

    private static final long serialVersionUID = -6582904849512384104L;
    private static final Class[] _elemClassSigWML =
            new Class[]{WMLDocumentImpl.class, String.class};
    private static Hashtable _elementTypesWML;

    static {
        _elementTypesWML = new Hashtable();
        _elementTypesWML.put("b", WMLBElementImpl.class);
        _elementTypesWML.put("noop", WMLNoopElementImpl.class);
        _elementTypesWML.put("a", WMLAElementImpl.class);
        _elementTypesWML.put("setvar", WMLSetvarElementImpl.class);
        _elementTypesWML.put("access", WMLAccessElementImpl.class);
        _elementTypesWML.put("strong", WMLStrongElementImpl.class);
        _elementTypesWML.put("postfield", WMLPostfieldElementImpl.class);
        _elementTypesWML.put("do", WMLDoElementImpl.class);
        _elementTypesWML.put("wml", WMLWmlElementImpl.class);
        _elementTypesWML.put("tr", WMLTrElementImpl.class);
        _elementTypesWML.put("go", WMLGoElementImpl.class);
        _elementTypesWML.put("big", WMLBigElementImpl.class);
        _elementTypesWML.put("anchor", WMLAnchorElementImpl.class);
        _elementTypesWML.put("timer", WMLTimerElementImpl.class);
        _elementTypesWML.put("small", WMLSmallElementImpl.class);
        _elementTypesWML.put("optgroup", WMLOptgroupElementImpl.class);
        _elementTypesWML.put("head", WMLHeadElementImpl.class);
        _elementTypesWML.put("td", WMLTdElementImpl.class);
        _elementTypesWML.put("fieldset", WMLFieldsetElementImpl.class);
        _elementTypesWML.put("img", WMLImgElementImpl.class);
        _elementTypesWML.put("refresh", WMLRefreshElementImpl.class);
        _elementTypesWML.put("onevent", WMLOneventElementImpl.class);
        _elementTypesWML.put("input", WMLInputElementImpl.class);
        _elementTypesWML.put("prev", WMLPrevElementImpl.class);
        _elementTypesWML.put("table", WMLTableElementImpl.class);
        _elementTypesWML.put("meta", WMLMetaElementImpl.class);
        _elementTypesWML.put("template", WMLTemplateElementImpl.class);
        _elementTypesWML.put("br", WMLBrElementImpl.class);
        _elementTypesWML.put("option", WMLOptionElementImpl.class);
        _elementTypesWML.put("u", WMLUElementImpl.class);
        _elementTypesWML.put("p", WMLPElementImpl.class);
        _elementTypesWML.put("select", WMLSelectElementImpl.class);
        _elementTypesWML.put("em", WMLEmElementImpl.class);
        _elementTypesWML.put("i", WMLIElementImpl.class);
        _elementTypesWML.put("card", WMLCardElementImpl.class);
    }

    /* DOM level 2 */
    public WMLDocumentImpl(DocumentType doctype) {
        super(doctype, false);
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        Class elemClass;
        Constructor cnst;

        elemClass = (Class) _elementTypesWML.get(tagName);
        if (elemClass != null) {
            try {
                cnst = elemClass.getConstructor(_elemClassSigWML);
                return (Element) cnst.newInstance(this, tagName);
            } catch (Exception except) {
                Throwable thrw;

                if (except instanceof java.lang.reflect.InvocationTargetException)
                    thrw = ((java.lang.reflect.InvocationTargetException) except).getTargetException();
                else
                    thrw = except;

                System.out.println("Exception " + thrw.getClass().getName());
                System.out.println(thrw.getMessage());

                throw new IllegalStateException("Tag '" + tagName + "' associated with an Element class that failed to construct.");
            }
        }
        return new WMLElementImpl(this, tagName);
    }

    /* (non-Javadoc)
     * @see CoreDocumentImpl#canRenameElements()
     */
    @Override
    protected boolean canRenameElements(String newNamespaceURI, String newNodeName, ElementImpl el) {
        // check whether a class change is required
        return _elementTypesWML.get(newNodeName) == _elementTypesWML.get(el.getTagName());
    }
}

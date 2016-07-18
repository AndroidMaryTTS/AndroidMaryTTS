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

import mf.org.apache.wml.WMLDOMImplementation;
import mf.org.apache.xerces.dom.CoreDocumentImpl;
import mf.org.apache.xerces.dom.DOMImplementationImpl;
import mf.org.w3c.dom.DOMImplementation;
import mf.org.w3c.dom.DocumentType;


/**
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLDOMImplementationImpl.java 809736 2009-08-31 20:43:19Z mrglavas $
 * @xerces.internal
 */
public class WMLDOMImplementationImpl extends DOMImplementationImpl implements WMLDOMImplementation {

    static final DOMImplementationImpl singleton = new WMLDOMImplementationImpl();

    /**
     * NON-DOM: Obtain and return the single shared object
     */
    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    //
    // Protected methods
    //

    @Override
    protected CoreDocumentImpl createDocument(DocumentType doctype) {
        return new WMLDocumentImpl(doctype);
    }
}


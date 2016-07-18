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

package mf.org.apache.xerces.dom;

import mf.org.apache.xerces.dom3.as.ASModel;
import mf.org.apache.xerces.dom3.as.DOMASBuilder;
import mf.org.apache.xerces.dom3.as.DOMASWriter;
import mf.org.apache.xerces.dom3.as.DOMImplementationAS;
import mf.org.apache.xerces.parsers.DOMASBuilderImpl;
import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.DOMImplementation;


/**
 * The DOMImplementation class is description of a particular
 * implementation of the Document Object Model. As such its data is
 * static, shared by all instances of this implementation.
 * <p/>
 * The DOM API requires that it be a real object rather than static
 * methods. However, there's nothing that says it can't be a singleton,
 * so that's how I've implemented it.
 * <p/>
 * This particular class, along with DocumentImpl, supports the DOM
 * Core, DOM Level 2 optional mofules, and Abstract Schemas (Experimental).
 *
 * @version $Id: ASDOMImplementationImpl.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @since PR-DOM-Level-1-19980818.
 * @deprecated
 */
@Deprecated
public class ASDOMImplementationImpl extends DOMImplementationImpl
        implements DOMImplementationAS {


    // static

    /**
     * Dom implementation singleton.
     */
    static final ASDOMImplementationImpl singleton = new ASDOMImplementationImpl();


    //
    // Public methods
    //

    /**
     * NON-DOM: Obtain and return the single shared object
     */
    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    //
    // DOM L3 Abstract Schemas:
    // REVISIT: implement hasFeature()
    //

    /**
     * DOM Level 3 WD - Experimental.
     * Creates an ASModel.
     *
     * @param isNamespaceAware Allow creation of <code>ASModel</code> with
     *                         this attribute set to a specific value.
     * @return A <code>null</code> return indicates failure.what is a
     * failure? Could be a system error.
     */
    @Override
    public ASModel createAS(boolean isNamespaceAware) {
        return new ASModelImpl(isNamespaceAware);
    }

    /**
     * DOM Level 3 WD - Experimental.
     * Creates an <code>DOMASBuilder</code>.Do we need the method since we
     * already have <code>DOMImplementationLS.createDOMParser</code>?
     *
     * @return DOMASBuilder
     */
    @Override
    public DOMASBuilder createDOMASBuilder() {
        return new DOMASBuilderImpl();
    }


    /**
     * DOM Level 3 WD - Experimental.
     * Creates an <code>DOMASWriter</code>.
     *
     * @return a DOMASWriter
     */
    @Override
    public DOMASWriter createDOMASWriter() {
        String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
    }


} // class DOMImplementationImpl

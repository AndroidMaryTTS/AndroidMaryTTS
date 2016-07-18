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

package mf.org.apache.xerces.impl.xs;

import mf.org.apache.xerces.dom.DOMMessageFormatter;
import mf.org.apache.xerces.dom.PSVIDOMImplementationImpl;
import mf.org.apache.xerces.impl.xs.util.LSInputListImpl;
import mf.org.apache.xerces.impl.xs.util.StringListImpl;
import mf.org.apache.xerces.xs.LSInputList;
import mf.org.apache.xerces.xs.StringList;
import mf.org.apache.xerces.xs.XSException;
import mf.org.apache.xerces.xs.XSImplementation;
import mf.org.apache.xerces.xs.XSLoader;
import mf.org.w3c.dom.DOMImplementation;
import mf.org.w3c.dom.ls.LSInput;


/**
 * Implements XSImplementation interface that allows one to retrieve an instance of <code>XSLoader</code>.
 * This interface should be implemented on the same object that implements
 * DOMImplementation.
 *
 * @author Elena Litani, IBM
 * @version $Id: XSImplementationImpl.java 726375 2008-12-14 05:48:29Z mrglavas $
 * @xerces.internal
 */
public class XSImplementationImpl extends PSVIDOMImplementationImpl
        implements XSImplementation {

    //
    // Data
    //

    // static

    /**
     * Dom implementation singleton.
     */
    static final XSImplementationImpl singleton = new XSImplementationImpl();

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
    // DOMImplementation methods
    //

    /**
     * Test if the DOM implementation supports a specific "feature" --
     * currently meaning language and level thereof.
     *
     * @param feature The package name of the feature to test.
     *                In Level 1, supported values are "HTML" and "XML" (case-insensitive).
     *                At this writing, org.apache.xerces.dom supports only XML.
     * @param version The version number of the feature being tested.
     *                This is interpreted as "Version of the DOM API supported for the
     *                specified Feature", and in Level 1 should be "1.0"
     * @return true iff this implementation is compatable with the specified
     * feature and version.
     */
    @Override
    public boolean hasFeature(String feature, String version) {

        return (feature.equalsIgnoreCase("XS-Loader") && (version == null || version.equals("1.0")) ||
                super.hasFeature(feature, version));
    } // hasFeature(String,String):boolean

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSImplementation#createXSLoader(org.apache.xerces.xs.StringList)
     */
    @Override
    public XSLoader createXSLoader(StringList versions) throws XSException {
        XSLoader loader = new XSLoaderImpl();
        if (versions == null) {
            return loader;
        }
        for (int i = 0; i < versions.getLength(); i++) {
            if (!versions.item(i).equals("1.0")) {
                String msg =
                        DOMMessageFormatter.formatMessage(
                                DOMMessageFormatter.DOM_DOMAIN,
                                "FEATURE_NOT_SUPPORTED",
                                new Object[]{versions.item(i)});
                throw new XSException(XSException.NOT_SUPPORTED_ERR, msg);
            }
        }
        return loader;
    }

    @Override
    public StringList createStringList(String[] values) {
        int length = (values != null) ? values.length : 0;
        return (length != 0) ? new StringListImpl(values.clone(), length) : StringListImpl.EMPTY_LIST;
    }

    @Override
    public LSInputList createLSInputList(LSInput[] values) {
        int length = (values != null) ? values.length : 0;
        return (length != 0) ? new LSInputListImpl(values.clone(), length) : LSInputListImpl.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.XSImplementation#getRecognizedVersions()
     */
    @Override
    public StringList getRecognizedVersions() {
        StringListImpl list = new StringListImpl(new String[]{"1.0"}, 1);
        return list;
    }

} // class XSImplementationImpl

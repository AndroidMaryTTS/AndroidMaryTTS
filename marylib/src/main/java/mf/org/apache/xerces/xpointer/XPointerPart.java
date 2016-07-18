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

package mf.org.apache.xerces.xpointer;

import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.QName;
import mf.org.apache.xerces.xni.XMLAttributes;
import mf.org.apache.xerces.xni.XNIException;

/**
 * <p>
 * Used for scheme specific parsing and evaluation of an XPointer expression.
 * This interface applies to both ShortHand and SchemeBased XPointer
 * expressions.
 * </p>
 *
 * @version $Id: XPointerPart.java 603808 2007-12-13 03:44:48Z mrglavas $
 * @xerces.internal
 */
public interface XPointerPart {

    // The start element event
    int EVENT_ELEMENT_START = 0;

    // The end element event
    int EVENT_ELEMENT_END = 1;

    // The empty element event    
    int EVENT_ELEMENT_EMPTY = 2;

    /**
     * Provides scheme specific parsing of a XPointer expression i.e.
     * the PointerPart or ShortHandPointer.
     *
     * @param part A String representing the PointerPart or ShortHandPointer.
     * @throws XNIException Thrown if the PointerPart string does not conform to
     *                      the syntax defined by its scheme.
     */
    void parseXPointer(String part) throws XNIException;

    /**
     * Evaluates an XML resource with respect to an XPointer expressions
     * by checking if it's element and attributes parameters match the
     * criteria specified in the xpointer expression.
     *
     * @param element    - The name of the element.
     * @param attributes - The element attributes.
     * @param augs       - Additional information that may include infoset augmentations
     * @param event      - An integer indicating
     *                   0 - The start of an element
     *                   1 - The end of an element
     *                   2 - An empty element call
     * @throws XNIException Thrown to signal an error
     */
    boolean resolveXPointer(QName element, XMLAttributes attributes,
                            Augmentations augs, int event) throws XNIException;

    /**
     * Returns true if the XPointer expression resolves to a resource fragment
     * specified as input else returns false.
     *
     * @return True if the xpointer expression matches a fragment in the resource
     * else returns false.
     * @throws XNIException Thrown to signal an error
     */
    boolean isFragmentResolved() throws XNIException;

    /**
     * Returns true if the XPointer expression resolves to a non-element child
     * of the current resource fragment.
     *
     * @return True if the XPointer expression resolves to a non-element child
     * of the current resource fragment.
     * @throws XNIException Thrown to signal an error
     */
    boolean isChildFragmentResolved() throws XNIException;

    /**
     * Returns a String containing the scheme name of the PointerPart
     * or the name of the ShortHand Pointer.
     *
     * @return A String containing the scheme name of the PointerPart.
     */
    String getSchemeName();

    /**
     * Sets the scheme name of the PointerPart or the ShortHand Pointer name.
     *
     * @param schemeName A String containing the scheme name of the PointerPart.
     */
    void setSchemeName(String schemeName);

    /**
     * Returns a String containing the scheme data of the PointerPart.
     *
     * @return A String containing the scheme data of the PointerPart.
     */
    String getSchemeData();

    /**
     * Sets the scheme data of the PointerPart.
     *
     * @param schemeData A String containing the scheme data of the PointerPart.
     */
    void setSchemeData(String schemeData);

}
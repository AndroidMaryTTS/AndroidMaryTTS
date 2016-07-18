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

import java.util.ArrayList;
import java.util.Vector;

import mf.org.w3c.dom.DOMStringList;

/**
 * DOM Level 3
 * <p/>
 * This class implements the DOM Level 3 Core interface DOMStringList.
 *
 * @author Neil Delima, IBM
 * @xerces.internal
 */
public class DOMStringListImpl implements DOMStringList {

    // A collection of DOMString values
    private final ArrayList fStrings;

    /**
     * Construct an empty list of DOMStringListImpl
     */
    public DOMStringListImpl() {
        fStrings = new ArrayList();
    }

    /**
     * Construct a DOMStringListImpl from an ArrayList
     */
    public DOMStringListImpl(ArrayList params) {
        fStrings = params;
    }

    /**
     * Construct a DOMStringListImpl from a Vector
     */
    public DOMStringListImpl(Vector params) {
        fStrings = new ArrayList(params);
    }

    /**
     * @see mf.org.w3c.dom.DOMStringList#item(int)
     */
    @Override
    public String item(int index) {
        final int length = getLength();
        if (index >= 0 && index < length) {
            return (String) fStrings.get(index);
        }
        return null;
    }

    /**
     * @see mf.org.w3c.dom.DOMStringList#getLength()
     */
    @Override
    public int getLength() {
        return fStrings.size();
    }

    /**
     * @see mf.org.w3c.dom.DOMStringList#contains(String)
     */
    @Override
    public boolean contains(String param) {
        return fStrings.contains(param);
    }

    /**
     * DOM Internal:
     * Add a <code>DOMString</code> to the list.
     *
     * @param param A string to add to the list
     */
    public void add(String param) {
        fStrings.add(param);
    }

}

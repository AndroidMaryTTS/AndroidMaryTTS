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
package mf.org.apache.wml;

import mf.org.w3c.dom.Element;

/**
 * <p>The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from
 * <a href="http://www.wapforum.org/DTD/wml_1.1.xml">
 * http://www.wapforum.org/DTD/wml_1.1.xml</a></p>
 * <p/>
 * <p>All WML Elements are derived from this class that contains two
 * core attributes defined in the DTD.</p>
 *
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLElement.java 447258 2006-09-18 05:41:23Z mrglavas $
 */

public interface WMLElement extends Element {

    String getId();

    /**
     * The element's identifier which is unique in a single deck.
     * (Section 8.9, WAP WML Version 16-Jun-1999)
     */
    void setId(String newValue);

    String getClassName();

    /**
     * The 'class' attribute of a element that affiliates an elements
     * with one or more elements.
     * (Section 8.9, WAP WML Version 16-Jun-1999)
     */
    void setClassName(String newValue);
}

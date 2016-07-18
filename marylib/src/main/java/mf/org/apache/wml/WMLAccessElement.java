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

/**
 * <p>The interface is modeled after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from
 * <a href="http://www.wapforum.org/DTD/wml_1.1.xml">
 * http://www.wapforum.org/DTD/wml_1.1.xml</a></p>
 * <p/>
 * <p>'access' element specifics the access control for the entire deck
 * (Section 11.3.1, WAP WML Version 16-Jun-1999)</p>
 *
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLAccessElement.java 447258 2006-09-18 05:41:23Z mrglavas $
 */

public interface WMLAccessElement extends WMLElement {

    String getDomain();

    /**
     * A deck's domain and path attributes specify which deck may
     * access it.
     * <p/>
     * domain attribute is suffix-matched against the domain name
     * portion of the referring URI
     */
    void setDomain(String newValue);

    String getPath();

    /**
     * path attribute is prefix-matched against the path portion of
     * the referring URI
     */
    void setPath(String newValue);
}

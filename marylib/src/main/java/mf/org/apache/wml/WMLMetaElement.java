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
 * <p>The meta element contains meta-info of an WML deck
 * (Section 11.3.2, WAP WML Version 16-Jun-1999)</p>
 *
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLMetaElement.java 447258 2006-09-18 05:41:23Z mrglavas $
 */
public interface WMLMetaElement extends WMLElement {

    String getName();

    /**
     * 'name' attribute specific the property name
     */
    void setName(String newValue);

    String getHttpEquiv();

    /**
     * 'http-equiv' attribute indicates the property should be
     * interpret as HTTP header.
     */
    void setHttpEquiv(String newValue);

    boolean getForua();

    /**
     * 'forua' attribute specifies whether a intermediate agent should
     * remove this meta element. A value of false means the
     * intermediate agent must remove the element.
     */
    void setForua(boolean newValue);

    String getScheme();

    /**
     * 'scheme' attribute specifies a form that may be used to
     * interpret the property value
     */
    void setScheme(String newValue);

    String getContent();

    /**
     * 'content' attribute specifies the property value
     */
    void setContent(String newValue);
}

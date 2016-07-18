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
 * <p>'select' element lets user pick from a list of options.
 * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)</p>
 *
 * @author <a href="mailto:david@topware.com.tw">David Li</a>
 * @version $Id: WMLSelectElement.java 447258 2006-09-18 05:41:23Z mrglavas $
 */
public interface WMLSelectElement extends WMLElement {

    int getTabIndex();

    /**
     * 'tabindex' specifies the tabbing position of the element
     * (Section 11.6.1, WAP WML Version 16-Jun-1999)
     */
    void setTabIndex(int newValue);

    boolean getMultiple();

    /**
     * 'multiple' indicates whether a list accept multiple selection
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    void setMultiple(boolean newValue);

    String getName();

    /**
     * 'name' specifies the name of variable to be set.
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    void setName(String newValue);

    String getValue();

    /**
     * 'value' specifics the default value of the variable of 'name'
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    void setValue(String newValue);

    String getTitle();

    /**
     * 'title' specifies a title for this element
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    void setTitle(String newValue);

    String getIName();

    /**
     * 'iname' specifies name of variable to be set with the index
     * result of selection.
     * (Section 11.6.2.1, WAP WML Version 16-Jun-1999)
     */
    void setIName(String newValue);

    String getIValue();

    /**
     * 'ivalue' specifies the default of the variable 'iname'
     */
    void setIValue(String newValue);

    String getXmlLang();

    /**
     * 'xml:lang' specifics the natural or formal language in which
     * the document is written.
     * (Section 8.8, WAP WML Version 16-Jun-1999)
     */
    void setXmlLang(String newValue);
}

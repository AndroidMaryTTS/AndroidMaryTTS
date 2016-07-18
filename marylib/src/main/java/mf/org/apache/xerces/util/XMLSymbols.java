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

package mf.org.apache.xerces.util;

/**
 * All internalized xml symbols. They can be compared using "==".
 *
 * @author Sandy Gao, IBM
 * @version $Id: XMLSymbols.java 447241 2006-09-18 05:12:57Z mrglavas $
 */
public class XMLSymbols {

    /**
     * The empty string.
     */
    public final static String EMPTY_STRING = "".intern();

    //==========================
    // Commonly used strings
    //==========================
    /**
     * The internalized "xml" prefix.
     */
    public final static String PREFIX_XML = "xml".intern();

    //==========================
    // Namespace prefixes/uris
    //==========================
    /**
     * The internalized "xmlns" prefix.
     */
    public final static String PREFIX_XMLNS = "xmlns".intern();
    /**
     * Symbol: "ANY".
     */
    public static final String fANYSymbol = "ANY".intern();

    //==========================
    // DTD symbols
    //==========================
    /**
     * Symbol: "CDATA".
     */
    public static final String fCDATASymbol = "CDATA".intern();
    /**
     * Symbol: "ID".
     */
    public static final String fIDSymbol = "ID".intern();
    /**
     * Symbol: "IDREF".
     */
    public static final String fIDREFSymbol = "IDREF".intern();
    /**
     * Symbol: "IDREFS".
     */
    public static final String fIDREFSSymbol = "IDREFS".intern();
    /**
     * Symbol: "ENTITY".
     */
    public static final String fENTITYSymbol = "ENTITY".intern();
    /**
     * Symbol: "ENTITIES".
     */
    public static final String fENTITIESSymbol = "ENTITIES".intern();
    /**
     * Symbol: "NMTOKEN".
     */
    public static final String fNMTOKENSymbol = "NMTOKEN".intern();
    /**
     * Symbol: "NMTOKENS".
     */
    public static final String fNMTOKENSSymbol = "NMTOKENS".intern();
    /**
     * Symbol: "NOTATION".
     */
    public static final String fNOTATIONSymbol = "NOTATION".intern();
    /**
     * Symbol: "ENUMERATION".
     */
    public static final String fENUMERATIONSymbol = "ENUMERATION".intern();
    /**
     * Symbol: "#IMPLIED.
     */
    public static final String fIMPLIEDSymbol = "#IMPLIED".intern();
    /**
     * Symbol: "#REQUIRED".
     */
    public static final String fREQUIREDSymbol = "#REQUIRED".intern();
    /**
     * Symbol: "#FIXED".
     */
    public static final String fFIXEDSymbol = "#FIXED".intern();

    // public constructor.
    public XMLSymbols() {
    }


}

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


package mf.org.apache.xerces.jaxp;

/**
 * This interface holds JAXP constant property/attribute names and values.
 * Since JAXP 1.2 is a maintenance release of JAXP 1.1, no public
 * signatures are allowed so these values cannot be exposed in the
 * javax.xml.parsers package.  Once equivalent constants have been defined
 * in future JAXP spec versions, this interface can be removed.
 *
 * @author Edwin Goei
 * @version $Id: JAXPConstants.java 447237 2006-09-18 05:03:10Z mrglavas $
 */
public interface JAXPConstants {
    String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";

    String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";
}

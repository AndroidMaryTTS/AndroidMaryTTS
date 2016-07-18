// ResolvingXMLReader.java - An XMLReader that performs catalog resolution

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

package mf.org.apache.xml.resolver.tools;

import mf.javax.xml.parsers.SAXParser;
import mf.javax.xml.parsers.SAXParserFactory;
import mf.org.apache.xml.resolver.CatalogManager;


/**
 * A SAX XMLReader that performs catalog-based entity resolution.
 * <p/>
 * <p>This class implements a SAX XMLReader that performs entity resolution
 * using the CatalogResolver. The actual, underlying parser is obtained
 * from a SAXParserFactory.</p>
 * </p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 * @see CatalogResolver
 * @see org.xml.sax.XMLReader
 */
public class ResolvingXMLReader extends ResolvingXMLFilter {
    /**
     * Make the parser Namespace aware?
     */
    public static boolean namespaceAware = true;

    /**
     * Make the parser validating?
     */
    public static boolean validating = false;

    /**
     * Construct a new reader from the JAXP factory.
     * <p/>
     * <p>In order to do its job, a ResolvingXMLReader must in fact be
     * a filter. So the only difference between this code and the filter
     * code is that the constructor builds a new reader.</p>
     */
    public ResolvingXMLReader() {
        super();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(namespaceAware);
        spf.setValidating(validating);
        try {
            SAXParser parser = spf.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Construct a new reader from the JAXP factory.
     * <p/>
     * <p>In order to do its job, a ResolvingXMLReader must in fact be
     * a filter. So the only difference between this code and the filter
     * code is that the constructor builds a new reader.</p>
     */
    public ResolvingXMLReader(CatalogManager manager) {
        super(manager);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(namespaceAware);
        spf.setValidating(validating);
        try {
            SAXParser parser = spf.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


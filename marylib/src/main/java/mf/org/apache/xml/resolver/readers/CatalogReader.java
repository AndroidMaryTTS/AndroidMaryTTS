// CatalogReader.java - An interface for reading catalog files

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

package mf.org.apache.xml.resolver.readers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import mf.org.apache.xml.resolver.Catalog;
import mf.org.apache.xml.resolver.CatalogException;

/**
 * The CatalogReader interface.
 * <p/>
 * <p>The Catalog class requires that classes implement this interface
 * in order to be used to read catalogs. Examples of CatalogReaders
 * include the TextCatalogReader, the SAXCatalogReader, and the
 * DOMCatalogReader.</p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 * @see Catalog
 */
public interface CatalogReader {
    /**
     * Read a catalog from a file.
     * <p/>
     * <p>This class reads a catalog from a URL.</p>
     *
     * @param catalog The catalog for which this reader is called.
     * @param fileUrl The URL of a document to be read.
     * @throws MalformedURLException         if the specified URL cannot be
     *                                       turned into a URL object.
     * @throws IOException                   if the URL cannot be read.
     * @throws UnknownCatalogFormatException if the catalog format is
     *                                       not recognized.
     * @throws UnparseableCatalogException   if the catalog cannot be parsed.
     *                                       (For example, if it is supposed to be XML and isn't well-formed.)
     */
    void readCatalog(Catalog catalog, String fileUrl)
            throws IOException, CatalogException;

    /**
     * Read a catalog from an input stream.
     * <p/>
     * <p>This class reads a catalog from an input stream.</p>
     *
     * @param catalog The catalog for which this reader is called.
     * @param is      The input stream that is to be read.
     * @throws IOException                   if the URL cannot be read.
     * @throws UnknownCatalogFormatException if the catalog format is
     *                                       not recognized.
     * @throws UnparseableCatalogException   if the catalog cannot be parsed.
     *                                       (For example, if it is supposed to be XML and isn't well-formed.)
     */
    void readCatalog(Catalog catalog, InputStream is)
            throws IOException, CatalogException;
}

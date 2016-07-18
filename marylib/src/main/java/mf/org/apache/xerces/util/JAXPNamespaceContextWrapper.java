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

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import mf.javax.xml.XMLConstants;
import mf.org.apache.xerces.xni.NamespaceContext;

/**
 * <p>A read-only XNI wrapper around a JAXP NamespaceContext.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: JAXPNamespaceContextWrapper.java 694381 2008-09-11 17:43:36Z mrglavas $
 */
public final class JAXPNamespaceContextWrapper implements NamespaceContext {

    private final Vector fAllPrefixes = new Vector();
    private mf.javax.xml.namespace.NamespaceContext fNamespaceContext;
    private SymbolTable fSymbolTable;
    private List fPrefixes;
    private int[] fContext = new int[8];
    private int fCurrentContext;

    public JAXPNamespaceContextWrapper(SymbolTable symbolTable) {
        setSymbolTable(symbolTable);
    }

    public mf.javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return fNamespaceContext;
    }

    public void setNamespaceContext(mf.javax.xml.namespace.NamespaceContext context) {
        fNamespaceContext = context;
    }

    public SymbolTable getSymbolTable() {
        return fSymbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        fSymbolTable = symbolTable;
    }

    public List getDeclaredPrefixes() {
        return fPrefixes;
    }

    public void setDeclaredPrefixes(List prefixes) {
        fPrefixes = prefixes;
    }
    
    /*
     * NamespaceContext methods
     */

    @Override
    public String getURI(String prefix) {
        if (fNamespaceContext != null) {
            String uri = fNamespaceContext.getNamespaceURI(prefix);
            if (uri != null && !XMLConstants.NULL_NS_URI.equals(uri)) {
                return (fSymbolTable != null) ? fSymbolTable.addSymbol(uri) : uri.intern();
            }
        }
        return null;
    }

    @Override
    public String getPrefix(String uri) {
        if (fNamespaceContext != null) {
            if (uri == null) {
                uri = XMLConstants.NULL_NS_URI;
            }
            String prefix = fNamespaceContext.getPrefix(uri);
            if (prefix == null) {
                prefix = XMLConstants.DEFAULT_NS_PREFIX;
            }
            return (fSymbolTable != null) ? fSymbolTable.addSymbol(prefix) : prefix.intern();
        }
        return null;
    }

    @Override
    public Enumeration getAllPrefixes() {
        // There may be duplicate prefixes in the list so we 
        // first transfer them to a set to ensure uniqueness.
        return Collections.enumeration(new TreeSet(fAllPrefixes));
    }

    @Override
    public void pushContext() {
        // extend the array, if necessary
        if (fCurrentContext + 1 == fContext.length) {
            int[] contextarray = new int[fContext.length * 2];
            System.arraycopy(fContext, 0, contextarray, 0, fContext.length);
            fContext = contextarray;
        }
        // push context
        fContext[++fCurrentContext] = fAllPrefixes.size();
        if (fPrefixes != null) {
            fAllPrefixes.addAll(fPrefixes);
        }
    }

    @Override
    public void popContext() {
        fAllPrefixes.setSize(fContext[fCurrentContext--]);
    }

    @Override
    public boolean declarePrefix(String prefix, String uri) {
        return true;
    }

    @Override
    public int getDeclaredPrefixCount() {
        return (fPrefixes != null) ? fPrefixes.size() : 0;
    }

    @Override
    public String getDeclaredPrefixAt(int index) {
        return (String) fPrefixes.get(index);
    }

    @Override
    public void reset() {
        fCurrentContext = 0;
        fContext[fCurrentContext] = 0;
        fAllPrefixes.clear();
    }

} // JAXPNamespaceContextWrapper

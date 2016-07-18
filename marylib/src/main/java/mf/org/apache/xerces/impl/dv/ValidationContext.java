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

package mf.org.apache.xerces.impl.dv;

import java.util.Locale;

/**
 * ValidationContext has all the information required for the
 * validation of: id, idref, entity, notation, qname
 *
 * @author Sandy Gao, IBM
 * @version $Id: ValidationContext.java 713638 2008-11-13 04:42:18Z mrglavas $
 * @xerces.internal
 */
public interface ValidationContext {
    // whether to validate against facets
    boolean needFacetChecking();

    // whether to do extra id/idref/entity checking
    boolean needExtraChecking();

    // whether we need to normalize the value that is passed!
    boolean needToNormalize();

    // are namespaces relevant in this context?
    boolean useNamespaces();

    // entity
    boolean isEntityDeclared(String name);

    boolean isEntityUnparsed(String name);

    // id
    boolean isIdDeclared(String name);

    void addId(String name);

    // idref
    void addIdRef(String name);

    // get symbol from symbol table
    String getSymbol(String symbol);

    // qname
    String getURI(String prefix);

    // Locale
    Locale getLocale();
}

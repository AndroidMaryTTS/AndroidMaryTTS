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

package mf.org.apache.xerces.jaxp.validation;

import mf.org.apache.xerces.xni.grammars.Grammar;
import mf.org.apache.xerces.xni.grammars.XMLGrammarDescription;
import mf.org.apache.xerces.xni.grammars.XMLGrammarPool;

/**
 * <p>Implementation of Schema for W3C XML Schemas
 * which contains no schema components.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: EmptyXMLSchema.java 447235 2006-09-18 05:01:44Z mrglavas $
 */
final class EmptyXMLSchema extends AbstractXMLSchema implements XMLGrammarPool {

    /**
     * Zero length grammar array.
     */
    private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];

    public EmptyXMLSchema() {
    }
    
    /*
     * XMLGrammarPool methods
     */

    @Override
    public Grammar[] retrieveInitialGrammarSet(String grammarType) {
        return ZERO_LENGTH_GRAMMAR_ARRAY;
    }

    @Override
    public void cacheGrammars(String grammarType, Grammar[] grammars) {
    }

    @Override
    public Grammar retrieveGrammar(XMLGrammarDescription desc) {
        return null;
    }

    @Override
    public void lockPool() {
    }

    @Override
    public void unlockPool() {
    }

    @Override
    public void clear() {
    }
    
    /*
     * XSGrammarPoolContainer methods
     */

    @Override
    public XMLGrammarPool getGrammarPool() {
        return this;
    }

    @Override
    public boolean isFullyComposed() {
        return true;
    }

} // EmptyXMLSchema

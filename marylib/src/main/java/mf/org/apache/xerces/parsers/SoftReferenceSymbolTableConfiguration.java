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

package mf.org.apache.xerces.parsers;

import mf.org.apache.xerces.util.SoftReferenceSymbolTable;
import mf.org.apache.xerces.util.SymbolTable;
import mf.org.apache.xerces.xni.grammars.XMLGrammarPool;
import mf.org.apache.xerces.xni.parser.XMLComponentManager;


/**
 * This parser configuration extends the default configuration allowing Xerces to
 * handle usage scenarios where the names in the XML documents being parsed are mostly
 * unique by installing a memory sensitive <code>SymbolTable</code>. The internalized
 * strings stored in this <code>SymbolTable</code> are softly reachable and may be
 * cleared by the garbage collector in response to memory demand.
 *
 * @author Peter McCracken, IBM
 * @version $Id: SoftReferenceSymbolTableConfiguration.java 478344 2006-11-22 22:19:56Z mrglavas $
 * @see mf.org.apache.xerces.util.SoftReferenceSymbolTable
 */
public class SoftReferenceSymbolTableConfiguration extends
        XIncludeAwareParserConfiguration {

    /**
     * Default constructor.
     */
    public SoftReferenceSymbolTableConfiguration() {
        this(new SoftReferenceSymbolTable(), null, null);
    } // <init>()

    /**
     * Constructs a parser configuration using the specified symbol table.
     *
     * @param symbolTable The symbol table to use.
     */
    public SoftReferenceSymbolTableConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    } // <init>(SymbolTable)

    /**
     * Constructs a parser configuration using the specified symbol table and
     * grammar pool.
     * <p/>
     *
     * @param symbolTable The symbol table to use.
     * @param grammarPool The grammar pool to use.
     */
    public SoftReferenceSymbolTableConfiguration(
            SymbolTable symbolTable,
            XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } // <init>(SymbolTable,XMLGrammarPool)

    /**
     * Constructs a parser configuration using the specified symbol table,
     * grammar pool, and parent settings.
     * <p/>
     *
     * @param symbolTable    The symbol table to use.
     * @param grammarPool    The grammar pool to use.
     * @param parentSettings The parent settings.
     */
    public SoftReferenceSymbolTableConfiguration(
            SymbolTable symbolTable,
            XMLGrammarPool grammarPool,
            XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);
    }
}

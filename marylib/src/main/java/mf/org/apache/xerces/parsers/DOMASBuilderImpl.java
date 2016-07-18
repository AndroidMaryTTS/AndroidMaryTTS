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

import java.util.Vector;

import mf.org.apache.xerces.dom.ASModelImpl;
import mf.org.apache.xerces.dom3.as.ASModel;
import mf.org.apache.xerces.dom3.as.DOMASBuilder;
import mf.org.apache.xerces.dom3.as.DOMASException;
import mf.org.apache.xerces.impl.Constants;
import mf.org.apache.xerces.impl.xs.SchemaGrammar;
import mf.org.apache.xerces.impl.xs.XSGrammarBucket;
import mf.org.apache.xerces.util.SymbolTable;
import mf.org.apache.xerces.util.XMLGrammarPoolImpl;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.grammars.Grammar;
import mf.org.apache.xerces.xni.grammars.XMLGrammarPool;
import mf.org.apache.xerces.xni.parser.XMLInputSource;
import mf.org.w3c.dom.ls.LSInput;


/**
 * This is Abstract Schema DOM Builder class. It extends the DOMParserImpl
 * class. Provides support for preparsing schemas.
 *
 * @author Pavani Mukthipudi, Sun Microsystems Inc.
 * @author Neil Graham, IBM
 * @version $Id: DOMASBuilderImpl.java 447239 2006-09-18 05:08:26Z mrglavas $
 * @deprecated
 */

@Deprecated
public class DOMASBuilderImpl
        extends DOMParserImpl implements DOMASBuilder {

    //
    // Constants
    //

    // Feature ids

    protected static final String SCHEMA_FULL_CHECKING =
            Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;

    // Property ids

    protected static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    protected static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    protected static final String ENTITY_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    //
    // Data
    //

    protected XSGrammarBucket fGrammarBucket;

    protected ASModelImpl fAbstractSchema;

    //
    // Constructors
    //

    /**
     * Constructs a DOM Builder using the dtd/xml schema parser configuration.
     */
    public DOMASBuilderImpl() {
        super(new XMLGrammarCachingConfiguration());
    } // <init>

    /**
     * Constructs a DOM Builder using the specified parser configuration.
     * We must demand that the configuration extend XMLGrammarCachingConfiguration to make
     * sure all relevant methods/features are available.
     */
    public DOMASBuilderImpl(XMLGrammarCachingConfiguration config) {
        super(config);
    } // <init>(XMLParserConfiguration)

    /**
     * Constructs a DOM Builder using the specified symbol table.
     */
    public DOMASBuilderImpl(SymbolTable symbolTable) {
        super(new XMLGrammarCachingConfiguration(symbolTable));
    } // <init>(SymbolTable)


    /**
     * Constructs a DOM Builder using the specified symbol table and
     * grammar pool.
     * The grammarPool implementation should extent the default
     * implementation; otherwise, correct functioning of this class may
     * not occur.
     */
    public DOMASBuilderImpl(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
        super(new XMLGrammarCachingConfiguration(symbolTable, grammarPool));
    }

    //
    // DOMASBuilder methods
    //

    /**
     * Associate an <code>ASModel</code> with a document instance. This
     * <code>ASModel</code> will be used by the "
     * <code>validate-if-schema</code>" and "
     * <code>datatype-normalization</code>" options during the load of a new
     * <code>Document</code>.
     */
    @Override
    public ASModel getAbstractSchema() {
        return fAbstractSchema;
    }

    /**
     * Associate an <code>ASModel</code> with a document instance. This
     * <code>ASModel</code> will be used by the "
     * <code>validate-if-schema</code>" and "
     * <code>datatype-normalization</code>" options during the load of a new
     * <code>Document</code>.
     */
    @Override
    public void setAbstractSchema(ASModel abstractSchema) {

        // since the ASModel associated with this object is an attribute
        // according to the DOM IDL, we must obliterate anything
        // that was set before, rather than adding to it.
        // REVISIT:  so shouldn't we attempt to clear the
        // grammarPool before adding stuff to it?  - NG
        fAbstractSchema = (ASModelImpl) abstractSchema;

        // make sure the GrammarPool is properly initialized.
        XMLGrammarPool grammarPool = (XMLGrammarPool) fConfiguration.getProperty(DTDConfiguration.XMLGRAMMAR_POOL);
        // if there is no grammar pool, create one
        // REVISIT: ASBuilder should always create one.
        if (grammarPool == null) {
            // something's not right in this situation...
            grammarPool = new XMLGrammarPoolImpl();
            fConfiguration.setProperty(DTDConfiguration.XMLGRAMMAR_POOL,
                    grammarPool);
        }
        if (fAbstractSchema != null) {
            initGrammarPool(fAbstractSchema, grammarPool);
        }
    }

    /**
     * Parse a Abstract Schema from a location identified by an URI.
     *
     * @param uri The location of the Abstract Schema to be read.
     * @return The newly created <code>Abstract Schema</code>.
     * @throws DOMASException     Exceptions raised by <code>parseASURI()</code> originate with the
     *                            installed ErrorHandler, and thus depend on the implementation of
     *                            the <code>DOMErrorHandler</code> interfaces. The default error
     *                            handlers will raise a <code>DOMASException</code> if any form of
     *                            Abstract Schema inconsistencies or warning occurs during the parse,
     *                            but application defined errorHandlers are not required to do so.
     *                            <br> WRONG_MIME_TYPE_ERR: Raised when <code>mimeTypeCheck</code> is
     *                            <code>true</code> and the inputsource has an incorrect MIME Type.
     *                            See attribute <code>mimeTypeCheck</code>.
     * @throws DOMSystemException Exceptions raised by <code>parseURI()</code> originate with the
     *                            installed ErrorHandler, and thus depend on the implementation of
     *                            the <code>DOMErrorHandler</code> interfaces. The default error
     *                            handlers will raise a DOMSystemException if any form I/O or other
     *                            system error occurs during the parse, but application defined error
     *                            handlers are not required to do so.
     */
    @Override
    public ASModel parseASURI(String uri)
            throws Exception {
        XMLInputSource source = new XMLInputSource(null, uri, null);
        return parseASInputSource(source);
    }

    /**
     * Parse a Abstract Schema from a location identified by an
     * <code>LSInput</code>.
     *
     * @param is The <code>LSInput</code> from which the source
     *           Abstract Schema is to be read.
     * @return The newly created <code>ASModel</code>.
     * @throws DOMASException     Exceptions raised by <code>parseASURI()</code> originate with the
     *                            installed ErrorHandler, and thus depend on the implementation of
     *                            the <code>DOMErrorHandler</code> interfaces. The default error
     *                            handlers will raise a <code>DOMASException</code> if any form of
     *                            Abstract Schema inconsistencies or warning occurs during the parse,
     *                            but application defined errorHandlers are not required to do so.
     *                            <br> WRONG_MIME_TYPE_ERR: Raised when <code>mimeTypeCheck</code> is
     *                            true and the inputsource has an incorrect MIME Type. See attribute
     *                            <code>mimeTypeCheck</code>.
     * @throws DOMSystemException Exceptions raised by <code>parseURI()</code> originate with the
     *                            installed ErrorHandler, and thus depend on the implementation of
     *                            the <code>DOMErrorHandler</code> interfaces. The default error
     *                            handlers will raise a DOMSystemException if any form I/O or other
     *                            system error occurs during the parse, but application defined error
     *                            handlers are not required to do so.
     */
    @Override
    public ASModel parseASInputSource(LSInput is)
            throws Exception {

        // need to wrap the LSInput with an XMLInputSource
        XMLInputSource xis = this.dom2xmlInputSource(is);
        try {
            return parseASInputSource(xis);
        } catch (XNIException e) {
            Exception ex = e.getException();
            throw ex;
        }
    }

    ASModel parseASInputSource(XMLInputSource is) throws Exception {

        if (fGrammarBucket == null) {
            fGrammarBucket = new XSGrammarBucket();
        }

        initGrammarBucket();

        // actually do the parse:
        // save some casting
        XMLGrammarCachingConfiguration gramConfig = (XMLGrammarCachingConfiguration) fConfiguration;
        // ensure grammarPool doesn't absorb grammars while it's parsing
        gramConfig.lockGrammarPool();
        SchemaGrammar grammar = gramConfig.parseXMLSchema(is);
        gramConfig.unlockGrammarPool();

        ASModelImpl newAsModel = null;
        if (grammar != null) {
            newAsModel = new ASModelImpl();
            fGrammarBucket.putGrammar(grammar, true);
            addGrammars(newAsModel, fGrammarBucket);
        }
        return newAsModel;
    }

    // put all the grammars we have access to in the GrammarBucket
    private void initGrammarBucket() {
        fGrammarBucket.reset();
        if (fAbstractSchema != null)
            initGrammarBucketRecurse(fAbstractSchema);
    }

    private void initGrammarBucketRecurse(ASModelImpl currModel) {
        if (currModel.getGrammar() != null) {
            fGrammarBucket.putGrammar(currModel.getGrammar());
        }
        for (int i = 0; i < currModel.getInternalASModels().size(); i++) {
            ASModelImpl nextModel = (ASModelImpl) (currModel.getInternalASModels().elementAt(i));
            initGrammarBucketRecurse(nextModel);
        }
    }

    private void addGrammars(ASModelImpl model, XSGrammarBucket grammarBucket) {
        SchemaGrammar[] grammarList = grammarBucket.getGrammars();
        for (int i = 0; i < grammarList.length; i++) {
            ASModelImpl newModel = new ASModelImpl();
            newModel.setGrammar(grammarList[i]);
            model.addASModel(newModel);
        }
    } // addGrammars

    private void initGrammarPool(ASModelImpl currModel, XMLGrammarPool grammarPool) {
        // put all the grammars in fAbstractSchema into the grammar pool.
        // grammarPool must never be null!
        Grammar[] grammars = new Grammar[1];
        if ((grammars[0] = currModel.getGrammar()) != null) {
            grammarPool.cacheGrammars(grammars[0].getGrammarDescription().getGrammarType(), grammars);
        }
        Vector modelStore = currModel.getInternalASModels();
        for (int i = 0; i < modelStore.size(); i++) {
            initGrammarPool((ASModelImpl) modelStore.elementAt(i), grammarPool);
        }
    }
} // class DOMASBuilderImpl

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

package mf.org.apache.xerces.impl.xs;

import mf.org.apache.xerces.impl.dv.ValidatedInfo;
import mf.org.apache.xerces.impl.xs.util.StringListImpl;
import mf.org.apache.xerces.xs.ElementPSVI;
import mf.org.apache.xerces.xs.ItemPSVI;
import mf.org.apache.xerces.xs.ShortList;
import mf.org.apache.xerces.xs.StringList;
import mf.org.apache.xerces.xs.XSElementDeclaration;
import mf.org.apache.xerces.xs.XSModel;
import mf.org.apache.xerces.xs.XSNotationDeclaration;
import mf.org.apache.xerces.xs.XSSimpleTypeDefinition;
import mf.org.apache.xerces.xs.XSTypeDefinition;
import mf.org.apache.xerces.xs.XSValue;


/**
 * Element PSV infoset augmentations implementation.
 * The following information will be available at the startElement call:
 * name, namespace, type, notation, validation context
 * <p/>
 * The following information will be available at the endElement call:
 * nil, specified, normalized value, member type, validity, error codes,
 * default
 *
 * @author Elena Litani IBM
 * @version $Id: ElementPSVImpl.java 1024038 2010-10-18 22:06:35Z sandygao $
 * @xerces.internal
 */
public class ElementPSVImpl implements ElementPSVI {

    /**
     * element declaration
     */
    protected XSElementDeclaration fDeclaration = null;

    /**
     * type of element, could be xsi:type
     */
    protected XSTypeDefinition fTypeDecl = null;

    /**
     * true if clause 3.2 of Element Locally Valid (Element) (3.3.4)
     * is satisfied, otherwise false
     */
    protected boolean fNil = false;

    /**
     * true if the element value was provided by the schema; false otherwise.
     */
    protected boolean fSpecified = false;

    /**
     * Schema value
     */
    protected ValidatedInfo fValue = new ValidatedInfo();

    /**
     * http://www.w3.org/TR/xmlschema-1/#e-notation
     */
    protected XSNotationDeclaration fNotation = null;

    /**
     * validation attempted: none, partial, full
     */
    protected short fValidationAttempted = ElementPSVI.VALIDATION_NONE;

    /**
     * validity: valid, invalid, unknown
     */
    protected short fValidity = ElementPSVI.VALIDITY_NOTKNOWN;

    /**
     * error codes and error messages
     */
    protected String[] fErrors = null;

    /**
     * validation context: could be QName or XPath expression
     */
    protected String fValidationContext = null;

    /**
     * deferred XSModel
     **/
    protected SchemaGrammar[] fGrammars = null;

    /**
     * the schema information property
     */
    protected XSModel fSchemaInformation = null;

    //
    // ElementPSVI methods
    //

    /**
     * [schema default]
     *
     * @return The canonical lexical representation of the declaration's {value constraint} value.
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_default>XML Schema Part 1: Structures [schema default]</a>
     */
    @Override
    public String getSchemaDefault() {
        return fDeclaration == null ? null : fDeclaration.getConstraintValue();
    }

    /**
     * [schema normalized value]
     *
     * @return the normalized value of this item after validation
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_normalized_value>XML Schema Part 1: Structures [schema normalized value]</a>
     */
    @Override
    public String getSchemaNormalizedValue() {
        return fValue.getNormalizedValue();
    }

    /**
     * [schema specified]
     *
     * @return true - value was specified in schema, false - value comes from the infoset
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_specified">XML Schema Part 1: Structures [schema specified]</a>
     */
    @Override
    public boolean getIsSchemaSpecified() {
        return fSpecified;
    }

    /**
     * Determines the extent to which the document has been validated
     *
     * @return return the [validation attempted] property. The possible values are
     * NO_VALIDATION, PARTIAL_VALIDATION and FULL_VALIDATION
     */
    @Override
    public short getValidationAttempted() {
        return fValidationAttempted;
    }

    /**
     * Determine the validity of the node with respect
     * to the validation being attempted
     *
     * @return return the [validity] property. Possible values are:
     * UNKNOWN_VALIDITY, INVALID_VALIDITY, VALID_VALIDITY
     */
    @Override
    public short getValidity() {
        return fValidity;
    }

    /**
     * A list of error codes generated from validation attempts.
     * Need to find all the possible subclause reports that need reporting
     *
     * @return Array of error codes
     */
    @Override
    public StringList getErrorCodes() {
        if (fErrors == null || fErrors.length == 0) {
            return StringListImpl.EMPTY_LIST;
        }
        return new PSVIErrorList(fErrors, true);
    }

    /**
     * A list of error messages generated from the validation attempt or
     * an empty <code>StringList</code> if no errors occurred during the
     * validation attempt. The indices of error messages in this list are
     * aligned with those in the <code>[schema error code]</code> list.
     */
    @Override
    public StringList getErrorMessages() {
        if (fErrors == null || fErrors.length == 0) {
            return StringListImpl.EMPTY_LIST;
        }
        return new PSVIErrorList(fErrors, false);
    }

    // This is the only information we can provide in a pipeline.
    @Override
    public String getValidationContext() {
        return fValidationContext;
    }

    /**
     * [nil]
     *
     * @return true if clause 3.2 of Element Locally Valid (Element) (3.3.4) above is satisfied, otherwise false
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-nil>XML Schema Part 1: Structures [nil]</a>
     */
    @Override
    public boolean getNil() {
        return fNil;
    }

    /**
     * [notation]
     *
     * @return The notation declaration.
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-notation>XML Schema Part 1: Structures [notation]</a>
     */
    @Override
    public XSNotationDeclaration getNotation() {
        return fNotation;
    }

    /**
     * An item isomorphic to the type definition used to validate this element.
     *
     * @return a type declaration
     */
    @Override
    public XSTypeDefinition getTypeDefinition() {
        return fTypeDecl;
    }

    /**
     * If and only if that type definition is a simple type definition
     * with {variety} union, or a complex type definition whose {content type}
     * is a simple thype definition with {variety} union, then an item isomorphic
     * to that member of the union's {member type definitions} which actually
     * validated the element item's normalized value.
     *
     * @return a simple type declaration
     */
    @Override
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return fValue.getMemberTypeDefinition();
    }

    /**
     * An item isomorphic to the element declaration used to validate
     * this element.
     *
     * @return an element declaration
     */
    @Override
    public XSElementDeclaration getElementDeclaration() {
        return fDeclaration;
    }

    /**
     * [schema information]
     *
     * @return The schema information property if it's the validation root,
     * null otherwise.
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_information">XML Schema Part 1: Structures [schema information]</a>
     */
    @Override
    public synchronized XSModel getSchemaInformation() {
        if (fSchemaInformation == null && fGrammars != null) {
            fSchemaInformation = new XSModelImpl(fGrammars);
        }
        return fSchemaInformation;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getActualNormalizedValue()
     */
    @Override
    public Object getActualNormalizedValue() {
        return fValue.getActualValue();
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getActualNormalizedValueType()
     */
    @Override
    public short getActualNormalizedValueType() {
        return fValue.getActualValueType();
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getItemValueTypes()
     */
    @Override
    public ShortList getItemValueTypes() {
        return fValue.getListValueTypes();
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getSchemaValue()
     */
    @Override
    public XSValue getSchemaValue() {
        return fValue;
    }

    /**
     * Reset() should be called in validator startElement(..) method.
     */
    public void reset() {
        fDeclaration = null;
        fTypeDecl = null;
        fNil = false;
        fSpecified = false;
        fNotation = null;
        fValidationAttempted = ItemPSVI.VALIDATION_NONE;
        fValidity = ItemPSVI.VALIDITY_NOTKNOWN;
        fErrors = null;
        fValidationContext = null;
        fValue.reset();
    }

    public void copySchemaInformationTo(ElementPSVImpl target) {
        target.fGrammars = fGrammars;
        target.fSchemaInformation = fSchemaInformation;
    }
}

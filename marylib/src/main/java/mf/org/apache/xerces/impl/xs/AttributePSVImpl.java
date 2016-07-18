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
import mf.org.apache.xerces.xs.AttributePSVI;
import mf.org.apache.xerces.xs.ItemPSVI;
import mf.org.apache.xerces.xs.ShortList;
import mf.org.apache.xerces.xs.StringList;
import mf.org.apache.xerces.xs.XSAttributeDeclaration;
import mf.org.apache.xerces.xs.XSSimpleTypeDefinition;
import mf.org.apache.xerces.xs.XSTypeDefinition;
import mf.org.apache.xerces.xs.XSValue;


/**
 * Attribute PSV infoset augmentations implementation.
 * The PSVI information for attributes will be available at the startElement call.
 *
 * @author Elena Litani IBM
 * @version $Id: AttributePSVImpl.java 1024038 2010-10-18 22:06:35Z sandygao $
 * @xerces.internal
 */
public class AttributePSVImpl implements AttributePSVI {

    /**
     * attribute declaration
     */
    protected XSAttributeDeclaration fDeclaration = null;

    /**
     * type of attribute, simpleType
     */
    protected XSTypeDefinition fTypeDecl = null;

    /**
     * If this attribute was explicitly given a
     * value in the original document, this is false; otherwise, it is true
     */
    protected boolean fSpecified = false;

    /**
     * Schema value
     */
    protected ValidatedInfo fValue = new ValidatedInfo();

    /**
     * validation attempted: none, partial, full
     */
    protected short fValidationAttempted = AttributePSVI.VALIDATION_NONE;

    /**
     * validity: valid, invalid, unknown
     */
    protected short fValidity = AttributePSVI.VALIDITY_NOTKNOWN;

    /**
     * error codes and error messages
     */
    protected String[] fErrors = null;

    /**
     * validation context: could be QName or XPath expression
     */
    protected String fValidationContext = null;

    //
    // AttributePSVI methods
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
     * @return list of error codes
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
     * An item isomorphic to the attribute declaration used to validate
     * this attribute.
     *
     * @return an attribute declaration
     */
    @Override
    public XSAttributeDeclaration getAttributeDeclaration() {
        return fDeclaration;
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
     * Reset()
     */
    public void reset() {
        fValue.reset();
        fDeclaration = null;
        fTypeDecl = null;
        fSpecified = false;
        fValidationAttempted = ItemPSVI.VALIDATION_NONE;
        fValidity = ItemPSVI.VALIDITY_NOTKNOWN;
        fErrors = null;
        fValidationContext = null;
    }
}

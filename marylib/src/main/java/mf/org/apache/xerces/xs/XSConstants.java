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

package mf.org.apache.xerces.xs;

/**
 * This interface defines constants used by this specification.
 */
public interface XSConstants {
    // XML Schema Components
    /**
     * The object describes an attribute declaration.
     */
    short ATTRIBUTE_DECLARATION = 1;
    /**
     * The object describes an element declaration.
     */
    short ELEMENT_DECLARATION = 2;
    /**
     * The object describes a complex type or simple type definition.
     */
    short TYPE_DEFINITION = 3;
    /**
     * The object describes an attribute use definition.
     */
    short ATTRIBUTE_USE = 4;
    /**
     * The object describes an attribute group definition.
     */
    short ATTRIBUTE_GROUP = 5;
    /**
     * The object describes a model group definition.
     */
    short MODEL_GROUP_DEFINITION = 6;
    /**
     * A model group.
     */
    short MODEL_GROUP = 7;
    /**
     * The object describes a particle.
     */
    short PARTICLE = 8;
    /**
     * The object describes a wildcard.
     */
    short WILDCARD = 9;
    /**
     * The object describes an identity constraint definition.
     */
    short IDENTITY_CONSTRAINT = 10;
    /**
     * The object describes a notation declaration.
     */
    short NOTATION_DECLARATION = 11;
    /**
     * The object describes an annotation.
     */
    short ANNOTATION = 12;
    /**
     * The object describes a constraining facet. Note: this object does not
     * describe pattern and enumeration facets.
     */
    short FACET = 13;
    /**
     * The object describes enumeration and pattern facets.
     */
    short MULTIVALUE_FACET = 14;

    // Derivation constants
    /**
     * No constraint is available.
     */
    short DERIVATION_NONE = 0;
    /**
     * <code>XSTypeDefinition</code> final set or
     * <code>XSElementDeclaration</code> disallowed substitution group.
     */
    short DERIVATION_EXTENSION = 1;
    /**
     * <code>XSTypeDefinition</code> final set or
     * <code>XSElementDeclaration</code> disallowed substitution group.
     */
    short DERIVATION_RESTRICTION = 2;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    short DERIVATION_SUBSTITUTION = 4;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    short DERIVATION_UNION = 8;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    short DERIVATION_LIST = 16;

    // Scope
    /**
     * The scope of a declaration within named model groups or attribute
     * groups is <code>absent</code>. The scope of such a declaration is
     * determined when it is used in the construction of complex type
     * definitions.
     */
    short SCOPE_ABSENT = 0;
    /**
     * A scope of <code>global</code> identifies top-level declarations.
     */
    short SCOPE_GLOBAL = 1;
    /**
     * <code>Locally scoped</code> declarations are available for use only
     * within the complex type.
     */
    short SCOPE_LOCAL = 2;

    // Value Constraint
    /**
     * Indicates that the component does not have any value constraint.
     */
    short VC_NONE = 0;
    /**
     * Indicates that there is a default value constraint.
     */
    short VC_DEFAULT = 1;
    /**
     * Indicates that there is a fixed value constraint for this attribute.
     */
    short VC_FIXED = 2;

    // Built-in types: primitive and derived
    /**
     * anySimpleType
     */
    short ANYSIMPLETYPE_DT = 1;
    /**
     * string
     */
    short STRING_DT = 2;
    /**
     * boolean
     */
    short BOOLEAN_DT = 3;
    /**
     * decimal
     */
    short DECIMAL_DT = 4;
    /**
     * float
     */
    short FLOAT_DT = 5;
    /**
     * double
     */
    short DOUBLE_DT = 6;
    /**
     * duration
     */
    short DURATION_DT = 7;
    /**
     * dateTime
     */
    short DATETIME_DT = 8;
    /**
     * time
     */
    short TIME_DT = 9;
    /**
     * date
     */
    short DATE_DT = 10;
    /**
     * gYearMonth
     */
    short GYEARMONTH_DT = 11;
    /**
     * gYear
     */
    short GYEAR_DT = 12;
    /**
     * gMonthDay
     */
    short GMONTHDAY_DT = 13;
    /**
     * gDay
     */
    short GDAY_DT = 14;
    /**
     * gMonth
     */
    short GMONTH_DT = 15;
    /**
     * hexBinary
     */
    short HEXBINARY_DT = 16;
    /**
     * base64Binary
     */
    short BASE64BINARY_DT = 17;
    /**
     * anyURI
     */
    short ANYURI_DT = 18;
    /**
     * QName
     */
    short QNAME_DT = 19;
    /**
     * NOTATION
     */
    short NOTATION_DT = 20;
    /**
     * normalizedString
     */
    short NORMALIZEDSTRING_DT = 21;
    /**
     * token
     */
    short TOKEN_DT = 22;
    /**
     * language
     */
    short LANGUAGE_DT = 23;
    /**
     * NMTOKEN
     */
    short NMTOKEN_DT = 24;
    /**
     * Name
     */
    short NAME_DT = 25;
    /**
     * NCName
     */
    short NCNAME_DT = 26;
    /**
     * ID
     */
    short ID_DT = 27;
    /**
     * IDREF
     */
    short IDREF_DT = 28;
    /**
     * ENTITY
     */
    short ENTITY_DT = 29;
    /**
     * integer
     */
    short INTEGER_DT = 30;
    /**
     * nonPositiveInteger
     */
    short NONPOSITIVEINTEGER_DT = 31;
    /**
     * negativeInteger
     */
    short NEGATIVEINTEGER_DT = 32;
    /**
     * long
     */
    short LONG_DT = 33;
    /**
     * int
     */
    short INT_DT = 34;
    /**
     * short
     */
    short SHORT_DT = 35;
    /**
     * byte
     */
    short BYTE_DT = 36;
    /**
     * nonNegativeInteger
     */
    short NONNEGATIVEINTEGER_DT = 37;
    /**
     * unsignedLong
     */
    short UNSIGNEDLONG_DT = 38;
    /**
     * unsignedInt
     */
    short UNSIGNEDINT_DT = 39;
    /**
     * unsignedShort
     */
    short UNSIGNEDSHORT_DT = 40;
    /**
     * unsignedByte
     */
    short UNSIGNEDBYTE_DT = 41;
    /**
     * positiveInteger
     */
    short POSITIVEINTEGER_DT = 42;
    /**
     * The type represents a list type definition whose item type (itemType)
     * is a union type definition
     */
    short LISTOFUNION_DT = 43;
    /**
     * The type represents a list type definition.
     */
    short LIST_DT = 44;
    /**
     * The built-in type category is not available.
     */
    short UNAVAILABLE_DT = 45;

}

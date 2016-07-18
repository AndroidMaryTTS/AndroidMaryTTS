/*
 * Copyright (c) 2001 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package mf.org.apache.xerces.dom3.as;

/**
 * @deprecated An attribute declaration in the context of a <code>ASObject</code>.The
 * constant 'REQUIRED' is missing from this interface.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ASAttributeDeclaration extends ASObject {
    // VALUE_TYPES
    /**
     * Describes that the attribute does not have any value constraint.
     */
    short VALUE_NONE = 0;
    /**
     * Indicates that the there is a default value constraint.
     */
    short VALUE_DEFAULT = 1;
    /**
     * Indicates that there is a fixed value constraint for this attribute.
     */
    short VALUE_FIXED = 2;

    /**
     * Datatype of the attribute.
     */
    ASDataType getDataType();

    /**
     * Datatype of the attribute.
     */
    void setDataType(ASDataType dataType);

    /**
     * Default or fixed value.
     */
    String getDataValue();

    /**
     * Default or fixed value.
     */
    void setDataValue(String dataValue);

    /**
     * Valid attribute values, separated by commas, in a string.
     */
    String getEnumAttr();

    /**
     * Valid attribute values, separated by commas, in a string.
     */
    void setEnumAttr(String enumAttr);

    /**
     * Owner elements <code>ASObject</code> of attribute, meaning that an
     * attribute declaration can be shared by multiple elements.
     */
    ASObjectList getOwnerElements();

    /**
     * Owner elements <code>ASObject</code> of attribute, meaning that an
     * attribute declaration can be shared by multiple elements.
     */
    void setOwnerElements(ASObjectList ownerElements);

    /**
     * Constraint type if any for this attribute.
     */
    short getDefaultType();

    /**
     * Constraint type if any for this attribute.
     */
    void setDefaultType(short defaultType);

}

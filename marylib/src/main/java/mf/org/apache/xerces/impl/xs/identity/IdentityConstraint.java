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

package mf.org.apache.xerces.impl.xs.identity;

import mf.org.apache.xerces.impl.xs.XSAnnotationImpl;
import mf.org.apache.xerces.impl.xs.util.StringListImpl;
import mf.org.apache.xerces.impl.xs.util.XSObjectListImpl;
import mf.org.apache.xerces.xs.StringList;
import mf.org.apache.xerces.xs.XSConstants;
import mf.org.apache.xerces.xs.XSIDCDefinition;
import mf.org.apache.xerces.xs.XSNamespaceItem;
import mf.org.apache.xerces.xs.XSObjectList;


/**
 * Base class of Schema identity constraint.
 *
 * @author Andy Clark, IBM
 * @version $Id: IdentityConstraint.java 699892 2008-09-28 21:08:27Z mrglavas $
 * @xerces.internal
 */
public abstract class IdentityConstraint implements XSIDCDefinition {

    //
    // Data
    //

    /**
     * target namespace
     */
    protected final String fNamespace;
    /**
     * Identity constraint name.
     */
    protected final String fIdentityConstraintName;
    /**
     * name of owning element
     */
    protected final String fElementName;
    /**
     * type
     */
    protected short type;
    /**
     * Selector.
     */
    protected Selector fSelector;

    /**
     * Field count.
     */
    protected int fFieldCount;

    /**
     * Fields.
     */
    protected Field[] fFields;

    // optional annotations
    protected XSAnnotationImpl[] fAnnotations = null;

    // number of annotations in this identity constraint
    protected int fNumAnnotations;

    //
    // Constructors
    //

    /**
     * Default constructor.
     */
    protected IdentityConstraint(String namespace, String identityConstraintName, String elemName) {
        fNamespace = namespace;
        fIdentityConstraintName = identityConstraintName;
        fElementName = elemName;
    } // <init>(String,String)

    //
    // Public methods
    //

    static final Field[] resize(Field[] oldArray, int newSize) {
        Field[] newArray = new Field[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
        return newArray;
    }

    /**
     * Returns the identity constraint name.
     */
    public String getIdentityConstraintName() {
        return fIdentityConstraintName;
    } // getIdentityConstraintName():String

    /**
     * Returns the selector.
     */
    public Selector getSelector() {
        return fSelector;
    } // getSelector():Selector

    /**
     * Sets the selector.
     */
    public void setSelector(Selector selector) {
        fSelector = selector;
    } // setSelector(Selector)

    /**
     * Adds a field.
     */
    public void addField(Field field) {
        if (fFields == null)
            fFields = new Field[4];
        else if (fFieldCount == fFields.length)
            fFields = resize(fFields, fFieldCount * 2);
        fFields[fFieldCount++] = field;
    } // addField(Field)

    /**
     * Returns the field count.
     */
    public int getFieldCount() {
        return fFieldCount;
    } // getFieldCount():int

    /**
     * Returns the field at the specified index.
     */
    public Field getFieldAt(int index) {
        return fFields[index];
    } // getFieldAt(int):Field

    //
    // Object methods
    //

    // get the name of the owning element
    public String getElementName() {
        return fElementName;
    } // getElementName(): String

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
        String s = super.toString();
        int index1 = s.lastIndexOf('$');
        if (index1 != -1) {
            return s.substring(index1 + 1);
        }
        int index2 = s.lastIndexOf('.');
        if (index2 != -1) {
            return s.substring(index2 + 1);
        }
        return s;
    } // toString():String

    // equals:  returns true if and only if the String
    // representations of all members of both objects (except for
    // the elenemtName field) are equal.
    public boolean equals(IdentityConstraint id) {
        boolean areEqual = fIdentityConstraintName.equals(id.fIdentityConstraintName);
        if (!areEqual) return false;
        areEqual = fSelector.toString().equals(id.fSelector.toString());
        if (!areEqual) return false;
        areEqual = (fFieldCount == id.fFieldCount);
        if (!areEqual) return false;
        for (int i = 0; i < fFieldCount; i++)
            if (!fFields[i].toString().equals(id.fFields[i].toString())) return false;
        return true;
    } // equals

    /**
     * Get the type of the object, i.e ELEMENT_DECLARATION.
     */
    @Override
    public short getType() {
        return XSConstants.IDENTITY_CONSTRAINT;
    }

    /**
     * The <code>name</code> of this <code>XSObject</code> depending on the
     * <code>XSObject</code> type.
     */
    @Override
    public String getName() {
        return fIdentityConstraintName;
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  defines how a namespace URI is attached to schema
     * components.
     */
    @Override
    public String getNamespace() {
        return fNamespace;
    }

    /**
     * {identity-constraint category} One of key, keyref or unique.
     */
    @Override
    public short getCategory() {
        return type;
    }

    /**
     * {selector} A restricted XPath ([XPath]) expression
     */
    @Override
    public String getSelectorStr() {
        return (fSelector != null) ? fSelector.toString() : null;
    }

    /**
     * {fields} A non-empty list of restricted XPath ([XPath]) expressions.
     */
    @Override
    public StringList getFieldStrs() {
        String[] strs = new String[fFieldCount];
        for (int i = 0; i < fFieldCount; i++)
            strs[i] = fFields[i].toString();
        return new StringListImpl(strs, fFieldCount);
    }

    /**
     * {referenced key} Required if {identity-constraint category} is keyref,
     * forbidden otherwise. An identity-constraint definition with
     * {identity-constraint category} equal to key or unique.
     */
    @Override
    public XSIDCDefinition getRefKey() {
        return null;
    }

    /**
     * Optional. Annotation.
     */
    @Override
    public XSObjectList getAnnotations() {
        return new XSObjectListImpl(fAnnotations, fNumAnnotations);
    }

    /**
     * @see mf.org.apache.xerces.xs.XSObject#getNamespaceItem()
     */
    @Override
    public XSNamespaceItem getNamespaceItem() {
        // REVISIT: implement
        return null;
    }

    public void addAnnotation(XSAnnotationImpl annotation) {
        if (annotation == null)
            return;
        if (fAnnotations == null) {
            fAnnotations = new XSAnnotationImpl[2];
        } else if (fNumAnnotations == fAnnotations.length) {
            XSAnnotationImpl[] newArray = new XSAnnotationImpl[fNumAnnotations << 1];
            System.arraycopy(fAnnotations, 0, newArray, 0, fNumAnnotations);
            fAnnotations = newArray;
        }
        fAnnotations[fNumAnnotations++] = annotation;
    }

} // class IdentityConstraint

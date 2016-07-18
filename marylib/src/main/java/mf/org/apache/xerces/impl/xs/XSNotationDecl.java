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

import mf.org.apache.xerces.impl.xs.util.XSObjectListImpl;
import mf.org.apache.xerces.xs.XSAnnotation;
import mf.org.apache.xerces.xs.XSConstants;
import mf.org.apache.xerces.xs.XSNamespaceItem;
import mf.org.apache.xerces.xs.XSNotationDeclaration;
import mf.org.apache.xerces.xs.XSObjectList;


/**
 * The XML representation for a NOTATION declaration
 * schema component is a global &lt;notation&gt; element information item
 *
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @version $Id: XSNotationDecl.java 658446 2008-05-20 21:37:22Z mrglavas $
 * @xerces.internal
 */
public class XSNotationDecl implements XSNotationDeclaration {

    // name of the group
    public String fName = null;
    // target namespace of the group
    public String fTargetNamespace = null;
    // public id of the notation
    public String fPublicId = null;
    // system id of the notation
    public String fSystemId = null;

    // optional annotation
    public XSObjectList fAnnotations = null;

    // The namespace schema information item corresponding to the target namespace 
    // of the notation declaration, if it is globally declared; or null otherwise.
    private XSNamespaceItem fNamespaceItem = null;

    /**
     * Get the type of the object, i.e ELEMENT_DECLARATION.
     */
    @Override
    public short getType() {
        return XSConstants.NOTATION_DECLARATION;
    }

    /**
     * The <code>name</code> of this <code>XSObject</code> depending on the
     * <code>XSObject</code> type.
     */
    @Override
    public String getName() {
        return fName;
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.  defines how a namespace URI is attached to schema
     * components.
     */
    @Override
    public String getNamespace() {
        return fTargetNamespace;
    }

    /**
     * Optional if {public identifier} is present. A URI reference.
     */
    @Override
    public String getSystemId() {
        return fSystemId;
    }

    /**
     * Optional if {system identifier} is present. A public identifier,
     * as defined in [XML 1.0 (Second Edition)].
     */
    @Override
    public String getPublicId() {
        return fPublicId;
    }

    /**
     * Optional. Annotation.
     */
    @Override
    public XSAnnotation getAnnotation() {
        return (fAnnotations != null) ? (XSAnnotation) fAnnotations.item(0) : null;
    }

    /**
     * Optional. Annotations.
     */
    @Override
    public XSObjectList getAnnotations() {
        return (fAnnotations != null) ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    /**
     * @see mf.org.apache.xerces.xs.XSObject#getNamespaceItem()
     */
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return fNamespaceItem;
    }

    void setNamespaceItem(XSNamespaceItem namespaceItem) {
        fNamespaceItem = namespaceItem;
    }

} // class XSNotationDecl

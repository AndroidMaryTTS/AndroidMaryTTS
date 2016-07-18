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

package mf.org.apache.xerces.impl.xs.traversers;

import mf.org.apache.xerces.impl.xs.SchemaGrammar;
import mf.org.apache.xerces.impl.xs.SchemaSymbols;
import mf.org.apache.xerces.impl.xs.XSElementDecl;
import mf.org.apache.xerces.impl.xs.identity.IdentityConstraint;
import mf.org.apache.xerces.impl.xs.identity.UniqueOrKey;
import mf.org.apache.xerces.util.DOMUtil;
import mf.org.apache.xerces.xs.XSIDCDefinition;
import mf.org.w3c.dom.Element;


/**
 * This class contains code that is used to traverse both <key>s and
 * <unique>s.
 *
 * @author Neil Graham, IBM
 * @version $Id: XSDUniqueOrKeyTraverser.java 819653 2009-09-28 17:29:56Z knoaman $
 * @xerces.internal
 */
class XSDUniqueOrKeyTraverser extends XSDAbstractIDConstraintTraverser {

    public XSDUniqueOrKeyTraverser(XSDHandler handler,
                                   XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }


    void traverse(Element uElem, XSElementDecl element,
                  XSDocumentInfo schemaDoc, SchemaGrammar grammar) {

        // General Attribute Checking
        Object[] attrValues = fAttrChecker.checkAttributes(uElem, false, schemaDoc);

        // create identity constraint
        String uName = (String) attrValues[XSAttributeChecker.ATTIDX_NAME];

        if (uName == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{DOMUtil.getLocalName(uElem), SchemaSymbols.ATT_NAME}, uElem);
            //return this array back to pool
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }

        UniqueOrKey uniqueOrKey = null;
        if (DOMUtil.getLocalName(uElem).equals(SchemaSymbols.ELT_UNIQUE)) {
            uniqueOrKey = new UniqueOrKey(schemaDoc.fTargetNamespace, uName, element.fName, XSIDCDefinition.IC_UNIQUE);
        } else {
            uniqueOrKey = new UniqueOrKey(schemaDoc.fTargetNamespace, uName, element.fName, XSIDCDefinition.IC_KEY);
        }
        // it's XSDElementTraverser's job to ensure that there's no
        // duplication (or if there is that restriction is involved
        // and there's identity).

        // If errors occurred in traversing the identity constraint, then don't
        // add it to the schema, to avoid errors when processing the instance.
        if (traverseIdentityConstraint(uniqueOrKey, uElem, schemaDoc, attrValues)) {
            // and stuff this in the grammar
            if (grammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName()) == null) {
                grammar.addIDConstraintDecl(element, uniqueOrKey);
            }

            final String loc = fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            final IdentityConstraint idc = grammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName(), loc);
            if (idc == null) {
                grammar.addIDConstraintDecl(element, uniqueOrKey, loc);
            }

            // handle duplicates
            if (fSchemaHandler.fTolerateDuplicates) {
                if (idc != null) {
                    if (idc instanceof UniqueOrKey) {
                        uniqueOrKey = uniqueOrKey;
                    }
                }
                fSchemaHandler.addIDConstraintDecl(uniqueOrKey);
            }
        }

        // and fix up attributeChecker
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
    } // traverse(Element,XSDElementDecl,XSDocumentInfo, SchemaGrammar)
} // XSDUniqueOrKeyTraverser

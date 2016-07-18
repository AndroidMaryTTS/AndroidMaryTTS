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

package mf.org.apache.xerces.impl.xs.models;

import mf.org.apache.xerces.impl.dtd.models.CMNode;
import mf.org.apache.xerces.impl.dtd.models.CMStateSet;
import mf.org.apache.xerces.impl.xs.XSModelGroupImpl;

/**
 * Content model Bin-Op node.
 *
 * @author Neil Graham, IBM
 * @version $Id: XSCMBinOp.java 476309 2006-11-17 20:49:31Z mrglavas $
 * @xerces.internal
 */
public class XSCMBinOp extends CMNode {
    // -------------------------------------------------------------------
    //  Private data members
    //
    //  fLeftChild
    //  fRightChild
    //      These are the references to the two nodes that are on either
    //      side of this binary operation.
    // -------------------------------------------------------------------
    private CMNode fLeftChild;
    private CMNode fRightChild;

    // -------------------------------------------------------------------
    //  Constructors
    // -------------------------------------------------------------------
    public XSCMBinOp(int type, CMNode leftNode, CMNode rightNode) {
        super(type);

        // Insure that its one of the types we require
        if ((type() != XSModelGroupImpl.MODELGROUP_CHOICE)
                && (type() != XSModelGroupImpl.MODELGROUP_SEQUENCE)) {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }

        // Store the nodes and init any data that needs it
        fLeftChild = leftNode;
        fRightChild = rightNode;
    }

    // -------------------------------------------------------------------
    //  Package, final methods
    // -------------------------------------------------------------------
    final CMNode getLeft() {
        return fLeftChild;
    }

    final CMNode getRight() {
        return fRightChild;
    }

    // -------------------------------------------------------------------
    //  Package, inherited methods
    // -------------------------------------------------------------------
    @Override
    public boolean isNullable() {
        //
        //  If its an alternation, then if either child is nullable then
        //  this node is nullable. If its a concatenation, then both of
        //  them have to be nullable.
        //
        if (type() == XSModelGroupImpl.MODELGROUP_CHOICE)
            return (fLeftChild.isNullable() || fRightChild.isNullable());
        else if (type() == XSModelGroupImpl.MODELGROUP_SEQUENCE)
            return (fLeftChild.isNullable() && fRightChild.isNullable());
        else
            throw new RuntimeException("ImplementationMessages.VAL_BST");
    }

    // -------------------------------------------------------------------
    //  Protected, inherited methods
    // -------------------------------------------------------------------
    @Override
    protected void calcFirstPos(CMStateSet toSet) {
        if (type() == XSModelGroupImpl.MODELGROUP_CHOICE) {
            // Its the the union of the first positions of our children.
            toSet.setTo(fLeftChild.firstPos());
            toSet.union(fRightChild.firstPos());
        } else if (type() == XSModelGroupImpl.MODELGROUP_SEQUENCE) {
            //
            //  If our left child is nullable, then its the union of our
            //  children's first positions. Else is our left child's first
            //  positions.
            //
            toSet.setTo(fLeftChild.firstPos());
            if (fLeftChild.isNullable())
                toSet.union(fRightChild.firstPos());
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
    }

    @Override
    protected void calcLastPos(CMStateSet toSet) {
        if (type() == XSModelGroupImpl.MODELGROUP_CHOICE) {
            // Its the the union of the first positions of our children.
            toSet.setTo(fLeftChild.lastPos());
            toSet.union(fRightChild.lastPos());
        } else if (type() == XSModelGroupImpl.MODELGROUP_SEQUENCE) {
            //
            //  If our right child is nullable, then its the union of our
            //  children's last positions. Else is our right child's last
            //  positions.
            //
            toSet.setTo(fRightChild.lastPos());
            if (fRightChild.isNullable())
                toSet.union(fLeftChild.lastPos());
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
    }
} // XSCMBinOp


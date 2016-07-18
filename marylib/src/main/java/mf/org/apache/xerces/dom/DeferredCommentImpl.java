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

package mf.org.apache.xerces.dom;

/**
 * Represents an XML (or HTML) comment.
 *
 * @version $Id: DeferredCommentImpl.java 447266 2006-09-18 05:57:49Z mrglavas $
 * @xerces.internal
 * @since PR-DOM-Level-1-19980818.
 */
public class DeferredCommentImpl
        extends CommentImpl
        implements DeferredNode {

    //
    // Constants
    //

    /**
     * Serialization version.
     */
    static final long serialVersionUID = 6498796371083589338L;

    //
    // Data
    //

    /**
     * Node index.
     */
    protected transient int fNodeIndex;

    //
    // Constructors
    //

    /**
     * This is the deferred constructor. Only the fNodeIndex is given here. All other data,
     * can be requested from the ownerDocument via the index.
     */
    DeferredCommentImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
        super(ownerDocument, null);

        fNodeIndex = nodeIndex;
        needsSyncData(true);

    } // <init>(DeferredDocumentImpl,int)

    //
    // DeferredNode methods
    //

    /**
     * Returns the node index.
     */
    @Override
    public int getNodeIndex() {
        return fNodeIndex;
    }

    //
    // Protected methods
    //

    /**
     * Synchronizes the data (name and value) for fast nodes.
     */
    @Override
    protected void synchronizeData() {

        // no need to sync in the future
        needsSyncData(false);

        // fluff data
        DeferredDocumentImpl ownerDocument =
                (DeferredDocumentImpl) this.ownerDocument();
        data = ownerDocument.getNodeValueString(fNodeIndex);

    } // synchronizeData()

} // class DeferredCommentImpl

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
 * @deprecated This interface allows creation of an <code>ASModel</code>. The expectation
 * is that an instance of the <code>DOMImplementationAS</code> interface can
 * be obtained by using binding-specific casting methods on an instance of
 * the <code>DOMImplementation</code> interface when the DOM implementation
 * supports the feature "<code>AS-EDIT</code>".
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface DOMImplementationAS {
    /**
     * Creates an ASModel.
     *
     * @param isNamespaceAware Allow creation of <code>ASModel</code> with
     *                         this attribute set to a specific value.
     * @return A <code>null</code> return indicates failure.what is a
     * failure? Could be a system error.
     */
    ASModel createAS(boolean isNamespaceAware);

    /**
     * Creates an <code>DOMASBuilder</code>.Do we need the method since we
     * already have <code>DOMImplementationLS.createDOMParser</code>?
     *
     * @return a DOMASBuilder
     */
    DOMASBuilder createDOMASBuilder();

    /**
     * Creates an <code>DOMASWriter</code>.
     *
     * @return a DOMASWriter
     */
    DOMASWriter createDOMASWriter();

}

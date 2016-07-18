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
 * @deprecated This interface represents a notation declaration.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface ASNotationDeclaration extends ASObject {
    /**
     * the URI reference representing the system identifier for the notation
     * declaration, if present, <code>null</code> otherwise.
     */
    String getSystemId();

    /**
     * the URI reference representing the system identifier for the notation
     * declaration, if present, <code>null</code> otherwise.
     */
    void setSystemId(String systemId);

    /**
     * The string representing the public identifier for this notation
     * declaration, if present; <code>null</code> otherwise.
     */
    String getPublicId();

    /**
     * The string representing the public identifier for this notation
     * declaration, if present; <code>null</code> otherwise.
     */
    void setPublicId(String publicId);

}

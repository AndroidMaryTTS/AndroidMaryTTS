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
 * @deprecated Abstract Schemas operations may throw a <code>DOMSystemException</code> as
 * described in their descriptions.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public class DOMASException extends RuntimeException {
    /**
     * If an element declaration already exists with the same name within an
     * <code>AS_CHOICE</code> operator.
     */
    public static final short DUPLICATE_NAME_ERR = 1;
    /**
     * If the type of the <code>ASObject</code> is neither an
     * <code>ASContentModel</code> nor an <code>ASElementDeclaration</code>.
     */
    public static final short TYPE_ERR = 2;
    // ASExceptionCode
    /**
     * If the <code>DocumentEditAS</code> related to the node does not have
     * any active <code>ASModel</code> and <code>wfValidityCheckLevel</code>
     * is set to <code>PARTIAL</code> or <code>STRICT_VALIDITY_CHECK</code>.
     */
    public static final short NO_AS_AVAILABLE = 3;
    /**
     * When <code>mimeTypeCheck</code> is <code>true</code> and the input
     * source has an incorrect MIME Type. See the attribute
     * <code>mimeTypeCheck</code>.
     */
    public static final short WRONG_MIME_TYPE_ERR = 4;
    public short code;

    public DOMASException(short code, String message) {
        super(message);
        this.code = code;
    }

}

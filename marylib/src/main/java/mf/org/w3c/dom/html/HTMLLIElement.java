/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * List item. See the LI element definition in HTML 4.0.
 */
public interface HTMLLIElement extends HTMLElement {
    /**
     * List item bullet style. See the type attribute definition in HTML 4.0.
     * This attribute is deprecated in HTML 4.0.
     */
    String getType();

    void setType(String type);

    /**
     * Reset sequence number when used in <code>OL</code> See the value
     * attribute definition in HTML 4.0. This attribute is deprecated in HTML
     * 4.0.
     */
    int getValue();

    void setValue(int value);
}


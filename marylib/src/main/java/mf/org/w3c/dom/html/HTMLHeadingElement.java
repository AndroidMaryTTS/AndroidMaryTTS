/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * For the <code>H1</code> to <code>H6</code> elements. See the H1 element
 * definition in HTML 4.0.
 */
public interface HTMLHeadingElement extends HTMLElement {
    /**
     * Horizontal text alignment. See the align attribute definition in HTML
     * 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);
}


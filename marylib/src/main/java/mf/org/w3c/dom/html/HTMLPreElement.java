/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Preformatted text. See the PRE element definition in HTML 4.0.
 */
public interface HTMLPreElement extends HTMLElement {
    /**
     * Fixed width for content. See the width attribute definition in HTML 4.0.
     * This attribute is deprecated in HTML 4.0.
     */
    int getWidth();

    void setWidth(int width);
}


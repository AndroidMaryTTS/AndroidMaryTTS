/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Definition list. See the DL element definition in HTML 4.0.
 */
public interface HTMLDListElement extends HTMLElement {
    /**
     * Reduce spacing between list items. See the compact attribute definition
     * in HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    boolean getCompact();

    void setCompact(boolean compact);
}


/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Root of an HTML document. See the HTML element definition in HTML 4.0.
 */
public interface HTMLHtmlElement extends HTMLElement {
    /**
     * Version information about the document's DTD. See the version attribute
     * definition in HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getVersion();

    void setVersion(String version);
}


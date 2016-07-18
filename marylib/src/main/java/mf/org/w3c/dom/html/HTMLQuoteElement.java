/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * For the <code>Q</code> and <code>BLOCKQUOTE</code> elements. See the Q
 * element definition in HTML 4.0.
 */
public interface HTMLQuoteElement extends HTMLElement {
    /**
     * A URI designating a document that designates a source document or
     * message. See the cite attribute definition in HTML 4.0.
     */
    String getCite();

    void setCite(String cite);
}


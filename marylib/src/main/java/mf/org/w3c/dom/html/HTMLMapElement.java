/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Client-side image map. See the MAP element definition in HTML 4.0.
 */
public interface HTMLMapElement extends HTMLElement {
    /**
     * The list of areas defined for the image map.
     */
    HTMLCollection getAreas();

    /**
     * Names the map (for use with <code>usemap</code>). See the name attribute
     * definition in HTML 4.0.
     */
    String getName();

    void setName(String name);
}


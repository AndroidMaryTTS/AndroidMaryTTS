/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Group options together in logical subdivisions. See the OPTGROUP element
 * definition in HTML 4.0.
 */
public interface HTMLOptGroupElement extends HTMLElement {
    /**
     * The control is unavailable in this context. See the disabled attribute
     * definition in HTML 4.0.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * Assigns a label to this option group. See the label attribute definition
     * in HTML 4.0.
     */
    String getLabel();

    void setLabel(String label);
}


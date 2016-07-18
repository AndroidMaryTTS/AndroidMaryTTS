/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Create a horizontal rule. See the HR element definition in HTML 4.0.
 */
public interface HTMLHRElement extends HTMLElement {
    /**
     * Align the rule on the page. See the align attribute definition in HTML
     * 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Indicates to the user agent that there should be no shading in the
     * rendering of this element. See the noshade attribute definition in HTML
     * 4.0. This attribute is deprecated in HTML 4.0.
     */
    boolean getNoShade();

    void setNoShade(boolean noShade);

    /**
     * The height of the rule. See the size attribute definition in HTML 4.0.
     * This attribute is deprecated in HTML 4.0.
     */
    String getSize();

    void setSize(String size);

    /**
     * The width of the rule. See the width attribute definition in HTML 4.0.
     * This attribute is deprecated in HTML 4.0.
     */
    String getWidth();

    void setWidth(String width);
}


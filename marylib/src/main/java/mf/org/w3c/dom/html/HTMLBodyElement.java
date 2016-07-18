/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * The HTML document body. This element is always present in the DOM API,even
 * if the tags are not present in the source document. See the BODY element
 * definition in HTML 4.0.
 */
public interface HTMLBodyElement extends HTMLElement {
    /**
     * Color of active links (after mouse-button down, but beforemouse-button
     * up). See the alink attribute definition in HTML 4.0. This attribute is
     * deprecated in HTML 4.0.
     */
    String getALink();

    void setALink(String aLink);

    /**
     * URI of the background texture tile image. See the background attribute
     * definition in HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getBackground();

    void setBackground(String background);

    /**
     * Document background color. See the bgcolor attribute definition in HTML
     * 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getBgColor();

    void setBgColor(String bgColor);

    /**
     * Color of links that are not active and unvisited. See the link attribute
     * definition in HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getLink();

    void setLink(String link);

    /**
     * Document text color. See the text attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getText();

    void setText(String text);

    /**
     * Color of links that have been visited by the user. See the vlink
     * attribute definition in HTML 4.0. This attribute is deprecated in HTML
     * 4.0.
     */
    String getVLink();

    void setVLink(String vLink);
}


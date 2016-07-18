/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Inline subwindows. See the IFRAME element definition in HTML 4.0.
 */
public interface HTMLIFrameElement extends HTMLElement {
    /**
     * Aligns this object (vertically or horizontally) with respect to its
     * surrounding text. See the align attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Request frame borders. See the frameborder attribute definition in HTML
     * 4.0.
     */
    String getFrameBorder();

    void setFrameBorder(String frameBorder);

    /**
     * Frame height. See the height attribute definition in HTML 4.0.
     */
    String getHeight();

    void setHeight(String height);

    /**
     * URI designating a long description of this image or frame. See the
     * longdesc attribute definition in HTML 4.0.
     */
    String getLongDesc();

    void setLongDesc(String longDesc);

    /**
     * Frame margin height, in pixels. See the marginheight attribute definition
     * in HTML 4.0.
     */
    String getMarginHeight();

    void setMarginHeight(String marginHeight);

    /**
     * Frame margin width, in pixels. See the marginwidth attribute definition
     * in HTML 4.0.
     */
    String getMarginWidth();

    void setMarginWidth(String marginWidth);

    /**
     * The frame name (object of the <code>target</code> attribute). See the
     * name attribute definition in HTML 4.0.
     */
    String getName();

    void setName(String name);

    /**
     * Specify whether or not the frame should have scrollbars. See the
     * scrolling attribute definition in HTML 4.0.
     */
    String getScrolling();

    void setScrolling(String scrolling);

    /**
     * A URI designating the initial frame contents. See the src attribute
     * definition in HTML 4.0.
     */
    String getSrc();

    void setSrc(String src);

    /**
     * Frame width. See the width attribute definition in HTML 4.0.
     */
    String getWidth();

    void setWidth(String width);
}


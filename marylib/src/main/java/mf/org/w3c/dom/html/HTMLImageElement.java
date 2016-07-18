/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Embedded image. See the IMG element definition in HTML 4.0.
 */
public interface HTMLImageElement extends HTMLElement {
    /**
     * URI designating the source of this image, for low-resolution output.
     */
    String getLowSrc();

    void setLowSrc(String lowSrc);

    /**
     * The name of the element (for backwards compatibility).
     */
    String getName();

    void setName(String name);

    /**
     * Aligns this object (vertically or horizontally) with respect to its
     * surrounding text. See the align attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Alternate text for user agents not rendering the normal contentof this
     * element. See the alt attribute definition in HTML 4.0.
     */
    String getAlt();

    void setAlt(String alt);

    /**
     * Width of border around image. See the border attribute definition in HTML
     * 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getBorder();

    void setBorder(String border);

    /**
     * Override height. See the height attribute definition in HTML 4.0.
     */
    String getHeight();

    void setHeight(String height);

    /**
     * Horizontal space to the left and right of this image. See the hspace
     * attribute definition in HTML 4.0. This attribute is deprecated in HTML
     * 4.0.
     */
    String getHspace();

    void setHspace(String hspace);

    /**
     * Use server-side image map. See the ismap attribute definition in HTML 4.0.
     */
    boolean getIsMap();

    void setIsMap(boolean isMap);

    /**
     * URI designating a long description of this image or frame. See the
     * longdesc attribute definition in HTML 4.0.
     */
    String getLongDesc();

    void setLongDesc(String longDesc);

    /**
     * URI designating the source of this image. See the src attribute definition
     * in HTML 4.0.
     */
    String getSrc();

    void setSrc(String src);

    /**
     * Use client-side image map. See the usemap attribute definition in HTML
     * 4.0.
     */
    String getUseMap();

    void setUseMap(String useMap);

    /**
     * Vertical space above and below this image. See the vspace attribute
     * definition in HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getVspace();

    void setVspace(String vspace);

    /**
     * Override width. See the width attribute definition in HTML 4.0.
     */
    String getWidth();

    void setWidth(String width);
}


/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Client-side image map area definition. See the AREA element definition in
 * HTML 4.0.
 */
public interface HTMLAreaElement extends HTMLElement {
    /**
     * A single character access key to give access to the form control. See the
     * accesskey attribute definition in HTML 4.0.
     */
    String getAccessKey();

    void setAccessKey(String accessKey);

    /**
     * Alternate text for user agents not rendering the normal contentof this
     * element. See the alt attribute definition in HTML 4.0.
     */
    String getAlt();

    void setAlt(String alt);

    /**
     * Comma-separated list of lengths, defining an active region geometry.See
     * also <code>shape</code> for the shape of the region. See the coords
     * attribute definition in HTML 4.0.
     */
    String getCoords();

    void setCoords(String coords);

    /**
     * The URI of the linked resource. See the href attribute definition in HTML
     * 4.0.
     */
    String getHref();

    void setHref(String href);

    /**
     * Specifies that this area is inactive, i.e., has no associated action. See
     * the nohref attribute definition in HTML 4.0.
     */
    boolean getNoHref();

    void setNoHref(boolean noHref);

    /**
     * The shape of the active area. The coordinates are givenby
     * <code>coords</code>. See the shape attribute definition in HTML 4.0.
     */
    String getShape();

    void setShape(String shape);

    /**
     * Index that represents the element's position in the tabbing order. See
     * the tabindex attribute definition in HTML 4.0.
     */
    int getTabIndex();

    void setTabIndex(int tabIndex);

    /**
     * Frame to render the resource in. See the target attribute definition in
     * HTML 4.0.
     */
    String getTarget();

    void setTarget(String target);
}


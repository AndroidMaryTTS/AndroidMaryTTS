/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * An embedded Java applet. See the APPLET element definition in HTML 4.0.
 * This element is deprecated in HTML 4.0.
 */
public interface HTMLAppletElement extends HTMLElement {
    /**
     * Aligns this object (vertically or horizontally) with respect to its
     * surrounding text. See the align attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Alternate text for user agents not rendering the normal contentof this
     * element. See the alt attribute definition in HTML 4.0. This attribute is
     * deprecated in HTML 4.0.
     */
    String getAlt();

    void setAlt(String alt);

    /**
     * Comma-separated archive list. See the archive attribute definition in
     * HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getArchive();

    void setArchive(String archive);

    /**
     * Applet class file.  See the code attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getCode();

    void setCode(String code);

    /**
     * Optional base URI for applet. See the codebase attribute definition in
     * HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getCodeBase();

    void setCodeBase(String codeBase);

    /**
     * Override height. See the height attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getHeight();

    void setHeight(String height);

    /**
     * Horizontal space to the left and right of this image, applet, or object.
     * See the hspace attribute definition in HTML 4.0. This attribute is
     * deprecated in HTML 4.0.
     */
    String getHspace();

    void setHspace(String hspace);

    /**
     * The name of the applet. See the name attribute definition in HTML 4.0.
     * This attribute is deprecated in HTML 4.0.
     */
    String getName();

    void setName(String name);

    /**
     * Serialized applet file. See the object attribute definition in HTML 4.0.
     * This attribute is deprecated in HTML 4.0.
     */
    String getObject();

    void setObject(String object);

    /**
     * Vertical space above and below this image, applet, or object. See the
     * vspace attribute definition in HTML 4.0. This attribute is deprecated in
     * HTML 4.0.
     */
    String getVspace();

    void setVspace(String vspace);

    /**
     * Override width. See the width attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getWidth();

    void setWidth(String width);
}


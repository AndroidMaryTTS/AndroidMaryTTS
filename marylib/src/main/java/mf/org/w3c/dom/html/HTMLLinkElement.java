/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * The <code>LINK</code> element specifies a link to an externalresource, and
 * defines this document's relationship to that resource(or vice versa).  See
 * the LINK element definition in HTML 4.0.
 */
public interface HTMLLinkElement extends HTMLElement {
    /**
     * Enables/disables the link. This is currently only used for style sheet
     * links, and may be used to activate or deactivate style sheets.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * The character encoding of the resource being linked to. See the charset
     * attribute definition in HTML 4.0.
     */
    String getCharset();

    void setCharset(String charset);

    /**
     * The URI of the linked resource. See the href attribute definition in HTML
     * 4.0.
     */
    String getHref();

    void setHref(String href);

    /**
     * Language code of the linked resource. See the hreflang attribute
     * definition in HTML 4.0.
     */
    String getHreflang();

    void setHreflang(String hreflang);

    /**
     * Designed for use with one or more target media. See the media attribute
     * definition in HTML 4.0.
     */
    String getMedia();

    void setMedia(String media);

    /**
     * Forward link type. See the rel attribute definition in HTML 4.0.
     */
    String getRel();

    void setRel(String rel);

    /**
     * Reverse link type. See the rev attribute definition in HTML 4.0.
     */
    String getRev();

    void setRev(String rev);

    /**
     * Frame to render the resource in. See the target attribute definition in
     * HTML 4.0.
     */
    String getTarget();

    void setTarget(String target);

    /**
     * Advisory content type. See the type attribute definition in HTML 4.0.
     */
    String getType();

    void setType(String type);
}


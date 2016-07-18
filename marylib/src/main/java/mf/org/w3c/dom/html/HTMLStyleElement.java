/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Style information. A more detailed style sheet object model is planned to be
 * defined in a separate document. See the STYLE element definition in HTML
 * 4.0.
 */
public interface HTMLStyleElement extends HTMLElement {
    /**
     * Enables/disables the style sheet.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * Designed for use with one or more target media. See the media attribute
     * definition in HTML 4.0.
     */
    String getMedia();

    void setMedia(String media);

    /**
     * The style sheet language (Internet media type). See the type attribute
     * definition in HTML 4.0.
     */
    String getType();

    void setType(String type);
}


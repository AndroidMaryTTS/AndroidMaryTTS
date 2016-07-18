/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * This element is usedfor single-line text input. See the ISINDEX element
 * definition in HTML 4.0. This element is deprecated in HTML 4.0.
 */
public interface HTMLIsIndexElement extends HTMLElement {
    /**
     * Returns the <code>FORM</code> element containing this control.Returns
     * null if this control is not within the context of a form.
     */
    HTMLFormElement getForm();

    /**
     * The prompt message. See the prompt attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getPrompt();

    void setPrompt(String prompt);
}


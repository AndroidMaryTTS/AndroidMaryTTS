/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Form field label text. See the LABEL element definition in HTML 4.0.
 */
public interface HTMLLabelElement extends HTMLElement {
    /**
     * Returns the <code>FORM</code> element containing this control.Returns
     * null if this control is not within the context of a form.
     */
    HTMLFormElement getForm();

    /**
     * A single character access key to give access to the form control. See the
     * accesskey attribute definition in HTML 4.0.
     */
    String getAccessKey();

    void setAccessKey(String accessKey);

    /**
     * This attribute links this label with another form controlby
     * <code>id</code> attribute. See the for attribute definition in HTML 4.0.
     */
    String getHtmlFor();

    void setHtmlFor(String htmlFor);
}


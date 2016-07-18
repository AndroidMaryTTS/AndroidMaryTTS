/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Provides a caption for a <code>FIELDSET</code> grouping.  See the LEGEND
 * element definition in HTML 4.0.
 */
public interface HTMLLegendElement extends HTMLElement {
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
     * Text alignment relative to <code>FIELDSET</code>. See the align attribute
     * definition in HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);
}


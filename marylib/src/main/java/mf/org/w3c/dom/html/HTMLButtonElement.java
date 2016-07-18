/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Push button. See the BUTTON element definition in HTML 4.0.
 */
public interface HTMLButtonElement extends HTMLElement {
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
     * The control is unavailable in this context. See the disabled attribute
     * definition in HTML 4.0.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * Form control or object name when submitted with a form. See the name
     * attribute definition in HTML 4.0.
     */
    String getName();

    void setName(String name);

    /**
     * Index that represents the element's position in the tabbing order. See
     * the tabindex attribute definition in HTML 4.0.
     */
    int getTabIndex();

    void setTabIndex(int tabIndex);

    /**
     * The type of button. See the type attribute definition in HTML 4.0.
     */
    String getType();

    /**
     * The current form control value. See the value attribute definition in
     * HTML 4.0.
     */
    String getValue();

    void setValue(String value);
}


/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Multi-line text field. See the TEXTAREA element definition in HTML 4.0.
 */
public interface HTMLTextAreaElement extends HTMLElement {
    /**
     * Stores the initial control value (i.e., the initial value of
     * <code>value</code>).
     */
    String getDefaultValue();

    void setDefaultValue(String defaultValue);

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
     * Width of control (in characters). See the cols attribute definition in
     * HTML 4.0.
     */
    int getCols();

    void setCols(int cols);

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
     * This control is read-only. See the readonly attribute definition in HTML
     * 4.0.
     */
    boolean getReadOnly();

    void setReadOnly(boolean readOnly);

    /**
     * Number of text rows. See the rows attribute definition in HTML 4.0.
     */
    int getRows();

    void setRows(int rows);

    /**
     * Index that represents the element's position in the tabbing order. See
     * the tabindex attribute definition in HTML 4.0.
     */
    int getTabIndex();

    void setTabIndex(int tabIndex);

    /**
     * The type of this form control.
     */
    String getType();

    /**
     * The current textual content of the multi-line text field. If the entirety
     * of the data can not fit into a single wstring, the implementation may
     * truncate the data.
     */
    String getValue();

    void setValue(String value);

    /**
     * Removes keyboard focus from this element.
     */
    void blur();

    /**
     * Gives keyboard focus to this element.
     */
    void focus();

    /**
     * Select the contents of the <code>TEXTAREA</code>.
     */
    void select();
}


/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * A selectable choice. See the OPTION element definition in HTML 4.0.
 */
public interface HTMLOptionElement extends HTMLElement {
    /**
     * Returns the <code>FORM</code> element containing this control.Returns
     * null if this control is not within the context of a form.
     */
    HTMLFormElement getForm();

    /**
     * Stores the initial value of the <code>selected</code> attribute.
     */
    boolean getDefaultSelected();

    void setDefaultSelected(boolean defaultSelected);

    /**
     * The text contained within the option element.
     */
    String getText();

    /**
     * The index of this <code>OPTION</code> in its parent <code>SELECT</code>.
     */
    int getIndex();

    void setIndex(int index);

    /**
     * The control is unavailable in this context. See the disabled attribute
     * definition in HTML 4.0.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * Option label for use in hierarchical menus. See the label attribute
     * definition in HTML 4.0.
     */
    String getLabel();

    void setLabel(String label);

    /**
     * Means that this option is initially selected. See the selected attribute
     * definition in HTML 4.0.
     */
    boolean getSelected();

    /**
     * The current form control value. See the value attribute definition in
     * HTML 4.0.
     */
    String getValue();

    void setValue(String value);
}


/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Form control. Note. Depending upon the environmentthe page is being viewed,
 * the value property may be read-only for thefile upload input type. For the
 * "password" input type, the actual valuereturned may be masked to prevent
 * unauthorized use. See the INPUT element definition in HTML 4.0.
 */
public interface HTMLInputElement extends HTMLElement {
    /**
     * Stores the initial control value (i.e., the initial value of
     * <code>value</code>).
     */
    String getDefaultValue();

    void setDefaultValue(String defaultValue);

    /**
     * When <code>type</code> has the value "Radio" or "Checkbox", stores the
     * initial value of the <code>checked</code> attribute.
     */
    boolean getDefaultChecked();

    void setDefaultChecked(boolean defaultChecked);

    /**
     * Returns the <code>FORM</code> element containing this control.Returns
     * null if this control is not within the context of a form.
     */
    HTMLFormElement getForm();

    /**
     * A comma-separated list of content types that a server processing thisform
     * will handle correctly. See the accept attribute definition in HTML 4.0.
     */
    String getAccept();

    void setAccept(String accept);

    /**
     * A single character access key to give access to the form control. See the
     * accesskey attribute definition in HTML 4.0.
     */
    String getAccessKey();

    void setAccessKey(String accessKey);

    /**
     * Aligns this object (vertically or horizontally) with respect to its
     * surrounding text. See the align attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Alternate text for user agents not rendering the normal contentof this
     * element. See the alt attribute definition in HTML 4.0.
     */
    String getAlt();

    void setAlt(String alt);

    /**
     * Describes whether a radio or check box is checked, when<code>type</code>
     * has the value "Radio" or "Checkbox".  The value isTRUE if explicitly
     * set. Represents the current state of the checkboxor radio button. See
     * the checked attribute definition in HTML 4.0.
     */
    boolean getChecked();

    void setChecked(boolean checked);

    /**
     * The control is unavailable in this context. See the disabled attribute
     * definition in HTML 4.0.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * Maximum number of characters for text fields, when <code>type</code>has
     * the value "Text" or "Password". See the maxlength attribute definition
     * in HTML 4.0.
     */
    int getMaxLength();

    void setMaxLength(int maxLength);

    /**
     * Form control or object name when submitted with a form. See the name
     * attribute definition in HTML 4.0.
     */
    String getName();

    void setName(String name);

    /**
     * This control is read-only. When <code>type</code> has the value "text"or
     * "password" only. See the readonly attribute definition in HTML 4.0.
     */
    boolean getReadOnly();

    void setReadOnly(boolean readOnly);

    /**
     * Size information. The precise meaning is specific to each type offield.
     * See the size attribute definition in HTML 4.0.
     */
    String getSize();

    void setSize(String size);

    /**
     * When the <code>type</code> attribute has the value "Image", thisattribute
     * specifies the location of the image to be used to decoratethe graphical
     * submit button. See the src attribute definition in HTML 4.0.
     */
    String getSrc();

    void setSrc(String src);

    /**
     * Index that represents the element's position in the tabbing order. See
     * the tabindex attribute definition in HTML 4.0.
     */
    int getTabIndex();

    void setTabIndex(int tabIndex);

    /**
     * The type of control created. See the type attribute definition in HTML
     * 4.0.
     */
    String getType();

    /**
     * Use client-side image map. See the usemap attribute definition in HTML
     * 4.0.
     */
    String getUseMap();

    void setUseMap(String useMap);

    /**
     * The current form control value. Used for radio buttons and check boxes.
     * See the value attribute definition in HTML 4.0.
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
     * Select the contents of the text area. For <code>INPUT</code> elements
     * whose <code>type</code> attribute has one of the following values:
     * "Text", "File", or "Password".
     */
    void select();

    /**
     * Simulate a mouse-click. For <code>INPUT</code> elements whose
     * <code>type</code> attribute has one of the followingvalues: "Button",
     * "Checkbox", "Radio", "Reset", or "Submit".
     */
    void click();
}


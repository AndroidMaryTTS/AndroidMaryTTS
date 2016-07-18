/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * The select element allows the selection of an option. The containedoptions
 * can be directly accessed through the select element as acollection. See
 * the SELECT element definition in HTML 4.0.
 */
public interface HTMLSelectElement extends HTMLElement {
    /**
     * The type of control created.
     */
    String getType();

    /**
     * The ordinal index of the selected option. The value -1 is returned ifno
     * element is selected. If multiple options are selected, the index ofthe
     * first selected option is returned.
     */
    int getSelectedIndex();

    void setSelectedIndex(int selectedIndex);

    /**
     * The current form control value.
     */
    String getValue();

    void setValue(String value);

    /**
     * The number of options in this <code>SELECT</code>.
     */
    int getLength();

    /**
     * Returns the <code>FORM</code> element containing this control.Returns
     * null if this control is not within the context of a form.
     */
    HTMLFormElement getForm();

    /**
     * The collection of <code>OPTION</code> elements contained by this element.
     */
    HTMLCollection getOptions();

    /**
     * The control is unavailable in this context. See the disabled attribute
     * definition in HTML 4.0.
     */
    boolean getDisabled();

    void setDisabled(boolean disabled);

    /**
     * If true, multiple <code>OPTION</code> elements may be selected in this
     * <code>SELECT</code>. See the multiple attribute definition in HTML 4.0.
     */
    boolean getMultiple();

    void setMultiple(boolean multiple);

    /**
     * Form control or object name when submitted with a form. See the name
     * attribute definition in HTML 4.0.
     */
    String getName();

    void setName(String name);

    /**
     * Number of visible rows. See the size attribute definition in HTML 4.0.
     */
    int getSize();

    void setSize(int size);

    /**
     * Index that represents the element's position in the tabbing order. See
     * the tabindex attribute definition in HTML 4.0.
     */
    int getTabIndex();

    void setTabIndex(int tabIndex);

    /**
     * Add a new element to the collection of <code>OPTION</code> elementsfor
     * this <code>SELECT</code>.
     *
     * @param element The element to add.
     * @param before  The element to insert before, or NULL for the head of the
     *                list.
     */
    void add(HTMLElement element,
             HTMLElement before);

    /**
     * Remove an element from the collection of <code>OPTION</code> elementsfor
     * this <code>SELECT</code>. Does nothing if no element has the givenindex.
     *
     * @param index The index of the item to remove.
     */
    void remove(int index);

    /**
     * Removes keyboard focus from this element.
     */
    void blur();

    /**
     * Gives keyboard focus to this element.
     */
    void focus();
}


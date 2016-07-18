/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Generic embedded object. Note. In principle, allproperties on the object
 * element are read-write but in someenvironments some properties may be
 * read-only once the underlyingobject is instantiated. See the OBJECT
 * element definition in HTML 4.0.
 */
public interface HTMLObjectElement extends HTMLElement {
    /**
     * Returns the <code>FORM</code> element containing this control.Returns
     * null if this control is not within the context of a form.
     */
    HTMLFormElement getForm();

    /**
     * Applet class file. See the <code>code</code> attribute for
     * HTMLAppletElement.
     */
    String getCode();

    void setCode(String code);

    /**
     * Aligns this object (vertically or horizontally) with respect to its
     * surrounding text. See the align attribute definition in HTML 4.0. This
     * attribute is deprecated in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Space-separated list of archives. See the archive attribute definition in
     * HTML 4.0.
     */
    String getArchive();

    void setArchive(String archive);

    /**
     * Width of border around the object. See the border attribute definition in
     * HTML 4.0. This attribute is deprecated in HTML 4.0.
     */
    String getBorder();

    void setBorder(String border);

    /**
     * Base URI for <code>classid</code>, <code>data</code>, and
     * <code>archive</code> attributes. See the codebase attribute definition
     * in HTML 4.0.
     */
    String getCodeBase();

    void setCodeBase(String codeBase);

    /**
     * Content type for data downloaded via <code>classid</code> attribute. See
     * the codetype attribute definition in HTML 4.0.
     */
    String getCodeType();

    void setCodeType(String codeType);

    /**
     * A URI specifying the location of the object's data.  See the data
     * attribute definition in HTML 4.0.
     */
    String getData();

    void setData(String data);

    /**
     * Declare (for future reference), but do not instantiate, thisobject. See
     * the declare attribute definition in HTML 4.0.
     */
    boolean getDeclare();

    void setDeclare(boolean declare);

    /**
     * Override height. See the height attribute definition in HTML 4.0.
     */
    String getHeight();

    void setHeight(String height);

    /**
     * Horizontal space to the left and right of this image, applet, or object.
     * See the hspace attribute definition in HTML 4.0. This attribute is
     * deprecated in HTML 4.0.
     */
    String getHspace();

    void setHspace(String hspace);

    /**
     * Form control or object name when submitted with a form. See the name
     * attribute definition in HTML 4.0.
     */
    String getName();

    void setName(String name);

    /**
     * Message to render while loading the object. See the standby attribute
     * definition in HTML 4.0.
     */
    String getStandby();

    void setStandby(String standby);

    /**
     * Index that represents the element's position in the tabbing order. See
     * the tabindex attribute definition in HTML 4.0.
     */
    int getTabIndex();

    void setTabIndex(int tabIndex);

    /**
     * Content type for data downloaded via <code>data</code> attribute. See the
     * type attribute definition in HTML 4.0.
     */
    String getType();

    void setType(String type);

    /**
     * Use client-side image map. See the usemap attribute definition in HTML
     * 4.0.
     */
    String getUseMap();

    void setUseMap(String useMap);

    /**
     * Vertical space above and below this image, applet, or object. See the
     * vspace attribute definition in HTML 4.0. This attribute is deprecated in
     * HTML 4.0.
     */
    String getVspace();

    void setVspace(String vspace);

    /**
     * Override width. See the width attribute definition in HTML 4.0.
     */
    String getWidth();

    void setWidth(String width);
}


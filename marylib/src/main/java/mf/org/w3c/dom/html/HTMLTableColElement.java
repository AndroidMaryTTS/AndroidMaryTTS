/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * Regroups the <code>COL</code> and <code>COLGROUP</code> elements. See the
 * COL element definition in HTML 4.0.
 */
public interface HTMLTableColElement extends HTMLElement {
    /**
     * Horizontal alignment of cell data in column. See the align attribute
     * definition in HTML 4.0.
     */
    String getAlign();

    void setAlign(String align);

    /**
     * Alignment character for cells in a column. See the char attribute
     * definition in HTML 4.0.
     */
    String getCh();

    void setCh(String ch);

    /**
     * Offset of alignment character. See the charoff attribute definition in
     * HTML 4.0.
     */
    String getChOff();

    void setChOff(String chOff);

    /**
     * Indicates the number of columns in a group or affected by a grouping. See
     * the span attribute definition in HTML 4.0.
     */
    int getSpan();

    void setSpan(int span);

    /**
     * Vertical alignment of cell data in column. See the valign attribute
     * definition in HTML 4.0.
     */
    String getVAlign();

    void setVAlign(String vAlign);

    /**
     * Default column width. See the width attribute definition in HTML 4.0.
     */
    String getWidth();

    void setWidth(String width);
}


/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

/**
 * The <code>THEAD</code>, <code>TFOOT</code>, and <code>TBODY</code>elements.
 */
public interface HTMLTableSectionElement extends HTMLElement {
    /**
     * Horizontal alignment of data in cells. See the <code>align</code>
     * attribute for HTMLTheadElement for details.
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
     * Vertical alignment of data in cells.See the <code>valign</code>attribute
     * for HTMLTheadElement for details.
     */
    String getVAlign();

    void setVAlign(String vAlign);

    /**
     * The collection of rows in this table section.
     */
    HTMLCollection getRows();

    /**
     * Insert a row into this section.
     *
     * @param index The row number where to insert a new row.
     * @return The newly created row.
     */
    HTMLElement insertRow(int index);

    /**
     * Delete a row from this section.
     *
     * @param index The index of the row to be deleted.
     */
    void deleteRow(int index);
}


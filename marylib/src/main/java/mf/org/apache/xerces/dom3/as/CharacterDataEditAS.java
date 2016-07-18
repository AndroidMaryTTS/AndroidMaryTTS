/*
 * Copyright (c) 2001 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package mf.org.apache.xerces.dom3.as;

/**
 * @deprecated This interface extends the <code>NodeEditAS</code> interface with
 * additional methods for document editing. An object implementing this
 * interface must also implement NodeEditAS interface.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface CharacterDataEditAS extends NodeEditAS {
    /**
     * <code>true</code> if content only whitespace; <code>false</code> for
     * non-whitespace.
     */
    boolean getIsWhitespaceOnly();

    /**
     * Determines if data can be set.
     *
     * @param offset Offset.
     * @param count  Argument to be set.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canSetData(int offset,
                       int count);

    /**
     * Determines if data can be appended.
     *
     * @param arg Argument to be appended.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canAppendData(String arg);

    /**
     * Determines if data can be replaced.
     *
     * @param offset Offset.
     * @param count  Replacement.
     * @param arg    Argument to be set.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canReplaceData(int offset,
                           int count,
                           String arg);

    /**
     * Determines if data can be inserted.
     *
     * @param offset Offset.
     * @param arg    Argument to be set.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canInsertData(int offset,
                          String arg);

    /**
     * Determines if data can be deleted.
     *
     * @param offset Offset.
     * @param count  Number of 16-bit units to delete.
     * @return <code>true</code> if no reason it can't be done;
     * <code>false</code> if it can't be done.
     */
    boolean canDeleteData(int offset,
                          int count);

}

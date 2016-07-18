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

import mf.org.w3c.dom.ls.LSSerializer;

/**
 * @deprecated A Abstract Schema serialization interface.
 * <p> DOMASWriters provides an API for serializing Abstract Schemas out in
 * the form of a source Abstract Schema. The Abstract Schema is written to
 * an output stream, the type of which depends on the specific language
 * bindings in use.
 * <p> DOMASWriter is a generic Abstract Schema serialization interface. It
 * can be applied to both an internal Abstract Schema and/or an external
 * Abstract Schema. DOMASWriter is applied to serialize a single Abstract
 * Schema. Serializing a document with an active Internal Abstract Schema
 * will serialize this internal Abstract Schema with the document as it is
 * part of the Document (see <code>LSSerializer</code>).
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
 * and Save Specification</a>.
 */
@Deprecated
public interface DOMASWriter extends LSSerializer {
    /**
     * Write out the specified Abstract Schema to the specified destination.
     * Does it write a DTD or an XML Schema (or something else)? Is it
     * possible to use this method to convert a DTD to an XML Schema?
     *
     * @param destination The destination for the data to be written.
     * @param model       The Abstract Schema to serialize.
     * @throws DOMSystemException This exception will be raised in response to any sort of IO or system
     *                            error that occurs while writing to the destination. It may wrap an
     *                            underlying system exception.
     */
    void writeASModel(java.io.OutputStream destination,
                      ASModel model)
            throws Exception;

}

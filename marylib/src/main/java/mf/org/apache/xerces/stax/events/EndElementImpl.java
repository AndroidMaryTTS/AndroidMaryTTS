/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mf.org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import mf.javax.xml.namespace.QName;
import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.events.EndElement;

/**
 * @author Lucian Holland
 * @version $Id: EndElementImpl.java 730796 2009-01-02 17:36:23Z mrglavas $
 * @xerces.internal
 */
public final class EndElementImpl extends ElementImpl implements EndElement {

    /**
     * @param location The location object for this event.
     */
    public EndElementImpl(final QName name, final Iterator namespaces, final Location location) {
        super(name, false, namespaces, location);
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            // Write end tags.
            writer.write("</");
            QName name = getName();
            String prefix = name.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(':');
            }
            writer.write(name.getLocalPart());
            writer.write('>');
        } catch (IOException ioe) {
            throw new XMLStreamException(ioe);
        }
    }
}

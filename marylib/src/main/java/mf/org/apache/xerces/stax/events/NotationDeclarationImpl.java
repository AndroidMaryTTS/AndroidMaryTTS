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

import mf.javax.xml.stream.Location;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.events.NotationDeclaration;

/**
 * @author Lucian Holland
 * @version $Id: NotationDeclarationImpl.java 730471 2008-12-31 20:45:32Z mrglavas $
 * @xerces.internal
 */
public final class NotationDeclarationImpl extends XMLEventImpl implements
        NotationDeclaration {

    private final String fSystemId;
    private final String fPublicId;
    private final String fName;

    /**
     * @param eventType
     * @param location
     * @param schemaType
     */
    public NotationDeclarationImpl(final String name, final String publicId, final String systemId, final Location location) {
        super(NOTATION_DECLARATION, location);
        fName = name;
        fPublicId = publicId;
        fSystemId = systemId;
    }

    /**
     * @see javax.xml.stream.events.NotationDeclaration#getName()
     */
    @Override
    public String getName() {
        return fName;
    }

    /**
     * @see javax.xml.stream.events.NotationDeclaration#getPublicId()
     */
    @Override
    public String getPublicId() {
        return fPublicId;
    }

    /**
     * @see javax.xml.stream.events.NotationDeclaration#getSystemId()
     */
    @Override
    public String getSystemId() {
        return fSystemId;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!NOTATION ");
            if (fPublicId != null) {
                writer.write("PUBLIC \"");
                writer.write(fPublicId);
                writer.write('\"');
                if (fSystemId != null) {
                    writer.write(" \"");
                    writer.write(fSystemId);
                    writer.write('\"');
                }
            } else {
                writer.write("SYSTEM \"");
                writer.write(fSystemId);
                writer.write('\"');
            }
            writer.write(fName);
            writer.write('>');
        } catch (IOException ioe) {
            throw new XMLStreamException(ioe);
        }
    }
}

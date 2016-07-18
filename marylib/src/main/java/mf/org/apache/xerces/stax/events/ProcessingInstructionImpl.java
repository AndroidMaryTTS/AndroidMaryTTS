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
import mf.javax.xml.stream.events.ProcessingInstruction;

/**
 * @author Lucian Holland
 * @version $Id: ProcessingInstructionImpl.java 730471 2008-12-31 20:45:32Z mrglavas $
 * @xerces.internal
 */
public final class ProcessingInstructionImpl extends XMLEventImpl implements
        ProcessingInstruction {

    private final String fTarget;
    private final String fData;

    /**
     * @param location
     */
    public ProcessingInstructionImpl(final String target, final String data, final Location location) {
        super(PROCESSING_INSTRUCTION, location);
        fTarget = target != null ? target : "";
        fData = data;
    }

    /**
     * @see javax.xml.stream.events.ProcessingInstruction#getTarget()
     */
    @Override
    public String getTarget() {
        return fTarget;
    }

    /**
     * @see javax.xml.stream.events.ProcessingInstruction#getData()
     */
    @Override
    public String getData() {
        return fData;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(fTarget);
            if (fData != null && fData.length() > 0) {
                writer.write(' ');
                writer.write(fData);
            }
            writer.write("?>");
        } catch (IOException ioe) {
            throw new XMLStreamException(ioe);
        }
    }
}

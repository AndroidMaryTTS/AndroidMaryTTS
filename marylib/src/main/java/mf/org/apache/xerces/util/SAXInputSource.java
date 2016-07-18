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

package mf.org.apache.xerces.util;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.io.Reader;

import mf.org.apache.xerces.xni.parser.XMLInputSource;

/**
 * <p>An <code>XMLInputSource</code> analogue to <code>javax.xml.transform.sax.SAXSource</code>.</p>
 *
 * @version $Id: SAXInputSource.java 447241 2006-09-18 05:12:57Z mrglavas $
 */
public final class SAXInputSource extends XMLInputSource {

    private XMLReader fXMLReader;
    private InputSource fInputSource;

    public SAXInputSource() {
        this(null);
    }

    public SAXInputSource(InputSource inputSource) {
        this(null, inputSource);
    }

    public SAXInputSource(XMLReader reader, InputSource inputSource) {
        super(inputSource != null ? inputSource.getPublicId() : null,
                inputSource != null ? inputSource.getSystemId() : null, null);
        if (inputSource != null) {
            setByteStream(inputSource.getByteStream());
            setCharacterStream(inputSource.getCharacterStream());
            setEncoding(inputSource.getEncoding());
        }
        fInputSource = inputSource;
        fXMLReader = reader;
    }

    public XMLReader getXMLReader() {
        return fXMLReader;
    }

    public void setXMLReader(XMLReader reader) {
        fXMLReader = reader;
    }

    public InputSource getInputSource() {
        return fInputSource;
    }

    public void setInputSource(InputSource inputSource) {
        if (inputSource != null) {
            setPublicId(inputSource.getPublicId());
            setSystemId(inputSource.getSystemId());
            setByteStream(inputSource.getByteStream());
            setCharacterStream(inputSource.getCharacterStream());
            setEncoding(inputSource.getEncoding());
        } else {
            setPublicId(null);
            setSystemId(null);
            setByteStream(null);
            setCharacterStream(null);
            setEncoding(null);
        }
        fInputSource = inputSource;
    }

    /**
     * Sets the public identifier.
     *
     * @param publicId The new public identifier.
     */
    @Override
    public void setPublicId(String publicId) {
        super.setPublicId(publicId);
        if (fInputSource == null) {
            fInputSource = new InputSource();
        }
        fInputSource.setPublicId(publicId);
    } // setPublicId(String)

    /**
     * Sets the system identifier.
     *
     * @param systemId The new system identifier.
     */
    @Override
    public void setSystemId(String systemId) {
        super.setSystemId(systemId);
        if (fInputSource == null) {
            fInputSource = new InputSource();
        }
        fInputSource.setSystemId(systemId);
    } // setSystemId(String)

    /**
     * Sets the byte stream. If the byte stream is not already opened
     * when this object is instantiated, then the code that opens the
     * stream should also set the byte stream on this object. Also, if
     * the encoding is auto-detected, then the encoding should also be
     * set on this object.
     *
     * @param byteStream The new byte stream.
     */
    @Override
    public void setByteStream(InputStream byteStream) {
        super.setByteStream(byteStream);
        if (fInputSource == null) {
            fInputSource = new InputSource();
        }
        fInputSource.setByteStream(byteStream);
    } // setByteStream(InputStream)

    /**
     * Sets the character stream. If the character stream is not already
     * opened when this object is instantiated, then the code that opens
     * the stream should also set the character stream on this object.
     * Also, the encoding of the byte stream used by the reader should
     * also be set on this object, if known.
     *
     * @param charStream The new character stream.
     * @see #setEncoding
     */
    @Override
    public void setCharacterStream(Reader charStream) {
        super.setCharacterStream(charStream);
        if (fInputSource == null) {
            fInputSource = new InputSource();
        }
        fInputSource.setCharacterStream(charStream);
    } // setCharacterStream(Reader)

    /**
     * Sets the encoding of the stream.
     *
     * @param encoding The new encoding.
     */
    @Override
    public void setEncoding(String encoding) {
        super.setEncoding(encoding);
        if (fInputSource == null) {
            fInputSource = new InputSource();
        }
        fInputSource.setEncoding(encoding);
    } // setEncoding(String)

} // SAXInputSource

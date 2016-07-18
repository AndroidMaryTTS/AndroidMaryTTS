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

package mf.org.apache.xerces.jaxp;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import mf.javax.xml.validation.TypeInfoProvider;
import mf.javax.xml.validation.ValidatorHandler;
import mf.org.apache.xerces.dom.DOMInputImpl;
import mf.org.apache.xerces.impl.Constants;
import mf.org.apache.xerces.impl.XMLErrorReporter;
import mf.org.apache.xerces.impl.xs.opti.DefaultXMLDocumentHandler;
import mf.org.apache.xerces.util.AttributesProxy;
import mf.org.apache.xerces.util.AugmentationsImpl;
import mf.org.apache.xerces.util.ErrorHandlerProxy;
import mf.org.apache.xerces.util.ErrorHandlerWrapper;
import mf.org.apache.xerces.util.LocatorProxy;
import mf.org.apache.xerces.util.SymbolTable;
import mf.org.apache.xerces.util.XMLResourceIdentifierImpl;
import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.NamespaceContext;
import mf.org.apache.xerces.xni.QName;
import mf.org.apache.xerces.xni.XMLAttributes;
import mf.org.apache.xerces.xni.XMLDocumentHandler;
import mf.org.apache.xerces.xni.XMLLocator;
import mf.org.apache.xerces.xni.XMLString;
import mf.org.apache.xerces.xni.XNIException;
import mf.org.apache.xerces.xni.parser.XMLComponent;
import mf.org.apache.xerces.xni.parser.XMLComponentManager;
import mf.org.apache.xerces.xni.parser.XMLConfigurationException;
import mf.org.apache.xerces.xni.parser.XMLEntityResolver;
import mf.org.apache.xerces.xni.parser.XMLErrorHandler;
import mf.org.apache.xerces.xni.parser.XMLInputSource;
import mf.org.w3c.dom.TypeInfo;
import mf.org.w3c.dom.ls.LSInput;
import mf.org.w3c.dom.ls.LSResourceResolver;

/**
 * Runs events through a {@link javax.xml.validation.ValidatorHandler}
 * and performs validation/infoset-augmentation by an external validator.
 * <p/>
 * <p/>
 * This component sets up the pipeline as follows:
 * <p/>
 * <!-- this picture may look teribble on your IDE but it is correct. -->
 * <pre>
 *             __                                           __
 *            /  |==> XNI2SAX --> Validator --> SAX2XNI ==>|
 *           /   |                                         |
 *       ==>| Tee|                                         | next
 *           \   |                                         |  component
 *            \  |============other XNI events============>|
 *             ~~                                           ~~
 * </pre>
 * <p/>
 * only those events that need to go through Validator will go the 1st route,
 * and other events go the 2nd direct route.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @version $Id: JAXPValidatorComponent.java 548088 2007-06-17 18:25:17Z mrglavas $
 */
final class JAXPValidatorComponent
        extends TeeXMLDocumentFilterImpl implements XMLComponent {

    /**
     * Property identifier: entity manager.
     */
    private static final String ENTITY_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;

    /**
     * Property identifier: error reporter.
     */
    private static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    /**
     * Property identifier: symbol table.
     */
    private static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;
    /**
     * {@link TypeInfoProvider} that returns no info.
     */
    private static final TypeInfoProvider noInfoProvider = new TypeInfoProvider() {
        @Override
        public TypeInfo getElementTypeInfo() {
            return null;
        }

        @Override
        public TypeInfo getAttributeTypeInfo(int index) {
            return null;
        }

        public TypeInfo getAttributeTypeInfo(String attributeQName) {
            return null;
        }

        public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
            return null;
        }

        @Override
        public boolean isIdAttribute(int index) {
            return false;
        }

        @Override
        public boolean isSpecified(int index) {
            return false;
        }
    };
    // pipeline parts
    private final ValidatorHandler validator;
    private final XNI2SAX xni2sax = new XNI2SAX();
    private final SAX2XNI sax2xni = new SAX2XNI();
    // never be null
    private final TypeInfoProvider typeInfoProvider;
    /**
     * Used to store the {@link Augmentations} associated with the
     * current event, so that we can pick it up again
     * when the event is forwarded by the {@link ValidatorHandler}.
     * <p/>
     * UGLY HACK.
     */
    private Augmentations fCurrentAug;

    // components obtained from a manager / property
    /**
     * {@link XMLAttributes} version of {@link #fCurrentAug}.
     */
    private XMLAttributes fCurrentAttributes;
    private SymbolTable fSymbolTable;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityResolver fEntityResolver;


    /**
     * @param validatorHandler may not be null.
     */
    public JAXPValidatorComponent(ValidatorHandler validatorHandler) {
        this.validator = validatorHandler;
        TypeInfoProvider tip = validatorHandler.getTypeInfoProvider();
        if (tip == null) tip = noInfoProvider;
        this.typeInfoProvider = tip;

        // configure wiring between internal components.
        xni2sax.setContentHandler(validator);
        validator.setContentHandler(sax2xni);
        this.setSide(xni2sax);

        // configure validator with proper EntityResolver/ErrorHandler.
        validator.setErrorHandler(new ErrorHandlerProxy() {
            @Override
            protected XMLErrorHandler getErrorHandler() {
                XMLErrorHandler handler = fErrorReporter.getErrorHandler();
                if (handler != null) return handler;
                return new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
            }
        });
        //MF
        LSResourceResolver rr = new LSResourceResolver() {
            @Override
            public LSInput resolveResource(String type, String ns, String publicId, String systemId, String baseUri) {
                if (fEntityResolver == null) return null;
                try {
                    XMLInputSource is = fEntityResolver.resolveEntity(
                            new XMLResourceIdentifierImpl(publicId, systemId, baseUri, null));
                    if (is == null) return null;

                    LSInput di = new DOMInputImpl();
                    di.setBaseURI(is.getBaseSystemId());
                    di.setByteStream(is.getByteStream());
                    di.setCharacterStream(is.getCharacterStream());
                    di.setEncoding(is.getEncoding());
                    di.setPublicId(is.getPublicId());
                    di.setSystemId(is.getSystemId());

                    return di;
                } catch (IOException e) {
                    // erors thrown by the callback is not supposed to be
                    // reported to users.
                    throw new XNIException(e);
                }
            }
        };
        validator.setResourceResolver(rr);
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        fCurrentAttributes = attributes;
        fCurrentAug = augs;
        xni2sax.startElement(element, attributes, null);
        fCurrentAttributes = null; // mostly to make it easy to find any bug.
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        fCurrentAug = augs;
        xni2sax.endElement(element, null);
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        startElement(element, attributes, augs);
        endElement(element, augs);
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        // since a validator may change the contents,
        // let this one go through a validator
        fCurrentAug = augs;
        xni2sax.characters(text, null);
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        // since a validator may change the contents,
        // let this one go through a validator
        fCurrentAug = augs;
        xni2sax.ignorableWhitespace(text, null);
    }

    @Override
    public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
        // obtain references from the manager
        fSymbolTable = (SymbolTable) componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter) componentManager.getProperty(ERROR_REPORTER);
        try {
            fEntityResolver = (XMLEntityResolver) componentManager.getProperty(ENTITY_MANAGER);
        } catch (XMLConfigurationException e) {
            fEntityResolver = null;
        }
    }

    /**
     * Compares the given {@link Attributes} with {@link #fCurrentAttributes}
     * and update the latter accordingly.
     */
    private void updateAttributes(Attributes atts) {
        int len = atts.getLength();
        for (int i = 0; i < len; i++) {
            String aqn = atts.getQName(i);
            int j = fCurrentAttributes.getIndex(aqn);
            String av = atts.getValue(i);
            if (j == -1) {
                // newly added attribute. add to the current attribute list.

                String prefix;
                int idx = aqn.indexOf(':');
                if (idx < 0) {
                    prefix = null;
                } else {
                    prefix = symbolize(aqn.substring(0, idx));
                }

                j = fCurrentAttributes.addAttribute(
                        new QName(
                                prefix,
                                symbolize(atts.getLocalName(i)),
                                symbolize(aqn),
                                symbolize(atts.getURI(i))),
                        atts.getType(i), av);
            } else {
                // the attribute is present.
                if (!av.equals(fCurrentAttributes.getValue(j))) {
                    // but the value was changed.
                    fCurrentAttributes.setValue(j, av);
                }
            }

            /** Augmentations augs = fCurrentAttributes.getAugmentations(j);
             augs.putItem( Constants.TYPEINFO,
             typeInfoProvider.getAttributeTypeInfo(i) );
             augs.putItem( Constants.ID_ATTRIBUTE,
             typeInfoProvider.isIdAttribute(i)?Boolean.TRUE:Boolean.FALSE ); **/
        }
    }

    private String symbolize(String s) {
        return fSymbolTable.addSymbol(s);
    }

    // no property/feature supported
    @Override
    public String[] getRecognizedFeatures() {
        return null;
    }

    @Override
    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    }

    @Override
    public String[] getRecognizedProperties() {
        return new String[]{ENTITY_MANAGER, ERROR_REPORTER, SYMBOL_TABLE};
    }

    //
    //
    // XMLComponent implementation.
    //
    //

    @Override
    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    }

    @Override
    public Boolean getFeatureDefault(String featureId) {
        return null;
    }

    @Override
    public Object getPropertyDefault(String propertyId) {
        return null;
    }

    /**
     * Converts {@link XNI} events to {@link ContentHandler} events.
     * <p/>
     * <p/>
     * Deriving from {@link DefaultXMLDocumentHandler}
     * to reuse its default {@link mf.org.apache.xerces.xni.XMLDocumentHandler}
     * implementation.
     *
     * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
     */
    private static final class XNI2SAX extends DefaultXMLDocumentHandler {

        /**
         * For efficiency, we reuse one instance.
         */
        private final AttributesProxy fAttributesProxy = new AttributesProxy(null);
        /**
         * Namespace context
         */
        protected NamespaceContext fNamespaceContext;
        private ContentHandler fContentHandler;
        private String fVersion;

        public ContentHandler getContentHandler() {
            return fContentHandler;
        }

        public void setContentHandler(ContentHandler handler) {
            this.fContentHandler = handler;
        }

        @Override
        public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
            this.fVersion = version;
        }

        @Override
        public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
            fNamespaceContext = namespaceContext;
            fContentHandler.setDocumentLocator(new LocatorProxy(locator));
            try {
                fContentHandler.startDocument();
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }

        @Override
        public void endDocument(Augmentations augs) throws XNIException {
            try {
                fContentHandler.endDocument();
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }

        @Override
        public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
            try {
                fContentHandler.processingInstruction(target, data.toString());
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }

        @Override
        public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
            try {
                // start namespace prefix mappings
                int count = fNamespaceContext.getDeclaredPrefixCount();
                if (count > 0) {
                    String prefix = null;
                    String uri = null;
                    for (int i = 0; i < count; i++) {
                        prefix = fNamespaceContext.getDeclaredPrefixAt(i);
                        uri = fNamespaceContext.getURI(prefix);
                        fContentHandler.startPrefixMapping(prefix, (uri == null) ? "" : uri);
                    }
                }

                String uri = element.uri != null ? element.uri : "";
                String localpart = element.localpart;
                fAttributesProxy.setAttributes(attributes);
                fContentHandler.startElement(uri, localpart, element.rawname, fAttributesProxy);
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }

        @Override
        public void endElement(QName element, Augmentations augs) throws XNIException {
            try {
                String uri = element.uri != null ? element.uri : "";
                String localpart = element.localpart;
                fContentHandler.endElement(uri, localpart, element.rawname);

                // send endPrefixMapping events
                int count = fNamespaceContext.getDeclaredPrefixCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        fContentHandler.endPrefixMapping(fNamespaceContext.getDeclaredPrefixAt(i));
                    }
                }
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }

        @Override
        public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
            startElement(element, attributes, augs);
            endElement(element, augs);
        }

        @Override
        public void characters(XMLString text, Augmentations augs) throws XNIException {
            try {
                fContentHandler.characters(text.ch, text.offset, text.length);
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }

        @Override
        public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
            try {
                fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            } catch (SAXException e) {
                throw new XNIException(e);
            }
        }
    }

    private static final class DraconianErrorHandler implements ErrorHandler {

        /**
         * Singleton instance.
         */
        private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE
                = new DraconianErrorHandler();

        private DraconianErrorHandler() {
        }

        /**
         * Returns the one and only instance of this error handler.
         */
        public static DraconianErrorHandler getInstance() {
            return ERROR_HANDLER_INSTANCE;
        }

        /**
         * Warning: Ignore.
         */
        @Override
        public void warning(SAXParseException e) throws SAXException {
            // noop
        }

        /**
         * Error: Throws back SAXParseException.
         */
        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        /**
         * Fatal Error: Throws back SAXParseException.
         */
        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

    } // DraconianErrorHandler

    /**
     * Uses {@link DefaultHandler} as a default implementation of
     * {@link ContentHandler}.
     * <p/>
     * <p/>
     * We only forward certain events from a {@link ValidatorHandler}.
     * Other events should go "the 2nd direct route".
     */
    private final class SAX2XNI extends DefaultHandler {

        /**
         * {@link Augmentations} to send along with events.
         * We reuse one object for efficiency.
         */
        private final Augmentations fAugmentations = new AugmentationsImpl();

        /**
         * {@link QName} to send along events.
         * we reuse one QName for efficiency.
         */
        private final QName fQName = new QName();

        @Override
        public void characters(char[] ch, int start, int len) throws SAXException {
            try {
                handler().characters(new XMLString(ch, start, len), aug());
            } catch (XNIException e) {
                throw toSAXException(e);
            }
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException {
            try {
                handler().ignorableWhitespace(new XMLString(ch, start, len), aug());
            } catch (XNIException e) {
                throw toSAXException(e);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
            try {
                updateAttributes(atts);
                handler().startElement(toQName(uri, localName, qname), fCurrentAttributes, elementAug());
            } catch (XNIException e) {
                throw toSAXException(e);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qname) throws SAXException {
            try {
                handler().endElement(toQName(uri, localName, qname), aug());
            } catch (XNIException e) {
                throw toSAXException(e);
            }
        }

        private Augmentations elementAug() {
            Augmentations aug = aug();
            /** aug.putItem(Constants.TYPEINFO,typeInfoProvider.getElementTypeInfo()); **/
            return aug;
        }


        /**
         * Gets the {@link Augmentations} that should be associated with
         * the current event.
         */
        private Augmentations aug() {
            if (fCurrentAug != null) {
                Augmentations r = fCurrentAug;
                fCurrentAug = null; // we "consumed" this augmentation.
                return r;
            }
            fAugmentations.removeAllItems();
            return fAugmentations;
        }

        /**
         * Get the handler to which we should send events.
         */
        private XMLDocumentHandler handler() {
            return JAXPValidatorComponent.this.getDocumentHandler();
        }

        /**
         * Converts the {@link XNIException} received from a downstream
         * component to a {@link SAXException}.
         */
        private SAXException toSAXException(XNIException xe) {
            Exception e = xe.getException();
            if (e == null) e = xe;
            if (e instanceof SAXException) return (SAXException) e;
            return new SAXException(e);
        }

        /**
         * Creates a proper {@link QName} object from 3 parts.
         * <p/>
         * This method does the symbolization.
         */
        private QName toQName(String uri, String localName, String qname) {
            String prefix = null;
            int idx = qname.indexOf(':');
            if (idx > 0)
                prefix = symbolize(qname.substring(0, idx));

            localName = symbolize(localName);
            qname = symbolize(qname);
            uri = symbolize(uri);

            // notify handlers
            fQName.setValues(prefix, localName, qname, uri);
            return fQName;
        }
    }

}

package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGLangSpace {
    String getXMLlang();

    void setXMLlang(String xmllang)
            throws DOMException;

    String getXMLspace();

    void setXMLspace(String xmlspace)
            throws DOMException;
}

package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGStyleElement extends
        SVGElement {
    String getXMLspace();

    void setXMLspace(String xmlspace)
            throws DOMException;

    String getType();

    void setType(String type)
            throws DOMException;

    String getMedia();

    void setMedia(String media)
            throws DOMException;

    String getTitle();

    void setTitle(String title)
            throws DOMException;
}

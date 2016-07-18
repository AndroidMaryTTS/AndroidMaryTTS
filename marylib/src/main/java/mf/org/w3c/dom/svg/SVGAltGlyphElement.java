package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGAltGlyphElement extends
        SVGTextPositioningElement,
        SVGURIReference {
    String getGlyphRef();

    void setGlyphRef(String glyphRef)
            throws DOMException;

    String getFormat();

    void setFormat(String format)
            throws DOMException;
}

package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGGlyphRefElement extends
        SVGElement,
        SVGURIReference,
        SVGStylable {
    String getGlyphRef();

    void setGlyphRef(String glyphRef)
            throws DOMException;

    String getFormat();

    void setFormat(String format)
            throws DOMException;

    float getX();

    void setX(float x)
            throws DOMException;

    float getY();

    void setY(float y)
            throws DOMException;

    float getDx();

    void setDx(float dx)
            throws DOMException;

    float getDy();

    void setDy(float dy)
            throws DOMException;
}

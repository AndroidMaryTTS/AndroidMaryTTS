package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPathSegLinetoHorizontalRel extends
        SVGPathSeg {
    float getX();

    void setX(float x)
            throws DOMException;
}

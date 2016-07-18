package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPathSegCurvetoCubicSmoothRel extends
        SVGPathSeg {
    float getX();

    void setX(float x)
            throws DOMException;

    float getY();

    void setY(float y)
            throws DOMException;

    float getX2();

    void setX2(float x2)
            throws DOMException;

    float getY2();

    void setY2(float y2)
            throws DOMException;
}

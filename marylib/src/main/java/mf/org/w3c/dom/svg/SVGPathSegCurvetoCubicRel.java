package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPathSegCurvetoCubicRel extends
        SVGPathSeg {
    float getX();

    void setX(float x)
            throws DOMException;

    float getY();

    void setY(float y)
            throws DOMException;

    float getX1();

    void setX1(float x1)
            throws DOMException;

    float getY1();

    void setY1(float y1)
            throws DOMException;

    float getX2();

    void setX2(float x2)
            throws DOMException;

    float getY2();

    void setY2(float y2)
            throws DOMException;
}

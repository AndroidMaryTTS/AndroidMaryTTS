package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPathSegCurvetoQuadraticSmoothRel extends
        SVGPathSeg {
    float getX();

    void setX(float x)
            throws DOMException;

    float getY();

    void setY(float y)
            throws DOMException;
}

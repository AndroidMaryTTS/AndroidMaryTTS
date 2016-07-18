package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPathSegLinetoVerticalRel extends
        SVGPathSeg {
    float getY();

    void setY(float y)
            throws DOMException;
}

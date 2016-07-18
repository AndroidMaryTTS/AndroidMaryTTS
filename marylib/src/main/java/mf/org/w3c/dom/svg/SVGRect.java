package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGRect {
    float getX();

    void setX(float x)
            throws DOMException;

    float getY();

    void setY(float y)
            throws DOMException;

    float getWidth();

    void setWidth(float width)
            throws DOMException;

    float getHeight();

    void setHeight(float height)
            throws DOMException;
}

package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGNumber {
    float getValue();

    void setValue(float value)
            throws DOMException;
}

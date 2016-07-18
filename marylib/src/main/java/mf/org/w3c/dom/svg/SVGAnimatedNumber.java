package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGAnimatedNumber {
    float getBaseVal();

    void setBaseVal(float baseVal)
            throws DOMException;

    float getAnimVal();
}

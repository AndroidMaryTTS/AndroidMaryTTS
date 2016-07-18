package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGAnimatedEnumeration {
    short getBaseVal();

    void setBaseVal(short baseVal)
            throws DOMException;

    short getAnimVal();
}

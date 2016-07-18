package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGAnimatedBoolean {
    boolean getBaseVal();

    void setBaseVal(boolean baseVal)
            throws DOMException;

    boolean getAnimVal();
}

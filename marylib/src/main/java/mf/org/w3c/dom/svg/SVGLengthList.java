package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGLengthList {
    int getNumberOfItems();

    void clear()
            throws DOMException;

    SVGLength initialize(SVGLength newItem)
            throws DOMException, SVGException;

    SVGLength getItem(int index)
            throws DOMException;

    SVGLength insertItemBefore(SVGLength newItem, int index)
            throws DOMException, SVGException;

    SVGLength replaceItem(SVGLength newItem, int index)
            throws DOMException, SVGException;

    SVGLength removeItem(int index)
            throws DOMException;

    SVGLength appendItem(SVGLength newItem)
            throws DOMException, SVGException;
}

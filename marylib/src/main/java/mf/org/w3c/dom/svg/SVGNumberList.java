package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGNumberList {
    int getNumberOfItems();

    void clear()
            throws DOMException;

    SVGNumber initialize(SVGNumber newItem)
            throws DOMException, SVGException;

    SVGNumber getItem(int index)
            throws DOMException;

    SVGNumber insertItemBefore(SVGNumber newItem, int index)
            throws DOMException, SVGException;

    SVGNumber replaceItem(SVGNumber newItem, int index)
            throws DOMException, SVGException;

    SVGNumber removeItem(int index)
            throws DOMException;

    SVGNumber appendItem(SVGNumber newItem)
            throws DOMException, SVGException;
}

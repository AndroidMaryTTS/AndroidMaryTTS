package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPointList {
    int getNumberOfItems();

    void clear()
            throws DOMException;

    SVGPoint initialize(SVGPoint newItem)
            throws DOMException, SVGException;

    SVGPoint getItem(int index)
            throws DOMException;

    SVGPoint insertItemBefore(SVGPoint newItem, int index)
            throws DOMException, SVGException;

    SVGPoint replaceItem(SVGPoint newItem, int index)
            throws DOMException, SVGException;

    SVGPoint removeItem(int index)
            throws DOMException;

    SVGPoint appendItem(SVGPoint newItem)
            throws DOMException, SVGException;
}

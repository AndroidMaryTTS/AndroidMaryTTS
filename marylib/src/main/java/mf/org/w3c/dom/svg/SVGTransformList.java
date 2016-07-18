package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGTransformList {
    int getNumberOfItems();

    void clear()
            throws DOMException;

    SVGTransform initialize(SVGTransform newItem)
            throws DOMException, SVGException;

    SVGTransform getItem(int index)
            throws DOMException;

    SVGTransform insertItemBefore(SVGTransform newItem, int index)
            throws DOMException, SVGException;

    SVGTransform replaceItem(SVGTransform newItem, int index)
            throws DOMException, SVGException;

    SVGTransform removeItem(int index)
            throws DOMException;

    SVGTransform appendItem(SVGTransform newItem)
            throws DOMException, SVGException;

    SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix);

    SVGTransform consolidate();
}

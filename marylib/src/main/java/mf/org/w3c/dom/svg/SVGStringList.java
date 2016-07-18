package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGStringList {
    int getNumberOfItems();

    void clear()
            throws DOMException;

    String initialize(String newItem)
            throws DOMException, SVGException;

    String getItem(int index)
            throws DOMException;

    String insertItemBefore(String newItem, int index)
            throws DOMException, SVGException;

    String replaceItem(String newItem, int index)
            throws DOMException, SVGException;

    String removeItem(int index)
            throws DOMException;

    String appendItem(String newItem)
            throws DOMException, SVGException;
}

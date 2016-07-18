package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.events.EventTarget;

public interface SVGElementInstance extends
        EventTarget {
    SVGElement getCorrespondingElement();

    SVGUseElement getCorrespondingUseElement();

    SVGElementInstance getParentNode();

    SVGElementInstanceList getChildNodes();

    SVGElementInstance getFirstChild();

    SVGElementInstance getLastChild();

    SVGElementInstance getPreviousSibling();

    SVGElementInstance getNextSibling();
}

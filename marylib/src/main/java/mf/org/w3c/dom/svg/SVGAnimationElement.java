package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.events.EventTarget;
import mf.org.w3c.dom.smil.ElementTimeControl;

public interface SVGAnimationElement extends
        SVGElement,
        SVGTests,
        SVGExternalResourcesRequired,
        ElementTimeControl,
        EventTarget {
    SVGElement getTargetElement();

    float getStartTime();

    float getCurrentTime();

    float getSimpleDuration()
            throws DOMException;
}

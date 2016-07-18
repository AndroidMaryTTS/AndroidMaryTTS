package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.events.EventTarget;

public interface SVGImageElement extends
        SVGElement,
        SVGURIReference,
        SVGTests,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGTransformable,
        EventTarget {
    SVGAnimatedLength getX();

    SVGAnimatedLength getY();

    SVGAnimatedLength getWidth();

    SVGAnimatedLength getHeight();

    SVGAnimatedPreserveAspectRatio getPreserveAspectRatio();
}

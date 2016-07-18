package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.events.EventTarget;

public interface SVGAElement extends
        SVGElement,
        SVGURIReference,
        SVGTests,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGTransformable,
        EventTarget {
    SVGAnimatedString getTarget();
}

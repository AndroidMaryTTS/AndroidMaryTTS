package mf.org.w3c.dom.svg;

public interface SVGViewElement extends
        SVGElement,
        SVGExternalResourcesRequired,
        SVGFitToViewBox,
        SVGZoomAndPan {
    SVGStringList getViewTarget();
}

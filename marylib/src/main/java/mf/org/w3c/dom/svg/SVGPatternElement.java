package mf.org.w3c.dom.svg;

public interface SVGPatternElement extends
        SVGElement,
        SVGURIReference,
        SVGTests,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGFitToViewBox,
        SVGUnitTypes {
    SVGAnimatedEnumeration getPatternUnits();

    SVGAnimatedEnumeration getPatternContentUnits();

    SVGAnimatedTransformList getPatternTransform();

    SVGAnimatedLength getX();

    SVGAnimatedLength getY();

    SVGAnimatedLength getWidth();

    SVGAnimatedLength getHeight();
}

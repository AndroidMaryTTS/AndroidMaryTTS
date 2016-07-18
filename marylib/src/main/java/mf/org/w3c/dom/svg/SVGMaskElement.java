package mf.org.w3c.dom.svg;

public interface SVGMaskElement extends
        SVGElement,
        SVGTests,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGUnitTypes {
    SVGAnimatedEnumeration getMaskUnits();

    SVGAnimatedEnumeration getMaskContentUnits();

    SVGAnimatedLength getX();

    SVGAnimatedLength getY();

    SVGAnimatedLength getWidth();

    SVGAnimatedLength getHeight();
}

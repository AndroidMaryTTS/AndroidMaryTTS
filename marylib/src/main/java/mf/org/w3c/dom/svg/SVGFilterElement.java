package mf.org.w3c.dom.svg;

public interface SVGFilterElement extends
        SVGElement,
        SVGURIReference,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGUnitTypes {
    SVGAnimatedEnumeration getFilterUnits();

    SVGAnimatedEnumeration getPrimitiveUnits();

    SVGAnimatedLength getX();

    SVGAnimatedLength getY();

    SVGAnimatedLength getWidth();

    SVGAnimatedLength getHeight();

    SVGAnimatedInteger getFilterResX();

    SVGAnimatedInteger getFilterResY();

    void setFilterRes(int filterResX, int filterResY);
}

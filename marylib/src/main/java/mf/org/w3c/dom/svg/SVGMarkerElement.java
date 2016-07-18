package mf.org.w3c.dom.svg;

public interface SVGMarkerElement extends
        SVGElement,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGFitToViewBox {
    // Marker Unit Types
    short SVG_MARKERUNITS_UNKNOWN = 0;
    short SVG_MARKERUNITS_USERSPACEONUSE = 1;
    short SVG_MARKERUNITS_STROKEWIDTH = 2;
    // Marker Orientation Types
    short SVG_MARKER_ORIENT_UNKNOWN = 0;
    short SVG_MARKER_ORIENT_AUTO = 1;
    short SVG_MARKER_ORIENT_ANGLE = 2;

    SVGAnimatedLength getRefX();

    SVGAnimatedLength getRefY();

    SVGAnimatedEnumeration getMarkerUnits();

    SVGAnimatedLength getMarkerWidth();

    SVGAnimatedLength getMarkerHeight();

    SVGAnimatedEnumeration getOrientType();

    SVGAnimatedAngle getOrientAngle();

    void setOrientToAuto();

    void setOrientToAngle(SVGAngle angle);
}

package mf.org.w3c.dom.svg;

public interface SVGFEBlendElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    // Blend Mode Types
    short SVG_FEBLEND_MODE_UNKNOWN = 0;
    short SVG_FEBLEND_MODE_NORMAL = 1;
    short SVG_FEBLEND_MODE_MULTIPLY = 2;
    short SVG_FEBLEND_MODE_SCREEN = 3;
    short SVG_FEBLEND_MODE_DARKEN = 4;
    short SVG_FEBLEND_MODE_LIGHTEN = 5;

    SVGAnimatedString getIn1();

    SVGAnimatedString getIn2();

    SVGAnimatedEnumeration getMode();
}

package mf.org.w3c.dom.svg;

public interface SVGFESpecularLightingElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();

    SVGAnimatedNumber getSurfaceScale();

    SVGAnimatedNumber getSpecularConstant();

    SVGAnimatedNumber getSpecularExponent();
}

package mf.org.w3c.dom.svg;

public interface SVGFESpotLightElement extends
        SVGElement {
    SVGAnimatedNumber getX();

    SVGAnimatedNumber getY();

    SVGAnimatedNumber getZ();

    SVGAnimatedNumber getPointsAtX();

    SVGAnimatedNumber getPointsAtY();

    SVGAnimatedNumber getPointsAtZ();

    SVGAnimatedNumber getSpecularExponent();

    SVGAnimatedNumber getLimitingConeAngle();
}

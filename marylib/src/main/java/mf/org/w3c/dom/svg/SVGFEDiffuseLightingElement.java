package mf.org.w3c.dom.svg;

public interface SVGFEDiffuseLightingElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();

    SVGAnimatedNumber getSurfaceScale();

    SVGAnimatedNumber getDiffuseConstant();

    SVGAnimatedNumber getKernelUnitLengthX();

    SVGAnimatedNumber getKernelUnitLengthY();
}

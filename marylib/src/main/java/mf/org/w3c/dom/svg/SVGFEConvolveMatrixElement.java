package mf.org.w3c.dom.svg;

public interface SVGFEConvolveMatrixElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    // Edge Mode Values
    short SVG_EDGEMODE_UNKNOWN = 0;
    short SVG_EDGEMODE_DUPLICATE = 1;
    short SVG_EDGEMODE_WRAP = 2;
    short SVG_EDGEMODE_NONE = 3;

    SVGAnimatedInteger getOrderX();

    SVGAnimatedInteger getOrderY();

    SVGAnimatedNumberList getKernelMatrix();

    SVGAnimatedNumber getDivisor();

    SVGAnimatedNumber getBias();

    SVGAnimatedInteger getTargetX();

    SVGAnimatedInteger getTargetY();

    SVGAnimatedEnumeration getEdgeMode();

    SVGAnimatedNumber getKernelUnitLengthX();

    SVGAnimatedNumber getKernelUnitLengthY();

    SVGAnimatedBoolean getPreserveAlpha();
}

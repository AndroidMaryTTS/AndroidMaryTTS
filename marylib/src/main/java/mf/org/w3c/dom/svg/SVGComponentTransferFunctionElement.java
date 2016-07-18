package mf.org.w3c.dom.svg;

public interface SVGComponentTransferFunctionElement extends
        SVGElement {
    // Component Transfer Types
    short SVG_FECOMPONENTTRANSFER_TYPE_UNKNOWN = 0;
    short SVG_FECOMPONENTTRANSFER_TYPE_IDENTITY = 1;
    short SVG_FECOMPONENTTRANSFER_TYPE_TABLE = 2;
    short SVG_FECOMPONENTTRANSFER_TYPE_DISCRETE = 3;
    short SVG_FECOMPONENTTRANSFER_TYPE_LINEAR = 4;
    short SVG_FECOMPONENTTRANSFER_TYPE_GAMMA = 5;

    SVGAnimatedEnumeration getType();

    SVGAnimatedNumberList getTableValues();

    SVGAnimatedNumber getSlope();

    SVGAnimatedNumber getIntercept();

    SVGAnimatedNumber getAmplitude();

    SVGAnimatedNumber getExponent();

    SVGAnimatedNumber getOffset();
}

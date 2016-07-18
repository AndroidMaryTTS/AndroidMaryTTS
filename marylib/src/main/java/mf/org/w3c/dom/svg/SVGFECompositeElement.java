package mf.org.w3c.dom.svg;

public interface SVGFECompositeElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    // Composite Operators
    short SVG_FECOMPOSITE_OPERATOR_UNKNOWN = 0;
    short SVG_FECOMPOSITE_OPERATOR_OVER = 1;
    short SVG_FECOMPOSITE_OPERATOR_IN = 2;
    short SVG_FECOMPOSITE_OPERATOR_OUT = 3;
    short SVG_FECOMPOSITE_OPERATOR_ATOP = 4;
    short SVG_FECOMPOSITE_OPERATOR_XOR = 5;
    short SVG_FECOMPOSITE_OPERATOR_ARITHMETIC = 6;

    SVGAnimatedString getIn1();

    SVGAnimatedString getIn2();

    SVGAnimatedEnumeration getOperator();

    SVGAnimatedNumber getK1();

    SVGAnimatedNumber getK2();

    SVGAnimatedNumber getK3();

    SVGAnimatedNumber getK4();
}

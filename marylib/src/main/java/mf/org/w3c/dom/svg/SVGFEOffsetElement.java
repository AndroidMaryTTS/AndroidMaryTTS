package mf.org.w3c.dom.svg;

public interface SVGFEOffsetElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();

    SVGAnimatedNumber getDx();

    SVGAnimatedNumber getDy();
}

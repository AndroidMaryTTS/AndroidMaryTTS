package mf.org.w3c.dom.svg;

public interface SVGFEGaussianBlurElement extends
        SVGElement,
        SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();

    SVGAnimatedNumber getStdDeviationX();

    SVGAnimatedNumber getStdDeviationY();

    void setStdDeviation(float stdDeviationX, float stdDeviationY);
}

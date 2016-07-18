package mf.org.w3c.dom.svg;

public interface SVGFilterPrimitiveStandardAttributes extends
        SVGStylable {
    SVGAnimatedLength getX();

    SVGAnimatedLength getY();

    SVGAnimatedLength getWidth();

    SVGAnimatedLength getHeight();

    SVGAnimatedString getResult();
}

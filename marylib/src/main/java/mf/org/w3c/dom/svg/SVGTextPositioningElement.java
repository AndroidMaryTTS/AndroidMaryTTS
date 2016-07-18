package mf.org.w3c.dom.svg;

public interface SVGTextPositioningElement extends
        SVGTextContentElement {
    SVGAnimatedLengthList getX();

    SVGAnimatedLengthList getY();

    SVGAnimatedLengthList getDx();

    SVGAnimatedLengthList getDy();

    SVGAnimatedNumberList getRotate();
}

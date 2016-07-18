package mf.org.w3c.dom.svg;

public interface SVGLocatable {
    SVGElement getNearestViewportElement();

    SVGElement getFarthestViewportElement();

    SVGRect getBBox();

    SVGMatrix getCTM();

    SVGMatrix getScreenCTM();

    SVGMatrix getTransformToElement(SVGElement element)
            throws SVGException;
}

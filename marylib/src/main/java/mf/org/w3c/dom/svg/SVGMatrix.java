package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGMatrix {
    float getA();

    void setA(float a)
            throws DOMException;

    float getB();

    void setB(float b)
            throws DOMException;

    float getC();

    void setC(float c)
            throws DOMException;

    float getD();

    void setD(float d)
            throws DOMException;

    float getE();

    void setE(float e)
            throws DOMException;

    float getF();

    void setF(float f)
            throws DOMException;

    SVGMatrix multiply(SVGMatrix secondMatrix);

    SVGMatrix inverse()
            throws SVGException;

    SVGMatrix translate(float x, float y);

    SVGMatrix scale(float scaleFactor);

    SVGMatrix scaleNonUniform(float scaleFactorX, float scaleFactorY);

    SVGMatrix rotate(float angle);

    SVGMatrix rotateFromVector(float x, float y)
            throws SVGException;

    SVGMatrix flipX();

    SVGMatrix flipY();

    SVGMatrix skewX(float angle);

    SVGMatrix skewY(float angle);
}

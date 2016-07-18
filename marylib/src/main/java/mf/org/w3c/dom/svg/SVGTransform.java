package mf.org.w3c.dom.svg;

public interface SVGTransform {
    // Transform Types
    short SVG_TRANSFORM_UNKNOWN = 0;
    short SVG_TRANSFORM_MATRIX = 1;
    short SVG_TRANSFORM_TRANSLATE = 2;
    short SVG_TRANSFORM_SCALE = 3;
    short SVG_TRANSFORM_ROTATE = 4;
    short SVG_TRANSFORM_SKEWX = 5;
    short SVG_TRANSFORM_SKEWY = 6;

    short getType();

    SVGMatrix getMatrix();

    void setMatrix(SVGMatrix matrix);

    float getAngle();

    void setTranslate(float tx, float ty);

    void setScale(float sx, float sy);

    void setRotate(float angle, float cx, float cy);

    void setSkewX(float angle);

    void setSkewY(float angle);
}

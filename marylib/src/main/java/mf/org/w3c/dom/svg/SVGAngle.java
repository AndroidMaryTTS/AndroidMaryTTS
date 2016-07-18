package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGAngle {
    // Angle Unit Types
    short SVG_ANGLETYPE_UNKNOWN = 0;
    short SVG_ANGLETYPE_UNSPECIFIED = 1;
    short SVG_ANGLETYPE_DEG = 2;
    short SVG_ANGLETYPE_RAD = 3;
    short SVG_ANGLETYPE_GRAD = 4;

    short getUnitType();

    float getValue();

    void setValue(float value)
            throws DOMException;

    float getValueInSpecifiedUnits();

    void setValueInSpecifiedUnits(float valueInSpecifiedUnits)
            throws DOMException;

    String getValueAsString();

    void setValueAsString(String valueAsString)
            throws DOMException;

    void newValueSpecifiedUnits(short unitType, float valueInSpecifiedUnits);

    void convertToSpecifiedUnits(short unitType);
}

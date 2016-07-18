package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.css.CSSValue;
import mf.org.w3c.dom.css.RGBColor;

public interface SVGColor extends
        CSSValue {
    // Color Types
    short SVG_COLORTYPE_UNKNOWN = 0;
    short SVG_COLORTYPE_RGBCOLOR = 1;
    short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;
    short SVG_COLORTYPE_CURRENTCOLOR = 3;

    short getColorType();

    RGBColor getRGBColor();

    void setRGBColor(String rgbColor)
            throws SVGException;

    SVGICCColor getICCColor();

    void setRGBColorICCColor(String rgbColor, String iccColor)
            throws SVGException;

    void setColor(short colorType, String rgbColor, String iccColor)
            throws SVGException;
}

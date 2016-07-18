package mf.org.w3c.dom.svg;


public interface SVGPaint extends
        SVGColor {
    // Paint Types
    short SVG_PAINTTYPE_UNKNOWN = 0;
    short SVG_PAINTTYPE_RGBCOLOR = 1;
    short SVG_PAINTTYPE_RGBCOLOR_ICCCOLOR = 2;
    short SVG_PAINTTYPE_NONE = 101;
    short SVG_PAINTTYPE_CURRENTCOLOR = 102;
    short SVG_PAINTTYPE_URI_NONE = 103;
    short SVG_PAINTTYPE_URI_CURRENTCOLOR = 104;
    short SVG_PAINTTYPE_URI_RGBCOLOR = 105;
    short SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR = 106;
    short SVG_PAINTTYPE_URI = 107;

    short getPaintType();

    String getUri();

    void setUri(String uri);

    void setPaint(short paintType, String uri, String rgbColor, String iccColor)
            throws SVGException;
}

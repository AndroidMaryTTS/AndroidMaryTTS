package mf.org.w3c.dom.svg;

public interface SVGTextPathElement extends
        SVGTextContentElement,
        SVGURIReference {
    // textPath Method Types
    short TEXTPATH_METHODTYPE_UNKNOWN = 0;
    short TEXTPATH_METHODTYPE_ALIGN = 1;
    short TEXTPATH_METHODTYPE_STRETCH = 2;
    // textPath Spacing Types
    short TEXTPATH_SPACINGTYPE_UNKNOWN = 0;
    short TEXTPATH_SPACINGTYPE_AUTO = 1;
    short TEXTPATH_SPACINGTYPE_EXACT = 2;

    SVGAnimatedLength getStartOffset();

    SVGAnimatedEnumeration getMethod();

    SVGAnimatedEnumeration getSpacing();
}

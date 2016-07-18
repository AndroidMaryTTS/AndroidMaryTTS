package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.events.EventTarget;

public interface SVGTextContentElement extends
        SVGElement,
        SVGTests,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        EventTarget {
    // lengthAdjust Types
    short LENGTHADJUST_UNKNOWN = 0;
    short LENGTHADJUST_SPACING = 1;
    short LENGTHADJUST_SPACINGANDGLYPHS = 2;

    SVGAnimatedLength getTextLength();

    SVGAnimatedEnumeration getLengthAdjust();

    int getNumberOfChars();

    float getComputedTextLength();

    float getSubStringLength(int charnum, int nchars)
            throws DOMException;

    SVGPoint getStartPositionOfChar(int charnum)
            throws DOMException;

    SVGPoint getEndPositionOfChar(int charnum)
            throws DOMException;

    SVGRect getExtentOfChar(int charnum)
            throws DOMException;

    float getRotationOfChar(int charnum)
            throws DOMException;

    int getCharNumAtPosition(SVGPoint point);

    void selectSubString(int charnum, int nchars)
            throws DOMException;
}

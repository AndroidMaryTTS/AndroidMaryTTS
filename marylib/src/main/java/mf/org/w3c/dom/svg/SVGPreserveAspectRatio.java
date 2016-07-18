package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPreserveAspectRatio {
    // Alignment Types
    short SVG_PRESERVEASPECTRATIO_UNKNOWN = 0;
    short SVG_PRESERVEASPECTRATIO_NONE = 1;
    short SVG_PRESERVEASPECTRATIO_XMINYMIN = 2;
    short SVG_PRESERVEASPECTRATIO_XMIDYMIN = 3;
    short SVG_PRESERVEASPECTRATIO_XMAXYMIN = 4;
    short SVG_PRESERVEASPECTRATIO_XMINYMID = 5;
    short SVG_PRESERVEASPECTRATIO_XMIDYMID = 6;
    short SVG_PRESERVEASPECTRATIO_XMAXYMID = 7;
    short SVG_PRESERVEASPECTRATIO_XMINYMAX = 8;
    short SVG_PRESERVEASPECTRATIO_XMIDYMAX = 9;
    short SVG_PRESERVEASPECTRATIO_XMAXYMAX = 10;
    // Meet-or-slice Types
    short SVG_MEETORSLICE_UNKNOWN = 0;
    short SVG_MEETORSLICE_MEET = 1;
    short SVG_MEETORSLICE_SLICE = 2;

    short getAlign();

    void setAlign(short align)
            throws DOMException;

    short getMeetOrSlice();

    void setMeetOrSlice(short meetOrSlice)
            throws DOMException;
}

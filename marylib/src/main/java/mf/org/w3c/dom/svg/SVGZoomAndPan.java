package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGZoomAndPan {
    // Zoom and Pan Types
    short SVG_ZOOMANDPAN_UNKNOWN = 0;
    short SVG_ZOOMANDPAN_DISABLE = 1;
    short SVG_ZOOMANDPAN_MAGNIFY = 2;

    short getZoomAndPan();

    void setZoomAndPan(short zoomAndPan)
            throws DOMException;
}

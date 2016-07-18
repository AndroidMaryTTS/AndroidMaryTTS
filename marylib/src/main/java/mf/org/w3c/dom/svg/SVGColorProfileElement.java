package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGColorProfileElement extends
        SVGElement,
        SVGURIReference,
        SVGRenderingIntent {
    String getLocal();

    void setLocal(String local)
            throws DOMException;

    String getName();

    void setName(String name)
            throws DOMException;

    short getRenderingIntent();

    void setRenderingIntent(short renderingIntent)
            throws DOMException;
}

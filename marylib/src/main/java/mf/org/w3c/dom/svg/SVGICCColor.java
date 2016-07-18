package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGICCColor {
    String getColorProfile();

    void setColorProfile(String colorProfile)
            throws DOMException;

    SVGNumberList getColors();
}

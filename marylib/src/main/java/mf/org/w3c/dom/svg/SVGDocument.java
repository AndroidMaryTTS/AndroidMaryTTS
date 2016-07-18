package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.events.DocumentEvent;

public interface SVGDocument extends
        Document,
        DocumentEvent {
    String getTitle();

    String getReferrer();

    String getDomain();

    String getURL();

    SVGSVGElement getRootElement();
}

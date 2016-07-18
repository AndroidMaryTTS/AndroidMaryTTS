package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.NodeList;
import mf.org.w3c.dom.css.DocumentCSS;
import mf.org.w3c.dom.css.ViewCSS;
import mf.org.w3c.dom.events.DocumentEvent;
import mf.org.w3c.dom.events.EventTarget;

public interface SVGSVGElement extends
        SVGElement,
        SVGTests,
        SVGLangSpace,
        SVGExternalResourcesRequired,
        SVGStylable,
        SVGLocatable,
        SVGFitToViewBox,
        SVGZoomAndPan,
        EventTarget,
        DocumentEvent,
        ViewCSS,
        DocumentCSS {
    SVGAnimatedLength getX();

    SVGAnimatedLength getY();

    SVGAnimatedLength getWidth();

    SVGAnimatedLength getHeight();

    String getContentScriptType();

    void setContentScriptType(String contentScriptType)
            throws DOMException;

    String getContentStyleType();

    void setContentStyleType(String contentStyleType)
            throws DOMException;

    SVGRect getViewport();

    float getPixelUnitToMillimeterX();

    float getPixelUnitToMillimeterY();

    float getScreenPixelToMillimeterX();

    float getScreenPixelToMillimeterY();

    boolean getUseCurrentView();

    void setUseCurrentView(boolean useCurrentView)
            throws DOMException;

    SVGViewSpec getCurrentView();

    float getCurrentScale();

    void setCurrentScale(float currentScale)
            throws DOMException;

    SVGPoint getCurrentTranslate();

    int suspendRedraw(int max_wait_milliseconds);

    void unsuspendRedraw(int suspend_handle_id)
            throws DOMException;

    void unsuspendRedrawAll();

    void forceRedraw();

    void pauseAnimations();

    void unpauseAnimations();

    boolean animationsPaused();

    float getCurrentTime();

    void setCurrentTime(float seconds);

    NodeList getIntersectionList(SVGRect rect, SVGElement referenceElement);

    NodeList getEnclosureList(SVGRect rect, SVGElement referenceElement);

    boolean checkIntersection(SVGElement element, SVGRect rect);

    boolean checkEnclosure(SVGElement element, SVGRect rect);

    void deselectAll();

    SVGNumber createSVGNumber();

    SVGLength createSVGLength();

    SVGAngle createSVGAngle();

    SVGPoint createSVGPoint();

    SVGMatrix createSVGMatrix();

    SVGRect createSVGRect();

    SVGTransform createSVGTransform();

    SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix);

    Element getElementById(String elementId);
}

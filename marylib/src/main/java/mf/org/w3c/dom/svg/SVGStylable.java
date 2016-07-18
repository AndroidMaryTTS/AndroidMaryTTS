package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.css.CSSStyleDeclaration;
import mf.org.w3c.dom.css.CSSValue;

public interface SVGStylable {
    SVGAnimatedString getClassName();

    CSSStyleDeclaration getStyle();

    CSSValue getPresentationAttribute(String name);
}

package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.css.CSSRule;

public interface SVGCSSRule extends
        CSSRule {
    // Additional CSS RuleType to support ICC color specifications
    short COLOR_PROFILE_RULE = 7;
}

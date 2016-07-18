package mf.org.w3c.dom.svg;

public interface SVGAnimatedPathData {
    SVGPathSegList getPathSegList();

    SVGPathSegList getNormalizedPathSegList();

    SVGPathSegList getAnimatedPathSegList();

    SVGPathSegList getAnimatedNormalizedPathSegList();
}

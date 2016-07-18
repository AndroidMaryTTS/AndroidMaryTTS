package mf.org.w3c.dom.svg;

public interface SVGTests {
    SVGStringList getRequiredFeatures();

    SVGStringList getRequiredExtensions();

    SVGStringList getSystemLanguage();

    boolean hasExtension(String extension);
}

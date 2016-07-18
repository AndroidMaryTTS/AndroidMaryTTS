package mf.org.w3c.dom.svg;

import mf.org.w3c.dom.DOMException;

public interface SVGPathSegArcRel extends
        SVGPathSeg {
    float getX();

    void setX(float x)
            throws DOMException;

    float getY();

    void setY(float y)
            throws DOMException;

    float getR1();

    void setR1(float r1)
            throws DOMException;

    float getR2();

    void setR2(float r2)
            throws DOMException;

    float getAngle();

    void setAngle(float angle)
            throws DOMException;

    boolean getLargeArcFlag();

    void setLargeArcFlag(boolean largeArcFlag)
            throws DOMException;

    boolean getSweepFlag();

    void setSweepFlag(boolean sweepFlag)
            throws DOMException;
}

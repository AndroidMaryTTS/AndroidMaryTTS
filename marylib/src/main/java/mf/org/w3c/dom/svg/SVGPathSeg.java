package mf.org.w3c.dom.svg;

public interface SVGPathSeg {
    // Path Segment Types
    short PATHSEG_UNKNOWN = 0;
    short PATHSEG_CLOSEPATH = 1;
    short PATHSEG_MOVETO_ABS = 2;
    short PATHSEG_MOVETO_REL = 3;
    short PATHSEG_LINETO_ABS = 4;
    short PATHSEG_LINETO_REL = 5;
    short PATHSEG_CURVETO_CUBIC_ABS = 6;
    short PATHSEG_CURVETO_CUBIC_REL = 7;
    short PATHSEG_CURVETO_QUADRATIC_ABS = 8;
    short PATHSEG_CURVETO_QUADRATIC_REL = 9;
    short PATHSEG_ARC_ABS = 10;
    short PATHSEG_ARC_REL = 11;
    short PATHSEG_LINETO_HORIZONTAL_ABS = 12;
    short PATHSEG_LINETO_HORIZONTAL_REL = 13;
    short PATHSEG_LINETO_VERTICAL_ABS = 14;
    short PATHSEG_LINETO_VERTICAL_REL = 15;
    short PATHSEG_CURVETO_CUBIC_SMOOTH_ABS = 16;
    short PATHSEG_CURVETO_CUBIC_SMOOTH_REL = 17;
    short PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS = 18;
    short PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL = 19;

    short getPathSegType();

    String getPathSegTypeAsLetter();
}

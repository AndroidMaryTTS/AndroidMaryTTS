package Jampack;

/**
 * Parameter is a class containing global parameters for
 * Jampack.
 *
 * @author G. W. Stewart
 * @version Pre-alpha
 */

public class Parameters {

    /**
     * The base index
     */
    protected static int BaseIndex = 1;

    /**
     * Flag allowing only one change in base index
     */
    protected static boolean BaseIndexNotChangeable;
    /**
     * The history flag indicating whether to save decompositions
     */

    protected static boolean History = true;
    /**
     * Output field width.
     */
    protected static int OutputFieldWidth = 12;
    /**
     * Number of places to the right of the decimal point.
     */
    protected static int OutputFracPlaces = 3;
    /**
     * Output page width
     */
    protected static int PageWidth = 80;

    /**
     * Returns the base index
     */

    public static int getBaseIndex() {
        return BaseIndex;
    }

    /**
     * Resets the default base index.
     *
     * @param xb The new base index
     * @throws JampackException Thrown when the base indices are
     *                          not changable.
     */
    public static void setBaseIndex(int bx)
            throws JampackException {
        if (BaseIndexNotChangeable) {
            throw new JampackException
                    ("Illegal attempt to change base index");
        }
        BaseIndex = bx;
        BaseIndexNotChangeable = true;
    }

    /**
     * Adjust the base index of a Zmat to make it conform to
     * the default.
     */
    public static void adjustBaseIndex(Zmat A) {
        BaseIndexNotChangeable = true;
        A.basex = BaseIndex;
        A.getProperties();
    }

    /**
     * Adjust the base index of a Zdiagmat to make it conform to
     * the default.
     */
    public static void adjustBaseIndex(Zdiagmat A) {
        BaseIndexNotChangeable = true;
        A.basex = BaseIndex;
        A.getProperties();
    }

    /**
     * Sets the history flag.
     */
    public static void setHistory() {
        History = true;
    }

    /**
     * Unsets the history flag.
     */
    public static void unsetHistory() {
        History = false;
    }

    /**
     * Changes the output parameters.  Nonpositive parameters are ignored.
     *
     * @param width     The new field width
     * @param frac      The new number of places to the right of the decimal
     * @param pagewidth The new page width
     */
    public static void setOutputParams(int width, int frac, int pagewidth) {

        OutputFieldWidth = (width > 0) ? width : OutputFieldWidth;
        OutputFracPlaces = (frac > 0) ? width : OutputFracPlaces;
        PageWidth = (pagewidth > 0) ? pagewidth : PageWidth;
    }
}

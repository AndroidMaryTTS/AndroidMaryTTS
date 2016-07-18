package Jampack;

/**
 * This is the exception class for Jampack.  Since
 * most errors in matrix algorithms are unrecoverable,
 * the standard response is to pass an error message
 * up the line.
 *
 * @author G. W. Stewart
 * @version Pre-alpha
 */

public class JampackException extends Exception {
    public JampackException() {
        super();
    }

    public JampackException(String s) {
        super(s);
    }
}

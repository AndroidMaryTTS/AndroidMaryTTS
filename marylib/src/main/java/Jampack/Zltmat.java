package Jampack;

/**
 * Zltmat is a tag class of Zmat, which tells Jampack to expect a
 * lower triangular matrix.  The user is entirely responsible for the
 * matrix having the proper form, and Jampack programs do no checking.
 * For the constructors, see the corresponding constructors for <a
 * href="Zmat.html"> Zmat </a>.
 *
 * @author G. W. Stewart
 * @version Pre-alpha
 */


public class Zltmat extends Zmat {


    public Zltmat(double re[][], double im[][])
            throws JampackException {
        super(re, im);
    }

    public Zltmat(Z A[][]) {
        super(A);
    }

    public Zltmat(double A[][]) {
        super(A);
    }

    public Zltmat(Zmat A) {
        super(A);
    }

    public Zltmat(int nrow, int ncol) {
        super(nrow, ncol);
    }
}

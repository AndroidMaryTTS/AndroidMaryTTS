package lib.comirva.audio.Gaussian;

import lib.comirva.audio.PointList;


/**
 * <b>Covariance Singularity Exception</b>
 * <p/>
 * <p>Description: </p>
 * This class indicates that during the EM training algorithem of a Gaussian
 * Mixture Model one of the components got singular. A corrected list of points
 * meight be passed through this exception, such that one can rerun the EM
 * algorithm with the corrected list of points.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 * @see comirva.audio.util.gmm.GaussianMixture
 * @see comirva.audio.util.gmm.GaussianComponent
 */
public class CovarianceSingularityException extends Exception {
    PointList list = null;


    /**
     * Constructs a  <code>CovarianceSingularityException</code>.
     *
     * @param list PointList the correcte list of points, or null if there is no
     *             corrected list
     */
    public CovarianceSingularityException(PointList list) {
        super("Covariance matrix got singular;");
        this.list = list;
    }


    /**
     * Returns the corrected list of points, or null if no corrected list of
     * points is available.
     *
     * @return PointList the corrected list of points
     */
    public PointList getCorrectedPointList() {
        return list;
    }
}

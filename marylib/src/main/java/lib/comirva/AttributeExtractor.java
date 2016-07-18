package lib.comirva;


import java.io.IOException;

import lib.comirva.audio.Attribute;
import lib.comirva.audio.PointList;


/**
 * <b>Title: Attribute Extractor</b>
 * <p/>
 * <p>Description: </p>
 * This interface defines a very general attribute extractor. It seperates the
 * process of extracting an attribute from the representation of an attribute as
 * an object.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 * @see comirva.audio.feature.Attribute
 */
public interface AttributeExtractor {
    /**
     * This method encapsulates the attribute extraction process. The input
     * parameter is open, because of the very general design of
     * <code>AttributeExtractor</code> und <code>Attribute</code>. A concrete
     * class implementing this interface will however expect the input to be of
     * a certain type and will raise a <code>IllegalArgumentException</code>, if
     * the passed argument is not of the expected type.
     *
     * @param input Object an object representing the input data to extract the
     *              feature out of
     * @return Feature a feature extracted from the input data
     * @throws IOException              failures due to io operations are signaled by
     *                                  IOExceptions
     * @throws IllegalArgumentException raised if mehtod contract is violated,
     *                                  especially if the open input type is not
     *                                  of the expected type
     */
//    public Attribute calculate(Object input) throws IOException, IllegalArgumentException;
    Attribute calculate(Object input) throws IOException, IllegalArgumentException;

    KMeansClustering getKmean();

    PointList getMFCC();

    double EuclideDistance(PointList mfccCofficients, KMeansClustering KMeans);

    double EuclideDistanceUsePointList(PointList mfcc, PointList kmean);

    /**
     * Returns the type of the attribute that the class implementing this
     * interface will return as the result of its extraction process. By
     * definition this is the hash code of the attribute's class name.
     *
     * @return int an integer uniquely identifying the returned
     * <code>Attribute</code>
     */
    int getAttributeType();
}

package lib.comirva.audio.Gaussian;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Random;

import lib.comirva.audio.Matrix;
import lib.comirva.audio.PointList;


/**
 * <b>Gaussian Mixture Model</b>
 * <p/>
 * <p>Description</p>
 * This class implements in combination with <code>GaussianComponent</code> a
 * gaussian mixture model. To create a gaussian mixture model you have to
 * specify means, covariances and the weight for each of the gaussian components.
 * <code>GaussianMixture</code> and GaussianComponent do not only model a GMM,
 * but also support training GMMs using the EM algorithm.<br>
 * <br>
 * Take a look at <code>KMeansClustering</code> for initializing the GMM.<br>
 * <br>
 * One noteable aspect regarding the implementation is the fact that if
 * the covariance matrix of any of the components of this GMM gets singular
 * during the training process a <code>CovarianceSingularityException</code> is
 * thrown. The <code>CovarianceSingularityException</code> contains a reduced
 * <code>PointList</code>. All points belonging to the singular component have
 * been removed. So after the reduction one can try to rerun the training
 * algorithem with the reduced <code>PointList</code>.<br>
 * <br>
 * Another aspect of the design of this class was influenced by the limited
 * memory on real world computers. To improve performace a buffer to store some
 * estimations is used. This buffer is <i>static</i> to reduce garbage
 * collection time and all training processes are synchronized on this buffer.
 * Consequently one can only train one GMM instance at a time.<br>
 * <br>
 * <b>New in version 1.1:</b><br>
 * - The cholesky decomposition is used to speed up computations.
 *
 * @author Klaus Seyerlehner
 * @version 1.1
 * @see comirva.audio.util.kmeans.KMeansClustering
 * @see comirva.audio.util.gmm.GaussianComponent
 */
public final class GaussianMixture {
    private static final int MAX_ITERATIONS = 80;                                                 //defines the maximum number of training iterations
    private static double[][] p_ij = new double[1][1];                                            //hard referece to the buffer of current estimate
    private static SoftReference<double[][]> p_ij_SoftRef = new SoftReference<double[][]>(p_ij);  //soft reference to the buffer of current estimates
    private static Random rnd = new Random();                                                     //a random number generator
    private int dimension = 0;                                                                    //dimension of the gmm
    private GaussianComponent[] components = new GaussianComponent[0];                            //gaussian components


    /**
     * This constructor creats a GMM and checks the parameters for plausibility.
     * The weights, means and covarinces of every component are passed as arrays
     * to the constructor. The i-th component therefore is completely defined by
     * the i-th entries within these arrays.
     *
     * @param componentWeights double[] specifies the components weights
     * @param means            Matrix[] specifies the components mean vectors
     * @param covariances      Matrix[] specifies the components covariance matrices
     * @throws IllegalArgumentException if any invalid parameter settings are
     *                                  detected while checking them
     */
    public GaussianMixture(double[] componentWeights, Matrix[] means, Matrix[] covariances) throws IllegalArgumentException {
        //check if all parameters are valid
        if (componentWeights.length != means.length || means.length != covariances.length || componentWeights.length < 1)
            throw new IllegalArgumentException("all arrays must have the same length with size greater than 0;");

        //create component array
        components = new GaussianComponent[componentWeights.length];

        //check and create the components
        double sum = 0;
        for (int i = 0; i < components.length; i++) {
            if (means[i] == null || covariances[i] == null)
                throw new IllegalArgumentException("all mean and covarince matrices must not be null values;");

            sum += componentWeights[i];

            components[i] = new GaussianComponent(componentWeights[i], means[i], covariances[i]);
        }

        //check if the component weights are set correctly
        if (sum < 0.99 || sum > 1.01)
            throw new IllegalArgumentException("the sum over all component weights must be in the interval [0.99, 1.01];");

        //set dimension
        this.dimension = components[0].getDimension();

        //check if all the components have the same dimensions
        for (int i = 0; i < components.length; i++)
            if (components[i].getDimension() != dimension)
                throw new IllegalArgumentException("the dimensions of all components must be the same;");

    }

    /**
     * This constructor creates a GMM and checks the components for compatibility.
     * The components themselfs have been checked during their construction.
     *
     * @param components GaussianComponent[] an array of gaussian components
     * @throws IllegalArgumentException if the passed components are not
     *                                  compatible
     */
    public GaussianMixture(GaussianComponent[] components) throws IllegalArgumentException {
        if (components == null)
            throw new IllegalArgumentException("the component array must not be null;");

        //check the components
        double sum = 0;
        for (int i = 0; i < components.length; i++) {
            if (components[i] == null)
                throw new IllegalArgumentException("all components in the array must not be null;");

            sum += components[i].getComponentWeight();
        }

        //check if the component weights are set correctly
        if (sum < 0.99 || sum > 1.01)
            throw new IllegalArgumentException("the sum over all component weights must be in the interval [0.99, 1.01];");

        this.components = components;
        this.dimension = components[0].getDimension();

        //check if all the components have the same dimensions
        for (int i = 0; i < components.length; i++)
            if (components[i].getDimension() != dimension)
                throw new IllegalArgumentException("the dimensions of all components must be the same;");
    }


    /**
     * This constructior is for XML serialization only.
     */
    private GaussianMixture() {
    }

    /**
     * This method returns a reference to a buffer for storing estimates of the
     * sample points. The buffer will be reused if possible or reallocated, if
     * it is too small or if the garbage collector allready captured the buffer.
     *
     * @param nrComponents   int the number of components of the gmm to allocate the
     *                       buffer for
     * @param nrSamplePoints int the number of sample points of the gmm to
     *                       allocate the buffer for
     */
    protected static void getBuffer(int nrComponents, int nrSamplePoints) {
        //get the buffer from the soft ref => now hard ref
        p_ij = p_ij_SoftRef.get();

        if (p_ij == null) {
            //reallocate since gc collected the buffer
            p_ij = new double[nrComponents][2 * nrSamplePoints];
            p_ij_SoftRef = new SoftReference<double[][]>(p_ij);
        }

        //check if buffer is too small
        if (p_ij[0].length >= nrSamplePoints && p_ij.length >= nrComponents)
            return;

        //to prevent gc runs take double of the current buffer size
        if (p_ij[0].length < nrSamplePoints)
            nrSamplePoints += nrSamplePoints;

        //reallocate since buffer was too small
        p_ij = new double[nrComponents][nrSamplePoints];
        p_ij_SoftRef = new SoftReference<double[][]>(p_ij);

        //run gc to collect old buffer
        System.gc();
    }

    /**
     * Returns the log likelihood of the points stored in the pointlist under the
     * assumption the these points where sample from this GMM.<br>
     * <br>
     * [SUM over all j: log (SUM over all i:(p(x_j | C = i) * P(C = i)))]
     *
     * @param points PointList list of sample points to estimate the log
     *               likelihood of
     * @return double the log likelihood of drawing these samples from this gmm
     */
    public double getLogLikelihood(PointList points) {
        double p = 0;
        for (int j = 0; j < points.size(); j++)
            p += Math.log(getProbability(points.get(j)));
        return p;
    }

    /**
     * Draws a sample from this GMM.
     *
     * @return double[] a sample drawn from this distribution
     */
    public double[] nextSample() {
        double upper_boundary = 0;
        double value = rnd.nextDouble();

        for (int i = 0; i < components.length; i++) {
            upper_boundary += components[i].getComponentWeight();
            if (value < upper_boundary) {
                return components[i].nextSample();
            }
        }

        if (components.length - 1 >= 0)
            return components[components.length - 1].nextSample();
        else
            throw new IllegalStateException("gaussian components of this mixture not yet defined;");
    }

    /**
     * Returns the probability of a single sample point under the assumption that
     * it was draw from the distribution represented by this GMM.<br>
     * <br>
     * [SUM over all i:(p(x | C = i) * P(C = i))]
     *
     * @param x Matrix a sample point
     * @return double the probability of the given sample
     */
    public double getProbability(Matrix x) {
        double p = 0;

        for (int i = 0; i < components.length; i++)
            p += components[i].getWeightedSampleProbability(x);

        return p;
    }

    /**
     * Returns the number of dimensions of the GMM.
     *
     * @return int number of dimmensions
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Prints some information about this gaussian component.
     * This is for debugging purpose only.
     */
    public void print() {
        for (int i = 0; i < components.length; i++) {
            System.out.println("Component " + i + ":");
            components[i].print();
        }
    }

    /**
     * For testing purpose only.
     *
     * @param numberOfComponent int the number of the component
     * @return Matrix the mean vector
     */
    public Matrix getMean(int numberOfComponent) {
        return components[numberOfComponent].getMean();
    }


    /**
     * This method allows to read a GMM from a xml input stream as recommended by
     * the XMLSerializable interface.
     *
     * @see comirva.audio.XMLSerializable
     * @param parser XMLStreamReader the xml input stream
     * @return GaussianMixture the GMM read from the xml stream
     *
     * @throws IOException raised, if there are any io troubles
     * @throws XMLStreamException raised, if there are any parsing errors
     */
//  public static GaussianMixture readGMM(XMLStreamReader parser) throws IOException, XMLStreamException
//  {
//    GaussianMixture gmm = new GaussianMixture();
//    gmm.readXML(parser);
//    return gmm;
//  }

    /**
     * Runs the EM algorithem to train this GMM given the sample points in the
     * <code>PointList</code>. The EM algorithm runs till either one cannot yield
     * an improvment of the log likelihood or the number of training iterations
     * exceeds the number of maximal allowed iterations(MAX_ITERATIONS).
     *
     * @param samplePoints PointList sample points of the distribution to
     *                     approximate by this GMM
     * @throws CovarianceSingularityException thrown if any of the gaussian
     *                                        components got singular
     */
    public void runEM(PointList samplePoints) throws CovarianceSingularityException {
        double p_old = -Double.MAX_VALUE;
        double p = -Double.MAX_VALUE;
        int numberIterations = 0;

        synchronized (p_ij) {
            //resize buffers if necessary
            getBuffer(components.length, samplePoints.size());

            //improve modell until log likelihood doesn't improve significantly (change < 0,1)
            do {
                //remember old log likelihood
                p_old = p;

                //e-step
                estimationStep(samplePoints);

                //m-step
                maximizationStep(samplePoints);

                //increase number of iterations
                numberIterations++;

                //compute the log likelihood of the sample points
                p = getLogLikelihood(samplePoints);

                //pint something to the standard out to indicate that we are still working
                System.out.print("*");

            } while (p_old - p < -0.1 && numberIterations < MAX_ITERATIONS);

            //free buffer
            p_ij = new double[1][1];
        }
        System.out.println();
    }


    /**
     * This methode performs the estimation step by computing the probability
     * p_ij = P(C=i | x_j) for sample j of being generated by component i under
     * the assumption that this sample has been drawn from this GMM.<br>
     * <br>
     * This is done by using the Bayes rule to estimate this conditional
     * probability.<br>
     * <br>
     * Therefore "For all j: (SUM over i: P(C=i | x_j) = 1)" holds.
     *
     * @param samplePoints PointList sample points of the distribution to
     *                     approximate by this GMM
     */
    private void estimationStep(PointList samplePoints) {
        double p_j;
        double value;

        for (int j = 0; j < samplePoints.size(); j++) {
            //total probability for sample j generated by all components
            p_j = 0;

            //estimate the probability of sample j under the distribution of component i
            for (int i = 0; i < components.length; i++) {
                Matrix x = samplePoints.get(j);
                value = components[i].getWeightedSampleProbability(x);
                p_j += value;
                p_ij[i][j] = value;
            }

            //compute the probability p_ij for sample j to belong to component i
            for (int i = 0; i < components.length; i++)
                p_ij[i][j] /= p_j;
        }
    }


    /**
     * Try to change the GMM configuration such that the log likelihood of the
     * given sample points is improved under the new GMM configuration. This is
     * done using a gradient descent method.
     *
     * @param samplePoints PointList sample points of the distribution to
     *                     approximate by this GMM
     * @throws CovarianceSingularityException thrown if any of the gaussian
     *                                        components got singular
     */
    private void maximizationStep(PointList samplePoints) throws CovarianceSingularityException {
        int i = 0;

        try {
            //simply maximize each component
            for (i = 0; i < components.length; i++)
                components[i].maximise(samplePoints, p_ij[i]);
        } catch (CovarianceSingularityException cse) {
            //well, one of the components got singular
            PointList corrected = new PointList(samplePoints.getDimension(), samplePoints.size());

            //=> remove all the points of this component from the pointList
            for (int j = 0; j < samplePoints.size(); j++) {
                if (p_ij[i][j] < 0.95)
                    corrected.add(samplePoints.get(j).getColumnPackedCopy());
            }

            throw new CovarianceSingularityException(corrected);
        }
    }


    /**
     * Writes the xml representation of this object to the xml ouput stream.<br>
     * <br>
     * There is the convetion, that each call to a <code>writeXML()</code> method
     * results in one xml element in the output stream.
     *
     * @param writer XMLStreamWriter the xml output stream
     *
     * @throws IOException raised, if there are any io troubles
     * @throws XMLStreamException raised, if there are any parsing errors
     */
  /*public void writeXML(XMLStreamWriter writer) throws IOException, XMLStreamException
  {
    writer.writeStartElement("gmm");

    for(int i = 0; i < components.length; i++)
      components[i].writeXML(writer);

    writer.writeEndElement();
  }
*/

    /**
     * Reads the xml representation of an object form the xml input stream.<br>
     * <br>
     * There is the convention, that <code>readXML()</code> starts parsing by
     * checking the start tag of this object and finishes parsing by checking the
     * end tag. The caller has to ensure, that at method entry the current token
     * is the start tag. After the method call it's the callers responsibility to
     * move from the end tag to the next token.
     *
     * @param parser XMLStreamReader the xml input stream
     *
     * @throws IOException raised, if there are any io troubles
     * @throws XMLStreamException raised, if there are any parsing errors
     */
 /* public void readXML(XMLStreamReader parser) throws IOException, XMLStreamException
  {
    List componentList = new LinkedList();
    GaussianComponent newComponent;

    parser.require(XMLStreamReader.START_ELEMENT, null, "gmm");
    parser.next();

    while(parser.isStartElement())
    {
      newComponent = GaussianComponent.readGC(parser);
      componentList.add(newComponent);

      parser.next();
    }

    components = new GaussianComponent[componentList.size()];

    for(int i = 0; i < componentList.size(); i++)
      components[i] = (GaussianComponent) componentList.get(i);

    if(components[0] != null)
      this.dimension = components[0].getDimension();

    parser.require(XMLStreamReader.END_ELEMENT, null, "gmm");
  }
*/
}

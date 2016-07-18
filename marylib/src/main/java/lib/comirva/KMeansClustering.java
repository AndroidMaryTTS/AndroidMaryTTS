package lib.comirva;

import java.util.Random;

import lib.comirva.audio.Matrix;
import lib.comirva.audio.PointList;


/**
 * This class implements a simle version of the <i>k-Means Clustering</i>
 * algorithm. A given list of points in a n-dimensional space will be grouped in
 * a specified number of clusters.<br>
 * <br>
 * To measure the distance between two points in the n-dimensional space the
 * euclidian distance is used.<br>
 * <br>
 * The clusters are initialized by randomly choosing one point of the given
 * point list for each cluster. Then the algorithem runs till there is no more
 * improvement to the <i>mean quantisation error (MQE)</i>.
 * <br>
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 * @see comirva.audio.util.kmeans.InvalidClusteringException
 * @see comirva.audio.util.PointList
 */
public class KMeansClustering {
    //static fields
    protected static Random rnd = new Random();

    //fields
    protected int maxIterations;                //maximal number of iterations till the algorithm stops
    protected int numberClusters;               //number of clusters to separate the data-points into
    protected int dimension;                    //dimension of the vector space
    protected boolean normalize;                //true if data-points have to be normalized before processing
    protected PointList data;                   //list of points in the n-dimensional space
    protected Cluster[] clusters;               //array holding the clusters
    protected Matrix[] covariances;             //array holding the covariance matrices
    protected boolean existsClustering = false; //flag indicating, if clustering exists

    //implementation details
    private Matrix mean;            //mean of each coordinate
    private Matrix variance;        //variance of each coordinate
    private Matrix stdDeviation;    //standard deviation of each coordinate


    public KMeansClustering(int numberClusters, PointList pl) throws IllegalArgumentException {
        this(numberClusters, pl, false, 80);
    }

    /**
     * Constructs an object that allows to separate a point list into k clusters.
     * By default maximal number of iterations is 20.
     *
     * @param numberClusters int number of clusters to separate the points into
     * @param pl             PointList a list of points in the n-dimensional space
     * @param normalize      boolean true if data-points should be normalized before
     *                       processing, otherwise false;
     * @throws IllegalArgumentException raised if mehtod contract is violated
     */
    public KMeansClustering(int numberClusters, PointList pl, boolean normalize) throws IllegalArgumentException {
        this(numberClusters, pl, normalize, 80);
    }


    /**
     * Constructs an object that allows to separate a point list into k clusters.
     * By default maximal number of iterations is 20.
     *
     * @param numberClusters int number of clusters to separate the points into
     * @param pl             PointList a list of points in the n-dimensional space
     * @param normalize      boolean true if data-points should be normalized before
     *                       processing, otherwise false;
     * @param maxIterations  int maximal number of iterations the algorithem should
     *                       perform
     * @throws IllegalArgumentException raised if mehtod contract is violated
     */
    public KMeansClustering(int numberClusters, PointList pl, boolean normalize, int maxIterations) throws IllegalArgumentException {
        //check parameters
        if (numberClusters < 1)
            throw new IllegalArgumentException("the number of clusters to find must be at leat one;");
        if (maxIterations < 1)
            throw new IllegalArgumentException("the maximal number of iterations must be at least one;");
        if (pl == null)
            throw new IllegalArgumentException("the given point list must not be a null value;");

        //initialize fields
        this.numberClusters = numberClusters;
        this.dimension = pl.getDimension();
        this.clusters = new Cluster[numberClusters];
        this.normalize = normalize;
        this.maxIterations = maxIterations;

        //get mean, varaince and standard deviation of the data
        this.mean = pl.getMean();
        this.variance = pl.getVariance();
        this.stdDeviation = variance.pow(0.5d);

        //eventually normalize the data points
        if (normalize)
            this.data = pl.normalize();
        else
            this.data = pl;

        //randomly select the a point to be the center of each cluster
        int[] startPoints = new int[numberClusters];
        for (int i = 0; i < numberClusters; ) {
            startPoints[i] = rnd.nextInt(pl.size());
            for (int j = 0; j < i; j++) {
                if (startPoints[j] == startPoints[i])
                    i--;
            }
            i++;
        }

        //create the specified number of clusters
        for (int i = 0; i < numberClusters; i++)
            clusters[i] = new Cluster(this.data.get(startPoints[i]));
    }

    /**
     * Constructs an object that allows to separate a point list into k clusters.
     * By default maximal number of iterations is 20 and normalization is turned off.
     *
     * @param numberClusters int number of clusters to separate the points into
     * @param pl             PointList a list of points in the n-dimensional space
     * @throws IllegalArgumentException raised if method contract is violated
     */
    public int getDimension() {
        return this.getMean(0).getRowDimension();
    }

    /**
     * Starts the k-mean clustering algorithm. The cluster centers are recomputed
     * till the mean quanisation error(MQE) stops decreasing.
     */
    public void run() {
        double MQE = 0.99d * Double.MAX_VALUE; //the mean quanisation error of the current clustering
        double oldMQE = Double.MAX_VALUE;      //the mean quantiation error of the last clustering
        double minDistance;                    //to remember the minimal distance up to now
        int clusterIndex = 0;                  //the index of the cluster with minimal distance
        Matrix curPoint;                       //the point, which we actually process
        int i = 0;                             //number of current iteration

        while (MQE < oldMQE && i < maxIterations) {
            //store old mean quantisation error
            oldMQE = MQE;
            MQE = 0;

            //ajust cluster centers and reset clusters (except in the first round)
            if (i != 0) {
                for (int n = 0; n < numberClusters; n++)
                    clusters[n].reset(clusters[n].getMeanOfElements());
            }

            //compute the new clustering
            for (int k = 0; k < data.size(); k++) {
                //get the next point
                curPoint = data.get(k);
                minDistance = Double.MAX_VALUE;

                //compare the point to each cluster center
                for (int j = 0; j < numberClusters; j++) {
                    double d = clusters[j].getDistanceFromCenter(curPoint);
                    if (d < minDistance) {
                        minDistance = d;
                        clusterIndex = j;
                    }
                }

                //increase mean quantisation error
                MQE += minDistance;
                //add the point to the cluster with minimal distance
                clusters[clusterIndex].add(curPoint);
            }

            i++;
        }

        //create covariance matrices
        createFullCovarianceMatrices();

        //clustering exists
        existsClustering = true;
    }


    /**
     * Creats the full covariance matrices.
     */
    protected void createFullCovarianceMatrices() {
        Matrix curPoint = null;
        Matrix[] centers;
        Matrix diff;
        double[][][] clusterElements = null;
        int[] numberElements;
        double minDistance = 0.0d;
        int clusterIndex = 0;

        //allocate matrices
        this.covariances = new Matrix[clusters.length];
        centers = new Matrix[clusters.length];
        numberElements = new int[clusters.length];
        clusterElements = new double[clusters.length][][];
        for (int i = 0; i < clusters.length; i++) {
            clusterElements[i] = new double[clusters[i].getNumberOfElements()][dimension];
            centers[i] = clusters[i].getCenter();
        }

        //add the differences to the specific matrices
        for (int k = 0; k < data.size(); k++) {
            //get the next point
            curPoint = data.get(k);
            minDistance = Double.MAX_VALUE;

            //compare the point to each cluster center
            for (int j = 0; j < numberClusters; j++) {
                double d = clusters[j].getDistanceFromCenter(curPoint);
                if (d < minDistance) {
                    minDistance = d;
                    clusterIndex = j;
                }
            }

            //add the diff to the specifc matrix
            diff = centers[clusterIndex].minus(curPoint);
            clusterElements[clusterIndex][numberElements[clusterIndex]] = diff.getColumnPackedCopy();
            numberElements[clusterIndex]++;
        }

        //compute covariance matrix
        for (int i = 0; i < numberClusters; i++) {
            covariances[i] = new Matrix(clusterElements[i], numberElements[i], dimension);
            covariances[i] = (covariances[i].transpose().times(covariances[i])).times(1.0d / numberElements[i]);

            if (covariances[i].rank() < dimension) {
                //Arbitrary width used if variance collapses to zero: make it 'large' so
                //that centre is responsible for a reasonable number of points.
                double GMM_WIDTH = 1.0d;

                //add GMM_WIDTH*Identity to rank-deficient covariance matrices
                covariances[i] = covariances[i].plus(Matrix.identity(dimension, dimension).times(GMM_WIDTH));
            }
//      System.out.println(numberClusters);
//      System.out.println(i);
        }

    }


    /**
     * Returns the number of clusters to separate the given data points into.
     *
     * @return int number of clusters
     */
    public int getNumberClusters() {
        return numberClusters;
    }


    /**
     * Returns the mean of the specifed cluster.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @param cluster int the number of cluster to get the mean of
     * @return Matrix the mean vector of the cluster
     */
    public Matrix getMean(int cluster) {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        Matrix y = clusters[cluster].getMeanOfElements();

        //correct mean according to linear normalization transformation
        if (normalize) {
            y.arrayTimesEquals(stdDeviation);
            y.plusEquals(mean);
        }

        return y;
    }


    /**
     * Returns the mean vectors of all clusters in one array.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @return Matrix[] array containing the mean vectors
     */
    public Matrix[] getMeans() {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        Matrix[] m = new Matrix[numberClusters];

        for (int i = 0; i < numberClusters; i++)
            m[i] = getMean(i);

        return m;
    }


    /**
     * Returns the full covariance matrix of the specified cluster.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @param cluster int the number of the cluster to get the covariance matrix of
     * @return Matrix the covariance matrix of the cluster
     */
    public Matrix getFullCovarianceMatrix(int cluster) {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        return covariances[cluster];
    }


    /**
     * Returns the full covaraince matrices of all clusters in one array.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @return Matrix[] array containing the covaraince matrices
     */
    public Matrix[] getFullCovariances() {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        return covariances;
    }


    /**
     * Returns the diagonal covariance matrix of the specified cluster.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @param cluster int the number of the cluster to get the covariance matrix of
     * @return Matrix the covariance matrix of the cluster
     */
    public Matrix getDiagCovarianceMatrix(int cluster) {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        Matrix v = clusters[cluster].getVarianceOfElements();

        //correct variance according to linear normalization transformation
        if (normalize)
            v.arrayTimesEquals(variance);

        //compute diagonal covariance matrix
        Matrix id = Matrix.identity(dimension, dimension);
        for (int i = 0; i < dimension; i++)
            id.set(i, i, v.get(i, 0));

        return id;
    }


    /**
     * Returns the diagonal covaraince matrices of all clusters in one array.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @return Matrix[] array containing the covaraince matrices
     */
    public Matrix[] getDiagCovariances() {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        Matrix[] cov = new Matrix[numberClusters];

        for (int i = 0; i < numberClusters; i++)
            cov[i] = getDiagCovarianceMatrix(i);

        return cov;
    }


    /**
     * Returns the weight of the specified cluster.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @param cluster int the number of cluster to get the weight of
     * @return double the weight of this cluster
     */
    public double getClusterWeight(int cluster) {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        return ((double) clusters[cluster].N) / (double) data.size();
    }


    /**
     * Retruns the weights of all clusters in one array.<br>
     * <br>
     * <b>Note:</b> Before calling this method there should exist a vaild
     * clustering, which one can create by calling the <code>run()</code> method.
     *
     * @return double[] array containing the weights
     */
    public double[] getClusterWeights() {
        //check if clustering exists
//    if(!existsClustering)
//      throw new InvalidClusteringException("there is no clustering yet;");

        double[] weights = new double[numberClusters];

        for (int i = 0; i < numberClusters; i++)
            weights[i] = getClusterWeight(i);

        return weights;
    }


    /**
     * For debugging purpose only.
     */
    public void print() {
        for (int i = 0; i < clusters.length; i++)
            clusters[i].print();
    }


    /**
     * A set of homogen points in the n-dimensional space is represented by a
     * <code>Cluster</code> object.
     *
     * @author Klaus Seyerlehner
     * @version 1.0
     */
    protected class Cluster {
        //fields
        protected int N;             //number of elements in this cluster
        protected Matrix center;       //center of this cluster

        //implementation details
        private Matrix sum;        //sum over all elements in this cluster
        private Matrix squaredSum; //squared sum over all elements in this cluster


        /**
         * Constructs a new cluster.
         *
         * @param mean Matrix the cluster center of this new cluster.
         */
        public Cluster(Matrix mean) {
            reset(mean);
        }


        /**
         * Returns the euclidian distance of a point x to the cluster center.
         *
         * @param x Matrix point in the n-dimensional space
         * @return double  distance to the cluster center
         */
        public double getDistanceFromCenter(Matrix x) {
            Matrix diff = center.minus(x);
            return diff.transpose().times(diff).get(0, 0);
        }


        /**
         * Adds a point x to this cluster.
         *
         * @param x Matrix o point in the n-dimensional space
         */
        public void add(Matrix x) {
            sum.plusEquals(x);
            squaredSum.plusEquals(x.pow(2.0d));
            N++;
        }


        /**
         * Returns the mean of all the elements in this cluster.
         *
         * @return Matrix mean of the elements in this cluster
         */
        public Matrix getMeanOfElements() {
            return sum.times(1.0d / N);
        }


        /**
         * Returns the variance of all the elements in this cluster.
         *
         * @return Matrix variance of the elements in this cluster
         */
        public Matrix getVarianceOfElements() {
            return squaredSum.times(1.0d / N).minus(getMeanOfElements().pow(2.0));
        }


        /**
         * Resets all internal values of this cluster. Additionally the cluster
         * center is set to the given value.
         *
         * @param newCenter Matrix
         */
        public void reset(Matrix newCenter) {
            this.center = newCenter;
            this.N = 0;
            this.sum = new Matrix(center.getRowDimension(), center.getColumnDimension());
            this.squaredSum = new Matrix(center.getRowDimension(), center.getColumnDimension());
        }


        /**
         * Returns the number of elements in this cluster.
         *
         * @return int number of elements in this cluster
         */
        public int getNumberOfElements() {
            return N;
        }


        /**
         * Returns the cluster center.
         *
         * @return Matrix the cluster center
         */
        public Matrix getCenter() {
            return center;
        }


        /**
         * Prints some information about this cluster. This is for debugging purpose
         * only.
         */
        public void print() {
            System.out.println("-----");
            center.print(10, 10);
        }
    }
}

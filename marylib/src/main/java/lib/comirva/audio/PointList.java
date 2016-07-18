package lib.comirva.audio;


/**
 * <b>PointList</b>
 * <p/>
 * <p>Description: </p>
 * Represents a list of points in an n-dimensional space. The coordinates of a
 * point are modeled as double values and the point is stored as an object of
 * type <code>Matrix</code>.
 * From the implementation point of view the list is managed by using a
 * dynamically growing array. So <code>PointList</code> is closely related to
 * <code>java.util.ArrayList</code>.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class PointList {
    //fields
    protected int dimension;       //dimensions of the vector space
    protected int numberElements;  //number of elements currently in the list
    protected Matrix[] data;       //array holding the data-points

    //implementation details
    private double[] min;        //array of minimum values for each coordinate
    private double[] max;        //array of maximum values for each coordinate
    private Matrix sum;          //sum over all elements in this list
    private Matrix squaredSum;   //squared sum over all elements in this list


    /**
     * Creates a new point list with default capacity.
     *
     * @param dimension int initial capacity of the array holding the points; must
     *                  be at least one
     * @throws IllegalArgumentException raised if mehtod contract is violated
     */
    public PointList(int dimension) throws IllegalArgumentException {
        this(dimension, 16384);
    }


    /**
     * Creates a new point list.
     *
     * @param dimension int dimension of the vector space; must be at least one
     * @param capacity  int initial capacity of the array holding the points must
     *                  be at least one
     * @throws IllegalArgumentException raised if mehtod contract is violated
     */
    public PointList(int dimension, int capacity) throws IllegalArgumentException {
        //check paramters
        if (dimension < 1)
            throw new IllegalArgumentException("the dimension must be at least one");
        if (capacity < 1)
            throw new IllegalArgumentException("the initial capacity must be at least one");

        //initialize fields
        this.data = new Matrix[capacity];
        this.dimension = dimension;
        this.min = new double[dimension];
        this.max = new double[dimension];
        this.sum = new Matrix(dimension, 1);
        this.squaredSum = new Matrix(dimension, 1);
    }


    /**
     * Adds a data-point to the list.
     *
     * @param point double[] the data point
     * @throws IllegalArgumentException
     */
    public void add(double[] point) throws IllegalArgumentException {
        //check the point
        if (point == null || point.length != dimension)
            throw new IllegalArgumentException("data point must not be a null value and dimension must agree");

        //keep track over minimum and maximum values
        for (int i = 0; i < dimension; i++) {
            if (point[i] > max[i])
                max[i] = point[i];
            if (point[i] < min[i])
                min[i] = point[i];
        }

        //create matrix object for this point
        Matrix x = new Matrix(point, dimension);

        //update sum and squared sum
        sum.plusEquals(x);
        squaredSum.plusEquals(x.pow(2.0d));

        //add the point to the list
        data[numberElements] = x;
        numberElements++;

        //ajust data array, if necessary
        if (numberElements == data.length)
            resize();
    }


    /**
     * Returns the minimum value for each coordinat observed up to now.
     *
     * @return double[] array containing the minimum values; the first element is
     * the minimum of the first coordinat observed, etc.
     */
    public double[] getMinPoint() {
        return min;
    }


    /**
     * Returns the maximum value for each coordinat observed up to now.
     *
     * @return double[] array containing the maximum values; the first element is
     * the maximum of the first coordinat observed, etc.
     */
    public double[] getMaxPoint() {
        return max;
    }


    /**
     * Returns the dimension of the points/vectors in this list.
     *
     * @return int dimension of the points in this list
     */
    public int getDimension() {
        return dimension;
    }


    /**
     * Returns the number of points in this list.
     *
     * @return int
     */
    public int size() {
        return numberElements;
    }


    /**
     * Returns the i-th point in ths list.
     *
     * @param i int must be a vaild index
     * @return Matrix the point encapsulated in a Matrix object
     * @throws IndexOutOfBoundsException if i is no a valid index
     */
    public Matrix get(int i) throws IndexOutOfBoundsException {
        return data[i];
    }


    /**
     * Returns a vector containing the means of each coordinate.
     *
     * @return Matrix mean vector
     */
    public Matrix getMean() {
        return sum.times(1.0d / numberElements);
    }


    /**
     * Returns a vector containing the variance of each coordinate.
     *
     * @return Matrix variance vector
     */
    public Matrix getVariance() {
        return squaredSum.times(1.0d / numberElements).minus(getMean().pow(2.0));
    }


    /**
     * Returns a new <code>PointList</code> containing normalized points. The
     * normalization is done for every coordinate by subtracting the mean and
     * dividing by the standard deviation. This is also known as z-normalization.
     * The original point list is unchanged.
     *
     * @return PointList the original point list
     */
    public PointList normalize() {
        //get mean, varaince and standard deviation for normalization
        Matrix mean = getMean();
        Matrix variances = getVariance();
        Matrix stdDeviation = variances.pow(0.5d);

        //create point list to return
        PointList normalized = new PointList(dimension);

        //normalize all points and add them to the new list
        for (int i = 0; i < numberElements; i++) {
            Matrix x = data[i];
            x = x.minus(mean);
            x.arrayRightDivideEquals(stdDeviation);
            normalized.add(x.getColumnPackedCopy());
        }

        return normalized;
    }


    /**
     * This method is for debugging purpose only. The complete list will be
     * printed using the standard output stream.
     */
    public void print() {
        System.out.println("{");
        for (int i = 0; i < data.length; i++) {
            Matrix x = data[i];
            System.out.print("{");
            for (int j = 0; j < dimension; j++) {
                if (j < dimension - 1)
                    System.out.print(x.get(j, 0) + ",");
                else
                    System.out.print(x.get(j, 0));
            }
            if (i < data.length - 1)
                System.out.println("},");
            else
                System.out.println("}");
        }
        System.out.println("}");
    }


    /**
     * Resizes the internal array holding all the point matrices. The size of the
     * array after the call will be twice the current size. All elements of the
     * current array will be copied.
     */
    private void resize() {
        Matrix[] newData = new Matrix[data.length * 2];

        for (int i = 0; i < data.length; i++)
            newData[i] = data[i];

        data = newData;
    }
}

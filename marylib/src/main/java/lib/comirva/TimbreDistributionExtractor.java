package lib.comirva;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

import lib.comirva.audio.Attribute;
import lib.comirva.audio.Gaussian.GaussianMixture;
import lib.comirva.audio.Matrix;
import lib.comirva.audio.PointList;
import lib.comirva.audio.TimbreDistribution;
import lib.sound.sampled.AudioInputStream;


/**
 * <b>Timbre Distribution Extractor</b>
 * <p/>
 * <p>
 * Description:
 * </p>
 * This class supports the extraction of the "Timbre Distribution" summarizing
 * the timbre of an audio stream.<br>
 * <br>
 * This is done by computing the MFCC for each audio frame(usually between 20ms
 * and 50ms and a 50% overlap). The MFCCs are known to somehow characterize the
 * timbre of such a short audio frame. Then one estimates the distribution of
 * the MFCC vectors using a Gaussian Mixture Model.<br>
 * <br>
 * The resulting distribution is a model of the song's overall timbre and can be
 * compared to other timbre models.
 * <p/>
 * <p/>
 * [1] Aucouturier, Pachet, "Improving Timbre Similarity: How high's the sky?"
 * Journal of Negative Results in Speech and Audio Sciences, 1(1), 2004.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 * @see comirva.audio.util.gmm.GaussianMixture
 * @see comirva.audio.util.MFCC
 * @see comirva.audio.feature.TimbreDistribution
 */
public class TimbreDistributionExtractor implements AudioFeatureExtractor {
    // so k-partition cua KmeanClustering
    public int DEFAULT_NUMBER_COMPONENTS = 3; // default number of components to
    // use for the gmm 3
    public int skipIntroSeconds = 0; // number of seconds to skip at the
    // beginning of the song 30
    public int skipFinalSeconds = 0; // number of seconds to skip at the end of
    // the song 30
    public int minimumStreamLength = 1; // minimal number of seconds of audio
    // data to return a vaild result 30

    protected AudioPreProcessor preProcessor;
    protected MFCC mfcc;
    protected int numberGaussianComponents = DEFAULT_NUMBER_COMPONENTS;
    protected KMeansClustering kmean;
    protected PointList mfccPointList;

    /**
     * The default constructor uses 3 gaussian components for modelling the
     * timbre distribution. For more details on the default MFCC computation
     * take a look at the <code>MFCC</code> documentation. The
     * <code>AudioPreProcessors<code>
     * default sample rate is used.
     *
     * @see comirva.audio.util.MFCC
     * @see comirva.audio.util.AudioPreProcessor
     */

    public TimbreDistributionExtractor() {
        this.mfcc = new MFCC(AudioPreProcessor.DEFAULT_SAMPLE_RATE);
        // this.mfcc = new MFCC(11025.0f);
    }

    /**
     * This constructor in contrast to the default constructor allows to specify
     * the number of gaussian components used for modelling the timbre
     * distribution.
     *
     * @param numberGaussianComponents int number of gaussian components
     * @param skipIntro                int number of seconds to skip at the beginning of the song
     * @param skipEnd                  int number of seconds to skip at the end of the song
     * @param minimumLength            int minimum length required for processing
     */
    public TimbreDistributionExtractor(int numberGaussianComponents,
                                       int skipIntro, int skipEnd, int minimumLength) {
        this.mfcc = new MFCC(AudioPreProcessor.DEFAULT_SAMPLE_RATE);
        // this.mfcc = new MFCC(11025.0f);

        if (numberGaussianComponents < 1 || skipIntro < 0 || skipEnd < 0
                || minimumStreamLength < 1)
            throw new IllegalArgumentException("illegal parametes;");

        this.numberGaussianComponents = numberGaussianComponents;
        this.skipIntroSeconds = skipIntro;
        this.skipFinalSeconds = skipEnd;
        this.minimumStreamLength = minimumLength;
    }

    // //////////////////////////////////////////////////////////////
    public static byte[] getBytesFromInputStream(InputStream inStream)
            throws IOException {

        // Get the size of the file
        long streamLength = inStream.available();
        if (streamLength > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) streamLength];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = inStream.read(bytes, offset, bytes.length
                - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file ");
        }

        // Close the input stream and return bytes
        inStream.close();
        return bytes;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    protected void convertToDouble1(byte[] in, int len, double[] out, int start) {
        boolean isBigEndian = false;
        int db = 0;
        int frameSize = 2;
        double scale = 1.9255290053716827;
        if (frameSize == 1) {
            // 8-bit signed PCM
            for (int i = 0; i < len; i += frameSize)
                out[start++] = ((double) in[i]) * scale;
        } else {
            // more than one byte per sample
            if (isBigEndian) {
                // the bytes of one sample value are in big-Endian order
                // first byte will be converted to int with respect to the sign
                db = (int) in[0];
                for (int i = 1, j = frameSize; i < len; i++) {
                    if (i == j)// finished one sample?
                    {
                        // convert to double value (and rescale for
                        // compatibility to the matlab implementation)
                        out[start++] = ((double) db) * scale;
                        // first byte will be converted to int with respect to
                        // the sign
                        db = (int) in[i];
                        // set the end of the next sample value
                        j += frameSize;
                    } else {
                        // combine the bytes of the sample (unsigned bytes)
                        db = db << 8 | ((int) in[i] & 0xff);
                    }
                }
                // convert to double value (and rescale for compatibility to the
                // matlab implementation)
                out[start++] = ((double) db) * scale;
            } else {
                // the bytes of one sample value are in little-Endian order
                for (int i = 0; i < len; i += frameSize) {
                    // first byte will be converted to int with respect to the
                    // sign
                    db = (int) in[i + frameSize - 1];

                    // combine the bytes of the sample (unsigned bytes) in
                    // reversed order
                    for (int b = frameSize - 2; b >= 0; b--)
                        db = db << 8 | ((int) in[i + b] & 0xff);

                    // convert to double value (and rescale for compatibility to
                    // the matlab implementation)
                    out[start++] = ((double) db) * scale;
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////////

    public KMeansClustering getKmean() {
        return kmean;
    }

    public void setKmean(KMeansClustering kmean) {
        this.kmean = kmean;
    }

    public PointList getMFCC() {
        return mfccPointList;
    }

    public void setMFCC(PointList mfcc) {
        this.mfccPointList = mfcc;
    }

	/*
     * (non-Javadoc)
	 * @see thesis.lib.comirva.AttributeExtractor#EuclideDistance(thesis.lib.comirva.audio.PointList, thesis.lib.comirva.KMeansClustering)
	 * @param  PointList, KmeansClustering
	 * 
	 */

    public double EuclideDistance(PointList mfccCofficients,
                                  KMeansClustering KMeans) {
        int kmeanElements = KMeans.getNumberClusters();
        int kmeanDimension = KMeans.getDimension();
        int mfccDimension = mfccCofficients.getDimension();
        int mfccElements = mfccCofficients.size();
        System.out.println("MFCC Elements: " + mfccElements);
        Matrix arrayDistance = new Matrix(1, mfccElements);
        for (int j = 0; j < mfccElements; j++) {
            double minMfcc2Kmean = 999999999; // infinity
            for (int i = 0; i < kmeanElements; i++) {
                double tempDistance = EuclideDistanceRows(
                        mfccCofficients.get(j), KMeans.getMean(i));
                // System.out.println("1st " + tempDistance);
                if (tempDistance < minMfcc2Kmean)
                    minMfcc2Kmean = tempDistance;
            }
            arrayDistance.set(0, j, minMfcc2Kmean);
        }
        double sumDistance = 0;
        for (int k = 0; k < mfccElements; k++) {
            sumDistance += arrayDistance.get(0, k);
        }
        return (sumDistance / mfccElements);
    }

    public double EuclideDistanceRows(Matrix mfccElement, Matrix kmeanElement) {
        int mfccDimension = mfccElement.getRowDimension();
        int kmeanDimension = kmeanElement.getRowDimension();
        // System.out.println("MFCC " + mfccDimension + "KM" + kmeanDimension);
        double distance = 0;
        for (int i = 0; i < mfccDimension; i++) {
            distance += Math.pow(
                    mfccElement.get(i, 0) - kmeanElement.get(i, 0), 2);
        }
        return distance;
    }

    /**
     * This method is used to calculate the timbre distribution for a whole
     * song. The song must be handed to this method as an
     * <code>AudioPreProcessor</code> object. All settings are set by the
     * constructor, so this method can easily be called for a large number of
     * songs to extract this feature.
     *
     * @param input Object an object representing the input data to extract the
     *              feature out of
     * @return Feature a feature extracted from the input data
     * @throws IOException              failures due to io operations are signaled by IOExceptions
     * @throws IllegalArgumentException raised if mehtod contract is violated, especially if the open
     *                                  input type is not of the expected type
     */
    public Attribute calculate(Object input)
            throws IOException, IllegalArgumentException {
        GaussianMixture gmm = null;

        // check input type
        if (input == null || !(input instanceof AudioInputStream))
            throw new IllegalArgumentException(
                    "input type for the td feature extraction process should be AudioPreProcessor and must not be null");
        else
            preProcessor = new AudioPreProcessor((AudioInputStream) input);

        // skip the intro part
        preProcessor.fastSkip((int) AudioPreProcessor.DEFAULT_SAMPLE_RATE
                * skipIntroSeconds);

        // pack the mfccs into a pointlist
        Vector mfccCoefficients = mfcc.process(preProcessor);
        System.out.println("test " + mfccCoefficients.size());

        // check if element 0 exists
        if (mfccCoefficients.size() == 0)
            throw new IllegalArgumentException(
                    "the input stream is to short to process;");


        // create a point list with appropriate dimensions
        // mfccCoefficients la ma tran chua 20 he so MFCCs
        double[] point = (double[]) mfccCoefficients.get(0);
        // ///////////////////////////////////////////////////////

        PointList pl = new PointList(point.length);

        // fill pointlist and remove the last samples
        int skip = (int) ((skipFinalSeconds * preProcessor.getSampleRate()) / (mfcc
                .getWindowSize() / 2));

        // khong bo bat ky doan am thanh nao
        skip = 0;
        for (int i = 0; i < mfccCoefficients.size() - skip; i++) {
            point = (double[]) mfccCoefficients.get(i);
            pl.add(point);
        }

        // check if the resulting point list has the required minimum length
        if (pl.size() < ((minimumStreamLength * preProcessor.getSampleRate()) / (mfcc
                .getWindowSize() / 2)))
            throw new IllegalArgumentException(
                    "the input stream ist to short to process;");

        KMeansClustering kmeans = new KMeansClustering(16, pl, false);
        kmeans.run();
        this.setKmean(kmeans);
        this.setMFCC(pl);

        // run EM algorithem for gaussian mixture model
        gmm = new GaussianMixture(kmeans.getClusterWeights(),
                kmeans.getMeans(), kmeans.getFullCovariances());

        return new TimbreDistribution(gmm);
    }

    /**
     * Returns the type of the attribute that the class implementing this
     * interface will return as the result of its extraction process. By
     * definition this is the hash code of the attribute's class name.
     *
     * @return int an integer uniquely identifying the returned
     * <code>Attribute</code>
     */
    public int getAttributeType() {
        return TimbreDistribution.class.getName().hashCode();
    }

    /**
     * Returns the feature extractors name.
     *
     * @return String name of this feature extractor
     */
    public String toString() {
        return "Timbre Distribution";
    }

    /*
     * Calculate distance between 2 PointList
     * @param PointList, PointList
     */
    public double EuclideDistanceUsePointList(PointList mfcc, PointList kmean) {
        int mfccElements = mfcc.size();
        int kmeanElements = kmean.size();
        Matrix arrayDistance = new Matrix(1, mfccElements);

        for (int j = 0; j < mfccElements; j++) {
            double minMfcc2Kmean = 999999999; // infinity
            for (int i = 0; i < kmeanElements; i++) {
                double tempDistance = EuclideDistanceRows(
                        mfcc.get(j), kmean.get(i));
                if (tempDistance < minMfcc2Kmean)
                    minMfcc2Kmean = tempDistance;
            }
            arrayDistance.set(0, j, minMfcc2Kmean);
        }
        double sumDistance = 0;
        for (int k = 0; k < mfccElements; k++) {
            sumDistance += arrayDistance.get(0, k);
        }
        return (sumDistance / mfccElements);
    }
}

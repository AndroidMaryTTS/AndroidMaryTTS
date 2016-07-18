package lib.comirva.audio;

import java.io.IOException;

import lib.comirva.audio.Gaussian.GaussianMixture;


/**
 * <b>Timbre Distribution</b>
 * <p/>
 * <p>Description: </p>
 * Represents a similarity measure based on the song's timbre. The distribution
 * of the MFCCs of a song is used to summerize the song. Songs having similliar
 * distributions are considert simillar.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 * @see comirva.audio.extraction.TimbreDistributionExtractor
 */
public class TimbreDistribution extends AudioFeature {
    private final int DSR = 2000;                               //distance sample rate
    private StrongLimitedReference<PointList> sampleListRef;    //soft reference to samples drawn from the gmm
    private GaussianMixture gmm;                                //the gmm summarizing this song
    private double logLikelihood = Double.NaN;                  //log likelihood of the samples drawn from the gmm


    /**
     * Constructs a <code>TimbreDistribution</code> feature.
     *
     * @param gmm GaussianMixture the gmm summarizing the song
     */
    public TimbreDistribution(GaussianMixture gmm) {
        super();
        this.gmm = gmm;
    }


    /**
     * Used for xml serialization only.
     */
    protected TimbreDistribution() {
        super();
    }


    /**
     * Returns the log likelihood for a given sample set.
     *
     * @param samplePoints PointList sample set
     * @return double log likelihood
     */
    public double getLogLikelihood(PointList samplePoints) {
        return gmm.getLogLikelihood(samplePoints);
    }


    /**
     * Returns the log likelihood for the own samples points, which have been
     * drawn from the gmm representing the song.
     *
     * @return double the log likelihood of the own samples
     */
    public double getLogLikelihood() {
        if (Double.isNaN(logLikelihood))
            logLikelihood = getLogLikelihood(getSamplePoints());

        return logLikelihood;
    }


    /**
     * Returns a list of samples drawn from the gmm representing this song.
     *
     * @return PointList list of samples
     */
    public PointList getSamplePoints() {
        PointList sampleList = null;

        //if the softref exists get it => hardref
        if (sampleListRef != null)
            sampleList = sampleListRef.get();

        //create the sample points on demand
        if (sampleListRef == null || sampleList == null) {
            sampleList = new PointList(gmm.getDimension(), DSR + 1);

            //sample form this distribution
            for (int i = 0; i < DSR; i++)
                sampleList.add(gmm.nextSample());

            //create soft reference
            sampleListRef = new StrongLimitedReference<PointList>(sampleList);

            //compute the own log likelihood
            logLikelihood = getLogLikelihood(sampleList);

            return sampleList;
        } else {
            return sampleList;
        }
    }


    /**
     * Computes the distance between two timbre distributions representing two
     * songs. This is done by mutally computing the log likelihood of drawn
     * samples.
     *
     * @param f AudioFeature another timbre distribution feature
     * @return double the distance between the two timbre distributions
     * @throws ClassCastException thrown if the passed <code>AudioFeature<code> is
     *                            not an object of type
     *                            <code>TimbreDistribution</code>
     */
    public double getDistance(AudioFeature f) throws ClassCastException {
        TimbreDistribution other;
        double distance;
        PointList mySamples, otherSamples;

        //check and cast the parameter
        if (f.getType() != this.getType())
            throw new ClassCastException("features of different type are not compareable");
        else
            other = (TimbreDistribution) f;

        //get sample points
        mySamples = this.getSamplePoints();
        otherSamples = other.getSamplePoints();

        //compute the distance
        distance = this.getLogLikelihood() +
                other.getLogLikelihood() -
                other.getLogLikelihood(mySamples) -
                this.getLogLikelihood(otherSamples);

        return distance;
    }


    /**
     * For testing purpose only.
     *
     * @return GaussianMixture the gmm
     */
    public GaussianMixture getGaussianMixtureModel() {
        return gmm;
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
/*  public void writeXML(XMLStreamWriter writer) throws IOException, XMLStreamException
  {
    writer.writeStartElement("feature");
    writer.writeAttribute("type", getClassName());
    gmm.writeXML(writer);
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
    parser.require(XMLStreamReader.START_ELEMENT, null, "feature");

    parser.nextTag();
    gmm = GaussianMixture.readGMM(parser);
  }
*/
}

package lib.comirva.audio;

/**
 * An audio feature is a special attribute designed to describe characteristics
 * of audiostreams. A audio feature is the result of a audio feature extraction
 * process.<br>
 * <br>
 * Beside describing the characteristic of audio streams, audio features also
 * support comparing two audio streams. So based on the supported
 * characterization a metric has to be implemented, which allows to compute the
 * distance (similarity/dissimilarity) of two audio streams.
 *
 * @author Markus Schedl, Klaus Seyerlehner
 * @see comirva.audio.extraction.AudioFeatureExtractor
 * @see comirva.audio.extraction.AudioFeatureExtraction
 */
public abstract class AudioFeature extends Attribute {
    /**
     * Measures the similarity/dissimilarity of two audio streams characterized
     * by two audio features.
     *
     * @param f AudioFeature another audio feature of the same type
     * @return double the distance between the two audio streams
     */
    abstract public double getDistance(AudioFeature f);
}

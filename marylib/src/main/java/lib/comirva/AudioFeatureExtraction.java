package lib.comirva;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import lib.comirva.audio.AudioFeature;
import lib.comirva.audio.PointList;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;

/**
 * This thread is intended to do a feature extraction batch job. Each file
 * handed to the constructor will be process the given feature extractor. The
 * priority of the thread is low, such that it may run in the background. Any
 * kind of exception during the batch process are reported to the standard
 * log and ignored.<br>
 * <br>
 * This class is abstract because it has one template method, which should
 * create a <code>DataMatrix</code> object by using the extracted features.
 * Depending on the feature a specialised feature extraction thread may for
 * example return a similarity matrix or a matrix containing vectors.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class AudioFeatureExtraction {
    //  File[] files1;
    File[] files;
    ArrayList<PointList> arrayFeatureExtractorTrainSet;
    AudioFeatureExtractor featureExtractorInput;
    private KMeansClustering kmean;
    private PointList mfccPointList;


    public AudioFeatureExtraction(ArrayList<PointList> arrayFeatureExtractor, AudioFeatureExtractor featureExtractorInput, File[] files) {
        //check input
        if (files == null || arrayFeatureExtractor == null)
            throw new IllegalArgumentException(
                    "featureExtractor and files must not be null arrays");

        //set fields
        this.arrayFeatureExtractorTrainSet = arrayFeatureExtractor;
        this.featureExtractorInput = featureExtractorInput;
        this.files = files;

    }

    public KMeansClustering getKmean() {
        return kmean;
    }

    /**
     * Creates a new audio feature extraction thread.
     *
     * @param featureExtractor AudioFeatureExtractor the extractor, defining the
     *                         extraction process
     * @param files            File[] the audio files to process
     * @param ws               Workspace the workspace object to store the result in
     * @param statusBar        JLabel the status bar to show some progress information
     */
    public void setKmean(KMeansClustering kmean) {
        this.kmean = kmean;
    }

    public PointList getMFCC() {
        return mfccPointList;
    }

    public void setMFCC(PointList mfcc) {
        this.mfccPointList = mfcc;
    }

    public double audioThreadDistance(PointList mfcc, PointList kmean) {
        return featureExtractorInput.EuclideDistanceUsePointList(mfcc, kmean);
    }

    /**
     * Starts processing the feature extraction batch job.
     */
    public double run() {
        try {
            AudioInputStream inInput = AudioSystem.getAudioInputStream(files[0]);
            AudioFeature audioFeatureInput = (AudioFeature) featureExtractorInput.calculate(inInput);
        } catch (Exception e) {
        }

        double avgDistance = 0;

        //process all
        for (int i = 0; i < arrayFeatureExtractorTrainSet.size(); i++) {
            double distance2DB = audioThreadDistance(featureExtractorInput.getMFCC(), arrayFeatureExtractorTrainSet.get(i));
            Log.i("size", "Different distance " + distance2DB);
            avgDistance += distance2DB;
        }

        Log.i("size", "average distance " + (avgDistance / arrayFeatureExtractorTrainSet.size()));
        return avgDistance / arrayFeatureExtractorTrainSet.size();

    }

    public double distance(ArrayList<PointList> arrayPointList, PointList pl) {
        float valueReturn = 0;
        for (int i = 0; i < arrayPointList.size(); i++) {
            double distance2DB = audioThreadDistance(pl, arrayPointList.get(i));
            Log.i("size", "Different distance " + distance2DB);
            valueReturn += distance2DB;
        }
        return valueReturn / arrayPointList.size();
    }
}

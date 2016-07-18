/**
 * Copyright 2000-2009 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.cart;

import java.util.ArrayList;
import java.util.List;

import marytts.exceptions.MaryConfigurationException;
import marytts.features.FeatureDefinition;
import marytts.features.FeatureVector;


/**
 * The leaf of a CART.
 */
public abstract class LeafNode extends Node {

    // unique index used in MaryCART format
    protected int uniqueLeafId;

    /**
     * Create a new LeafNode.
     *
     * @param tok          the String Tokenizer containing the String with the
     *                     indices
     * @param openBrackets the number of opening brackets at the first token
     */
    public LeafNode() {
        super();
        isRoot = false;
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }

    /**
     * Count all the nodes at and below this node.
     * A leaf will return 1; the root node will
     * report the total number of decision and leaf nodes
     * in the tree.
     *
     * @return
     */
    @Override
    public int getNumberOfNodes() {
        return 1;
    }

    public int getUniqueLeafId() {
        return uniqueLeafId;
    }

    //  unique index used in MaryCART format
    public void setUniqueLeafId(int id) {
        this.uniqueLeafId = id;
    }

    @Override
    public String toString() {
        return "id" + uniqueLeafId;
    }

    /**
     * Indicate whether the leaf node has no meaningful data.
     *
     * @return
     */
    public abstract boolean isEmpty();

    /**
     * Count all the data available at and below this node.
     * The meaning of this depends on the type of nodes; for example,
     * when IntArrayLeafNodes are used, it is the total number of ints
     * that are saved in all leaf nodes below the current node.
     *
     * @return an int counting the data below the current node, or -1
     * if such a concept is not meaningful.
     */
    @Override
    public abstract int getNumberOfData();

    /**
     * Get all the data at or below this node.
     * The type of data returned depends on the type of nodes; for example,
     * when IntArrayLeafNodes are used, one int[] is returned which contains all
     * int values in all leaf nodes below the current node.
     *
     * @return an object containing all data below the current node, or null
     * if such a concept is not meaningful.
     */
    @Override
    public abstract Object getAllData();

    /**
     * Write this node's data into the target object at pos,
     * making sure that exactly len data are written.
     * The type of data written depends on the type of nodes; for example,
     * when IntArrayLeafNodes are used, target would be an int[].
     *
     * @param array the object to write to, usually an array.
     * @param pos   the position in the target at which to start writing
     * @param len   the amount of data items to write, usually equals
     *              getNumberOfData().
     */
    @Override
    protected abstract void fillData(Object target, int pos, int len);

    /**
     * The type of this leaf node.
     *
     * @return
     */
    public abstract LeafType getLeafNodeType();

    public enum LeafType {
        IntArrayLeafNode,
        FloatLeafNode,
        IntAndFloatArrayLeafNode,
        StringAndFloatLeafNode,
        FeatureVectorLeafNode,
        PdfLeafNode
    }

    /**
     * An LeafNode class suitable for representing the leaves of
     * classification trees -- the leaf is a collection of items identified
     * by an index number.
     *
     * @author marc
     */
    public static class IntArrayLeafNode extends LeafNode {
        protected int[] data;

        public IntArrayLeafNode(int[] data) {
            super();
            this.data = data;
        }

        /**
         * Get all data in this leaf
         *
         * @return the int array contained in this leaf
         */
        @Override
        public Object getAllData() {
            return data;
        }

        public int[] getIntData() {
            return data;
        }

        @Override
        protected void fillData(Object target, int pos, int len) {
            if (!(target instanceof int[]))
                throw new IllegalArgumentException("Expected target object of type int[], got " + target.getClass());
            int[] array = (int[]) target;
            assert len <= data.length;
            System.arraycopy(data, 0, array, pos, len);
        }

        @Override
        public int getNumberOfData() {
            if (data != null) return data.length;
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return data == null || data.length == 0;
        }

        @Override
        public LeafType getLeafNodeType() {
            return LeafType.IntArrayLeafNode;
        }

        @Override
        public String toString() {
            if (data == null) return super.toString() + "(int[null])";
            return super.toString() + "(int[" + data.length + "])";
        }

    }


    public static class IntAndFloatArrayLeafNode extends IntArrayLeafNode {

        protected float[] floats;

        public IntAndFloatArrayLeafNode(int[] data, float[] floats) {
            super(data);
            this.floats = floats;
        }

        public float[] getFloatData() {
            return floats;
        }

        /**
         * For the int-float pairs in this leaf, return the int value for which
         * the associated float value is the highest one. If the float values
         * are probabilities, this method returns the most probable int.
         *
         * @return
         */
        public int mostProbableInt() {
            int bestInd = 0;
            float maxProb = 0f;

            for (int i = 0; i < data.length; i++) {
                if (floats[i] > maxProb) {
                    maxProb = floats[i];
                    bestInd = data[i];
                }
            }
            return bestInd;
        }

        /**
         * Delete a candidate of the leaf by its given data/index
         *
         * @param target the given data
         */
        public void eraseData(int target) {
            int[] newData = new int[data.length - 1];
            float[] newFloats = new float[floats.length - 1];
            int index = 0;
            for (int i = 0; i < data.length; i++) {
                if (data[i] != target) {
                    newData[index] = data[i];
                    newFloats[index] = floats[i];
                    index++;
                }
            }
            data = newData;
            floats = newFloats;
        }

        @Override
        public LeafType getLeafNodeType() {
            return LeafType.IntAndFloatArrayLeafNode;
        }


        @Override
        public String toString() {
            if (data == null) return super.toString() + "(int and floats[null])";
            return super.toString() + "(int and floats[" + data.length + "])";
        }

    }

    public static class StringAndFloatLeafNode extends IntAndFloatArrayLeafNode {

        public StringAndFloatLeafNode(int[] data, float[] floats) {
            super(data, floats);
        }

        /**
         * Return the most probable value in this leaf, translated into its
         * string representation using the featureIndex'th feature of the given
         * feature definition.
         *
         * @param featureDefinition
         * @param featureIndex
         * @return
         */
        public String mostProbableString(FeatureDefinition featureDefinition, int featureIndex) {
            int bestInd = mostProbableInt();
            return featureDefinition.getFeatureValueAsString(featureIndex, bestInd);
        }

        @Override
        public String toString() {
            if (data == null) return super.toString() + "(string and floats[null])";
            return super.toString() + "(string and floats[" + data.length + "])";
        }

        @Override
        public LeafType getLeafNodeType() {
            return LeafType.StringAndFloatLeafNode;
        }

    }


    public static class FeatureVectorLeafNode extends LeafNode {
        private FeatureVector[] featureVectors;
        private List<FeatureVector> featureVectorList;
        private boolean growable;

        /**
         * Build a new leaf node containing the given feature vectors
         *
         * @param featureVectors the feature vectors
         */
        public FeatureVectorLeafNode(FeatureVector[] featureVectors) {
            super();
            this.featureVectors = featureVectors;
            growable = false;
        }

        /**
         * Build a new, empty leaf node
         * to be filled with vectors later
         */
        public FeatureVectorLeafNode() {
            super();
            featureVectorList = new ArrayList<FeatureVector>();
            featureVectors = null;
            growable = true;
        }

        public void addFeatureVector(FeatureVector fv) {
            featureVectorList.add(fv);
        }

        /**
         * Get the feature vectors of this node
         *
         * @return the feature vectors
         */
        public FeatureVector[] getFeatureVectors() {
            if (growable &&
                    (featureVectors == null
                            || featureVectors.length == 0)) {
                featureVectors = featureVectorList.toArray(
                        new FeatureVector[featureVectorList.size()]);
            }
            return featureVectors;
        }

        public void setFeatureVectors(FeatureVector[] fv) {
            this.featureVectors = fv;
        }

        /**
         * Get all data in this leaf
         *
         * @return the featurevector array contained in this leaf
         */
        @Override
        public Object getAllData() {
            if (growable &&
                    (featureVectors == null
                            || featureVectors.length == 0)) {
                featureVectors = featureVectorList.toArray(
                        new FeatureVector[featureVectorList.size()]);
            }
            return featureVectors;
        }

        @Override
        protected void fillData(Object target, int pos, int len) {
            if (!(target instanceof FeatureVector[]))
                throw new IllegalArgumentException("Expected target object of type FeatureVector[], got " + target.getClass());
            FeatureVector[] array = (FeatureVector[]) target;
            assert len <= featureVectors.length;
            System.arraycopy(featureVectors, 0, array, pos, len);
        }

        @Override
        public int getNumberOfData() {
            if (growable) {
                return featureVectorList.size();
            }
            if (featureVectors != null) return featureVectors.length;
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return featureVectors == null || featureVectors.length == 0;
        }

        @Override
        public LeafType getLeafNodeType() {
            return LeafType.FeatureVectorLeafNode;
        }


        @Override
        public String toString() {
            if (growable) return "fv[" + featureVectorList.size() + "]";
            if (featureVectors == null) return super.toString() + "(fv[null])";
            return super.toString() + "(fv[" + featureVectors.length + "])";
        }

    }

    /**
     * A leaf class that is suitable for regression trees.
     * Here, a leaf consists of a mean and a standard deviation.
     *
     * @author marc
     */
    public static class FloatLeafNode extends LeafNode {
        private float[] data;

        public FloatLeafNode(float[] data) {
            super();
            if (data.length != 2)
                throw new IllegalArgumentException("data must have length 2, found " + data.length);
            this.data = data;
        }

        /**
         * Get all data in this leaf
         *
         * @return the mean/standard deviation value contained in this leaf
         */
        @Override
        public Object getAllData() {
            return data;
        }

        public int getDataLength() {
            return data.length;
        }

        public float getMean() {
            return data[1];
        }

        public float getStDeviation() {
            return data[0];
        }

        @Override
        protected void fillData(Object target, int pos, int len) {
            throw new IllegalStateException("This method should not be called for FloatLeafNodes");
        }

        @Override
        public int getNumberOfData() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public LeafType getLeafNodeType() {
            return LeafType.FloatLeafNode;
        }

        @Override
        public String toString() {
            if (data == null) return super.toString() + "(mean=null, stddev=null)";
            return super.toString() + "(mean=" + data[1] + ", stddev=" + data[0] + ")";
        }

    }


    /**
     * A leaf class that is suitable for regression trees.
     * Here, a leaf consists of a mean and a diagonal covariance vectors.
     * This node will be used in HTS CART trees.
     */
    public static class PdfLeafNode extends LeafNode {
        private int vectorSize;
        private double[] mean;       // mean vector.
        private double[] variance;   // diagonal covariance.
        private double voicedWeight; // only for lf0 tree.

        /**
         * @param idx, a unique index number
         * @param pdf, pdf[numStreams][2*vectorSize]
         */
        public PdfLeafNode(int idx, double pdf[][]) throws MaryConfigurationException {
            super();
            this.setUniqueLeafId(idx);
            //System.out.println("adding leaf node: " + idx);
            if (pdf != null) {
                double val;
                int i, j, vsize, nstream;
                nstream = pdf.length;

                if (nstream == 1) {  // This is the case for dur, mgc, str, mag, or joinModel.
                    vsize = (pdf[0].length) / 2;
                    vectorSize = vsize;
                    mean = new double[vsize];
                    for (i = 0, j = 0; j < vsize; i++, j++)
                        mean[i] = pdf[0][j];
                    variance = new double[vsize];
                    for (i = 0, j = vsize; j < (2 * vsize); i++, j++)
                        variance[i] = pdf[0][j];

                } else {           // this is the case for lf0
                    vectorSize = nstream;
                    mean = new double[nstream];
                    variance = new double[nstream];
                    for (int stream = 0; stream < nstream; stream++) {
                        mean[stream] = pdf[stream][0];
                        variance[stream] = pdf[stream][1];
                        //vw  = lf0pdf[numStates][numPdfs][numStreams][2]; /* voiced weight */
                        //uvw = lf0pdf[numStates][numPdfs][numStreams][3]; /* unvoiced weight */
                        if (stream == 0)
                            voicedWeight = pdf[stream][2];
                    }
                }
            } else {
                throw new MaryConfigurationException("PdfLeafNode: pdf vector is null for index=" + idx);
            }

        }

        public int getDataLength() {
            return mean.length;
        }

        public double[] getMean() {
            return mean;
        }

        public double[] getVariance() {
            return variance;
        }

        public double getVoicedWeight() {
            return voicedWeight;
        }

        public int getVectorSize() {
            return vectorSize;
        }

        @Override
        protected void fillData(Object target, int pos, int len) {
            throw new IllegalStateException("This method should not be called for PdfLeafNodes");
        }

        // not meaningful here.
        @Override
        public Object getAllData() {
            return null;
        }

        // not meaningful here.
        // i need this value positive when searching ???
        @Override
        public int getNumberOfData() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public LeafType getLeafNodeType() {
            return LeafType.PdfLeafNode;
        }


        @Override
        public String toString() {
            if (mean == null) return super.toString() + "(mean=null, stddev=null)";
            return super.toString() + "(mean=[" + vectorSize + "], stddev=[" + vectorSize + "])";
        }

    }


}


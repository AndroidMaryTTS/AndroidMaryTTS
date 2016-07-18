/**
 * Copyright 2007 DFKI GmbH.
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
package marytts.signalproc.adaptation.gmm.jointgmm;

import marytts.signalproc.adaptation.gmm.GMMMatch;

/**
 * Joint-GMM output for voice conversion
 *
 * @author Oytun T&uumlrk
 */
public class JointGMMMatch extends GMMMatch {
    public double[] mappedSourceFeatures; //Source LSFs mapped onto source acoustic space
    public double[] outputFeatures; //Estimate of target LSFs on the target acoustic space

    public JointGMMMatch() {
        init(0);
    }

    public JointGMMMatch(int dimension) {
        init(dimension);
    }

    public void init(int dimension) {
        if (dimension > 0) {
            mappedSourceFeatures = new double[dimension];
            outputFeatures = new double[dimension];
        } else {
            mappedSourceFeatures = null;
            outputFeatures = null;
        }
    }

}


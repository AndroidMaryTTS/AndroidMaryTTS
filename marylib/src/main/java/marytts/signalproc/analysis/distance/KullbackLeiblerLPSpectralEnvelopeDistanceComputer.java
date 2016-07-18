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
package marytts.signalproc.analysis.distance;

/**
 * Implements Kullback-Leibler LP spectral envelope distance between two speech frames
 *
 * @author Oytun T&uumlrk
 */
public class KullbackLeiblerLPSpectralEnvelopeDistanceComputer extends BaselineLPSpectralEnvelopeDistortionComputer {
    public KullbackLeiblerLPSpectralEnvelopeDistanceComputer() {
        super();
    }

    //Put source and target wav and lab files into two folders and call this function
    public static void main(String[] args) {

    }

    @Override
    public double frameDistance(double[] frm1, double[] frm2, int fftSize, int lpOrder) {
        super.frameDistance(frm1, frm2, fftSize, lpOrder);

        double dist = SpectralDistanceMeasures.kullbackLeiblerSpectralDist(frm1, frm2, fftSize, lpOrder);

        return dist;
    }
}


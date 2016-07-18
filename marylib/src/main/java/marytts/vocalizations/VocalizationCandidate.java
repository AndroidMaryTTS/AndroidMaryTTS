/**
 * Copyright 2010 DFKI GmbH.
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
package marytts.vocalizations;

/**
 * Class represents a vocalization candidate
 *
 * @author sathish
 */
public class VocalizationCandidate implements Comparable<VocalizationCandidate> {

    int unitIndex;
    double cost;

    public VocalizationCandidate(int unitIndex, double cost) {
        this.unitIndex = unitIndex;
        this.cost = cost;
    }

    @Override
    public int compareTo(VocalizationCandidate other) {
        if (cost == other.cost) return 0;
        if (cost < other.cost) return -1;
        return 1;
    }

    @Override
    public boolean equals(Object dc) {
        if (!(dc instanceof VocalizationCandidate)) return false;
        VocalizationCandidate other = (VocalizationCandidate) dc;
        return cost == other.cost;
    }

    @Override
    public String toString() {
        return unitIndex + " " + cost;
    }

}

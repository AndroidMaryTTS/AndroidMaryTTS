/**
 * Copyright 2006 DFKI GmbH.
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

import marytts.unitselection.data.Unit;


/**
 * Representation of a unit from a unit database. This gives access to
 * everything that is known about a given unit, including all sorts of
 * features and the actual audio data.
 *
 * @author Sathish pammi
 */
public class VocalizationUnit extends marytts.unitselection.data.Unit {
    protected Unit[] units;
    protected String[] unitNames;

    public VocalizationUnit(long startTime, int duration, int index) {
        super(startTime, duration, index);
    }

    public Unit[] getUnits() {
        return this.units;
    }

    /**
     * Set units
     *
     * @return
     */
    public void setUnits(Unit[] units) {
        this.units = units;
    }

    public String[] getUnitNames() {
        return this.unitNames;
    }

    public void setUnitNames(String[] unitNames) {
        this.unitNames = unitNames;
    }
}


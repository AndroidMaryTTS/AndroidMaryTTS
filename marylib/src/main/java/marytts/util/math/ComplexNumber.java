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
package marytts.util.math;

/**
 * A complex number: real+sqrt(-1).imag
 *
 * @author Oytun T&uumlrk
 */
public class ComplexNumber {

    public float real;
    public float imag;

    public ComplexNumber() {

    }

    public ComplexNumber(ComplexNumber c) {
        this.real = c.real;
        this.imag = c.imag;
    }

    public ComplexNumber(float realIn, float imagIn) {
        this.real = realIn;
        this.imag = imagIn;
    }

    public ComplexNumber(double realIn, double imagIn) {
        this.real = (float) realIn;
        this.imag = (float) imagIn;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ComplexNumber)) {
            return false;
        }
        ComplexNumber cn = (ComplexNumber) other;
        if (real != cn.real) return false;
        return imag == cn.imag;

    }

    @Override
    public String toString() {
        String str;
        //if (Math.abs(real)>1e-10 || Math.abs(imag)>1e-10)
        //{
        if (imag >= 0.0)
            str = String.valueOf(real) + "+i" + String.valueOf(Math.abs(imag));
        else
            str = String.valueOf(real) + "-i" + String.valueOf(Math.abs(imag));
        //}
        //else
        //    str = "0";

        return str;
    }
}


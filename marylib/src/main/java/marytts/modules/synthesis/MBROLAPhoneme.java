/**
 * Copyright 2000-2006 DFKI GmbH.
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
package marytts.modules.synthesis;

import java.util.Vector;

public class MBROLAPhoneme {
    /**
     * The phone symbol, e.g. "a:".
     */
    private String symbol;
    /**
     * The duration in ms.
     */
    private int duration;
    /**
     * The f0 targets. This is a vector of int[]. Each target int[] has two
     * entries, the first [0] is the percent duration, the second [1] the f0 in
     * Hz.
     */
    private Vector<int[]> targets;
    /**
     * The voice quality
     */
    private String vq;

    public MBROLAPhoneme(String symbol, int duration, Vector<int[]> targets, String vq) {
        this.symbol = symbol;
        this.duration = duration;
        this.targets = targets;
        this.vq = vq;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Vector<int[]> getTargets() {
        return targets;
    }

    public void setTargets(Vector targets) {
        this.targets = targets;
    }

    public String getVoiceQuality() {
        return vq;
    }

    public void setVoiceQuality(String vq) {
        this.vq = vq;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(symbol);
        if (vq != null && !vq.equals("")) {
            buf.append("_");
            buf.append(vq);
        }
        buf.append(" ");
        buf.append(duration);
        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                int[] target = targets.get(i);
                buf.append(" (");
                buf.append(target[0]); // percent
                buf.append(",");
                buf.append(target[1]); // f0
                buf.append(")");
            }
        }
        return buf.toString();
    }
}


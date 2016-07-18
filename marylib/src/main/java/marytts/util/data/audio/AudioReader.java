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
package marytts.util.data.audio;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import marytts.server.Mary;
import marytts.util.MaryUtils;

//import org.apache.log4j.Logger;


/**
 * An convenience class copying audio data from an input stream
 * (e.g., a MARY module) to an AudioDestination object. Used by
 * java-external synthesis methods (SynthesisCallerBase and subclasses).
 *
 * @author Marc Schröder.
 */
public class AudioReader extends Thread {
    private InputStream from;
    private AudioDestination audioDestination;
    private byte[] endMarker;
    private long latestSeenTime;
    //private Logger logger;

    public AudioReader(InputStream from, AudioDestination audioDestination) {
        this(from, audioDestination, null);
    }

    public AudioReader(InputStream from, AudioDestination audioDestination, String endMarker) {
        super(Thread.currentThread().getName() + " reader");
        this.from = from;
        this.audioDestination = audioDestination;
        this.endMarker = endMarker != null ? endMarker.getBytes() : null;
        latestSeenTime = System.currentTimeMillis();
        // logger = MaryUtils.getLogger("Audio reader");
    }

    @Override
    public void run() {
        byte[] bytes = new byte[8192];
        int nrRead;
        boolean terminate = false;
        try {
            while (!terminate && (nrRead = from.read(bytes)) > -1) {
                latestSeenTime = System.currentTimeMillis();
                Log.d(Mary.LOG, "Read " + nrRead + " bytes from audio source.");
                if (endMarker != null) {
                    int start = MaryUtils.indexOf(bytes, endMarker);
                    if (start != -1) { // found the end marker!
                        nrRead = start; // truncate
                        terminate = true;
                        Log.d(Mary.LOG, "Found end marker at index position " + start);
                    }
                }
                audioDestination.write(bytes, 0, nrRead);
            }
            Log.i(Mary.LOG, "Finished reading.");
            from.close();
        } catch (IOException e) {
            Log.w(Mary.LOG, "Problem reading from module:", e);
        }
    }

    public long latestSeenTime() {
        return latestSeenTime;
    }

}


/**
 * Copyright 2009 DFKI GmbH.
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

package marytts.server.http;

import android.util.Log;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentOutputStream;
import org.apache.http.nio.entity.ProducingNHttpEntity;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SharedOutputBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import marytts.server.Mary;
import marytts.server.Request;

//import org.apache.log4j.Logger;

/**
 * @author marc
 */
public class AudioStreamNHttpEntity
        extends AbstractHttpEntity implements ProducingNHttpEntity, Runnable {
    private Request maryRequest;
    private AudioInputStream audio;
    private AudioFileFormat.Type audioType;
    // private Logger logger;
    private Object mutex;
    private SharedOutputBuffer out;

    public AudioStreamNHttpEntity(Request maryRequest) {
        this.maryRequest = maryRequest;
        this.audio = maryRequest.getAudio();
        this.audioType = maryRequest.getAudioFileFormat().getType();
        setContentType(MaryHttpServerUtils.getMimeType(audioType));
        this.mutex = new Object();
    }

    @Override
    public void finish() {
        //assert logger != null : "we should never be able to write if run() is not called";
        Log.i(Mary.LOG, "Completed sending streaming audio");
        maryRequest = null;
        audio = null;
        audioType = null;
        //logger = null;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioctrl)
            throws IOException {
        if (out == null) {
            synchronized (mutex) {
                out = new SharedOutputBuffer(8192, ioctrl, new HeapByteBufferAllocator());
                mutex.notify();
            }
        }
        while (!encoder.isCompleted())
            out.produceContent(encoder);
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public InputStream getContent() {
        return null;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        throw new RuntimeException("Should not be called");
    }


    /**
     * Wait for the SharedOutputBuffer to become available, write audio data to it.
     */
    @Override
    public void run() {
        //     this.logger = MaryUtils.getLogger(Thread.currentThread().getName());
        // We must wait until produceContent() is called:
        while (out == null) {
            synchronized (mutex) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        assert out != null;
        ContentOutputStream outStream = new ContentOutputStream(out);
        try {
            AudioSystem.write(audio, audioType, outStream);
            outStream.flush();
            outStream.close();
            Log.i(Mary.LOG, "Finished writing output");
        } catch (IOException ioe) {
            Log.i(Mary.LOG, "Cannot write output, client seems to have disconnected. ", ioe);
            maryRequest.abort();
        }
    }

}

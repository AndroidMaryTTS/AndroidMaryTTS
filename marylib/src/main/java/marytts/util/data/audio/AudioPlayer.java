/**
 * Copyright 2000-2007 DFKI GmbH.
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

import java.io.File;
import java.io.IOException;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import lib.sound.sampled.DataLine;
import lib.sound.sampled.LineListener;
import lib.sound.sampled.SourceDataLine;
import lib.sound.sampled.UnsupportedAudioFileException;

/**
 * This audio player is used by the example code MaryClientUser, but not by
 * MaryClient, which uses a com.sun.speech.freetts.audio.AudioPlayer. Maybe
 * change the example code?
 *
 * @author Marc Schr&ouml;der
 */
public class AudioPlayer extends Thread {
    public static final int MONO = 0;
    public static final int STEREO = 3;
    public static final int LEFT_ONLY = 1;
    public static final int RIGHT_ONLY = 2;
    private AudioInputStream ais;

    private LineListener lineListener;
    private SourceDataLine line;
    private int outputMode;
    private Status status = Status.WAITING;
    private boolean exitRequested = false;

    /**
     * AudioPlayer which can be used if audio stream is to be set separately, using setAudio().
     */
    public AudioPlayer() {
    }

    public AudioPlayer(File audioFile)
            throws IOException, UnsupportedAudioFileException {
        this.ais = AudioSystem.getAudioInputStream(audioFile);
    }

    public AudioPlayer(AudioInputStream ais) {
        this.ais = ais;
    }

    public AudioPlayer(File audioFile, LineListener lineListener)
            throws IOException, UnsupportedAudioFileException {
        this.ais = AudioSystem.getAudioInputStream(audioFile);
        this.lineListener = lineListener;
    }

    public AudioPlayer(AudioInputStream ais, LineListener lineListener) {
        this.ais = ais;
        this.lineListener = lineListener;
    }

    public AudioPlayer(File audioFile, SourceDataLine line, LineListener lineListener)
            throws IOException, UnsupportedAudioFileException {
        this.ais = AudioSystem.getAudioInputStream(audioFile);
        this.line = line;
        this.lineListener = lineListener;
    }

    public AudioPlayer(AudioInputStream ais, SourceDataLine line, LineListener lineListener) {
        this.ais = ais;
        this.line = line;
        this.lineListener = lineListener;
    }

    /**
     * @param audioFile
     * @param line
     * @param lineListener
     * @param outputMode   if MONO, force output to be mono; if STEREO, force output to be STEREO;
     *                     if LEFT_ONLY, play a mono signal over the left channel of a stereo output, or mute the
     *                     right channel of a stereo signal; if RIGHT_ONLY, do the same with the right output channel.
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public AudioPlayer(File audioFile, SourceDataLine line, LineListener lineListener, int outputMode)
            throws IOException, UnsupportedAudioFileException {
        this.ais = AudioSystem.getAudioInputStream(audioFile);
        this.line = line;
        this.lineListener = lineListener;
        this.outputMode = outputMode;
    }

    /**
     * @param ais
     * @param line
     * @param lineListener
     * @param outputMode   if MONO, force output to be mono; if STEREO, force output to be STEREO;
     *                     if LEFT_ONLY, play a mono signal over the left channel of a stereo output, or mute the
     *                     right channel of a stereo signal; if RIGHT_ONLY, do the same with the right output channel.
     */
    public AudioPlayer(AudioInputStream ais, SourceDataLine line, LineListener lineListener, int outputMode) {
        this.ais = ais;
        this.line = line;
        this.lineListener = lineListener;
        this.outputMode = outputMode;
    }

    public static void main(String[] args) throws Exception {
        boolean listFilename = false;
        if (args[0].equals("-l")) listFilename = true;
        for (int i = (listFilename ? 1 : 0); i < args.length; i++) {
            AudioPlayer player = new AudioPlayer(new File(args[i]), null);
            if (listFilename) System.out.println(args[i]);
            player.start();
            player.join();
        }
        System.exit(0);
    }

    public void setAudio(AudioInputStream audio) {
        if (status == Status.PLAYING) {
            throw new IllegalStateException("Cannot set audio while playing");
        }
        this.ais = audio;
    }


    public void cancel() {
        if (line != null)
            line.stop();
        exitRequested = true;
    }

    public SourceDataLine getLine() {
        return line;
    }

    @Override
    public void run() {
        status = Status.PLAYING;
        AudioFormat audioFormat = ais.getFormat();
        if (audioFormat.getChannels() == 1) {
            if (outputMode != MONO) { // mono -> convert to stereo
                ais = new StereoAudioInputStream(ais, outputMode);
                audioFormat = ais.getFormat();
            }
        } else { // 2 channels
            assert audioFormat.getChannels() == 2 : "Unexpected number of channels: " + audioFormat.getChannels();
            if (outputMode == MONO) {
                ais = new MonoAudioInputStream(ais);
            } else if (outputMode == LEFT_ONLY) {
                ais = new StereoAudioInputStream(ais, outputMode);
            } else if (outputMode == RIGHT_ONLY) {
                ais = new StereoAudioInputStream(ais, outputMode);
            } else {
                assert outputMode == STEREO : "Unexpected output mode: " + outputMode;

            }
        }

        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                audioFormat);

        try {
            if (line == null) {
                boolean bIsSupportedDirectly = AudioSystem.isLineSupported(info);
                if (!bIsSupportedDirectly) {
                    AudioFormat sourceFormat = audioFormat;
                    AudioFormat targetFormat = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            sourceFormat.getSampleRate(),
                            sourceFormat.getSampleSizeInBits(),
                            sourceFormat.getChannels(),
                            sourceFormat.getChannels() * (sourceFormat.getSampleSizeInBits() / 8),
                            sourceFormat.getSampleRate(),
                            sourceFormat.isBigEndian());
                    ais = AudioSystem.getAudioInputStream(targetFormat, ais);
                    audioFormat = ais.getFormat();
                }
                info = new DataLine.Info(SourceDataLine.class, audioFormat);
                line = (SourceDataLine) AudioSystem.getLine(info);
            }
            if (lineListener != null)
                line.addLineListener(lineListener);
            line.open(audioFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        line.start();
        int nRead = 0;
        byte[] abData = new byte[65532]; // needs to be a multiple of 4 and 6, to support both 16 and 24 bit stereo
        while (nRead != -1 && !exitRequested) {
            try {
                nRead = ais.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nRead >= 0) {
                line.write(abData, 0, nRead);
            }
        }
        if (!exitRequested) {
            line.drain();
        }
        line.close();
    }

    public enum Status {WAITING, PLAYING}
}


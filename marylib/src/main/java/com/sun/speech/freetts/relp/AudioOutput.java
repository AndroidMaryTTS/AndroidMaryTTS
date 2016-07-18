/**
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.relp;

import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.audio.AudioPlayer;

import lib.sound.sampled.AudioFormat;

/**
 * Supports generating audio output from an utterance. This is an
 * utterance processor. The primary method, <code> procesUtterance </code>
 * takes an utterance and hands it off to the LPCResult to be sent to the
 * proper audio player.
 *
 * @see LPCResult
 */
public class AudioOutput implements UtteranceProcessor {
    private final static AudioFormat AUDIO_8KHZ =
            new AudioFormat(8000.0f, 16, 1, true, true);
    private final static AudioFormat AUDIO_16KHZ =
            new AudioFormat(16000.0f, 16, 1, true, true);

    /**
     * Generates audio waves for the given Utterance. The audio data
     * is decoded using the Linear Predictive Decoder
     *
     * @param utterance the utterance to generate waves
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     * @see LPCResult
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        LPCResult lpcResult = (LPCResult) utterance.getObject("target_lpcres");
        SampleInfo sampleInfo =
                (SampleInfo) utterance.getObject(SampleInfo.UTT_NAME);
        AudioPlayer audioPlayer = utterance.getVoice().getAudioPlayer();

        audioPlayer.setAudioFormat(getAudioFormat(sampleInfo));
        audioPlayer.setVolume(utterance.getVoice().getVolume());

        utterance.getVoice().log("=== " +
                utterance.getString("input_text"));
        if (!lpcResult.playWave(audioPlayer, utterance)) {
            throw new ProcessException("Output Cancelled");
        }
    }


    /**
     * Gets the current audio format.
     * Given a sample info return an appropriate audio format. A cache
     * of common audio formats is used to reduce unnecessary object
     * creation. Note that this method always returns an AudioFormat
     * that uses 16-bit samples.
     *
     * @param sampleInfo the sample info
     * @return an audio format
     */
    private AudioFormat getAudioFormat(SampleInfo sampleInfo) {
        if (sampleInfo.getSampleRate() == 8000) {
            return AUDIO_8KHZ;
        } else if (sampleInfo.getSampleRate() == 16000) {
            return AUDIO_16KHZ;
        } else {
            return new AudioFormat(sampleInfo.getSampleRate(),
                    16, 1, true, true);
        }
    }

    /**
     * Returns the string form of this object
     *
     * @return the string form of this object
     */
    public String toString() {
        return "AudioOutput";
    }
}





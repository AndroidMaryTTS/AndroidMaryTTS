/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.audio;

import lib.sound.sampled.AudioFormat;

/**
 * Provides an  interface to the audio system for use by freetts.
 * Audio is presented to the AudioPlayer as byte arrays.
 * Implementations of this AudioPlayer interface will format the data
 * based upon the current audio format (as set by
 * <code>setAudioFormat</code>) and output the data.
 * <p/>
 * <p/>
 * The AudioPlayer
 * interface provides a set of potential synchronization points to
 * allow a specific AudioPlayer to batch output in various ways.
 * <p/>
 * These synchronization points are in pairs: <code>reset,
 * drain</code> are used to bracket output of large amounts of audio
 * data. Typically, an implementation will not return from
 * <code>drain</code> until all queued audio has been played (or
 * cancelled).
 * <p/>
 * The methods: <code> begin, end</code> are used to bracket smaller amounts of
 * audio data (typically associated with a single utterance).
 * <p/>
 * <h1>Threading Issues</h1>
 * Most of the methods in an AudioPlayer must be called from a
 * single thread. The only exceptions to this rule are <code> pause,
 * resume, cancel, showMetrics, close, getTime, resetTime</code>
 * which can be called from other threads.
 */
public interface AudioPlayer {

    /**
     * Retrieves the audio format for this player
     *
     * @return the current audio format
     */
    AudioFormat getAudioFormat();

    /**
     * Sets the audio format to use for the next set of outputs. Since
     * an audio player can be shared by a number of voices, and since
     * voices can have different AudioFormats (sample rates for
     * example), it is necessary to allow clients to dynamically set
     * the audio format for the player.
     *
     * @param format the audio format
     */
    void setAudioFormat(AudioFormat format);

    /**
     * Pauses all audio output on this player. Play can be resumed
     * with a call to resume
     */
    void pause();

    /**
     * Resumes audio output on this player
     */
    void resume();

    /**
     * Prepares for another batch of output. Larger groups of output
     * (such as all output associated with a single FreeTTSSpeakable)
     * should be grouped between a reset/drain pair.
     */
    void reset();

    /**
     * Waits for all queued audio to be played
     *
     * @return <code>true</code> if the audio played to completion;
     * otherwise <code> false </code> if the audio was stopped
     */
    boolean drain();


    /**
     * Starts the output of a set of data. Audio data for a single
     * utterance should be grouped between begin/end pairs.
     *
     * @param size the size of data in bytes to be output before
     *             <code>end</code> is called.
     */
    void begin(int size);

    /**
     * Signals the end of a set of data. Audio data for a single
     * utterance should be groupd between <code> begin/end </code> pairs.
     *
     * @return <code>true</code> if the audio was output properly,
     * <code> false</code> if the output was cancelled
     * or interrupted.
     */
    boolean end();


    /**
     * Cancels all queued output. All 'write' calls until the next
     * reset will return false.
     */
    void cancel();


    /**
     * Waits for all audio playback to stop, and closes this AudioPlayer.
     */
    void close();


    /**
     * Returns the current volume. The volume is specified as a number
     * between 0.0 and 1.0, where 1.0 is the maximum volume and 0.0 is
     * the minimum volume.
     *
     * @return the current volume (between 0 and 1)
     */
    float getVolume();

    /**
     * Sets the current volume. The volume is specified as a number
     * between 0.0 and 1.0, where 1.0 is the maximum volume and 0.0 is
     * the minimum volume.
     *
     * @param volume the new volume (between 0 and 1)
     */
    void setVolume(float volume);


    /**
     * Gets the amount of audio played since the last resetTime
     *
     * @return the amount of audio in milliseconds
     */
    long getTime();


    /**
     * Resets the audio clock
     */
    void resetTime();


    /**
     * Starts the first sample timer
     */
    void startFirstSampleTimer();

    /**
     * Writes the given bytes to the audio stream
     *
     * @param audioData audio data to write to the device
     * @return <code>true</code> of the write completed successfully,
     * <code> false </code>if the write was cancelled.
     */
    boolean write(byte[] audioData);

    /**
     * Writes the given bytes to the audio stream
     *
     * @param audioData audio data to write to the device
     * @param offset    the offset into the buffer
     * @param size      the number of bytes to write.
     * @return <code>true</code> of the write completed successfully,
     * <code> false </code>if the write was cancelled.
     */
    boolean write(byte[] audioData, int offset, int size);

    /**
     * Shows metrics for this audio player
     */
    void showMetrics();
}



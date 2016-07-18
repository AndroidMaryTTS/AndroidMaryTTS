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
package marytts.modules;

import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.modules.synthesis.FestivalUttSectioner;
import marytts.modules.synthesis.MbrolaVoiceSectioner;
import marytts.modules.synthesis.Voice;
import marytts.modules.synthesis.VoiceSection;
import marytts.modules.synthesis.VoiceSectioner;
import marytts.server.Mary;
import marytts.util.data.audio.AppendableSequenceAudioInputStream;


/**
 * A base class for a synthesis caller. This can work as a normal MARY module,
 * converting synthesis markup data into audio, or it can be indirectly called
 * from the corresponding Synthesizer.
 *
 * @author Marc Schr&ouml;der
 */

public abstract class SynthesisCallerBase extends InternalModule {
    protected SynthesisCallerBase(String name, MaryDataType inputType,
                                  MaryDataType outputType) {
        super(name, inputType, outputType, null);
    }

    @Override
    public synchronized void startup() throws Exception {
        super.startup();
    }

    /**
     * Perform a power-on self test by processing some example input data.
     * This implementation does nothing; instead, the module test is carried out
     * via Synthesis in the WaveformSynthesizer associated with this Caller.
     *
     * @throws Exception if the module does not work properly.
     */
    @Override
    public synchronized void powerOnSelfTest() {
    }

    /**
     * From synthesis markup input <code>d</code>, create audio output of the
     * type specified by a preceding call to <code>setAudioType()</code>.
     * Returns a MaryData structure whose data is an input stream from which
     * audio data of the specified type can be read.
     */
    @Override
    public MaryData process(MaryData d)
            throws
            Exception {
        assert d.getAudioFileFormat() != null;
        assert getState() == MODULE_RUNNING;

        // As the input may contain multipe voice sections,
        // the challenge in this method is to join the audio data
        // resulting from individual synthesis calls with the respective
        // voice into one audio stream of the specified type.
        // Overall strategy:
        // * In input, identify the sections to be spoken by different voices
        // * For each of these sections,
        //   - synthesise the section in the voice's native audio format
        //   - convert to the common audio format if necessary / possible
        // * Join the audio input streams
        // * Return a MaryData structure containing a single audio input stream
        //   from which the audio data in the desired format can be read.

        String input = (String) d.getData();
        Voice defaultVoice = d.getDefaultVoice();
        if (defaultVoice == null) {
            defaultVoice = Voice.getDefaultVoice(Locale.GERMAN);
            assert defaultVoice != null;
            Log.i(Mary.LOG, "No default voice associated with data. Assuming global default " +
                    defaultVoice.getName());
        }

        MaryData result = new MaryData(outputType(), d.getLocale());
        result.setAudioFileFormat(d.getAudioFileFormat());
        AudioFormat targetFormat = d.getAudioFileFormat().getFormat();
        if (d.getAudio() != null) {
            // This (empty) AppendableSequenceAudioInputStream object allows a 
            // thread reading the audio data on the other "end" to get to our data as we are producing it.
            assert d.getAudio() instanceof AppendableSequenceAudioInputStream;
            result.setAudio(d.getAudio());
        }

        VoiceSectioner sectioner = null;
        if (MaryDataType.get("FESTIVAL_UTT") != null && inputType().equals(MaryDataType.get("FESTIVAL_UTT"))) {
            sectioner = new FestivalUttSectioner(input, defaultVoice);
        } else if (MaryDataType.get("MBROLA") != null && inputType().equals(MaryDataType.get("MBROLA"))) {
            sectioner = new MbrolaVoiceSectioner(input, defaultVoice);
        } else {
            throw new RuntimeException("Don't know how to handle input type '" + inputType() + "'");
        }
        VoiceSection section = null;
        // A first pass identifying the voice with the highest sampling rate:
        AudioFormat commonAudioFormat = null;
        while ((section = sectioner.nextSection()) != null) {
            if (commonAudioFormat == null)
                commonAudioFormat = section.voice().dbAudioFormat();
            else if (section.voice().dbAudioFormat().getSampleRate()
                    > commonAudioFormat.getSampleRate())
                commonAudioFormat = section.voice().dbAudioFormat();
        }
        // And second pass:
        if (MaryDataType.get("FESTIVAL_UTT") != null && inputType().equals(MaryDataType.get("FESTIVAL_UTT"))) {
            sectioner = new FestivalUttSectioner(input, defaultVoice);
        } else {
            sectioner = new MbrolaVoiceSectioner(input, defaultVoice);
        }
        section = null;
        while ((section = sectioner.nextSection()) != null) {
            AudioInputStream ais = synthesiseOneSection(section.text(), section.voice());
            if (ais != null) {
                // Conversion required?
                ais = convertIfNeededAndPossible(ais, commonAudioFormat, section.voice().getName());
                ais = convertIfNeededAndPossible(ais, targetFormat, section.voice().getName());
                result.appendAudio(ais);
            }
        }

        return result;
    }

    protected AudioInputStream convertIfNeededAndPossible(AudioInputStream input, AudioFormat format, String voiceName) {
        if (input.getFormat().equals(format)) {
            return input;
        }
        // Attempt conversion; if not supported, log a warning
        // and provide the non-converted stream.
        Log.i(Mary.LOG, "Conversion required for voice " + voiceName);
        if (AudioSystem.isConversionSupported(format, input.getFormat())) {
            return AudioSystem.getAudioInputStream(format, input);
        }
        // conversion not supported
        Log.w(Mary.LOG, "Conversion to audio format " + format +
                " not supported. Providing voice default instead: " +
                input.getFormat());
        return input;
    }

    /**
     * Synthesise one chunk of synthesis markup with a given voice.
     *
     * @param synthesisMarkup the input data in the native format expected by
     *                        the synthesis engine
     * @param voice           the voice with which to synthesise the data
     * @return an AudioInputStream in the native audio format of the voice
     */
    public abstract AudioInputStream synthesiseOneSection(String synthesisMarkup, Voice voice) throws IOException;
}


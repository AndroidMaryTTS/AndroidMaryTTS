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

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.datatypes.MaryXML;
import marytts.exceptions.SynthesisException;
import marytts.modules.MaryModule;
import marytts.modules.MaryXMLToMbrola;
import marytts.modules.MbrolaCaller;
import marytts.modules.ModuleRegistry;
import marytts.modules.synthesis.Voice.Gender;
import marytts.server.Mary;
import marytts.server.MaryProperties;
import marytts.util.MaryUtils;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.NodeList;

//import org.apache.log4j.Logger;


/**
 * The Mbrola waveform synthesizer wrapper.
 */
public class MbrolaSynthesizer implements WaveformSynthesizer {
    private MaryXMLToMbrola maryxmlToMbrola;
    private MbrolaCaller mbrolaCaller;
    //  private Logger logger;

    public MbrolaSynthesizer() {
    }

    public static AudioFormat mbrolaAudioFormat(int samplingRate) {
        boolean bigEndian;
        if (System.getProperty("os.name").equals("Mac OS X")) {
            bigEndian = true; // big-endian
            // Special treatment for Mac OS X, because the MBROLA binary
            // (which stems from PowerPC times) produces big-endian
            // even on an i386 machine
        } else bigEndian = !(System.getProperty("os.arch").equals("x86") ||
                System.getProperty("os.arch").equals("i386") ||
                System.getProperty("os.arch").equals("amd64"));
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                samplingRate, // samples per second
                16, // bits per sample
                1, // mono
                2, // nr. of bytes per frame
                samplingRate, // nr. of frames per second
                bigEndian);
    }

    public static boolean isMbrolaVoice(Voice voice) {
        if (voice == null) throw new NullPointerException("Received null argument");
        WaveformSynthesizer ws = voice.synthesizer();
        if (ws == null) throw new NullPointerException("Voice has no waveform synthesizer");
        return (ws instanceof MbrolaSynthesizer);
    }

    @Override
    public void startup() throws Exception {
        //   logger = MaryUtils.getLogger(this.toString());
        // Try to get instances of our tools from Mary; if we cannot get them,
        // instantiate new objects.
        try {
            maryxmlToMbrola =
                    (MaryXMLToMbrola) ModuleRegistry.getModule(MaryXMLToMbrola.class);
        } catch (NullPointerException npe) {
            maryxmlToMbrola = null;
        }
        if (maryxmlToMbrola == null) {
            Log.i(Mary.LOG, "Starting my own MaryXMLToMbrola");
            maryxmlToMbrola = new MaryXMLToMbrola();
            maryxmlToMbrola.startup();
        } else if (maryxmlToMbrola.getState() == MaryModule.MODULE_OFFLINE) {
            maryxmlToMbrola.startup();
        }
        String mbrolaCallerProperty;
        if (System.getProperty("os.name").startsWith("Windows") && false) { // let's try without this, use cygwin binary instead...
            mbrolaCallerProperty = "mbrolasynthesizer.mbrolacaller.class.win32";
        } else {
            mbrolaCallerProperty = "mbrolasynthesizer.mbrolacaller.class";
        }
        Class mbrolaClass = MaryProperties.needClass(mbrolaCallerProperty);
        Object obj;
        try {
            obj = ModuleRegistry.getModule(mbrolaClass);
        } catch (NullPointerException npe) {
            obj = null;
        }
        if (obj == null) {
            Log.i(Mary.LOG, "Starting my own MbrolaCaller (" + mbrolaClass.getName() + ")");
            obj = mbrolaClass.newInstance();
        }
        if (!(obj instanceof MbrolaCaller)) {
            throw new ClassCastException("Class `" + mbrolaClass.getName() +
                    "' is not an MbrolaCaller. Check property `" + mbrolaCallerProperty +
                    "' in configuration files");
        }
        mbrolaCaller = (MbrolaCaller) obj;
        if (mbrolaCaller.getState() == MaryModule.MODULE_OFFLINE) {
            mbrolaCaller.startup();
        }

        // Register Mbrola voices:
        String basePath =
                System.getProperty("mary.base")
                        + File.separator
                        + "lib"
                        + File.separator
                        + "voices"
                        + File.separator;

        Log.d(Mary.LOG, "Register MBROLA voices:");
        String voiceNames = MaryProperties.needProperty("mbrola.voices.list");
        for (StringTokenizer st = new StringTokenizer(voiceNames); st.hasMoreTokens(); ) {
            String voiceName = st.nextToken();
            String path = MaryProperties.getFilename("voice." + voiceName + ".path", basePath + voiceName + File.separator + voiceName);
            if (new File(path).exists()) {
                Log.d(Mary.LOG, "Voice '" + voiceName + "'");
                Locale locale = MaryUtils.string2locale(MaryProperties.needProperty("voice." + voiceName + ".locale"));
                int samplingRate = MaryProperties.getInteger("voice." + voiceName + ".samplingrate", 16000);

                Gender gender = new Gender(MaryProperties.needProperty("voice." + voiceName + ".gender"));
                int topStart = MaryProperties.needInteger("voice." + voiceName + ".topline.start");
                int topEnd = MaryProperties.needInteger("voice." + voiceName + ".topline.end");
                int baseStart = MaryProperties.needInteger("voice." + voiceName + ".baseline.start");
                int baseEnd = MaryProperties.needInteger("voice." + voiceName + ".baseline.end");
                String vqString = MaryProperties.getProperty("voice." + voiceName + ".voicequalities", null);
                String[] voiceQualities = null;
                if (vqString != null) voiceQualities = vqString.split("\\s+");
                String missingDiphones = MaryProperties.getFilename("voice." + voiceName + ".missingdiphones", null);
                Voice v = new MbrolaVoice(path,
                        new String[]{voiceName},
                        locale,
                        mbrolaAudioFormat(samplingRate),
                        this,
                        gender,
                        topStart, topEnd, baseStart, baseEnd,
                        voiceQualities,
                        missingDiphones);
                Voice.registerVoice(v);
            } else { // voice not present
                Log.d(Mary.LOG, "Voice `" + voiceName + "' is not present. Skipping.");
            }
        }
        Log.i(Mary.LOG, "started.");
    }

    /**
     * Perform a power-on self test by processing some example input data.
     *
     * @throws Error if the module does not work properly.
     */
    @Override
    public synchronized void powerOnSelfTest() throws Error {
        Log.i(Mary.LOG, "Starting power-on self test.");
        try {
            MaryDataType inType = maryxmlToMbrola.inputType();
            Collection voices = Voice.getAvailableVoices(this);
            if (voices.isEmpty())
                throw new Error("No MBROLA voices present");
            Voice v = (Voice) voices.iterator().next();
            assert v != null;
            MaryData in = new MaryData(inType, v.getLocale());
            String example = inType.exampleText(v.getLocale());
            if (example != null) {
                in.readFrom(new StringReader(example));
                in.setDefaultVoice(v);
                MaryData mbrola = maryxmlToMbrola.process(in);
                mbrola.setAudioFileFormat(new AudioFileFormat(
                                AudioFileFormat.Type.WAVE, Voice.AF22050, AudioSystem.NOT_SPECIFIED)
                );
                mbrola.setDefaultVoice(v);
                mbrolaCaller.process(mbrola);
            } else {
                Log.d(Mary.LOG, "4 No example text -- no power-on self test!");
            }
        } catch (Throwable t) {
            throw new Error("Module " + toString() + ": Power-on self test failed.", t);
        }
        Log.i(Mary.LOG, "Power-on self test complete.");
    }

    @Override
    public String toString() {
        return "MbrolaSynthesizer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioInputStream synthesize(List<Element> tokensAndBoundaries, Voice voice, String outputParams)
            throws SynthesisException {
        if (!voice.synthesizer().equals(this)) {
            throw new IllegalArgumentException(
                    "Voice " + voice.getName() + " is not an MBROLA voice.");
        }
        Log.i(Mary.LOG, "Synthesizing one sentence.");
        // 1. Convert into MBROLA .pho format.
        List<Element> phonesAndBoundaries = new ArrayList<Element>();
        for (Element element : tokensAndBoundaries) {
            if (element.getTagName().equals(MaryXML.TOKEN)) {
                NodeList nl = element.getElementsByTagName(MaryXML.PHONE);
                for (int i = 0; i < nl.getLength(); i++) {
                    phonesAndBoundaries.add((Element) nl.item(i));
                }
            } else if (element.getTagName().equals(MaryXML.BOUNDARY)) {
                phonesAndBoundaries.add(element);
            } else {
                throw new IllegalArgumentException(
                        "Expected only <t> and <boundary> elements, got <"
                                + element.getTagName()
                                + ">");
            }
        }
        String pho =
                maryxmlToMbrola.convertToMbrola(phonesAndBoundaries, voice);

        if (Boolean.getBoolean("democenter.workaround")) {
            pho = pho + "_ 300\n";
        }

        // 2. Call MBROLA synthesizer.
        /*Vector audioInputStreams = new Vector();
        long totalFrames = 0;
        StringTokenizer st = new StringTokenizer(pho, "#");
        try {
            while (st.hasMoreTokens()) {
                AudioInputStream ais =
                    mbrolaCaller.synthesiseOneSection(st.nextToken(), voice);
                audioInputStreams.add(ais);
                totalFrames += ais.getFrameLength();
            }
        } catch (IOException ioe) {
            throw new SynthesisException("Cannot synthesise", ioe);
        }
        assert !audioInputStreams.isEmpty();
        return MaryAudioUtils.createSingleAudioInputStream(audioInputStreams);*/
        AudioInputStream ais;
        try {
            ais = mbrolaCaller.synthesiseOneSection(pho, voice);
        } catch (IOException ioe) {
            throw new SynthesisException("Cannot synthesise", ioe);
        }
        assert ais != null;
        return ais;
    }
}


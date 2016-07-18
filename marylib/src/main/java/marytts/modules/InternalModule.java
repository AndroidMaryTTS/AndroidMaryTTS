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

import java.io.StringReader;
import java.util.Locale;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioSystem;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.modules.synthesis.Voice;
import marytts.server.Mary;


/**
 * A stub implementation of the MaryModule interface
 * as a basis for internal modules.
 * <p/>
 * Any internal module extending this class will need to implement
 * a constructor calling this class's constructor, and override
 * <code>process()</code> in a meaningful way. Care must be taken
 * to make sure the <code>process()</code> method is thread-seafe.
 * <p/>
 * Example for a subclass:
 * <pre>
 * public class Postlex extends InternalModule
 * {
 *    public Postlex()
 *    {
 *        super("Postlex",
 *              MaryDataType.PHONEMISED,
 *              MaryDataType.POSTPROCESSED);
 *    }
 *
 *    public MaryData process(MaryData d)
 *    throws Exception
 *    {
 *        Document doc = d.getDocument();
 *        mtuPostlex(doc);
 *        phonologicalRules(doc);
 *        MaryData result = new MaryData(outputType());
 *        result.setDocument(doc);
 *        return result;
 *    }
 *
 *    private void mtuPostlex(Document doc) {...}
 *    private void phonologicalRules(Document doc) {...}
 * }
 * </pre>
 *
 * @author Marc Schr&ouml;der
 */

public class InternalModule implements MaryModule {
    protected int state;
    private String name = null;
    private MaryDataType inputType = null;
    private MaryDataType outputType = null;
    private Locale locale = null;

    /**
     * The logger instance to be used by this module.
     * It will identify the origin of the log message in the log file.
     */

    protected InternalModule(String name, MaryDataType inputType, MaryDataType outputType, Locale locale) {
        this.name = name;
        this.inputType = inputType;
        this.outputType = outputType;
        this.locale = locale;
        this.state = MODULE_OFFLINE;
    }

    // Interface MaryModule implementation:
    @Override
    public String name() {
        return name;
    }

    @Override
    public MaryDataType inputType() {
        return inputType;
    }

    @Override
    public MaryDataType outputType() {
        return outputType;
    }

    @Override
    public Locale getLocale() {
        return Locale.US;

    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void startup() throws Exception {
        assert state == MODULE_OFFLINE;
        Log.i(Mary.LOG, "Module started (" + inputType() + "->" + outputType() + ", locale " + getLocale() + ").");

        state = MODULE_RUNNING;
    }

    /**
     * Perform a power-on self test by processing some example input data.
     *
     * @throws Error if the module does not work properly.
     */
    @Override
    public void powerOnSelfTest() throws Error {
        assert state == MODULE_RUNNING;
        Log.i(Mary.LOG, "Starting power-on self test.");
        try {
            MaryData in = new MaryData(inputType, getLocale());
            String example = inputType.exampleText(getLocale());
            if (example != null) {
                in.readFrom(new StringReader(example));
                if (outputType.equals(MaryDataType.get("AUDIO")))
                    in.setAudioFileFormat(new AudioFileFormat(
                                    AudioFileFormat.Type.WAVE, Voice.AF22050, AudioSystem.NOT_SPECIFIED)
                    );
                process(in);
            } else {
                Log.d(Mary.LOG, "3 No example text -- no power-on self test!");
            }
        } catch (Throwable t) {
            throw new Error("Module " + name + ": Power-on self test failed.", t);
        }
        Log.i(Mary.LOG, "Power-on self test complete.");
    }


    @Override
    public void shutdown() {
        //   logger = MaryUtils.getLogger(name());
        Log.i(Mary.LOG, "Module shut down.");
        state = MODULE_OFFLINE;
    }


    /**
     * Perform this module's processing on abstract "MaryData" input
     * <code>d</code>.
     * Subclasses need to make sure that the <code>process()</code>
     * method is thread-safe, because in server-mode,
     * it will be called from different threads at the same time.
     * A sensible way to do this seems to be not to use any
     * global or static variables, or to use them read-only.
     * <p/>
     *
     * @return A MaryData object of type
     * <code>outputType()</code> encapsulating the processing result.
     * <p/>
     * This method just returns its input. Subclasses should override this.
     */
    @Override
    public MaryData process(MaryData d) throws Exception {
        return d; // just return input.
    }

}


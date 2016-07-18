package marytts.modules.synthesis;

import android.util.Log;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lib.sound.sampled.AudioInputStream;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.datatypes.MaryXML;
import marytts.exceptions.SynthesisException;
import marytts.features.FeatureRegistry;
import marytts.features.TargetFeatureComputer;
import marytts.htsengine.HMMVoice;
import marytts.modules.HTSEngine;
import marytts.modules.MaryModule;
import marytts.modules.ModuleRegistry;
import marytts.modules.TargetFeatureLister;
import marytts.server.Mary;
import marytts.server.MaryProperties;
import marytts.unitselection.select.Target;
import marytts.util.dom.MaryDomUtils;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.traversal.NodeIterator;
import mf.org.w3c.dom.traversal.TreeWalker;


/**
 * HTS-HMM synthesiser.
 * <p/>
 * Java port and extension of HTS engine version 2.0
 * Extension: mixed excitation
 *
 * @author Marc Schr&ouml;der, Marcela Charfuelan
 */
public class HMMSynthesizer implements WaveformSynthesizer {
    private TargetFeatureLister targetFeatureLister;
    private HTSEngine htsEngine;
    //private Logger logger;
    //private TargetFeatureComputer comp;

    public HMMSynthesizer() {
    }

    public void startup() throws Exception {
        // logger = MaryUtils.getLogger(this.toString());
        // Try to get instances of our tools from Mary; if we cannot get them,
        // instantiate new objects.

        try {
            targetFeatureLister = (TargetFeatureLister) ModuleRegistry.getModule(TargetFeatureLister.class);
        } catch (NullPointerException npe) {

            targetFeatureLister = null;
        }
        if (targetFeatureLister == null) {
            Log.d(Mary.LOG, "Starting my own TargetFeatureLister");
            targetFeatureLister = new TargetFeatureLister();
            targetFeatureLister.startup();
        } else if (targetFeatureLister.getState() == MaryModule.MODULE_OFFLINE) {

            targetFeatureLister.startup();
        }

        try {
            htsEngine = (HTSEngine) ModuleRegistry.getModule(HTSEngine.class);
        } catch (NullPointerException npe) {
            htsEngine = null;
        }
        if (htsEngine == null) {
            Log.i(Mary.LOG, "Starting my own HTSEngine");
            htsEngine = new HTSEngine();
            htsEngine.startup();
        } else if (htsEngine.getState() == MaryModule.MODULE_OFFLINE) {
            htsEngine.startup();
        }

        // Register HMM voices:
        List<String> voiceNames = MaryProperties.getList("hmm.voices.list");
        for (String voiceName : voiceNames) {
            Log.d(Mary.LOG, "Voice '" + voiceName + "'");

            /** When creating a HMMVoice object it should create and initialise a 
             * TreeSet ts, a ModelSet ms and load the context feature list used in this voice. */

            HMMVoice v = new HMMVoice(voiceName, this);
            Voice.registerVoice(v);
        }
        Log.i(Mary.LOG, "started.");

    }

    /**
     * Perform a power-on self test by processing some example input data.
     *
     * @throws Error if the module does not work properly.
     */
    public synchronized void powerOnSelfTest() throws Error {

        Log.i(Mary.LOG, "Starting power-on self test.");
        try {
            Collection<Voice> myVoices = Voice.getAvailableVoices(this);
            if (myVoices.size() == 0) {
                return;
            }

            Voice v = myVoices.iterator().next();
            MaryData in = new MaryData(MaryDataType.ACOUSTPARAMS, v.getLocale());

            String exampleText = MaryDataType.ACOUSTPARAMS.exampleText(v.getLocale());
            if (exampleText != null) {
                in.readFrom(new StringReader(exampleText));
                in.setDefaultVoice(v);
                assert v instanceof HMMVoice : "Expected voice to be a HMMVoice, but it is a " + v.getClass().toString();

                //-- Here it is set the targetFeatureComputer for this voice
                String features = ((HMMVoice) v).getHMMData().getFeatureDefinition().getFeatureNames();
                TargetFeatureComputer comp = FeatureRegistry.getTargetFeatureComputer(v, features);

                in.setOutputParams(features);
                Document doc = in.getDocument();
                // First, get the list of segments and boundaries in the current document
                TreeWalker tw = MaryDomUtils.createTreeWalker(doc, doc, MaryXML.PHONE, MaryXML.BOUNDARY);
                List<Element> segmentsAndBoundaries = new ArrayList<Element>();
                Element e;
                while ((e = (Element) tw.nextNode()) != null) {
                    segmentsAndBoundaries.add(e);
                }

                List<Target> targetFeaturesList = targetFeatureLister.getListTargetFeatures(comp, segmentsAndBoundaries);

                // The actual durations are already fixed in the htsEngine.process()
                // here i pass segements and boundaries to update the realised acoustparams, dur and f0
                MaryData audio = htsEngine.process(in, targetFeaturesList, segmentsAndBoundaries, null);

                assert audio.getAudio() != null;

            } else {
                Log.d(Mary.LOG, "2 No example text -- no power-on self test!");
            }
        } catch (Throwable t) {
            throw new Error("Module " + toString() + ": Power-on self test failed.", t);
        }
        Log.i(Mary.LOG, "Power-on self test complete.");


    }

    public String toString() {
        return "HMMSynthesizer";
    }

    /**
     * {@inheritDoc}
     */
    public AudioInputStream synthesize(List<Element> tokensAndBoundaries, Voice voice, String outputParams)
            throws SynthesisException {

        if (!voice.synthesizer().equals(this)) {
            throw new IllegalArgumentException(
                    "Voice " + voice.getName() + " is not an HMM voice.");
        }
        Log.i(Mary.LOG, "Synthesizing one sentence.");

        // from tokens and boundaries, extract segments and boundaries:
        List<Element> segmentsAndBoundaries = new ArrayList<Element>();
        Document doc = null;
        for (Element tOrB : tokensAndBoundaries) {
            if (tOrB.getTagName().equals(MaryXML.BOUNDARY)) {
                segmentsAndBoundaries.add(tOrB);
            } else { // a token -- add all segments below it
                if (doc == null) {
                    doc = tOrB.getOwnerDocument();
                }
                NodeIterator ni = MaryDomUtils.createNodeIterator(doc, tOrB, MaryXML.PHONE);
                Element s;
                while ((s = (Element) ni.nextNode()) != null) {
                    segmentsAndBoundaries.add(s);
                }
            }
        }
        try {
            assert voice instanceof HMMVoice : "Expected voice to be a HMMVoice, but it is a " + voice.getClass().toString();

            //-- This can be done just once when powerOnSelfTest() of this voice
            //-- mmmmmm it did not work, it takes the comp from the default voice
            //-- CHECK: do we need to do this for every call???
            String features = ((HMMVoice) voice).getHMMData().getFeatureDefinition().getFeatureNames();
            TargetFeatureComputer comp = FeatureRegistry.getTargetFeatureComputer(voice, features);

            // it is not faster to pass directly a list of targets?
            //--String targetFeatureString = targetFeatureLister.listTargetFeatures(comp, segmentsAndBoundaries);

            MaryData d = new MaryData(targetFeatureLister.outputType(), voice.getLocale());
            //--d.setPlainText(targetFeatureString);
            d.setDefaultVoice(voice);

            List<Target> targetFeaturesList = targetFeatureLister.getListTargetFeatures(comp, segmentsAndBoundaries);

            // the actual durations are already fixed in the htsEngine.process()
            // here i pass segements and boundaries to update the realised acoustparams, dur and f0
            MaryData audio = htsEngine.process(d, targetFeaturesList, segmentsAndBoundaries, tokensAndBoundaries);

            return audio.getAudio();

        } catch (Exception e) {
            throw new SynthesisException("HMM Synthesiser could not synthesise: ", e);
        }
    }


}
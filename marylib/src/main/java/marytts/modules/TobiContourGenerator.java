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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.datatypes.MaryXML;
import marytts.exceptions.NoSuchPropertyException;
import marytts.modules.phonemiser.Allophone;
import marytts.modules.phonemiser.AllophoneSet;
import marytts.modules.synthesis.MbrolaVoice;
import marytts.modules.synthesis.Voice;
import marytts.server.Mary;
import marytts.server.MaryProperties;
import marytts.util.MaryRuntimeUtils;
import marytts.util.MaryUtils;
import marytts.util.dom.DomUtils;
import marytts.util.dom.NameNodeFilter;
import mf.javax.xml.parsers.DocumentBuilder;
import mf.javax.xml.parsers.DocumentBuilderFactory;
import mf.javax.xml.parsers.FactoryConfigurationError;
import mf.javax.xml.parsers.ParserConfigurationException;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.NodeList;
import mf.org.w3c.dom.traversal.DocumentTraversal;
import mf.org.w3c.dom.traversal.NodeFilter;
import mf.org.w3c.dom.traversal.NodeIterator;
import mf.org.w3c.dom.traversal.TreeWalker;


/**
 * The language-independent tobi contour generation module.
 *
 * @author Marc Schr&ouml;der
 */

public class TobiContourGenerator extends InternalModule {
    /**
     * This map contains the topline-baseline frequency configurations for the
     * currently used phrase and sub-phrase prosody elements. As this is a
     * WeakHashMap, entries will automatically be deleted when not in regular
     * use anymore.
     */
    private WeakHashMap<Element, TopBaseConfiguration> topBaseConfMap;
    /**
     * This map contains the prosodic settings, as ProsodicSettings objects,
     * for the currently used prosody elements. As this is a WeakHashMap,
     * entries will automatically be deleted when not in regular use
     * anymore.
     */
    private WeakHashMap<Element, ProsodicSettings> prosodyMap;
    /**
     * This map contains the default voice element for a given document.
     * As this is a WeakHashMap, entries will automatically be deleted when not in
     * regular use anymore.
     */
    private WeakHashMap<Document, Voice> defaultVoiceMap;
    /**
     * The allophoneSet used for this language
     */
    private AllophoneSet allophoneSet;
    private String phoneSetPropertyName;
    /**
     * The tobi realisation rules for this language
     */
    private Map<String, Element> tobiMap;
    private String tobirulefilePropertyName;

    public TobiContourGenerator(String localeString) {
        super("ContourGenerator", MaryDataType.DURATIONS, MaryDataType.ACOUSTPARAMS, MaryUtils.string2locale(localeString));
        this.phoneSetPropertyName = localeString + ".allophoneset";
        this.tobirulefilePropertyName = localeString + ".cap.tobirulefile";
    }

    /**
     * Find the segment preceding this segment within the same
     * <code>phrase</code>.
     *
     * @return that segment, or <code>null</code> if there is no such segment.
     */
    private static Element getPreviousSegment(Element segment) {
        Element phrase = (Element) DomUtils.getAncestor(segment, MaryXML.PHRASE);
        return DomUtils.getPreviousOfItsKindIn(segment, phrase);
    }

    /**
     * Find the segment following this segment within the same
     * <code>phrase</code>.
     *
     * @return that segment, or <code>null</code> if there is no such segment.
     */
    private static Element getNextSegment(Element segment) {
        Element phrase = (Element) DomUtils.getAncestor(segment, MaryXML.PHRASE);
        return DomUtils.getNextOfItsKindIn(segment, phrase);
    }

    /**
     * Find the syllable preceding this syllable within the same
     * <code>phrase</code>.
     *
     * @return that syllable, or <code>null</code> if there is no such
     * syllable.
     */
    private static Element getPreviousSyllable(Element syllable) {
        Element phrase = (Element) DomUtils.getAncestor(syllable, MaryXML.PHRASE);
        return DomUtils.getPreviousOfItsKindIn(syllable, phrase);
    }

    /**
     * Find the syllable following this syllable within the same
     * <code>phrase</code>.
     *
     * @return that syllable, or <code>null</code> if there is no such
     * syllable.
     */
    private static Element getNextSyllable(Element syllable) {
        if (syllable == null)
            return null;
        Element phrase = (Element) DomUtils.getAncestor(syllable, MaryXML.PHRASE);
        return DomUtils.getNextOfItsKindIn(syllable, phrase);
    }

    /**
     * For the given segment, provide its end time, in milliseconds.
     *
     * @param segment
     * @return the end time or -1 if no end time can be determined
     */
    private static int getSegmentEndInMillis(Element segment) {
        try {
            float endInSeconds = Float.valueOf(segment.getAttribute("end"));
            int endInMillis = (int) (endInSeconds * 1000);
            return endInMillis;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public void startup() throws Exception {
        super.startup();
        // We depend on the Synthesis module:
        MaryModule synthesis;
        try {
            synthesis = ModuleRegistry.getModule(marytts.modules.Synthesis.class);
        } catch (NullPointerException npe) {
            synthesis = new Synthesis();
        }
        assert synthesis != null;
        if (synthesis.getState() == MaryModule.MODULE_OFFLINE)
            synthesis.startup();
        // load phone list
        allophoneSet = MaryRuntimeUtils.needAllophoneSet(phoneSetPropertyName);
        // load tobi rules
        tobiMap = new HashMap<String, Element>();
        loadTobiRules();
        // instantiate the Map in which settings are associated with elements:
        // (when the objects serving as keys are not in ordinary use any more,
        // the key-value pairs are deleted from the WeakHashMap earlier or
        // later; that means we do not need to keep track of the hashmaps per
        // thread)
        topBaseConfMap = new WeakHashMap<Element, TopBaseConfiguration>();
        prosodyMap = new WeakHashMap<Element, ProsodicSettings>();
        defaultVoiceMap = new WeakHashMap<Document, Voice>();
    }

    private synchronized void loadTobiRules()
            throws FactoryConfigurationError, ParserConfigurationException, org.xml.sax.SAXException, IOException,
            NoSuchPropertyException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        Document tobiRules = b.parse(new FileInputStream(MaryProperties.needFilename(tobirulefilePropertyName)));
        // Now fill the map of tobi symbols:
        Element root = tobiRules.getDocumentElement();
        for (Element e = DomUtils.getFirstChildElement(root);
             e != null;
             e = DomUtils.getNextSiblingElement(e)) {
            if (e.getTagName().equals("accent") || e.getTagName().equals(MaryXML.BOUNDARY)) {
                String name = e.getAttribute("name");
                // We want to be able to find tobi labels both in
                // uppercase and lowercase form:
                tobiMap.put(name.toUpperCase(), e);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    ///////////////////////////// Tobi Rules /////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    @Override
    public MaryData process(MaryData d) throws Exception {
        Document doc = d.getDocument();
        defaultVoiceMap.put(doc, d.getDefaultVoice());
        determineProsodicSettings(doc);
        addOrDeleteBoundaries(doc);

        NodeList sentences = doc.getElementsByTagName(MaryXML.SENTENCE);
        for (int i = 0; i < sentences.getLength(); i++) {
            Element sentence = (Element) sentences.item(i);
            processSentence(sentence);
        }
        MaryData result = new MaryData(outputType(), d.getLocale());
        result.setDocument(doc);
        return result;
    }

    /**
     * For all (possibly nested) prosody elements in the document,
     * calculate their (possibly cumulated) prosodic settings
     * and save them in a map.
     */
    private void determineProsodicSettings(Document doc) {
        // Determine the prosodic setting for each prosody element
        // Note: It is important that ancestor nodes are processed before
        // descendant nodes, because the descendants will inherit the
        // ancestors' settings!
        NodeList prosodies = doc.getElementsByTagName(MaryXML.PROSODY);
        for (int i = 0; i < prosodies.getLength(); i++) {
            Element prosody = (Element) prosodies.item(i);
            determineProsodicSettings(prosody);
        }
    }

    /**
     * For one given prosody element, determine the prosodic settings,
     * taking into account its closest prosody ancestor's settings.
     * This method needs to be called once when starting to work with a document
     * (from determineProsodicSettings(Document) and when a new prosody element
     * is created (e.g. for upstep/downstep).
     *
     * @param prosody the prosody element for which to save the prosodic settings in
     *                the map.
     */
    private void determineProsodicSettings(Element prosody) {
        ProsodicSettings settings = new ProsodicSettings();
        // Neutral default settings:
        ProsodicSettings parentSettings = new ProsodicSettings();
        // Obtain parent settings, if any:
        Element ancestor = (Element) DomUtils.getAncestor(prosody, MaryXML.PROSODY);
        if (ancestor != null) {
            ProsodicSettings testSettings = prosodyMap.get(ancestor);
            if (testSettings != null) {
                parentSettings = testSettings;
            }
        }
        // Only accept relative changes, i.e. percentage delta:
        settings.setRate(parentSettings.rate() + MaryUtils.getPercentageDelta(prosody.getAttribute("rate")));
        settings.setAccentProminence(
                parentSettings.accentProminence() + MaryUtils.getPercentageDelta(prosody.getAttribute("accent-prominence")));
        settings.setAccentSlope(
                parentSettings.accentSlope() + MaryUtils.getPercentageDelta(prosody.getAttribute("accent-slope")));
        settings.setNumberOfPauses(
                parentSettings.numberOfPauses() + MaryUtils.getPercentageDelta(prosody.getAttribute("number-of-pauses")));
        settings.setPauseDuration(
                parentSettings.pauseDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("pause-duration")));
        settings.setVowelDuration(
                parentSettings.vowelDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("vowel-duration")));
        settings.setPlosiveDuration(
                parentSettings.plosiveDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("plosive-duration")));
        settings.setFricativeDuration(
                parentSettings.fricativeDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("fricative-duration")));
        settings.setNasalDuration(
                parentSettings.nasalDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("nasal-duration")));
        settings.setLiquidDuration(
                parentSettings.liquidDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("liquid-duration")));
        settings.setGlideDuration(
                parentSettings.glideDuration() + MaryUtils.getPercentageDelta(prosody.getAttribute("glide-duration")));

        String sVolume = prosody.getAttribute("volume");
        if (sVolume.equals("")) {
            settings.setVolume(parentSettings.volume());
        } else if (MaryUtils.isPercentageDelta(sVolume)) {
            int newVolume = parentSettings.volume() + MaryUtils.getPercentageDelta(sVolume);
            if (newVolume < 0)
                newVolume = 0;
            else if (newVolume > 100)
                newVolume = 100;
            settings.setVolume(newVolume);
        } else if (MaryUtils.isUnsignedNumber(sVolume)) {
            settings.setVolume(MaryUtils.getUnsignedNumber(sVolume));
        } else if (sVolume.equals("silent")) {
            settings.setVolume(0);
        } else if (sVolume.equals("soft")) {
            settings.setVolume(25);
        } else if (sVolume.equals("medium")) {
            settings.setVolume(50);
        } else if (sVolume.equals("loud")) {
            settings.setVolume(75);
        }
        prosodyMap.put(prosody, settings);
    }

    /**
     * Adjust the number of boundaries according to rate and the
     * "number-of-pauses" attribute.
     */
    private void addOrDeleteBoundaries(Document doc) {
        // TODO: Check if this is needed; for German, this is already done in KlattDurationModeller!
        // Go through boundaries. A boundary is deleted if the determined
        // minimum breakindex size is larger than this boundary's breakindex.
        NodeIterator it =
                ((DocumentTraversal) doc).createNodeIterator(
                        doc,
                        NodeFilter.SHOW_ELEMENT,
                        new NameNodeFilter(MaryXML.BOUNDARY),
                        false);
        Element boundary = null;
        List<Element> bi1prosodyElements = null;
        while ((boundary = (Element) it.nextNode()) != null) {
            int minBI = 3;
            Element prosody = (Element) DomUtils.getAncestor(boundary, MaryXML.PROSODY);
            if (prosody != null) {
                ProsodicSettings settings = prosodyMap.get(prosody);
                assert settings != null;
                int rate = settings.rate();
                int numberOfPauses = settings.numberOfPauses();
                if (numberOfPauses <= 50)
                    minBI = 5;
                else if (numberOfPauses <= 75)
                    minBI = 4;
                else if (numberOfPauses > 150)
                    minBI = 1;
                else if (numberOfPauses > 125)
                    minBI = 2;
                // Rate can only shift the number of pauses by one breakindex
                if (rate < 90 && minBI > 1)
                    minBI--;
                if (minBI == 1) {
                    // Remember that the current prosody element wants bi 1 boundaries:
                    if (bi1prosodyElements == null)
                        bi1prosodyElements = new ArrayList<Element>();
                    bi1prosodyElements.add(prosody);
                }
            }
            // This boundary's bi:
            int bi = 3;
            try {
                bi = Integer.parseInt(boundary.getAttribute("breakindex"));
            } catch (NumberFormatException e) {
                Log.i(Mary.LOG,
                        "Unexpected breakindex value `" + boundary.getAttribute("breakindex") + "', assuming " + bi);
            }
            // TODO: removed because bi may be necessary later
            /*
            if (bi < minBI) {
                if (!boundary.hasAttribute("duration"))
                    boundary.getParentNode().removeChild(boundary);
                else
                    boundary.removeAttribute("bi"); // but keep duration
            }
            */
        }
        // Do we need to add any boundaries?
        if (bi1prosodyElements != null) {
            Iterator<Element> elIt = bi1prosodyElements.iterator();
            while (elIt.hasNext()) {
                Element prosody = elIt.next();
                NodeIterator nodeIt =
                        ((DocumentTraversal) doc).createNodeIterator(
                                prosody,
                                NodeFilter.SHOW_ELEMENT,
                                new NameNodeFilter(MaryXML.TOKEN, MaryXML.BOUNDARY),
                                false);
                Element el = null;
                Element prevEl = null;
                while ((el = (Element) nodeIt.nextNode()) != null) {
                    if (el.getTagName().equals(MaryXML.TOKEN) && prevEl != null && prevEl.getTagName().equals(MaryXML.TOKEN)) {
                        // Need to insert a boundary before el:
                        Element newBoundary = MaryXML.createElement(doc, MaryXML.BOUNDARY);
                        newBoundary.setAttribute("breakindex", "1");
                        el.getParentNode().insertBefore(newBoundary, el);
                    }
                    prevEl = el;
                }
            }
        }
    }

    private void processSentence(Element sentence) {
        NodeList tokens = sentence.getElementsByTagName(MaryXML.TOKEN);
        if (tokens.getLength() < 1) {
            return; // no tokens -- what can we do?
        }

        NodeList phrases = sentence.getElementsByTagName(MaryXML.PHRASE);
        for (int i = 0; i < phrases.getLength(); i++) {
            Element phrase = (Element) phrases.item(i);
            // calculate the F0 targets
            calculateF0Targets(phrase);
            // anchor the F0 targets at individual segments
            // calculate frequency values.
        }
    }

    /**
     * Determine the topline and baseline start and end frequencies for a
     * <code>phrase</code>. Create an appropriate TopBaseConfiguration object
     * and save it in a hash, as a value to which the phrase element is the
     * key.
     *
     * @see #getToplineFrequency(Element, int)
     * @see #getBaselineFrequency(Element, int)
     */
    private void determinePhraseTopBaseConf(Element phrase) {
        Voice voice = null;
        // Determine the settings for the phrase element:
        Element voiceElement = (Element) DomUtils.getAncestor(phrase, MaryXML.VOICE);
        if (voiceElement != null)
            voice = Voice.getVoice(voiceElement);
        if (voice == null)
            voice = defaultVoiceMap.get(phrase.getOwnerDocument());
        // In any case, if we do not have a voice now,
        // use the global default voice:
        if (voice == null) {
            voice = Voice.getDefaultVoice(getLocale());
        }
        if (!(voice instanceof MbrolaVoice)) {
            throw new IllegalStateException("TobiContourGenerator can be used only for MBROLA voices, but voice " + voice.getName() + " is a " + voice.getClass().toString());
        }
        MbrolaVoice mVoice = (MbrolaVoice) voice;
        int topStart = mVoice.topStart();
        int topEnd = mVoice.topEnd();
        int baseStart = mVoice.baseStart();
        int baseEnd = mVoice.baseEnd();
        TopBaseConfiguration tbConf = new TopBaseConfiguration(topStart, topEnd, baseStart, baseEnd);

        // Now see if there are any global modifiers (<prosody> elements
        // ancestors to this phrase element, but inside the voiceElement if
        // there is one; start with the outermost <prosody> element and
        // superpose them one after the other):
        Element current = phrase;
        Stack<Element> prosodyElements = new Stack<Element>();
        while (DomUtils.hasAncestor(current, MaryXML.PROSODY)) {
            current = (Element) DomUtils.getAncestor(current, MaryXML.PROSODY);
            prosodyElements.push(current);
            // Ignore prosody elements that are outside the closest voice element:
            if (voiceElement != null && !DomUtils.isAncestor(voiceElement, current)) {
                // We have gone upwards past the voiceElement, so stop.
                break;
            }
        }
        while (!prosodyElements.empty()) {
            Element prosody = prosodyElements.pop();
            tbConf = calculateTopBase(prosody, tbConf);
        }

        // OK, now tbConf is the best we can do for the prosodic settings of
        // this phrase.
        // Add timing information: (start is 0 for a phrase, end is the end of
        // the last segment in the phrase)
        Element lastSegment = DomUtils.getLastElementByTagName(phrase, MaryXML.PHONE);
        if (lastSegment != null) {
            // There ARE segments in this phrase
            int endTime = getSegmentEndInMillis(lastSegment);
            if (endTime == -1) {
                Log.w(Mary.LOG, "Unexpected end time `" + lastSegment.getAttribute("end") + "'");
            }
            tbConf.setTimes(0, endTime);
        }
        // Save the TopBaseConfiguration object in a hash, with the phrase
        // element as a key:
        topBaseConfMap.put(phrase, tbConf);

        //System.err.println("For phrase ranging from " + tbConf.startTime() + " to " + tbConf.endTime() + ", determined topStart " + tbConf.topStart() + ", topEnd " + tbConf.topEnd() + ", baseStart " + tbConf.baseStart() + ", baseEnd " + tbConf.baseEnd());
    }

    /**
     * Determine the topline and baseline start and end frequencies for a
     * <code>prosody</code> element within a <code>phrase</code>. Create an
     * appropriate TopBaseConfiguration object and save it in a hash, as a
     * value to which the prosody element is the key.
     *
     * @see getToplineFrequency(Element,int)
     * @see getBaselineFrequency(Element,int)
     */
    private void determineProsodyTopBaseConf(Element prosody) {
        if (prosody == null)
            throw new NullPointerException("Received null argument");
        if (!prosody.getTagName().equals(MaryXML.PROSODY))
            throw new IllegalArgumentException("Expected <prosody> argument, got <" + prosody.getTagName() + ">");
        // Find closest ancestor phrase or prosody element:
        Element phrase = (Element) DomUtils.getAncestor(prosody, MaryXML.PHRASE);
        if (phrase == null) {
            Log.w(Mary.LOG, "Trying to determine prosody top base conf for element without a <phrase> ancestor. Ignoring.");
            return;
        }
        Element confReferenceKey = phrase;
        Element firstSegment = DomUtils.getFirstElementByTagName(prosody, MaryXML.PHONE);
        // Dummy entry for empty prosody elements:
        if (firstSegment == null) {
            //topBaseConfMap.put(prosody, new TopBaseConfiguration(0,0,0,0));
            Log.i(Mary.LOG, "prosody top base conf for element containing no phones -- ignoring.");
            return;
        }

        // Now see if there is a prosody element which is our ancestor and which is
        // inside the phrase -- then that one is our configuration reference:
        Element prosodyAncestor = (Element) DomUtils.getAncestor(prosody, MaryXML.PROSODY);
        if (prosodyAncestor != null && DomUtils.isAncestor(phrase, prosodyAncestor)) {
            confReferenceKey = prosodyAncestor;
        }
        TopBaseConfiguration confReference = topBaseConfMap.get(confReferenceKey);
        assert confReference != null;
        // Now calculate start and end times for this element:
        int startTime = 0;
        try {
            startTime =
                    getSegmentEndInMillis(firstSegment) - Integer.parseInt(firstSegment.getAttribute("d"));
        } catch (NumberFormatException e) {
            Log.w(Mary.LOG,
                    "Unexpected start time `"
                            + getSegmentEndInMillis(firstSegment)
                            + "' - `"
                            + firstSegment.getAttribute("d")
                            + "'");
        }

        Element lastSegment = DomUtils.getLastElementByTagName(prosody, MaryXML.PHONE);
        int endTime = getSegmentEndInMillis(lastSegment);
        if (endTime == -1) {
            Log.w(Mary.LOG, "Unexpected end time `" + lastSegment.getAttribute("end") + "'");
        }
        // Create a new TopBaseConfiguration element reflecting the
        // settings in the confReference:
        TopBaseConfiguration tbConf =
                new TopBaseConfiguration(
                        confReference.toplineFrequency(startTime),
                        confReference.toplineFrequency(endTime),
                        confReference.baselineFrequency(startTime),
                        confReference.baselineFrequency(endTime),
                        startTime,
                        endTime);
        // Modify this reference according to this prosody element:
        tbConf = calculateTopBase(prosody, tbConf);
        // Save the TopBaseConfiguration object in a hash, with the prosody
        // element as a key:
        topBaseConfMap.put(prosody, tbConf);

        //System.err.println("For prosody ranging from " + tbConf.startTime() + " to " + tbConf.endTime() + ", determined topStart " + tbConf.topStart() + ", topEnd " + tbConf.topEnd() + ", baseStart " + tbConf.baseStart() + ", baseEnd " + tbConf.baseEnd());

    }

    /**
     * Starting from a baseline prosodic configuration and the settings
     * requested in the <code>prosody</code> element, a new prosodic
     * configuration (topline and baseline start and end frequencies) is
     * calculated.
     */
    private TopBaseConfiguration calculateTopBase(Element prosody, TopBaseConfiguration origConf) {
        int topStart = origConf.topStart();
        int topEnd = origConf.topEnd();
        int baseStart = origConf.baseStart();
        int baseEnd = origConf.baseEnd();
        String pitch = prosody.getAttribute("pitch");
        if (!pitch.equals("")) {
            if (MaryUtils.isPercentageDelta(pitch)) {
                //System.err.println("Percentage delta: `" + pitch + "'");
                int percentage = MaryUtils.getPercentageDelta(pitch);
                baseStart = (baseStart * (100 + percentage)) / 100;
                baseEnd = (baseEnd * (100 + percentage)) / 100;
                // For the topline we have two possibilities:
                // i) we shift by the same number of Hz as the baseline,
                //    i.e. keep the range constant in the frequency domain;
                // ii) we multiply with the same factor,
                //    i.e. keep the range constant in the log frequency domain
                //    (constant number of semitones range)
                // The latter seems more appropriate given the fact that the
                // human ear hears frequencies logarithmically.
                topStart = (topStart * (100 + percentage)) / 100;
                topEnd = (topEnd * (100 + percentage)) / 100;
            } else if (MaryUtils.isSemitonesDelta(pitch)) {
                //System.err.println("Semitones delta: `" + pitch + "'");
                double semitones = MaryUtils.getSemitonesDelta(pitch);
                // Adding one semitone to any frequency corresponds to a
                // multiplication with 2^(1/12) = 1.0595.
                // Subtracting one semitone corresponds to a division by 1.0595.
                // In general: Changing the frequency by x semitones corresponds
                // to a multiplication with 1.0595^x.
                double factor = Math.pow(1.0595, semitones);
                baseStart = (int) (baseStart * factor);
                baseEnd = (int) (baseEnd * factor);
                topStart = (int) (topStart * factor);
                topEnd = (int) (topEnd * factor);
            } else if (MaryUtils.isNumberDelta(pitch)) { // +5, -10.2
                //System.err.println("Number delta: `" + pitch + "'");
                int delta = MaryUtils.getNumberDelta(pitch);
                baseStart += delta;
                baseEnd += delta;
                topStart += delta;
                topEnd += delta;
            } else if (MaryUtils.isUnsignedNumber(pitch)) { // 180, 212.75
                //System.err.println("Unsigned number: `" + pitch + "'");
                // In order to keep the range constant in log frequency domain,
                // calculate the ratio of current topMean and baseMean. Notice
                // that the spreads are not calculated in the frequency domain,
                // i.e. the slope of topline and baseline in the frequency
                // domain change during the shift. It is unclear whether this
                // is very relevant.
                int baseMean = (baseStart + baseEnd) / 2;
                int topMean = (topStart + topEnd) / 2;
                double topBaseRatio = ((double) topMean) / baseMean;
                int topSpread = (topEnd - topStart) / 2;
                int newBaseMean = MaryUtils.getUnsignedNumber(pitch);
                int baseSpread = (baseEnd - baseStart) / 2;
                baseStart = newBaseMean - baseSpread;
                baseEnd = newBaseMean + baseSpread;
                topStart = (int) (newBaseMean * topBaseRatio - topSpread);
                topEnd = (int) (newBaseMean * topBaseRatio + topSpread);
            }
        }
        String range = prosody.getAttribute("range");
        if (!range.equals("")) {
            // Range leaves the baseline untouched, and moves the topline.
            // All relative changes stretch the distance top-base.
            if (MaryUtils.isPercentageDelta(range)) { // +25%, -17.2%
                //System.err.println("Percentage delta: `" + range + "'");
                int percentage = MaryUtils.getPercentageDelta(range);
                topStart = baseStart + ((topStart - baseStart) * (100 + percentage)) / 100;
                topEnd = baseEnd + ((topEnd - baseEnd) * (100 + percentage)) / 100;
            } else if (MaryUtils.isSemitonesDelta(range)) { // +5.2st, -0.7st
                //System.err.println("Semitones delta: `" + range + "'");
                // Change the current range by x semitones
                double semitones = MaryUtils.getSemitonesDelta(range);
                // for explanations, see pitch section above.
                double factor = Math.pow(1.0595, semitones);
                int deltaStart = (int) ((topStart - baseStart) * factor);
                int deltaEnd = (int) ((topEnd - baseEnd) * factor);
                topStart = baseStart + deltaStart;
                topEnd = baseEnd + deltaEnd;
            } else if (MaryUtils.isNumberDelta(range)) { // +15, -27.3
                //System.err.println("Number delta: `" + range + "'");
                int delta = MaryUtils.getNumberDelta(range);
                topStart += delta;
                topEnd += delta;
            } else if (MaryUtils.isUnsignedSemitones(range)) { // 12st, 5.32st
                //System.err.println("Unsigned semitones: `" + range + "'");
                // Set the new range to x semitones, discarding the previous
                // range
                double semitones = MaryUtils.getUnsignedSemitones(range);
                // for explanations, see pitch section above.
                double factor = Math.pow(1.0595, semitones);
                topStart = (int) (baseStart * factor);
                topEnd = (int) (baseEnd * factor);
            } else if (MaryUtils.isUnsignedNumber(range)) { // 60, 50.4
                //System.err.println("Unsigned number: `" + range + "'");
                // Notice that the spread is not calculated in the frequency
                // domain, i.e. the slope of topline and baseline in the
                // frequency domain change during the shift. It is unclear
                // whether this is very relevant.
                int baseMean = (baseStart + baseEnd) / 2;
                int topSpread = (topEnd - topStart) / 2;
                int newRange = MaryUtils.getUnsignedNumber(range);
                topStart = baseMean + newRange - topSpread;
                topEnd = baseMean + newRange + topSpread;
            }
        }
        String pitchDynamics = prosody.getAttribute("pitch-dynamics");
        if (!pitchDynamics.equals("")) {
            if (MaryUtils.isPercentageDelta(pitchDynamics)) { // +25%, -17.2%
                //System.err.println("Percentage delta: `" + pitchDynamics + "'");
                int percentage = MaryUtils.getPercentageDelta(pitchDynamics);
                int baseMean = (baseStart + baseEnd) / 2;
                // Motivation: m = (a+z)/2, and
                // z = (1+p)*a (that is the idea in "pitch-dynamics"!)
                // => m = (1 + p/2) * a  =>  a = m / (1 + p/2)
                baseStart = (200 * baseMean) / (200 + percentage);
                baseEnd = (baseStart * (100 + percentage)) / 100;
            } else if (MaryUtils.isNumberDelta(pitchDynamics)) { // +15, -27.3
                //System.err.println("Number delta: `" + pitchDynamics + "'");
                int delta = MaryUtils.getNumberDelta(pitchDynamics);
                int baseMean = (baseStart + baseEnd) / 2;
                baseStart = baseMean + delta / 2;
                baseEnd = baseMean - delta / 2;
            } else if (MaryUtils.isSemitonesDelta(pitchDynamics)) { // +5.2st, -0.7st
                //System.err.println("Semitones delta: `" + pitchDynamics + "'");
                double semitones = MaryUtils.getSemitonesDelta(pitchDynamics);
                // for explanations, see pitch section above.
                double factor = Math.pow(1.0595, semitones);
                int baseMean = (baseStart + baseEnd) / 2;
                // Motivation: as for percentage delta above, replacing
                // (1+p) with factor:
                // m = (a+z)/2
                // z = factor * a (that is the idea in "pitch-dynamics"!)
                // => m = (1+factor)*a/2  =>  a = 2m / (1+factor)
                baseStart = (int) ((2 * baseMean) / (1 + factor));
                baseEnd = (int) (factor * baseStart);
            } // non-delta values don't make sense for X-dynamics.
        }
        String rangeDynamics = prosody.getAttribute("range-dynamics");
        if (!rangeDynamics.equals("")) {
            if (MaryUtils.isPercentageDelta(rangeDynamics)) { // +25%, -17.2%
                //System.err.println("Percentage delta: `" + rangeDynamics + "'");
                int percentage = MaryUtils.getPercentageDelta(rangeDynamics);
                int baseMean = (baseStart + baseEnd) / 2;
                int topMean = (topStart + topEnd) / 2;
                int rangeMean = topMean - baseMean;
                // Motivation: see "pitch-dynamics" above
                int rangeStart = (200 * rangeMean) / (200 + percentage);
                int rangeEnd = (rangeStart * (100 + percentage)) / 100;
                topStart = baseStart + rangeStart;
                topEnd = baseEnd + rangeEnd;
            } else if (MaryUtils.isNumberDelta(rangeDynamics)) { // +15, -27.3
                //System.err.println("Number delta: `" + rangeDynamics + "'");
                int delta = MaryUtils.getNumberDelta(rangeDynamics);
                int baseMean = (baseStart + baseEnd) / 2;
                int topMean = (topStart + topEnd) / 2;
                int rangeMean = topMean - baseMean;
                int rangeStart = rangeMean + delta / 2;
                int rangeEnd = rangeMean - delta / 2;
                topStart = baseStart + rangeStart;
                topEnd = baseEnd + rangeEnd;
            } else if (MaryUtils.isSemitonesDelta(rangeDynamics)) { // +5.2st, -0.7st
                //System.err.println("Semitones delta: `" + rangeDynamics + "'");
                double semitones = MaryUtils.getSemitonesDelta(rangeDynamics);
                // for explanations, see pitch section above.
                double factor = Math.pow(1.0595, semitones);
                int baseMean = (baseStart + baseEnd) / 2;
                int topMean = (topStart + topEnd) / 2;
                int rangeMean = topMean - baseMean;
                // Motivation: see pitch-dynamics section above
                int rangeStart = (int) ((2 * rangeMean) / (1 + factor));
                int rangeEnd = (int) (factor * rangeStart);
                topStart = baseStart + rangeStart;
                topEnd = baseEnd + rangeEnd;
            } // non-delta values don't make sense for X-dynamics.
        }

        // Refuse to put topline below baseline:
        if (topStart < baseStart)
            topStart = baseStart;
        if (topEnd < baseEnd)
            topEnd = baseEnd;
        return new TopBaseConfiguration(topStart, topEnd, baseStart, baseEnd, origConf.startTime(), origConf.endTime());
    }

    /**
     * For a given phrase, calculate the target positions and frequencies
     * for each ToBI accent and boundary tone in the phrase.
     */
    private void calculateF0Targets(Element phrase) {
        // Determine top- / baseline start and end values for each phrase
        determinePhraseTopBaseConf(phrase);
        // and for all the <prosody> elements within the phrase.
        NodeList prosodies = phrase.getElementsByTagName(MaryXML.PROSODY);
        for (int j = 0; j < prosodies.getLength(); j++) {
            Element prosody = (Element) prosodies.item(j);
            determineProsodyTopBaseConf(prosody);
        }

        // Some useful memories for assigning the targets:
        boolean isFirstInPhrase = true;
        Element prevToneSyllable = null;
        char prevTone = 0; // valid values: 'H' and 'L'
        int lastHFreq = 0; // in Hertz
        List<Target> allTargetList = new ArrayList<Target>();
        // Go through all tokens and boundaries in the phrase, from left to
        // right:
        TreeWalker tw =
                ((DocumentTraversal) phrase.getOwnerDocument()).createTreeWalker(
                        phrase,
                        NodeFilter.SHOW_ELEMENT,
                        new NameNodeFilter(MaryXML.TOKEN, MaryXML.BOUNDARY),
                        false);
        Element e = null;
        while ((e = (Element) tw.nextNode()) != null) {
            Element referenceSyllable = null;
            Element rule = null;
            if (e.getTagName().equals(MaryXML.TOKEN)) { // a token
                // Accent:
                if (e.hasAttribute("accent")) {
                    String accent = e.getAttribute("accent").toUpperCase();
                    rule = tobiMap.get(accent);
                    if (rule != null) {
                        // Determine the stressed syllable in the token:
                        referenceSyllable = getStressedSyllable(e);
                    }
                }
            } else {
                // Boundary:
                if (e.hasAttribute("tone")) {
                    String tone = e.getAttribute("tone").toUpperCase();
                    rule = tobiMap.get(tone);
                    if (rule != null) {
                        // The reference syllable is the one preceding the
                        // boundary:
                        TreeWalker stw =
                                ((DocumentTraversal) e.getOwnerDocument()).createTreeWalker(
                                        phrase,
                                        NodeFilter.SHOW_ELEMENT,
                                        new NameNodeFilter(MaryXML.SYLLABLE),
                                        false);
                        stw.setCurrentNode(e);
                        referenceSyllable = (Element) stw.previousNode();
                    }
                }
            }
            if (referenceSyllable != null && rule != null) {
                Log.d(Mary.LOG,
                        "Now assigning targets for tone `"
                                + rule.getAttribute("name")
                                + "' on syllable ["
                                + referenceSyllable.getAttribute("ph")
                                + "]");
                // We have some targets to assign
                // For each target in the rule, first determine its location:
                List<Target> targetList = new ArrayList<Target>();
                Target starTarget = null;
                TreeWalker rtw =
                        ((DocumentTraversal) rule.getOwnerDocument()).createTreeWalker(
                                rule,
                                NodeFilter.SHOW_ELEMENT,
                                new NameNodeFilter("target", "prosody"),
                                false);
                Element rulePart = null;
                while ((rulePart = (Element) rtw.nextNode()) != null) {
                    if (rulePart.getTagName().equals("target")) {
                        Target target =
                                determineInitialTargetLocation(
                                        rulePart,
                                        referenceSyllable,
                                        isFirstInPhrase,
                                        prevTone,
                                        prevToneSyllable);
                        if (target != null) {
                            targetList.add(target);
                            allTargetList.add(target);
                            Log.d(Mary.LOG,
                                    "  "
                                            + target.type()
                                            + " target on ["
                                            + target.segment().getAttribute("p")
                                            + "] at "
                                            + target.getTargetTime()
                                            + " ms");
                            if (target.type().equals("star")) {
                                if (starTarget != null) {
                                    Log.i(Mary.LOG,
                                            "Found more than one star target for tone rule `"
                                                    + rule.getAttribute("name")
                                                    + "'");
                                }
                                starTarget = target;
                            }
                        }
                    } else { // "prosody": downstep or upstep
                        // First, identify the syllable which is to be the
                        // first to be downstepped or upstepped.
                        String tCode = rulePart.getAttribute("t_code");
                        Element prosSyllable = null;
                        if (tCode.equals("21")) { // this syllable
                            prosSyllable = referenceSyllable;
                        } else if (tCode.equals("11")) { // previous syllable
                            prosSyllable = DomUtils.getPreviousOfItsKindIn(referenceSyllable, phrase);
                        } else if (tCode.equals("31")) { // next syllable
                            prosSyllable = DomUtils.getNextOfItsKindIn(referenceSyllable, phrase);
                        } else if (tCode.equals("99")) {
                            // syllable after last tone
                            prosSyllable = DomUtils.getNextOfItsKindIn(prevToneSyllable, phrase);
                        }
                        if (prosSyllable == null) {
                            // Unknown tCode setting or no previous or next
                            // syllable -- Well, then we start with this
                            // syllable
                            prosSyllable = referenceSyllable;
                        }
                        Log.d(Mary.LOG,
                                "  upstep/downstep starting with syllable [" + prosSyllable.getAttribute("ph") + "]");
                        // Insert a prosody element into the phrase such
                        // that it encloses this syllable and the last
                        // syllable in the phrase.
                        adaptProsody(rulePart, prosSyllable);
                    }
                }
                // Adjust location of "plus" type targets if necessary:
                Iterator<Target> it = targetList.iterator();
                while (it.hasNext()) {
                    Target target = it.next();
                    target.setMyStar(starTarget);
                    if (target.type().equals("plus")) {
                        adjustTargetLocation(target, starTarget);
                    }
                }
                // Calculate target frequencies, and write the targets into the
                // XML structure:
                it = targetList.iterator();
                while (it.hasNext()) {
                    Target target = it.next();
                    lastHFreq = calculateTargetFrequency(target, lastHFreq);
                }

                // Now some useful memories for future rules inside this phrase:
                // We have already assigned at least one target:
                isFirstInPhrase = false;
                prevToneSyllable = referenceSyllable;
                String label = rule.getAttribute("name");
                if (label.lastIndexOf('H') > label.lastIndexOf('L')) {
                    // Remember previous tone was an H tone
                    prevTone = 'H';
                } else {
                    // Remember previous tone was an L tone
                    prevTone = 'L';
                }
            }
        }
        // Now verify that targets don't overlap, and that no target is closer
        // to another tone's target than to its own "star".
        ListIterator<Target> it = allTargetList.listIterator();
        Target prev = null;
        Target current = null;
        Target next = null;
        while (it.hasNext()) {
            next = it.next();
            if (current != null) {
                // Verify that next comes later than current:
                int currentTargetTime = current.getTargetTime();
                int nextTargetTime = next.getTargetTime();
                if (currentTargetTime > nextTargetTime) {
                    // If one is a star, move the other one:
                    if (current.type().equals("star") && !next.type().equals("star")) {
                        Element oldSegment = next.segment();
                        // Move next to the segment following current:
                        Element newSegment = getNextSegment(current.segment());
                        int newTiming = 10; // at 10% of following segment
                        if (newSegment == null) { // no such segment
                            newSegment = current.segment();
                            newTiming = 100;
                        }
                        next.setSegment(newSegment);
                        next.setTiming(newTiming);
                        // And recalculate the target frequency (trust that
                        // lastHFreq is not needed for this target)
                        calculateTargetFrequency(next, 0);
                        Log.d(Mary.LOG,
                                "Found overlapping targets. Moved "
                                        + "\"plus\" target from "
                                        + nextTargetTime
                                        + "ms ["
                                        + oldSegment.getAttribute("p")
                                        + "] to "
                                        + next.getTargetTime()
                                        + "ms ["
                                        + next.segment().getAttribute("p")
                                        + "].");
                    } else if (next.type().equals("star") && !current.type().equals("star")) {
                        Element oldSegment = current.segment();
                        // Move current to the segment preceding next:
                        Element newSegment = getPreviousSegment(next.segment());
                        int newTiming = 90; // at 90% of following segment
                        if (newSegment == null) { // no such segment
                            newSegment = next.segment();
                            newTiming = 0;
                        }
                        current.setSegment(newSegment);
                        current.setTiming(newTiming);
                        // And recalculate the target frequency (trust that
                        // lastHFreq is not needed for this target)
                        calculateTargetFrequency(current, 0);
                        Log.d(Mary.LOG,
                                "Found overlapping targets. Moved "
                                        + "\"plus\" target from "
                                        + currentTargetTime
                                        + "ms ["
                                        + oldSegment.getAttribute("p")
                                        + "] to "
                                        + current.getTargetTime()
                                        + "ms ["
                                        + current.segment().getAttribute("p")
                                        + "].");
                    } else { // none is a star
                        // If none is a star, calculate the meeting point
                        // of their respective interpolation lines, and
                        // replace them with a single target at this point.
                        int tn = next.getTargetTime();
                        int fn = next.f0();
                        int tc = current.getTargetTime();
                        int fc = current.f0();
                        int t1; // new target time
                        int f1; // new f0
                        // Two methods for calculating the new target:
                        if (next.myStar() != null
                                && next.myStar() != next
                                && current.myStar() != null
                                && current.myStar() != current) {
                            // The maths:
                            // next = (tn, fn); next.myStar() = (tns, fns)
                            // current = (tc, fc); current.myStar() = (tcs, fcs)
                            // We search for point (t1, f1) where lines meet.
                            // slope_n = (fns - fn) / (tns - tn)
                            // slope_c = (fc - fcs) / (tc - tcs)
                            // f1 = fn + slope_n * (t1 - tn)
                            // and
                            // f1 = fc - slope_c * (tc - t1)
                            // out of which we can conlcude
                            // t1 = ((fn - slope_n tn) - (fc - slope_c tc)) /
                            //      (slope_c - slope_n)
                            int tns = next.myStar().getTargetTime();
                            int fns = next.myStar().f0();
                            int tcs = current.myStar().getTargetTime();
                            int fcs = current.myStar().f0();
                            double slope_n = ((double) fns - fn) / (tns - tn);
                            double slope_c = ((double) fc - fcs) / (tc - tcs);
                            if (slope_n < 0 && slope_c >= 0 || slope_c < 0 && slope_n >= 0) {
                                t1 = (int) (((fn - slope_n * tn) - (fc - slope_c * tc)) / (slope_c - slope_n));
                                f1 = (int) (fn + slope_n * (t1 - tn));
                            } else {
                                t1 = (tn + tc) / 2;
                                f1 = (fn + fc) / 2;
                            }
                        } else {
                            // One of them has no star (or both are stars
                            // themselves, which should not happen)
                            // Calculate a simple time and frequency mean.
                            t1 = (tn + tc) / 2;
                            f1 = (fn + fc) / 2;
                        }
                        // Set the new values for current:
                        current.setTargetTime(t1);
                        current.setF0(f1);

                        Log.d(Mary.LOG,
                                "Found two overlapping targets, at "
                                        + tc
                                        + "ms, "
                                        + fc
                                        + "Hz and "
                                        + tn
                                        + "ms, "
                                        + fn
                                        + "Hz. Replaced them with a target at "
                                        + t1
                                        + "ms, "
                                        + f1
                                        + "Hz.");
                        // Delete next:
                        it.remove(); // removes next;
                        continue; // and re-get a next
                    }
                }
            }
            // Once we know no targets are inversed, we can check that no
            // target is closer to another tone's target than to its own star.
            if (prev != null) {
                int prevTime = prev.getTargetTime();
                int currentTime = current.getTargetTime();
                int nextTime = next.getTargetTime();
                if (current.myStar() == next
                        && nextTime - currentTime > currentTime - prevTime
                        || current.myStar() == prev
                        && currentTime - prevTime > nextTime - currentTime) {
                    int newTime = (prevTime + nextTime) / 2;
                    Log.d(Mary.LOG,
                            "Target at "
                                    + currentTime
                                    + "ms is further from its star than from"
                                    + " a different target -- moving to "
                                    + newTime
                                    + "ms.");
                    current.setTargetTime(newTime);
                    // And recalculate the target frequency (trust that
                    // lastHFreq is not needed for this target)
                    calculateTargetFrequency(current, 0);

                }
            }
            prev = current;
            current = next;
        }
        // Finally, insert the targets into MaryXML:
        it = allTargetList.listIterator();
        while (it.hasNext()) {
            insertTargetIntoMaryXML(it.next());
        }
    }

    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helpers ///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    /**
     * Determine the initial location of a target point on the time axis, based
     * on the rule in Element <code>target</code> relative to the given
     * <code>syllable</code> Element. For accents, this is the stressed
     * syllable of the accented word; for boundary tones, it is the last
     * syllable before the boundary.
     *
     * @return a newly created Target object, or null if the conditions for
     * applying this rule part were not fulfilled or no suitable target
     * location could be found.
     */
    private Target determineInitialTargetLocation(
            Element rulePart,
            Element syllable,
            boolean isFirstInPhrase,
            char prevTone,
            Element prevToneSyllable) {
        // Essential sanity check:
        if (rulePart == null
                || !rulePart.getTagName().equals("target")
                || syllable == null
                || !syllable.getTagName().equals(MaryXML.SYLLABLE)) {
            return null;
        }
        // Verify if rulePart condition applies:
        if (rulePart.hasAttribute("condition")) {
            String condition = rulePart.getAttribute("condition");
            if (condition.equals("first_in_IP") && !isFirstInPhrase) {
                return null;
            } else if (condition.equals("prevtone_H") && prevTone != 'H') {
                return null;
            } else if (condition.equals("prevtone_L") && prevTone != 'L') {
                return null;
            }
        }
        // OK, no condition violated.
        // Locate target position:
        Element segment = null;
        // Target timing is in percent of the segment duration, relative to the
        // segment start:
        int timing = -1;
        String tCode = rulePart.getAttribute("t_code");
        if (tCode.equals("0")) { // start of this declination phrase
            Element phrase = (Element) DomUtils.getAncestor(syllable, MaryXML.PHRASE);
            segment = DomUtils.getFirstElementByTagName(phrase, MaryXML.PHONE);
            timing = 0;
        } else if (tCode.equals("12")) {
            // start of the nucleus of the preceding syllable
            Element prevSyl = getPreviousSyllable(syllable);
            if (prevSyl == null) {
                // No such syllable -- just ignore this target
            } else {
                Element nucleus = getNucleus(prevSyl);
                if (nucleus == null) // No nucleus -- take first segment then:
                    segment = DomUtils.getFirstElementByTagName(prevSyl, MaryXML.PHONE);
                else
                    segment = nucleus;
                timing = 0;
            }
        } else if (tCode.equals("21")) {
            // start of this syllable
            segment = DomUtils.getFirstElementByTagName(syllable, MaryXML.PHONE);
            timing = 0;
        } else if (tCode.equals("22")) {
            // start of the nucleus of this syllable
            Element nucleus = getNucleus(syllable);
            if (nucleus == null) // No nucleus -- take first segment then:
                segment = DomUtils.getFirstElementByTagName(syllable, MaryXML.PHONE);
            else
                segment = nucleus;
            timing = 0;
        } else if (tCode.equals("23")) {
            // middle of the nucleus of this syllable
            // (simplifying assumption: the nucleus contains only one segment)
            Element nucleus = getNucleus(syllable);
            if (nucleus == null) // No nucleus -- take first segment then:
                segment = DomUtils.getFirstElementByTagName(syllable, MaryXML.PHONE);
            else
                segment = nucleus;
            timing = 50;
        } else if (tCode.equals("24")) {
            // end of the nucleus of this syllable
            // (simplifying assumption: the nucleus contains only one segment)
            Element nucleus = getNucleus(syllable);
            if (nucleus == null) // No nucleus -- take last segment then:
                segment = DomUtils.getLastElementByTagName(syllable, MaryXML.PHONE);
            else
                segment = nucleus;
            timing = 100;
        } else if (tCode.equals("25")) {
            // end of this syllable
            segment = DomUtils.getLastElementByTagName(syllable, MaryXML.PHONE);
            timing = 100;
        } else if (tCode.equals("34")) {
            // end of the nucleus of the following syllable
            // (simplifying assumption: the nucleus contains only one segment)
            Element nextSyl = getNextSyllable(syllable);
            if (nextSyl == null) {
                // No such syllable -- just ignore this target
            } else {
                Element nucleus = getNucleus(nextSyl);
                if (nucleus == null) // No nucleus -- take last segment then:
                    segment = DomUtils.getLastElementByTagName(nextSyl, MaryXML.PHONE);
                else
                    segment = nucleus;
                timing = 100;
            }
        } else if (tCode.equals("99")) {
            // middle of the nucleus of the syllable after the syllable
            // associated with the previous tone
            Element syl = getNextSyllable(prevToneSyllable);
            if (syl == null) {
                // No such syllable -- just ignore this target
            } else {
                Element nucleus = getNucleus(syl);
                if (nucleus == null) // No nucleus -- take first segment then:
                    segment = DomUtils.getFirstElementByTagName(syl, MaryXML.PHONE);
                else
                    segment = nucleus;
                timing = 50;
            }
        } else if (tCode.equals("98")) {
            // middle of the nucleus of the first 1ary or 2ary stressed
            // syllable after the syllable after the syllable associated with
            // the previous tone
            Element syl = getNextSyllable(prevToneSyllable);
            if (syl == null) {
                // No such syllable -- just ignore this target
            } else {
                // OK, skip that first syllable:
                syl = getNextSyllable(syl);
                if (syl == null) {
                    // No such syllable -- just ignore this target
                } else {
                    // Now take the first one we get which has 1ary or 2ary stress:
                    Element fallback = syl;
                    while (syl != null
                            && !(syl.getAttribute("stress").equals("1") || syl.getAttribute("stress").equals("2"))) {
                        syl = getNextSyllable(syl);
                    }
                    if (syl == null)
                        syl = fallback;
                    // OK, now we have a syllable to work with.
                    Element nucleus = getNucleus(syl);
                    if (nucleus == null) // No nucleus -- take first segment then:
                        segment = DomUtils.getFirstElementByTagName(syl, MaryXML.PHONE);
                    else
                        segment = nucleus;
                    timing = 50;
                }
            }
        }
        if (segment == null || timing == -1) {
            Log.d(Mary.LOG, "  Target (" + rulePart.getAttribute("f0") + ") could not be attached. skipping.");
            return null;
        }
        return new Target(rulePart, segment, timing, 0);
    }

    /**
     * Take into account the MaryXML prosody attribute "accent-slope" -- shift
     * the "plus" target relative to the "star" target accordingly. The star
     * target is not moved.
     */
    private void adjustTargetLocation(Target plus, Target star) {
        if (plus == null
                || plus.segment() == null
                || plus.timing() == -1
                || star == null
                || star.segment() == null
                || star.timing() == -1)
            return;
        // Find out if there is a need to adjust:
        Element prosody = (Element) DomUtils.getAncestor(plus.segment(), MaryXML.PROSODY);
        if (prosody == null)
            return;
        ProsodicSettings settings = prosodyMap.get(prosody);
        assert settings != null;
        int accentSlope = settings.accentSlope();
        if (accentSlope == 100) // unchanged
            return;
        else if (accentSlope == 0) // causes division by zero error below
            accentSlope = 1;
        // OK, we need to adjust the plus location!
        // calculate distance from target to starTarget:
        int plusTime = plus.getTargetTime();
        int starTime = star.getTargetTime();
        if (plusTime == -1 || starTime == -1)
            return;
        int distance = starTime - plusTime;
        // distance is positive if plus comes before star, negative otherwise
        // The slope is inversely proportional to the distance.
        int newDistance = 100 * distance / accentSlope;
        int newPlusTime = starTime - newDistance;
        if (newPlusTime < 0)
            newPlusTime = 0;
        String oldSegment = plus.segment().getAttribute("p");
        boolean success = plus.setTargetTime(newPlusTime);
        if (success) {
            Log.d(Mary.LOG,
                    "Accent slope: moved \"plus\" target from ["
                            + oldSegment
                            + "] at "
                            + plusTime
                            + " ms to ["
                            + plus.segment().getAttribute("p")
                            + "] at "
                            + plus.getTargetTime()
                            + " ms.");
        }
    }

    /**
     * For the given target, calculate the appropriate F0 by taking into
     * account the local topline and baseline frequency.
     *
     * @return The last target frequency on the topline, either as passed here
     * through parameter <code>lastHFreq</code> or as realised by this target.
     */
    private int calculateTargetFrequency(Target target, int lastHFreq) {
        // sanity check:
        if (target == null || target.targetRule() == null || target.segment() == null)
            throw new NullPointerException("Null target specification -- cannot calculate Frequency");
        // Calculate target frequency:
        String f0descr = target.targetRule().getAttribute("f0");
        int f0 = 0;
        TopBaseConfiguration tbConf = null;
        // Approximation if we need lastHFreq and don't have it:
        if (f0descr.equals("last_H_freq") && lastHFreq == 0) {
            f0descr = "1100"; // 10% above top line
        }
        if (MaryUtils.isNumber(f0descr)) {
            int f0promille = MaryUtils.getNumber(f0descr);
            Element phrase = (Element) DomUtils.getAncestor(target.segment(), MaryXML.PHRASE);
            Element prosody = (Element) DomUtils.getAncestor(target.segment(), MaryXML.PROSODY);
            Element topBaseRef = phrase;
            if (prosody != null && DomUtils.isAncestor(phrase, prosody)) {
                // A local prosody tag -- this is our reference
                topBaseRef = prosody;
            }
            // For accents, realise target overshoot or undershoot
            // as a function of the "accent-prominence" attribute:
            if (prosody != null
                    && // inside or outside phrase
                    target.targetRule().getParentNode().getNodeName().equals("accent")
                    && target.type().equals("star")) {
                ProsodicSettings settings = prosodyMap.get(prosody);
                if (settings != null) {
                    int accentProminence = settings.accentProminence();
                    // Stretch the distance of f0promille from 500
                    int dist = f0promille - 500;
                    int newDist = (dist * accentProminence) / 100;
                    f0promille = 500 + newDist;
                }
            }
            tbConf = topBaseConfMap.get(topBaseRef);
            assert tbConf != null;
            int d = 0;
            try {
                d = Integer.parseInt(target.segment().getAttribute("d"));
            } catch (NumberFormatException e) {
                Log.w(Mary.LOG, "Unexpected duration value `" + target.segment().getAttribute("d") + "'");
            }
            int end = getSegmentEndInMillis(target.segment());
            if (end == -1) {
                Log.w(Mary.LOG, "Unexpected duration value `" + target.segment().getAttribute("end") + "'");
            }
            // Remember that timing is expressed as a percentage of d:
            int timeMillis = (end - d) + (d * target.timing()) / 100;
            if (f0promille == 1000) { // on topline
                f0 = tbConf.toplineFrequency(timeMillis);
                lastHFreq = f0;
            } else if (f0promille == 0) { // on baseline
                f0 = tbConf.baselineFrequency(timeMillis);
            } else { // somewhere in between or above or below
                int base = tbConf.baselineFrequency(timeMillis);
                int top = tbConf.toplineFrequency(timeMillis);
                int range = top - base;
                f0 = base + (f0promille * range) / 1000;
            }
        } else if (f0descr.equals("last_H_freq")) {
            f0 = lastHFreq;
        } else {
            Log.w(Mary.LOG, "Unknown f0 specification `" + f0descr + "' in file " + MaryProperties.getFilename(tobirulefilePropertyName));
        }
        if (f0 != 0) {
            // OK, valid
            target.setF0(f0);
            Log.d(Mary.LOG,
                    "Target on segment ["
                            + target.segment().getAttribute("p")
                            + "] at "
                            + target.getTargetTime()
                            + " ms, "
                            + target.f0()
                            + " Hz ("
                            + f0descr
                            + ")");
        }
        return lastHFreq;
    }

    private void insertTargetIntoMaryXML(Target target) {
        if (target != null && target.segment() != null && target.timing() != -1 && target.f0() != 0) {
            String newF0 = "(" + target.timing() + "," + target.f0() + ")";
            if (target.segment().hasAttribute("f0")) {
                String oldF0 = target.segment().getAttribute("f0");
                target.segment().setAttribute("f0", oldF0 + " " + newF0);
            } else {
                target.segment().setAttribute("f0", newF0);
            }
        }
    }

    /**
     * Add a <code>prosody</code> element in the current phrase, realising the
     * upstep or downstep formulated in <code>prosodyRule</code>, and enclosing
     * the given <code>syllable</code> and all other syllables until the end of
     * the phrase.
     */
    private void adaptProsody(Element prosodyRule, Element syllable) {
        Element phrase = (Element) DomUtils.getAncestor(syllable, MaryXML.PHRASE);
        Element first = (Element) DomUtils.getAncestor(syllable, MaryXML.TOKEN);
        if (DomUtils.hasAncestor(first, MaryXML.MTU)) {
            first = (Element) DomUtils.getHighestLevelAncestor(first, MaryXML.MTU);
        }
        Element lastSyl = DomUtils.getLastElementByTagName(phrase, MaryXML.SYLLABLE);
        Element last = (Element) DomUtils.getAncestor(lastSyl, MaryXML.TOKEN);
        if (DomUtils.hasAncestor(last, MaryXML.MTU)) {
            last = (Element) DomUtils.getHighestLevelAncestor(last, MaryXML.MTU);
        }
        Element newProsody = DomUtils.encloseNodesWithNewElement(first, last, MaryXML.PROSODY);
        newProsody.setAttribute("range", prosodyRule.getAttribute("range"));
        determineProsodicSettings(newProsody);
        determineProsodyTopBaseConf(newProsody);
        // And now, if there are any prosody tags enclosed by the new
        // prosody tag, their topbaseconf needs to be calculated again
        TreeWalker tw =
                ((DocumentTraversal) newProsody.getOwnerDocument()).createTreeWalker(
                        newProsody,
                        NodeFilter.SHOW_ELEMENT,
                        new NameNodeFilter(MaryXML.PROSODY),
                        false);
        Element p = null;
        while ((p = (Element) tw.nextNode()) != null) {
            determineProsodyTopBaseConf(p);
        }
    }

    private Element getToken(Element segmentOrSyllable) {
        return (Element) DomUtils.getAncestor(segmentOrSyllable, MaryXML.TOKEN);
    }

    private Element getSyllable(Element segment) {
        return (Element) DomUtils.getAncestor(segment, MaryXML.SYLLABLE);
    }

    private boolean hasAccent(Element token) {
        String accent = token.getAttribute("accent").toUpperCase();
        // Is it a known / valid accent:
        return tobiMap.containsKey(accent);
    }

    /**
     * Search for boundary and syllable elements following the given syllable.
     * If the next matching element found is a boundary with breakindex
     * <code>minBreakindex</code> or larger, return true; otherwise,
     * return false.
     * If there is no next node, return true.
     */
    private boolean isLastBeforeBoundary(Element syllable, int minBreakindex) {
        Document doc = syllable.getOwnerDocument();
        Element sentence = (Element) DomUtils.getAncestor(syllable, MaryXML.SENTENCE);
        TreeWalker tw =
                ((DocumentTraversal) doc).createTreeWalker(
                        sentence,
                        NodeFilter.SHOW_ELEMENT,
                        new NameNodeFilter(MaryXML.SYLLABLE, MaryXML.BOUNDARY),
                        false);
        tw.setCurrentNode(syllable);
        Element next = (Element) tw.nextNode();
        if (next == null) {
            // no matching node after syllable --
            // we must be in a final position.
            return true;
        }
        if (next.getNodeName().equals(MaryXML.BOUNDARY)) {
            if (getBreakindex(next) >= minBreakindex)
                return true;
        }
        // This syllable is either followed by another syllable or
        // by a boundary with breakindex < minBreakindex
        return false;
    }

    private boolean isMajIPFinal(Element syllable) {
        // If this syllable is followed by a boundary with breakindex
        // 4 or above, return true.
        return isLastBeforeBoundary(syllable, 4);
    }

    private boolean isMinipFinal(Element syllable) {
        // If this syllable is followed by a boundary with breakindex
        // 3 or above, return true.
        return isLastBeforeBoundary(syllable, 3);
    }

    private boolean isWordFinal(Element syllable) {
        Element e = syllable;
        while (e != null) {
            e = DomUtils.getNextSiblingElement(e);
            if (e != null && e.getNodeName().equals(MaryXML.SYLLABLE))
                return false;
        }
        return true;
    }

    private boolean isWordMedial(Element syllable) {
        return !(isWordFinal(syllable) || isWordInitial(syllable));

    }

    private boolean isWordInitial(Element syllable) {
        Element e = syllable;
        while (e != null) {
            e = DomUtils.getPreviousSiblingElement(e);
            if (e != null && e.getNodeName().equals(MaryXML.SYLLABLE))
                return false;
        }
        return true;
    }

    private boolean isInOnset(Element segment) {
        Allophone ph = allophoneSet.getAllophone(segment.getAttribute("p"));
        assert ph != null;
        if (ph.isSyllabic()) {
            return false;
        }
        // OK, segment is not syllabic. See if it is followed by a syllabic
        // segment:
        for (Element e = DomUtils.getNextSiblingElement(segment);
             e != null;
             e = DomUtils.getNextSiblingElement(e)) {
            ph = allophoneSet.getAllophone(e.getAttribute("p"));
            assert ph != null;
            if (ph.isSyllabic()) {
                return true;
            }
        }
        return false;
    }

    private boolean isInNucleus(Element segment) {
        Allophone ph = allophoneSet.getAllophone(segment.getAttribute("p"));
        assert ph != null;
        return ph.isSyllabic();
    }

    private boolean isInCoda(Element segment) {
        Allophone ph = allophoneSet.getAllophone(segment.getAttribute("p"));
        assert ph != null;
        if (ph.isSyllabic()) {
            return false;
        }
        // OK, segment is not syllabic. See if it is preceded by a syllabic
        // segment:
        for (Element e = DomUtils.getPreviousSiblingElement(segment);
             e != null;
             e = DomUtils.getPreviousSiblingElement(e)) {
            ph = allophoneSet.getAllophone(e.getAttribute("p"));
            assert ph != null;
            if (ph.isSyllabic()) {
                return true;
            }
        }
        return false;
    }

    private int getBreakindex(Element boundary) {
        int breakindex = 0;
        try {
            breakindex = Integer.parseInt(boundary.getAttribute("breakindex"));
        } catch (NumberFormatException e) {
            Log.w(Mary.LOG, "Unexpected breakindex value `" + boundary.getAttribute("breakindex") + "'");
        }
        return breakindex;
    }

    /**
     * For a given token, find the stressed syllable. If no syllable has
     * primary stress, return the first syllable with secondary stress. If none
     * has secondary stress, return the first syllable in the token. If there
     * is no syllable in the token or the element given in the argument is not
     * a token element, return null.
     */
    private Element getStressedSyllable(Element token) {
        if (token == null || !token.getTagName().equals(MaryXML.TOKEN))
            return null;
        Element syl = DomUtils.getFirstElementByTagName(token, MaryXML.SYLLABLE);
        while (syl != null && !syl.getAttribute("stress").equals("1")) {
            syl = DomUtils.getNextSiblingElementByTagName(syl, MaryXML.SYLLABLE);
        }
        if (syl != null) {
            return syl;
        }
        // If we get here, there is no stressed syllable. As a fallback, use
        // the first syllable with secondary stress, or the first syllable if
        // none has secondary stress.
        Element first = DomUtils.getFirstElementByTagName(token, MaryXML.SYLLABLE);
        Element secondary = first;
        while (secondary != null && !secondary.getAttribute("stress").equals("2")) {
            secondary = DomUtils.getNextSiblingElementByTagName(secondary, MaryXML.SYLLABLE);
        }
        if (secondary != null) {
            return secondary;
        }
        return first;
    }

    /**
     * For a syllable, return the first child segment which is a nucleus
     * segment. Return <code>null</code> if there is no such segment.
     */
    private Element getNucleus(Element syllable) {
        if (syllable == null || !syllable.getTagName().equals(MaryXML.SYLLABLE))
            return null;
        Element seg = DomUtils.getFirstElementByTagName(syllable, MaryXML.PHONE);
        while (seg != null && !isInNucleus(seg)) {
            seg = DomUtils.getNextSiblingElementByTagName(seg, MaryXML.PHONE);
        }
        return seg;
    }

    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    /////////////////////////// Helper Classes ///////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    static class ProsodicSettings {
        // Relative settings: 100 = 100% = no change
        int rate;
        int accentProminence;
        int accentSlope;
        int numberOfPauses;
        int pauseDuration;
        int vowelDuration;
        int plosiveDuration;
        int fricativeDuration;
        int nasalDuration;
        int liquidDuration;
        int glideDuration;
        int volume;

        ProsodicSettings() {
            this.rate = 100;
            this.accentProminence = 100;
            this.accentSlope = 100;
            this.numberOfPauses = 100;
            this.pauseDuration = 100;
            this.vowelDuration = 100;
            this.plosiveDuration = 100;
            this.fricativeDuration = 100;
            this.nasalDuration = 100;
            this.liquidDuration = 100;
            this.glideDuration = 100;
            this.volume = 50;
        }

        ProsodicSettings(
                int rate,
                int accentProminence,
                int accentSlope,
                int numberOfPauses,
                int pauseDuration,
                int vowelDuration,
                int plosiveDuration,
                int fricativeDuration,
                int nasalDuration,
                int liquidDuration,
                int glideDuration,
                int volume) {
            this.rate = rate;
            this.accentProminence = accentProminence;
            this.accentSlope = accentSlope;
            this.numberOfPauses = numberOfPauses;
            this.pauseDuration = pauseDuration;
            this.vowelDuration = vowelDuration;
            this.plosiveDuration = plosiveDuration;
            this.fricativeDuration = fricativeDuration;
            this.nasalDuration = nasalDuration;
            this.liquidDuration = liquidDuration;
            this.glideDuration = glideDuration;
            this.volume = volume;
        }

        int rate() {
            return rate;
        }

        int accentProminence() {
            return accentProminence;
        }

        int accentSlope() {
            return accentSlope;
        }

        int numberOfPauses() {
            return numberOfPauses;
        }

        int pauseDuration() {
            return pauseDuration;
        }

        int vowelDuration() {
            return vowelDuration;
        }

        int plosiveDuration() {
            return plosiveDuration;
        }

        int fricativeDuration() {
            return fricativeDuration;
        }

        int nasalDuration() {
            return nasalDuration;
        }

        int liquidDuration() {
            return liquidDuration;
        }

        int glideDuration() {
            return glideDuration;
        }

        int volume() {
            return volume;
        }

        void setRate(int value) {
            rate = value;
        }

        void setAccentProminence(int value) {
            accentProminence = value;
        }

        void setAccentSlope(int value) {
            accentSlope = value;
        }

        void setNumberOfPauses(int value) {
            numberOfPauses = value;
        }

        void setPauseDuration(int value) {
            pauseDuration = value;
        }

        void setVowelDuration(int value) {
            vowelDuration = value;
        }

        void setPlosiveDuration(int value) {
            plosiveDuration = value;
        }

        void setFricativeDuration(int value) {
            fricativeDuration = value;
        }

        void setNasalDuration(int value) {
            nasalDuration = value;
        }

        void setLiquidDuration(int value) {
            liquidDuration = value;
        }

        void setGlideDuration(int value) {
            glideDuration = value;
        }

        void setVolume(int value) {
            volume = value;
        }

    }

    static class TopBaseConfiguration {
        int topStart;
        int topEnd;
        int baseStart;
        int baseEnd;
        int startTime;
        int endTime;
        double topSlope;
        double baseSlope;

        TopBaseConfiguration(int topStart, int topEnd, int baseStart, int baseEnd) {
            this(topStart, topEnd, baseStart, baseEnd, 0, 0);
        }

        TopBaseConfiguration(int topStart, int topEnd, int baseStart, int baseEnd, int startTime, int endTime) {
            this.topStart = topStart;
            this.topEnd = topEnd;
            this.baseStart = baseStart;
            this.baseEnd = baseEnd;
            this.startTime = startTime;
            this.endTime = endTime;
            if (startTime != endTime) { // can calculate slope
                topSlope = ((double) topEnd - topStart) / (endTime - startTime);
                baseSlope = ((double) baseEnd - baseStart) / (endTime - startTime);
            } else {
                topSlope = 0;
                baseSlope = 0;
            }
        }

        int topStart() {
            return topStart;
        }

        int topEnd() {
            return topEnd;
        }

        int baseStart() {
            return baseStart;
        }

        int baseEnd() {
            return baseEnd;
        }

        int startTime() {
            return startTime;
        }

        int endTime() {
            return endTime;
        }

        void setTimes(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            if (startTime != endTime) { // can calculate slope
                topSlope = ((double) topEnd - topStart) / (endTime - startTime);
                baseSlope = ((double) baseEnd - baseStart) / (endTime - startTime);
            } else {
                topSlope = 0;
                baseSlope = 0;
            }
        }

        /**
         * Calculate the frequency of the topline at time <code>time</code>.
         * This is calculated as a linear function of topStart, topEnd and
         * time.
         */
        int toplineFrequency(int time) {
            if (time < startTime || time > endTime) {
                throw new RuntimeException(
                        "Invalid time " + time + " (startTime " + startTime + ", endTime " + endTime + ")");
            }
            return topStart + (int) (topSlope * (time - startTime));
        }

        /**
         * Calculate the frequency of the baseline at time <code>time</code>.
         * This is calculated as a linear function of baseStart, baseEnd and
         * time.
         */
        int baselineFrequency(int time) {
            if (time < startTime || time > endTime) {
                throw new RuntimeException(
                        "Invalid time " + time + "(startTime " + startTime + ", endTime " + endTime + ")");
            }
            return baseStart + (int) (baseSlope * (time - startTime));
        }
    }

    /**
     * A class representing an F0-time target.
     */
    static class Target {
        Element targetRule;
        Element segment;
        int timing;
        int f0;
        Target myStar;

        Target() {
            targetRule = null;
            segment = null;
            timing = -1;
            f0 = 0;
            myStar = null;
        }

        Target(Element targetRule, Element segment, int timing, int f0) {
            this.targetRule = targetRule;
            this.segment = segment;
            this.timing = timing;
            this.f0 = f0;
            myStar = null;
        }

        Element targetRule() {
            return targetRule;
        }

        Element segment() {
            return segment;
        }

        int timing() {
            return timing;
        }

        int f0() {
            return f0;
        }

        Target myStar() {
            return myStar;
        }

        void setTargetRule(Element targetRule) {
            this.targetRule = targetRule;
        }

        void setSegment(Element segment) {
            this.segment = segment;
        }

        void setTiming(int timing) {
            this.timing = timing;
        }

        void setF0(int f0) {
            this.f0 = f0;
        }

        void setMyStar(Target star) {
            this.myStar = star;
        }

        String type() {
            if (targetRule != null)
                return targetRule.getAttribute("type");
            else
                return "";
        }


        /**
         * Get the target time relative to the beginning of the phrase, on the
         * same scale as that used by the segment "end" attributes.
         *
         * @return the target time, or -1 if the time cannot be determined.
         */
        int getTargetTime() {
            if (segment == null || timing == -1)
                return -1;
            int end = getSegmentEndInMillis(segment);
            if (end == -1) {
                return -1;
            }
            int d = -1;
            try {
                d = Integer.parseInt(segment.getAttribute("d"));
            } catch (NumberFormatException e) {
                return -1;
            }
            // The target time is:
            // t = end - d + (timing/100 * d) = end - (1 - timing/100) * d
            return end - (100 - timing) * d / 100;
        }

        /**
         * Set the target time relative to the beginning of the phrase, on the
         * same scale as that used by the segment "end" attributes. Adjust this
         * target's segment and timing accordingly. This is done as possible --
         * in particular in the presence of pauses, the target is only shifted towards
         * the border of the pause but not into or beyond it.
         *
         * @return true on success, false on failure.
         */
        boolean setTargetTime(int targetTime) {
            if (targetTime < 0)
                return false;
            int currentTargetTime = getTargetTime();
            Element seg = segment;
            try {
                if (targetTime < currentTargetTime) {
                    while (seg != null
                            && getSegmentEndInMillis(seg) - Integer.parseInt(seg.getAttribute("d"))
                            > targetTime) {
                        Element s = getPreviousSegment(seg);
                        // Check for "holes": If last start time (end-d) is too large but
                        // next end time is too small, there is a small pause between the
                        // two segments
                        // => stay at the side of the pause closer to the original
                        if (s != null && getSegmentEndInMillis(s) < targetTime) {
                            targetTime =
                                    getSegmentEndInMillis(seg) - Integer.parseInt(seg.getAttribute("d"));
                            break; // keep seg, forget about s
                        } else {
                            seg = s;
                        }
                    }
                } else { // targetTime > currentTargetTime
                    while (seg != null && getSegmentEndInMillis(seg) < targetTime) {
                        Element s = getNextSegment(seg);
                        // Check for "holes": If last end time is too small but
                        // next start time (end-d) is too large, there is a small pause
                        // between the two segments
                        // => stay at the side of the pause closer to the original
                        if (s != null
                                && getSegmentEndInMillis(s) - Integer.parseInt(s.getAttribute("d"))
                                > targetTime) {
                            targetTime = getSegmentEndInMillis(seg);
                            break; // keep seg, forget about s
                        } else {
                            seg = s;
                        }
                    }
                }
                if (seg != null) {
                    // newTiming = (1 - (end - targetTime) / d) * 100
                    int newTiming =
                            100
                                    - (100 * (getSegmentEndInMillis(seg) - targetTime))
                                    / Integer.parseInt(seg.getAttribute("d"));
                    segment = seg;
                    timing = newTiming;
                    assert timing >= 0 && timing <= 100;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

    }

}


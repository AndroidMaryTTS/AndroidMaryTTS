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
package com.sun.speech.freetts.en;

import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.PathExtractor;
import com.sun.speech.freetts.PathExtractorImpl;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.Voice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * Calculates the F0 curve for an utterance based on the Black and
 * Hunt article "Generating F0 Contours from ToBI Labels Using Linear
 * Regression," ICSLP96, vol.&nbsp;3, pp 1385-1388, Philadelphia,
 * PA.&nbsp;1996.
 */
public class ContourGenerator implements UtteranceProcessor {
    private final static PathExtractor endPath =
            new PathExtractorImpl("R:SylStructure.daughter.R:Segment.p.end",
                    true);
    private final static PathExtractor lastDaughterEndPath =
            new PathExtractorImpl("R:SylStructure.daughtern.end",
                    true);
    private final static PathExtractor postBreakPath =
            new PathExtractorImpl("R:SylStructure.daughter.R:Segment.p.name",
                    true);
    private final static PathExtractor preBreakPath =
            new PathExtractorImpl("R:SylStructure.daughtern.R:Segment.n.name",
                    true);
    private final static PathExtractor vowelMidPath =
            new PathExtractorImpl("R:Segment.p.end",
                    true);
    private final static PathExtractor localF0Shift =
            new PathExtractorImpl(
                    "R:SylStructure.parent.R:Token.parent.local_f0_shift", true);
    private final static PathExtractor localF0Range =
            new PathExtractorImpl(
                    "R:SylStructure.parent.R:Token.parent.local_f0_range", true);

    private final float modelMean;
    private final float modelStddev;
    private F0ModelTerm[] terms = {null};

    /**
     * Creates a ContourGenerator utterance processor.
     *
     * @param url         source of the data
     * @param modelMean   the average frequency
     * @param modelStddev the std deviation of the frequency
     * @throws IOException if an error occurs while loading data
     */
    public ContourGenerator(URL url,
                            float modelMean, float modelStddev)
            throws IOException {
        this.modelMean = modelMean;
        this.modelStddev = modelStddev;

        List termsList = new ArrayList();

        String line;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream()));
        line = reader.readLine();
        while (line != null) {
            if (!line.startsWith("***")) {
                parseAndAdd(termsList, line);
            }
            line = reader.readLine();
        }
        terms = (F0ModelTerm[]) termsList.toArray(terms);
        reader.close();
    }

    /**
     * Generates the F0 contour for the utterance.
     *
     * @param utterance the utterance to process
     * @throws ProcessException if an <code>IOException</code> is
     *                          thrown during the processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        float lend = 0.0f;
        float mean;
        float stddev;
        float localMean;
        float localStddev;
        Object tval;

        assert utterance.getRelation(Relation.SYLLABLE_STRUCTURE) != null;
        assert utterance.getRelation(Relation.SYLLABLE) != null;
        assert utterance.getRelation(Relation.TARGET) == null;
        mean = utterance.getVoice().getPitch();
        mean *= utterance.getVoice().getPitchShift();
        stddev = utterance.getVoice().getPitchRange();

        Relation target = utterance.createRelation(Relation.TARGET);
        for (Item syllable =
             utterance.getRelation(Relation.SYLLABLE).getHead();
             syllable != null;
             syllable = syllable.getNext()) {

            if (syllable.getItemAs(Relation.SYLLABLE_STRUCTURE).hasDaughters()) {

                tval = localF0Shift.findFeature(syllable);
                localMean = Float.parseFloat(tval.toString());

                if (localMean == 0.0) {
                    localMean = mean;
                } else {
                    localMean *= mean;
                }

                tval = localF0Range.findFeature(syllable);
                localStddev = Float.parseFloat(tval.toString());

                if (localStddev == 0.0) {
                    localStddev = stddev;
                }

                Interceptor interceptor = applyLrModel(syllable);
                if (isPostBreak(syllable)) {
                    lend = mapF0(interceptor.start, localMean, localStddev);
                }

                Float val = (Float) endPath.findFeature(syllable);
                // assert val != null;
                // don't mind null ptr exception
                addTargetPoint(target, val.floatValue(),
                        mapF0((interceptor.start + lend) / 2.0f,
                                localMean, localStddev));
                addTargetPoint(target, vowelMid(syllable),
                        mapF0(interceptor.mid, localMean, localStddev));
                lend = mapF0(interceptor.end, localMean, localStddev);
                if (isPreBreak(syllable)) {
                    Float eval = (Float) lastDaughterEndPath.findFeature(
                            syllable);
                    addTargetPoint(target, eval.floatValue(),
                            mapF0(interceptor.end, localMean, localStddev));
                }
            }
        }

        if (utterance.getRelation(Relation.SEGMENT).getHead() != null) {
            Item first = target.getHead();
            if (first == null) {
                addTargetPoint(target, 0, mean);
            } else if (first.getFeatures().getFloat("pos") > 0) {
                Item newItem = first.prependItem(null);
                newItem.getFeatures().setFloat("pos", 0.0f);
                newItem.getFeatures().setFloat(
                        "f0", first.getFeatures().getFloat("f0"));
            }
            Item last = target.getTail();
            Item lastSegment
                    = utterance.getRelation(Relation.SEGMENT).getTail();
            float segEnd = 0.0f;

            if (lastSegment != null) {
                segEnd = lastSegment.getFeatures().getFloat("end");
            }

            if (last.getFeatures().getFloat("pos") < segEnd) {
                addTargetPoint(target, segEnd, last.getFeatures().
                        getFloat("f0"));
            }
        }
    }

    /**
     * Applies the linear regression model.
     *
     * @param syllable the syllable to process
     * @return the 3 points for the syllable as an <code>Interceptor</code>
     */
    private Interceptor applyLrModel(Item syllable) {
        float fv = 0.0f;
        Interceptor interceptor = new Interceptor();
        interceptor.start = terms[0].start;
        interceptor.mid = terms[0].mid;
        interceptor.end = terms[0].end;

        for (int i = 1; i < terms.length; i++) {
            Object value = terms[i].findFeature(syllable);
            if (terms[i].type != null) {
                if (value.toString().equals(terms[i].type)) {
                    fv = 1.0f;
                } else {
                    fv = 0.0f;
                }
            } else {
                fv = Float.parseFloat(value.toString());
            }

            interceptor.start += fv * terms[i].start;
            interceptor.mid += fv * terms[i].mid;
            interceptor.end += fv * terms[i].end;
        }

        return interceptor;
    }

    /**
     * Returns the time point mid way in vowel in this syllable.
     *
     * @param syllable the syllable of interest
     * @return the time point mid way in vowel in this syllable
     */
    private final float vowelMid(Item syllable) {
        Voice voice = syllable.getUtterance().getVoice();
        Item firstSeg = syllable.getItemAs(
                Relation.SYLLABLE_STRUCTURE).getDaughter();
        Item segment;
        float val;

        for (segment = firstSeg; segment != null; segment = segment.getNext()) {
            // TODO refactor phone feature stuff like this so that
            // it can be understood.
            if ("+".equals(voice.getPhoneFeature(segment.toString(), "vc"))) {
                val = (segment.getFeatures().getFloat("end") +
                        ((Float) vowelMidPath.findFeature(segment)).floatValue()) / 2.0f;
                return val;
            }
        }

        if (firstSeg == null) {
            val = 0.0f;
        } else {
            val = (firstSeg.getFeatures().getFloat("end") +
                    ((Float) vowelMidPath.findFeature(firstSeg)).floatValue())
                    / 2.0f;
        }

        return val;
    }

    /**
     * Adds the target point at the given time to the given frequency
     * to the given relation.
     *
     * @param target the target of interest
     * @param pos    the time
     * @param f0     the frequency
     */
    private void addTargetPoint(Relation target, float pos, float f0) {
        Item item = target.appendItem();
        item.getFeatures().setFloat("pos", pos);
        if (f0 > 500.0) {
            item.getFeatures().setFloat("f0", 500.0f);
        } else if (f0 < 50.0) {
            item.getFeatures().setFloat("f0", 50.0f);
        } else {
            item.getFeatures().setFloat("f0", f0);
        }
    }

    /**
     * Determines if this syllable is following a break.
     *
     * @param syllable the syllable to check
     * @return <code>true</code> if this syllable is following a
     * break; otherwise <code>false</code>.
     */
    private final boolean isPostBreak(Item syllable) {
        return ((syllable.getPrevious() == null) ||
                "pau".equals(postBreakPath.findFeature(syllable)));
    }

    /**
     * Determines if this syllable is before a break.
     *
     * @param syllable the syllable to check
     * @return <code>true</code> if this syllable is before a
     * break; otherwise <code>false</code>.
     */
    private final boolean isPreBreak(Item syllable) {
        return ((syllable.getNext() == null) ||
                "pau".equals(preBreakPath.findFeature(syllable)));
    }

    /**
     * Maps the given value to the curve.
     *
     * @param val the value to map
     * @return the mapped value
     */
    private final float mapF0(float val, float mean, float stddev) {
        return ((((val - modelMean) / modelStddev) * stddev) + mean);
    }

    /**
     * Parses the line into an F0ModelTerm.
     *
     * @param list resulting F0ModelTerm is added to this list
     * @param line the string to parse
     */
    protected void parseAndAdd(List list, String line) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(line, " ");
            String feature = tokenizer.nextToken();
            float start = Float.parseFloat(tokenizer.nextToken());
            float mid = Float.parseFloat(tokenizer.nextToken());
            float end = Float.parseFloat(tokenizer.nextToken());
            String type = tokenizer.nextToken();

            if (type.equals("null")) {
                type = null;
            }

            list.add(new F0ModelTerm(feature, start, mid, end, type));
        } catch (NoSuchElementException nsee) {
            throw new Error("ContourGenerator: Error while parsing F0ModelTerm "
                    + nsee.getMessage());
        } catch (NumberFormatException nfe) {
            throw new Error("ContourGenerator: Bad float format "
                    + nfe.getMessage());
        }
    }

    /**
     * Returns the string representation of the object.
     *
     * @return the string representation of the object
     */
    public String toString() {
        return "ContourGenerator";
    }
}

/**
 * Represents a single term for the F0 model
 */
class F0ModelTerm {
    PathExtractor path;
    float start;
    float mid;
    float end;
    String type;

    /**
     * Constructs an F0ModelTerm.
     *
     * @param feature the feature of the term
     * @param start   the starting point of the term
     * @param mid     the mid-point of the term
     * @param end     the end point of the term
     * @param type    the type of the term
     */
    F0ModelTerm(String feature, float start, float mid,
                float end, String type) {
        path = new PathExtractorImpl(feature, true);
        this.start = start;
        this.mid = mid;
        this.end = end;
        this.type = type;
    }

    /**
     * Find the feature associated with the given item
     *
     * @param item the item of interest
     * @return the object representing the feature.
     */
    public Object findFeature(Item item) {
        return path.findFeature(item);
    }

    /**
     * Returns the string representation of the object
     *
     * @return the string representation of the object
     */
    public String toString() {
        return path.toString();
    }
}

/**
 * Represents an interceptor.
 */
class Interceptor {
    float start;
    float mid;
    float end;

    /**
     * Constructs the default interceptor
     */
    Interceptor() {
        start = 0.0f;
        mid = 0.0f;
        end = 0.0f;
    }

    /**
     * Returns the string representation of the object.
     *
     * @return the string representation of the object
     */
    public String toString() {
        return Float.toString(start) + " " +
                Float.toString(mid) + " " +
                Float.toString(end);
    }
}

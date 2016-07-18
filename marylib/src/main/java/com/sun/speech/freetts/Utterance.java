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
package com.sun.speech.freetts;

import com.sun.speech.freetts.util.SegmentRelationUtils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Holds all the data for an utterance to be spoken.
 * It is incrementally modified by various UtteranceProcessor
 * implementations.  An utterance contains a set of Features (essential
 * a set of properties) and a set of Relations. A Relation is an
 * ordered set of Item graphs. The utterance contains a set of
 * features and implements FeatureSet so that applications can set/get
 * features directly from the utterance. If a feature query is not
 * found in the utterance feature set, the query is forwarded to the
 * FeatureSet of the voice associated with the utterance.
 */
public class Utterance implements FeatureSet, Serializable {

    private Voice voice;
    private FeatureSetImpl features;
    private FeatureSetImpl relations;
    private Vector listeners = null;
    private boolean first;    // first in a connected series
    private boolean last;    // last in a connected series
    private FreeTTSSpeakable speakable;

    /**
     * Creates a new, empty utterance.
     *
     * @param voice the voice associated with the utterance
     */
    public Utterance(Voice voice) {
        this.voice = voice;
        listeners = new Vector();
        features = new FeatureSetImpl();
        relations = new FeatureSetImpl();
    }

    /**
     * Creates an utterance with the given set of tokenized text.
     *
     * @param voice     the voice associated with the utterance
     * @param tokenList the list of tokens for this utterance
     */
    public Utterance(Voice voice, List tokenList) {
        this(voice);
        setTokenList(tokenList);
    }

    /**
     * Returns the queueitem associated with this utterance.
     *
     * @return the queue item
     */
    public FreeTTSSpeakable getSpeakable() {
        return speakable;
    }

    /**
     * Sets the speakable item for this utterance.
     *
     * @param speakable the speakable item for this utterance
     */
    public void setSpeakable(FreeTTSSpeakable speakable) {
        this.speakable = speakable;
    }

    /**
     * Creates a new relation with the given name and adds it to this
     * utterance.
     *
     * @param name the name of the new relation
     * @return the newly created relation
     */
    public Relation createRelation(String name) {
        Relation relation = new Relation(name, this);
        relations.setObject(name, relation);
        return relation;
    }


    /**
     * Retrieves a relation from this utterance.
     *
     * @param name the name of the Relation
     * @return the relation or null if the relation is not found
     */
    public Relation getRelation(String name) {
        return (Relation) relations.getObject(name);
    }

    /**
     * Determines if this utterance contains a relation with the given
     * name.
     *
     * @param name the name of the relation of interest.
     */
    public boolean hasRelation(String name) {
        return relations.isPresent(name);
    }

    /**
     * Retrieves the Voice associated with this Utterance.
     *
     * @return the voice associated with this utterance.
     */
    public Voice getVoice() {
        return voice;
    }

    /**
     * Dumps this utterance in textual form.
     *
     * @param output        where to send the formatted output
     * @param pad           the initial padding
     * @param title         the title to print when dumping out the utterance
     * @param justRelations if true don't print voice features
     */
    public void dump(PrintWriter output, int pad, String title,
                     boolean justRelations) {
        output.println(" ============ " + title + " ========== ");
        if (!justRelations) {
            voice.dump(output, pad + 4, "Voice");
            features.dump(output, pad + 4, "Features");
        }
        relations.dump(output, pad + 4, "Relations");
        output.flush();
    }

    /**
     * Dumps this utterance in textual form.
     *
     * @param output where to send the formatted output
     * @param pad    the initial padding
     * @param title  the title to print when dumping out the utterance
     */
    public void dump(PrintWriter output, int pad, String title) {
        dump(output, pad, title, false);
    }

    /**
     * Dumps this utterance in textual form.
     *
     * @param output where to send the formatted output
     * @param title  the title to print when dumping out the utterance
     */
    public void dump(PrintWriter output, String title) {
        dump(output, 0, title, false);
    }

    /**
     * Dumps this utterance in textual form.
     *
     * @param title the title to print when dumping out the utterance
     */
    public void dump(String title) {
        dump(new PrintWriter(System.out), 0, title, false);
    }

    /**
     * Dumps the utterance in textual form
     *
     * @param title the title to print when dumping out the utterance
     */
    public void dumpRelations(String title) {
        dump(new PrintWriter(System.out), 0, title, true);
    }

    /**
     * Determines if the given feature is present. If the feature is
     * not present in the utterance, the feature set of the voice is
     * checked.
     *
     * @param name the name of the feature of interest
     * @return true if the named feature is present
     */
    public boolean isPresent(String name) {
        if (!features.isPresent(name)) {
            return getVoice().getFeatures().isPresent(name);
        } else {
            return true;
        }
    }

    /**
     * Removes the named feature from this set of features.
     *
     * @param name the name of the feature of interest
     */
    public void remove(String name) {
        features.remove(name);
    }

    /**
     * Convenience method that returns the named feature as a string.
     * If the named feature is not present in the utterance, then this
     * attempts to retrieve it from the voice.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     * @throws ClassCastException if theassociated value is not a
     *                            String
     */
    public String getString(String name) {
        if (!features.isPresent(name)) {
            return getVoice().getFeatures().getString(name);
        } else {
            return features.getString(name);
        }
    }

    /**
     * Convenience method that returns the named feature as a int.
     * If the named feature is not present in the utterance, then this
     * attempts to retrieve it from the voice.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     * @throws ClassCastException if the associated value is not an
     *                            int
     */
    public int getInt(String name) {
        if (!features.isPresent(name)) {
            return getVoice().getFeatures().getInt(name);
        } else {
            return features.getInt(name);
        }
    }

    /**
     * Convenience method that returns the named feature as a float.
     * If the named feature is not present in the utterance, then this
     * attempts to retrieve it from the voice.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     * @throws ClassCastException if the associated value is not a
     *                            float
     */
    public float getFloat(String name) {
        if (!features.isPresent(name)) {
            return getVoice().getFeatures().getFloat(name);
        } else {
            return features.getFloat(name);
        }
    }

    /**
     * Returns the named feature as an object.
     * If the named feature is not present in the utterance, then this
     * attempts to retrieve it from the voice.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     */
    public Object getObject(String name) {
        if (!features.isPresent(name)) {
            return getVoice().getFeatures().getObject(name);
        } else {
            return features.getObject(name);
        }
    }

    /**
     * Convenience method that sets the named feature as an int.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setInt(String name, int value) {
        features.setInt(name, value);
    }

    /**
     * Convenience method that sets the named feature as a float.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setFloat(String name, float value) {
        features.setFloat(name, value);
    }

    /**
     * Convenience method that sets the named feature as a String.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setString(String name, String value) {
        features.setString(name, value);
    }

    /**
     * Sets the named feature.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setObject(String name, Object value) {
        features.setObject(name, value);
    }

    /**
     * Returns the Item in the given Relation associated with the given time.
     *
     * @param relation the name of the relation
     * @param time     the time
     * @throws IllegalStateException if the Segment durations
     *                               have not been calculated in the Utterance or if the
     *                               given relation is not present in the Utterance
     */
    public Item getItem(String relation, float time) {

        Relation segmentRelation = null;

        if ((segmentRelation = getRelation(Relation.SEGMENT)) == null) {
            throw new IllegalStateException
                    ("Utterance has no Segment relation");
        }

        String pathName = null;

        if (relation.equals(Relation.SEGMENT)) {
            // do nothing
        } else if (relation.equals(Relation.SYLLABLE)) {
            pathName = "R:SylStructure.parent.R:Syllable";
        } else if (relation.equals(Relation.SYLLABLE_STRUCTURE)) {
            pathName = "R:SylStructure.parent.parent";
        } else if (relation.equals(Relation.WORD)) {
            pathName = "R:SylStructure.parent.parent.R:Word";
        } else if (relation.equals(Relation.TOKEN)) {
            pathName = "R:SylStructure.parent.parent.R:Token.parent";
        } else if (relation.equals(Relation.PHRASE)) {
            pathName = "R:SylStructure.parent.parent.R:Phrase.parent";
        } else {
            throw new IllegalArgumentException
                    ("Utterance.getItem(): relation cannot be " + relation);
        }

        PathExtractor path = new PathExtractorImpl(pathName, false);

        // get the Item in the Segment Relation with the given time
        Item segmentItem = SegmentRelationUtils.getItem
                (segmentRelation, time);

        if (relation.equals(Relation.SEGMENT)) {
            return segmentItem;
        } else if (segmentItem != null) {
            return path.findItem(segmentItem);
        } else {
            return null;
        }
    }

    /**
     * Returns the duration of this Utterance in seconds. It does this
     * by looking at last Item in the following Relations, in this order:
     * Segment, Target. If none of these Relations exist, or if these
     * Relations contain no Items, an IllegalStateException will be thrown.
     *
     * @return the duration of this Utterance in seconds
     */
    public float getDuration() {
        float duration = -1;
        if ((duration = getLastFloat(Relation.SEGMENT, "end")) == -1) {
            if ((duration = getLastFloat(Relation.TARGET, "pos")) == -1) {
                throw new IllegalStateException
                        ("Utterance: Error finding duration");
            }
        }
        return duration;
    }

    /**
     * Returns the float feature of the last Item in the named
     * Relation.
     *
     * @return the float feature of the last Item in the named Relation,
     * or -1 otherwise
     */
    private float getLastFloat(String relationName, String feature) {
        float duration = -1;
        Relation relation;
        if ((relation = getRelation(relationName)) != null) {
            Item lastItem = relation.getTail();
            if (lastItem != null) {
                duration = lastItem.getFeatures().getFloat(feature);
            }
        }
        return duration;
    }

    /**
     * Sets the token list for this utterance. Note that this could be
     * optimized by turning the token list directly into the token
     * relation. 
     *
     * <p>[[[ TODO: future optimization, turn this into a token
     *    relation directly ]]]
     *
     * @param tokenList the tokenList
     */
    /*
    private void setTokenList(List tokenList) {
	StringBuffer sb = new StringBuffer();
	for (Iterator i = tokenList.iterator(); i.hasNext(); ) {
	    sb.append(i.next().toString());
	}
	setString("input_text", sb.toString());
    }
    */

    /**
     * Sets the input text for this utterance
     *
     * @param tokenList the set of tokens for this utterance
     */
    private void setInputText(List tokenList) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tokenList.iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
        }
        setString("input_text", sb.toString());
    }


    /**
     * Sets the token list for this utterance. Note that this could be
     * optimized by turning the token list directly into the token
     * relation.
     *
     * @param tokenList the tokenList
     */
    private void setTokenList(List tokenList) {
        setInputText(tokenList);

        Relation relation = createRelation(Relation.TOKEN);
        for (Iterator i = tokenList.iterator(); i.hasNext(); ) {
            Token token = (Token) i.next();
            String tokenWord = token.getWord();

            if (tokenWord != null && tokenWord.length() > 0) {
                Item item = relation.appendItem();

                FeatureSet featureSet = item.getFeatures();
                featureSet.setString("name", tokenWord);
                featureSet.setString("whitespace", token.getWhitespace());
                featureSet.setString("prepunctuation",
                        token.getPrepunctuation());
                featureSet.setString("punc", token.getPostpunctuation());
                featureSet.setString("file_pos",
                        String.valueOf(token.getPosition()));
                featureSet.setString("line_number",
                        String.valueOf(token.getLineNumber()));

            }
        }
    }

    /**
     * Returns true if this utterance is the first is a series of
     * utterances.
     *
     * @return true if this is the first utterance in a series of
     * connected utterances.
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * Sets this utterance as the first in a series.
     *
     * @param first if true, the item is the first in a series
     */
    public void setFirst(boolean first) {
        this.first = first;
    }

    /**
     * Returns true if this utterance is the last is a series of
     * utterances.
     *
     * @return true if this is the last utterance in a series of
     * connected utterances.
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Sets this utterance as the last in a series.
     *
     * @param last if true, the item is the last in a series
     */
    public void setLast(boolean last) {
        this.last = last;
    }
}

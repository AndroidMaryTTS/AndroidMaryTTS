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
package com.sun.speech.freetts.diphone;

import com.sun.speech.freetts.FeatureSet;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.relp.Sample;
import com.sun.speech.freetts.relp.SampleInfo;

import java.io.IOException;
import java.net.URL;


/**
 * Generates the Unit Relation of an Utterance from the
 * Segment Relation.
 */
public class DiphoneUnitSelector implements UtteranceProcessor {

    // the UnitDatabase to use
    private DiphoneUnitDatabase diphoneDatabase;

    // The Utterance that this DiphoneUnitSelector works on
    // private Utterance utterance;


    /**
     * Constructs a DiphoneUnitSelector.
     *
     * @param url the URL for the unit database. If the URL path ends
     *            with a '.bin' it is assumed that the DB is a binary database,
     *            otherwise, its assumed that its a text database1
     * @throws IOException if an error occurs while loading the
     *                     database
     */
    public DiphoneUnitSelector(URL url) throws IOException {
        if (url == null) {
            throw new IOException("Can't load unit database");
        }
        boolean binary = url.getPath().endsWith(".bin");
        diphoneDatabase = new DiphoneUnitDatabase(url, binary);
    }

    /**
     * Generates the Unit Relation from the Segment Relation.
     *
     * @param utterance the utterance to generate the Unit Relation
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {

        if (utterance.getRelation(Relation.SEGMENT) == null) {
            throw new IllegalStateException
                    ("DiphoneUnitSelector: Segment relation does not exist");
        }

        utterance.setObject(SampleInfo.UTT_NAME,
                diphoneDatabase.getSampleInfo());
        createUnitRelation(utterance);
    }

    /**
     * Creates the Unit Relation in the given utterance from
     * the diphone units and their some associated information from
     * the units database.
     *
     * @param utterance the utterance that gets the new unit relation
     */
    private void createUnitRelation(Utterance utterance) {

        Item segmentItem0, segmentItem1;
        float end0, end1;
        int targetEnd;

        Item unitItem0, unitItem1;

        String diphoneName;
        Diphone diphone;

        Relation unitRelation = utterance.createRelation(Relation.UNIT);
        Relation segmentRelation = utterance.getRelation(Relation.SEGMENT);

        for (segmentItem0 = segmentRelation.getHead();
             segmentItem0 != null && segmentItem0.getNext() != null;
             segmentItem0 = segmentItem1) {
            segmentItem1 = segmentItem0.getNext();
            diphoneName = segmentItem0.getFeatures().getString("name") + "-" +
                    segmentItem1.getFeatures().getString("name");


            // First half of diphone
            end0 = segmentItem0.getFeatures().getFloat("end");
            targetEnd = (int) (end0 *
                    diphoneDatabase.getSampleInfo().getSampleRate());
            unitItem0 = createUnitItem(unitRelation, diphoneName, targetEnd, 1);
            segmentItem0.addDaughter(unitItem0);

            // Second half of diphone
            end1 = segmentItem1.getFeatures().getFloat("end");
            targetEnd = (int) (((end0 + end1) / 2.0) *
                    diphoneDatabase.getSampleInfo().getSampleRate());
            unitItem1 = createUnitItem(unitRelation, diphoneName, targetEnd, 2);
            segmentItem1.addDaughter(unitItem1);
        }
    }

    /**
     * Returns a new Item (a Unit) in the given Relation, and
     * sets the new Item to the given diphone name, target end,
     * unit entry (index in the database), and unit part (1 or 2).
     *
     * @param unitRelation the relation that gets the new item
     * @param diphoneName  the name of the dipohone
     * @param targetEnd    the time at the end of this unit
     * @param unitPart     the item can be in the first(1) or second part (2)
     */
    private Item createUnitItem(Relation unitRelation,
                                String diphoneName,
                                int targetEnd,
                                int unitPart) {
        Diphone diphone = diphoneDatabase.getUnit(diphoneName);
        if (diphone == null) {
            System.err.println
                    ("FreeTTS: unit database failed to find entry for: " +
                            diphoneName);
        }
        Item unit = unitRelation.appendItem();
        FeatureSet unitFeatureSet = unit.getFeatures();

        unitFeatureSet.setString("name", diphoneName);
        unitFeatureSet.setInt("target_end", targetEnd);
        unitFeatureSet.setObject("unit", new DiphoneUnit(diphone, unitPart));
        // unitFeatureSet.setInt("unit_part", unitPart);
        return unit;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "DiphoneUnitSelector";
    }
}


/**
 * A wrapper around the Diphone class that turns the
 * diphone into a unit
 */
class DiphoneUnit implements com.sun.speech.freetts.Unit {

    private Diphone diphone;
    private int unitPart;

    /**
     * Contructs a diphone unit given a diphone and unit part.
     *
     * @param diphone  the diphone to wrap
     * @param unitPart which half (1 or 2) does this unit represent
     */
    public DiphoneUnit(Diphone diphone, int unitPart) {
        this.diphone = diphone;
        this.unitPart = unitPart;
    }

    /**
     * Returns the name of this Unit.
     *
     * @return the name of the unit
     */
    public String getName() {
        return diphone.getName();
    }

    /**
     * Returns the size of the unit.
     *
     * @return the size of the unit
     */
    public int getSize() {
        return diphone.getUnitSize(unitPart);
    }

    /**
     * Retrieves the nearest sample.
     *
     * @param index the ideal index
     * @return the nearest Sample
     */
    public Sample getNearestSample(float index) {
        return diphone.nearestSample(index, unitPart);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return getName();
    }


    /**
     * Dumps this unit.
     */
    public void dump() {
        diphone.dump();
    }
}

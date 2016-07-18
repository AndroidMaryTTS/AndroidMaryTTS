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
package com.sun.speech.freetts.util;

import com.sun.speech.freetts.FeatureSet;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.Relation;


/**
 * Provides a set of utilities for the SegmentRelation. A
 * SegmentRelation is a Relation, but has
 * features specific to Segments.
 */
public class SegmentRelationUtils {

    /**
     * Returns the Item in the Segment Relation that corresponds to the given
     * time.
     *
     * @param segmentRelation the segmentRelation of interest
     * @param time            the time
     */
    public static Item getItem(Relation segmentRelation, float time) {

        Item lastSegment = segmentRelation.getTail();

        // if given time is closer to the front than the end, search from
        // the front; otherwise, start search from end
        // this might not be the best strategy though

        float lastSegmentEndTime = SegmentRelationUtils.getSegmentEnd
                (lastSegment);

        if (time < 0 || lastSegmentEndTime < time) {
            return null;
        } else if (lastSegmentEndTime - time > time) {
            return SegmentRelationUtils.findFromFront(segmentRelation, time);
        } else {
            return SegmentRelationUtils.findFromEnd(segmentRelation, time);
        }
    }

    /**
     * Returns the value of the feature <code>end</code> of
     * the given Segment Item.
     *
     * @param segment the Segment Item
     * @return the <code>end</code> feature of the Segment
     */
    public static float getSegmentEnd(Item segment) {
        FeatureSet segmentFeatureSet = segment.getFeatures();
        return segmentFeatureSet.getFloat("end");
    }

    /**
     * Starting from the front of the given Segment Relation, finds the Item
     * that corresponds to the given time.
     *
     * @param segmentRelation the Segment Relation to search
     * @param time            the time of the Segment Item
     * @return the Segment Item
     */
    public static Item findFromFront(Relation segmentRelation, float time) {
        Item item = segmentRelation.getHead();

        while (item != null &&
                time > SegmentRelationUtils.getSegmentEnd(item)) {
            item = item.getNext();
        }

        return item;
    }

    /**
     * Starting from the end of the given Segment Relation, go backwards
     * to find the Item that corresponds to the given time.
     *
     * @param segmentRelation the Segment Relation to search
     * @param time            the time of the Segment Item
     * @return the Segment Item
     */
    public static Item findFromEnd(Relation segmentRelation, float time) {
        Item item = segmentRelation.getTail();

        while (item != null &&
                SegmentRelationUtils.getSegmentEnd(item) > time) {
            item = item.getPrevious();
        }

        if (item != segmentRelation.getTail()) {
            item = item.getNext();
        }

        return item;
    }
}

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
import com.sun.speech.freetts.relp.LPCResult;
import com.sun.speech.freetts.relp.SampleInfo;

/**
 * Calculates pitchmarks. This is an utterance processor that expects
 * the utterance to have a target relation. It will create an
 * LPCResult and add it to the utterance based upon features of the
 * target relation.
 *
 * @see LPCResult
 * @see Relation
 * @see SampleInfo
 */
public class DiphonePitchmarkGenerator implements UtteranceProcessor {

    /**
     * Generates the LPCResult for this utterance.
     *
     * @param utterance the utterance to process
     * @throws ProcessException      if an error occurs while processing
     *                               the utterance
     * @throws IllegalStateException if the given utterance has no
     *                               relation named Relation.TARGET or a feature named
     *                               SampleInfo.UTT_NAME
     */
    public void processUtterance(Utterance utterance) throws ProcessException {

        // precondition that must be satisfied
        Relation targetRelation = utterance.getRelation(Relation.TARGET);
        if (targetRelation == null) {
            throw new IllegalStateException
                    ("DiphonePitchmarkGenerator: Target relation does not exist");
        }

        SampleInfo sampleInfo;
        sampleInfo = (SampleInfo) utterance.getObject(SampleInfo.UTT_NAME);
        if (sampleInfo == null) {
            throw new IllegalStateException
                    ("DiphonePitchmarkGenerator: SampleInfo does not exist");
        }

        float pos, lpos = 0, f0, m = 0;
        final float lf0 = 120;

        double time = 0;
        int pitchMarks = 0;  // how many pitch marks

        LPCResult lpcResult;
        IntLinkedList timesList = new IntLinkedList();

        // first pass to count how many pitch marks will be required
        for (Item targetItem = targetRelation.getHead();
             targetItem != null; targetItem = targetItem.getNext()) {
            FeatureSet featureSet = targetItem.getFeatures();
            pos = featureSet.getFloat("pos");
            f0 = featureSet.getFloat("f0");
            if (time == pos) {
                continue;
            }
            m = (f0 - lf0) / pos;
            for (; time < pos; pitchMarks++) {
                time += 1 / (lf0 + (time * m));
                // save the time value in a list
                timesList.add((int) (time * sampleInfo.getSampleRate()));
            }
        }
        lpcResult = new LPCResult();
        // resize the number of frames to the number of pitchmarks
        lpcResult.resizeFrames(pitchMarks);

        pitchMarks = 0;

        int[] targetTimes = lpcResult.getTimes();

        // second pass puts the values in
        timesList.resetIterator();
        for (; pitchMarks < targetTimes.length; pitchMarks++) {
            targetTimes[pitchMarks] = timesList.nextInt();
        }
        utterance.setObject("target_lpcres", lpcResult);
    }


    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "DiphonePitchmarkGenerator";
    }
}

/**
 * Represents a linked list with each node of the list storing
 * a primitive int data type. Unlike the java.util.LinkedList, it avoids
 * the need to wrap the float number in a Float object. This avoids
 * unnecessary object creation, and is therefore faster and saves memory.
 * However, it does not implement the java.util.List interface.
 * <p/>
 * This linked list is used as a replacement for a simple array of
 * ints. Certain performance critical loops have had performance
 * issues due to the overhead associated with array index bounds
 * checking performed by the VM. Using this type of data structure
 * allowed the checking to be bypassed. Note however that we've seen
 * great improvement in compiler performance in this area such that we
 * may be able to revert to using an array without any performance
 * impact.
 * <p/>
 * [[[ TODO look at replacing this with a simple int array ]]]
 */
class IntLinkedList {
    private IntListNode head = null;
    private IntListNode tail = null;
    private IntListNode iterator = null;

    /**
     * Constructs an empty IntLinkedList.
     */
    public IntLinkedList() {
        head = null;
        tail = null;
        iterator = null;
    }

    /**
     * Adds the given float to the end of the list.
     *
     * @param val the float to add
     */
    public void add(int val) {
        IntListNode node = new IntListNode(val);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
    }

    /**
     * Moves the iterator to point to the front of the list.
     */
    public void resetIterator() {
        iterator = head;
    }

    /**
     * Returns the next float in the list, advances the iterator.
     * The <code>hasNext()</code> method MUST be called before calling
     * this method to check if the iterator is point to null,
     * otherwise NullPointerException will be thrown.
     *
     * @return the next value
     */
    public int nextInt() {
        int val = iterator.val;
        if (iterator != null) {
            iterator = iterator.next;
        }
        return val;
    }

    /**
     * Checks if there are more elements for the iterator.
     *
     * @return <code>true</code>  if there are more elements;
     * otherwise <code>false</code>
     */
    public boolean hasNext() {
        return (iterator != null);
    }
}

/**
 * Represents a node for the IntList
 */
class IntListNode {
    int val;
    IntListNode next;

    /**
     * Creates a node that wraps the given value.
     *
     * @param val the value to be contained in the list
     */
    public IntListNode(int val) {
        this.val = val;
        next = null;
    }
}

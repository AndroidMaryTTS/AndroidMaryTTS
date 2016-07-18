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

import com.sun.speech.freetts.util.Utilities;

import java.io.PrintWriter;

/**
 * Represents an ordered set of <code>Items</code> and their associated
 * children. A relation has a name and a list of items, and is
 * added to an <code>Utterance</code> via an <code>UtteranceProcessor</code>.
 */
public class Relation implements Dumpable {
    /**
     * Name of the relation that contains tokens from the original
     * input text.  This is the first thing to be added to the
     * utterance.
     *
     * @see #WORD
     */
    public static final String TOKEN = "Token";
    /**
     * Name of the relation that contains the normalized version of
     * the original input text.
     *
     * @see #TOKEN
     */
    public static final String WORD = "Word";
    /**
     * Name of the relation that groups elements from the Word relation
     * into phrases.
     */
    public static final String PHRASE = "Phrase";
    /**
     * Name of the relation that contains the ordered list of the
     * smallest units of speech (typically phonemes) for the entire
     * utterance.
     *
     * @see #SYLLABLE
     * @see #SYLLABLE_STRUCTURE
     * @see Segmenter
     */
    public static final String SEGMENT = "Segment";
    /**
     * Name of the relation that contains the description of the
     * syllables for the Utterance.  This is typically added to the
     * utterance at the same time as the <code>Segment</code> and
     * <code>SylStructure</code> relations.
     *
     * @see #SEGMENT
     * @see #SYLLABLE_STRUCTURE
     * @see Segmenter
     */
    public static final String SYLLABLE = "Syllable";
    /**
     * Name of the relation that contains the syllable structure
     * for the utterance.
     *
     * @see #SEGMENT
     * @see #SYLLABLE
     * @see Segmenter
     */
    public static final String SYLLABLE_STRUCTURE = "SylStructure";
    /**
     * Name of the relation that maps fundamental frequency targets
     * to absolute times for wave to be generated from the utterance.
     */
    public static final String TARGET = "Target";
    /**
     * Name of the relation that contains the ordered list of the
     * units from the unit database that will be used to create
     * the synthesized wave.
     */
    public static final String UNIT = "Unit";
    private String name;
    private Utterance owner;
    private Item head;
    private Item tail;

    /**
     * Creates a relation.
     *
     * @param name  the name of the Relation
     * @param owner the utterance that contains this relation
     */
    Relation(String name, Utterance owner) {
        this.name = name;
        this.owner = owner;
        head = null;
        tail = null;
    }

    /**
     * Retrieves the name of this Relation.
     *
     * @return the name of this Relation
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the head of the item list.
     *
     * @return the head item
     */
    public Item getHead() {
        return head;
    }

    /**
     * Sets the head of the item list.
     *
     * @param item the new head item
     */
    void setHead(Item item) {
        head = item;
    }

    /**
     * Gets the tail of the item list.
     *
     * @return the tail item
     */
    public Item getTail() {
        return tail;
    }

    /**
     * Sets the tail of the item list.
     *
     * @param item the new tail item
     */
    void setTail(Item item) {
        tail = item;
    }

    /**
     * Adds a new item to this relation. The item added does not share
     * its contents with any other item.
     *
     * @return the newly added item
     */
    public Item appendItem() {
        return appendItem(null);
    }

    /**
     * Adds a new item to this relation. The item added shares its
     * contents with the original item.
     *
     * @param originalItem the ItemContents that will be
     *                     shared by the new item
     * @return the newly added item
     */
    public Item appendItem(Item originalItem) {
        ItemContents contents;
        Item newItem;

        if (originalItem == null) {
            contents = null;
        } else {
            contents = originalItem.getSharedContents();
        }
        newItem = new Item(this, contents);
        if (head == null) {
            head = newItem;
        }

        if (tail != null) {
            tail.attach(newItem);
        }
        tail = newItem;
        return newItem;
    }


    /**
     * Returns the utterance that contains this relation.
     *
     * @return the utterance that contains this relation
     */
    public Utterance getUtterance() {
        return owner;
    }


    /**
     * Dumps this relation to the print writer.
     *
     * @param pw    the output stream
     * @param pad   the padding
     * @param title the title for the dump
     */
    public void dump(PrintWriter pw, int pad, String title) {
        Utilities.dump(pw, pad, "========= Relation: " + title +
                " =========");
        Item item = head;
        while (item != null) {
            item.dump(pw, pad + 4, title);
            item = item.getNext();
        }
    }
}

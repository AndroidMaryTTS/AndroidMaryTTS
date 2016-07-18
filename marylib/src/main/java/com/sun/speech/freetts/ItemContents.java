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

import java.io.PrintWriter;

/**
 * Contains the information that is shared between multiple items.
 */
public class ItemContents {
    private FeatureSetImpl features;
    private FeatureSetImpl relations;

    /**
     * Class Constructor.
     */
    public ItemContents() {
        features = new FeatureSetImpl();
        relations = new FeatureSetImpl();
    }

    /**
     * Adds the given item to the set of relations. Whenever an item
     * is added to a relation, it should add the name and the Item reference
     * to this set of name/item mappings. This allows an item to find
     * out the set of all relations that it is contained in.
     *
     * @param relationName the name of the relation
     * @param item         the item reference in the relation
     */
    public void addItemRelation(String relationName, Item item) {
        // System.out.println("AddItemRelation: " + relationName
        //                    + " item: " + item);
        relations.setObject(relationName, item);
    }

    /**
     * Removes the relation/item mapping from this ItemContents.
     *
     * @param relationName the name of the relation/item to remove
     */
    public void removeItemRelation(String relationName) {
        relations.remove(relationName);
    }

    // for debugging
    public void showRelations() {
        PrintWriter pw = new PrintWriter(System.out);
        relations.dump(pw, 0, "Contents relations", true);
        pw.flush();
    }

    /**
     * Given the name of a relation, returns the item the shares the
     * same ItemContents.
     *
     * @param relationName the name of the relation of interest
     * @return the item associated with this ItemContents in the named
     * relation, or null if it does not exist
     */
    public Item getItemRelation(String relationName) {
        return (Item) relations.getObject(relationName);
    }

    /**
     * Returns the feature set for this item contents.
     *
     * @return the FeatureSet for this contents
     */
    public FeatureSet getFeatures() {
        return features;
    }
}

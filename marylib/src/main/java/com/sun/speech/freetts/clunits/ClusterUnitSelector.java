/**
 * Portions Copyright 2001-2003 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.clunits;


import com.sun.speech.freetts.FeatureSet;
import com.sun.speech.freetts.FeatureSetImpl;
import com.sun.speech.freetts.Item;
import com.sun.speech.freetts.PathExtractor;
import com.sun.speech.freetts.PathExtractorImpl;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.cart.CART;
import com.sun.speech.freetts.relp.Sample;
import com.sun.speech.freetts.relp.SampleInfo;
import com.sun.speech.freetts.relp.SampleSet;

import java.io.IOException;
import java.net.URL;

import de.dfki.lt.freetts.ClusterUnitNamer;


/**
 * Generates the Unit Relation of an Utterance from the
 * Segment Relation.
 */
public class ClusterUnitSelector implements UtteranceProcessor {

    final static boolean DEBUG = false;
    private final static PathExtractor DNAME = new PathExtractorImpl(
            "R:SylStructure.parent.parent.name", true);
    private ClusterUnitDatabase clunitDB;
    private ClusterUnitNamer unitNamer;

    /**
     * Constructs a ClusterUnitSelector.
     *
     * @param url the URL for the unit database. If the URL path ends
     *            with a '.bin' it is assumed that the DB is a binary database,
     *            otherwise, its assumed that its a text database1
     * @throws IOException if an error occurs while loading the
     *                     database
     */
    public ClusterUnitSelector(URL url) throws IOException {
        this(url, null);
    }

    /**
     * Constructs a ClusterUnitSelector.
     *
     * @param url       the URL for the unit database. If the URL path ends
     *                  with a '.bin' it is assumed that the DB is a binary database,
     *                  otherwise, its assumed that its a text database1
     * @param unitNamer an optional unit namer, specifying how the cluster
     *                  units are called in the voice database referenced by url. If this is null,
     *                  an ldom unit naming scheme will be used (e.g., 'ae_afternoon' for the
     *                  phoneme 'ae' in the word 'afternoon'.
     * @throws IOException if an error occurs while loading the
     *                     database
     */
    public ClusterUnitSelector(URL url, ClusterUnitNamer unitNamer) throws IOException {
        if (url == null) {
            throw new IOException("Can't load cluster unit database");
        }
        boolean binary = url.getPath().endsWith(".bin");
        clunitDB = new ClusterUnitDatabase(url, binary);
        this.unitNamer = unitNamer;
    }

    /**
     * Prints debug messages.
     *
     * @param s the debug message
     */
    static void debug(String s) {
        if (DEBUG) {
            System.out.println("cludebug: " + s);
        }
    }

    /**
     * Generates the Unit Relation from the Segment Relation.
     * <br><b>Implementation note:</b><br>
     * Populates the segment relation with segment names of the form:
     * XX_YY where XX is the segment name (typically a phoneme)
     * and YY is the word that the segment is in (stripped and
     * lower case).
     * <p/>
     * The first step in cluster unit selection is to determine the unit
     * type for each unit in the utterance. The unit type for
     * selection in the simple talking clock example (cmu_time_awb) is
     * done per phone. The unit type consists of the phone
     * name followed by the word the phone comes from (e.g., n_now for
     * the phone 'n' in the word 'now').
     * <p/>
     * Invoke the Viterbi algorithm (via a viterbi class) that
     * selects the proper units for the segment and adds that to
     * each segment item.
     * <p/>
     * For each segment, create a unit and attach features based
     * upon the selected units.
     *
     * @param utterance the utterance to generate the Unit Relation
     * @throws ProcessException if an IOException is thrown during the
     *                          processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        Viterbi vd;
        Relation segs = utterance.getRelation(Relation.SEGMENT);

        utterance.setObject(SampleInfo.UTT_NAME,
                clunitDB.getSampleInfo());
        utterance.setObject("sts_list", clunitDB.getSts());

        vd = new Viterbi(segs, clunitDB);

        for (Item s = segs.getHead(); s != null; s = s.getNext()) {
            setUnitName(s);
        }

        // Carry out the CART lookup for the target costs, and the viterbi
        // search for finding the best path (join costs) through the candidates.
        vd.decode();

        // Now associate the candidate units in the best path
        // with the items in the segment relation.
        if (!vd.result("selected_unit")) {
            utterance.getVoice().error("clunits: can't find path");
        }

        // If optimal coupling was used, the join points must now be copied
        // from the path elements to the actual items in the segment relation.
        vd.copyFeature("unit_prev_move");
        vd.copyFeature("unit_this_move");

        // Based on this data, create a Unit relation giving the details of the
        // units to concatenate.
        Relation unitRelation = utterance.createRelation(Relation.UNIT);

        for (Item s = segs.getHead(); s != null; s = s.getNext()) {
            Item unit = unitRelation.appendItem();
            FeatureSet unitFeatureSet = unit.getFeatures();
            int unitEntry = s.getFeatures().getInt("selected_unit");

            // The item name is the segment name
            unitFeatureSet.setString("name", s.getFeatures().getString("name"));

            int unitStart;
            int unitEnd;
            String clunitName = s.getFeatures().getString("clunit_name");

            if (s.getFeatures().isPresent("unit_this_move")) {
                unitStart = s.getFeatures().getInt("unit_this_move");
            } else {
                unitStart = clunitDB.getStart(unitEntry);
            }

            if (s.getNext() != null &&
                    s.getNext().getFeatures().isPresent("unit_prev_move")) {
                unitEnd = s.getNext().getFeatures().getInt("unit_prev_move");
            } else {
                unitEnd = clunitDB.getEnd(unitEntry);
            }

            unitFeatureSet.setInt("unit_entry", unitEntry);
            ClusterUnit clunit = new ClusterUnit(clunitDB,
                    clunitName, unitStart, unitEnd);
            unitFeatureSet.setObject("unit", clunit);
            if (true) {
                unitFeatureSet.setInt("unit_start", clunit.getStart());
                unitFeatureSet.setInt("unit_end", clunit.getEnd());
                unitFeatureSet.setInt("instance", unitEntry - clunitDB.getUnitIndex(clunitName, 0));
            } // add the rest of these things for debugging.

            if (DEBUG) {
                debug(" sr " + clunitDB.getSampleInfo().getSampleRate() + " " +
                        s.getFeatures().getFloat("end") + " " +
                        (int) (s.getFeatures().getFloat("end") *
                                clunitDB.getSampleInfo().getSampleRate()));
            }
            unitFeatureSet.setInt("target_end",
                    (int) (s.getFeatures().getFloat("end")
                            * clunitDB.getSampleInfo().getSampleRate()));
        }
    }

    /**
     * Sets the cluster unit name given the segment.
     *
     * @param seg the segment item that gets the name
     */
    protected void setUnitName(Item seg) {
        if (unitNamer != null) {
            unitNamer.setUnitName(seg);
            return;
        }
        // default to LDOM naming scheme 'ae_afternoon':
        String cname = null;

        String segName = seg.getFeatures().getString("name");

        if (segName.equals("pau")) {
            cname = "pau_" + seg.findFeature("p.name");
        } else {
            // remove single quotes from name
            String dname = ((String) DNAME.findFeature(seg)).toLowerCase();
            cname = segName + "_" + stripQuotes(dname);
        }
        seg.getFeatures().setString("clunit_name", cname);
    }

    /**
     * Strips quotes from the given string.
     *
     * @param s the string to strip quotes from
     * @return a string with all single quotes removed
     */
    private String stripQuotes(String s) {
        StringBuffer sb = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\'') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Retrieves the string representation of this object.
     *
     * @return the string representation of this object
     */
    public String toString() {
        return "ClusterUnitSelector";
    }

    /**
     * Provides support for the Viterbi Algorithm.
     * <p/>
     * Implementation Notes
     * <p/>
     * For each candidate for the current unit, calculate the cost
     * between it and the first candidate in the next unit.  Save
     * only the path that has the least cost. By default, if two
     * candidates come from units that are adjacent in the
     * database, the cost is 0 (i.e., they were spoken together,
     * so they are a perfect match).
     * <p/>
     * <p/>
     * Repeat the previous process for each candidate in the next
     * unit, creating a list of least cost paths between the
     * candidates between the current unit and the unit following
     * it.
     * <p/>
     * <p/>
     * Toss out all candidates in the current unit that are not
     * included in a path.
     * <p/>
     * <p/>
     * Move to the next unit and repeat the process.
     */
    static class Viterbi {
        private int numStates = -1;
        private boolean bigIsGood = false;
        private ViterbiPoint timeline = null;
        private ViterbiPoint lastPoint = null;
        private FeatureSet f = null;
        private ClusterUnitDatabase clunitDB;

        /**
         * Creates a Viterbi class to process the given utterance.
         * A queue of ViterbiPoints corresponding to the Items in the Relation segs
         * is built up.
         */
        public Viterbi(Relation segs, ClusterUnitDatabase db) {
            ViterbiPoint last = null;
            clunitDB = db;
            f = new FeatureSetImpl();
            for (Item s = segs.getHead(); true; s = s.getNext()) {
                ViterbiPoint n = new ViterbiPoint(s);
                // The number of ViterbiPaths associated with each ViterbiPoint
                // is determined using the variable numStates.
                // TODO: Where can numStates be set?
                if (numStates > 0) {
                    n.initPathArray(numStates);
                }
                if (last != null) { // continue to build up the queue
                    last.next = n;
                } else { // timeline is the start of the queue
                    timeline = n;
                }
                last = n;

                if (s == null) { // no further segments, leave loop
                    lastPoint = n;
                    break;
                }
            }

            if (DEBUG) {
                debug("num states " + numStates);
            }

            if (numStates == 0) {        // its a  general beam search
                timeline.paths = new ViterbiPath();
            }

            if (numStates == -1) {    // dynamic number of states (# cands)
                timeline.initPathArray(1);
            }
        }

        /**
         * Sets the given feature to the given value.
         *
         * @param name the name of the feature
         * @param obj  the new value.
         */
        public void setFeature(String name, Object obj) {
            f.setObject(name, obj);
        }

        /**
         * Gets the value for the given feature.
         *
         * @param name the name of the feature
         * @return the value of the feature
         */
        public Object getFeature(String name) {
            return f.getObject(name);
        }

        /**
         * Carry out a Viterbi search in for a prepared queue of ViterbiPoints.
         * In a nutshell, each Point represents a target item (a target segment);
         * for each target Point, a number of Candidate units in the voice database
         * are determined; a Path structure is built up, based on local best transitions.
         * Concretely, a Path consists of a (possibly empty) previous Path, a current Candidate,
         * and a Score. This Score is a quality measure of the Path; it is calculated as the
         * sum of the previous Path's score, the Candidate's score, and the Cost of joining
         * the Candidate to the previous Path's Candidate. At each step, only one Path
         * leading to each Candidate is retained, viz. the Path with the best Score.
         * All that is left to do is to call result() to get the best-rated
         * path from among the paths associated with the last Point, and to associate
         * the resulting Candidates with the segment items they will realise.
         */
        void decode() {
            for (ViterbiPoint p = timeline; p.next != null; p = p.next) {
                // The candidates for the current item:
                p.cands = getCandidate(p.item);
                if (DEBUG) {
                    debug("decode " + p.cands);
                }
                if (numStates != 0) {
                    if (numStates == -1) {
                        // put as many (empty) path elements into p.next as there are candidates in p
                        p.next.initDynamicPathArray(p.cands);
                    }

                    // Now go through all existing paths and all candidates for the current item;
                    // tentatively extend each existing path to each of the candidates,
                    // but only retain the
                    // Attention: p.numStates is not numStates!
                    // numStates = a general flag indicating which type of viterbi search
                    //             to use (only -1 seems to be implemented);
                    // p.numStates = the number of paths in p.statePaths, i.e. p.numStates==p.statePaths.length
                    for (int i = 0; i < p.numStates; i++) {
                        if ((p == timeline && i == 0) ||
                                (p.statePaths[i] != null)) {
                            // We are at the very beginning of the search, or have a usable path to extend
                            // debug("   dc p " + p);
                            for (ViterbiCandidate c = p.cands;
                                 c != null; c = c.next) {
                                // For the candidate c, create a path extending the previous path
                                // p.statePaths[i] to that candidate:
                                ViterbiPath np = getPath(p.statePaths[i], c);
                                // Compare this path to the existing best path (if any) leading to
                                // candidate c; only retain the one with the better score.
                                // TODO: why should the paths leading to the candidates realising p
                                // be stored in p.next?
                                addPaths(p.next, np);
                            }
                        }
                    }
                } else {
                    System.err.println(
                            "Viterbi.decode: general beam search not implemented");
                }
            }
        }


        /**
         * Try to add paths to the given point.
         *
         * @param point the point to add the paths to
         * @param paths the path
         */
        void addPaths(ViterbiPoint point, ViterbiPath path) {
            ViterbiPath nextPath;
            for (ViterbiPath p = path; p != null; p = nextPath) {
                nextPath = p.next;
                addPath(point, p);
            }
        }

        /**
         * Add the new path to the state path if it is
         * better than the current path. In this, state means
         * the position of the candidate associated with this
         * path in the candidate queue
         * for the corresponding segment item. In other words,
         * this method uses newPath as the one path leading to
         * the candidate newPath.candidate, if it has a better
         * score than the previously best path leading to that candidate.
         *
         * @param point   where the path is added
         * @param newPath the path to add if its score is best
         */
        void addPath(ViterbiPoint point, ViterbiPath newPath) {
            if (point.statePaths[newPath.state] == null) {
                // we don't have one yet, so this is best
                point.statePaths[newPath.state] = newPath;
            } else if (isBetterThan(newPath.score,
                    point.statePaths[newPath.state].score)) {
                // its better than what we already have
                point.statePaths[newPath.state] = newPath;
            } else {
                // its not better that what we already have
                // so we just forget about it.
            }
        }

        /**
         * See if a is better than b. Goodness is defined
         * by 'bigIsGood'.
         *
         * @param a value to check
         * @param b value to check.
         *          <p/>
         *          return true if a is better than b.
         */
        private boolean isBetterThan(int a, int b) {
            if (bigIsGood) {
                return a > b;
            } else {
                return a < b;
            }
        }

        /**
         * Find the best path through the decoder, adding the feature
         * name to the candidate.
         *
         * @param feature the feature to add
         * @return true if a best path was found
         */
        boolean result(String feature) {
            ViterbiPath path;

            if (timeline == null || timeline.next == null) {
                return true; // null case succeeds
            }
            path = findBestPath();

            if (path == null) {
                return false;
            }

            for (; path != null; path = path.from) {
                if (path.candidate != null) {
                    path.candidate.item.getFeatures().setObject(feature,
                            path.candidate.value);
                }
            }
            return true;
        }

        /**
         * Given a feature, copy the value associated with feature
         * name from the path to each item in the path.
         *
         * @param feature the name of the feature.
         */
        void copyFeature(String feature) {
            ViterbiPath path = findBestPath();
            if (path == null) {
                return;  // nothing to copy, empty stream or no solution
            }

            for (; path != null; path = path.from) {
                if (path.candidate != null && path.isPresent(feature)) {
                    path.candidate.item.getFeatures().setObject(feature,
                            path.getFeature(feature));
                }
            }
        }

        /**
         * Finds the best (queue of) candidate(s) for a given (segment) item.
         * This traverses a CART tree for target cluster selection as described in
         * the paper introducing the clunits algorithm. This corresponds to the
         * "target costs" described for general unit selection.
         *
         * @return the first candidate in the queue of candidate units for this item.
         */
        private ViterbiCandidate getCandidate(Item item) {
            // TODO: This should better be called getCandidates() (plural form),
            // because what it does is find all the candidates for the item
            // and return the head of the queue.
            String unitType = item.getFeatures().getString("clunit_name");
            CART cart = clunitDB.getTree(unitType);
            // Here, the unit candidates are selected.
            int[] clist = (int[]) cart.interpret(item);
            // Now, clist is an array of instance numbers for the units of type
            // unitType that belong to the best cluster according to the CART.

            ViterbiCandidate p;
            ViterbiCandidate all;
            ViterbiCandidate gt;

            all = null;
            for (int i = 0; i < clist.length; i++) {
                p = new ViterbiCandidate();
                p.next = all; // link them reversely: the first in clist will be at the end of the queue
                p.item = item; // The item is the same for all these candidates in the queue.
                p.score = 0;
                // remember the absolute unit index:
                p.setInt(clunitDB.getUnitIndex(unitType, clist[i]));
                all = p;
                // this is OK
                if (DEBUG) {
                    debug("    gc adding " + clist[i]);
                }
            }

            // Take into account candidates for previous item?
            // Depending on the setting of EXTEND_SELECTIONS in the database,
            // look the first candidates for the preceding item,
            // and add the units following these (which are not yet candidates)
            // as candidates. EXTEND_SELECTIONS indicates how many of these
            // are added. A high setting will add candidates which don't fit the
            // target well, but which can be smoothly concatenated with the context.
            // In a sense, this means trading target costs against join costs.
            if (clunitDB.getExtendSelections() > 0 &&
                    item.getPrevious() != null) {
                // Get the candidates for the preceding (segment) item
                ViterbiCandidate lc = (ViterbiCandidate) (item.
                        getPrevious().getFeatures().getObject("clunit_cands"));
                if (DEBUG) {
                    debug("      lc " + lc);
                }
                for (int e = 0; lc != null &&
                        (e < clunitDB.getExtendSelections());
                     lc = lc.next) {
                    int nu = clunitDB.getNextUnit(lc.ival);
                    if (DEBUG) {
                        debug("      e: " + e + " nu: " + nu);
                    }
                    if (nu == ClusterUnitDatabase.CLUNIT_NONE) {
                        continue;
                    }

                    // Look through the list of candidates for the current item:
                    for (gt = all; gt != null; gt = gt.next) {
                        if (DEBUG) {
                            debug("       gt " + gt.ival + " nu " + nu);
                        }
                        if (nu == gt.ival) {
                            // The unit following one of the candidates for the preceding
                            // item already is a candidate for the current item.
                            break;
                        }
                    }

                    if (DEBUG) {
                        debug("nu " + clunitDB.getUnit(nu).getName() + " all " +
                                clunitDB.getUnit(all.ival).getName() +
                                " " + all.ival);
                    }
                    if ((gt == null) && clunitDB.isUnitTypeEqual(nu, all.ival)) {
                        // nu is of the right unit type and is not yet one of the candidates.
                        // add it to the queue of candidates for the current item:
                        p = new ViterbiCandidate();
                        p.next = all;
                        p.item = item;
                        p.score = 0;
                        p.setInt(nu);
                        all = p;
                        e++;
                    }
                }
            }
            item.getFeatures().setObject("clunit_cands", all);
            return all;
        }

        /**
         * Construct a new path element linking a previous path to the given candidate.
         * The (penalty) score associated with the new path is calculated as the sum of
         * the score of the old path plus the score of the candidate itself plus the
         * join cost of appending the candidate to the nearest candidate in the given path.
         * This join cost takes into account optimal coupling if the database has
         * OPTIMAL_COUPLING set to 1. The join position is saved in the new path, as
         * the features "unit_prev_move" and "unit_this_move".
         *
         * @param path     the previous path, or null if this candidate starts a new path
         * @param candiate the candidate to add to the path
         * @return a new path, consisting of this candidate appended to the previous path, and
         * with the cumulative (penalty) score calculated.
         */
        private ViterbiPath getPath(ViterbiPath path,
                                    ViterbiCandidate candidate) {
            int cost;
            ViterbiPath newPath = new ViterbiPath();

            newPath.candidate = candidate;
            newPath.from = path;
            //
            // Flite 1.1 has some logic here to test to see
            // if  the unit database is fully populated or not and if not
            // load fixed residuals and calculate distance with a
            // different distance algorithm that is designed for fixed
            // point. FreeTTS doesn't really need to do that.
            //

            if (path == null || path.candidate == null) {
                cost = 0;
            } else {
                int u0 = path.candidate.ival;
                int u1 = candidate.ival;
                if (clunitDB.getOptimalCoupling() == 1) {
                    Cost oCost = getOptimalCouple(u0, u1);
                    if (oCost.u0Move != -1) {
                        newPath.setFeature("unit_prev_move", new
                                Integer(oCost.u0Move));
                    }
                    if (oCost.u1Move != -1) {
                        newPath.setFeature("unit_this_move", new
                                Integer(oCost.u1Move));
                    }
                    cost = oCost.cost;
                } else if (clunitDB.getOptimalCoupling() == 2) {
                    cost = getOptimalCoupleFrame(u0, u1);
                } else {
                    cost = 0;
                }
            }

            // cost *= clunitDB.getContinuityWeight();
            cost *= 5;    // magic number ("continuity weight") from flite
            // TODO: remove the state attribute from ViterbiPath, as it is simply path.candidate.pos!
            newPath.state = candidate.pos;
            if (path == null) {
                newPath.score = cost + candidate.score;
            } else {
                newPath.score = cost + candidate.score + path.score;
            }

            return newPath;
        }

        /**
         * Find the best path. This requires decode() to have been run.
         *
         * @return the best path.
         */
        private ViterbiPath findBestPath() {
            ViterbiPoint t;
            int best;
            int worst;
            ViterbiPath bestPath = null;

            if (bigIsGood) {
                worst = Integer.MIN_VALUE;
            } else {
                worst = Integer.MAX_VALUE;
            }

            best = worst;

            // TODO: do not need t, can use lastPoint throughout
            t = lastPoint;

            if (numStates != 0) {
                if (DEBUG) {
                    debug("fbp ns " + numStates + " t "
                            + t.numStates + " best " + best);
                }
                // All paths end in lastPoint, and take into account
                // previous path segment's scores. Therefore, it is
                // sufficient to find the best path from among the
                // paths for lastPoint.
                for (int i = 0; i < t.numStates; i++) {
                    if (t.statePaths[i] != null &&
                            (isBetterThan(t.statePaths[i].score, best))) {
                        best = t.statePaths[i].score;
                        bestPath = t.statePaths[i];
                    }
                }
            }
            return bestPath;
        }

        /**
         * Find the optimal coupling frame for a pair of units.
         *
         * @param u0 first unit to try
         * @param u1 second unit to try
         * @return the cost for this coupling, including the best coupling frame
         */
        Cost getOptimalCouple(int u0, int u1) {
            int a, b;
            int u1_p;
            int i, fcount;
            int u0_st, u1_p_st, u0_end, u1_p_end;
            int best_u0, best_u1_p;
            int dist, best_val;
            Cost cost = new Cost();

            u1_p = clunitDB.getPrevUnit(u1);

            // If u0 precedes u1, the cost is 0, and we're finished.
            if (u1_p == u0) {
                return cost;
            }


            // If u1 does not have a previous unit, or that previous
            // unit does not belong to the same phone, the optimal
            // couple frame must be found between u0 and u1.
            if (u1_p == ClusterUnitDatabase.CLUNIT_NONE ||
                    clunitDB.getPhone(u0) !=
                            clunitDB.getPhone(u1_p)) {
                cost.cost = 10 * getOptimalCoupleFrame(u0, u1);
                return cost;
            }

            // If u1 has a valid previous unit, try to find the optimal
            // couple point between u0 and that previous unit, u1_p.

            // Find out which of u1_p and u0 is shorter.
            // In both units, we plan to start from one third of the unit length,
            // and to compare frame coupling frame by frame until the end of the
            // shorter unit is reached.
            u0_end = clunitDB.getEnd(u0) - clunitDB.getStart(u0);
            u1_p_end = clunitDB.getEnd(u1_p) - clunitDB.getStart(u1_p);
            u0_st = u0_end / 3;
            u1_p_st = u1_p_end / 3;

            if ((u0_end - u0_st) < (u1_p_end - u1_p_st)) {
                fcount = u0_end - u0_st;
                // We could now shift the starting point for coupling in the longer unit
                // so that the distance from the end is the same in both units:
            /* u1_p_st = u1_p_end - fcount; */
            } else {
                fcount = u1_p_end - u1_p_st;
                // We could now shift the starting point for coupling in the longer unit
                // so that the distance from the end is the same in both units:
            /* u0_st = u0_end - fcount; */
            }

            // Now go through the two units, and search for the frame pair where
            // the acoustic distance is smallest.
            best_u0 = u0_end;
            best_u1_p = u1_p_end;
            best_val = Integer.MAX_VALUE;

            for (i = 0; i < fcount; ++i) {
                a = clunitDB.getStart(u0) + u0_st + i;
                b = clunitDB.getStart(u1_p) + u1_p_st + i;
                dist = getFrameDistance(a, b,
                        clunitDB.getJoinWeights(),
                        clunitDB.getMcep().getSampleInfo().getNumberOfChannels())
                        + Math.abs(clunitDB.getSts().getFrameSize(a) -
                        clunitDB.getSts().getFrameSize(b)) *
                        clunitDB.getContinuityWeight();

                if (dist < best_val) {
                    best_val = dist;
                    best_u0 = u0_st + i;
                    best_u1_p = u1_p_st + i;
                }
            }

            // u0Move is the new end for u0
            // u1Move is the new start for u1
            cost.u0Move = clunitDB.getStart(u0) + best_u0;
            cost.u1Move = clunitDB.getStart(u1_p) + best_u1_p;
            cost.cost = 30000 + best_val;
            return cost;
        }

        /**
         * Returns the distance between the successive potential
         * frames.
         *
         * @param u0 the first unit to try
         * @param u1 the second unit to try
         * @return the distance between the two units
         */
        int getOptimalCoupleFrame(int u0, int u1) {
            int a, b;

            if (clunitDB.getPrevUnit(u1) == u0) {
                return 0; // consecutive units win
            }

            if (clunitDB.getNextUnit(u0) != ClusterUnitDatabase.CLUNIT_NONE) {
                a = clunitDB.getEnd(u0);
            } else {  // don't want to do this but it's all that is left to do
                a = clunitDB.getEnd(u0) - 1; // if num frames < 1 this is bad
            }
            b = clunitDB.getStart(u1);

            return getFrameDistance(a, b,
                    clunitDB.getJoinWeights(),
                    clunitDB.getMcep().getSampleInfo().getNumberOfChannels())
                    + Math.abs(clunitDB.getSts().getFrameSize(a) -
                    clunitDB.getSts().getFrameSize(b)) *
                    clunitDB.getContinuityWeight();
        }

        /**
         * Get the 'distance' between the frames a and b.
         *
         * @param a           first frame
         * @param b           second frame
         * @param joinWeights the weights used in comparison
         * @param order       number of compares
         * @return the distance between the frames
         */
        public int getFrameDistance(int a, int b, int[] joinWeights, int order) {

            if (DEBUG) {
                debug(" gfd  a " + a + " b " + b + " or " + order);
            }
            int r, i;
            short[] bv = clunitDB.getMcep().getSample(b).getFrameData();
            short[] av = clunitDB.getMcep().getSample(a).getFrameData();

            for (r = 0, i = 0; i < order; i++) {
                int diff = av[i] - bv[i];
                r += Math.abs(diff) * joinWeights[i] / 65536;
            }
            return r;
        }

    }

    /**
     * Represents a point in the Viterbi path. A point corresponds to an item,
     * e.g. a Segment.
     * Each ViterbiPoint knows
     * about its next ViterbiPoint, i.e. they can form a queue.
     */
    static class ViterbiPoint {
        Item item = null;
        // TODO: remove the numStates attribute from ViterbiPoint, as this is only statePaths.length
        int numStates = 0;
        int numPaths = 0;
        ViterbiCandidate cands = null;
        ViterbiPath paths = null;
        ViterbiPath[] statePaths = null;
        ViterbiPoint next = null;

        /**
         * Creates a ViterbiPoint for the given item. A typical item of choice is a Segment item.
         *
         * @param item the item of interest
         */
        public ViterbiPoint(Item item) {
            this.item = item;
        }

        /**
         * Initialize the path array to the given size.
         *
         * @param size the size of the path array
         */
        public void initPathArray(int size) {
            if (DEBUG) {
                debug("init_path_array: " + size);
            }
            numStates = size;
            statePaths = new ViterbiPath[size];
        }

        /**
         * Initializes the dynamic path array. The path array will have
         * as many ViterbiPath members as there are candidates in the
         * queue starting with candidate.
         * Side effect on parameter: This will set the pos member of the
         * candidates in the queue starting with candidate to the position
         * in the queue.
         *
         * @param candidate the first candidate of interest
         */
        public void initDynamicPathArray(ViterbiCandidate candidate) {
            int i = 0;
            for (ViterbiCandidate cc = candidate; cc != null;
                 i++, cc = cc.next) {
                cc.pos = i;
            }
            if (DEBUG) {
                debug("init_dynamic_ path_array: " + i);
            }
            initPathArray(i);
        }

        public String toString() {
            return " pnt: " + numStates + " paths " + numPaths;
        }
    }

    /**
     * Represents a candidate for the Viterbi algorthm.
     * Each candidate knows about its next candidate, i.e. they can form
     * a queue.
     */
    static class ViterbiCandidate {
        int score = 0;
        Object value = null;
        int ival = 0;
        int pos = 0;
        Item item = null;
        ViterbiCandidate next = null;

        /**
         * Sets the object for this candidate.
         *
         * @param obj the object
         */
        void set(Object obj) {
            value = obj;
        }

        /**
         * Sets the integer value  for this candidate. This can be used for saving
         * the unit index of the candidate unit represented by this ViterbiCandidate.
         *
         * @param ival the integer value
         */
        void setInt(int ival) {
            this.ival = ival;
            set(new Integer(ival));
        }

        /**
         * Converts this object to a string.
         *
         * @return the string form of this object
         */
        public String toString() {
            return "VC: Score " + score + " ival " + ival + " Pos " + pos;
        }
    }

    /**
     * Describes a Viterbi path.
     */
    static class ViterbiPath {
        int score = 0;
        int state = 0;
        ViterbiCandidate candidate = null;
        ViterbiPath from = null;
        ViterbiPath next = null;
        private FeatureSet f = null;

        /**
         * Sets a feature with the given name to the given value.
         *
         * @param name  the name of the feature
         * @param value the new value for the feature
         */
        void setFeature(String name, Object value) {
            if (f == null) {
                f = new FeatureSetImpl();
            }
            f.setObject(name, value);
        }

        /**
         * Retrieves a feature.
         *
         * @param name the name of the feature
         * @return the feature
         */
        Object getFeature(String name) {
            Object value = null;
            if (f != null) {
                value = f.getObject(name);
            }
            return value;
        }

        /**
         * Determines if the feature with the given name
         * exsists.
         *
         * @param name the feature to look for
         * @return <code>true</code> if the feature is present;
         * otherwise <code>false</code>.
         */
        boolean isPresent(String name) {
            if (f == null) {
                return false;
            } else {
                return getFeature(name) != null;
            }
        }

        /**
         * Converts this object to a string.
         *
         * @return the string form of this object
         */
        public String toString() {
            return "ViterbiPath score " + score + " state " + state;
        }
    }
}


/**
 * Information returned from getOptimalCoupling.
 */
class Cost {
    int cost = 0;
    int u0Move = -1;
    int u1Move = -1;
}


/**
 * A Cluster Unit.
 */
class ClusterUnit implements com.sun.speech.freetts.Unit {

    private final static boolean DEBUG = false;

    private ClusterUnitDatabase db;
    private String name;
    private int start;
    private int end;

    /**
     * Contructs a cluster unit given.
     *
     * @param db    the database
     * @param name  the unitName
     * @param start the start
     * @param end   the end
     */
    public ClusterUnit(ClusterUnitDatabase db, String name, int start, int end) {
        this.db = db;
        this.start = start;
        this.end = end;
        this.name = name;
    }


    /**
     * Returns the start.
     *
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end.
     *
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the name of this Unit.
     *
     * @return the name of this unit
     */
    public String getName() {
        return name;
    }

    /**
     * returns the size of the unit.
     *
     * @return the size of the unit
     */
    public int getSize() {
        return db.getSts().getUnitSize(start, end);
    }

    /**
     * Retrieves the nearest sample.
     *
     * @param index the ideal index
     * @return the nearest Sample
     */
    public Sample getNearestSample(float index) {
        int i, iSize = 0, nSize;
        SampleSet sts = db.getSts();

        // loop through all the Samples in this unit
        for (i = start; i < end; i++) {
            Sample sample = sts.getSample(i);
            nSize = iSize + sample.getResidualSize();

            if (Math.abs(index - (float) iSize) <
                    Math.abs(index - (float) nSize)) {
                return sample;
            }
            iSize = nSize;
        }
        return sts.getSample(end - 1);
    }

    /**
     * gets the string name for the unit.
     *
     * @return string rep of this object.
     */
    public String toString() {
        return getName();
    }


    /**
     * Dumps this unit.
     */
    public void dump() {
    }

    /**
     * Prints debugging statements.
     *
     * @param s the debugging message
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println("Clunit debug: " + s);
        }
    }
}


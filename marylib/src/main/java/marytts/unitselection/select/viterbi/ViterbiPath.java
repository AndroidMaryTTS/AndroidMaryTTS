/**
 * Portions Copyright 2006 DFKI GmbH.
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * Permission is hereby granted, free of charge, to use and distribute
 * this software and its documentation without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of this work, and to
 * permit persons to whom this work is furnished to do so, subject to
 * the following conditions:
 * <p/>
 * 1. The code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Any modifications must be clearly marked as such.
 * 3. Original authors' names are not deleted.
 * 4. The authors' names are not used to endorse or promote products
 * derived from this software without specific prior written
 * permission.
 * <p/>
 * DFKI GMBH AND THE CONTRIBUTORS TO THIS WORK DISCLAIM ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS, IN NO EVENT SHALL DFKI GMBH NOR THE
 * CONTRIBUTORS BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package marytts.unitselection.select.viterbi;

/**
 * Describes a Viterbi path.
 */
public class ViterbiPath implements Comparable<ViterbiPath> {
    final double score;
    final ViterbiCandidate candidate;
    final ViterbiPath previous;
    ViterbiPath next = null;

    public ViterbiPath(ViterbiCandidate candidate, ViterbiPath previousPath, double score) {
        this.candidate = candidate;
        this.previous = previousPath;
        this.score = score;
    }

    /**
     * Get the score of this path
     *
     * @return the score
     */
    public double getScore() {
        return score;
    }


    /**
     * Get the candidate of this path.
     * Each path leads to exactly one candidate.
     *
     * @return the candidate
     */
    public ViterbiCandidate getCandidate() {
        return candidate;
    }


    /**
     * Get the next path
     *
     * @return the next path
     */
    public ViterbiPath getNext() {
        return next;
    }

    /**
     * Set the next path
     *
     * @param next the next path
     */
    public void setNext(ViterbiPath next) {
        this.next = next;
    }

    /**
     * Get the previous path
     *
     * @return the previous path
     */
    public ViterbiPath getPrevious() {
        return previous;
    }


    /**
     * Converts this object to a string.
     *
     * @return the string form of this object
     */
    @Override
    public String toString() {
        return "ViterbiPath score " + score + " leads to candidate unit " + candidate.getUnit();
    }

    /**
     * Compare two viterbi paths such that the one with the lower score is considered smaller.
     */
    @Override
    public int compareTo(ViterbiPath o) {
        return Double.compare(score, o.score);
    }
}
   
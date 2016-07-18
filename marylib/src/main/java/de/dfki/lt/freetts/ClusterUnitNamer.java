/**
 * Portions Copyright 2004 DFKI GmbH.
 * Portions Copyright 2001 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package de.dfki.lt.freetts;

import com.sun.speech.freetts.Item;

public interface ClusterUnitNamer {
    /**
     * Sets the cluster unit name given the segment.
     *
     * @param seg the segment item that gets the name
     */
    void setUnitName(Item seg);

}

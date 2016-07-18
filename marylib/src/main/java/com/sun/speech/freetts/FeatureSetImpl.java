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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the FeatureSet interface.
 */
public class FeatureSetImpl implements FeatureSet {
    static DecimalFormat formatter;
    private Map featureMap;

    /**
     * Creates a new empty feature set
     */
    public FeatureSetImpl() {
        featureMap = new LinkedHashMap();
    }

    /**
     * Determines if the given feature is present.
     *
     * @param name the name of the feature of interest
     * @return true if the named feature is present
     */
    public boolean isPresent(String name) {
        return featureMap.containsKey(name);
    }


    /**
     * Removes the named feature from this set of features.
     *
     * @param name the name of the feature of interest
     */
    public void remove(String name) {
        featureMap.remove(name);
    }

    /**
     * Convenience method that returns the named feature as a string.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     * @throws ClassCastException if theassociated value is not a
     *                            String
     */
    public String getString(String name) {
        return (String) getObject(name);
    }

    /**
     * Convenience method that returns the named feature as a int.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     * @throws ClassCastException if the associated value is not an int.
     */
    public int getInt(String name) {
        return ((Integer) getObject(name)).intValue();
    }

    /**
     * Convenience method that returns the named feature as a float.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found.
     * @throws ClassCastException if the associated value is not a
     *                            float
     */
    public float getFloat(String name) {
        return ((Float) getObject(name)).floatValue();
    }

    /**
     * Returns the named feature as an object.
     *
     * @param name the name of the feature
     * @return the value associated with the name or null if the value
     * is not found
     */
    public Object getObject(String name) {
        return featureMap.get(name);
    }

    /**
     * Convenience method that sets the named feature as a int.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setInt(String name, int value) {
        setObject(name, new Integer(value));
    }

    /**
     * Convenience method that sets the named feature as a float.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setFloat(String name, float value) {
        setObject(name, new Float(value));
    }

    /**
     * Convenience method that sets the named feature as a String.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setString(String name, String value) {
        setObject(name, value);
    }

    /**
     * Sets the named feature.
     *
     * @param name  the name of the feature
     * @param value the value of the feature
     */
    public void setObject(String name, Object value) {
        featureMap.put(name, value);
    }

    /**
     * Dumps the FeatureSet in textual form.  The feature name
     * is not included in the dump.
     *
     * @param output where to send the formatted output
     * @param pad    the padding
     * @param title  the title
     */
    public void dump(PrintWriter output, int pad, String title) {
        dump(output, pad, title, false);
    }

    /**
     * Dumps the FeatureSet in textual form.
     *
     * @param output   where to send the formatted output
     * @param pad      the padding
     * @param title    the title
     * @param showName if <code>true</code>, include the feature name
     */
    public void dump(PrintWriter output, int pad, String title,
                     boolean showName) {
        List keys = new ArrayList(featureMap.keySet());

        if (formatter == null) {
            formatter = new DecimalFormat("########0.000000");
        }
        // Collections.sort(keys);
        Collections.reverse(keys);  // to match flite

        Utilities.dump(output, pad, title);
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            String key = (String) i.next();

            if (!showName && key.equals("name")) {
                continue;
            }

            Object value = getObject(key);
            if (value instanceof Dumpable) {
                Dumpable d = (Dumpable) value;
                d.dump(output, pad + 4, key);
            } else {
                if (value instanceof Float) {
                    Float fval = (Float) value;
                    Utilities.dump(output, pad + 4, key + "=" +
                            formatter.format(fval.floatValue()));
                } else {
                    Utilities.dump(output, pad + 4, key + "=" + value);
                }
            }
        }
    }
}

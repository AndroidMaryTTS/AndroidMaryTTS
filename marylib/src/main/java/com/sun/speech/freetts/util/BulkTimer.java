/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a suite of timers that are used to collect and generate
 * performance metrics for FreeTTS.
 */
public class BulkTimer {
    /**
     * A BulkTimer that can be used by classes that need to
     * time their loading phase.
     */
    public final static BulkTimer LOAD = new BulkTimer();

    private final static String SELF = "OverallTime";
    private boolean verbose;
    private Map timers;


    /**
     * Creates a bulk timer.
     */
    public BulkTimer() {
        this.verbose = false;
        timers = new LinkedHashMap();
    }

    /**
     * Starts the timer with the given name. A BulkTimer can manage
     * any number of timers. The timers are referenced by name. A
     * timer is created the first time it is referenced.
     *
     * @param name the name of the timer to start
     */
    public void start(String name) {
        getTimer(name).start();
    }

    /**
     * Stops the timer with the given name.
     *
     * @param name the name of the timer
     */
    public void stop(String name) {
        getTimer(name).stop(verbose);
    }

    /**
     * Starts the bulk timer.  The BulkTimer maintains a timer for
     * itself (called SELF). This is used to measure the overall time
     * for a bulk timer. When timing data is displayed, the percentage
     * of total time is displayed. The total time is the time between
     * <code> start </code> and <code> end </code>  calls on the
     * <code> BulkTimer </code> .
     */
    public void start() {
        getTimer(SELF).start();
    }


    /**
     * Stops the bulk timer.
     */
    public void stop() {
        getTimer(SELF).stop(verbose);
    }

    /**
     * Checks to see if we are in verbose mode.
     *
     * @return <code>true</code>  if verbose mode; otherwise
     * <code>false</code>.
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Sets verbose mode.
     *
     * @param verbose the verbose mode
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Gets the timer with the given name.
     *
     * @param name the timer name
     * @return the timer with that name
     */
    public Timer getTimer(String name) {
        if (!timers.containsKey(name)) {
            timers.put(name, new Timer(name));
        }
        return (Timer) timers.get(name);
    }

    /**
     * Shows all of the collected times.
     *
     * @param title the title for the display
     */
    public void show(String title) {
        long overall = getTimer(SELF).getCurrentTime();
        Collection values = timers.values();
        Timer.showTimesShortTitle(title);
        for (Iterator i = values.iterator(); i.hasNext(); ) {
            Timer t = (Timer) i.next();
            t.showTimes(overall);
        }
    }
}

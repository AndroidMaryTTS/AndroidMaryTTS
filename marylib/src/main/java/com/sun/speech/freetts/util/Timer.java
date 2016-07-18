/**
 * Copyright 2001 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.util;

import java.text.DecimalFormat;

/**
 * Keeps track of execution times.
 */
public class Timer {
    private final static DecimalFormat timeFormatter
            = new DecimalFormat("###0.0000");
    private final static DecimalFormat percentFormatter
            = new DecimalFormat("###0.00%");
    private String name;
    private long startTime;
    private long curTime;
    private long count;
    private double sum;
    private long minTime = Long.MAX_VALUE;
    private long maxTime = 0L;
    private boolean notReliable; // if true, timing is not reliable

    /**
     * Creates a timer.
     *
     * @param name the name of the timer
     */
    public Timer(String name) {
        this.name = name;
        reset();
    }

    /**
     * Shows the timing stats title.
     *
     * @param title shows the title and column headings for the time
     *              display
     */
    public static void showTimesShortTitle(String title) {
        String titleBar =
                "# ----------------------------- " + title +
                        "----------------------------------------------------------- ";
        System.out.println(Utilities.pad(titleBar, 78));
        System.out.print(Utilities.pad("# Name", 15) + " ");
        System.out.print(Utilities.pad("Count", 6));
        System.out.print(Utilities.pad("CurTime", 10));
        System.out.print(Utilities.pad("MinTime", 10));
        System.out.print(Utilities.pad("MaxTime", 10));
        System.out.print(Utilities.pad("AvgTime", 10));
        System.out.print(Utilities.pad("TotTime", 10));
        System.out.print(Utilities.pad("% Total", 8));
        System.out.println();
    }

    /**
     * The main program for performing simple tests. Creates a timer
     * uses it and shows its output.
     *
     * @param args program arguments (not used)
     */
    public static void main(String[] args) {
        Timer timer = new Timer("testTimer");
        Timer overallTimer = new Timer("overall");
        timer.showTimes(0L);

        overallTimer.start();

        for (int i = 0; i < 5; i++) {
            timer.start();
            try {
                Thread.sleep(i * 1000L);
            } catch (InterruptedException e) {
            }
            timer.stop(true);
        }
        overallTimer.stop();
        timer.showTimes(overallTimer.getCurrentTime());
        overallTimer.showTimes();
    }

    /**
     * Resets the timer as if it has never run before.
     */
    public void reset() {
        startTime = 0L;
        count = 0L;
        sum = 0L;
        minTime = Long.MAX_VALUE;
        maxTime = 0L;
        notReliable = false;
    }

    /**
     * Starts the timer running.
     */
    public void start() {
        if (startTime != 0L) {
            notReliable = true;
            // throw new IllegalStateException("timer stutter start " + name);
        }
        startTime = System.currentTimeMillis();
    }

    /**
     * Returns the current time.
     *
     * @return the current time
     */
    public long getCurrentTime() {
        return curTime;
    }

    /**
     * Stops the timer.
     *
     * @param verbose if <code>true</code>, print out details from
     *                this run; otherwise, don't print the details
     */
    public void stop(boolean verbose) {
        if (startTime == 0L) {
            notReliable = true;
        }
        curTime = System.currentTimeMillis() - startTime;
        startTime = 0L;
        if (curTime > maxTime) {
            maxTime = curTime;
        }
        if (curTime < minTime) {
            minTime = curTime;
        }
        count++;
        sum += curTime;
        if (verbose) {
            showTimesShort(0L);
        }
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        stop(false);
    }

    /**
     * Formats times into a standard format.
     *
     * @param time the time (in milliseconds) to be formatted
     * @return a string representation of the time.
     */
    private String fmtTime(long time) {
        return fmtTime(time / 1000.0);
    }

    /**
     * Formats times into a standard format.
     *
     * @param time the time (in seconds) to be formatted
     * @return a string representation of the time.
     */
    private String fmtTime(double time) {
        return Utilities.pad(timeFormatter.format(time) + "s", 10);
    }

    /**
     * Shows detailed timing stats. If overall is non-zero, it represents
     * the overall processing time and a percentage of overall
     * time for this timer should be calculated and displayed.
     *
     * @param overall the overall processing time in milliseconds or 0.
     */
    public void showTimesLong(long overall) {
        System.out.println(" Timer:    " + name);
        System.out.println(" Count:    " + count);

        if (notReliable) {
            System.out.println(" Not reliable");
        } else {
            if (count == 1) {
                System.out.println(" Cur Time: " + fmtTime(curTime));
            } else if (count > 1) {
                System.out.println(" Min Time: " + fmtTime(minTime));
                System.out.println(" Max Time: " + fmtTime(maxTime));
                System.out.println(" Avg Time: "
                        + fmtTime(sum / count / 1000.0));
                System.out.println(" Tot Time: " + fmtTime(sum / 1000.0));
                if (overall != 0) {
                    System.out.println(" Percent:  "
                            + percentFormatter.format(sum / overall));
                }
            }
        }
        System.out.println();
    }

    /**
     * Shows brief timing stats. If overall is non-zero, it represents
     * the overall processing time and a percentage of overall
     * time for this timer should be calculated and displayed.
     *
     * @param overall the overall processing time in milliseconds or 0.
     */
    public void showTimesShort(long overall) {
        double avgTime = 0.0;
        double totTime = sum / 1000.0;
        double overallPercent = 0;

	/*
    if (curTime == 0) {
	    return;
	}
	*/

        if (count == 0) {
            return;
        }

        if (count > 0) {
            avgTime = sum / count / 1000.0;
        }

        if (overall != 0) {
            overallPercent = sum / overall;
        }

        if (notReliable) {
            System.out.print(Utilities.pad(name, 15) + " ");
            System.out.println("Not reliable.");
        } else {
            System.out.print(Utilities.pad(name, 15) + " ");
            System.out.print(Utilities.pad("" + count, 6));
            System.out.print(fmtTime(curTime));
            System.out.print(fmtTime(minTime));
            System.out.print(fmtTime(maxTime));
            System.out.print(fmtTime(avgTime));
            System.out.print(fmtTime(sum / 1000.0));
            System.out.print(percentFormatter.format(overallPercent));
            System.out.println();
        }
    }

    /**
     * Shows timing stats. If overall is non-zero, it represents
     * the overall processing time and a percentage of overall
     * time for this timer should be calculated and displayed.
     *
     * @param overall the overall processing time in milliseconds or 0.
     */
    public void showTimes(long overall) {
        showTimesShort(overall);
    }

    /**
     * Shows timing stats. No overall percentage is shown with this
     * method.
     */
    public void showTimes() {
        // showTimes(0L);
        showTimesShort(0L);
    }
}


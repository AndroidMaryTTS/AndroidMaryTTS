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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * Standalone utility that tells the time.
 * <p/>
 * Defaults to "alan" voice.
 */
public class FreeTTSTime extends FreeTTS {

    private final static String VERSION =
            "FreeTTSTime Version 1.1, August  1, 2003";

    /**
     * Class constructor.
     */
    public FreeTTSTime() {
        super(VoiceManager.getInstance().getVoice("alan"));
    }

    /**
     * Class constructor.
     *
     * @param voice Voice to say time with
     */
    public FreeTTSTime(Voice voice) {
        super(voice);
    }


    /**
     * Prints the usage message for FreeTTSTime.
     */
    public static void usage() {
        System.out.println(VERSION);
        System.out.println("Usage:");
        System.out.println("    -dumpASCII file : dump the final wave to file");
        System.out.println("    -dumpAudio file : dump audio to file ");
        System.out.println("    -help           : shows usage information");
        System.out.println("    -detailedMetrics: turn on detailed metrics");
        System.out.println("    -dumpRelations  : dump the relations ");
        System.out.println("    -dumpUtterance  : dump the final utterance");
        System.out.println("    -metrics        : turn on metrics");
        System.out.println("    -run  name      : sets the name of the run");
        System.out.println("    -silent         : don't say anything");
        System.out.println("    -verbose        : verbose output");
        System.out.println("    -version        : shows version number");
        System.out.println("    -timeTest       : runs a lengthy time test");
        System.out.println("    -iter count     : run for count iterations");
        System.out.println("    -time XX:XX     : speak the given time");
        System.out.println("    -time now       : speak the current time");
        System.out.println("    -period secs    : period of iter");
        System.out.println("    -clockMode      : tells time every 5 mins");
        System.out.println("    -voice VOICE    : " +
                VoiceManager.getInstance().toString());
        System.exit(0);
    }

    /**
     * Starts interactive mode on the given FreeTTSTime. Reads text
     * from the console and gives it to FreeTTSTime to speak.
     * terminates on end of file.
     *
     * @param freetts the engine that speaks
     */
    private static void interactiveMode(FreeTTSTime freetts) {
        try {
            while (true) {
                String time;
                BufferedReader reader;
                reader = new BufferedReader(
                        new InputStreamReader(System.in));
                System.out.print("Enter time: ");
                System.out.flush();
                time = reader.readLine();
                if ((time == null) || (time.length() == 0)
                        || time.equals("quit")) {
                    freetts.shutdown();
                    System.exit(0);
                } else {
                    freetts.getVoice().startBatch();
                    freetts.safeTimeToSpeech(time);
                    freetts.getVoice().endBatch();
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * Returns a phrase that conveys the exactness
     * of the time.
     *
     * @param hour the hour of the time
     * @param min  the minute of the time
     * @return a string phrase
     */
    private static String timeApprox(int hour, int min) {
        int mm;

        mm = min % 5;

        if ((mm == 0) || (mm == 4)) {
            return "exactly";
        } else if (mm == 1) {
            return "just after";
        } else if (mm == 2) {
            return "a little after";
        } else {
            return "almost";
        }
    }

    /**
     * Returns a phrase that conveys the minutes in relation
     * to the hour.
     *
     * @param hour the hour of the time
     * @param min  the minute of the time
     * @return a string phrase.
     */
    private static String timeMin(int hour, int min) {
        int mm;

        mm = min / 5;
        if ((min % 5) > 2) {
            mm += 1;
        }
        mm = mm * 5;
        if (mm > 55) {
            mm = 0;
        }

        if (mm == 0) {
            return "";
        } else if (mm == 5) {
            return "five past";
        } else if (mm == 10) {
            return "ten past";
        } else if (mm == 15) {
            return "quarter past";
        } else if (mm == 20) {
            return "twenty past";
        } else if (mm == 25) {
            return "twenty-five past";
        } else if (mm == 30) {
            return "half past";
        } else if (mm == 35) {
            return "twenty-five to";
        } else if (mm == 40) {
            return "twenty to";
        } else if (mm == 45) {
            return "quarter to";
        } else if (mm == 50) {
            return "ten to";
        } else if (mm == 55) {
            return "five to";
        } else {
            return "five to";
        }
    }

    /**
     * Returns a phrase that conveys the hour in relation
     * to the hour.
     *
     * @param hour the hour of the time
     * @param min  the minute of the time
     * @return a string phrase.
     */
    private static String timeHour(int hour, int min) {
        int hh;

        hh = hour;
        if (min > 32) { // PBL: fixed from flite_time
            hh += 1;
        }
        if (hh == 24) {
            hh = 0;
        }
        if (hh > 12) {
            hh -= 12;
        }

        if (hh == 0) {
            return "midnight";
        } else if (hh == 1) {
            return "one";
        } else if (hh == 2) {
            return "two";
        } else if (hh == 3) {
            return "three";
        } else if (hh == 4) {
            return "four";
        } else if (hh == 5) {
            return "five";
        } else if (hh == 6) {
            return "six";
        } else if (hh == 7) {
            return "seven";
        } else if (hh == 8) {
            return "eight";
        } else if (hh == 9) {
            return "nine";
        } else if (hh == 10) {
            return "ten";
        } else if (hh == 11) {
            return "eleven";
        } else if (hh == 12) {
            return "twelve";
        } else {
            return "twelve";
        }
    }

    /**
     * Returns a phrase that conveys the time of day.
     *
     * @param hour the hour of the time
     * @param min  the minute of the time
     * @return a string phrase
     */
    private static String timeOfDay(int hour, int min) {
        int hh = hour;

        if (min > 58)
            hh++;

        if (hh == 24) {
            return "";
        } else if (hh > 17) {
            return "in the evening";
        } else if (hh > 11) {
            return "in the afternoon";
        } else {
            return "in the morning";
        }
    }

    /**
     * Returns a string that corresponds to the given time.
     *
     * @param time the time in the form HH:MM
     * @return the time in string, null if the given time is not in the
     * form HH:MM
     */
    public static String timeToString(String time) {
        String theTime = null;
        if (Pattern.matches("[012][0-9]:[0-5][0-9]", time)) {
            int hour = Integer.parseInt(time.substring(0, 2));
            int min = Integer.parseInt(time.substring(3));

            theTime = timeToString(hour, min);
        }
        return theTime;
    }

    /**
     * Returns a string that corresponds to the given time.
     *
     * @param hour the hour
     * @param min  the minutes
     * @return the time in string, null if the given time out of range
     */
    public static String timeToString(int hour, int min) {
        String theTime = "The time is now, " +
                timeApprox(hour, min) + " " +
                timeMin(hour, min) + " " +
                timeHour(hour, min) + ", " +
                timeOfDay(hour, min) + ".";
        return theTime;
    }

    /**
     * The main entry point for FreeTTSTime.
     */
    public static void main(String[] args) {

        String time = null; // default is interactive mode
        String dumpFile = null;
        String protocol = null;

        boolean timeTest = false;
        int iterations = 1;
        int delay = 0;

        boolean setMetrics = false;
        boolean setDetailedMetrics = false;
        boolean setVerbose = false;
        boolean setDumpUtterance = false;
        boolean setDumpRelations = false;
        String waveDumpFile = null;
        String runTitle = null;

        boolean setSilentMode = false;
        String audioFile = null;
        boolean setInputMode = false;

        String voiceName = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-metrics")) {
                setMetrics = true;
            } else if (args[i].equals("-detailedMetrics")) {
                setDetailedMetrics = true;
            } else if (args[i].equals("-silent")) {
                setSilentMode = true;
            } else if (args[i].equals("-period")) {
                if (++i < args.length) {
                    try {
                        delay = Integer.parseInt(args[i]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Bad clock period");
                        usage();
                    }
                }
            } else if (args[i].equals("-verbose")) {
                setVerbose = true;
            } else if (args[i].equals("-dumpUtterance")) {
                setDumpUtterance = true;
            } else if (args[i].equals("-dumpRelations")) {
                setDumpRelations = true;
            } else if (args[i].equals("-clockMode")) {
                iterations = Integer.MAX_VALUE;
                delay = 300;
            } else if (args[i].equals("-timeTest")) {
                timeTest = true;
                iterations = 100;
            } else if (args[i].equals("-dumpAudio")) {
                if (++i < args.length) {
                    audioFile = args[i];
                } else {
                    usage();
                }
            } else if (args[i].equals("-iter")) {
                if (++i < args.length) {
                    try {
                        iterations = Integer.parseInt(args[i]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Bad iteration format");
                        usage();
                    }
                } else {
                    usage();
                }
            } else if (args[i].equals("-dumpASCII")) {
                if (++i < args.length) {
                    dumpFile = args[i];
                    waveDumpFile = args[i];
                } else {
                    usage();
                }
            } else if (args[i].equals("-version")) {
                System.out.println(VERSION);
            } else if (args[i].equals("-help")) {
                usage();
            } else if (args[i].equals("-time")) {
                setInputMode = true;
                if (++i < args.length) {
                    time = args[i];
                } else {
                    usage();
                }
            } else if (args[i].equals("-run")) {
                if (++i < args.length) {
                    runTitle = args[i];
                } else {
                    usage();
                }
            } else if (args[i].equals("-voice")) {
                if (++i < args.length) {
                    voiceName = args[i];
                } else {
                    usage();
                }
            } else {
                System.out.println("Unknown option:" + args[i]);
            }
        }

        if (voiceName == null) {
            voiceName = "alan";
        }

        FreeTTSTime freetts = new
                FreeTTSTime(VoiceManager.getInstance().getVoice(voiceName));
        Voice voice = freetts.getVoice();

        if (setMetrics) {
            voice.setMetrics(true);
        }

        if (setDetailedMetrics) {
            voice.setDetailedMetrics(true);
        }

        if (setVerbose) {
            voice.setVerbose(true);
        }

        if (setDumpUtterance) {
            voice.setDumpUtterance(true);
        }

        if (setDumpRelations) {
            voice.setDumpRelations(true);
        }

        if (waveDumpFile != null) {
            voice.setWaveDumpFile(waveDumpFile);
        }

        if (runTitle != null) {
            voice.setRunTitle(runTitle);
        }

        if (setSilentMode) {
            freetts.setSilentMode(true);
        }

        if (audioFile != null) {
            freetts.setAudioFile(audioFile);
        }

        if (setInputMode) {
            freetts.setInputMode(InputMode.TEXT);
        }


        freetts.startup();

        if (time != null) {
            freetts.getVoice().startBatch();
            for (int i = 0; i < iterations; i++) {
                freetts.safeTimeToSpeech(time);
                try {
                    Thread.sleep(delay * 1000L);
                } catch (InterruptedException ie) {
                    break;
                }
            }
            freetts.getVoice().endBatch();
        } else {
            interactiveMode(freetts);
        }
        freetts.shutdown();
        System.exit(0);
    }

    /**
     * Speaks the given time. Time should be in the exact form
     * HH:MM where HH is the hour 00 to 23, and MM is the minute 00 to
     * 59.
     *
     * @param time the time in the form HH:MM
     * @throws IllegalArgumentException if time is not in the form
     *                                  HH:MM
     */
    public void timeToSpeech(String time) {
        String theTime = timeToString(time);
        if (theTime != null) {
            textToSpeech(theTime);
        } else {
            throw new IllegalArgumentException("Bad time format");
        }
    }

    /**
     * Speaks the time given the hour and minute.
     *
     * @param hour the hour of the day (0 to 23)
     * @param min  the minute of the hour (0 to 59)
     */
    public void timeToSpeech(int hour, int min) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Bad time format: hour");
        }

        if (min < 0 || min > 59) {
            throw new IllegalArgumentException("Bad time format: min");
        }
        String theTime = timeToString(hour, min);
        textToSpeech(theTime);
    }

    /**
     * Speaks the given time.  Prints an error message if the time
     * is ill-formed.
     *
     * @param time the time in the form HH:MM
     */
    public void safeTimeToSpeech(String time) {
        try {
            if (time.equals("now")) {
                speakNow();
            } else {
                timeToSpeech(time);
            }
        } catch (IllegalArgumentException iae) {
            System.err.println("Bad time format");
        }
    }

    /**
     * Tells the current time.
     */
    public void speakNow() {
        long now = System.currentTimeMillis();
        Calendar cal = new GregorianCalendar();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        timeToSpeech(hour, min);
    }
}

  

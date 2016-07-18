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
package com.sun.speech.freetts.lexicon;

import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Provides the phone list for words using the CMU6 letter-to-sound
 * (LTS) rules, which are based on the Black, Lenzo, and Pagel paper,
 * "Issues in Building General Letter-to-Sound Rules."  Proceedings
 * of ECSA Workshop on Speech Synthesis, pages 77-80, Australia, 1998.
 * <p/>
 * <p>The LTS rules are a simple state machine, with one entry point
 * for each letter of the alphabet (lower case letters are always
 * assumed, and the rules keep an array with one entry per letter that
 * point into the state machine).
 * <p/>
 * <p>The state machine consists of a huge array, with most entries
 * containing a decision and the indeces of two other entries. The
 * first of these two indeces represents where to go if the decision
 * is true, and the second represents where to go if the decision is
 * false. All entries that do not contain a decision are final
 * entries, and these contain a phone.
 * <p/>
 * <p>The decision in this case is a simple character comparison,
 * but it is done in the context of a window around the character in
 * the word. The decision consists of a index into the context window
 * and a character value. If the character in the context window
 * matches the character value, then the decision is true.
 * <p/>
 * <p>The machine traversal for each letter starts at that letter's
 * entry in the state machine and ends only when it reaches a final
 * state. If there is no phone that can be mapped, the phone in the
 * final state is set to 'epsilon.'
 * <p/>
 * <p>The context window for a character is generated in the following
 * way:
 * <p/>
 * <ul>
 * <li>Pad the original word on either side with '#' and '0'
 * characters the size of the window for the LTS rules (in this case,
 * the window size is 4). The "#" is used to indicate the beginning
 * and end of the word. So, the word "monkey" would turn into
 * "000#monkey#000".
 * <li>For each character in the word, the context window consists of
 * the characters in the padded form the preceed and follow the word.
 * The number of characters on each side is dependent upon the window
 * size. So, for this implementation, the context window for the 'k'
 * in monkey is "#money#0".
 * </ul>
 * <p/>
 * <p>Here's how the phone for 'k' in 'monkey' might be determined:
 * <p/>
 * <ul>
 * <li>Create the context window "#money#0".
 * <li>Start at the state machine entry for 'k' in the state machine.
 * <li>Grab the 'index' from the current state. This represents an
 * index into the context window.
 * <li>Compare the value of the character at the index in the context
 * window to the character from the current state. If there is a
 * match, the next state is the qtrue value. If there isn't a match,
 * the next state is the qfalse state.
 * <li>Keep on working through the machine until you read a final
 * state.
 * <li>When you get to the final state, the phone is the character in
 * that state.
 * </ul>
 * <p/>
 * <p>This implementation will either read from a straight ASCII file
 * or a binary file.  When reading from an ASCII file, you can specify
 * when the input line is tokenized:  load, lookup, or never.  If you
 * specify 'load', the entire file will be parsed when it is loaded.
 * If you specify 'lookup', the file will be loaded, but the parsing
 * for each line will be delayed until it is referenced and the parsed
 * form will be saved away.  If you specify 'never', the lines will
 * parsed each time they are referenced.  The default is 'load'.  To
 * specify the load type, set the system property as follows:
 * <p/>
 * <pre>
 *   -Dcom.sun.speech.freetts.lexicon.LTSTokenize=load
 * </pre>
 * <p/>
 * <p>[[[TODO:  This implementation uses ASCII 'a'-'z', which is not
 * internationalized.]]]
 */
public class LetterToSoundImpl implements LetterToSound {
    /**
     * Entry in file represents the total number of states in the
     * file.  This should be at the top of the file.  The format
     * should be "TOTAL n" where n is an integer value.
     */
    final static String TOTAL = "TOTAL";

    /**
     * Entry in file represents the beginning of a new letter index.
     * This should appear before the list of a new set of states for
     * a particular letter.  The format should be "INDEX n c" where
     * n is the index into the state machine array and c is the
     * character.
     */
    final static String INDEX = "INDEX";

    /**
     * Entry in file represents a state.  The format should be
     * "STATE i c t f" where 'i' represents an index to look at in the
     * decision string, c is the character that should match, t is the
     * index of the state to go to if there is a match, and f is the
     * of the state to go to if there isn't a match.
     */
    final static String STATE = "STATE";

    /**
     * Entry in file represents a final state.  The format should be
     * "PHONE p" where p represents a phone string that comes from the
     * phone table.
     */
    final static String PHONE = "PHONE";
    /**
     * Magic number for binary LTS files.
     */
    private final static int MAGIC = 0xdeadbeef;
    /**
     * Current binary file version.
     */
    private final static int VERSION = 1;
    /**
     * The 'window size' of the LTS rules.
     */
    private final static int WINDOW_SIZE = 4;
    /**
     * The list of phones that can be returned by the LTS rules.
     */
    static private List phonemeTable;
    /**
     * If true, the state string is tokenized when it is first read.
     * The side effects of this are quicker lookups, but more memory
     * usage and a longer startup time.
     */
    protected boolean tokenizeOnLoad = false;
    /**
     * If true, the state string is tokenized the first time it is
     * referenced.  The side effects of this are quicker lookups, but
     * more memory usage.
     */
    protected boolean tokenizeOnLookup = false;
    /**
     * The indexes of the starting points for letters in the state machine.
     */
    protected HashMap letterIndex;
    /**
     * The LTS state machine. Entries can be String or State.  An
     * ArrayList could be used here -- I chose not to because I
     * thought it might be quicker to avoid dealing with the dynamic
     * resizing.
     */
    private Object[] stateMachine = null;
    /**
     * The number of states in the state machine.
     */
    private int numStates = 0;
    /**
     * An array of characters to hold a string for checking against a
     * rule.  This will be reused over and over again, so the goal
     * was just to have a single area instead of new'ing up a new one
     * for every word.  The name choice is to match that in Flite's
     * <code>cst_lts.c</code>.
     */
    private char[] fval_buff = new char[WINDOW_SIZE * 2];

    /**
     * Class constructor.
     *
     * @param ltsRules a URL pointing to the text
     *                 containing the letter to sound rules
     * @param binary   if true, the URL is a binary source
     * @throws NullPointerException if the ltsRules are null
     * @throws IOException          if errors are encountered while reading the
     *                              compiled form or the addenda
     */
    public LetterToSoundImpl(URL ltsRules, boolean binary) throws IOException {
        BulkTimer.LOAD.start("LTS");
        InputStream is = ltsRules.openStream();
        if (binary) {
            loadBinary(is);
        } else {
            loadText(is);
        }
        is.close();
        BulkTimer.LOAD.stop("LTS");
    }

    /**
     * Translates between text and binary forms of the CMU6 LTS rules.
     */
    public static void main(String[] args) {
        LexiconImpl lex, lex2;
        boolean showTimes = false;
        String srcPath = ".";
        String destPath = ".";
        String name = "cmulex_lts";

        try {
            if (args.length > 0) {
                BulkTimer timer = new BulkTimer();
                timer.start();
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-src")) {
                        srcPath = args[++i];
                    } else if (args[i].equals("-dest")) {
                        destPath = args[++i];
                    } else if (args[i].equals("-name")
                            && i < args.length - 1) {
                        name = args[++i];
                    } else if (args[i].equals("-generate_binary")) {

                        System.out.println("Loading " + name);
                        timer.start("load_text");
                        LetterToSoundImpl text = new LetterToSoundImpl(
                                new URL("file:" + srcPath + "/"
                                        + name + ".txt"),
                                false);
                        timer.stop("load_text");

                        System.out.println("Dumping " + name);
                        timer.start("dump_binary");
                        text.dumpBinary(destPath + "/" + name + ".bin");
                        timer.stop("dump_binary");

                    } else if (args[i].equals("-compare")) {

                        timer.start("load_text");
                        LetterToSoundImpl text = new LetterToSoundImpl(
                                new URL("file:./" + name + ".txt"), false);
                        timer.stop("load_text");

                        timer.start("load_binary");
                        LetterToSoundImpl binary = new LetterToSoundImpl(
                                new URL("file:./" + name + ".bin"), true);
                        timer.stop("load_binary");

                        timer.start("compare");
                        if (!text.compare(binary)) {
                            System.out.println("NOT EQUIVALENT");
                        } else {
                            System.out.println("ok");
                        }
                        timer.stop("compare");
                    } else if (args[i].equals("-showtimes")) {
                        showTimes = true;
                    } else {
                        System.out.println("Unknown option " + args[i]);
                    }
                }
                timer.stop();
                if (showTimes) {
                    timer.show("LTS loading and dumping");
                }
            } else {
                System.out.println("Options: ");
                System.out.println("    -src path");
                System.out.println("    -dest path");
                System.out.println("    -compare");
                System.out.println("    -generate_binary");
                System.out.println("    -showTimes");
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    /**
     * Loads the LTS rules from the given text input stream.  The
     * stream is not closed after the rules are read.
     *
     * @param is the input stream
     * @throws IOException if an error occurs on input.
     */
    private void loadText(InputStream is) throws IOException {
        BufferedReader reader;
        String line;

        // Find out when to convert the phone string into an array.
        //
        String tokenize =
                Utilities.getProperty("com.sun.speech.freetts.lexicon.LTSTokenize",
                        "load");
        tokenizeOnLoad = tokenize.equals("load");
        tokenizeOnLookup = tokenize.equals("lookup");

        letterIndex = new HashMap();

        reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        while (line != null) {
            if (!line.startsWith("***")) {
                parseAndAdd(line);
            }
            line = reader.readLine();
        }
    }

    /**
     * Loads the LTS rules from the given binary input stream.  The
     * input stream is not closed after the rules are read.
     *
     * @param is the input stream
     * @throws IOException if an error occurs on input.
     */
    private void loadBinary(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        if (dis.readInt() != MAGIC) {
            throw new Error("Bad LTS binary file format");
        }

        if (dis.readInt() != VERSION) {
            throw new Error("Bad LTS binary file version");
        }

        // read the phoneme table
        //
        int phonemeTableSize = dis.readInt();
        phonemeTable = new ArrayList(phonemeTableSize);

        for (int i = 0; i < phonemeTableSize; i++) {
            String phoneme = dis.readUTF();
            phonemeTable.add(phoneme);
        }

        // letter index
        //
        int letterIndexSize = dis.readInt();
        letterIndex = new HashMap();
        for (int i = 0; i < letterIndexSize; i++) {
            char c = dis.readChar();
            int index = dis.readInt();
            letterIndex.put(Character.toString(c), new Integer(index));
        }

        // statemachine states
        //
        int stateMachineSize = dis.readInt();
        stateMachine = new Object[stateMachineSize];
        for (int i = 0; i < stateMachineSize; i++) {
            int type = dis.readInt();

            if (type == FinalState.TYPE) {
                stateMachine[i] = FinalState.loadBinary(dis);
            } else if (type == DecisionState.TYPE) {
                stateMachine[i] = DecisionState.loadBinary(dis);
            } else {
                throw new Error("Unknown state type in LTS load");
            }
        }
    }

    /**
     * Creates a word from the given input line and add it to the state
     * machine.  It expects the TOTAL line to come before any of the
     * states.
     *
     * @param line the line of text from the input file
     */
    protected void parseAndAdd(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        String type = tokenizer.nextToken();

        if (type.equals(STATE) || type.equals(PHONE)) {
            if (tokenizeOnLoad) {
                stateMachine[numStates] = getState(type, tokenizer);
            } else {
                stateMachine[numStates] = line;
            }
            numStates++;
        } else if (type.equals(INDEX)) {
            Integer index = new Integer(tokenizer.nextToken());
            if (index.intValue() != numStates) {
                throw new Error("Bad INDEX in file.");
            } else {
                String c = tokenizer.nextToken();
                letterIndex.put(c, index);
            }
        } else if (type.equals(TOTAL)) {
            stateMachine = new Object[Integer.parseInt(tokenizer.nextToken())];
        }
    }

    /**
     * Dumps a binary form of the letter to sound rules.
     * This method is not thread-safe.
     * <p/>
     * <p>Binary format is:
     * <pre>
     *   MAGIC
     *   VERSION
     *   NUM STATES
     *   for each state ...
     * </pre>
     *
     * @param path the path to dump the file to
     * @throws IOException if a problem occurs during the dump
     */
    public void dumpBinary(String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        DataOutputStream dos = new DataOutputStream(new
                BufferedOutputStream(fos));

        dos.writeInt(MAGIC);
        dos.writeInt(VERSION);

        // Phoneme table
        //
        phonemeTable = findPhonemes();
        dos.writeInt(phonemeTable.size());
        for (Iterator i = phonemeTable.iterator(); i.hasNext(); ) {
            String phoneme = (String) i.next();
            dos.writeUTF(phoneme);
        }

        // letter index
        //
        dos.writeInt(letterIndex.size());
        for (Iterator i = letterIndex.keySet().iterator(); i.hasNext(); ) {
            String letter = (String) i.next();
            int index = ((Integer) letterIndex.get(letter)).intValue();
            dos.writeChar(letter.charAt(0));
            dos.writeInt(index);
        }

        // statemachine states
        //
        dos.writeInt(stateMachine.length);

        for (int i = 0; i < stateMachine.length; i++) {
            getState(i).writeBinary(dos);
        }
        dos.close();
    }

    /**
     * Returns a list of all the phonemes used by the LTS rules.
     *
     * @return a list of all the phonemes
     */
    private List findPhonemes() {
        Set set = new HashSet();
        for (int i = 0; i < stateMachine.length; i++) {
            if (stateMachine[i] instanceof FinalState) {
                FinalState fstate = (FinalState) stateMachine[i];
                if (fstate.phoneList != null) {
                    for (int j = 0; j < fstate.phoneList.length; j++) {
                        set.add(fstate.phoneList[j]);
                    }
                }
            }
        }
        return new ArrayList(set);
    }

    /**
     * Gets the <code>State</code> at the given index.  This may
     * replace a <code>String</code> at
     * the current spot with an actual <code>State</code> instance.
     *
     * @param i the index into the state machine
     * @return the <code>State</code> at the given index.
     */
    protected State getState(int i) {
        State state = null;
        if (stateMachine[i] instanceof String) {
            state = getState((String) stateMachine[i]);
            if (tokenizeOnLookup) {
                stateMachine[i] = state;
            }
        } else {
            state = (State) stateMachine[i];
        }
        return state;
    }

    /**
     * Gets the <code>State</code> based upon the <code>String</code>.
     *
     * @param s the string to parse
     * @return the parsed <code>State</code>
     */
    protected State getState(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, " ");
        return getState(tokenizer.nextToken(), tokenizer);
    }

    /**
     * Gets the <code>State</code> based upon the <code>type</code>
     * and <code>tokenizer<code>.
     *
     * @param type      one of <code>STATE</code> or <code>PHONE</code>
     * @param tokenizer a <code>StringTokenizer</code> containing the
     *                  <code>State</code>
     * @return the parsed <code>State</code>
     */
    protected State getState(String type, StringTokenizer tokenizer) {
        if (type.equals(STATE)) {
            int index = Integer.parseInt(tokenizer.nextToken());
            String c = tokenizer.nextToken();
            int qtrue = Integer.parseInt(tokenizer.nextToken());
            int qfalse = Integer.parseInt(tokenizer.nextToken());
            return new DecisionState(index, c.charAt(0), qtrue, qfalse);
        } else if (type.equals(PHONE)) {
            return new FinalState(tokenizer.nextToken());
        }
        return null;
    }

    /**
     * Makes a character array that looks like "000#word#000".
     *
     * @param word the original word
     * @return the padded word
     */
    protected char[] getFullBuff(String word) {
        char[] full_buff = new char[word.length() + (2 * WINDOW_SIZE)];

        // Make full_buff look like "000#word#000"
        //
        for (int i = 0; i < (WINDOW_SIZE - 1); i++) {
            full_buff[i] = '0';
        }
        full_buff[WINDOW_SIZE - 1] = '#';
        word.getChars(0, word.length(), full_buff, WINDOW_SIZE);
        for (int i = 0; i < (WINDOW_SIZE - 1); i++) {
            full_buff[full_buff.length - i - 1] = '0';
        }
        full_buff[full_buff.length - WINDOW_SIZE] = '#';
        return full_buff;
    }

    /**
     * Calculates the phone list for a given word.  If a phone list cannot
     * be determined, <code>null</code> is returned.  This particular
     * implementation ignores the part of speech.
     *
     * @param word         the word to find
     * @param partOfSpeech the part of speech.
     * @return the list of phones for word or <code>null</code>
     */
    public String[] getPhones(String word, String partOfSpeech) {
        ArrayList phoneList = new ArrayList();
        State currentState;
        Integer startIndex;
        int stateIndex;
        char c;

        // Create "000#word#000"
        //
        char[] full_buff = getFullBuff(word);

        // For each character in the word, create a WINDOW_SIZE
        // context on each size of the character, and then ask the
        // state machine what's next.  It's magic.  BTW, this goes
        // through the word from beginning to end.  Flite goes through
        // it from end to beginning.  There doesn't seem to be a
        // difference in the result.
        //
        for (int pos = 0; pos < word.length(); pos++) {
            for (int i = 0; i < WINDOW_SIZE; i++) {
                fval_buff[i] = full_buff[pos + i];
                fval_buff[i + WINDOW_SIZE] =
                        full_buff[i + pos + 1 + WINDOW_SIZE];
            }
            c = word.charAt(pos);
            startIndex = (Integer) letterIndex.get(Character.toString(c));
            if (startIndex == null) {
                continue;
            }
            assert (startIndex != null);
            stateIndex = startIndex.intValue();
            currentState = getState(stateIndex);
            while (!(currentState instanceof FinalState)) {
                stateIndex =
                        ((DecisionState)
                                currentState).getNextState(fval_buff);
                currentState = getState(stateIndex);
            }
            ((FinalState) currentState).append(phoneList);
        }
        return (String[]) phoneList.toArray(new String[0]);
    }

    /**
     * Compares this LTS to another for debugging purposes.
     *
     * @param other the other LTS to compare to
     * @return <code>true</code> if these are equivalent
     */
    public boolean compare(LetterToSoundImpl other) {

        // compare letter index table
        //
        for (Iterator i = letterIndex.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            Integer thisIndex = (Integer) letterIndex.get(key);
            Integer otherIndex = (Integer) other.letterIndex.get(key);
            if (!thisIndex.equals(otherIndex)) {
                System.out.println("Bad Index for " + key);
                return false;
            }
        }

        // compare states
        //
        for (int i = 0; i < stateMachine.length; i++) {
            State state = getState(i);
            State otherState = other.getState(i);
            if (!state.compare(otherState)) {
                System.out.println("Bad state " + i);
                return false;
            }
        }

        return true;
    }


    /**
     * A marker interface for the states in the LTS state machine.
     *
     * @see DecisionState
     * @see FinalState
     */
    interface State {
        void writeBinary(DataOutputStream dos) throws IOException;

        boolean compare(State other);
    }

    /**
     * A <code>State</code> that represents a decision to be made.
     *
     * @see FinalState
     */
    static class DecisionState implements State {
        final static int TYPE = 1;
        int index;
        char c;
        int qtrue;
        int qfalse;

        /**
         * Class constructor.
         *
         * @param index  the index into a string for comparison to c
         * @param c      the character to match in a string at index
         * @param qtrue  the state to go to in the state machine on a match
         * @param qfalse the state to go to in the state machine on no match
         */
        public DecisionState(int index, char c, int qtrue, int qfalse) {
            this.index = index;
            this.c = c;
            this.qtrue = qtrue;
            this.qfalse = qfalse;
        }

        /**
         * Loads a <code>DecisionState</code> object from the given
         * input stream.
         *
         * @param dis the data input stream
         * @return a newly constructed decision state
         * @throws IOException if an error occurs
         */
        public static State loadBinary(DataInputStream dis)
                throws IOException {
            int index = dis.readInt();
            char c = dis.readChar();
            int qtrue = dis.readInt();
            int qfalse = dis.readInt();
            return new DecisionState(index, c, qtrue, qfalse);
        }

        /**
         * Gets the next state to go to based upon the given character
         * sequence.
         *
         * @param chars the characters for comparison
         * @ret an index into the state machine.
         */
        public int getNextState(char[] chars) {
            return (chars[index] == c) ? qtrue : qfalse;
        }

        /**
         * Outputs this <code>State</code> as though it came from the
         * text input file.
         *
         * @return a <code>String</code> describing this <code>State</code>.
         */
        public String toString() {
            return STATE + " " + Integer.toString(index)
                    + " " + Character.toString(c)
                    + " " + Integer.toString(qtrue)
                    + " " + Integer.toString(qfalse);
        }

        /**
         * Writes this <code>State</code> to the given output stream.
         *
         * @param dos the data output stream
         * @throws IOException if an error occurs
         */
        public void writeBinary(DataOutputStream dos) throws IOException {
            dos.writeInt(TYPE);
            dos.writeInt(index);
            dos.writeChar(c);
            dos.writeInt(qtrue);
            dos.writeInt(qfalse);
        }

        /**
         * Compares this state to another state for debugging purposes.
         *
         * @param other the other state to compare against
         * @return true if the states are equivalent
         */
        public boolean compare(State other) {
            if (other instanceof DecisionState) {
                DecisionState otherState = (DecisionState) other;
                return index == otherState.index &&
                        c == otherState.c &&
                        qtrue == otherState.qtrue &&
                        qfalse == otherState.qfalse;
            }
            return false;
        }
    }

    /**
     * A <code>State</code> that represents a final state in the
     * state machine.  It contains one or more phones from the
     * phone table.
     *
     * @see DecisionState
     */
    static class FinalState implements State {
        final static int TYPE = 2;
        String[] phoneList;

        /**
         * Class constructor.  The string "epsilon" is used to indicate
         * an empty list.
         *
         * @param phones the phones for this state
         */
        public FinalState(String phones) {
            if (phones.equals("epsilon")) {
                phoneList = null;
            } else {
                int i = phones.indexOf('-');
                if (i != -1) {
                    phoneList = new String[2];
                    phoneList[0] = phones.substring(0, i);
                    phoneList[1] = phones.substring(i + 1);
                } else {
                    phoneList = new String[1];
                    phoneList[0] = phones;
                }
            }
        }

        /**
         * Class constructor.
         *
         * @param phones an array of phones for this state
         */
        public FinalState(String[] phones) {
            phoneList = phones;
        }

        /**
         * Loads a FinalState object from the given input stream
         *
         * @param dis the data input stream
         * @return a newly constructed final state
         * @throws IOException if an error occurs
         */
        public static State loadBinary(DataInputStream dis)
                throws IOException {
            String[] phoneList;
            int phoneListLength = dis.readInt();

            if (phoneListLength == 0) {
                phoneList = null;
            } else {
                phoneList = new String[phoneListLength];
            }
            for (int i = 0; i < phoneListLength; i++) {
                int index = dis.readInt();
                phoneList[i] = (String) phonemeTable.get(index);
            }
            return new FinalState(phoneList);
        }

        /**
         * Appends the phone list for this state to the given
         * <code>ArrayList</code>.
         *
         * @param array the array to append to
         */
        public void append(ArrayList array) {
            if (phoneList == null) {
                return;
            } else {
                for (int i = 0; i < phoneList.length; i++) {
                    array.add(phoneList[i]);
                }
            }
        }

        /**
         * Outputs this <code>State</code> as though it came from the
         * text input file.  The string "epsilon" is used to indicate
         * an empty list.
         *
         * @return a <code>String</code> describing this <code>State</code>
         */
        public String toString() {
            if (phoneList == null) {
                return PHONE + " epsilon";
            } else if (phoneList.length == 1) {
                return PHONE + " " + phoneList[0];
            } else {
                return PHONE + " " + phoneList[0] + "-" + phoneList[1];
            }
        }

        /**
         * Compares this state to another state for debugging
         * purposes.
         *
         * @param other the other state to compare against
         * @return <code>true</code> if the states are equivalent
         */
        public boolean compare(State other) {
            if (other instanceof FinalState) {
                FinalState otherState = (FinalState) other;
                if (phoneList == null) {
                    return otherState.phoneList == null;
                } else {
                    for (int i = 0; i < phoneList.length; i++) {
                        if (!phoneList[i].equals(otherState.phoneList[i])) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * Writes this state to the given output stream.
         *
         * @param dos the data output stream
         * @throws IOException if an error occurs
         */
        public void writeBinary(DataOutputStream dos) throws IOException {
            dos.writeInt(TYPE);
            if (phoneList == null) {
                dos.writeInt(0);
            } else {
                dos.writeInt(phoneList.length);
                for (int i = 0; i < phoneList.length; i++) {
                    dos.writeInt(phonemeTable.indexOf(phoneList[i]));
                }
            }
        }
    }
}

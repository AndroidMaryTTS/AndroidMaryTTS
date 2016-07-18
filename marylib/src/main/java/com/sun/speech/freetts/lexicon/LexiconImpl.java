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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Provides an implementation of a Lexicon.
 * <p/>
 * <p>This implementation will either read from a straight ASCII file
 * or a binary file.  When reading from an ASCII file, you can specify
 * when the input line is tokenized:  load, lookup, or never.  If you
 * specify 'load', the entire file will be parsed when it is loaded.
 * If you specify 'lookup', the file will be loaded, but the parsing
 * for each line will be delayed until it is referenced and the parsed
 * form will be saved away.  If you specify 'never', the lines will
 * parsed each time they are referenced.  The default is 'never'.  To
 * specify the load type, set the system property as follows:
 * <p/>
 * <pre>
 *   -Dcom.sun.speech.freetts.lexicon.LexTokenize=load
 * </pre>
 * <p/>
 * <p>If a binary file is used, you can also specify whether the new
 * IO package is used.  The new IO package is new for JDK1.4, and can
 * greatly improve the speed of loading files.  To enable new IO, use
 * the following system property (it is enabled by default):
 * <p/>
 * <pre>
 *   -Dcom.sun.speech.freetts.useNewIO=true
 * </pre>
 * <p/>
 * <p>The implementation also allows users to define their own addenda
 * that will be used in addition to the system addenda.  If the user
 * defines their own addenda, it values will be added to the system
 * addenda, overriding any existing elements in the system addenda.
 * To define a user addenda, the user needs to set the following
 * property:
 * <p/>
 * <pre>
 *   -Dcom.sun.speeech.freetts.lexicon.userAddenda=&lt;URLToUserAddenda>
 * </pre>
 * <p/>
 * Where &lt;URLToUserAddenda> is a URL pointing to an ASCII file
 * containing addenda entries.
 * <p/>
 * <p>[[[TODO: support multiple homographs with the same part of speech.]]]
 */
abstract public class LexiconImpl implements Lexicon {
    /**
     * Magic number for binary Lexicon files.
     */
    private final static int MAGIC = 0xBABB1E;
    /**
     * Current binary file version.
     */
    private final static int VERSION = 1;
    /**
     * No phones for this word.
     */
    final static private String[] NO_PHONES = new String[0];
    /**
     * A static directory of compiledURL URL objects and associated
     * already-loaded compiled Map objects. This is used to share
     * the immutable compiled lexicons between lexicon instances.
     * As the addenda can be changed using <code>addAddendum()</code>
     * and <code>removeAddendum</code>, each lexicon instance has its
     * own addenda.
     */
    private static Map loadedCompiledLexicons;
    /**
     * If true, the phone string is replaced with the phone array in
     * the hashmap when the phone array is loaded.  The side effects
     * of this are quicker lookups, but more memory usage and a longer
     * startup time.
     */
    protected boolean tokenizeOnLoad = false;
    /**
     * If true, the phone string is replaced with the phone array in
     * the hashmap when the phone array is first looked up.  The side effects
     * Set by cmufilelex.tokenize=lookup.
     */
    protected boolean tokenizeOnLookup = false;
    /**
     * URL for the compiled form.
     */
    private URL compiledURL;
    /**
     * URL for the addenda.
     */
    private URL addendaURL;
    /**
     * URL for the letter to sound rules.
     */
    private URL letterToSoundURL;
    /**
     * The addenda.
     */
    private Map addenda;
    /**
     * The compiled lexicon.
     */
    private Map compiled;
    /**
     * The LetterToSound rules.
     */
    private LetterToSound letterToSound = null;
    /**
     * Parts of Speech.
     */
    private ArrayList partsOfSpeech = new ArrayList();
    /**
     * Loaded State of the lexicon
     */
    private boolean loaded = false;
    /**
     * Type of lexicon to load
     */
    private boolean binary = false;
    /**
     * Temporary place holder.
     */
    private char charBuffer[] = new char[128];

    /**
     * Use the new IO package?
     */
    private boolean useNewIO =
            Utilities.getProperty("com.sun.speech.freetts.useNewIO",
                    "true").equals("true");

    /**
     * Create a new LexiconImpl by reading from the given URLS.
     *
     * @param compiledURL      a URL pointing to the compiled lexicon
     * @param addendaURL       a URL pointing to lexicon addenda
     * @param letterToSoundURL a LetterToSound to use if a word cannot
     *                         be found in the compiled form or the addenda
     * @param binary           if <code>true</code>, the input streams are binary;
     *                         otherwise, they are text.
     */
    public LexiconImpl(URL compiledURL, URL addendaURL,
                       URL letterToSoundURL,
                       boolean binary) {
        this();
        setLexiconParameters(compiledURL, addendaURL, letterToSoundURL, binary);
    }

    /**
     * Class constructor for an empty Lexicon.
     */
    public LexiconImpl() {
        // Find out when to convert the phone string into an array.
        //
        String tokenize =
                Utilities.getProperty("com.sun.speech.freetts.lexicon.LexTokenize",
                        "never");
        tokenizeOnLoad = tokenize.equals("load");
        tokenizeOnLookup = tokenize.equals("lookup");
    }

    /**
     * Fixes the part of speech if it is <code>null</code>.  The
     * default representation of a <code>null</code> part of speech
     * is the number "0".
     */
    static protected String fixPartOfSpeech(String partOfSpeech) {
        return (partOfSpeech == null) ? "0" : partOfSpeech;
    }

    /**
     * Sets the lexicon parameters
     *
     * @param compiledURL      a URL pointing to the compiled lexicon
     * @param addendaURL       a URL pointing to lexicon addenda
     * @param letterToSoundURL a URL pointing to the LetterToSound to use
     * @param binary           if <code>true</code>, the input streams are binary;
     *                         otherwise, they are text.
     */
    protected void setLexiconParameters(URL compiledURL,
                                        URL addendaURL,
                                        URL letterToSoundURL,
                                        boolean binary) {
        this.compiledURL = compiledURL;
        this.addendaURL = addendaURL;
        this.letterToSoundURL = letterToSoundURL;
        this.binary = binary;
    }

    /**
     * Determines if this lexicon is loaded.
     *
     * @return <code>true</code> if the lexicon is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Loads the data for this lexicon.  If the
     *
     * @throws IOException if errors occur during loading
     */
    public void load() throws IOException {
        BulkTimer.LOAD.start("Lexicon");

        //##########################
        //compiledURL = new URL("jar:file:/cmulex.jar/com.sun.speech.freetts.en.us.cmulex_compiled.bin");
        compiledURL = Utilities.getResourceURL("cmulex_compiled.bin");
        //addendaURL = new URL("jar:file:/cmulex.jar/com.sub.speech.freetts.en.us.cmulex_addenda.bin");
        addendaURL = Utilities.getResourceURL("cmulex_addenda.bin");
        //##########################

        if (compiledURL == null) {
            throw new IOException("Can't load lexicon");
        }

        if (addendaURL == null) {
            throw new IOException("Can't load lexicon addenda ");
        }

        if (loadedCompiledLexicons == null) {
            loadedCompiledLexicons = new HashMap();
        }
        if (!loadedCompiledLexicons.containsKey(compiledURL)) {
            InputStream compiledIS = Utilities.getInputStream(compiledURL);
            if (compiledIS == null) {
                throw new IOException("Can't load lexicon from " + compiledURL);
            }
            Map newCompiled = createLexicon(compiledIS, binary, 65000);
            loadedCompiledLexicons.put(compiledURL, newCompiled);
            compiledIS.close();
        }
        assert loadedCompiledLexicons.containsKey(compiledURL);
        compiled = Collections.unmodifiableMap((Map) loadedCompiledLexicons.get(compiledURL));

        InputStream addendaIS = Utilities.getInputStream(addendaURL);
        if (addendaIS == null) {
            throw new IOException("Can't load lexicon addenda from "
                    + addendaURL);
        }

        // [[[TODO: what is the best way to derive the estimated sizes?]]]
        //
        addenda = createLexicon(addendaIS, binary, 50);
        addendaIS.close();

        /* Load the user-defined addenda and override any existing
         * entries in the system addenda.
         */
        String userAddenda = Utilities.getProperty(
                "com.sun.speech.freetts.lexicon.userAddenda", null);
        if (userAddenda != null) {
            try {
                URL userAddendaURL = new URL(userAddenda);
                InputStream userAddendaIS = Utilities.getInputStream(
                        userAddendaURL);
                if (userAddendaIS == null) {
                    throw new IOException("Can't load user addenda from "
                            + userAddenda);
                }
                Map tmpAddenda = createLexicon(userAddendaIS, false, 50);
                userAddendaIS.close();
                for (Iterator keys = tmpAddenda.keySet().iterator();
                     keys.hasNext(); ) {
                    Object key = keys.next();
                    addenda.put(key, tmpAddenda.get(key));
                }
            } catch (MalformedURLException e) {
                throw new IOException("User addenda URL is malformed: " +
                        userAddenda);
            }
        }

        loaded = true;
        BulkTimer.LOAD.stop("Lexicon");
        letterToSound = new LetterToSoundImpl(letterToSoundURL, binary);
    }

    /**
     * Reads the given input stream as lexicon data and returns the
     * results in a <code>Map</code>.
     *
     * @param is            the input stream
     * @param binary        if <code>true</code>, the data is binary
     * @param estimatedSize the estimated size of the lexicon
     * @throws IOException if errors are encountered while reading the data
     */
    protected Map createLexicon(InputStream is,
                                boolean binary,
                                int estimatedSize)
            throws IOException {
        if (binary) {
            if (useNewIO && is instanceof FileInputStream) {
                FileInputStream fis = (FileInputStream) is;
                return loadMappedBinaryLexicon(fis, estimatedSize);
            } else {
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(is));
                return loadBinaryLexicon(dis, estimatedSize);
            }
        } else {
            return loadTextLexicon(is, estimatedSize);
        }
    }

    /**
     * Reads the given input stream as text lexicon data and returns the
     * results in a <code>Map</code>.
     *
     * @param is            the input stream
     * @param estimatedSize the estimated number of entries of the lexicon
     * @throws IOException if errors are encountered while reading the data
     */
    protected Map loadTextLexicon(InputStream is, int estimatedSize)
            throws IOException {
        Map lexicon = new LinkedHashMap(estimatedSize * 4 / 3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        line = reader.readLine();
        while (line != null) {
            if (!line.startsWith("***")) {
                parseAndAdd(lexicon, line);
            }
            line = reader.readLine();
        }
        return lexicon;
    }

    /**
     * Creates a word from the given input line and add it to the lexicon.
     *
     * @param lexicon the lexicon
     * @param line    the input text
     */
    protected void parseAndAdd(Map lexicon, String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, "\t");
        String phones = null;

        String wordAndPos = tokenizer.nextToken();
        String pos = wordAndPos.substring(wordAndPos.length() - 1);
        if (!partsOfSpeech.contains(pos)) {
            partsOfSpeech.add(pos);
        }
        if (tokenizer.hasMoreTokens()) {
            phones = tokenizer.nextToken();
        }
        if ((phones != null) && (tokenizeOnLoad)) {
            lexicon.put(wordAndPos, getPhones(phones));
        } else if (phones == null) {
            lexicon.put(wordAndPos, NO_PHONES);
        } else {
            lexicon.put(wordAndPos, phones);
        }
    }

    /**
     * Gets the phone list for a given word.  If a phone list cannot
     * be found, returns <code>null</code>.  The format is lexicon
     * dependent.  If the part of speech does not matter, pass in
     * <code>null</code>.
     *
     * @param word         the word to find
     * @param partOfSpeech the part of speech
     * @return the list of phones for word or <code>null</code>
     */
    public String[] getPhones(String word, String partOfSpeech) {
        return getPhones(word, partOfSpeech, true);
    }

    /**
     * Gets the phone list for a given word.  If a phone list cannot
     * be found, <code>null</code> is returned.  The
     * <code>partOfSpeech</code> is implementation dependent, but
     * <code>null</code> always matches.
     *
     * @param word         the word to find
     * @param partOfSpeech the part of speech or <code>null</code>
     * @param useLTS       whether to use the letter-to-sound rules when
     *                     the word is not in the lexicon.
     * @return the list of phones for word or null
     */
    public String[] getPhones
    (String word, String partOfSpeech, boolean useLTS) {
        String[] phones = null;
        phones = getPhones(addenda, word, partOfSpeech);
        if (phones == null) {
            phones = getPhones(compiled, word, partOfSpeech);
        }
        if (useLTS) {
            if (phones == null && letterToSound != null) {
                phones = letterToSound.getPhones(word, partOfSpeech);
            }
        }
        if (phones != null) {
            String[] copy = new String[phones.length];
            System.arraycopy(phones, 0, copy, 0, phones.length);
            return copy;
        } else return null;

    }

    /**
     * Gets a phone list for a word from a given lexicon.  If a phone
     * list cannot be found, returns <code>null</code>.  The format is
     * lexicon dependent.  If the part of speech does not matter, pass
     * in <code>null</code>.
     *
     * @param lexicon      the lexicon
     * @param word         the word to find
     * @param partOfSpeech the part of speech
     * @return the list of phones for word or <code>null</code>
     */
    protected String[] getPhones(Map lexicon,
                                 String word,
                                 String partOfSpeech) {
        String[] phones;
        partOfSpeech = fixPartOfSpeech(partOfSpeech);
        phones = getPhones(lexicon, word + partOfSpeech);
        for (int i = 0;
             (i < partsOfSpeech.size()) && (phones == null);
             i++) {
            if (!partOfSpeech.equals(partsOfSpeech.get(i))) {
                phones = getPhones(lexicon,
                        word + partsOfSpeech.get(i));
            }
        }
        return phones;
    }

    /**
     * Gets a phone list for a word from a given lexicon.  If a phone
     * list cannot be found, returns <code>null</code>.
     *
     * @param lexicon             the lexicon
     * @param wordAndPartOfSpeech word and part of speech concatenated
     *                            together
     * @return the list of phones for word or <code>null</code>
     */
    protected String[] getPhones(Map lexicon,
                                 String wordAndPartOfSpeech) {
        Object value = lexicon.get(wordAndPartOfSpeech);
        if (value instanceof String[]) {
            return (String[]) value;
        } else if (value instanceof String) {
            String[] phoneArray;
            phoneArray = getPhones((String) value);
            if (tokenizeOnLookup) {
                lexicon.put(wordAndPartOfSpeech, phoneArray);
            }
            return phoneArray;
        } else {
            return null;
        }
    }

    /**
     * Turns the phone <code>String</code> into a <code>String[]</code>,
     * using " " as the delimiter.
     *
     * @param phones the phones
     * @return the phones split into an array
     */
    protected String[] getPhones(String phones) {
        ArrayList phoneList = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(phones, " ");
        while (tokenizer.hasMoreTokens()) {
            phoneList.add(tokenizer.nextToken());
        }
        return (String[]) phoneList.toArray(new String[0]);
    }

    /**
     * Adds a word to the addenda.
     *
     * @param word         the word to find
     * @param partOfSpeech the part of speech
     * @param phones       the phones for the word
     */
    public void addAddendum(String word,
                            String partOfSpeech,
                            String[] phones) {
        String pos = fixPartOfSpeech(partOfSpeech);
        if (!partsOfSpeech.contains(pos)) {
            partsOfSpeech.add(pos);
        }
        addenda.put(word + pos, phones);
    }

    /**
     * Removes a word from the addenda.
     *
     * @param word         the word to remove
     * @param partOfSpeech the part of speech
     */
    public void removeAddendum(String word, String partOfSpeech) {
        addenda.remove(word + fixPartOfSpeech(partOfSpeech));
    }

    /**
     * Outputs a string to a data output stream.
     *
     * @param dos the data output stream
     * @param s   the string to output
     * @throws IOException if errors occur during writing
     */
    private void outString(DataOutputStream dos, String s)
            throws IOException {
        dos.writeByte((byte) s.length());
        for (int i = 0; i < s.length(); i++) {
            dos.writeChar(s.charAt(i));
        }
    }

    /**
     * Inputs a string from a DataInputStream.  This method is not re-entrant.
     *
     * @param dis the data input stream
     * @return the string
     * @throws IOException if errors occur during reading
     */
    private String getString(DataInputStream dis) throws IOException {
        int size = dis.readByte();
        for (int i = 0; i < size; i++) {
            charBuffer[i] = dis.readChar();
        }
        return new String(charBuffer, 0, size);
    }

    /**
     * Inputs a string from a DataInputStream.  This method is not re-entrant.
     *
     * @param bb the input byte buffer
     * @return the string
     * @throws IOException if errors occur during reading
     */
    private String getString(ByteBuffer bb) throws IOException {
        int size = bb.get();
        for (int i = 0; i < size; i++) {
            charBuffer[i] = bb.getChar();
        }
        return new String(charBuffer, 0, size);
    }

    /**
     * Dumps a binary form of the database.  This method is not thread-safe.
     * <p/>
     * <p>Binary format is:
     * <pre>
     * MAGIC
     * VERSION
     * (int) numPhonemes
     * (String) phoneme0
     * (String) phoneme1
     * (String) phonemeN
     * (int) numEntries
     * (String) nameWithPOS
     * (byte) numPhonemes
     * phoneme index 1
     * phoneme index 2
     * phoneme index n
     * </pre>
     * <p/>
     * <p>Strings are formatted as: <code>(byte) len char0 char1 charN</code>
     * <p/>
     * <p>Limits: Strings: 128 chars
     * <p>Limits: Strings: 128 phonemes per word
     *
     * @param lexicon the lexicon to dump
     * @param path    the path to dump the file to
     */
    private void dumpBinaryLexicon(Map lexicon, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            DataOutputStream dos = new DataOutputStream(new
                    BufferedOutputStream(fos));
            List phonemeList = findPhonemes(lexicon);

            dos.writeInt(MAGIC);
            dos.writeInt(VERSION);
            dos.writeInt(phonemeList.size());

            for (int i = 0; i < phonemeList.size(); i++) {
                outString(dos, (String) phonemeList.get(i));
            }

            dos.writeInt(lexicon.keySet().size());
            for (Iterator i = lexicon.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                outString(dos, key);
                String[] phonemes = getPhones(lexicon, key);
                dos.writeByte((byte) phonemes.length);
                for (int index = 0; index < phonemes.length; index++) {
                    int phonemeIndex = phonemeList.indexOf(phonemes[index]);
                    if (phonemeIndex == -1) {
                        throw new Error("Can't find phoneme index");
                    }
                    dos.writeByte((byte) phonemeIndex);
                }
            }
            dos.close();
        } catch (FileNotFoundException fe) {
            throw new Error("Can't dump binary database " +
                    fe.getMessage());
        } catch (IOException ioe) {
            throw new Error("Can't write binary database " +
                    ioe.getMessage());
        }
    }

    /**
     * Loads the binary lexicon from the given InputStream.
     * This method is not thread safe.
     *
     * @param is            the InputStream to load the database from
     * @param estimatedSize estimate of how large the database is
     * @return a <code>Map</code> containing the lexicon
     * @throws IOException if an IO error occurs
     */
    private Map loadMappedBinaryLexicon(FileInputStream is, int estimatedSize)
            throws IOException {
        FileChannel fc = is.getChannel();

        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY,
                0, (int) fc.size());
        bb.load();
        int size = 0;
        int numEntries = 0;
        List phonemeList = new ArrayList();

        // we get better performance for some reason if we
        // just ignore estimated size
        //
        // Map lexicon = new HashMap();
        Map lexicon = new LinkedHashMap(estimatedSize * 4 / 3);

        if (bb.getInt() != MAGIC) {
            throw new Error("bad magic number in lexicon");
        }

        if (bb.getInt() != VERSION) {
            throw new Error("bad version number in lexicon");
        }

        size = bb.getInt();
        for (int i = 0; i < size; i++) {
            String phoneme = getString(bb);
            phonemeList.add(phoneme);
        }
        numEntries = bb.getInt();

        for (int i = 0; i < numEntries; i++) {
            String wordAndPos = getString(bb);
            String pos = Character.toString(
                    wordAndPos.charAt(wordAndPos.length() - 1));
            if (!partsOfSpeech.contains(pos)) {
                partsOfSpeech.add(pos);
            }

            int numPhonemes = bb.get();
            String[] phonemes = new String[numPhonemes];

            for (int j = 0; j < numPhonemes; j++) {
                phonemes[j] = (String) phonemeList.get(bb.get());
            }
            lexicon.put(wordAndPos, phonemes);
        }
        fc.close();
        return lexicon;
    }

    /**
     * Loads the binary lexicon from the given InputStream.
     * This method is not thread safe.
     *
     * @param is            the InputStream to load the database from
     * @param estimatedSize estimate of how large the database is
     * @return a <code>Map</code> containing the lexicon
     * @throws IOException if an IO error occurs
     */
    private Map loadBinaryLexicon(InputStream is, int estimatedSize)
            throws IOException {
        DataInputStream dis = new DataInputStream(new
                BufferedInputStream(is));
        int size = 0;
        int numEntries = 0;
        List phonemeList = new ArrayList();

        // we get better performance for some reason if we
        // just ignore estimated size
        //
        Map lexicon = new LinkedHashMap();

        int magic;
        try {
            //####### THIS is busted ##############
            // No reason for an exception here, but we get one anyhow.
            if (dis.readInt() != MAGIC) {
                throw new Error("bad magic number in lexicon");
            }
        } catch (IOException e) {
            throw e;
        }

        if (dis.readInt() != VERSION) {
            throw new Error("bad version number in lexicon");
        }

        size = dis.readInt();
        for (int i = 0; i < size; i++) {
            String phoneme = getString(dis);
            phonemeList.add(phoneme);
        }
        numEntries = dis.readInt();

        for (int i = 0; i < numEntries; i++) {
            String wordAndPos = getString(dis);
            String pos = Character.toString(
                    wordAndPos.charAt(wordAndPos.length() - 1));
            if (!partsOfSpeech.contains(pos)) {
                partsOfSpeech.add(pos);
            }

            int numPhonemes = dis.readByte();
            String[] phonemes = new String[numPhonemes];

            for (int j = 0; j < numPhonemes; j++) {
                phonemes[j] = (String) phonemeList.get(dis.readByte());
            }
            lexicon.put(wordAndPos, phonemes);
        }
        dis.close();
        return lexicon;
    }

    /**
     * Dumps this lexicon (just the compiled form). Lexicon will be
     * dumped to two binary files PATH_compiled.bin and
     * PATH_addenda.bin
     *
     * @param path the root path to dump it to
     */
    public void dumpBinary(String path) {
        String compiledPath = path + "_compiled.bin";
        String addendaPath = path + "_addenda.bin";

        dumpBinaryLexicon(compiled, compiledPath);
        dumpBinaryLexicon(addenda, addendaPath);
    }

    /**
     * Returns a list of the unique phonemes in the lexicon.
     *
     * @param lexicon the lexicon of interest
     * @return list the unique set of phonemes
     */
    private List findPhonemes(Map lexicon) {
        List phonemeList = new ArrayList();
        for (Iterator i = lexicon.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            String[] phonemes = getPhones(lexicon, key);
            for (int index = 0; index < phonemes.length; index++) {
                if (!phonemeList.contains(phonemes[index])) {
                    phonemeList.add(phonemes[index]);
                }
            }
        }
        return phonemeList;
    }

    /**
     * Tests to see if this lexicon is identical to the other for
     * debugging purposes.
     *
     * @param other the other lexicon to compare to
     * @return true if lexicons are identical
     */
    public boolean compare(LexiconImpl other) {
        return compare(addenda, other.addenda) &&
                compare(compiled, other.compiled);
    }

    /**
     * Determines if the two lexicons are identical for debugging purposes.
     *
     * @param lex   this lex
     * @param other the other lexicon to chd
     * @return true if they are identical
     */
    private boolean compare(Map lex, Map other) {
        for (Iterator i = lex.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            String[] thisPhonemes = getPhones(lex, key);
            String[] otherPhonemes = getPhones(other, key);
            if (thisPhonemes == null) {
                System.out.println(key + " not found in this.");
                return false;
            } else if (otherPhonemes == null) {
                System.out.println(key + " not found in other.");
                return false;
            } else if (thisPhonemes.length == otherPhonemes.length) {
                for (int j = 0; j < thisPhonemes.length; j++) {
                    if (!thisPhonemes[j].equals(otherPhonemes[j])) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}

/**
 * Portions Copyright 2003 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts.diphone;

import com.sun.speech.freetts.relp.Sample;
import com.sun.speech.freetts.relp.SampleInfo;
import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Represents and manages the unit data for all diphones.  The diphone
 * data set is stored in a set of data files. These data are loaded by this
 * class into internal data structures before diphone synthesis can
 * occur.
 * <p/>
 * The diphone data set is one of the largest sets of data that
 * needs to be loaded by the synthesizer and therefore can add to the
 * overall startup time for any system using this database.  For
 * certain applications, the startup time is a critical spec that
 * needs to be optimized, while for other applications, startup time
 * is inconsequential.  This class provides settings (via system
 * properties) that control how the database is to be loaded so that
 * applications can tune for quick startup or optimal run time.
 * <p/>
 * This class serves also as a testbed for comparing performance of
 * the traditional java binary I/O and the new io ( <code>java.nio </code>)
 * package.
 * <p/>
 * <p> A diphone database can be loaded from a text data file, or a
 * binary datafile.  The binary version loads significantly faster
 * than the text version. Additionally, a binary index can be
 * generated and used to reduce overall memory footprint.
 * <p/>
 * <p/>
 * A DiphoneUnitDatabase contains an array of frames, and an aray of
 * residuals. The frames are the samples of the wave, and the
 * residuals are for linear predictive coding use. This is called
 * "cst_sts" (a struct) in flite.
 * <p/>
 * Note that if 'com.sun.speech.freetts.useNewIO' is set to true and
 * the input type is binary, than the JDK1.4+ new IO api is used to
 * load the database.
 * <p/>
 * The system property
 * <pre>
 * 	com.sun.speech.freetts.diphone.UnitDatabase.cacheType
 * </pre>
 * <p/>
 * can be set to one of:
 * <p/>
 * <ul>
 * <li> preload: database is loaded at startup
 * <li> demand: database is loaded on demand
 * <li> hard: database is loaded on demand but cached
 * <li> soft: database is loaded on demand but cached with soft references
 * </ul>
 * <p/>
 * This <code> cacheType </code> setting controls how the database is
 * loaded. The default is to 'preload' the database. This setting
 * gives best runtime performance but with longer initial startup
 * cost.
 */
public class DiphoneUnitDatabase {

    private final static int MAGIC = 0xFEEDFACE;
    private final static int INDEX_MAGIC = 0xFACADE;
    private final static int VERSION = 1;
    private final static int MAX_DB_SIZE = 4 * 1024 * 1024;
    private String name;
    private int sampleRate;
    private int numChannels;
    private int residualFold = 1;
    private float lpcMin;
    private float lpcRange;
    private int lineCount = 0;
    private Diphone defaultDiphone;
    private Map diphoneMap = null;
    private Map diphoneIndex;
    private SampleInfo sampleInfo;
    private boolean useNewIO =
            Utilities.getProperty("com.sun.speech.freetts.useNewIO",
                    "true").equals("true");
    // cache can be 'preload' 'none', 'soft' or 'hard'
    private String cacheType =
            Utilities.getProperty(
                    "com.sun.speech.freetts.diphone.UnitDatabase.cacheType",
                    "preload");
    private boolean useIndexing = !cacheType.equals("preload");
    private boolean useCache = !cacheType.equals("demand");
    private boolean useSoftCache = cacheType.equals("soft");
    private String indexName = null;
    private MappedByteBuffer mbb = null;
    private int defaultIndex = -1;

    /**
     * Creates the DiphoneUnitDatabase from the given input stream.
     *
     * @param url      the location of the database
     * @param isBinary if <code>true</code> the database is in
     *                 binary format; otherwise it is in text format
     * @throws IOException if there is trouble opening the DB
     */
    public DiphoneUnitDatabase(URL url, boolean isBinary) throws IOException {

        if (!useIndexing || useCache) {
            diphoneMap = new LinkedHashMap();
        }
        InputStream is = Utilities.getInputStream(url);

        indexName = getIndexName(url.toString());

        if (isBinary) {
            loadBinary(is);
        } else {
            loadText(is);
        }
        is.close();
        sampleInfo = new SampleInfo(sampleRate, numChannels,
                residualFold, lpcMin, lpcRange, 0.0f);
    }

    /**
     * Manipulates a DiphoneUnitDatabase. This program is typically
     * used to generate the binary form (with index) of the
     * DiphoneUnitDatabase from the text form. Additionally, this program
     * can be used to compare two databases to see if they are
     * identical (used for testing).
     * <p/>
     * <p/>
     * <b> Usage </b>
     * <p/>
     * <code> java com.sun.speech.freetts.diphone.DiphoneUnitDatabase
     * [options]</code>
     * <p/>
     * <b> Options </b>
     * <p/>
     * <ul>
     * <li> <code> -src path </code> provides a directory
     * path to the source text for the database
     * <li> <code> -dest path </code> provides a directory
     * for where to place the resulting binaries
     * <li> <code> -generate_binary [filename] </code>
     * reads in the text
     * version of the database and generates the binary
     * version of the database.
     * <li> <code> -compare </code>  Loads the text and
     * binary versions of the database and compares them to
     * see if they are equivalent.
     * <li> <code> -showTimes </code> shows timings for any
     * loading, comparing or dumping operation
     * </ul>
     */
    public static void main(String[] args) {
        boolean showTimes = false;
        String srcPath = ".";
        String destPath = ".";

        try {
            if (args.length > 0) {
                BulkTimer timer = BulkTimer.LOAD;
                timer.start();
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-src")) {
                        srcPath = args[++i];
                    } else if (args[i].equals("-dest")) {
                        destPath = args[++i];
                    } else if (args[i].equals("-generate_binary")) {
                        String name = "diphone_units.txt";
                        if (i + 1 < args.length) {
                            String nameArg = args[++i];
                            if (!nameArg.startsWith("-")) {
                                name = nameArg;
                            }
                        }

                        int suffixPos = name.lastIndexOf(".txt");

                        String binaryName = "diphone_units.bin";
                        if (suffixPos != -1) {
                            binaryName = name.substring(0, suffixPos) + ".bin";
                        }

                        String indexName = "diphone_units.idx";

                        if (suffixPos != -1) {
                            indexName = name.substring(0, suffixPos) + ".idx";
                        }

                        System.out.println("Loading " + name);
                        timer.start("load_text");
                        DiphoneUnitDatabase udb = new DiphoneUnitDatabase(
                                new URL("file:"
                                        + srcPath + "/" + name), false);
                        timer.stop("load_text");

                        System.out.println("Dumping " + binaryName);
                        timer.start("dump_binary");
                        udb.dumpBinary(destPath + "/" + binaryName);
                        timer.stop("dump_binary");

                        timer.start("load_binary");
                        DiphoneUnitDatabase budb =
                                new DiphoneUnitDatabase(
                                        new URL("file:"
                                                + destPath + "/" + binaryName),
                                        true);
                        timer.stop("load_binary");

                        System.out.println("Dumping " + indexName);
                        timer.start("dump index");
                        budb.dumpBinaryIndex(destPath + "/" + indexName);
                        timer.stop("dump index");
                    } else if (args[i].equals("-compare")) {

                        timer.start("load_text");
                        DiphoneUnitDatabase udb = new DiphoneUnitDatabase(
                                new URL("file:./diphone_units.txt"), false);
                        timer.stop("load_text");

                        timer.start("load_binary");
                        DiphoneUnitDatabase budb =
                                new DiphoneUnitDatabase(
                                        new URL("file:./diphone_units.bin"), true);
                        timer.stop("load_binary");

                        timer.start("compare");
                        if (udb.compare(budb)) {
                            System.out.println("other compare ok");
                        } else {
                            System.out.println("other compare different");
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
                    timer.show("DiphoneUnitDatabase");
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
     * Return the information about the sample data
     * for this database.
     *
     * @return the sample info
     */

    SampleInfo getSampleInfo() {
        return sampleInfo;
    }

    /**
     * Returns the index name from the databaseName.
     *
     * @param databaseName the database name
     * @return the index name or null if the database is not
     * a binary database.
     * <p/>
     * [[[ TODO the index should probably be incorporated into the
     * binary database ]]]
     */
    private String getIndexName(String databaseName) {
        String indexName = null;
        if (databaseName.lastIndexOf(".") != -1) {
            indexName = databaseName.substring(0,
                    databaseName.lastIndexOf(".")) + "_idx.idx";
        }
        return indexName;
    }

    /**
     * Loads the database from the given input stream.
     *
     * @param is the input stream
     */
    private void loadText(InputStream is) {
        BufferedReader reader;
        String line;

        if (is == null) {
            throw new Error("Can't load diphone db file.");
        }

        reader = new BufferedReader(new InputStreamReader(is));
        try {
            line = reader.readLine();
            lineCount++;
            while (line != null) {
                if (!line.startsWith("***")) {
                    parseAndAdd(line, reader);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new Error(e.getMessage() + " at line " + lineCount);
        } finally {
        }
    }

    /**
     * Parses and process the given line. Used to process the text
     * form of the database.
     *
     * @param line   the line to process
     * @param reader the source for the lines
     */
    private void parseAndAdd(String line, BufferedReader reader) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(line, " ");
            String tag = tokenizer.nextToken();
            if (tag.equals("NAME")) {
                name = tokenizer.nextToken();
            } else if (tag.equals("SAMPLE_RATE")) {
                sampleRate = Integer.parseInt(tokenizer.nextToken());
            } else if (tag.equals("NUM_CHANNELS")) {
                numChannels = Integer.parseInt(tokenizer.nextToken());
            } else if (tag.equals("LPC_MIN")) {
                lpcMin = Float.parseFloat(tokenizer.nextToken());
            } else if (tag.equals("COEFF_MIN")) {
                lpcMin = Float.parseFloat(tokenizer.nextToken());
            } else if (tag.equals("COEFF_RANGE")) {
                lpcRange = Float.parseFloat(tokenizer.nextToken());
            } else if (tag.equals("LPC_RANGE")) {
                lpcRange = Float.parseFloat(tokenizer.nextToken());
            } else if (tag.equals("DIPHONE")) {
                String name = tokenizer.nextToken();
                int start = Integer.parseInt(tokenizer.nextToken());
                int mid = Integer.parseInt(tokenizer.nextToken());
                int end = Integer.parseInt(tokenizer.nextToken());
                int numSamples = (end - start);
                int midPoint = mid - start;

                if (numChannels <= 0) {
                    throw new Error("Bad number of channels " + numChannels);
                }

                if (numSamples <= 0) {
                    throw new Error("Bad number of samples " + numSamples);
                }

                Sample[] samples = new Sample[numSamples];

                for (int i = 0; i < samples.length; i++) {
                    samples[i] = new Sample(reader, numChannels);
                    assert samples[i].getFrameData().length == numChannels;
                }
                Diphone diphone = new Diphone(name, samples, midPoint);
                add(diphone);
            } else {
                throw new Error("Unsupported tag " + tag);
            }
        } catch (NoSuchElementException nse) {
            throw new Error("Error parsing db " + nse.getMessage());
        } catch (NumberFormatException nfe) {
            throw new Error("Error parsing numbers in db " + nfe.getMessage());
        }
    }

    /**
     * Adds the given diphone to the DB. Diphones are kept in a map so
     * they can be accessed by name.
     *
     * @param diphone the diphone to add.
     */
    private void add(Diphone diphone) {
        diphoneMap.put(diphone.getName(), diphone);
        if (defaultDiphone == null) {
            defaultDiphone = diphone;
        }
    }

    /**
     * Looks up the diphone with the given name.
     *
     * @param unitName the name of the diphone to look for
     * @return the diphone or the defaultDiphone if not found.
     */
    public Diphone getUnit(String unitName) {
        Diphone diphone = null;

        if (useIndexing) {
            diphone = getFromCache(unitName);
            if (diphone == null) {
                int index = getIndex(unitName);
                if (index != -1) {
                    mbb.position(index);
                    try {
                        diphone = Diphone.loadBinary(mbb);
                        if (diphone != null) {
                            putIntoCache(unitName, diphone);
                        }
                    } catch (IOException ioe) {
                        System.err.println("Can't load diphone " +
                                unitName);
                        diphone = null;
                    }
                }
            }
        } else {
            diphone = (Diphone) diphoneMap.get(unitName);
        }

        if (diphone == null) {
            System.err.println("Can't find diphone " + unitName);
            diphone = defaultDiphone;
        }

        return diphone;
    }

    /**
     * Gets the named diphone from the cache. If we are using soft
     * caching, the reference may be a soft/weak reference so check to
     * see if the reference is still valid, if so return it; otherwise
     * invalidate it. Note that we have not had good success with weak
     * caches so far. The goal is to reduce the minimum required
     * memory footprint as far as possible while not compromising
     * performance. In small memory systems, the weak cache would
     * likely be reclaimed, giving us lower performance but with the
     * ability to still be able to run. In reality, the soft caches
     * did not help much. They just did not work correctly.
     * [[[ TODO: test weak/soft cache behavior with new versions of
     * the runtime to see if their behavior has improved ]]]
     *
     * @param name the name of the diphone
     * @return the diphone or <code> null </code>  if not in the cache
     */
    private Diphone getFromCache(String name) {
        if (diphoneMap == null) {
            return null;
        }
        Diphone diphone = null;

        if (useSoftCache) {
            Reference ref = (Reference) diphoneMap.get(name);
            if (ref != null) {
                diphone = (Diphone) ref.get();
                if (diphone == null) {
                    diphoneMap.remove(name);
                } else {
                }
            }
        } else {
            diphone = (Diphone) diphoneMap.get(name);
        }
        return diphone;
    }

    /**
     * Puts the diphone in the cache.
     *
     * @param diphoneName the name of the diphone
     * @param diphone     the diphone to put in the cache
     */
    private void putIntoCache(String diphoneName, Diphone diphone) {
        if (diphoneMap == null) {
            return;
        }
        if (useSoftCache) {
            diphoneMap.put(diphoneName, new WeakReference(diphone));
        } else {
            diphoneMap.put(diphoneName, diphone);
        }
    }

    /**
     * Dumps the soft ref cache.
     */
    private void dumpCacheSize() {
        int empty = 0;
        int full = 0;
        System.out.println("Entries: " + diphoneMap.size());
        for (Iterator i = diphoneMap.values().iterator(); i.hasNext(); ) {
            Reference ref = (Reference) i.next();
            if (ref.get() == null) {
                empty++;
            } else {
                full++;
            }
        }
        System.out.println("   empty: " + empty);
        System.out.println("    full: " + full);
    }

    /**
     * Returns the name of this DiphoneUnitDatabase.
     */
    public String getName() {
        return name;
    }

    /**
     * Dumps the diphone database.
     */
    public void dump() {
        System.out.println("Name        " + name);
        System.out.println("SampleRate  " + sampleRate);
        System.out.println("NumChannels " + numChannels);
        System.out.println("lpcMin      " + lpcMin);
        System.out.println("lpcRange    " + lpcRange);

        for (Iterator i = diphoneMap.values().iterator(); i.hasNext(); ) {
            Diphone diphone = (Diphone) i.next();
            diphone.dump();
        }
    }

    /**
     * Dumps a binary form of the database.
     *
     * @param path the path to dump the file to
     */
    public void dumpBinary(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            DataOutputStream os = new DataOutputStream(fos);
            int written;

            os.writeInt(MAGIC);
            os.writeInt(VERSION);
            os.writeInt(sampleRate);
            os.writeInt(numChannels);
            os.writeFloat(lpcMin);
            os.writeFloat(lpcRange);
            os.writeInt(diphoneMap.size());

            for (Iterator i = diphoneMap.values().iterator(); i.hasNext(); ) {
                Diphone diphone = (Diphone) i.next();
                diphone.dumpBinary(os);
            }
            os.flush();
            fos.close();

        } catch (FileNotFoundException fe) {
            throw new Error("Can't dump binary database " +
                    fe.getMessage());
        } catch (IOException ioe) {
            throw new Error("Can't write binary database " +
                    ioe.getMessage());
        }
    }

    /**
     * Dumps a binary index. The database index is used if our
     * cacheType is not set to 'preload' and we are loading a binary
     * database. The index is a simple mapping of diphone names (the
     * key) to the file position in the database. In situations where
     * the entire database is not preloaded, this index can be loaded
     * and used to provide quicker startup (since only the index need
     * be loaded at startup) and quick access to the diphone data.
     *
     * @param path the path to dump the file to
     */
    void dumpBinaryIndex(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(INDEX_MAGIC);
            dos.writeInt(diphoneIndex.keySet().size());

            for (Iterator i = diphoneIndex.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                int pos = ((Integer) diphoneIndex.get(key)).intValue();
                dos.writeUTF(key);
                dos.writeInt(pos);
            }
            dos.close();

        } catch (FileNotFoundException fe) {
            throw new Error("Can't dump binary index " +
                    fe.getMessage());
        } catch (IOException ioe) {
            throw new Error("Can't write binary index " +
                    ioe.getMessage());
        }
    }

    /**
     * Loads a binary index.
     *
     * @param url the location  of the binary index file
     */
    private void loadBinaryIndex(URL url) {

        diphoneIndex = new HashMap();

        try {
            InputStream is = Utilities.getInputStream(url);
            DataInputStream dis = new DataInputStream(is);

            if (dis.readInt() != INDEX_MAGIC) {
                throw new Error("Bad index file format");
            }

            int size = dis.readInt();

            for (int i = 0; i < size; i++) {
                String diphoneName = dis.readUTF();
                int pos = dis.readInt();
                diphoneIndex.put(diphoneName, new Integer(pos));
            }
            dis.close();

        } catch (FileNotFoundException fe) {
            throw new Error("Can't load binary index " +
                    fe.getMessage());
        } catch (IOException ioe) {
            throw new Error("Can't read binary index " +
                    ioe.getMessage());
        }
    }

    /**
     * Gets the index for the given diphone.
     *
     * @param diphone the name of the diphone
     * @return the index into the database for the diphone
     */
    private int getIndex(String diphone) {
        Integer index = (Integer) diphoneIndex.get(diphone);
        if (index != null) {
            int idx = index.intValue();
            if (defaultIndex == -1) {
                defaultIndex = idx;
            }
            return idx;
        } else {
            System.out.println("Can't find index entry for " + diphone);
            return defaultIndex;
        }
    }

    /**
     * Loads a binary file from the input stream.
     * <p/>
     * Note that we currently have four! methods of loading up the
     * database. We were interested in the performance characteristics
     * of the various methods of loading the database so we coded it
     * all up.
     *
     * @param is the input stream to read the database
     *           from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadBinary(InputStream is) throws IOException {
        // we get better performance if we can map the file in
        // 1.0 seconds vs. 1.75 seconds, but we can't
        // always guarantee that we can do that.
        if (useNewIO && is instanceof FileInputStream) {
            FileInputStream fis = (FileInputStream) is;
            if (useIndexing) {
                loadBinaryIndex(new URL(indexName));
                mapDatabase(fis);
            } else {
                loadMappedBinary(fis);
            }
        } else {
            DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(is));
            loadBinary(dis);
        }
    }

    /**
     * Loads the binary data from the given input stream.
     *
     * @param dis the data input stream.
     */
    private void loadBinary(DataInputStream dis) throws IOException {
        int size;
        if (dis.readInt() != MAGIC) {
            throw new Error("Bad magic in db");
        }
        if (dis.readInt() != VERSION) {
            throw new Error("Bad VERSION in db");
        }

        sampleRate = dis.readInt();
        numChannels = dis.readInt();
        lpcMin = dis.readFloat();
        lpcRange = dis.readFloat();
        size = dis.readInt();

        for (int i = 0; i < size; i++) {
            Diphone diphone = Diphone.loadBinary(dis);
            add(diphone);
        }
    }

    /**
     * Loads the database from the given FileInputStream.
     *
     * @param is the InputStream to load the database from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadMappedBinary(FileInputStream is) throws IOException {
        FileChannel fc = is.getChannel();

        MappedByteBuffer bb =
                fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
        bb.load();
        loadDatabase(bb);
        is.close();
    }

    /**
     * Maps the database from the given FileInputStream.
     *
     * @param is the InputStream to load the database from
     * @throws IOException if there is trouble opening the DB
     */
    private void mapDatabase(FileInputStream is) throws IOException {
        FileChannel fc = is.getChannel();
        mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
        mbb.load();
        loadDatabaseHeader(mbb);
    }

    /**
     * Loads the database header from the given byte buffer.
     *
     * @param bb the byte buffer to load the db from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadDatabaseHeader(ByteBuffer bb) throws IOException {
        if (bb.getInt() != MAGIC) {
            throw new Error("Bad magic in db");
        }
        if (bb.getInt() != VERSION) {
            throw new Error("Bad VERSION in db");
        }

        sampleRate = bb.getInt();
        numChannels = bb.getInt();
        lpcMin = bb.getFloat();
        lpcRange = bb.getFloat();
    }

    /**
     * Loads the database from the given byte buffer.
     *
     * @param bb the byte buffer to load the db from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadDatabase(ByteBuffer bb) throws IOException {
        int size;
        loadDatabaseHeader(bb);
        size = bb.getInt();

        diphoneIndex = new HashMap();
        for (int i = 0; i < size; i++) {
            int pos = bb.position();
            Diphone diphone = Diphone.loadBinary(bb);
            add(diphone);
            diphoneIndex.put(diphone.getName(), new Integer(pos));
        }
    }

    /**
     * Compares this database to another. This is used for testing.
     * With this method we can load up two databases (one perhaps from
     * a text source and one from a binary source) and compare to
     * verify that the dbs are identical
     *
     * @param other the other database
     * @return <code>true</code>  if the DBs are identical;
     * otherwise <code>false</code>
     */
    public boolean compare(DiphoneUnitDatabase other) {
        if (sampleRate != other.sampleRate) {
            return false;
        }

        if (numChannels != other.numChannels) {
            return false;
        }

        if (lpcMin != other.lpcMin) {
            return false;
        }

        if (lpcRange != other.lpcRange) {
            return false;
        }

        for (Iterator i = diphoneMap.values().iterator(); i.hasNext(); ) {
            Diphone diphone = (Diphone) i.next();
            Diphone otherDiphone = other.getUnit(diphone.getName());
            if (!diphone.compare(otherDiphone)) {
                System.out.println("Diphones differ:");
                System.out.println("THis:");
                diphone.dump();
                System.out.println("Other:");
                otherDiphone.dump();
                return false;
            }
        }
        return true;
    }
}

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
package com.sun.speech.freetts.clunits;

import com.sun.speech.freetts.cart.CART;
import com.sun.speech.freetts.cart.CARTImpl;
import com.sun.speech.freetts.relp.SampleInfo;
import com.sun.speech.freetts.relp.SampleSet;
import com.sun.speech.freetts.util.BulkTimer;
import com.sun.speech.freetts.util.Utilities;

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
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * Provides support for the cluster unit database. The use of the
 * cluster unit database is confined to this clunits package. This
 * class provides a main program that can be used to convert from a
 * text version of the database to a binary version of the database.
 * <p/>
 * The ClusterUnitDataBase can be loaded from a text or a binary
 * source. The binary form of the database loads much faster and
 * therefore is generally used in a deployed system.
 */
public class ClusterUnitDatabase {
    final static int CLUNIT_NONE = 65535;
    private final static int MAGIC = 0xf0cacc1a;
    private final static int VERSION = 0x1000;
    private DatabaseClusterUnit[] units;
    private UnitType[] unitTypes;
    private SampleSet sts;
    private SampleSet mcep;
    private int continuityWeight;
    private int optimalCoupling;
    private int extendSelections;
    private int joinMethod;
    private int[] joinWeights;
    private int joinWeightShift;
    private Map cartMap = new HashMap();
    private CART defaultCart = null;
    private transient List unitList;
    private transient int lineCount;
    private transient List unitTypesList;


    /**
     * Creates the UnitDatabase from the given input stream.
     *
     * @param is       the input stream to read the database from
     * @param isBinary the input stream is a binary stream
     * @throws IOException if there is trouble opening the DB
     */
    ClusterUnitDatabase(URL url, boolean isBinary) throws IOException {
        BulkTimer.LOAD.start("ClusterUnitDatabase");
        InputStream is = Utilities.getInputStream(url);
        if (isBinary) {
            loadBinary(is);
        } else {
            loadText(is);
        }
        is.close();
        BulkTimer.LOAD.stop("ClusterUnitDatabase");
    }

    /**
     * Manipulates a ClusterUnitDatabase.
     * <p/>
     * <p/>
     * <b> Usage </b>
     * <p/>
     * <code> java com.sun.speech.freetts.clunits.ClusterUnitDatabase
     * [options]</code>
     * <p/>
     * <b> Options </b>
     * <p/>
     * <ul>
     * <li> <code> -src path </code> provides a directory
     * path to the source text for the database
     * <li> <code> -dest path </code> provides a directory
     * for where to place the resulting binaries
     * <li> <code> -generate_binary [filename]</code> reads
     * in the text version of the database and generates
     * the binary version of the database.
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
                BulkTimer timer = new BulkTimer();
                timer.start();
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-src")) {
                        srcPath = args[++i];
                    } else if (args[i].equals("-dest")) {
                        destPath = args[++i];
                    } else if (args[i].equals("-generate_binary")) {
                        String name = "clunits.txt";
                        if (i + 1 < args.length) {
                            String nameArg = args[++i];
                            if (!nameArg.startsWith("-")) {
                                name = nameArg;
                            }
                        }

                        int suffixPos = name.lastIndexOf(".txt");

                        String binaryName = "clunits.bin";
                        if (suffixPos != -1) {
                            binaryName = name.substring(0, suffixPos) + ".bin";
                        }

                        System.out.println("Loading " + name);
                        timer.start("load_text");
                        ClusterUnitDatabase udb = new
                                ClusterUnitDatabase(
                                new URL("file:" + srcPath + "/" + name),
                                false);
                        timer.stop("load_text");

                        System.out.println("Dumping " + binaryName);
                        timer.start("dump_binary");
                        udb.dumpBinary(destPath + "/" + binaryName);
                        timer.stop("dump_binary");

                    } else if (args[i].equals("-compare")) {

                        timer.start("load_text");
                        ClusterUnitDatabase udb = new
                                ClusterUnitDatabase(
                                new URL("file:./cmu_time_awb.txt"), false);
                        timer.stop("load_text");

                        timer.start("load_binary");
                        ClusterUnitDatabase budb =
                                new ClusterUnitDatabase(
                                        new URL("file:./cmu_time_awb.bin"), true);
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
                    timer.show("ClusterUnitDatabase");
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
     * Retrieves the begininning sample index for the
     * given entry.
     *
     * @param unitEntry the entry of interest
     * @return the begininning sample index
     */
    int getStart(int unitEntry) {
        return units[unitEntry].start;
    }

    /**
     * Retrieves the ending sample index for the
     * given entry.
     *
     * @param unitEntry the entry of interest
     * @return the ending sample index
     */
    int getEnd(int unitEntry) {
        return units[unitEntry].end;
    }

    /**
     * Retrieves the phone for the given entry
     *
     * @param unitEntry the entry of interest
     * @return the phone for the entry
     */
    int getPhone(int unitEntry) {
        return units[unitEntry].phone;
    }

    /**
     * Returns the cart of the given unit type.
     *
     * @param unitType the type of cart
     * @return the cart
     */
    CART getTree(String unitType) {
        CART cart = (CART) cartMap.get(unitType);

        if (cart == null) {
            System.err.println("ClusterUnitDatabase: can't find tree for "
                    + unitType);
            return defaultCart;    // "graceful" failrue
        }
        return cart;
    }

    /**
     * Retrieves the type index for the name given a name.
     *
     * @param name the name
     * @return the index for the name
     */
// [[[TODO: perhaps replace this with  java.util.Arrays.binarySearch]]]
    int getUnitTypeIndex(String name) {
        int start, end, mid, c;

        start = 0;
        end = unitTypes.length;

        while (start < end) {
            mid = (start + end) / 2;
            c = unitTypes[mid].getName().compareTo(name);
            if (c == 0) {
                return mid;
            } else if (c > 0) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return -1;
    }

    /**
     * Retrieves the unit index given a unit type and val.
     *
     * @param unitType the type of the unit
     * @param instance the value associated with the unit
     * @return the index.
     */
    int getUnitIndex(String unitType, int instance) {
        int i = getUnitTypeIndex(unitType);
        if (i == -1) {
            error("getUnitIndex: can't find unit type " + unitType);
            i = 0;
        }
        if (instance >= unitTypes[i].getCount()) {
            error("getUnitIndex: can't find instance "
                    + instance + " of " + unitType);
            instance = 0;
        }
        return unitTypes[i].getStart() + instance;
    }

    /**
     * Retrieves the index for the name given a name.
     *
     * @param name the name
     * @return the index for the name
     */
    int getUnitIndexName(String name) {
        int lastIndex = name.lastIndexOf('_');
        if (lastIndex == -1) {
            error("getUnitIndexName: bad unit name " + name);
            return -1;
        }
        int index = Integer.parseInt(name.substring(lastIndex + 1));
        String type = name.substring(0, lastIndex);
        return getUnitIndex(type, index);
    }

    /**
     * Retrieves the extend selections setting.
     *
     * @return the extend selections setting
     */
    int getExtendSelections() {
        return extendSelections;
    }

    /**
     * Gets the next unit.
     *
     * @return the next unit
     */
    int getNextUnit(int which) {
        return units[which].next;
    }

    /**
     * Gets the previous units.
     *
     * @param which which unit is of interest
     * @return the previous unit
     */
    int getPrevUnit(int which) {
        return units[which].prev;
    }

    /**
     * Determines if the unit types are equal.
     *
     * @param unitA the index of unit a
     * @param unitB the index of unit B
     * @return <code>true</code> if the types of units a and b are
     * equal; otherwise return <code>false</code>
     */
    boolean isUnitTypeEqual(int unitA, int unitB) {
        return units[unitA].type == units[unitB].type;
        // String nameA = units[unitA].getName();
        // String nameB = units[unitB].getName();
        // int lastUnderscore = nameA.lastIndexOf('_');
        // return nameA.regionMatches(0, nameB, 0, lastUnderscore + 1);
    }

    /**
     * Retrieves the optimal coupling setting.
     *
     * @return the optimal coupling setting
     */
    int getOptimalCoupling() {
        return optimalCoupling;
    }

    /**
     * Retrieves the continuity weight setting.
     *
     * @return the continuity weight setting
     */
    int getContinuityWeight() {
        return continuityWeight;
    }

    /**
     * Retrieves the join weights.
     *
     * @return the join weights
     */
    int[] getJoinWeights() {
        return joinWeights;
    }

    /**
     * Looks up the unit with the given name.
     *
     * @param unitName the name of the unit to look for
     * @return the unit or the defaultUnit if not found.
     */
    DatabaseClusterUnit getUnit(String unitName) {
        return null;
    }

    /**
     * Looks up the unit with the given index.
     *
     * @param index the index of the unit to look for
     * @return the unit
     */
    DatabaseClusterUnit getUnit(int which) {
        return units[which];
    }

    /**
     * Returns the name of this UnitDatabase.
     *
     * @return the name of the database
     */
    String getName() {
        return "ClusterUnitDatabase";
    }

    /**
     * Returns the sample info for this set of data.
     *
     * @return the sample info
     */
    SampleInfo getSampleInfo() {
        return sts.getSampleInfo();
    }

    /**
     * Gets the sample list.
     *
     * @return the sample list
     */
    SampleSet getSts() {
        return sts;
    }

    /**
     * Gets the Mel Ceptra list.
     *
     * @return the Mel Ceptra list
     */
    SampleSet getMcep() {
        return mcep;
    }

    /**
     * Determines if the application of the given join weights could
     * be applied  as a simple right-shift. If so return the shift
     * otherwise return 0.
     *
     * @return the amount to right shift (or zero if not possible)
     */
    int getJoinWeightShift() {
        return joinWeightShift;
    }

    /**
     * Calculates the join weight shift.
     *
     * @param joinWeights the weights to check
     * @return the amount to right shift (or zero if not possible)
     */
    private int calcJoinWeightShift(int[] joinWeights) {
        int first = joinWeights[0];
        for (int i = 1; i < joinWeights.length; i++) {
            if (joinWeights[i] != first) {
                return 0;
            }
        }

        int divisor = 65536 / first;
        if (divisor == 2) {
            return 1;
        } else if (divisor == 4) {
            return 2;
        }
        return 0;
    }

    /**
     * Loads the database from the given input stream.
     *
     * @param is the input stream
     */
    private void loadText(InputStream is) {
        BufferedReader reader;
        String line;


        unitList = new ArrayList();
        unitTypesList = new ArrayList();

        if (is == null) {
            throw new Error("Can't load cluster db file.");
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

            units = new DatabaseClusterUnit[unitList.size()];
            units = (DatabaseClusterUnit[]) unitList.toArray(units);
            unitList = null;

            unitTypes = new UnitType[unitTypesList.size()];
            unitTypes = (UnitType[]) unitTypesList.toArray(unitTypes);
            unitTypesList = null;

        } catch (IOException e) {
            throw new Error(e.getMessage() + " at line " + lineCount);
        } finally {
        }
    }

    /**
     * Parses and process the given line.
     *
     * @param line   the line to process
     * @param reader the source for the lines
     * @throws IOException if an error occurs while reading
     */
    private void parseAndAdd(String line, BufferedReader reader)
            throws IOException {
        try {
            StringTokenizer tokenizer = new StringTokenizer(line, " ");
            String tag = tokenizer.nextToken();
            if (tag.equals("CONTINUITY_WEIGHT")) {
                continuityWeight = Integer.parseInt(tokenizer.nextToken());
            } else if (tag.equals("OPTIMAL_COUPLING")) {
                optimalCoupling = Integer.parseInt(tokenizer.nextToken());
            } else if (tag.equals("EXTEND_SELECTIONS")) {
                extendSelections = Integer.parseInt(tokenizer.nextToken());
            } else if (tag.equals("JOIN_METHOD")) {
                joinMethod = Integer.parseInt(tokenizer.nextToken());
            } else if (tag.equals("JOIN_WEIGHTS")) {
                int numWeights = Integer.parseInt(tokenizer.nextToken());
                joinWeights = new int[numWeights];
                for (int i = 0; i < numWeights; i++) {
                    joinWeights[i] = Integer.parseInt(tokenizer.nextToken());
                }

                joinWeightShift = calcJoinWeightShift(joinWeights);

            } else if (tag.equals("STS")) {
                String name = tokenizer.nextToken();
                if (name.equals("STS")) {
                    sts = new SampleSet(tokenizer, reader);
                } else {
                    mcep = new SampleSet(tokenizer, reader);
                }
            } else if (tag.equals("UNITS")) {
                int type = Integer.parseInt(tokenizer.nextToken());
                int phone = Integer.parseInt(tokenizer.nextToken());
                int start = Integer.parseInt(tokenizer.nextToken());
                int end = Integer.parseInt(tokenizer.nextToken());
                int prev = Integer.parseInt(tokenizer.nextToken());
                int next = Integer.parseInt(tokenizer.nextToken());
                DatabaseClusterUnit unit
                        = new DatabaseClusterUnit(type, phone, start,
                        end, prev, next);
                unitList.add(unit);
            } else if (tag.equals("CART")) {
                String name = tokenizer.nextToken();
                int nodes = Integer.parseInt(tokenizer.nextToken());
                CART cart = new CARTImpl(reader, nodes);
                cartMap.put(name, cart);

                if (defaultCart == null) {
                    defaultCart = cart;
                }
            } else if (tag.equals("UNIT_TYPE")) {
                String name = tokenizer.nextToken();
                int start = Integer.parseInt(tokenizer.nextToken());
                int count = Integer.parseInt(tokenizer.nextToken());
                UnitType unitType = new UnitType(name, start, count);
                unitTypesList.add(unitType);
            } else {
                throw new Error("Unsupported tag " + tag + " in db line `" + line + "'");
            }
        } catch (NoSuchElementException nse) {
            throw new Error("Error parsing db " + nse.getMessage());
        } catch (NumberFormatException nfe) {
            throw new Error("Error parsing numbers in db line `" + line + "':" + nfe.getMessage());
        }
    }

    /**
     * Loads a binary file from the input stream.
     *
     * @param is the input stream to read the database from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadBinary(InputStream is) throws IOException {
        // we get better performance if we can map the file in
        // 1.0 seconds vs. 1.75 seconds, but we can't
        // always guarantee that we can do that.
        if (is instanceof FileInputStream) {
            FileInputStream fis = (FileInputStream) is;
            FileChannel fc = fis.getChannel();

            MappedByteBuffer bb =
                    fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
            bb.load();
            loadBinary(bb);
            is.close();
        } else {
            loadBinary(new DataInputStream(is));
        }
    }

    /**
     * Loads the database from the given byte buffer.
     *
     * @param bb the byte buffer to load the db from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadBinary(ByteBuffer bb) throws IOException {

        if (bb.getInt() != MAGIC) {
            throw new Error("Bad magic in db");
        }
        if (bb.getInt() != VERSION) {
            throw new Error("Bad VERSION in db");
        }

        continuityWeight = bb.getInt();
        optimalCoupling = bb.getInt();
        extendSelections = bb.getInt();
        joinMethod = bb.getInt();
        joinWeightShift = bb.getInt();

        int weightLength = bb.getInt();
        joinWeights = new int[weightLength];
        for (int i = 0; i < joinWeights.length; i++) {
            joinWeights[i] = bb.getInt();
        }

        int unitsLength = bb.getInt();
        units = new DatabaseClusterUnit[unitsLength];
        for (int i = 0; i < units.length; i++) {
            units[i] = new DatabaseClusterUnit(bb);
        }

        int unitTypesLength = bb.getInt();
        unitTypes = new UnitType[unitTypesLength];
        for (int i = 0; i < unitTypes.length; i++) {
            unitTypes[i] = new UnitType(bb);
        }
        sts = new SampleSet(bb);
        mcep = new SampleSet(bb);

        int numCarts = bb.getInt();
        cartMap = new HashMap();
        for (int i = 0; i < numCarts; i++) {
            String name = Utilities.getString(bb);
            CART cart = CARTImpl.loadBinary(bb);
            cartMap.put(name, cart);

            if (defaultCart == null) {
                defaultCart = cart;
            }
        }
    }

    /**
     * Loads the database from the given input stream.
     *
     * @param is the input stream to load the db from
     * @throws IOException if there is trouble opening the DB
     */
    private void loadBinary(DataInputStream is) throws IOException {

        if (is.readInt() != MAGIC) {
            throw new Error("Bad magic in db");
        }
        if (is.readInt() != VERSION) {
            throw new Error("Bad VERSION in db");
        }

        continuityWeight = is.readInt();
        optimalCoupling = is.readInt();
        extendSelections = is.readInt();
        joinMethod = is.readInt();
        joinWeightShift = is.readInt();

        int weightLength = is.readInt();
        joinWeights = new int[weightLength];
        for (int i = 0; i < joinWeights.length; i++) {
            joinWeights[i] = is.readInt();
        }

        int unitsLength = is.readInt();
        units = new DatabaseClusterUnit[unitsLength];
        for (int i = 0; i < units.length; i++) {
            units[i] = new DatabaseClusterUnit(is);
        }

        int unitTypesLength = is.readInt();
        unitTypes = new UnitType[unitTypesLength];
        for (int i = 0; i < unitTypes.length; i++) {
            unitTypes[i] = new UnitType(is);
        }
        sts = new SampleSet(is);
        mcep = new SampleSet(is);

        int numCarts = is.readInt();
        cartMap = new HashMap();
        for (int i = 0; i < numCarts; i++) {
            String name = Utilities.getString(is);
            CART cart = CARTImpl.loadBinary(is);
            cartMap.put(name, cart);

            if (defaultCart == null) {
                defaultCart = cart;
            }
        }
    }

    /**
     * Dumps a binary form of the database.
     *
     * @param path the path to dump the file to
     */
    void dumpBinary(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            DataOutputStream os = new DataOutputStream(new
                    BufferedOutputStream(fos));

            os.writeInt(MAGIC);
            os.writeInt(VERSION);
            os.writeInt(continuityWeight);
            os.writeInt(optimalCoupling);
            os.writeInt(extendSelections);
            os.writeInt(joinMethod);
            os.writeInt(joinWeightShift);
            os.writeInt(joinWeights.length);
            for (int i = 0; i < joinWeights.length; i++) {
                os.writeInt(joinWeights[i]);
            }

            os.writeInt(units.length);
            for (int i = 0; i < units.length; i++) {
                units[i].dumpBinary(os);
            }

            os.writeInt(unitTypes.length);
            for (int i = 0; i < unitTypes.length; i++) {
                unitTypes[i].dumpBinary(os);
            }
            sts.dumpBinary(os);
            mcep.dumpBinary(os);

            os.writeInt(cartMap.size());
            for (Iterator i = cartMap.keySet().iterator(); i.hasNext(); ) {
                String name = (String) i.next();
                CART cart = (CART) cartMap.get(name);

                Utilities.outString(os, name);
                cart.dumpBinary(os);
            }
            os.close();

            // note that we are not currently saving the state
            // of the default cart

        } catch (FileNotFoundException fe) {
            throw new Error("Can't dump binary database " +
                    fe.getMessage());
        } catch (IOException ioe) {
            throw new Error("Can't write binary database " +
                    ioe.getMessage());
        }
    }

    /**
     * Determines if two databases are identical.
     *
     * @param other the database to compare this one to
     * @return true if the databases are identical
     */
    public boolean compare(ClusterUnitDatabase other) {
        System.out.println("Warning: Compare not implemented yet");
        return false;
    }

    /**
     * Displays an error message
     *
     * @param s the error message
     */
    private void error(String s) {
        System.out.println("ClusterUnitDatabase Error: " + s);
    }

    /**
     * Represents a unit  for the cluster database.
     */
    class DatabaseClusterUnit {

        int type;
        int phone;
        int start;
        int end;
        int prev;
        int next;

        /**
         * Constructs a unit.
         *
         * @param type  the name of the unit
         * @param phone the name of the unit
         * @param start the starting frame
         * @param end   the ending frame
         * @param prev  the previous index
         * @param next  the next index
         */
        DatabaseClusterUnit(int type, int phone, int start,
                            int end, int prev, int next) {
            this.type = type;
            this.phone = phone;
            this.start = start;
            this.end = end;
            this.prev = prev;
            this.next = next;
        }

        /**
         * Creates a unit by reading it from the given byte buffer.
         *
         * @param bb source of the DatabaseClusterUnit data
         * @throws IOException if an IO error occurs
         */
        DatabaseClusterUnit(ByteBuffer bb) throws IOException {
            this.type = bb.getInt();
            this.phone = bb.getInt();
            this.start = bb.getInt();
            this.end = bb.getInt();
            this.prev = bb.getInt();
            this.next = bb.getInt();
        }

        /**
         * Creates a unit by reading it from the given input stream.
         *
         * @param is source of the DatabaseClusterUnit data
         * @throws IOException if an IO error occurs
         */
        DatabaseClusterUnit(DataInputStream is) throws IOException {
            this.type = is.readInt();
            this.phone = is.readInt();
            this.start = is.readInt();
            this.end = is.readInt();
            this.prev = is.readInt();
            this.next = is.readInt();
        }

        /**
         * Returns the name of the unit.
         *
         * @return the name
         */
        String getName() {
            return unitTypes[type].getName();
        }

        /**
         * Dumps this unit to the given output stream.
         *
         * @param os the output stream
         * @throws IOException if an error occurs.
         */
        void dumpBinary(DataOutputStream os) throws IOException {
            os.writeInt(type);
            os.writeInt(phone);
            os.writeInt(start);
            os.writeInt(end);
            os.writeInt(prev);
            os.writeInt(next);
        }
    }
}

/**
 * Represents a unit type in the system
 */
class UnitType {
    private String name;
    private int start;
    private int count;

    /**
     * Constructs a UnitType from the given parameters
     *
     * @param name  the name of the type
     * @param start the starting index for this type
     * @param count the number of elements for this type
     */
    UnitType(String name, int start, int count) {
        this.name = name;
        this.start = start;
        this.count = count;
    }

    /**
     * Creates a unit type by reading it from the given input stream.
     *
     * @param is source of the UnitType data
     * @throws IOException if an IO error occurs
     */
    UnitType(DataInputStream is) throws IOException {
        this.name = Utilities.getString(is);
        this.start = is.readInt();
        this.count = is.readInt();
    }

    /**
     * Creates a unit type by reading it from the given byte buffer.
     *
     * @param bb source of the UnitType  data
     * @throws IOException if an IO error occurs
     */
    UnitType(ByteBuffer bb) throws IOException {
        this.name = Utilities.getString(bb);
        this.start = bb.getInt();
        this.count = bb.getInt();
    }

    /**
     * Gets the name for this unit type
     *
     * @return the name for the type
     */
    String getName() {
        return name;
    }

    /**
     * Gets the start index for this type
     *
     * @return the start index
     */
    int getStart() {
        return start;
    }

    /**
     * Gets the count for this type
     *
     * @return the  count for this type
     */
    int getCount() {
        return count;
    }

    /**
     * Dumps this unit to the given output stream.
     *
     * @param os the output stream
     * @throws IOException if an error occurs.
     */
    void dumpBinary(DataOutputStream os) throws IOException {
        Utilities.outString(os, name);
        os.writeInt(start);
        os.writeInt(count);
    }
}

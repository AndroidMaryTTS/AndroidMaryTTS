/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mf.org.apache.xerces.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * This symbol table uses SoftReferences to its String entries, which means that table entries
 * that have no references to them can be garbage collected when memory is needed.  Thus, in
 * documents with very very large numbers of unique strings, using this SymbolTable will prevent
 * an out of memory error from occuring.
 *
 * @author Peter McCracken, IBM
 * @version $Id: SoftReferenceSymbolTable.java 924298 2010-03-17 13:57:20Z mrglavas $
 * @see SymbolTable
 */
/*
 * This class extends SymbolTable.  Logically, it would make more sense if it and SymbolTable
 * shared a common interface, because despite almost identical logic, SoftReferenceSymbolTable
 * shares almost no code with SymbolTable (because of necessary checks for null table entries
 * in the code).  I've chosen to avoid making the interface because we don't want to slow down
 * the vastly more common case of using the regular SymbolTable.  We also don't want to change
 * SymbolTable, since it's a public class that's probably commonly in use by many applications.
 * -PJM
 */
public class SoftReferenceSymbolTable extends SymbolTable {

    private final ReferenceQueue fReferenceQueue;
    /*
     * This variable masks the fBuckets variable used by SymbolTable.
     */
    protected SREntry[] fBuckets = null;

    //
    // Constructors
    //

    /**
     * Constructs a new, empty SymbolTable with the specified initial
     * capacity and the specified load factor.
     *
     * @param initialCapacity the initial capacity of the SymbolTable.
     * @param loadFactor      the load factor of the SymbolTable.
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero, or if the load factor is nonpositive.
     */
    public SoftReferenceSymbolTable(int initialCapacity, float loadFactor) {
        /*
         * Not calling super() because we don't want to initialize the Entry buckets
         * used by the base class.
         */
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }

        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        }

        if (initialCapacity == 0) {
            initialCapacity = 1;
        }

        fLoadFactor = loadFactor;
        fTableSize = initialCapacity;
        fBuckets = new SREntry[fTableSize];
        fThreshold = (int) (fTableSize * loadFactor);
        fCount = 0;

        fReferenceQueue = new ReferenceQueue();
    }

    /**
     * Constructs a new, empty SymbolTable with the specified initial capacity
     * and default load factor, which is <tt>0.75</tt>.
     *
     * @param initialCapacity the initial capacity of the hashtable.
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero.
     */
    public SoftReferenceSymbolTable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    /**
     * Constructs a new, empty SymbolTable with a default initial capacity (101)
     * and load factor, which is <tt>0.75</tt>.
     */
    public SoftReferenceSymbolTable() {
        this(TABLE_SIZE, 0.75f);
    }

    //
    // Public methods
    //

    /**
     * Adds the specified symbol to the symbol table and returns a
     * reference to the unique symbol. If the symbol already exists,
     * the previous symbol reference is returned instead, in order
     * guarantee that symbol references remain unique.
     *
     * @param symbol The new symbol.
     */
    @Override
    public String addSymbol(String symbol) {
        clean();
        // search for identical symbol
        int bucket = hash(symbol) % fTableSize;
        for (SREntry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            SREntryData data = (SREntryData) entry.get();
            if (data == null) {
                continue;
            }
            if (data.symbol.equals(symbol)) {
                return data.symbol;
            }
        }

        if (fCount >= fThreshold) {
            // Rehash the table if the threshold is exceeded
            rehash();
            bucket = hash(symbol) % fTableSize;
        }

        // add new entry
        symbol = symbol.intern();
        SREntry entry = new SREntry(symbol, fBuckets[bucket], bucket, fReferenceQueue);
        fBuckets[bucket] = entry;
        ++fCount;
        return symbol;
    } // addSymbol(String):String

    /**
     * Adds the specified symbol to the symbol table and returns a
     * reference to the unique symbol. If the symbol already exists,
     * the previous symbol reference is returned instead, in order
     * guarantee that symbol references remain unique.
     *
     * @param buffer The buffer containing the new symbol.
     * @param offset The offset into the buffer of the new symbol.
     * @param length The length of the new symbol in the buffer.
     */
    @Override
    public String addSymbol(char[] buffer, int offset, int length) {
        clean();
        // search for identical symbol
        int bucket = hash(buffer, offset, length) % fTableSize;
        OUTER:
        for (SREntry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            SREntryData data = (SREntryData) entry.get();
            if (data == null) {
                continue;
            }
            if (length == data.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (buffer[offset + i] != data.characters[i]) {
                        continue OUTER;
                    }
                }
                return data.symbol;
            }
        }

        if (fCount >= fThreshold) {
            // Rehash the table if the threshold is exceeded
            rehash();
            bucket = hash(buffer, offset, length) % fTableSize;
        }

        // add new entry
        String symbol = new String(buffer, offset, length).intern();
        SREntry entry = new SREntry(symbol, buffer, offset, length, fBuckets[bucket], bucket, fReferenceQueue);
        fBuckets[bucket] = entry;
        ++fCount;
        return symbol;
    } // addSymbol(char[],int,int):String

    /**
     * Increases the capacity of and internally reorganizes this
     * SymbolTable, in order to accommodate and access its entries more
     * efficiently.  This method is called automatically when the
     * number of keys in the SymbolTable exceeds this hashtable's capacity
     * and load factor.
     */
    @Override
    protected void rehash() {

        int oldCapacity = fBuckets.length;
        SREntry[] oldTable = fBuckets;

        int newCapacity = oldCapacity * 2 + 1;
        SREntry[] newTable = new SREntry[newCapacity];

        fThreshold = (int) (newCapacity * fLoadFactor);
        fBuckets = newTable;
        fTableSize = fBuckets.length;

        for (int i = oldCapacity; i-- > 0; ) {
            for (SREntry old = oldTable[i]; old != null; ) {
                SREntry e = old;
                old = old.next;

                SREntryData data = (SREntryData) e.get();
                if (data != null) {
                    int index = hash(data.characters, 0, data.characters.length) % newCapacity;
                    if (newTable[index] != null) {
                        newTable[index].prev = e;
                    }
                    e.next = newTable[index];
                    e.prev = null;
                    newTable[index] = e;
                } else {
                    fCount--;
                }
            }
        }
    }

    /**
     * Returns true if the symbol table already contains the specified
     * symbol.
     *
     * @param symbol The symbol to look for.
     */
    @Override
    public boolean containsSymbol(String symbol) {

        // search for identical symbol
        int bucket = hash(symbol) % fTableSize;
        int length = symbol.length();
        OUTER:
        for (SREntry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            SREntryData data = (SREntryData) entry.get();
            if (data == null) {
                continue;
            }
            if (length == data.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (symbol.charAt(i) != data.characters[i]) {
                        continue OUTER;
                    }
                }
                return true;
            }
        }

        return false;

    } // containsSymbol(String):boolean

    /**
     * Returns true if the symbol table already contains the specified
     * symbol.
     *
     * @param buffer The buffer containing the symbol to look for.
     * @param offset The offset into the buffer.
     * @param length The length of the symbol in the buffer.
     */
    @Override
    public boolean containsSymbol(char[] buffer, int offset, int length) {

        // search for identical symbol
        int bucket = hash(buffer, offset, length) % fTableSize;
        OUTER:
        for (SREntry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            SREntryData data = (SREntryData) entry.get();
            if (data == null) {
                continue;
            }
            if (length == data.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (buffer[offset + i] != data.characters[i]) {
                        continue OUTER;
                    }
                }
                return true;
            }
        }

        return false;

    } // containsSymbol(char[],int,int):boolean

    private void removeEntry(SREntry entry) {
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        }
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        } else {
            fBuckets[entry.bucket] = entry.next;
        }
        fCount--;
    }

    /**
     * Removes stale symbols from the table.
     */
    private void clean() {
        SREntry entry = (SREntry) fReferenceQueue.poll();
        while (entry != null) {
            removeEntry(entry);
            entry = (SREntry) fReferenceQueue.poll();
        }
    }

    //
    // Classes
    //

    /**
     * This class is a symbol table entry. Each entry acts as a node
     * in a doubly-linked list.
     * <p/>
     * The "SR" stands for SoftReference.
     */
    protected static final class SREntry extends SoftReference {

        /**
         * The next entry.
         */
        public SREntry next;

        /**
         * The previous entry.
         */
        public SREntry prev;

        public int bucket;

        //
        // Constructors
        //

        /**
         * Constructs a new entry from the specified symbol and next entry
         * reference.
         */
        public SREntry(String internedSymbol, SREntry next, int bucket, ReferenceQueue q) {
            super(new SREntryData(internedSymbol), q);
            initialize(next, bucket);
        }

        /**
         * Constructs a new entry from the specified symbol information and
         * next entry reference.
         */
        public SREntry(String internedSymbol, char[] ch, int offset, int length, SREntry next, int bucket, ReferenceQueue q) {
            super(new SREntryData(internedSymbol, ch, offset, length), q);
            initialize(next, bucket);
        }

        private void initialize(SREntry next, int bucket) {
            this.next = next;
            if (next != null) {
                next.prev = this;
            }
            this.prev = null;
            this.bucket = bucket;
        }
    } // class Entry

    protected static final class SREntryData {
        public final String symbol;
        public final char[] characters;

        public SREntryData(String internedSymbol) {
            this.symbol = internedSymbol;
            characters = new char[symbol.length()];
            symbol.getChars(0, characters.length, characters, 0);
        }

        public SREntryData(String internedSymbol, char[] ch, int offset, int length) {
            this.symbol = internedSymbol;
            characters = new char[length];
            System.arraycopy(ch, offset, characters, 0, length);
        }
    }
} // class SoftReferenceSymbolTable

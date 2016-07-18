/**
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 1999-2004 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;

import java.util.LinkedList;

/**
 * Manages a process queue for utterances. Utterances that are
 * queued to a processor can be written via the post method.
 * A processing thread can wait for an utterance to arrive via the
 * pend method.
 */
public class OutputQueue {
    private final static int DEFAULT_SIZE = 5;
    private LinkedList list = new LinkedList();
    private int size;
    private volatile boolean closed = false;

    /**
     * Creates an OutputQueue with the given size.
     *
     * @param size the size of the queue
     */
    public OutputQueue(int size) {
        this.size = size;
    }

    /**
     * Creates a queue with the default size.
     */
    public OutputQueue() {
        this(DEFAULT_SIZE);
    }

    /**
     * Posts the given utterance to the queue. This call will block if
     * the queue is full.
     *
     * @param utterance the utterance to post
     * @throws IllegalStateException if the queue is closed
     */
    public synchronized void post(Utterance utterance) {
        if (closed) {
            throw new IllegalStateException("output queue closed");
        }

        while (list.size() >= size) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }

        list.add(utterance);
        notify();
    }


    /**
     * Closes the queue.
     */
    public synchronized void close() {
        closed = true;
        list.add(null);
        notify();
    }


    /**
     * Determines if the queue is closed.
     *
     * @return true the queue is closed; otherwise false
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Blocks until there is an utterance in the queue.
     *
     * @return the next utterance. On a close or interrupt, a null is
     * returned.
     */
    public synchronized Utterance pend() {
        Utterance utterance = null;
        while (list.size() == 0) {
            try {
                wait();
            } catch (InterruptedException ie) {
                return null;
            }
        }
        utterance = (Utterance) list.removeFirst();
        notify();
        return utterance;
    }

    /**
     * Removes all items from this OutputQueue.
     */
    public synchronized void removeAll() {
        list.clear();
    }
}



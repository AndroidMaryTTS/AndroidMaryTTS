package lib.comirva.audio;

import java.util.LinkedList;

/**
 * <p>Strong Limited Reference</p>
 * <p/>
 * <p>Description: </p>
 * This class is designed according to the <code>java.lang.ref<code> package.
 * However the Garbage Collector cannot remove any of the references, because
 * all of them are strong references. But since the number of instances, still
 * pointing to the referenced object is limited, the total memory usage has a
 * fixed upper boundary. Only <code>MAX_REFERENCES</code> objects will be kept
 * alive. The algorithm will free the least frequently used reference as
 * SoftReference does.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class StrongLimitedReference<T> {
    //maximum number of references to keep alive
    public static final int MAX_REFERENCES = 200;
    //queue taking care of the least frequently used reference
    private static LinkedList<StrongLimitedReference> queue = new LinkedList<StrongLimitedReference>();
    //the reference to the object to keep alive
    private T ref;

    public StrongLimitedReference(T object) {
        super();

        if (object == null)
            throw new IllegalArgumentException("object reference must not be a null value;");

        //store object reference
        this.ref = object;

        //enqueue in list with maximum size
        synchronized (queue) {
            if (queue.size() < MAX_REFERENCES) {
                //enough place to enqueue a further reference
                queue.addLast(this);
            } else {
                //free the least frequently used reference
                queue.removeFirst().free();
                //now, there is place to place the new reference
                queue.addLast(this);
            }
        }
    }

    /**
     * Returns the reference to the object this class points to, or null if the
     * referenced object has been freed.
     *
     * @return Object the referenced object or null
     */
    public T get() {
        synchronized (queue) {
            //simply return, if the reference doesn't exist
            if (ref == null)
                return null;
            //remove this reference from the list
            queue.remove(this);
            //add it at the end of the list (last used reference)
            queue.addLast(this);
            //return the reference
            return ref;
        }
    }

    /**
     * Frees the referenced object.
     */
    private void free() {
        this.ref = null;
    }
}

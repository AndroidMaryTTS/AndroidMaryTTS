package lib.comirva.audio;


/**
 * <b>Attribute</b>
 * <p/>
 * <p>Description: </p>
 * An <code>Attribute</code> is an abstract concept strongly realted to another
 * object, which one can attributed with an <code>Attribute</code> object.
 * One can think about the attributed object to be some kinde of a container,
 * which represents a real world object, that one can describe closer simply by
 * putting <code>Attribute</code> objects in the container. In latter the
 * attributed object is simpliy called <i>container</>.<br>
 * <br>
 * For example one could think of a container <i>House</i> being closer
 * described by an attribute <i>Color</i>, whose state could be <i>red</i> for
 * example. Another attribute could be <i>Cat</i>, telling us that there lives a
 * cat in the house.<br>
 * <br>
 * The obvious advantage is that there is no need to change the container's
 * class structure, if one has to add a new attibute, but simply can subclass
 * <code>Atrribute</code>. This is extremely usefull, if possible attributes of
 * a container are unknown at the current development stage.<br>
 * <br>
 * In combination with an <code>AttributeExtractor</code> an
 * <code>Attribute</code> object gets a further semantics. An
 * <code>Attribute</code> describes a container, and the describtion can be
 * obtained automatically by an <i>extraction process</i>. The type of
 * information needed to obtain an attribute depends on the attribute extraction
 * process and therfore can variy from attribute to attribute.
 * So if one has a large number of containers, a batch job for extracting
 * attributes is a good idea. This takes us to the concept of a
 * <code>AttributeExtractionThread</code>.<br>
 * Last but not least <code>Attributes</code> may implement the
 * <code>XMLSerializable</code> interface, such that it is easy for a container
 * to make attributes persistent.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 * @see comirva.audio.extraction.AttributeExtractor
 * @see comirva.audio.XMLSerializable
 * @see comirva.audio.extraction.AudioFeatureExtraction
 */
public abstract class Attribute {
    /**
     * Constructor of an atrribute.
     */
    protected Attribute() {
        super();
    }


    /**
     * Returns an unique integer value for each subclass of attribute.
     * This can be used to distinguish different subclasses.  By
     * definition this is the hash code of the attribute's class name.
     *
     * @return int typevalue
     */
    public final int getType() {
        return this.getClassName().hashCode();
    }


    /**
     * Returns the full qualified class name of an attribute.
     *
     * @return String the full qualified class name
     */
    public final String getClassName() {
        return this.getClass().getName();
    }


}
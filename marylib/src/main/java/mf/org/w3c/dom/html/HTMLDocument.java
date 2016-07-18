/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package mf.org.w3c.dom.html;

import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.NodeList;

/**
 * An <code>HTMLDocument</code> is the root of the HTML hierarchy andholds the
 * entire content. Beside providing access to the hierarchy, italso provides
 * some convenience methods for accessing certain sets ofinformation from the
 * document.
 * <p>The following properties have been deprecated in favor of the
 * corresponding ones for the BODY element:alinkColorbackgroundbgColorfgColor
 * linkColorvlinkColor
 */
public interface HTMLDocument extends Document {
    /**
     * The title of a document as specified by the <code>TITLE</code>element in
     * the head of the document.
     */
    String getTitle();

    void setTitle(String title);

    /**
     * Returns the URI of the page that linked to this page. The value isan
     * empty string if the user navigated to the page directly (notthrough a
     * link, but, for example, via a bookmark).
     */
    String getReferrer();

    /**
     * The domain name of the server that served the document, or a nullstring
     * if the server cannot be identified by a domain name.
     */
    String getDomain();

    /**
     * The complete URI of the document.
     */
    String getURL();

    /**
     * The element that contains the content for the document. In documentswith
     * <code>BODY</code> contents, returns the <code>BODY</code>element, and in
     * frameset documents, this returns the outermost<code>FRAMESET</code>
     * element.
     */
    HTMLElement getBody();

    void setBody(HTMLElement body);

    /**
     * A collection of all the <code>IMG</code> elements in a document.The
     * behavior is limited to <code>IMG</code> elements forbackwards
     * compatibility.
     */
    HTMLCollection getImages();

    /**
     * A collection of all the <code>OBJECT</code> elements that includeapplets
     * and <code>APPLET</code> (deprecated) elements ina document.
     */
    HTMLCollection getApplets();

    /**
     * A collection of all <code>AREA</code> elements andanchor (<code>A</code>)
     * elements in a documentwith a value for the <code>href</code> attribute.
     */
    HTMLCollection getLinks();

    /**
     * A collection of all the forms of a document.
     */
    HTMLCollection getForms();

    /**
     * A collection of all the anchor (<code>A</code>) elements in a document
     * with a value for the <code>name</code> attribute.Note. For reasons of
     * backwardscompatibility, the returned set of anchors only contains those
     * anchors created with the <code>name</code> attribute, not those created
     * with the <code>id</code> attribute.
     */
    HTMLCollection getAnchors();

    /**
     * The cookies associated with this document. If there are none, thevalue is
     * an empty string. Otherwise, the value is a string: asemicolon-delimited
     * list of "name, value" pairs for all the cookiesassociated with the page.
     * For example, <code>name=value;expires=date</code>.
     */
    String getCookie();

    void setCookie(String cookie);

    /**
     * Note.This method and the ones following allow a user to add to or replace
     * the structuremodel of a document using strings of unparsed HTML. At the
     * time of writing alternate methods for providing similar functionality
     * for both HTML and XML documents were being considered. The following
     * methodsmay be deprecated at some point in the future in favor of a more
     * general-purpose mechanism.
     * <br>Open a document stream for writing. If a document exists in the
     * target, this method clears it.
     */
    void open();

    /**
     * Closes a document stream opened by <code>open()</code>and forces
     * rendering.
     */
    void close();

    /**
     * Write a string of text to a document stream opened by<code>open()</code>.
     * The text is parsed into the document's structuremodel.
     *
     * @param text The string to be parsed into some structure in the document
     *             structuremodel.
     */
    void write(String text);

    /**
     * Write a string of text followed by a newline character to a document
     * stream opened by <code>open()</code>. The text is parsed into the
     * document's structure model.
     *
     * @param text The string to be parsed into some structure in the document
     *             structuremodel.
     */
    void writeln(String text);

    /**
     * Returns the Element whose <code>id</code> is given by elementId. If no
     * such element exists, returns <code>null</code>. Behavior is not defined
     * if more than one element has this <code>id</code>.
     *
     * @param elementId The unique <code>id</code> value for an element.
     * @return The matching element.
     */
    @Override
    Element getElementById(String elementId);

    /**
     * Returns the (possibly empty) collection of elements whose<code>name</code>
     * value is given by <code>elementName</code>.
     *
     * @param elementName The <code>name</code> attribute value for an element.
     * @return The matching elements.
     */
    NodeList getElementsByName(String elementName);
}


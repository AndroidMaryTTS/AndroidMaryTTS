/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2014  DFKI Language Technology Lab http://www.dfki.de/lt
 *   Author: Joerg Steffen, steffen@dfki.de
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.dfki.lt.tools.tokenizer.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.CharacterIterator;

import de.dfki.lt.tools.tokenizer.FileTools;
import de.dfki.lt.tools.tokenizer.JTok;
import de.dfki.lt.tools.tokenizer.annotate.AnnotatedString;
import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;
import mf.javax.xml.parsers.DocumentBuilder;
import mf.javax.xml.parsers.DocumentBuilderFactory;
import mf.javax.xml.parsers.ParserConfigurationException;
import mf.javax.xml.transform.OutputKeys;
import mf.javax.xml.transform.Transformer;
import mf.javax.xml.transform.TransformerException;
import mf.javax.xml.transform.TransformerFactory;
import mf.javax.xml.transform.dom.DOMSource;
import mf.javax.xml.transform.stream.StreamResult;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Element;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * {@link XmlOutputter} provides static methods that return an XML presentation of an
 * {@link AnnotatedString}.
 *
 * @author Joerg Steffen, DFKI
 */
public final class XmlOutputter {

    /**
     * name of XML elements in the result that describe a document
     */
    public static final String XML_DOCUMENT = "Document";

    /**
     * name of XML elements in the result that describe a paragraph
     */
    public static final String XML_PARAGRAPH = "p";

    /**
     * name of XML elements in the result that describe a text unit; text units are contained in
     * paragraphs
     */
    public static final String XML_TEXT_UNIT = "tu";

    /**
     * name of the XML attribute in {@code XML_TEXT_UNIT} that contains the text unit id
     */
    public static final String ID_ATT = "id";

    /**
     * name of XML elements in the result that describe a token; tokens are contained in text units
     */
    public static final String XML_TOKEN = "Token";

    /**
     * name of the XML attribute in {@code XML_TOKEN} that contains the token image
     */
    public static final String IMAGE_ATT = "string";

    /**
     * name of the XML attribute in {@code XML_TOKEN} that contains the Penn Treebank token image if
     * it is any different than the regular surface string
     */
    public static final String PTB_ATT = "ptb";

    /**
     * name of the XML attribute in {@code XML_TOKEN} that contains the token type
     */
    public static final String TOK_TYPE_ATT = "type";

    /**
     * name of the XML attribute in {@code XML_TOKEN} that contains the token offset
     */
    public static final String OFFSET_ATT = "offset";

    /**
     * name of the XML attribute in {@code XML_TOKEN} that contains the token length
     */
    public static final String LENGTH_ATT = "length";

    /**
     * the logger
     */
//  private static final Logger logger = LoggerFactory.getLogger(XmlOutputter.class);


    // would create a new instance of {@link XmlOutputter}; not to be used
    private XmlOutputter() {

        // private constructor to enforce noninstantiability
    }


    /**
     * Creates an XML document from the given annotated string.
     *
     * @param input the annotated string
     * @return the XML document
     * @throws ProcessingException if an error occurs
     */
    public static Document createXmlDocument(AnnotatedString input) {

        // create result document
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException pce) {
            throw new ProcessingException(pce.getLocalizedMessage(), pce);
        }

        // create root element
        Element root = doc.createElement(XML_DOCUMENT);
        doc.appendChild(root);

        // init text unit counter
        int tuId = 0;

        // create paragraph element
        Element p = doc.createElement(XML_PARAGRAPH);
        // create text unit element
        Element tu = doc.createElement(XML_TEXT_UNIT);
        tu.setAttribute(ID_ATT, tuId + "");

        // iterate over tokens and create XML elements
        char c = input.setIndex(0);
        while (c != CharacterIterator.DONE) {

            int tokenStart = input.getRunStart(JTok.CLASS_ANNO);
            int tokenEnd = input.getRunLimit(JTok.CLASS_ANNO);
            // check if c belongs to a token
            if (null != input.getAnnotation(JTok.CLASS_ANNO)) {
                // get tag
                String type = (String) input.getAnnotation(JTok.CLASS_ANNO);
                if (null == type) {
                    throw new ProcessingException(
                            String.format("undefined class %s", input.getAnnotation(JTok.CLASS_ANNO)));
                }
                // create new element
                Element xmlToken = doc.createElement(XML_TOKEN);
                // set attributes
                String image = input.substring(tokenStart, tokenEnd);
                xmlToken.setAttribute(IMAGE_ATT, image);
                String ptbImage = Token.applyPtbFormat(image, type);
                if (null != ptbImage) {
                    xmlToken.setAttribute(PTB_ATT, ptbImage);
                }
                xmlToken.setAttribute(TOK_TYPE_ATT, type);
                xmlToken.setAttribute(OFFSET_ATT, tokenStart + "");
                xmlToken.setAttribute(LENGTH_ATT, image.length() + "");

                // check if token is first token of a paragraph or text unit
                if (null != input.getAnnotation(JTok.BORDER_ANNO)) {
                    // add current text unit to paragraph and create new one
                    if (tu.hasChildNodes()) {
                        p.appendChild(tu);
                        tu = doc.createElement(XML_TEXT_UNIT);
                        tuId++;
                        tu.setAttribute(ID_ATT, tuId + "");
                    }
                }

                // check if token is first token of a paragraph
                if (input.getAnnotation(JTok.BORDER_ANNO) == JTok.P_BORDER) {
                    // add current paragraph to document and create new one
                    if (p.hasChildNodes()) {
                        root.appendChild(p);
                        p = doc.createElement(XML_PARAGRAPH);
                    }
                }

                // add token to text unit
                tu.appendChild(xmlToken);
            }
            // set iterator to next token
            c = input.setIndex(tokenEnd);
        }
        // add last text units to paragraph
        if (tu.hasChildNodes()) {
            p.appendChild(tu);
        }
        // add last paragraph element to document
        if (p.hasChildNodes()) {
            root.appendChild(p);
        }

        // return document
        return doc;
    }


    /**
     * Creates an XML file from the given annotated string.
     *
     * @param input    the annotated string
     * @param encoding the encoding to use
     * @param fileName the name of the XML file
     * @throws ProcessingException if an error occurs
     */
    public static void createXmlFile(
            AnnotatedString input, String encoding, String fileName) {

        // tokenize text
        Document doc = createXmlDocument(input);

        try {
            // init writer for result
            Writer out = new OutputStreamWriter(new FileOutputStream(fileName), encoding);
            // use a transformer for output
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
            out.close();
        } catch (TransformerException te) {
            throw new ProcessingException(te.getLocalizedMessage(), te);
        } catch (IOException ioe) {
            throw new ProcessingException(ioe.getLocalizedMessage(), ioe);
        }
    }


    /**
     * Creates an XML string from the given annotated string.
     *
     * @param input the annotated string
     * @return an XML String
     * @throws ProcessingException if an error occurs
     */
    public static String createXmlString(AnnotatedString input) {

        // tokenize text
        Document doc = createXmlDocument(input);

        // init output writer for result
        StringWriter out = new StringWriter();

        // use a transformer for output
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        } catch (TransformerException te) {
            throw new ProcessingException(te.getLocalizedMessage(), te);
        }

        // return result
        return out.toString();
    }


    /**
     * This main method must be used with two or three arguments:
     * <ul>
     * <li>a file name for the document to tokenize
     * <li>the language of the document
     * <li>an optional encoding to use (default is UTF-8)
     * </ul>
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        // check for correct arguments
        if ((args.length != 2)
                && (args.length != 3)) {
            System.out.println(
                    "This method needs two arguments:\n"
                            + "- a file name for the document to tokenize\n"
                            + "- the language of the document\n"
                            + "- an optional encoding to use (default is UTF-8)");
            System.exit(1);
        }

        // check encoding
        String encoding = "UTF-8";
        if (args.length == 3) {
            encoding = args[2];
        }

        String text = null;
        try {
            // get text from file
            text = FileTools.readFileAsString(new File(args[0]), encoding);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        try {
            // create new instance of JTok
            JTok testTok = new JTok();

            // tokenize text
            AnnotatedString result = testTok.tokenize(text, args[1]);

            // print result
            System.out.println(XmlOutputter.createXmlString(result));

        } catch (IOException e) {
            //logger.error(e.getLocalizedMessage(), e);
            System.out.println(e.getLocalizedMessage());
        }
    }
}

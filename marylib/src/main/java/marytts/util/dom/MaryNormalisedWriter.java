/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.util.dom;

// TraX classes

import android.util.Log;

import com.marytts.android.link.MaryLink;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

import marytts.exceptions.MaryConfigurationException;
import marytts.server.Mary;
import marytts.util.io.ReaderSplitter;
import mf.javax.xml.transform.Result;
import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.Templates;
import mf.javax.xml.transform.Transformer;
import mf.javax.xml.transform.TransformerConfigurationException;
import mf.javax.xml.transform.TransformerException;
import mf.javax.xml.transform.TransformerFactory;
import mf.javax.xml.transform.TransformerFactoryConfigurationError;
import mf.javax.xml.transform.dom.DOMSource;
import mf.javax.xml.transform.stream.StreamResult;
import mf.javax.xml.transform.stream.StreamSource;
import mf.org.w3c.dom.Node;

//import org.apache.log4j.Logger;

/**
 * A wrapper class for output of XML DOM trees in a Mary normalised way:
 * One tag or text node per line, no indentation.
 * This is only needed during the transition phase to "real" XML modules.
 *
 * @author Marc Schr&ouml;der
 */

public class MaryNormalisedWriter {
    private static TransformerFactory tFactory = null;
    private static Templates stylesheet = null;

    //private static Logger logger; // only used for extensive debug output

    private Transformer transformer;

    /**
     * Default constructor.
     * Calls <code>startup()</code> if it has not been called before.
     *
     * @see #startup().
     */
    public MaryNormalisedWriter()
            throws MaryConfigurationException {
        try {
            // startup every time:
            startup();
            transformer = stylesheet.newTransformer();
        } catch (Exception e) {
            Log.d(Mary.LOG, "Cannot initialise XML writing code" + e);
            throw new MaryConfigurationException("Cannot initialise XML writing code", e);
        }
    }

    // Methods

    /**
     * Start up the static parts, and compile the normalise-maryxml XSLT
     * stylesheet which can then be used by multiple threads.
     *
     * @throws TransformerFactoryConfigurationError if the TransformerFactory cannot be instanciated.
     * @throws FileNotFoundException                if the stylesheet file cannot be found.
     * @throws TransformerConfigurationException    if the templates stylesheet cannot be generated.
     */
    private static void startup()
            throws TransformerFactoryConfigurationError, TransformerConfigurationException {
        // only start the stuff if it hasn't been started yet.
        if (tFactory == null) {
            tFactory = TransformerFactory.newInstance();
        }
        if (stylesheet == null) {
            StreamSource stylesheetStream =
                    null;
            try {
                stylesheetStream = new StreamSource(
                        MaryLink.getContext().getAssets().open("marytts/util/dom/normalise-maryxml.xsl"));
            } catch (IOException e) {
                Log.d(Mary.LOG, e.getMessage());
            }
            stylesheet = tFactory.newTemplates(stylesheetStream);
        }
//        if (logger == null)
//            logger = MaryUtils.getLogger("MaryNormalisedWriter");

    }

    /**
     * The simplest possible command line interface to the
     * MaryNormalisedWriter. Reads a "real" XML document from stdin,
     * and outputs it in the MaryNormalised form to stdout.
     */
    public static void main(String[] args) throws Throwable {
        startup();
        MaryNormalisedWriter writer = new MaryNormalisedWriter();

        ReaderSplitter splitter = new ReaderSplitter(new InputStreamReader(System.in), "</maryxml>");

        Reader oneXMLStructure = null;
        while ((oneXMLStructure = splitter.nextReader()) != null) {
            writer.output(new StreamSource(oneXMLStructure));
        }
    }

    /**
     * The actual output to stdout.
     *
     * @param input a DOMSource, a SAXSource or a StreamSource.
     * @throws TransformerException if the transformation cannot be performed.
     * @see javax.xml.transform.Transformer
     */
    public void output(Source input, Result destination) throws TransformerException {
        //logger.debug("Before transform");
        transformer.transform(input, destination);
        //logger.debug("After transform");
    }

    /**
     * Output any Source to stdout.
     */
    public void output(Source input) throws TransformerException {
        output(input, new StreamResult(new PrintStream(System.out, true)));
    }

    /**
     * Output a DOM node to stdout.
     *
     * @see #output(Source)
     */
    public void output(Node input) throws TransformerException {
        output(new DOMSource(input));
    }

    /**
     * Output a DOM node to a specified destination
     */
    public void output(Node input, OutputStream destination) throws TransformerException {
        output(new DOMSource(input), new StreamResult(destination));
    }
}


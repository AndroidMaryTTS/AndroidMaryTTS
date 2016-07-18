// xparse.java - A simple command-line XML parser

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

package mf.org.apache.xml.resolver.apps;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import mf.org.apache.xml.resolver.Catalog;
import mf.org.apache.xml.resolver.CatalogManager;
import mf.org.apache.xml.resolver.helpers.Debug;
import mf.org.apache.xml.resolver.tools.ResolvingParser;


/**
 * A simple command-line XML parsing application.
 * <p/>
 * <p>This class implements a simple command-line XML Parser. It's
 * just a little wrapper around the JAXP Parser with support for
 * catalogs.
 * </p>
 * <p/>
 * <p>Usage: xparse [options] document.xml</p>
 * <p/>
 * <p>Where:</p>
 * <p/>
 * <dl>
 * <dt><code>-c</code> <em>catalogfile</em></dt>
 * <dd>Load a particular catalog file.</dd>
 * <dt><code>-w</code></dt>
 * <dd>Perform a well-formed parse, not a validating parse.</dd>
 * <dt><code>-v</code> (the default)</dt>
 * <dd>Perform a validating parse.</dd>
 * <dt><code>-n</code></dt>
 * <dd>Perform a namespace-ignorant parse.</dd>
 * <dt><code>-N</code> (the default)</dt>
 * <dd>Perform a namespace-aware parse.</dd>
 * <dt><code>-d</code> <em>integer</em></dt>
 * <dd>Set the debug level. Warnings are shown if the debug level
 * is &gt; 2.</dd>
 * <dt><code>-E</code> <em>integer</em></dt>
 * <dd>Set the maximum number of errors to display.</dd>
 * </dl>
 * <p/>
 * <p>The process ends with error-level 1, if there errors.</p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 * @see mf.org.apache.xml.resolver.tools.ResolvingParser
 * @deprecated This interface has been replaced by the
 * {@link mf.org.apache.xml.resolver.tools.ResolvingXMLReader} for SAX2.
 */
@Deprecated
public class xparse {
    private static Debug debug = CatalogManager.getStaticManager().debug;

    /**
     * The main entry point
     */
    public static void main(String[] args)
            throws IOException {

        String xmlfile = null;
        int debuglevel = 0;
        int maxErrs = 10;
        boolean nsAware = true;
        boolean validating = true;
        boolean showWarnings = (debuglevel > 2);
        boolean showErrors = true;
        Vector catalogFiles = new Vector();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                ++i;
                catalogFiles.add(args[i]);
                continue;
            }

            if (args[i].equals("-w")) {
                validating = false;
                continue;
            }

            if (args[i].equals("-v")) {
                validating = true;
                continue;
            }

            if (args[i].equals("-n")) {
                nsAware = false;
                continue;
            }

            if (args[i].equals("-N")) {
                nsAware = true;
                continue;
            }

            if (args[i].equals("-d")) {
                ++i;
                String debugstr = args[i];
                try {
                    debuglevel = Integer.parseInt(debugstr);
                    if (debuglevel >= 0) {
                        debug.setDebug(debuglevel);
                        showWarnings = (debuglevel > 2);
                    }
                } catch (Exception e) {
                    // nop
                }
                continue;
            }

            if (args[i].equals("-E")) {
                ++i;
                String errstr = args[i];
                try {
                    int errs = Integer.parseInt(errstr);
                    if (errs >= 0) {
                        maxErrs = errs;
                    }
                } catch (Exception e) {
                    // nop
                }
                continue;
            }

            xmlfile = args[i];
        }

        if (xmlfile == null) {
            System.out.println("Usage: org.apache.xml.resolver.apps.xparse [opts] xmlfile");
            System.exit(1);
        }

        ResolvingParser.validating = validating;
        ResolvingParser.namespaceAware = nsAware;
        ResolvingParser reader = new ResolvingParser();
        Catalog catalog = reader.getCatalog();

        for (int count = 0; count < catalogFiles.size(); count++) {
            String file = (String) catalogFiles.elementAt(count);
            catalog.parseCatalog(file);
        }

        XParseError xpe = new XParseError(showErrors, showWarnings);
        xpe.setMaxMessages(maxErrs);
        reader.setErrorHandler(xpe);

        String parseType = validating ? "validating" : "well-formed";
        String nsType = nsAware ? "namespace-aware" : "namespace-ignorant";
        if (maxErrs > 0) {
            System.out.println("Attempting "
                    + parseType
                    + ", "
                    + nsType
                    + " parse");
        }

        Date startTime = new Date();

        try {
            reader.parse(xmlfile);
        } catch (SAXException sx) {
            System.out.println("SAX Exception: " + sx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date endTime = new Date();

        long millisec = endTime.getTime() - startTime.getTime();
        long secs = 0;
        long mins = 0;
        long hours = 0;

        if (millisec > 1000) {
            secs = millisec / 1000;
            millisec = millisec % 1000;
        }

        if (secs > 60) {
            mins = secs / 60;
            secs = secs % 60;
        }

        if (mins > 60) {
            hours = mins / 60;
            mins = mins % 60;
        }

        if (maxErrs > 0) {
            System.out.print("Parse ");
            if (xpe.getFatalCount() > 0) {
                System.out.print("failed ");
            } else {
                System.out.print("succeeded ");
                System.out.print("(");
                if (hours > 0) {
                    System.out.print(hours + ":");
                }
                if (hours > 0 || mins > 0) {
                    System.out.print(mins + ":");
                }
                System.out.print(secs + "." + millisec);
                System.out.print(") ");
            }
            System.out.print("with ");

            int errCount = xpe.getErrorCount();
            int warnCount = xpe.getWarningCount();

            if (errCount > 0) {
                System.out.print(errCount + " error");
                System.out.print(errCount > 1 ? "s" : "");
                System.out.print(" and ");
            } else {
                System.out.print("no errors and ");
            }

            if (warnCount > 0) {
                System.out.print(warnCount + " warning");
                System.out.print(warnCount > 1 ? "s" : "");
                System.out.print(".");
            } else {
                System.out.print("no warnings.");
            }

            System.out.println("");
        }

        if (xpe.getErrorCount() > 0) {
            System.exit(1);
        }
    }
}



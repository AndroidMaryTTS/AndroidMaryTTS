// XParseError.java - An error handler for xparse

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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import java.net.MalformedURLException;
import java.net.URL;

import mf.org.apache.xml.resolver.helpers.FileURL;


/**
 * An ErrorHandler for xparse.
 * <p/>
 * <p>This class is just the error handler for xparse.</p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 * @see xparse
 */
public class XParseError implements ErrorHandler {
    /**
     * Show errors?
     */
    private boolean showErrors = true;

    /**
     * Show warnings?
     */
    private boolean showWarnings = false;

    /**
     * How many messages should be presented?
     */
    private int maxMessages = 10;

    /**
     * The number of fatal errors seen so far.
     */
    private int fatalCount = 0;

    /**
     * The number of errors seen so far.
     */
    private int errorCount = 0;

    /**
     * The number of warnings seen so far.
     */
    private int warningCount = 0;

    /**
     * The base URI of the running application.
     */
    private String baseURI = "";

    /**
     * Constructor
     */
    public XParseError(boolean errors, boolean warnings) {
        showErrors = errors;
        showWarnings = warnings;

        try {
            URL url = FileURL.makeURL("basename");
            baseURI = url.toString();
        } catch (MalformedURLException mue) {
            // nop;
        }
    }

    /**
     * Return the error count
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Return the fatal error count
     */
    public int getFatalCount() {
        return fatalCount;
    }

    /**
     * Return the warning count
     */
    public int getWarningCount() {
        return warningCount;
    }

    /**
     * Return the number of messages to display
     */
    public int getMaxMessages() {
        return maxMessages;
    }

    /**
     * Set the number of messages to display
     */
    public void setMaxMessages(int max) {
        maxMessages = max;
    }

    /**
     * SAX2 API
     */
    @Override
    public void error(SAXParseException exception) {
        errorCount++;
        if (showErrors && (errorCount + warningCount < maxMessages)) {
            message("Error", exception);
        }
    }

    /**
     * SAX2 API
     */
    @Override
    public void fatalError(SAXParseException exception) {
        errorCount++;
        fatalCount++;
        if (showErrors && (errorCount + warningCount < maxMessages)) {
            message("Fatal error", exception);
        }
    }

    /**
     * SAX2 API
     */
    @Override
    public void warning(SAXParseException exception) {
        warningCount++;
        if (showWarnings && (errorCount + warningCount < maxMessages)) {
            message("Warning", exception);
        }
    }

    /**
     * Display a message to the user
     */
    private void message(String type, SAXParseException exception) {
        String filename = exception.getSystemId();
        if (filename.startsWith(baseURI)) {
            filename = filename.substring(baseURI.length());
        }

        System.out.print(type
                + ":"
                + filename
                + ":"
                + exception.getLineNumber());

        if (exception.getColumnNumber() > 0) {
            System.out.print(":" + exception.getColumnNumber());
        }

        System.out.println(":" + exception.getMessage());
    }
}

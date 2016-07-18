// FileURL.java - Construct a file: scheme URL

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

package mf.org.apache.xml.resolver.helpers;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Static method for dealing with file: URLs.
 * <p/>
 * <p>This class defines a static method that can be used to construct
 * an appropriate file: URL from parts. It's defined here so that it
 * can be reused throught the resolver.</p>
 * <p/>
 * <p>(Yes, I'd rather have called this class FileUR<b>I</b>, but
 * given that a jave.net.URL is returned, it seemed...even more
 * confusing.)</p>
 *
 * @author Norman Walsh
 *         <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 * @version 1.0
 */
public abstract class FileURL {
    protected FileURL() {
    }

    /**
     * Construct a file: URL for a path name.
     * <p/>
     * <p>URLs in the file: scheme can be constructed for paths on
     * the local file system. Several possibilities need to be considered:
     * </p>
     * <p/>
     * <ul>
     * <li>If the path does not begin with a slash, then it is assumed
     * to reside in the users current working directory
     * (System.getProperty("user.dir")).</li>
     * <li>On Windows machines, the current working directory uses
     * backslashes (\\, instead of /).</li>
     * <li>If the current working directory is "/", don't add an extra
     * slash before the base name.</li>
     * </ul>
     * <p/>
     * <p>This method is declared static so that other classes
     * can use it directly.</p>
     *
     * @param pathname The path name component for which to construct a URL.
     * @return The appropriate file: URL.
     * @throws MalformedURLException if the pathname can't be turned into
     *                               a proper URL.
     */
    public static URL makeURL(String pathname) throws MalformedURLException {
        if (pathname.startsWith("/")) {
            return new URL("file://" + pathname);
        }

        String userdir = System.getProperty("user.dir");
        userdir = userdir.replace('\\', '/');

        if (userdir.endsWith("/")) {
            return new URL("file:///" + userdir + pathname);
        } else {
            return new URL("file:///" + userdir + "/" + pathname);
        }
    }
}

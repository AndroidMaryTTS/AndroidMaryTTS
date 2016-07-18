/**
 * Copyright 2009 DFKI GmbH.
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

package marytts.tools.install;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A central book-keeping place for the licenses referenced by installable components.
 * Licenses are identified by their URL and kept in local files if they have been "used" before.
 * A license gets "used" in particular when the user attempts to download a component goverened
 * by that license.
 *
 * @author marc
 */
public class LicenseRegistry {

    private static Map<URL, String> remote2local = null;

    /**
     * For the license identified by the given URL,
     * return the URL of a local file providing the
     * same content as the given URL. If the license has not
     * been downloaded yet, it will be now.
     *
     * @param licenseURL the remote URL of the license, serving as the license's identifier.
     * @return the URL of a local file from which the license text can be read even if there is no internet connection.
     */
    public static URL getLicense(URL licenseURL) {
        long startT = System.currentTimeMillis();
        if (remote2local == null) {
            loadLocalLicenses();
        }
        assert remote2local != null;
        if (!remote2local.containsKey(licenseURL)) {
            downloadLicense(licenseURL);
        }
        String localFilename = remote2local.get(licenseURL);
        File downloadDir = new File(System.getProperty("mary.downloadDir", "."));
        File localFile = new File(downloadDir, localFilename);
        try {
            URL localURL = localFile.toURI().toURL();
            System.out.println("Lookup took " + (System.currentTimeMillis() - startT) + " ms");
            return localURL;
        } catch (MalformedURLException e) {
            System.err.println("Cannot create URL from local file " + localFile.getAbsolutePath());
            e.printStackTrace();
        }
        return null;
    }


    private static void loadLocalLicenses() {
        remote2local = new HashMap<URL, String>();
        File downloadDir = new File(System.getProperty("mary.downloadDir", "."));
        File licenseIndexFile = new File(downloadDir, "license-index.txt");
        if (!licenseIndexFile.canRead()) {
            return; // nothing to load
        }
        try {
            // Each line in licenseIndexFile is expected to be a pair of local file name (relative to downloadDir) and URL string, separated by a | (pipe) character.
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(licenseIndexFile), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                StringTokenizer st = new StringTokenizer(line, "|");
                if (!st.hasMoreTokens()) {
                    continue; // skip empty lines
                }
                String localFilename = st.nextToken().trim();
                if (!st.hasMoreTokens()) {
                    continue; // skip lines that don't contain a |
                }
                String remoteURLString = st.nextToken().trim();
                File localLicenseFile = new File(downloadDir, localFilename);
                if (!localLicenseFile.canRead()) {
                    System.err.println("License index file " + licenseIndexFile.getAbsolutePath() + " refers to license file " + localLicenseFile.getAbsolutePath() + ", but that file cannot be read. Skipping.");
                    continue;
                }
                URL remoteURL = new URL(remoteURLString);
                remote2local.put(remoteURL, localFilename);
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Problem reading local license index file " + licenseIndexFile.getAbsolutePath() + ":");
            e.printStackTrace();
        }
    }


    private static void downloadLicense(URL licenseURL) {
        assert remote2local != null;
        File downloadDir = new File(System.getProperty("mary.downloadDir", "."));
        String filename = licenseURL.toString().replace('/', '_').replace(':', '_');
        File licenseFile = new File(downloadDir, filename);
        System.out.println("Downloading license from " + licenseURL.toString());
        try {

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(licenseFile));
            BufferedInputStream in = new BufferedInputStream(licenseURL.openStream());
            byte[] buf = new byte[4096];
            int n = -1;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Cannot download license from " + licenseURL.toString());
            e.printStackTrace();
        }

        // Now we need to update remote2local and write an updated license-index.txt:
        remote2local.put(licenseURL, filename);
        saveIndex();
    }

    private static void saveIndex() {
        assert remote2local != null;
        File downloadDir = new File(System.getProperty("mary.downloadDir", "."));
        File licenseIndexFile = new File(downloadDir, "license-index.txt");
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(licenseIndexFile), "UTF-8"));
            for (URL remote : remote2local.keySet()) {
                pw.println(remote2local.get(remote) + "|" + remote.toString());
            }
            pw.close();
        } catch (IOException e) {
            System.err.println("Problem updating the index file " + licenseIndexFile.getAbsolutePath());
            e.printStackTrace();
        }

    }


}


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
package marytts;

import android.util.Log;

import com.marytts.android.link.MaryLink;

import java.io.IOException;
import java.io.InputStream;

import marytts.server.Mary;
import marytts.util.io.FileUtils;


/**
 * Provide Version information for the Mary server and client.
 *
 * @author Marc Schr&ouml;der
 */
public class Version {
    private static String specificationVersion;
    private static String implementationVersion;

    static {
//        InputStream specVersionStream = Version.class.
//            getResourceAsStream("specification-version.txt");
//
        InputStream specVersionStream = null;
        try {
            specVersionStream = MaryLink.getContext().getAssets().open("marytts/specification-version.txt");
        } catch (Exception e) {
            Log.i(Mary.LOG, e.toString());
        }

        if (specVersionStream != null) {
            try {
                specificationVersion = FileUtils.getStreamAsString(specVersionStream, "UTF-8").trim();
            } catch (IOException e) {
                specificationVersion = "undeterminable";
            }
        } else {
            specificationVersion = "unknown";
        }


//        InputStream implVersionStream = Version.class.
//            getResourceAsStream("implementation-version.txt");


        InputStream implVersionStream = null;
        try {
            implVersionStream = MaryLink.getContext().getAssets().open("marytts/implementation-version.txt");
        } catch (Exception e) {
            Log.i(Mary.LOG, e.toString());
        }


        if (implVersionStream != null) {
            try {
                implementationVersion = FileUtils.getStreamAsString(implVersionStream, "UTF-8").trim();
            } catch (IOException e) {
                implementationVersion = "undeterminable";
            }
        } else {
            implementationVersion = "unknown";
        }
    }

    /**
     * Specification version
     */
    public static String specificationVersion() {
        return specificationVersion;
    }

    /**
     * Implementation version
     */
    public static String implementationVersion() {
        return implementationVersion;
    }

}


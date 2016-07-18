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

package de.dfki.lt.tools.tokenizer;

import android.util.Log;

import com.marytts.android.link.MaryLink;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import marytts.server.Mary;


public final class FileTools {

    // would create a new instance of {@link FileTools}; not to be used
    private FileTools() {
        // private constructor to enforce noninstantiability
    }


    public static void readInputStream(OutputStream os, InputStream is)
            throws IOException {

        byte[] buffer = new byte[4096];
        int readb;
        while (true) {
            readb = is.read(buffer);
            if (readb == -1) {
                break;
            }
            os.write(buffer, 0, readb);
        }
    }


    /**
     * Reads some input stream and return its content as a string.
     *
     * @param is       the input stream
     * @param encoding the encoding to use
     * @return the content of the stream as string or {@code null} if content could not be read
     * @throws IOException if there is an error when reading the stream
     */
    public static String readInputStream(InputStream is, String encoding)
            throws IOException {

        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            readInputStream(bos, is);
        } finally {
            // always close
            if (null != bos) {
                bos.close();
            }
        }

        if (null != bos) {
            return bos.toString(encoding);
        }
        return null;
    }


    /**
     * Reads some input stream and return its content as byte array.
     *
     * @param is the input stream
     * @return the content of the stream as byte array or {@code null} if content could not be read
     * @throws IOException if there is an error when reading the stream
     */
    public static byte[] readInputStreamToByteArray(InputStream is)
            throws IOException {

        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            readInputStream(bos, is);
        } finally {
            // always close
            if (null != bos) {
                bos.close();
            }
        }

        if (null != bos) {
            return bos.toByteArray();
        }
        return null;
    }


    /**
     * Recursively collects all filenames in the given directory with the given suffix and returns
     * them in a list.
     *
     * @param directory the directory name
     * @param suffix    the filename suffix
     * @return a list with the filenames
     */
    public static List<String> getFilesFromDir(String directory, String suffix) {

        // initialize result list
        List<String> fileNames = new ArrayList<>();

        // add file separator to directory if necessary
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }

        // check if input is an directory
        File dirFile = new File(directory);
        if (!dirFile.isDirectory()) {
            return fileNames;
        }

        // iterate over files in directory
        File[] filesInDir = dirFile.listFiles();
        for (int i = 0; i < filesInDir.length; i++) {
            // if file is a directory, collect recursivly
            if (filesInDir[i].isDirectory()) {
                fileNames.addAll(getFilesFromDir(filesInDir[i].getAbsolutePath(), suffix));
            } else if (filesInDir[i].getName().endsWith(suffix)) {
                // otherwise, add filename with matching suffix to result list
                fileNames.add(filesInDir[i].getAbsolutePath());
            }
        }
        return fileNames;
    }


    /**
     * Copies a source file to a target file.
     *
     * @param source the source file to copy
     * @param target the target file
     * @throws IOException if copying fails
     */
    public static void copyFile(File source, File target)
            throws IOException {

        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(target);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }


    /**
     * New NIO based method to read the contents of a file as byte array. Only files up to size
     * Integer.MAX_INT can be read. The byte buffer is rewinded when returned.
     *
     * @param file the file to read
     * @return the file content as byte array
     * @throws IOException if reading the content fails
     */
    public static ByteBuffer readFile(File file)
            throws IOException {

        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        // for some reason, the buffer must be 1 byte bigger than the file
        ByteBuffer readBuffer = ByteBuffer.allocate((int) fc.size());
        fc.read(readBuffer);
        fis.close();
        // also closes channel
        readBuffer.rewind();

        return readBuffer;
    }


    /**
     * New NIO based method to read a file as a String with the given encoding.
     *
     * @param file     the file to read
     * @param encoding the encoding to use for conversion, if {@code null} UTF-8 is used
     * @return the file content as string
     * @throws IOException if there is an error when reading the file
     */
    public static String readFileAsString(File file, String encoding)
            throws IOException {

        ByteBuffer buffer = readFile(file);
        if (null == encoding) {
            encoding = "UTF-8";
        }
        String converted = new String(buffer.array(), encoding);
        return converted;
    }


    /**
     * Returns an input stream for the given resource file.
     *
     * @param resourceFileName the resource name
     * @return an input stream where to read from the content of the resource file
     * @throws IOException if there is an error when reading the resource
     */
    public static InputStream openResourceFileAsStream(String resourceFileName) {
        if (resourceFileName.equals("jtok/enen_b-abbrev.txt"))
            resourceFileName = "jtok/en/en_b-abbrev.txt";
        // Log.d(Mary.LOG,"openResourceFileAsStream, resourceFileName= "+resourceFileName);
        // first check for any user specific configuration in 'jtok-user'
        InputStream is = null;
        try {
            is = MaryLink.getContext().getAssets().open(resourceFileName);
            if (null == is) {
                // try local loader with absolute path
                is = MaryLink.getContext().getAssets().open("/" + resourceFileName);
                if (null == is) {
                    // try local loader, relative, just in case
                    is = MaryLink.getContext().getAssets().open(resourceFileName);
                    if (null == is) {
                        // can't find it on classpath, so try relative to current directory
                        // this will throw security exception under and applet but there's
                        // no other choice left
                        is = new FileInputStream(resourceFileName);
                    }
                }
            }

        } catch (Exception e) {
            Log.d(Mary.LOG, "openResourceFileAsStream, CANN'T open file=" + resourceFileName);
        }


        return is;
    }
}

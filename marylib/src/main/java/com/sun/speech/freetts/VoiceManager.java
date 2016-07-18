/**
 * Copyright 2003 Sun Microsystems, Inc.
 * <p/>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
package com.sun.speech.freetts;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.jar.Attributes;

/**
 * Provides access to voices for all of FreeTTS.  There is only one
 * instance of the VoiceManager.
 * <p/>
 * Each call to getVoices() creates a new instance of each voice.
 *
 * @see Voice
 * @see VoiceDirectory
 */
public class VoiceManager {

    private static final VoiceManager INSTANCE =
            new VoiceManager();

    private static final String fileSeparator =
            System.getProperty("file.separator");

    private static final String pathSeparator =
            System.getProperty("path.separator");

    // we only want one class loader, otherwise the static information
    // for loaded classes would be duplicated for each class loader
    private static final DynamicClassLoader classLoader =
            new DynamicClassLoader(new URL[0]);

    private VoiceManager() {
        // do nothing
    }

    /**
     * Gets the instance of the VoiceManager
     *
     * @return a VoiceManager
     */
    public static VoiceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the class loader used for loading dynamically detected
     * jars.  This is useful to get resources out of jars that may be
     * in the class path of this class loader but not in the class
     * path of the system class loader.
     *
     * @return the class loader
     */
    public static URLClassLoader getVoiceClassLoader() {
        return classLoader;
    }

    /**
     * Provide an array of all voices available to FreeTTS.
     * <p/>
     * First, if the "freetts.voices" property is set, it is assumed
     * to be a comma-separated list of VoiceDirectory classnames
     * (e.g.,
     * "-Dfreetts.voices=com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory").
     * If this property exists, the VoiceManager will use only
     * this property to find voices -- no other method described below
     * will be used.  The primary purpose for this property is testing
     * and for use with WebStart.
     * <p/>
     * <p>Second, the file internal_voices.txt is looked for in the
     * same directory as VoiceManager.class.  If the file does not
     * exist, the VoiceManager moves on.  Next, it looks for
     * voices.txt in the same directory as freetts.jar.  If the file
     * does not exist, the VoiceManager moves on.  Next, if the
     * property "freetts.voicesfile" is defined, then that file is
     * read in.  If the property is defined and the file does not
     * exist, then an error is raised.
     * <p/>
     * <P>Every voices file that is read in contains a list of
     * VoiceDirectory class names.
     * <p/>
     * <p>Next, the voice manager looks for freetts voice jarfiles that
     * may exist in well-known locations.  The directory that contains
     * freetts.jar is searched for voice jarfiles, then directories
     * specified by the "freetts.voicespath" system property.
     * Any jarfile whose Manifest contains
     * "FreeTTSVoiceDefinition: true" is assumed to be a FreeTTS
     * voice, and the Manifest's "Main-Class" entry is assumed to be
     * the name of the voice directory.  The dependencies of the voice
     * jarfiles specified by the "Class-Path" Manifest entry are also
     * loaded.
     * <p/>
     * <p>The VoiceManager instantiates each voice directory
     * and calls getVoices() on each.
     *
     * @return the array of new instances of all available voices
     */
    public Voice[] getVoices() {
        UniqueVector voices = new UniqueVector();
        VoiceDirectory[] voiceDirectories = getVoiceDirectories();
        for (int i = 0; i < voiceDirectories.length; i++) {
            voices.addArray(voiceDirectories[i].getVoices());
        }

        Voice[] voiceArray = new Voice[voices.size()];
        return (Voice[]) voices.toArray(voiceArray);
    }

    /**
     * Prints detailed information about all available voices.
     *
     * @return a String containing the information
     */
    public String getVoiceInfo() {
        String infoString = "";
        VoiceDirectory[] voiceDirectories = getVoiceDirectories();
        for (int i = 0; i < voiceDirectories.length; i++) {
            infoString += voiceDirectories[i].toString();
        }
        return infoString;
    }

    /**
     * Creates an array of all voice directories of all available
     * voices using the criteria specified by the contract for
     * getVoices().
     *
     * @return the array of voice directories
     * @see getVoices()
     */
    private VoiceDirectory[] getVoiceDirectories() {
        try {
            // If there is a freetts.voices property, it means two
            // things:  1) it is a comma separated list of class names
            //          2) no other attempts to find voices should be
            //             made
            //
            // The main purpose for this property is to allow for
            // voices to be found via WebStart.
            //
            //#####################
            //String voiceClasses = System.getProperty("freetts.voices");
            String voiceClasses = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
            //#####################
            if (voiceClasses != null) {
                return getVoiceDirectoryNamesFromProperty(voiceClasses);
            }

            // Get voice directory names from voices files
            UniqueVector voiceDirectoryNames =
                    getVoiceDirectoryNamesFromFiles();

            System.out.println("TEST: got " +
                    voiceDirectoryNames.size() + " names from voices files");

            // Get list of voice jars
            UniqueVector pathURLs = getVoiceJarURLs();
            voiceDirectoryNames.addVector(
                    getVoiceDirectoryNamesFromJarURLs(pathURLs));

            //TEST
            for (int i = 0; i < voiceDirectoryNames.size(); i++) {
                System.out.println("TEST vd: " + voiceDirectoryNames.get(i));
            }

            // Get dependencies
            // Copy of vector made because vector may be modified by
            // each call to getDependencyURLs
            URL[] voiceJarURLs = (URL[])
                    pathURLs.toArray(new URL[pathURLs.size()]);
            for (int i = 0; i < voiceJarURLs.length; i++) {
                getDependencyURLs(voiceJarURLs[i], pathURLs);
            }

            // Extend class path
            for (int i = 0; i < pathURLs.size(); i++) {
                classLoader.addUniqueURL((URL) pathURLs.get(i));
            }

            //TEST
            System.out.println("TEST ClassLoader Path:");
            URL[] classPath = classLoader.getURLs();
            for (int i = 0; i < classPath.length; i++) {
                System.out.println(classPath[i]);
            }

            // Create an instance of each voice directory
            UniqueVector voiceDirectories = new UniqueVector();
            for (int i = 0; i < voiceDirectoryNames.size(); i++) {
                System.out.println("TEST About to try and load " + voiceDirectoryNames.get(i));
                Class c = Class.forName((String) voiceDirectoryNames.get(i),
                        true, classLoader);
                voiceDirectories.add(c.newInstance());
            }

            return (VoiceDirectory[]) voiceDirectories.toArray(new
                    VoiceDirectory[voiceDirectories.size()]);
        } catch (InstantiationException e) {
            throw new Error("Unable to load voice directory. " + e);
        } catch (ClassNotFoundException e) {
            throw new Error("Unable to load voice directory. " + e);
        } catch (IllegalAccessException e) {
            throw new Error("Unable to load voice directory. " + e);
        }

    }

    /**
     * Gets VoiceDirectory instances by parsing a comma separated
     * String of VoiceDirectory class names.
     */
    private VoiceDirectory[] getVoiceDirectoryNamesFromProperty(
            String voiceClasses)
            throws InstantiationException,
            IllegalAccessException,
            ClassNotFoundException {

        String[] classnames = voiceClasses.split(",");

        VoiceDirectory[] directories = new VoiceDirectory[classnames.length];

        for (int i = 0; i < directories.length; i++) {
            Class c = Class.forName(classnames[i]);
            directories[i] = (VoiceDirectory) c.newInstance();
        }

        return directories;
    }

    /**
     * Recursively gets the urls of the class paths that url is
     * dependant on.
     * <p/>
     * Conventions specified in
     * http://java.sun.com/j2se/1.4.1/docs/guide/extensions/spec.html#bundled
     * are followed.
     *
     * @param url            the url to recursively check.  If it ends with a "/"
     *                       then it is presumed to be a directory, and is not checked.
     *                       Otherwise it is assumed to be a jar, and its manifest is read
     *                       to get the urls Class-Path entry.  These urls are passed to
     *                       this method recursively.
     * @param dependencyURLs a vector containing all of the dependant
     *                       urls found.  This parameter is modified as urls are added to
     *                       it.
     */
    private void getDependencyURLs(URL url, UniqueVector dependencyURLs) {
        try {
            String urlDirName = getURLDirName(url);
            if (url.getProtocol().equals("jar")) { // only check deps of jars

                // read in Class-Path attribute of jar Manifest
                JarURLConnection jarConnection =
                        (JarURLConnection) url.openConnection();
                Attributes attributes = jarConnection.getMainAttributes();
                String fullClassPath =
                        attributes.getValue(Attributes.Name.CLASS_PATH);
                if (fullClassPath == null || fullClassPath.equals("")) {
                    return; // no classpaths to add
                }

                // The URLs are separated by one or more spaces
                String[] classPath = fullClassPath.split("\\s+");
                URL classPathURL;
                for (int i = 0; i < classPath.length; i++) {
                    try {
                        if (classPath[i].endsWith("/")) {  // assume directory
                            classPathURL = new URL("file:" + urlDirName
                                    + classPath[i]);
                        } else {                        // assume jar
                            classPathURL = new URL("jar", "", "file:" +
                                    urlDirName + classPath[i] + "!/");
                        }
                    } catch (MalformedURLException e) {
                        System.err.println(
                                "Warning: unable to resolve dependency "
                                        + classPath[i]
                                        + " referenced by " + url);
                        continue;
                    }

                    //System.out.println("TEST: adding dep url: " + classPathURL);
                    // don't get in a recursive loop if two jars
                    // are mutually dependant
                    if (!dependencyURLs.contains(classPathURL)) {
                        dependencyURLs.add(classPathURL);
                        getDependencyURLs(classPathURL, dependencyURLs);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the names of the subclasses of VoiceDirectory that are
     * listed in the voices.txt files.
     *
     * @return a vector containing the String names of the voice
     * directories
     */
    private UniqueVector getVoiceDirectoryNamesFromFiles() {
        try {
            UniqueVector voiceDirectoryNames = new UniqueVector();

            // first, load internal_voices.txt
            InputStream is =
                    this.getClass().getResourceAsStream("internal_voices.txt");
            if (is != null) { // if it doesn't exist, move on
                voiceDirectoryNames.addVector(
                        getVoiceDirectoryNamesFromInputStream(is));
            }

            // next, try loading voices.txt
            try {
                voiceDirectoryNames.addVector(
                        getVoiceDirectoryNamesFromFile(getBaseDirectory()
                                + "voices.txt"));
            } catch (FileNotFoundException e) {
                // do nothing
            } catch (IOException e) {
                // do nothing
            }

            // last, read voices from property freetts.voicesfile
            String voicesFile = System.getProperty("freetts.voicesfile");
            if (voicesFile != null) {
                voiceDirectoryNames.addVector(
                        getVoiceDirectoryNamesFromFile(voicesFile));
            }

            return voiceDirectoryNames;
        } catch (IOException e) {
            throw new Error("Error reading voices files. " + e);
        }
    }

    /**
     * Gets the voice directory class names from a list of urls
     * specifying voice jarfiles.  The class name is specified as the
     * Main-Class in the manifest of the jarfiles.
     *
     * @param urls a UniqueVector of URLs that refer to the voice jarfiles
     * @return a UniqueVector of Strings representing the voice directory
     * class names
     */
    private UniqueVector getVoiceDirectoryNamesFromJarURLs(UniqueVector urls) {
        try {
            UniqueVector voiceDirectoryNames = new UniqueVector();
            for (int i = 0; i < urls.size(); i++) {
                //System.out.println("TEST: reading manifest of " +
                //        (URL)urls.get(i));
                JarURLConnection jarConnection =
                        (JarURLConnection) ((URL) urls.get(i)).openConnection();
                Attributes attributes = jarConnection.getMainAttributes();
                String mainClass =
                        attributes.getValue(Attributes.Name.MAIN_CLASS);
                //System.out.println("TEST: Main-Class: " + mainClass);
                if (mainClass == null || mainClass.trim().equals("")) {
                    throw new Error("No Main-Class found in jar "
                            + urls.get(i));
                }

                voiceDirectoryNames.add(mainClass);
            }
            return voiceDirectoryNames;
        } catch (IOException e) {
            throw new Error("Error reading jarfile manifests. ");
        }
    }

    /**
     * Gets the list of voice jarfiles.  Voice jarfiles are searched
     * for in the same directory as freetts.jar and the directories
     * specified by the freetts.voicespath system property.  Voice
     * jarfiles are defined by the manifest entry
     * "FreeTTSVoiceDefinition: true"
     *
     * @return a vector of URLs refering to the voice jarfiles.
     */
    private UniqueVector getVoiceJarURLs() {
        UniqueVector voiceJarURLs = new UniqueVector();

        // check in same directory as freetts.jar
        try {
            String baseDirectory = getBaseDirectory();
            if (!baseDirectory.equals("")) {  // not called from a jar
                voiceJarURLs.addVector(getVoiceJarURLsFromDir(baseDirectory));
            }
        } catch (FileNotFoundException e) {
            // do nothing
        }

        // search voicespath
        String voicesPath = System.getProperty("freetts.voicespath", "");
        if (!voicesPath.equals("")) {
            String[] dirNames = voicesPath.split(pathSeparator);
            for (int i = 0; i < dirNames.length; i++) {
                try {
                    //System.out.println("TEST: adding voicepath " + dirNames[i]);
                    voiceJarURLs.addVector(getVoiceJarURLsFromDir(dirNames[i]));
                } catch (FileNotFoundException e) {
                    throw new Error("Error loading jars from voicespath "
                            + dirNames[i] + ". ");
                }
            }
        }

        return voiceJarURLs;
    }

    /**
     * Gets the list of voice jarfiles in a specific directory.
     *
     * @return a vector of URLs refering to the voice jarfiles
     * @see getVoiceJarURLs()
     */
    private UniqueVector getVoiceJarURLsFromDir(String dirName)
            throws FileNotFoundException {
        try {
            UniqueVector voiceJarURLs = new UniqueVector();
            File dir = new File(new URI("file://" + dirName));
            if (!dir.isDirectory()) {
                throw new FileNotFoundException("File is not a directory: "
                        + dirName);
            }
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                //System.out.println("TEST: checking url " + files[i].getName());
                if (files[i].isFile() && (!files[i].isHidden()) &&
                        files[i].getName().endsWith(".jar")) {
                    URL jarURL = files[i].toURL();
                    jarURL = new URL("jar", "",
                            "file:" + jarURL.getPath() + "!/");
                    //System.out.println("TEST: reading url " + jarURL);
                    JarURLConnection jarConnection = (JarURLConnection)
                            jarURL.openConnection();
                    // if it is not a real jar file, we will end up
                    // with a null set of attributes.

                    Attributes attributes = jarConnection.getMainAttributes();
                    if (attributes != null) {
                        String isVoice =
                                attributes.getValue("FreeTTSVoiceDefinition");
                        if (isVoice != null && isVoice.trim().equals("true")) {
                            voiceJarURLs.add(jarURL);
                        }
                    }
                }
            }
            return voiceJarURLs;
        } catch (java.net.URISyntaxException e) {
            throw new Error("Error reading directory name '" + dirName + "'.");
        } catch (MalformedURLException e) {
            throw new Error("Error reading jars from directory "
                    + dirName + ". ");
        } catch (IOException e) {
            throw new Error("Error reading jars from directory "
                    + dirName + ". ");
        }
    }

    /**
     * Provides a string representation of all voices available to
     * FreeTTS.
     *
     * @return a String which is a space-delimited list of voice
     * names.  If there is more than one voice, then the word "or"
     * appears before the last one.
     */
    public String toString() {
        String names = "";
        Voice[] voices = getVoices();
        for (int i = 0; i < voices.length; i++) {
            if (i == voices.length - 1) {
                if (i == 0) {
                    names = voices[i].getName();
                } else {
                    names += "or " + voices[i].getName();
                }
            } else {
                names += voices[i].getName() + " ";
            }
        }
        return names;
    }

    /**
     * Check if there is a voice provides with the given name.
     *
     * @param voiceName the name of the voice to check
     * @return <b>true</b> if FreeTTS has a voice available with the
     * name <b>voiceName</b>, else <b>false</b>.
     */
    public boolean contains(String voiceName) {
        return (getVoice(voiceName) != null);
    }

    /**
     * Get a Voice with a given name.
     *
     * @param voiceName the name of the voice to get.
     * @return the Voice that has the same name as <b>voiceName</b>
     * if one exists, else <b>null</b>
     */
    public Voice getVoice(String voiceName) {
        Voice[] voices = getVoices();
        for (int i = 0; i < voices.length; i++) {
            if (voices[i].getName().equals(voiceName)) {
                return voices[i];
            }
        }
        return null;
    }

    /**
     * Get the directory that the jar file containing this class
     * resides in.
     *
     * @return the name of the directory with a trailing "/" (or
     * equivalent for the particular operating system), or "" if
     * unable to determin.  (For example this class does not reside
     * inside a jar file).
     */
    private String getBaseDirectory() {
        String name = this.getClass().getName();
        int lastdot = name.lastIndexOf('.');
        if (lastdot != -1) {  // remove package information
            name = name.substring(lastdot + 1);
        }

        URL url = this.getClass().getResource(name + ".class");
        return getURLDirName(url);
    }

    /**
     * Gets the directory name from a URL
     *
     * @param url the url to parse
     * @return the String representation of the directory name in a
     * URL
     */
    private String getURLDirName(URL url) {
        String urlFileName = url.getPath();
        int i = urlFileName.lastIndexOf('!');
        if (i == -1) {
            i = urlFileName.length();
        }
        int dir = urlFileName.lastIndexOf("/", i);
        if (!urlFileName.startsWith("file:")) {
            return "";
        }
        return urlFileName.substring(5, dir) + "/";
    }

    /**
     * Get the names of the voice directories from a voices file.
     * Blank lines and lines beginning with "#" are ignored.
     * Beginning and trailing whitespace is ignored.
     *
     * @param fileName the name of the voices file to read from
     * @return a vector of the names of the VoiceDirectory subclasses
     * @throws FileNotFoundException
     * @throws IOException
     */
    private UniqueVector getVoiceDirectoryNamesFromFile(String fileName)
            throws IOException {
        InputStream is = new FileInputStream(fileName);
        if (is == null) {
            throw new IOException();
        } else {
            return getVoiceDirectoryNamesFromInputStream(is);
        }
    }

    /**
     * Get the names of the voice directories from an input stream.
     * Blank lines and lines beginning with "#" are ignored.
     * Beginning and trailing whitespace is ignored.
     *
     * @param is the input stream to read from
     * @return a vector of the names of the VoiceDirectory subclasses
     * @throws IOException
     */
    private UniqueVector getVoiceDirectoryNamesFromInputStream(InputStream is)
            throws IOException {
        UniqueVector names = new UniqueVector();
        BufferedReader reader = new
                BufferedReader(new InputStreamReader(is));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (!line.startsWith("#") && !line.equals("")) {
                //System.out.println("TEST Adding vd " + line);
                names.add(line);
            }
        }
        return names;
    }
}

/**
 * The DynamicClassLoader provides a means to add urls to the
 * classpath after the class loader has already been instantiated.
 */
class DynamicClassLoader extends URLClassLoader {

    private java.util.HashSet classPath;

    /**
     * Constructs a new URLClassLoader for the specified URLs using
     * the default delegation parent ClassLoader. The URLs will be
     * searched in the order specified for classes and resources
     * after first searching in the parent class loader. Any URL
     * that ends with a '/' is assumed to refer to a directory.
     * Otherwise, the URL is assumed to refer to a JAR file which
     * will be downloaded and opened as needed.
     * <p/>
     * If there is a security manager, this method first calls the
     * security manager's checkCreateClassLoader method to ensure
     * creation of a class loader is allowed.
     *
     * @param urls the URLs from which to load classes and resources
     * @throws SecurityException if a security manager exists and
     *                           its checkCreateClassLoader method doesn't allow creation of a
     *                           class loader.
     */
    public DynamicClassLoader(URL[] urls) {
        super(urls);
        classPath = new java.util.HashSet(urls.length);
        for (int i = 0; i < urls.length; i++) {
            classPath.add(urls[i]);
        }
    }

    /**
     * Constructs a new URLClassLoader for the given URLs. The
     * URLs will be searched in the order specified for classes
     * and resources after first searching in the specified parent
     * class loader. Any URL that ends with a '/' is assumed to refer
     * to a directory. Otherwise, the URL is assumed to refer to a
     * JAR file which will be downloaded and opened as needed.
     * <p/>
     * If there is a security manager, this method first calls the
     * security manager's checkCreateClassLoader method to ensure
     * creation of a class loader is allowed.
     *
     * @param urls   the URLs from which to load classes and resources
     * @param parent the parent class loader for delegation
     * @throws SecurityException if a security manager exists and
     *                           its checkCreateClassLoader method doesn't allow creation of a
     *                           class loader.
     */
    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        classPath = new java.util.HashSet(urls.length);
        for (int i = 0; i < urls.length; i++) {
            classPath.add(urls[i]);
        }
    }

    /**
     * Constructs a new URLClassLoader for the specified URLs, parent
     * class loader, and URLStreamHandlerFactory. The parent argument
     * will be used as the parent class loader for delegation. The
     * factory argument will be used as the stream handler factory to
     * obtain protocol handlers when creating new URLs.
     * <p/>
     * If there is a security manager, this method first calls the
     * security manager's checkCreateClassLoader method to ensure
     * creation of a class loader is allowed.
     *
     * @param urls    the URLs from which to load classes and resources
     * @param parent  the parent class loader for delegation
     * @param factory the URLStreamHandlerFactory to use when creating URLs
     * @throws SecurityException if a security manager exists and
     *                           its checkCreateClassLoader method doesn't allow creation of a
     *                           class loader.
     */
    public DynamicClassLoader(URL[] urls, ClassLoader parent,
                              URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        classPath = new java.util.HashSet(urls.length);
        for (int i = 0; i < urls.length; i++) {
            classPath.add(urls[i]);
        }
    }

    /**
     * Add a URL to a class path only if has not already been added.
     *
     * @param url the url to add to the class path
     */
    public synchronized void addUniqueURL(URL url) {
        if (!classPath.contains(url)) {
            super.addURL(url);
            classPath.add(url);
        }
    }
}

/**
 * Provides a vector whose elements are always unique.  The
 * advantage over a Set is that the elements are still ordered
 * in the way they were added.  If an element is added that
 * already exists, then nothing happens.
 */
class UniqueVector {
    private java.util.HashSet elementSet;
    private java.util.Vector elementVector;

    /**
     * Creates a new vector
     */
    public UniqueVector() {
        elementSet = new java.util.HashSet();
        elementVector = new java.util.Vector();
    }

    /**
     * Add an object o to the vector if it is not already present as
     * defined by the function HashSet.contains(o)
     *
     * @param o the object to add
     */
    public void add(Object o) {
        if (!contains(o)) {
            elementSet.add(o);
            elementVector.add(o);
        }
    }

    /**
     * Appends all elements of a vector to this vector.
     * Only unique elements are added.
     *
     * @param v the vector to add
     */
    public void addVector(UniqueVector v) {
        for (int i = 0; i < v.size(); i++) {
            add(v.get(i));
        }
    }

    /**
     * Appends all elements of an array to this vector.
     * Only unique elements are added.
     *
     * @param a the array to add
     */
    public void addArray(Object[] a) {
        for (int i = 0; i < a.length; i++) {
            add(a[i]);
        }
    }

    /**
     * Gets the number of elements currently in vector.
     *
     * @return the number of elements in vector
     */
    public int size() {
        return elementVector.size();
    }

    /**
     * Checks if an element is present in the vector.  The check
     * follows the convention of HashSet contains() function, so
     * performance can be expected to be a constant factor.
     *
     * @param o the object to check
     * @return true if element o exists in the vector, else false.
     */
    public boolean contains(Object o) {
        return elementSet.contains(o);
    }

    /**
     * Gets an element from a vector.
     *
     * @param index the index into the vector from which to retrieve
     *              the element
     * @return the object at index <b>index</b>
     */
    public Object get(int index) {
        return elementVector.get(index);
    }

    /**
     * Creates an array of the elements in the vector.  Follows
     * conventions of Vector.toArray().
     *
     * @return an array representation of the object
     */
    public Object[] toArray() {
        return elementVector.toArray();
    }

    /**
     * Creates an array of the elements in the vector.  Follows
     * conventions of Vector.toArray(Object[]).
     *
     * @return an array representation of the object
     */
    public Object[] toArray(Object[] a) {
        return elementVector.toArray(a);
    }
}

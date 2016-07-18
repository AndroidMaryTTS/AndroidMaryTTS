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
package marytts.server;

// General Java Classes

import android.util.Log;

import com.marytts.android.link.MaryLink;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;

import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioSystem;
import marytts.Version;
import marytts.datatypes.MaryDataType;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.NoSuchPropertyException;
import marytts.features.FeatureProcessorManager;
import marytts.features.FeatureRegistry;
import marytts.modules.MaryModule;
import marytts.modules.ModuleRegistry;
import marytts.modules.Synthesis;
import marytts.modules.synthesis.Voice;
import marytts.util.MaryCache;
import marytts.util.MaryRuntimeUtils;
import marytts.util.MaryUtils;
import marytts.util.Pair;
import marytts.util.data.audio.MaryAudioUtils;
import marytts.util.io.FileUtils;

//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;


/**
 * The main program for the mary TtS system.
 * It can run as a socket server or as a stand-alone program.
 *
 * @author Marc Schr&ouml;der
 */

public class Mary {
    public static final int STATE_OFF = 0;
    public static final int STATE_STARTING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_SHUTTING_DOWN = 3;

    // private static Logger logger;
    public static String LOG = "test";

    private static int currentState = STATE_OFF;
    private static boolean jarsAdded = false;

    public Mary() {
    }

    /**
     * Inform about system state.
     *
     * @return an integer representing the current system state.
     * @see #STATE_OFF
     * @see #STATE_STARTING
     * @see #STATE_RUNNING
     * @see #STATE_SHUTTING_DOWN
     */
    public static int currentState() {
        return currentState;
    }

    /**
     * Add jars to classpath. Normally this is called from startup().
     *
     * @throws Exception
     */
    protected static void addJarsToClasspath() throws Exception {
        if (true) return;
        // TODO: clean this up when the new modularity mechanism is in place
        if (jarsAdded) return; // have done this already
        File jarDir = new File(MaryProperties.maryBase() + "/java");
        File[] jarFiles = jarDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        assert jarFiles != null;
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        for (int i = 0; i < jarFiles.length; i++) {
            URL jarURL = new URL("file:" + jarFiles[i].getPath());
            method.invoke(sysloader, jarURL);
        }
        jarsAdded = true;
    }

    private static void startModules()
            throws Exception {

        for (String moduleClassName : MaryProperties.moduleInitInfo()) {
             Log.d(Mary.LOG,"moduleInitInfo __ "+moduleClassName);
            MaryModule m = ModuleRegistry.instantiateModule(moduleClassName);

            // Partially fill module repository here; 
            // TODO: voice-specific entries will be added when each voice is loaded.
            ModuleRegistry.registerModule(m, m.getLocale(), null);
            // Log.d(Mary.LOG,m+" module regustered");
        }

        ModuleRegistry.setRegistrationComplete();

        List<Pair<MaryModule, Long>> startupTimes = new ArrayList<Pair<MaryModule, Long>>();

        //  Log.d(Mary.LOG,"Startup complete.");
        // Separate loop for startup allows modules to cross-reference to each
        // other via Mary.getModule(Class) even if some have not yet been
        // started.
        for (MaryModule m : ModuleRegistry.getAllModules()) {
            // Only start the modules here if in server mode:
            if (((!MaryProperties.getProperty("server").equals("commandline")) || m instanceof Synthesis)
                    && m.getState() == MaryModule.MODULE_OFFLINE) {
                long before = System.currentTimeMillis();
                try {
                    Log.d(Mary.LOG, m.name() + " starting");
                    m.startup();


                } catch (Throwable t) {
                    Log.d(Mary.LOG, new Exception("Problem starting module " + m.toString(), t).toString());
                    throw new Exception("Problem starting module " + m.name(), t);
                }


                long after = System.currentTimeMillis();
                startupTimes.add(new Pair<MaryModule, Long>(m, after - before));
            }
//            if (MaryProperties.getAutoBoolean("modules.poweronselftest", false)) {
//                m.powerOnSelfTest();
//            }
        }

        if (startupTimes.size() > 0) {
            Collections.sort(startupTimes, new Comparator<Pair<MaryModule, Long>>() {
                @Override
                public int compare(Pair<MaryModule, Long> o1, Pair<MaryModule, Long> o2) {
                    return -o1.getSecond().compareTo(o2.getSecond());
                }
            });
//            logger.debug("Startup times:");
//            for (Pair<MaryModule, Long> p : startupTimes) {
//                logger.debug(p.getFirst().name()+": "+p.getSecond()+" ms");
//            }
        }
    }

    private static void setupFeatureProcessors()
            throws Exception {
        // Log.d("test", "setupFeatureProcessors loading");
        for (String fpmInitInfo : MaryProperties.getList("featuremanager.classes.list")) {

//            Log.d("test", "*** setupFeatureProcessors inside for fpmInitInfo="+fpmInitInfo);
            try {

                FeatureProcessorManager mgr = (FeatureProcessorManager) MaryRuntimeUtils.instantiateObject(fpmInitInfo);

                Locale locale = mgr.getLocale();
                if (locale != null) {
                    FeatureRegistry.setFeatureProcessorManager(locale, mgr);
                } else {
                    //  logger.debug("Setting fallback feature processor manager to '"+fpmInitInfo+"'");
                    FeatureRegistry.setFallbackFeatureProcessorManager(mgr);
                }
            } catch (Throwable t) {
                Log.d(Mary.LOG, new Exception("Cannot instantiate feature processor manager '" + fpmInitInfo + "'", t).toString());
                throw new Exception("Cannot instantiate feature processor manager '" + fpmInitInfo + "'", t);
            }
        }
        // Log.d("test", "setupFeatureProcessors loaded .....");
    }


    public static void startup() throws Exception {
        startup(true);
    }


    public static void startup(boolean addJarsToClasspath) throws Exception {
        if (currentState != STATE_OFF)
            throw new IllegalStateException("Cannot start system: it is not offline");
        currentState = STATE_STARTING;

        if (addJarsToClasspath) {
            addJarsToClasspath();
        }

        configureLogging();

//configure();
        String[] installedFilenames = MaryLink.getContext().getAssets().list("installed");
        if (installedFilenames == null) {
            Log.d(LOG, "The installed/ folder does not exist.");
        } else {
            StringBuilder installedMsg = new StringBuilder();
            for (String filename : installedFilenames) {
                if (installedMsg.length() > 0) {
                    installedMsg.append(", ");
                }
                installedMsg.append(filename);
            }
            Log.d(Mary.LOG, "Content of installed/ folder: " + installedMsg);
        }
        String[] confFilenames = MaryLink.getContext().getAssets().list("conf");
        if (confFilenames == null) {
            Log.v(LOG, "The conf/ folder does not exist.");
        } else {
            StringBuilder confMsg = new StringBuilder();
            for (String filename : confFilenames) {
                if (confMsg.length() > 0) {
                    confMsg.append(", ");
                }
                confMsg.append(filename);
            }
            Log.v(LOG, "Content of conf/ folder: " + confMsg);
        }
        Log.v(LOG, "Full dump of system properties:");
        for (Object key : new TreeSet<Object>(System.getProperties().keySet())) {
            Log.v(LOG, key + " = " + System.getProperties().get(key));
        }
        Log.v(LOG, "XML libraries used:");
        // Log.v(LOG,"DocumentBuilderFactory: " + DocumentBuilderFactory.newInstance().getClass());
        try {
            //  Class<? extends Object> xercesVersion = Class.forName("org.apache.xerces.impl.Version");
            //Log.v(LOG,xercesVersion.getMethod("getVersion").invoke(null));
        } catch (Exception e) {
            // Not xerces, no version number
        }
        // Log.v(LOG,"TransformerFactory:     " + TransformerFactory.newInstance().getClass());
        try {
            // Nov 2009, Marc: This causes "[Deprecated] Xalan: org.apache.xalan.Version" to be written to the console.
            //Class xalanVersion = Class.forName("org.apache.xalan.Version");
            //Log.v(LOG,xalanVersion.getMethod("getVersion").invoke(null));
        } catch (Exception e) {
            // Not xalan, no version number
        }

        // Essential environment checks:
        //  EnvironmentChecks.check();


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });


        setupFeatureProcessors();
        //*********************


        // Instantiate module classes and startup modules:
        startModules();

        Log.v(LOG, "Startup complete.");
        currentState = STATE_RUNNING;
    }


//
//    public static void configure() {
////    	  LogConfigurator logConfigurator = new LogConfigurator();
////    	    logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "log/file.log");
////    	    logConfigurator.setRootLevel(Level.ALL);
////    	    logConfigurator.setLevel("org.apache", Level.ALL);
////    	    logConfigurator.setUseFileAppender(true);
////    	    logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
////    	    logConfigurator.setMaxFileSize(1024 * 1024 * 5);
////    	    logConfigurator.setImmediateFlush(true);
////    	    logConfigurator.configure();
//    	    logger = Logger.getLogger(MainActivity.class);
//        
//    }

    /**
     * Log4j initialisation, called from {@link #startup(boolean)}.
     *
     * @throws NoSuchPropertyException
     * @throws IOException
     */
    private static void configureLogging() throws MaryConfigurationException, IOException {
        if (!MaryUtils.isLog4jConfigured()) { // maybe log4j has been externally configured already?
            // Configure logging:
            /*        logger = MaryUtils.getLogger("main");
                    Logger.getRootLogger().setLevel(Level.toLevel(MaryProperties.needProperty("log.level")));
                    PatternLayout layout = new PatternLayout("%d [%t] %-5p %-10c %m\n");
                    File logFile = null;
                    if (MaryProperties.needAutoBoolean("log.tofile")) {
                        String filename = MaryProperties.getFilename("log.filename", "mary.log");
                        logFile = new File(filename);
                        File parentFile = logFile.getParentFile();
                        // prevent a NullPointerException in the following conditional if the user has requested a non-existing, *relative* log filename
                        if (parentFile == null) {
                            parentFile = new File(logFile.getAbsolutePath()).getParentFile();
                        }
                        if (!(logFile.exists()&&logFile.canWrite() // exists and writable
                                || parentFile.exists() && parentFile.canWrite())) { // parent exists and writable
                            // cannot write to file
                            System.err.print("\nCannot write to log file '"+filename+"' -- ");
                            File fallbackLogFile = new File(System.getProperty("user.home")+"/mary.log");
                            if (fallbackLogFile.exists()&&fallbackLogFile.canWrite() // exists and writable 
                                    || fallbackLogFile.exists()&&fallbackLogFile.canWrite()) { // parent exists and writable
                                // fallback log file is OK
                                System.err.println("will log to '"+fallbackLogFile.getAbsolutePath()+"' instead.");
                                logFile = fallbackLogFile;
                            } else {
                                // cannot write to fallback log either
                                System.err.println("will log to standard output instead.");
                                logFile = null;
                            }
                        }
                        if (logFile != null && logFile.exists()) logFile.delete();
                    }
                    if (logFile != null) {
                        BasicConfigurator.configure(new FileAppender(layout, logFile.getAbsolutePath()));
                    } else {
                        BasicConfigurator.configure(new WriterAppender(layout, System.err));
                    }
                    */
            Properties logprops = new Properties();
            InputStream propIS = new BufferedInputStream(MaryProperties.needStream("log.config"));
            logprops.load(propIS);
            propIS.close();
            // Now replace MARY_BASE with the install location of MARY in every property:
            for (Object key : logprops.keySet()) {
                String val = (String) logprops.get(key);
                if (val.contains("MARY_BASE")) {
                    String maryBase = MaryProperties.maryBase();
                    if (maryBase.contains("\\")) {
                        maryBase = maryBase.replaceAll("\\\\", "/");
                    }
                    val = val.replaceAll("MARY_BASE", maryBase);
                    logprops.put(key, val);
                }
            }
            // And allow MaryProperties (and thus System properties) to overwrite the single entry
            // log4j.logger.marytts:
            String loggerMaryttsKey = "log4j.logger.marytts";
            String loggerMaryttsValue = MaryProperties.getProperty(loggerMaryttsKey);
            if (loggerMaryttsValue != null) {
                logprops.setProperty(loggerMaryttsKey, loggerMaryttsValue);
            }
            //PropertyConfigurator.configure(logprops);
        }

        // logger = MaryUtils.getLogger("main");
        // logger = ALogger.getLogger(MainActivity.class);


    }

    /**
     * Orderly shut down the MARY system.
     *
     * @throws IllegalStateException if the MARY system is not running.
     */
    public static void shutdown() {
        if (currentState != STATE_RUNNING)
            throw new IllegalStateException("MARY system is not running");
        currentState = STATE_SHUTTING_DOWN;
        Log.i(LOG, "Shutting down modules...");
        // Shut down modules:
        for (MaryModule m : ModuleRegistry.getAllModules()) {
            if (m.getState() == MaryModule.MODULE_RUNNING)
                m.shutdown();
        }

        if (MaryCache.haveCache()) {
            MaryCache cache = MaryCache.getCache();
            try {
                cache.shutdown();
            } catch (SQLException e) {
                Log.v(LOG, "Cannot shutdown cache: ", e);
            }
        }
        Log.i(LOG, "Shutdown complete.");
        currentState = STATE_OFF;
    }

    public static void process(String input, String inputTypeName, String outputTypeName,
                               String localeString, String audioTypeName, String voiceName,
                               String style, String effects, String outputTypeParams, OutputStream output)
            throws Exception {
        if (currentState != STATE_RUNNING)
            throw new IllegalStateException("MARY system is not running");

        MaryDataType inputType = MaryDataType.get(inputTypeName);
        MaryDataType outputType = MaryDataType.get(outputTypeName);
        Locale locale = MaryUtils.string2locale(localeString);
        Voice voice = null;
        if (voiceName != null)
            voice = Voice.getVoice(voiceName);
        AudioFileFormat audioFileFormat = null;
        AudioFileFormat.Type audioType = null;
        if (audioTypeName != null) {
            audioType = MaryAudioUtils.getAudioFileFormatType(audioTypeName);
            AudioFormat audioFormat = null;
            if (audioTypeName.equals("MP3")) {
                audioFormat = MaryRuntimeUtils.getMP3AudioFormat();
            } else if (audioTypeName.equals("Vorbis")) {
                audioFormat = MaryRuntimeUtils.getOggAudioFormat();
            } else if (voice != null) {
                audioFormat = voice.dbAudioFormat();
            } else {
                audioFormat = Voice.AF22050;
            }
            audioFileFormat = new AudioFileFormat(audioType, audioFormat, AudioSystem.NOT_SPECIFIED);
        }

        Request request = new Request(inputType, outputType, locale, voice, effects, style, 1, audioFileFormat, false, outputTypeParams);
        request.setInputData(input);
        request.process();
        request.writeOutputData(output);


    }


    /**
     * The starting point of the standalone Mary program.
     * If server mode is requested by property settings, starts
     * the <code>MaryServer</code>; otherwise, a <code>Request</code>
     * is created reading from the file given as first argument and writing
     * to System.out.
     * <p/>
     * <p>Usage:<p>
     * As a socket server:
     * <pre>
     * java -Dmary.base=$MARY_BASE -Dserver=true marytts.server.Mary
     * </pre><p>
     * As a stand-alone program:
     * <pre>
     * java -Dmary.base=$MARY_BASE marytts.server.Mary myfile.txt
     * </pre>
     *
     * @see MaryProperties
     * @see MaryServer
     * @see RequestHandler
     * @see Request
     */
    public static void main(final String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        addJarsToClasspath();

        String server = MaryProperties.needProperty("server");
        System.err.print("MARY server " + Version.specificationVersion() + " starting as a ");
        if (server.equals("socket")) System.err.print("socket server...");
        else if (server.equals("http")) System.err.print("HTTP server...");
        else System.err.print("command-line application...");

        // first thing we do, let's test if the port is available:
        if (!server.equals("commandline")) {
            int localPort = MaryProperties.needInteger("socket.port");
            try {
                ServerSocket serverSocket = new ServerSocket(localPort);
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("\nPort " + localPort + " already in use!");
                throw e;
            }
        }

        startup();
        System.err.println(" started in " + (System.currentTimeMillis() - startTime) / 1000. + " s");

        Runnable main = null;

        if (server.equals("socket")) { //socket server mode
            main = (Runnable) Class.forName("marytts.server.MaryServer").newInstance();
        } else if (server.equals("http")) { //http server mode
            main = (Runnable) Class.forName("marytts.server.http.MaryHttpServer").newInstance();
        } else { // command-line mode
            main = new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream;
                        if (args.length == 0 || args[0].equals("-"))
                            inputStream = System.in;
                        else
                            inputStream = new FileInputStream(args[0]);
                        String input = FileUtils.getStreamAsString(inputStream, "UTF-8");
                        process(input,
                                MaryProperties.getProperty("input.type", "TEXT"),
                                MaryProperties.getProperty("output.type", "AUDIO"),
                                MaryProperties.getProperty("locale", "en_US"),
                                MaryProperties.getProperty("audio.type", "WAVE"),
                                MaryProperties.getProperty("voice", null),
                                MaryProperties.getProperty("style", null),
                                MaryProperties.getProperty("effect", null),
                                MaryProperties.getProperty("output.type.params", null),
                                System.out);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        main.run();

        //shutdown();
    }
}

